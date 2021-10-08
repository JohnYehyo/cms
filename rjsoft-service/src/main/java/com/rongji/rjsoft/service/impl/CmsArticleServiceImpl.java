package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.dfa.WordTree;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsArticleAo;
import com.rongji.rjsoft.ao.content.CmsArticleAuditAo;
import com.rongji.rjsoft.ao.content.CmsArticleDeleteAo;
import com.rongji.rjsoft.common.security.util.SecurityUtils;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.*;
import com.rongji.rjsoft.enums.CmsArticleStateEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.*;
import com.rongji.rjsoft.query.content.*;
import com.rongji.rjsoft.service.ICmsArticleService;
import com.rongji.rjsoft.service.ICmsSensitiveWordsService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsArticleInfoVo;
import com.rongji.rjsoft.vo.content.CmsArticlePortalVo;
import com.rongji.rjsoft.vo.content.CmsArticleRefVo;
import com.rongji.rjsoft.vo.content.CmsArticleVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        boolean result3 = saveArticleWithColumn(cmsArticleAo);

        return result && result1 && result2 && result3;
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        cmsArticleAo.setAuthorId(SecurityUtils.getLoginUser().getSysDept().getDeptId());
        cmsArticleAo.setAuthorName(SecurityUtils.getLoginUser().getSysDept().getDeptName());
        BeanUtil.copyProperties(cmsArticleAo, cmsArticle);
        cmsArticle.setFiles(JSON.toJSONString(cmsArticleAo.getFiles()));
        cmsArticle.setArticleUrl(dateTimeFormatter.format(LocalDateTime.now())
                + "-" + SecureUtil.md5(String.valueOf(cmsArticle.getArticleId() + cmsArticle.getAuthorId())));
        //判断是否需要审核
        if (cmsArticleAo.getState() == CmsArticleStateEnum.TO_AUDIT.getState()
                && SecurityUtils.getLoginUser().getRoles().contains(Constants.CMS_ADMIN)) {
            cmsArticle.setState(3);
        } else {
            cmsArticle.setState(cmsArticleAo.getState());
        }
        return cmsArticleMapper.insert(cmsArticle) > 0;
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

    private boolean saveArticleWithColumn(CmsArticleAo cmsArticleAo) {
        List<CmsSiteColumn> siteColumnList = cmsArticleAo.getList();
        List<CmsFinalArticle> list = new ArrayList<>();
        CmsFinalArticle cmsColumnArticle;
        for (CmsSiteColumn cmsSiteColumn : siteColumnList) {
            cmsColumnArticle = new CmsFinalArticle();
            cmsColumnArticle.setArticleId(cmsArticleAo.getArticleId());
            cmsColumnArticle.setSiteId(cmsSiteColumn.getSiteId());
            cmsColumnArticle.setColumnId(cmsSiteColumn.getColumnId());
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
        return result && result1 && result2 && result3;
    }

    private boolean updateArticleWithColumn(CmsArticleAo cmsArticleAo) {
        LambdaUpdateWrapper<CmsFinalArticle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsFinalArticle::getArticleId, cmsArticleAo.getArticleId());
        cmsFinalArticleMapper.delete(wrapper);
        return saveArticleWithColumn(cmsArticleAo);
    }

    private boolean updateTags(CmsArticleAo cmsArticleAo) {
        LambdaUpdateWrapper<CmsArticleTags> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsArticleTags::getArticleId, cmsArticleAo.getArticleId());
        cmsArticleTagsMapper.delete(wrapper);
        return saveTags(cmsArticleAo);
    }

    private boolean editArticle(CmsArticleAo cmsArticleAo) {
        cmsArticleAo.setAuthorId(SecurityUtils.getLoginUser().getUser().getUserId());
        cmsArticleAo.setAuthorName(SecurityUtils.getLoginUser().getUser().getUserName());
        CmsArticle cmsArticle = new CmsArticle();
        BeanUtil.copyProperties(cmsArticleAo, cmsArticle);
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
        return cmsArticleMapper.audit(cmsArticleAuditAo);
    }

    /**
     * 文章列表
     *
     * @param cmsArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticleVo> getPage(CmsArticleQuery cmsArticleQuery) {
        //判断是否为内容管理员
        boolean flag = SecurityUtils.getLoginUser().getRoles().contains(Constants.CMS_ADMIN);
        if(flag && CmsArticleStateEnum.DRAFT.getState().equals(cmsArticleQuery.getState())){
            throw new BusinessException(ResponseEnum.NO_PERMISSION.getCode(), "不能查看未提交的文章");
        }
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        IPage<CmsArticleVo> page = new Page<>();
        page = cmsArticleMapper.getPage(page, cmsArticleQuery, flag, deptId);
        List<CmsArticleVo> records = page.getRecords();
        for (CmsArticleVo cmsArticleVo : records) {
            cmsArticleVo.setTags(cmsArticleTagsMapper.getTagsByArticleId(cmsArticleVo.getArticleId()));
        }
        return CommonPageUtils.assemblyPage(page);
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
        IPage<CmsArticlePortalVo> page = new Page<>();
        page = cmsFinalArticleMapper.getArticlePageByColumn(page, columns, cmsColumnArticleQuery.getSiteId());
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 通过标签获取文章列表
     * @param cmsTagArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticlePortalVo> getArticlesByTag(CmsTagArticleQuery cmsTagArticleQuery) {
        LambdaQueryWrapper<CmsArticleTags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticleTags::getTagId, cmsTagArticleQuery.getTagId());
        List<CmsArticleTags> cmsArticleTags = cmsArticleTagsMapper.selectList(wrapper);
        List<Long> articles = cmsArticleTags.stream().map(k -> k.getArticleId()).collect(Collectors.toList());
        IPage<CmsArticlePortalVo> page = new Page<>();
        page = cmsFinalArticleMapper.getArticlePageByTag(page, articles, cmsTagArticleQuery.getSiteId());
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 通过标签获取文章列表
     * @param cmsCategoryArticleQuery 查询对象
     * @return 文章列表
     */
    @Override
    public CommonPage<CmsArticlePortalVo> getArticlesByCategory(CmsCategoryArticleQuery cmsCategoryArticleQuery) {
        IPage<CmsArticlePortalVo> page = new Page<>();
        page = cmsFinalArticleMapper.getArticlesByCategory(page, cmsCategoryArticleQuery);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 获取轮播文章
     * @param cmsSliderArticleQuery 查询对象
     * @return 轮播文章
     */
    @Override
    public List<CmsArticlePortalVo> getArticlesBySlider(CmsSliderArticleQuery cmsSliderArticleQuery) {
        return cmsFinalArticleMapper.getArticlesBySlider(cmsSliderArticleQuery);
    }
}
