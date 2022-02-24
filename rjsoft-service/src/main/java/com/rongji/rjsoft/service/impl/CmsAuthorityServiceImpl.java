package com.rongji.rjsoft.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsAuthorityAo;
import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsAuthority;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsAuthorityMapper;
import com.rongji.rjsoft.query.content.CmsAuthorityQuery;
import com.rongji.rjsoft.service.ICmsAuthorityService;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsAuthorityVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 站点栏目授权服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-24
 */
@Service
@AllArgsConstructor
public class CmsAuthorityServiceImpl extends ServiceImpl<CmsAuthorityMapper, CmsAuthority> implements ICmsAuthorityService {

    private final CmsAuthorityMapper cmsAuthorityMapper;

    private final ICmsColumnService cmsColumnService;

    /**
     * 授权
     *
     * @param cmsAuthorityAo 授权关系参数体
     * @return 授权结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean authorization(CmsAuthorityAo cmsAuthorityAo) {
        //部门
        if (0 == cmsAuthorityAo.getType()) {
            return batchAdd(cmsAuthorityAo);
        }
        //栏目
        return add(cmsAuthorityAo);
    }

    /**
     * 添加栏目部门关系
     *
     * @param cmsAuthorityAo 栏目部门关系参数体
     * @return 添加结果
     */
    private boolean add(CmsAuthorityAo cmsAuthorityAo) {
        Long siteId = Long.valueOf(cmsAuthorityAo.getId().split("_")[0]);
        Long columnId = Long.valueOf(cmsAuthorityAo.getId().split("_")[1]);
        LambdaQueryWrapper<CmsAuthority> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsAuthority::getSiteId, siteId).eq(CmsAuthority::getColumnId, columnId)
                .in(CmsAuthority::getDeptId, cmsAuthorityAo.getDeptId())
                .last(" limit 0, 1");
        CmsAuthority cmsAuthority = cmsAuthorityMapper.selectOne(wrapper);
        if (null != cmsAuthority) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "记录中已存在该授权关系,请核实后重新建立!");
        }
        return insertRecords(siteId, columnId, cmsAuthorityAo.getDeptId());
    }

    private boolean insertRecords(Long siteId, Long columnId, Long[] deptIds) {
        List<CmsAuthority> list = new ArrayList<>();
        CmsAuthority cmsAuthority;
        for (Long deptId : deptIds) {
            cmsAuthority = new CmsAuthority();
            cmsAuthority.setSiteId(siteId);
            cmsAuthority.setColumnId(columnId);
            cmsAuthority.setDeptId(deptId);
            list.add(cmsAuthority);
        }
        return cmsAuthorityMapper.batchInsert(list) > 0;
    }

    /**
     * 批量添加授权关系
     *
     * @param cmsAuthorityAo 授权关系
     * @return 添加结果
     */
    private boolean batchAdd(CmsAuthorityAo cmsAuthorityAo) {
        CmsSiteDeptAo cmsSiteDeptAo = new CmsSiteDeptAo();
        Long siteId = Long.valueOf(cmsAuthorityAo.getId().split("_")[0]);
        cmsSiteDeptAo.setSiteId(siteId);
        Long[] deptIds = cmsAuthorityAo.getDeptId();
        //重组栏目部门信息
        List<CmsAuthority> records = getCmsColumnDepts(siteId, deptIds);
        //过滤库中已存在的栏目
        if (CollectionUtil.isEmpty(records)) {
            throw new BusinessException(ResponseEnum.NO_DATA.getCode(), "该站点下无相关栏目");
        }
        Iterator<CmsAuthority> iterator = records.iterator();
        while (iterator.hasNext()) {
            LambdaQueryWrapper<CmsAuthority> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CmsAuthority::getSiteId, siteId).eq(CmsAuthority::getColumnId, iterator.next().getColumnId());
            Integer count = cmsAuthorityMapper.selectCount(wrapper);
            if (count > 0) {
                iterator.remove();
            }
        }
        return cmsAuthorityMapper.batchInsert(records) > 0;
    }

    /**
     * 重组栏目部门信息
     *
     * @param siteId  站点
     * @param deptIds 部门id集合
     * @return 栏目部门信息
     */
    private List<CmsAuthority> getCmsColumnDepts(Long siteId, Long[] deptIds) {
        List<CmsColumn> list = getCmsColumnsBySite(siteId);
        List<CmsAuthority> records = new ArrayList<>();
        CmsAuthority cmsAuthority;
        for (CmsColumn cmsColumn : list) {
            for (Long deptId : deptIds) {
                cmsAuthority = new CmsAuthority();
                cmsAuthority.setColumnId(siteId);
                cmsAuthority.setColumnId(cmsColumn.getColumnId());
                cmsAuthority.setDeptId(deptId);
                records.add(cmsAuthority);
            }
        }
        return records;
    }

    /**
     * 查询站点下属栏目
     *
     * @param siteId 站点id
     * @return 站点下属栏目
     */
    private List<CmsColumn> getCmsColumnsBySite(Long siteId) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getSiteId, siteId);
        List<CmsColumn> list = cmsColumnService.list(wrapper);
        return list;
    }

    /**
     * 授权关系分页查询
     *
     * @param cmsAuthorityQuery 授权分页查询对象
     * @return 分页结果
     */
    @Override
    public CommonPage<CmsAuthorityVo> getPage(CmsAuthorityQuery cmsAuthorityQuery) {
        Long siteId = Long.valueOf(cmsAuthorityQuery.getId().split("_")[0]);
        cmsAuthorityQuery.setSiteId(siteId);
        //部门
        if (0 == cmsAuthorityQuery.getType()) {
            List<CmsColumn> columns = getCmsColumnsBySite(siteId);
            return getPageByColumnIds(columns, cmsAuthorityQuery);
        }
        //栏目
        Long columnId = Long.valueOf(cmsAuthorityQuery.getId().split("_")[1]);
        cmsAuthorityQuery.setColumnId(columnId);
        return getPageByColumnId(cmsAuthorityQuery);
    }

    /**
     * 通过栏目id查询授权部门
     *
     * @param columns            查询条件
     * @param cmsAuthorityQuery  分页条件
     * @return 授权关系分页
     */
    private CommonPage<CmsAuthorityVo> getPageByColumnIds(List<CmsColumn> columns, CmsAuthorityQuery cmsAuthorityQuery) {
        if(CollectionUtil.isEmpty(columns)){
            throw new BusinessException(ResponseEnum.NO_DATA);
        }
        List<CmsAuthorityVo> records = cmsAuthorityMapper.getPageByColumnIds(cmsAuthorityQuery.getSiteId(), columns);
        List<CmsAuthorityVo> collect = records.stream().distinct().collect(Collectors.toList());
        return CommonPageUtils.assemblyPage(cmsAuthorityQuery, collect);
    }

    /**
     * 通过栏目id查询授权部门
     *
     * @param cmsAuthorityQuery 查询条件
     * @return 授权关系分页
     */
    private CommonPage<CmsAuthorityVo> getPageByColumnId(CmsAuthorityQuery cmsAuthorityQuery) {
        IPage<CmsAuthorityVo> page = new Page<>(cmsAuthorityQuery.getCurrent(), cmsAuthorityQuery.getPageSize());
        page = cmsAuthorityMapper.getPageByColumnId(page, cmsAuthorityQuery);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 删除授权
     *
     * @param cmsAuthorityAo 授权参数体
     * @return 删除结果
     */
    @Override
    public boolean delete(CmsAuthorityAo cmsAuthorityAo) {
        Long siteId = Long.valueOf(cmsAuthorityAo.getId().split("_")[0]);
        //部门
        if (0 == cmsAuthorityAo.getType()) {
            Long[] deptIds = cmsAuthorityAo.getDeptId();
            List<CmsAuthority> records = getCmsColumnDepts(siteId, deptIds);
            return deleteRelations(records);
        }
        //栏目
        Long columnId = Long.valueOf(cmsAuthorityAo.getId().split("_")[1]);
        return deleteRelation(siteId, columnId, cmsAuthorityAo.getDeptId());
    }

    /**
     * 删除授权关系
     *
     * @param records 授权关系
     * @return 删除结果
     */
    private boolean deleteRelations(List<CmsAuthority> records) {
        return cmsAuthorityMapper.deleteRelations(records);
    }

    /**
     * 删除授权
     *
     * @param siteId    站点id
     * @param columnId  栏目id
     * @param deptIds   部门集合
     * @return 删除结果
     */
    private boolean deleteRelation(Long siteId, Long columnId, Long[] deptIds) {
        return cmsAuthorityMapper.deleteRelation(siteId, columnId, deptIds);
    }
}
