package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsColumnAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.entity.content.CmsSiteColumn;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsSiteColumnMapper;
import com.rongji.rjsoft.query.content.CmsColumnQuery;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.*;
import lombok.AllArgsConstructor;
import org.springframework.security.web.access.channel.AbstractRetryEntryPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
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

    private final CmsSiteColumnMapper cmsSiteColumnMapper;

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
        cmsColumn.setAncestors(parent.getAncestors() + "," + parent.getParentId());
        boolean result = cmsColumnMapper.insert(cmsColumn) > 0;

        //保存站点关系
        cmsColumnAo.setColumnId(cmsColumn.getColumnId());
        boolean result1 = saveSiteWithColumn(cmsColumnAo);

        return result && result1;
    }

    private boolean saveSiteWithColumn(CmsColumnAo cmsColumnAo) {
        CmsSiteColumn cmsSiteColumn;
        List<CmsSiteColumn> list = new ArrayList<>();
        Long[] siteIds = cmsColumnAo.getSiteId();
        for (int i = 0; i < siteIds.length; i++) {
            cmsSiteColumn = new CmsSiteColumn();
            cmsSiteColumn.setSiteId(siteIds[i]);
            cmsSiteColumn.setColumnId(cmsColumnAo.getColumnId());
            list.add(cmsSiteColumn);
        }
        boolean result1 = cmsSiteColumnMapper.batchInsert(list);
        return result1;
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

        //编辑站点栏目关系
        boolean result = updateSiteWithColumn(cmsColumnAo);

        //编辑栏目信息
        CmsColumn parent = cmsColumnMapper.selectById(cmsColumnAo.getParentId());
        CmsColumn old = cmsColumnMapper.selectById(cmsColumnAo.getColumnId());

        CmsColumn cmsColumn = new CmsColumn();
        BeanUtil.copyProperties(cmsColumnAo, cmsColumn);
        cmsColumn.setAncestors(parent.getAncestors() + "," + parent.getParentId());

        if (null != parent && null != old) {
            String newAncestors = parent.getAncestors() + "," + parent.getColumnId();
            String oldAncestors = old.getAncestors();
            old.setAncestors(newAncestors);
            cmsColumn.setAncestors(newAncestors);
            //修改该节点下所有节点的ancestors
            updateSiteChildren(old.getColumnId(), newAncestors, oldAncestors);
        }

        boolean result1 = cmsColumnMapper.updateById(cmsColumn) > 0;

        return result && result1;
    }

    private boolean updateSiteWithColumn(CmsColumnAo cmsColumnAo) {
        LambdaUpdateWrapper<CmsSiteColumn> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsSiteColumn::getColumnId, cmsColumnAo.getColumnId());
        cmsSiteColumnMapper.delete(wrapper);
        return saveSiteWithColumn(cmsColumnAo);
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
     * @param columnId 栏目id
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long[] columnId) {
        //删除站点栏目关系
        boolean result = cmsSiteColumnMapper.deleteSiteColumnByColumnId(columnId) > 0;
        //删除栏目
        boolean result1 = cmsColumnMapper.batchDeleteColumn(columnId) > 0;
        return result && result1;
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
        wrapper.eq(CmsColumn::getDelFlag, DelFlagEnum.exist.getCode());
        list = cmsColumnMapper.selectList(wrapper);
        for (CmsColumn cmsColumn : list) {
            cmsColumnTreeVo = new CmsColumnTreeVo();
            BeanUtil.copyProperties(cmsColumn, cmsColumnTreeVo);
            cmsColumnTreeVo.setParentNode(!isLeaf(cmsColumnTreeVo));
            treeList.add(cmsColumnTreeVo);
        }
        return treeList;
    }

    private boolean isLeaf(CmsColumnTreeVo cmsColumnTreeVo) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getParentId, cmsColumnTreeVo.getColumnId());
        Integer count = cmsColumnMapper.selectCount(wrapper);
        return count > 0 ? false : true;
    }

    /**
     * 获取站点下的栏目树
     *
     * @param siteId 站点Id
     * @return 栏目树
     */
    @Override
    public CmsColumnAllTree getColumnTreeBySite(Long siteId) {


        List<CmsColumnAllTree> list = cmsColumnMapper.getColumnTreeBySite(siteId);

        if (CollectionUtil.isNotEmpty(list)) {
            List<CmsColumnAllTree> tree = new ArrayList<>();
            CmsColumnAllTree top = list.remove(0);
            tree.add(top);
            assembly(tree, list);
            return top;
        }
        return null;
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

}
