package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsSiteAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.mapper.CmsSiteColumnMapper;
import com.rongji.rjsoft.mapper.CmsSiteMapper;
import com.rongji.rjsoft.query.content.CmsSiteQuery;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteTreeVo;
import com.rongji.rjsoft.vo.content.CmsSiteVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        cmsSite.setAncestors(parent.getAncestors() + "," + parent.getParentId());
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
        CmsSite parent = cmsSiteMapper.selectById(cmsSiteAo.getParentId());
        CmsSite old = cmsSiteMapper.selectById(cmsSiteAo.getSiteId());

        CmsSite cmsSite = new CmsSite();
        BeanUtil.copyProperties(cmsSiteAo, cmsSite);
        cmsSite.setAncestors(parent.getAncestors() + "," + parent.getParentId());

        if (null != parent && null != old) {
            String newAncestors = parent.getAncestors() + "," + parent.getSiteId();
            String oldAncestors = old.getAncestors();
            old.setAncestors(newAncestors);
            cmsSite.setAncestors(newAncestors);
            //修改该节点下所有节点的ancestors
            updateSiteChildren(old.getSiteId(), newAncestors, oldAncestors);
        }
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
        if (result && result1) {
            ThreadUtil.execute(() -> refreshCache());
        }
        return result && result1;
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
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        List<CmsSite> list;
        List<CmsSiteTreeVo> treeList = new ArrayList<>();
        CmsSiteTreeVo cmsSiteTreeVo;
        cmsSiteQuery.setSiteId(cmsSiteQuery.getSiteId() == null ? 0L : cmsSiteQuery.getSiteId());
        //查询以cmsSiteQuery.getSiteId为父节点的所有站点
        wrapper.eq(CmsSite::getParentId, cmsSiteQuery.getSiteId());
        wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.exist.getCode());
        list = cmsSiteMapper.selectList(wrapper);
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
        wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.exist.getCode());
        List<CmsSite> cmsSites = cmsSiteMapper.selectList(wrapper);
        for (CmsSite cmsSite : cmsSites) {
            redisCache.setCacheMapValue(Constants.SITE_DICT, String.valueOf(cmsSite.getSiteId()), cmsSite.getSiteFile());
        }
    }
}
