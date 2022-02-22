package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsColumnAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.enums.TableTypeEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsTemplateMapper;
import com.rongji.rjsoft.mapper.SysDeptMapper;
import com.rongji.rjsoft.query.common.SysCommonFileQuery;
import com.rongji.rjsoft.query.content.CmsColumnQuery;
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
 * 栏目信息表 服务实现类
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

    /**
     * 添加栏目信息
     *
     * @param cmsColumnAo 栏目信息
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(CmsColumnAo cmsColumnAo) {
        //保存栏目
        CmsColumn parent = cmsColumnMapper.selectById(cmsColumnAo.getParentId());
        CmsColumn cmsColumn = new CmsColumn();
        BeanUtil.copyProperties(cmsColumnAo, cmsColumn);
        if (null == parent) {
            cmsColumn.setAncestors("0");
        } else {
            cmsColumn.setAncestors(parent.getAncestors() + "," + parent.getColumnId());
        }
        boolean result = cmsColumnMapper.insert(cmsColumn) > 0;
        if (result) {
            ThreadUtil.execute(this::refreshCache);
        }
        return result;
    }

    /**
     * 编辑栏目信息
     *
     * @param cmsColumnAo 栏目信息
     * @return 编辑结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(CmsColumnAo cmsColumnAo) {
        if (cmsColumnAo.getParentId().longValue() == cmsColumnAo.getColumnId().longValue()) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "父节点不能选择自身!");
        }

        //编辑栏目信息
        CmsColumn parent = cmsColumnMapper.selectById(cmsColumnAo.getParentId());
        CmsColumn old = cmsColumnMapper.selectById(cmsColumnAo.getColumnId());

        if (null == old) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "请联系管理员!");
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
        //修改该节点下所有节点的ancestors
        updateSiteChildren(old.getColumnId(), newAncestors, oldAncestors);

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
     * 删除栏目
     *
     * @param columnIds 站点id集合
     * @return 删除结果
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
     * 栏目分页列表
     *
     * @param cmsColumnQuery 查询条件
     * @return 栏目分页列表
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
     * 栏目树异步
     *
     * @param cmsColumnQuery 查询条件
     * @return 栏目树
     */
    @Override
    public List<CmsColumnTreeVo> tree(CmsColumnQuery cmsColumnQuery) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        List<CmsColumn> list;
        List<CmsColumnTreeVo> treeList = new ArrayList<>();
        CmsColumnTreeVo cmsColumnTreeVo;
        cmsColumnQuery.setColumnId(cmsColumnQuery.getColumnId() == null ? 0L : cmsColumnQuery.getColumnId());
        //查询以cmsSiteQuery.getSiteId为父节点的所有站点
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
     * 获取站点下部门拥有的栏目树
     *
     * @param siteId 站点Id
     * @param deptId 部门Id
     * @return 栏目树
     */
    @Override
    public List<CmsColumnAllTree> getColumnTreeBySite(Long siteId, Long deptId) {

        //获取站点下部门拥有的栏目树
        List<CmsColumnAllTree> list = cmsColumnMapper.getColumnTreeBySite(siteId, deptId);

        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException(ResponseEnum.NO_DATA);
        }

        //筛选出最上层的节点集合和其它节点集合
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

        //对上层节点依次处理填入children
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
     * 获取栏目详情
     *
     * @param columnId 栏目id
     * @return 栏目详情
     */
    @Override
    public CmsColumnDetailsVo getDetails(Long columnId) {
        CmsColumnDetailsVo cmsColumnDetailsVo = new CmsColumnDetailsVo();
        CmsTemplate cmsTemplate = cmsTemplateMapper.getTemplateByColumnId(columnId);
        SysCommonFileQuery query = new SysCommonFileQuery();
        if (null != cmsTemplate) {
            query.setTableId(cmsTemplate.getTemplateId());
            cmsColumnDetailsVo.setSiteTemplate(cmsTemplate.getTemplateId());
            cmsColumnDetailsVo.setListTemplate(cmsTemplate.getTemplateId());
            cmsColumnDetailsVo.setArticleTemplate(cmsTemplate.getTemplateId());
        }
        query.setTableType(TableTypeEnum.CMS_TEMPLATE.getValue());
        List<SysCommonFileVo> files = sysCommonFileService.getFiles(query);
        cmsColumnDetailsVo.setFiles(files);
        return cmsColumnDetailsVo;
    }

    /**
     * 刷新栏目缓存
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
     * 通过站点及栏目获取栏目异步树
     *
     * @param siteId 站点id
     * @param columnId 栏目id
     * @return 栏目异步树
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
            treeList.add(cmsSiteColumnTreeVo);
        }
        return treeList;
    }


}
