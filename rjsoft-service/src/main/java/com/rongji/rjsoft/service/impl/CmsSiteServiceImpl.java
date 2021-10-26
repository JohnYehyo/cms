package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsSiteAo;
import com.rongji.rjsoft.ao.content.CmsSiteColumnAo;
import com.rongji.rjsoft.common.security.util.SecurityUtils;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.entity.content.CmsSiteColumn;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsSiteColumnMapper;
import com.rongji.rjsoft.mapper.CmsSiteMapper;
import com.rongji.rjsoft.query.content.CmsSiteQuery;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteAllTreeVo;
import com.rongji.rjsoft.vo.content.CmsSiteDetailsVo;
import com.rongji.rjsoft.vo.content.CmsSiteTreeVo;
import com.rongji.rjsoft.vo.content.CmsSiteVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 站点信息表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-15
 */
@Service
@AllArgsConstructor
public class CmsSiteServiceImpl extends ServiceImpl<CmsSiteMapper, CmsSite> implements ICmsSiteService {

    private final CmsSiteMapper cmsSiteMapper;

    private final CmsSiteColumnMapper cmsSiteColumnMapper;

    private final RedisCache redisCache;

    /**
     * 新增站点信息
     *
     * @param cmsSiteAo 站点信息
     * @return 新增结果
     */
    @Override
    public boolean add(CmsSiteAo cmsSiteAo) {
        CmsSite parent = cmsSiteMapper.selectById(cmsSiteAo.getParentId());
        CmsSite cmsSite = new CmsSite();
        BeanUtil.copyProperties(cmsSiteAo, cmsSite);
        if (null == parent) {
            cmsSite.setAncestors("0");
        } else {
            cmsSite.setAncestors(parent.getAncestors() + "," + parent.getSiteId());
        }

        boolean result = cmsSiteMapper.insert(cmsSite) > 0;
        if (result) {
            ThreadUtil.execute(() -> refreshCache());
        }

        return result;
    }

    /**
     * 编辑站点信息
     *
     * @param cmsSiteAo 站点信息
     * @return 编辑结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(CmsSiteAo cmsSiteAo) {

        //更新站点信息
        CmsSite parent = cmsSiteMapper.selectById(cmsSiteAo.getParentId());
        CmsSite old = cmsSiteMapper.selectById(cmsSiteAo.getSiteId());

        if (null == old) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "请联系管理员!");
        }

        CmsSite cmsSite = new CmsSite();
        BeanUtil.copyProperties(cmsSiteAo, cmsSite);
        if (null == cmsSiteAo.getParentId()) {
            cmsSite.setParentId(0L);
        }

        String newAncestors = "0";
        if (null != parent) {
            newAncestors = parent.getAncestors() + "," + parent.getSiteId();
        }

        String oldAncestors = old.getAncestors();
        old.setAncestors(newAncestors);
        cmsSite.setAncestors(newAncestors);
        //修改该节点下所有节点的ancestors
        updateSiteChildren(old.getSiteId(), newAncestors, oldAncestors);

        boolean result = cmsSiteMapper.updateById(cmsSite) > 0;
        if (result) {
            ThreadUtil.execute(() -> refreshCache());
        }
        return result;
    }

    private void updateSiteChildren(Long siteId, String newAncestors, String oldAncestors) {
        List<CmsSite> list = cmsSiteMapper.selectChildrenBySiteId(siteId);
        if (null == list || list.size() == 0) {
            return;
        }
        for (CmsSite cmsSite : list) {
            cmsSite.setAncestors(cmsSite.getAncestors().replace(oldAncestors, newAncestors));
        }
        int count = cmsSiteMapper.batchUpdateChildreAncestors(list);
    }

    /**
     * 删除站点信息
     *
     * @param siteId 站点id
     * @return 删除站点信息
     */
    @Override
    public boolean delete(Long[] siteId) {
        //删除站点栏目关系
        boolean result = cmsSiteColumnMapper.deleteSiteColumnBySiteId(siteId) > 0;
        //删除站点
        boolean result1 = cmsSiteMapper.deleteSites(siteId) > 0;
        if (result1) {
            ThreadUtil.execute(() -> refreshCache());
        }
        return result1;
    }

