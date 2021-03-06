package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsSiteAo;
import com.rongji.rjsoft.common.security.util.SecurityUtils;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.LanguageUtil;
import com.rongji.rjsoft.common.util.LogUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsAuthority;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsSiteMapper;
import com.rongji.rjsoft.query.content.CmsSiteQuery;
import com.rongji.rjsoft.service.ICmsAuthorityService;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.*;
import lombok.AllArgsConstructor;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
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

    private final CmsColumnMapper cmsColumnMapper;

    private final RedisCache redisCache;

    private final ICmsAuthorityService cmsAuthorityService;

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
        String siteFile = null;
        try {
            siteFile = LanguageUtil.hanToPinyin(cmsSiteAo.getSiteName());
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            LogUtils.error("{}:汉语转拼音失败", cmsSiteAo.getSiteName(), e);
        }
        cmsSite.setSiteFile(siteFile);
        boolean result = cmsSiteMapper.insert(cmsSite) > 0;
        if (result) {
            ThreadUtil.execute(this::refreshCache);
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

        String siteFile = null;
        try {
            siteFile = LanguageUtil.hanToPinyin(cmsSiteAo.getSiteName());
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            LogUtils.error("{}:汉语转拼音失败", cmsSiteAo.getSiteName(), e);
        }
        cmsSite.setSiteFile(siteFile);

        boolean result = cmsSiteMapper.updateById(cmsSite) > 0;
        if (result) {
            ThreadUtil.execute(this::refreshCache);
        }
        return result;
    }

    private void updateSiteChildren(Long siteId, String newAncestors, String oldAncestors) {
        List<CmsSite> list = cmsSiteMapper.selectChildrenBySiteId(siteId);
        if (null == list || list.isEmpty()) {
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
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long[] siteId) {
        //删除站点栏目关系
        boolean result = cmsColumnMapper.deleteBySiteId(siteId) > 0;
        //删除站点
        boolean result1 = cmsSiteMapper.deleteSites(siteId) > 0;
        if (result1) {
            ThreadUtil.execute(this::refreshCache);
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
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.EXIST.getCode());
        List<CmsSite> list;
        List<CmsSiteTreeVo> treeList = new ArrayList<>();
        CmsSiteTreeVo cmsSiteTreeVo;
        if (cmsSiteQuery.getSiteId() == null) {
            //无siteId查询顶级site(parentId为0的)
            wrapper.eq(CmsSite::getParentId, 0);
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
        return count <= 0;
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
     * 站点树
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
        if (CollectionUtil.isNotEmpty(treeList)) {
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
                if (next.getParentId().longValue() == cmsSiteAllTreeVo.getSiteId().longValue()) {
                    children.add(next);
                    iterator.remove();
                }
            }
            cmsSiteAllTreeVo.setChildren(children);
            if (CollectionUtil.isNotEmpty(treeList)) {
                assembly(cmsSiteAllTreeVo.getChildren(), treeList);
            }
        }
    }

    private CmsSite getTopNode(CmsSiteQuery cmsSiteQuery) {
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        //todo 通过部门获取顶级节点
//        wrapper.eq(CmsSite::getDeptId, deptId);
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

    /**
     * 站点详情
     *
     * @param siteId 站点id
     * @return 站点详情
     */
    @Override
    public CmsSiteDetailsVo getDetails(Long siteId) {
        CmsSite cmsSite = cmsSiteMapper.selectById(siteId);
        CmsSiteDetailsVo cmsSiteDetailsVo = new CmsSiteDetailsVo();
        BeanUtil.copyProperties(cmsSite, cmsSiteDetailsVo);
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getSiteId, siteId);
        List<CmsColumn> siteColumns = cmsColumnMapper.selectList(wrapper);
        if (null != siteColumns) {
            List<Long> columnIds = siteColumns.stream().map(CmsColumn::getColumnId).collect(Collectors.toList());
            cmsSiteDetailsVo.setColumnIds(columnIds);
        }
        return cmsSiteDetailsVo;
    }

    /**
     * 通过站点及栏目获取栏目异步树
     * @param cmsSiteQuery 查询条件
     * @return 栏目异步树
     */
    @Override
    public List<CmsSiteColumnTreeVo> getListBySite(CmsSiteQuery cmsSiteQuery) {
        List<CmsSiteTreeVo> siteTree = tree(cmsSiteQuery);
        //组装
        List<CmsSiteColumnTreeVo> siteTreeList = getCmsSiteColumnTree(siteTree);
        return siteTreeList;
    }

    /**
     * 组装站点栏目树
     * @param siteTree
     * @return
     */
    private List<CmsSiteColumnTreeVo> getCmsSiteColumnTree(List<CmsSiteTreeVo> siteTree) {
        List<CmsSiteColumnTreeVo> siteTreeList = new ArrayList<>();
        CmsSiteColumnTreeVo cmsSiteColumnTreeVo;
        for (CmsSiteTreeVo cmsSiteTreeVo : siteTree) {
            cmsSiteColumnTreeVo = new CmsSiteColumnTreeVo();
            cmsSiteColumnTreeVo.setId(cmsSiteTreeVo.getSiteId() + "_" + 0);
            cmsSiteColumnTreeVo.setParentId(cmsSiteTreeVo.getParentId() + "_" + 0);
            cmsSiteColumnTreeVo.setName(cmsSiteTreeVo.getSiteName());
            cmsSiteColumnTreeVo.setParentNode(cmsSiteTreeVo.isParentNode());
            cmsSiteColumnTreeVo.setType(0);
            siteTreeList.add(cmsSiteColumnTreeVo);
        }
        return siteTreeList;
    }

    /**
     * 通过站点及栏目获取有权限栏目异步树
     * @param cmsSiteQuery 查询条件
     * @return 栏目异步树
     */
    @Override
    public List<CmsSiteColumnTreeVo> getLimitListBySite(CmsSiteQuery cmsSiteQuery) {
        List<CmsSiteTreeVo> siteTree = limitTree(cmsSiteQuery);
        List<CmsSiteColumnTreeVo> siteTreeList = getCmsSiteColumnTree(siteTree);
        return siteTreeList;
    }

    /**
     * 已授权的站点树(异步实现)
     *
     * @param cmsSiteQuery 查询条件
     * @return 站点树
     */
    @Override
    public List<CmsSiteTreeVo> limitTree(CmsSiteQuery cmsSiteQuery) {

        //获取当前用户所有授权的站点节点
        List<Long> limitSiteIds = getLimitSiteIds();

        LambdaQueryWrapper<CmsSite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.EXIST.getCode());
        List<CmsSite> list;
        List<CmsSiteTreeVo> treeList = new ArrayList<>();
        CmsSiteTreeVo cmsSiteTreeVo;
        if (cmsSiteQuery.getSiteId() == null) {
            //无siteId查询顶级site(parentId为0的)
            wrapper.eq(CmsSite::getParentId, 0);
            list = cmsSiteMapper.selectList(wrapper);
            list = getLimitNode(limitSiteIds, list);

        } else {
            //查询以cmsSiteQuery.getSiteId为父节点的所有站点
            wrapper.eq(CmsSite::getParentId, cmsSiteQuery.getSiteId());
            wrapper.eq(CmsSite::getDelFlag, DelFlagEnum.EXIST.getCode());
            list = cmsSiteMapper.selectList(wrapper);
            list = getLimitNode(limitSiteIds, list);
        }
        if(CollectionUtil.isEmpty(list)){
            return treeList;
        }
        for (CmsSite cmsSite : list) {
            cmsSiteTreeVo = new CmsSiteTreeVo();
            BeanUtil.copyProperties(cmsSite, cmsSiteTreeVo);
            cmsSiteTreeVo.setParentNode(!isLeaf(cmsSiteTreeVo));
            treeList.add(cmsSiteTreeVo);
        }
        return treeList;
    }

    /**
     * 获取当前用户所有授权的站点节点
     * @return
     */
    private List<Long> getLimitSiteIds() {
        Long deptId = SecurityUtils.getLoginUser().getSysDept().getDeptId();
        LambdaQueryWrapper<CmsAuthority> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmsAuthority::getDeptId, deptId);
        List<CmsAuthority> limits = cmsAuthorityService.list(queryWrapper);
        List<Long> limitSiteIds = limits.stream().map(k -> k.getSiteId()).collect(Collectors.toList());
        return limitSiteIds;
    }

    private List<CmsSite> getLimitNode(List<Long> limitSiteIds, List<CmsSite> list) {
        List<CmsSite> allowList = new ArrayList<>();
        for (CmsSite cmsSite: list) {
            if(limitSiteIds.contains(cmsSite.getSiteId())){
                allowList.add(cmsSite);
            }
        }
        //本次是否含有权的站点
        if(CollectionUtil.isEmpty(allowList)){
            allowList = recursion(limitSiteIds, list);
        }
        return allowList;
    }

    /**
     * 递归每次查询直到含有有权的站点
     * @param limitSiteIds
     * @param list
     * @return
     */
    private List<CmsSite> recursion(List<Long> limitSiteIds, List<CmsSite> list) {
        List<Long> unAllowIds = list.stream().map(k -> k.getSiteId()).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(unAllowIds)){
            return null;
        }
        List<CmsSite> allowList = new ArrayList<>();
        list = cmsSiteMapper.selectSiteByParents(unAllowIds);
        for (CmsSite cmsSite: list) {
            if(limitSiteIds.contains(cmsSite.getSiteId())){
                allowList.add(cmsSite);
            }
        }
        if(CollectionUtil.isEmpty(allowList)){
            return recursion(limitSiteIds, list);
        }
        return allowList;
    }
}
