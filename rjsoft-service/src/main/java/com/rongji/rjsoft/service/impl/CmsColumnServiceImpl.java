package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsColumnAo;
import com.rongji.rjsoft.common.security.util.SecurityUtils;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsAuthority;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.enums.TableTypeEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsAuthorityMapper;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsTemplateMapper;
import com.rongji.rjsoft.mapper.SysDeptMapper;
import com.rongji.rjsoft.query.common.SysCommonFileQuery;
import com.rongji.rjsoft.query.content.CmsColumnQuery;
import com.rongji.rjsoft.service.ICmsAuthorityService;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.service.ISysCommonFileService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.common.SysCommonFileVo;
import com.rongji.rjsoft.vo.content.*;
import com.rongji.rjsoft.vo.system.dept.SysDeptTreeVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-15
 */
@Service
@AllArgsConstructor
public class CmsColumnServiceImpl extends ServiceImpl<CmsColumnMapper, CmsColumn> implements ICmsColumnService {

    private final CmsColumnMapper cmsColumnMapper;

    private final SysDeptMapper sysDeptMapper;

    private final CmsTemplateMapper cmsTemplateMapper;

    private final ISysCommonFileService sysCommonFileService;

    private final RedisCache redisCache;

    private final CmsAuthorityMapper cmsAuthorityMapper;