    /**
     * 站点信息分页列表
     *
     * @param cmsSiteQuery 查询条件
     * @return 站点信息分页列表
     */
    @Override
    public CommonPage<CmsSiteVo> pageList(CmsSiteQuery cmsSiteQuery) {
        IPage<CmsSiteVo> page = new Page<>(cmsSiteQuery.getCurrent(), cmsSiteQuery.getPageSize());
        page = cmsSiteMapper.getPage(page, cmsSiteQuery);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 站点树(异步实现)
     *
     * @param cmsSiteQuery 查询条件
     * @return 站点树
     */
    @Override
    public List<CmsSiteTreeVo> tree(CmsSiteQuery cmsSiteQuery) {
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSite::getDelFlag, 0);
        List<CmsSite> list;
        List<CmsSiteTreeVo> treeList = new ArrayList<>();
        CmsSiteTreeVo cmsSiteTreeVo;
        if (cmsSiteQuery.getSiteId() == null) {
            //无siteId查询部门拥有的最顶级站点
            wrapper.eq(CmsSite::getDeptId, deptId);
            list = cmsSiteMapper.selectList(wrapper);
        } else {
            //查询以cmsSiteQuery.getSiteId为父节点的所有站点
            wrapper.eq(CmsSite::getParentId, cmsSiteQuery.getSiteId());
            wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.EXIST.getCode());
            list = cmsSiteMapper.selectList(wrapper);
        }
        for (CmsSite cmsSite : list) {
            cmsSiteTreeVo = new CmsSiteTreeVo();
            BeanUtil.copyProperties(cmsSite, cmsSiteTreeVo);
            cmsSiteTreeVo.setParentNode(!isLeaf(cmsSiteTreeVo));
            treeList.add(cmsSiteTreeVo);
        }
        return treeList;
    }

    private boolean isLeaf(CmsSiteTreeVo cmsSiteTreeVo) {
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSite::getParentId, cmsSiteTreeVo.getSiteId());
        Integer count = cmsSiteMapper.selectCount(wrapper);
        return count > 0 ? false : true;
    }

    /**
     * 刷新站点缓存
     */
    @Override
    public void refreshCache() {
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper();
        wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.EXIST.getCode());
        List<CmsSite> cmsSites = cmsSiteMapper.selectList(wrapper);
        for (CmsSite cmsSite : cmsSites) {
            redisCache.setCacheMapValue(Constants.SITE_DICT, String.valueOf(cmsSite.getSiteId()), cmsSite.getSiteFile());
        }
    }

    /**
     * 站点树(异步实现)
     *
     * @param cmsSiteQuery 查询条件
     * @return 站点树
     */
    @Override
    public CmsSiteAllTreeVo allTree(CmsSiteQuery cmsSiteQuery) {
        //查询最上层节点
        CmsSite cmsSite = getTopNode(cmsSiteQuery);
        if (null == cmsSite) {
            throw new BusinessException(ResponseEnum.NO_DATA);
        }
        //查询所有下属节点
        List<CmsSiteAllTreeVo> treeList = cmsSiteMapper.selectAllTree(cmsSite.getSiteId());

        CmsSiteAllTreeVo topNode = new CmsSiteAllTreeVo();
        BeanUtil.copyProperties(cmsSite, topNode);

        //树结构组装
        if(CollectionUtil.isNotEmpty(treeList)){
            List<CmsSiteAllTreeVo> tree = new ArrayList<>();
            tree.add(topNode);
            assembly(tree, treeList);
            return topNode;
        }
        return topNode;
    }

    private void assembly(List<CmsSiteAllTreeVo> tree, List<CmsSiteAllTreeVo> treeList) {
        for (CmsSiteAllTreeVo cmsSiteAllTreeVo : tree) {
            List<CmsSiteAllTreeVo> children = new ArrayList<>();
            Iterator<CmsSiteAllTreeVo> iterator = treeList.iterator();
            CmsSiteAllTreeVo next;
            while (iterator.hasNext()) {
                next = iterator.next();
                if(next.getParentId().longValue() == cmsSiteAllTreeVo.getSiteId().longValue()){
                    children.add(next);
                    iterator.remove();
                }
            }
            cmsSiteAllTreeVo.setChildren(children);
            if(CollectionUtil.isNotEmpty(treeList)){
                assembly(cmsSiteAllTreeVo.getChildren(), treeList);
            }
        }
    }

    private CmsSite getTopNode(CmsSiteQuery cmsSiteQuery) {
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSite::getDeptId, deptId);
        if (null != cmsSiteQuery.getSiteId()) {
            wrapper.eq(CmsSite::getSiteId, cmsSiteQuery.getSiteId());
        }
        if (StringUtils.isNotEmpty(cmsSiteQuery.getSiteName())) {
            wrapper.eq(CmsSite::getSiteName, cmsSiteQuery.getSiteName());
        }
        wrapper.eq(CmsSite::getDelFlag, 0);
        wrapper.last(" limit 0, 1");
        return cmsSiteMapper.selectOne(wrapper);
    }

    private boolean saveSiteWithColumn(CmsSiteColumnAo cmsSiteColumnAo) {
        CmsSiteColumn cmsSiteColumn;
        List<CmsSiteColumn> list = new ArrayList<>();
        Long[] columnIds = cmsSiteColumnAo.getColumnId();
        for (int i = 0; i < columnIds.length; i++) {
            cmsSiteColumn = new CmsSiteColumn();
            cmsSiteColumn.setSiteId(cmsSiteColumnAo.getSiteId());
            cmsSiteColumn.setColumnId(columnIds[i]);
            list.add(cmsSiteColumn);
        }
        boolean result1 = cmsSiteColumnMapper.batchInsert(list);
        return result1;
    }

    /**
     * 站点详情
     * @param siteId 站点id
     * @return 站点详情
     */
    @Override
    public CmsSiteDetailsVo getDetails(Long siteId) {
        CmsSite cmsSite = cmsSiteMapper.selectById(siteId);
        CmsSiteDetailsVo cmsSiteDetailsVo = new CmsSiteDetailsVo();
        BeanUtil.copyProperties(cmsSite, cmsSiteDetailsVo);
        LambdaQueryWrapper<CmsSiteColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSiteColumn::getSiteId, siteId);
        List<CmsSiteColumn> siteColumns = cmsSiteColumnMapper.selectList(wrapper);
        if(null != siteColumns){
            List<Long> columnIds = siteColumns.stream().map(k -> k.getColumnId()).collect(Collectors.toList());
            cmsSiteDetailsVo.setColumnIds(columnIds);
        }
        return cmsSiteDetailsVo;
    }

    /**
     * 维护部门栏目关系
     * @param cmsSiteColumnAo 部门栏目表单
     * @return 维护结果
     */
    @Override
    public boolean maintainSiteWithColumn(CmsSiteColumnAo cmsSiteColumnAo) {
        LambdaUpdateWrapper<CmsSiteColumn> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsSiteColumn::getSiteId, cmsSiteColumnAo.getSiteId());
        cmsSiteColumnMapper.delete(wrapper);
        return saveSiteWithColumn(cmsSiteColumnAo);
    }
}
