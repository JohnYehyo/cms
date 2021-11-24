package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.dfa.WordTree;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.*;
import com.rongji.rjsoft.common.security.entity.LoginUser;
import com.rongji.rjsoft.common.security.util.SecurityUtils;
import com.rongji.rjsoft.common.security.util.TokenUtils;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.common.util.ServletUtils;
import com.rongji.rjsoft.common.util.bean.SpringBeanUtil;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.*;
import com.rongji.rjsoft.entity.system.SysDept;
import com.rongji.rjsoft.enums.*;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.*;
import com.rongji.rjsoft.query.content.*;
import com.rongji.rjsoft.service.ICmsArticleDeptService;
import com.rongji.rjsoft.service.ICmsArticleService;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.rongji.rjsoft.service.ICmsSensitiveWordsService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.common.FileVo;
import com.rongji.rjsoft.vo.content.CmsArticleInfoVo;
import com.rongji.rjsoft.vo.content.CmsArticlePortalVo;
import com.rongji.rjsoft.vo.content.CmsArticleRefVo;
import com.rongji.rjsoft.vo.content.CmsArticleVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章信息表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-22
 */
@Service
@AllArgsConstructor
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements ICmsArticleService {

    private final CmsArticleMapper cmsArticleMapper;

    private final CmsArticleContentMapper cmsArticleContentMapper;

    private final CmsArticleTagsMapper cmsArticleTagsMapper;

    private final CmsFinalArticleMapper cmsFinalArticleMapper;

    private final ICmsSensitiveWordsService cmsSensitiveWordsService;

    private final RedisCache redisCache;

    private final CmsColumnMapper cmsColumnMapper;

    private final SysDeptMapper sysDeptMapper;

    private final TokenUtils tokenUtils;

    private final ICmsFinalArticleService cmsFinalArticleService;

    private final ICmsArticleDeptService cmsArticleDeptService;

    /**
     * 添加文章
     *
     * @param cmsArticleAo 文章表单信息
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveArticle(CmsArticleAo cmsArticleAo) {

        checkSensitiveWords(cmsArticleAo);

        //保存文章
        CmsArticle cmsArticle = new CmsArticle();
        boolean result = insertArticle(cmsArticleAo, cmsArticle);
        cmsArticleAo.setArticleId(cmsArticle.getArticleId());

        //保存文章内容
        boolean result1 = saveContent(cmsArticleAo);
        //保存文章标签
        boolean result2 = saveTags(cmsArticleAo);
        //保存栏目文章关系
        List<CmsSiteColumn> list = cmsArticleAo.getList();
        Long articleId = cmsArticleAo.getArticleId();
        boolean result3 = saveArticleWithColumn(list, articleId, CmsOriginalEnum.ORIGINAL.getCode());

        //保存限制查看部门
        boolean result4 = saveLimitDepts(cmsArticleAo);

        //具有文章审核管理员权限的用户提交的文章且文章类型为直接发布时提交完毕直接发布
        publishArticle(cmsArticleAo, cmsArticle);

        return result && result1 && result2 && result3 && result4;
    }

    private boolean saveLimitDepts(CmsArticleAo cmsArticleAo) {
        if (null == cmsArticleAo.getDeptIds() || cmsArticleAo.getDeptIds().length == 0) {
            return true;
        }
        //保存文章部门关系
        return saveArticleDepts(cmsArticleAo);
    }

    private void publishArticle(CmsArticleAo cmsArticleAo, CmsArticle cmsArticle) {
        if (cmsArticleAo.getState() == CmsArticleStateEnum.TO_AUDIT.getState()
                && SecurityUtils.getLoginUser().getRoles().contains(Constants.ARTICLE_AUDIT_ADMIN)
                && cmsArticleAo.getPublishType() == CmsArticlePublishTypeEnum.MANUAL.getCode()) {
            ThreadUtil.execute(() -> {
                cmsFinalArticleService.generateArticle(cmsArticle.getArticleId());
            });
        }
    }

    private void checkSensitiveWords(CmsArticleAo cmsArticleAo) {
        List<String> list = redisCache.getCacheList(Constants.SENSITIVE_WORDS);
        if (CollectionUtil.isEmpty(list)) {
            list = cmsSensitiveWordsService.refreshCache();
        }
        if (CollectionUtil.isNotEmpty(list)) {
            WordTree tree = new WordTree();
            tree.addWords(list);
            String text = cmsArticleAo.getArticleTitle() + cmsArticleAo.getDescription() + cmsArticleAo.getContent();
            System.out.println(tree.isMatch(text));
            List<String> matchAll = tree.matchAll(text, -1, false, true);

            if (CollectionUtil.isNotEmpty(matchAll)) {
                throw new BusinessException(ResponseEnum.NO_ALLOW_WORD.getCode(),
                        ResponseEnum.NO_ALLOW_WORD.getValue() + matchAll);
            }
        }
    }

    private boolean insertArticle(CmsArticleAo cmsArticleAo, CmsArticle cmsArticle) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        cmsArticleAo.setAuthorId(SecurityUtils.getLoginUser().getSysDept().getDeptId());
        cmsArticleAo.setAuthorName(SecurityUtils.getLoginUser().getSysDept().getDeptName());
        BeanUtil.copyProperties(cmsArticleAo, cmsArticle);
        cmsArticle.setFiles(JSON.toJSONString(cmsArticleAo.getFiles()));
        if (cmsArticleAo.getPublishType() == CmsArticlePublishTypeEnum.AUTOMATIC.getCode()) {
            cmsArticle.setArticleUrl(dateTimeFormatter.format(cmsArticle.getPublishTime())
                    + "-" + UUID.fastUUID().toString().replace("-", ""));
        } else {
            cmsArticle.setArticleUrl(dateTimeFormatter.format(LocalDateTime.now())
                    + "-" + UUID.fastUUID().toString().replace("-", ""));
        }

        //分析文章类型
        cmsArticle.setCategoryId(analysisType(cmsArticleAo));
        //判断是否需要审核
        if (cmsArticleAo.getState() == CmsArticleStateEnum.TO_AUDIT.getState()
                && SecurityUtils.getLoginUser().getRoles().contains(Constants.ARTICLE_AUDIT_ADMIN)) {
            cmsArticle.setState(3);
        } else {
            cmsArticle.setState(cmsArticleAo.getState());
        }
        return cmsArticleMapper.insert(cmsArticle) > 0;
    }

    /**
     * 分析文章类型
     *
     * @param cmsArticleAo 文章信息
     */
    private Long analysisType(CmsArticleAo cmsArticleAo) {
        Long categoryId = CmsArticleCategoryEnmu.WORD.getCode();
        CmsArticleCategoryEnmu[] values = CmsArticleCategoryEnmu.values();
        if (null == values || values.length == 0) {
            return categoryId;
        }
        int count = 0;
        //分析富文本
        for (int i = 0; i < values.length - 2; i++) {
            if (cmsArticleAo.getContent().contains(values[i].getSignature())
                    && values[i].getCode() > CmsArticleCategoryEnmu.WORD.getCode()) {
                categoryId = values[i].getCode();
                count++;
            }
        }
        //分析附件
        if(CollectionUtil.isNotEmpty(cmsArticleAo.getFiles())){
            categoryId = CmsArticleCategoryEnmu.APPENDIX.getCode();
            count++;
        }
        //包含图片、视频、音频、附件中两种及两种以上的视为多媒体类型
        if (count > 1) {
            categoryId = CmsArticleCategoryEnmu.MULTIMEDIA.getCode();
        }
        return categoryId;
    }

    private boolean saveTags(CmsArticleAo cmsArticleAo) {
        List<CmsArticleTags> list = new ArrayList<>();
        Long[] tagIds = cmsArticleAo.getTagIds();
        CmsArticleTags cmsArticleTags;
        for (int i = 0; i < tagIds.length; i++) {
            cmsArticleTags = new CmsArticleTags();
            cmsArticleTags.setArticleId(cmsArticleAo.getArticleId());
            cmsArticleTags.setTagId(tagIds[i]);
            list.add(cmsArticleTags);
        }
        return cmsArticleTagsMapper.batchInsert(list) > 0;
    }

    private boolean saveContent(CmsArticleAo cmsArticleAo) {
        CmsArticleContent cmsArticleContent = new CmsArticleContent();
        BeanUtil.copyProperties(cmsArticleAo, cmsArticleContent);
        return cmsArticleContentMapper.insert(cmsArticleContent) > 0;
    }

    private boolean saveArticleWithColumn(List<CmsSiteColumn> siteColumnList, Long articleId, int original) {
        List<CmsFinalArticle> list = new ArrayList<>();
        CmsFinalArticle cmsColumnArticle;
        for (CmsSiteColumn cmsSiteColumn : siteColumnList) {
            cmsColumnArticle = new CmsFinalArticle();
            cmsColumnArticle.setArticleId(articleId);
            cmsColumnArticle.setSiteId(cmsSiteColumn.getSiteId());
            cmsColumnArticle.setColumnId(cmsSiteColumn.getColumnId());
            cmsColumnArticle.setOriginal(original);
            list.add(cmsColumnArticle);
        }
        return cmsFinalArticleMapper.batchInsert(list) > 0;
    }

    /**
     * 编辑文章
     *
     * @param cmsArticleAo 文章表单信息
     * @return 编辑结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(CmsArticleAo cmsArticleAo) {

        checkSensitiveWords(cmsArticleAo);

        //编辑文章
        boolean result = editArticle(cmsArticleAo);
        //编辑文章标签
        boolean result1 = updateTags(cmsArticleAo);
        //编辑栏目文章关系
        boolean result2 = updateArticleWithColumn(cmsArticleAo);
        //编辑文章内容
        boolean result3 = updateContent(cmsArticleAo);
        //更新限制查看部门
        boolean result4 = updateLimitDepts(cmsArticleAo);

        return result && result1 && result2 && result3 && result4;
    }

    private boolean updateLimitDepts(CmsArticleAo cmsArticleAo) {
        if (null == cmsArticleAo.getDeptIds() || cmsArticleAo.getDeptIds().length == 0) {
            return true;
        }
        boolean result = cmsArticleDeptService.removeById(cmsArticleAo.getArticleId());
        if (result) {
            //保存文章部门关系
            return saveArticleDepts(cmsArticleAo);
        }
        return result;
    }

    private boolean saveArticleDepts(CmsArticleAo cmsArticleAo) {
        CmsArticleDept cmsArticleDept;
        List<CmsArticleDept> list = new ArrayList<>();
        for (int i = 0; i < cmsArticleAo.getDeptIds().length - 1; i++) {
            cmsArticleDept = new CmsArticleDept();
            cmsArticleDept.setArticleId(cmsArticleAo.getArticleId());
            cmsArticleDept.setDeptId(cmsArticleAo.getDeptIds()[i]);
            list.add(cmsArticleDept);
        }
        return cmsArticleDeptService.saveBatch(list);
    }

    private boolean updateArticleWithColumn(CmsArticleAo cmsArticleAo) {
        LambdaUpdateWrapper<CmsFinalArticle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsFinalArticle::getArticleId, cmsArticleAo.getArticleId());
        cmsFinalArticleMapper.delete(wrapper);
        Long articleId = cmsArticleAo.getArticleId();
        List<CmsSiteColumn> list = cmsArticleAo.getList();
        return saveArticleWithColumn(list, articleId, CmsOriginalEnum.ORIGINAL.getCode());
    }

    private boolean updateTags(CmsArticleAo cmsArticleAo) {
        LambdaUpdateWrapper<CmsArticleTags> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsArticleTags::getArticleId, cmsArticleAo.getArticleId());
        cmsArticleTagsMapper.delete(wrapper);
        return saveTags(cmsArticleAo);
    }

    private boolean editArticle(CmsArticleAo cmsArticleAo) {
        cmsArticleAo.setAuthorId(SecurityUtils.getLoginUser().getSysDept().getDeptId());
        cmsArticleAo.setAuthorName(SecurityUtils.getLoginUser().getSysDept().getDeptName());
        CmsArticle cmsArticle = new CmsArticle();
        BeanUtil.copyProperties(cmsArticleAo, cmsArticle);
        //分析文章类型
        cmsArticle.setCategoryId(analysisType(cmsArticleAo));
        cmsArticle.setFiles(JSON.toJSONString(cmsArticleAo.getFiles()));
        return cmsArticleMapper.updateById(cmsArticle) > 0;
    }

    private boolean updateContent(CmsArticleAo cmsArticleAo) {
        CmsArticleContent cmsArticleContent = new CmsArticleContent();
        BeanUtil.copyProperties(cmsArticleAo, cmsArticleContent);
        LambdaUpdateWrapper<CmsArticleContent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsArticleContent::getArticleId, cmsArticleContent.getArticleId());
        return cmsArticleContentMapper.update(cmsArticleContent, wrapper) > 0;
    }

    /**
     * 删除文章
     *
     * @param list 删除条件
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(CmsArticleDeleteAo[] list) {
        return cmsFinalArticleMapper.deleteArticle(list) > 0;
    }

    /**
     * 审核文章
     *
     * @param cmsArticleAuditAo 文章状态信息
     * @return 审核结果
     */
    @Override
    public boolean audit(CmsArticleAuditAo cmsArticleAuditAo) {
        boolean result = cmsArticleMapper.audit(cmsArticleAuditAo);
        if (cmsArticleAuditAo.getState() == CmsArticleStateEnum.ENABLE.getState()) {
            ThreadUtil.execute(() -> {
                cmsFinalArticleService.generateArticle(cmsArticleAuditAo.getArticleIds());
            });
        }
        return result;
    }

    /**
     * 文章列表
     *
     * @param cmsArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticleVo> getPage(CmsArticleQuery cmsArticleQuery) {
        //查询用户的部门及所有下属部门
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        List<Long> deptIds = getOwnDepts();

        IPage<CmsArticleVo> page = new Page<>();
        Set<String> roles = SecurityUtils.getLoginUser().getRoles();
        if (null != roles && roles.size() == 1 && roles.contains(Constants.ARTICLE_AUDIT_ADMIN)) {
            if (null != cmsArticleQuery.getState() && cmsArticleQuery.getState().equals(CmsArticleStateEnum.DRAFT.getState())) {
                throw new BusinessException(ResponseEnum.NO_PERMISSION);
            }
            page = cmsArticleMapper.getPageForAudit(page, cmsArticleQuery, deptIds);
        } else {
            roles.remove(deptId);
            page = cmsArticleMapper.getPage(page, cmsArticleQuery, deptIds, deptId);
        }
        List<CmsArticleVo> records = page.getRecords();
        for (CmsArticleVo cmsArticleVo : records) {
            cmsArticleVo.setTags(cmsArticleTagsMapper.getTagsByArticleId(cmsArticleVo.getArticleId()));
        }
        return CommonPageUtils.assemblyPage(page);
    }

    private List<Long> getOwnDepts() {
        String branchCode = SecurityUtils.getLoginUser().getSysDept().getBranchCode();
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(SysDept::getBranchCode, branchCode);
        List<SysDept> sysDepts = sysDeptMapper.selectList(wrapper);
        if (null == sysDepts) {
            throw new BusinessException(ResponseEnum.NO_DATA);
        }
        List<Long> deptIds = sysDepts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        return deptIds;
    }

    /**
     * 文章详情
     *
     * @param articleId 文章id
     * @return 文章详情
     */
    @Override
    public CmsArticleInfoVo getInfo(Long articleId) {
        CmsArticleInfoVo cmsArticleInfoVo = cmsArticleMapper.getInfo(articleId);
        if (null == cmsArticleInfoVo) {
            throw new BusinessException(ResponseEnum.NO_DATA);
        }
        if (StringUtils.isNotEmpty(cmsArticleInfoVo.getFiles())) {
            cmsArticleInfoVo.setFile(JSONArray.parseArray(cmsArticleInfoVo.getFiles(), FileVo.class));
        }
        cmsArticleInfoVo.setTags(cmsArticleTagsMapper.getTagsByArticleId(cmsArticleInfoVo.getArticleId()));
        cmsArticleInfoVo.setSiteColumns(cmsFinalArticleMapper.listOfArticleRef(articleId));
        return cmsArticleInfoVo;
    }

    /**
     * 文章引用查询
     *
     * @param articleId 文章ID
     * @return 文章引用列表
     */
    @Override
    public List<CmsArticleRefVo> listOfArticleRef(Long articleId) {
        return cmsFinalArticleMapper.listOfArticleRef(articleId);
    }

    /**
     * 通过栏目获取文章列表
     *
     * @param cmsColumnArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticlePortalVo> getArticlesByColumn(CmsColumnArticleQuery cmsColumnArticleQuery) {
        List<CmsColumn> cmsColumns = cmsColumnMapper.selectChildrenByColumnId(cmsColumnArticleQuery.getColumnId());
        List<Long> columns = cmsColumns.stream().map(k -> k.getColumnId()).collect(Collectors.toList());
        Long deptId = getDeptId();
        IPage<CmsArticlePortalVo> page = new Page<>();
        page = cmsFinalArticleMapper.getArticlePageByColumn(page, columns, cmsColumnArticleQuery.getSiteId(), deptId);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 从登录信息获取部门id
     *
     * @return 部门id
     */
    private Long getDeptId() {
        LoginUser loginUser = tokenUtils.getLoginUser(ServletUtils.getRequest());
        if (null != loginUser) {
            return loginUser.getSysDept().getDeptId();
        }
        return null;
    }

    /**
     * 通过标签获取文章列表
     *
     * @param cmsTagArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticlePortalVo> getArticlesByTag(CmsTagArticleQuery cmsTagArticleQuery) {
        LambdaQueryWrapper<CmsArticleTags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticleTags::getTagId, cmsTagArticleQuery.getTagId());
        List<CmsArticleTags> cmsArticleTags = cmsArticleTagsMapper.selectList(wrapper);
        List<Long> articles = cmsArticleTags.stream().map(k -> k.getArticleId()).collect(Collectors.toList());
        Long deptId = getDeptId();
        IPage<CmsArticlePortalVo> page = new Page<>();
        page = cmsFinalArticleMapper.getArticlePageByTag(page, articles, cmsTagArticleQuery.getSiteId(), deptId);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 通过标签获取文章列表
     *
     * @param cmsCategoryArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticlePortalVo> getArticlesByCategory(CmsCategoryArticleQuery cmsCategoryArticleQuery) {
        Long deptId = getDeptId();
        IPage<CmsArticlePortalVo> page = new Page<>();
        page = cmsFinalArticleMapper.getArticlesByCategory(page, cmsCategoryArticleQuery, deptId);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 获取轮播文章
     *
     * @param cmsSliderArticleQuery 查询对象
     * @return 轮播文章
     */
    @Override
    public List<CmsArticlePortalVo> getArticlesBySlider(CmsSliderArticleQuery cmsSliderArticleQuery) {
        Long deptId = getDeptId();
        return cmsFinalArticleMapper.getArticlesBySlider(cmsSliderArticleQuery, deptId);
    }

    /**
     * 转发文章
     *
     * @param cmsArticleForWardingAo 转发文章参数体
     * @return 转发文章结果
     */
    @Override
    public boolean forwarding(CmsArticleForWardingAo cmsArticleForWardingAo) {
        List<CmsSiteColumn> list = cmsArticleForWardingAo.getList();
        Long articleId = cmsArticleForWardingAo.getArticleId();
        return saveArticleWithColumn(list, articleId, CmsOriginalEnum.FORWARDING.getCode());
    }

    /**
     * 移动文章
     *
     * @param cmsArticleForMoveAo 移动文章参数体
     * @return 移动文章结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean move(CmsArticleForMoveAo cmsArticleForMoveAo) {
        LambdaUpdateWrapper<CmsFinalArticle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsFinalArticle::getSiteId, cmsArticleForMoveAo.getSiteId())
                .eq(CmsFinalArticle::getColumnId, cmsArticleForMoveAo.getColumnId())
                .eq(CmsFinalArticle::getArticleId, cmsArticleForMoveAo.getArticleId());
        cmsFinalArticleMapper.delete(wrapper);
        ICmsArticleService bean = SpringBeanUtil.getBean(ICmsArticleService.class);
        if (null != bean) {
            return bean.forwarding(cmsArticleForMoveAo);
        }
        return false;
    }

    /**
     * 按部门查询文章
     *
     * @param cmsDeptArticleQuerys 查询对象
     * @return 文章列表
     */
    @Override
    public List<CmsArticlePortalVo> getArticlesByDept(CmsDeptArticleQuery cmsDeptArticleQuerys) {
        Long deptId = getDeptId();
        if (null == deptId) {
            throw new BusinessException(ResponseEnum.NO_PERMISSION);
        }
        return cmsFinalArticleMapper.getArticlesByDept(cmsDeptArticleQuerys);
    }

}