    /**
     * ??????????????????
     *
     * @param cmsColumnAo ????????????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(CmsColumnAo cmsColumnAo) {
        //????????????
        CmsColumn parent = cmsColumnMapper.selectById(cmsColumnAo.getParentId());
        CmsColumn cmsColumn = new CmsColumn();
        BeanUtil.copyProperties(cmsColumnAo, cmsColumn);
        if (null == parent) {
            cmsColumn.setAncestors("0");
        } else {
            cmsColumn.setAncestors(parent.getAncestors() + "," + parent.getColumnId());
        }
        cmsColumn.setColumnUrl(PinyinUtil.getPinyin(cmsColumnAo.getColumnName(), ""));
        boolean result = cmsColumnMapper.insert(cmsColumn) > 0;
        if (result) {
            ThreadUtil.execute(this::refreshCache);
        }
        return result;
    }

    /**
     * ??????????????????
     *
     * @param cmsColumnAo ????????????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(CmsColumnAo cmsColumnAo) {
        if (cmsColumnAo.getParentId().longValue() == cmsColumnAo.getColumnId().longValue()) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "???????????????????????????!");
        }

        //??????????????????
        CmsColumn parent = cmsColumnMapper.selectById(cmsColumnAo.getParentId());
        CmsColumn old = cmsColumnMapper.selectById(cmsColumnAo.getColumnId());

        if (null == old) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "??????????????????!");
        }

        CmsColumn cmsColumn = new CmsColumn();
        BeanUtil.copyProperties(cmsColumnAo, cmsColumn);
        if (null == cmsColumnAo.getParentId()) {
            cmsColumn.setParentId(0L);
        }

        String newAncestors = "0";
        if (null != parent) {
            newAncestors = parent.getAncestors() + "," + parent.getColumnId();
        }
        cmsColumn.setAncestors(newAncestors);
        String oldAncestors = old.getAncestors();
        old.setAncestors(newAncestors);
        cmsColumn.setAncestors(newAncestors);
        //?????????????????????????????????ancestors
        updateSiteChildren(old.getColumnId(), newAncestors, oldAncestors);
        cmsColumn.setColumnUrl(PinyinUtil.getPinyin(cmsColumnAo.getColumnName(), ""));
        boolean result = cmsColumnMapper.updateById(cmsColumn) > 0;
        if (result) {
            ThreadUtil.execute(this::refreshCache);
        }
        return result;
    }

    private void updateSiteChildren(Long columnId, String newAncestors, String oldAncestors) {
        List<CmsColumn> list = cmsColumnMapper.selectChildrenByColumnId(columnId);
        if (null == list || list.size() == 0) {
            return;
        }
        for (CmsColumn cmsColumn : list) {
            cmsColumn.setAncestors(cmsColumn.getAncestors().replace(oldAncestors, newAncestors));
        }
        int count = cmsColumnMapper.batchUpdateChildreAncestors(list);
    }


    /**
     * ????????????
     *
     * @param columnIds ??????id??????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long[] columnIds) {
        boolean result = cmsColumnMapper.batchDeleteColumn(columnIds) > 0;
        if (result) {
            ThreadUtil.execute(this::refreshCache);
        }
        return result;
    }

    /**
     * ??????????????????
     *
     * @param cmsColumnQuery ????????????
     * @return ??????????????????
     */
    @Override
    public CommonPage<CmsColumnVo> pageList(CmsColumnQuery cmsColumnQuery) {
        IPage<CmsColumnVo> page = new Page<>(cmsColumnQuery.getCurrent(), cmsColumnQuery.getPageSize());
        page = cmsColumnMapper.getPage(page, cmsColumnQuery);
        if (CollectionUtil.isNotEmpty(page.getRecords())) {
            for (CmsColumnVo cmsColumnVo : page.getRecords()) {
                if (null == cmsColumnVo.getDeptId()) {
                    continue;
                }
                List<Long> deptIds = Arrays
                        .stream(cmsColumnVo.getDeptId().split(","))
                        .map(k -> Long.parseLong(k))
                        .collect(Collectors.toList());
                List<SysDeptTreeVo> sysDepts = sysDeptMapper.selectDeptsByIds(deptIds);
                if (null == sysDepts) {
                    continue;
                }
                cmsColumnVo.setDepts(sysDepts);
            }
        }
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * ???????????????
     *
     * @param cmsColumnQuery ????????????
     * @return ?????????
     */
    @Override
    public List<CmsColumnTreeVo> tree(CmsColumnQuery cmsColumnQuery) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        List<CmsColumn> list;
        List<CmsColumnTreeVo> treeList = new ArrayList<>();
        CmsColumnTreeVo cmsColumnTreeVo;
        cmsColumnQuery.setColumnId(cmsColumnQuery.getColumnId() == null ? 0L : cmsColumnQuery.getColumnId());
        //?????????cmsSiteQuery.getSiteId???????????????????????????
        wrapper.eq(CmsColumn::getParentId, cmsColumnQuery.getColumnId());
        wrapper.eq(CmsColumn::getDelFlag, DelFlagEnum.EXIST.getCode());
        list = cmsColumnMapper.selectList(wrapper);
        for (CmsColumn cmsColumn : list) {
            cmsColumnTreeVo = new CmsColumnTreeVo();
            BeanUtil.copyProperties(cmsColumn, cmsColumnTreeVo);
            cmsColumnTreeVo.setParentNode(!isLeaf(cmsColumnTreeVo.getColumnId()));
            treeList.add(cmsColumnTreeVo);
        }
        return treeList;
    }

    private boolean isLeaf(Long columnId) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getParentId, columnId);
        Integer count = cmsColumnMapper.selectCount(wrapper);
        return count > 0 ? false : true;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param siteId ??????Id
     * @param deptId ??????Id
     * @return ?????????
     */
    @Override
    public List<CmsColumnAllTree> getColumnTreeBySite(Long siteId, Long deptId) {

        //???????????????????????????????????????
        List<CmsColumnAllTree> list = cmsColumnMapper.getColumnTreeBySite(siteId, deptId);

        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException(ResponseEnum.NO_DATA);
        }

        //??????????????????????????????????????????????????????
        List<Long> columnIds = list.stream().map(CmsColumnAllTree::getColumnId).collect(Collectors.toList());
        List<CmsColumnAllTree> top = new ArrayList<>();
        List<CmsColumnAllTree> other = new ArrayList<>();
        for (CmsColumnAllTree cms : list) {
            if (columnIds.contains(cms.getParentId())) {
                other.add(cms);
                continue;
            }
            top.add(cms);
        }

        //?????????????????????????????????children
        for (CmsColumnAllTree parent : top) {
            List<CmsColumnAllTree> tree = new ArrayList<>();
            tree.add(parent);
            assembly(tree, other);
        }

        return top;
    }


    private void assembly(List<CmsColumnAllTree> parentChildren, List<CmsColumnAllTree> list) {

        for (CmsColumnAllTree CmsColumnAllTree : parentChildren) {
            List<CmsColumnAllTree> children = new ArrayList<>();
            Iterator<CmsColumnAllTree> iterator = list.iterator();
            CmsColumnAllTree next;
            while (iterator.hasNext()) {
                next = iterator.next();
                if (next.getParentId().longValue() == CmsColumnAllTree.getColumnId().longValue()) {
                    children.add(next);
                    iterator.remove();
                }
            }
            CmsColumnAllTree.setChildren(children);
            if (CollectionUtil.isNotEmpty(list)) {
                assembly(CmsColumnAllTree.getChildren(), list);
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param columnId ??????id
     * @return ????????????
     */
    @Override
    public CmsColumnDetailsVo getDetails(Long columnId) {
        CmsColumnDetailsVo cmsColumnDetailsVo = new CmsColumnDetailsVo();
//        CmsTemplate cmsTemplate = cmsTemplateMapper.getTemplateByColumnId(columnId);
        SysCommonFileQuery query = new SysCommonFileQuery();
//        if (null != cmsTemplate) {
//            query.setTableId(cmsTemplate.getTemplateId());
//            cmsColumnDetailsVo.setSiteTemplate(cmsTemplate.getTemplateId());
//            cmsColumnDetailsVo.setListTemplate(cmsTemplate.getTemplateId());
//            cmsColumnDetailsVo.setArticleTemplate(cmsTemplate.getTemplateId());
//        }
        query.setTableType(TableTypeEnum.CMS_TEMPLATE.getValue());
        List<SysCommonFileVo> files = sysCommonFileService.getFiles(query);
        cmsColumnDetailsVo.setFiles(files);
        return cmsColumnDetailsVo;
    }

    /**
     * ??????????????????
     */
    @Override
    public void refreshCache() {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper();
        wrapper.eq(CmsColumn::getDelFlag, DelFlagEnum.EXIST.getCode());
        List<CmsColumn> cmsColumns = cmsColumnMapper.selectList(wrapper);
        for (CmsColumn cmsColumn : cmsColumns) {
            redisCache.setCacheMapValue(Constants.COLUMN_DICT, cmsColumn.getColumnId() + Constants.SITE_DICT_TEMPLATE, cmsColumn.getSiteTemplate());
            redisCache.setCacheMapValue(Constants.COLUMN_DICT, cmsColumn.getColumnId() + Constants.LIST_DICT_TEMPLATE, cmsColumn.getListTemplate());
            redisCache.setCacheMapValue(Constants.COLUMN_DICT, cmsColumn.getColumnId() + Constants.ARTICLE_DICT_TEMPLATE, cmsColumn.getArticleTemplate());
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param siteId ??????id
     * @param columnId ??????id
     * @return ???????????????
     */
    @Override
    public List<CmsSiteColumnTreeVo> getListBySite(Long siteId, Long columnId) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getSiteId, siteId);
        wrapper.eq(CmsColumn::getParentId, columnId == null ? 0 : columnId);
        wrapper.eq(CmsColumn::getDelFlag, DelFlagEnum.EXIST.getCode());
        List<CmsColumn> list = cmsColumnMapper.selectList(wrapper);
        List<CmsSiteColumnTreeVo> treeList = new ArrayList<>();
        CmsSiteColumnTreeVo cmsSiteColumnTreeVo;
        for (CmsColumn cmsColumn : list) {
            cmsSiteColumnTreeVo = new CmsSiteColumnTreeVo();
            cmsSiteColumnTreeVo.setId(siteId + "_" + cmsColumn.getColumnId());
            cmsSiteColumnTreeVo.setParentId(siteId + "_" + cmsColumn.getParentId());
            cmsSiteColumnTreeVo.setName(cmsColumn.getColumnName());
            cmsSiteColumnTreeVo.setParentNode(!isLeaf(cmsColumn.getColumnId()));
            cmsSiteColumnTreeVo.setType(1);
            treeList.add(cmsSiteColumnTreeVo);
        }
        return treeList;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param siteId ??????id
     * @param columnId ??????id
     * @return ???????????????
     */
    @Override
    public List<CmsSiteColumnTreeVo> getLimitListBySite(Long siteId, Long columnId) {

        //?????????????????????????????????????????????
        List<Long> limitColumnIds = getLimitSiteIds();

        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getSiteId, siteId);
        wrapper.eq(CmsColumn::getParentId, columnId == null ? 0 : columnId);
        wrapper.eq(CmsColumn::getDelFlag, DelFlagEnum.EXIST.getCode());
        List<CmsColumn> list = cmsColumnMapper.selectList(wrapper);
        list = getLimitNode(limitColumnIds, list);
        List<CmsSiteColumnTreeVo> treeList = new ArrayList<>();
        if(CollectionUtil.isEmpty(list)){
            return treeList;
        }
        CmsSiteColumnTreeVo cmsSiteColumnTreeVo;
        for (CmsColumn cmsColumn : list) {
            cmsSiteColumnTreeVo = new CmsSiteColumnTreeVo();
            cmsSiteColumnTreeVo.setId(siteId + "_" + cmsColumn.getColumnId());
            cmsSiteColumnTreeVo.setParentId(siteId + "_" + cmsColumn.getParentId());
            cmsSiteColumnTreeVo.setName(cmsColumn.getColumnName());
            cmsSiteColumnTreeVo.setParentNode(!isLeaf(cmsColumn.getColumnId()));
            cmsSiteColumnTreeVo.setType(1);
            treeList.add(cmsSiteColumnTreeVo);
        }
        return treeList;
    }

    /**
     * ?????????????????????????????????????????????
     * @return
     */
    private List<Long> getLimitSiteIds() {
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        LambdaQueryWrapper<CmsAuthority> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmsAuthority::getDeptId, deptId);
        List<CmsAuthority> limits = cmsAuthorityMapper.selectList(queryWrapper);
        List<Long> limitSiteIds = limits.stream().map(k -> k.getColumnId()).collect(Collectors.toList());
        return limitSiteIds;
    }

    private List<CmsColumn> getLimitNode(List<Long> limitColumnIds, List<CmsColumn> list) {
        List<CmsColumn> allowList = new ArrayList<>();
        for (CmsColumn cmsColumn: list) {
            if(limitColumnIds.contains(cmsColumn.getColumnId())){
                allowList.add(cmsColumn);
            }
        }
        //??????????????????????????????
        if(CollectionUtil.isEmpty(allowList)){
            allowList = recursion(limitColumnIds, list);
        }
        return allowList;
    }

    /**
     * ?????????????????????????????????????????????
     * @param limitSiteIds
     * @param list
     * @return
     */
    private List<CmsColumn> recursion(List<Long> limitSiteIds, List<CmsColumn> list) {
        List<Long> unAllowIds = list.stream().map(k -> k.getSiteId()).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(unAllowIds)){
            return null;
        }
        List<CmsColumn> allowList = new ArrayList<>();
        list = cmsColumnMapper.selectColumnByParents(unAllowIds);
        for (CmsColumn cmsColumn: list) {
            if(limitSiteIds.contains(cmsColumn.getColumnId())){
                allowList.add(cmsColumn);
            }
        }
        if(CollectionUtil.isEmpty(allowList)){
            return recursion(limitSiteIds, list);
        }
        return allowList;
    }

}
