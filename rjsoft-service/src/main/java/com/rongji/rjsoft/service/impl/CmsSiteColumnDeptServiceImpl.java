package com.rongji.rjsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.ao.content.CmsSiteColumnDeptAo;
import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.service.ICmsColumnDeptService;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.service.ICmsSiteColumnDeptService;
import com.rongji.rjsoft.service.ICmsSiteDeptService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: JohnYehyo
 * @create: 2022-02-22 18:01:17
 */
@Service
@AllArgsConstructor
public class CmsSiteColumnDeptServiceImpl implements ICmsSiteColumnDeptService {

    private final ICmsSiteDeptService cmsSiteDeptService;

    private final ICmsColumnDeptService cmsColumnDeptService;

    private final ICmsColumnService cmsColumnService;

    /**
     * 站点栏目授权部门
     *
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 授权结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean authorization(CmsSiteColumnDeptAo cmsSiteColumnDeptAo) {
        //部门
        if (0 == cmsSiteColumnDeptAo.getType()) {
            CmsSiteDeptAo cmsSiteDeptAo = new CmsSiteDeptAo();
            Long siteId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[0]);
            cmsSiteDeptAo.setSiteId(siteId);
            Long[] deptIds = cmsSiteColumnDeptAo.getDeptId();
            //重组栏目部门信息
            List<CmsColumnDept> records = getCmsColumnDepts(siteId, deptIds);
            return cmsColumnDeptService.batchAdd(records);
        }
        //栏目
        CmsColumnDeptAo cmsColumnDeptAo = new CmsColumnDeptAo();
        Long columnId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[1]);
        cmsColumnDeptAo.setColumnId(columnId);
        cmsColumnDeptAo.setDeptId(cmsSiteColumnDeptAo.getDeptId());
        return cmsColumnDeptService.add(cmsColumnDeptAo);
    }

    /**
     * 重组栏目部门信息
     * @param siteId 站点
     * @param deptIds 部门id集合
     * @return 栏目部门信息
     */
    private List<CmsColumnDept> getCmsColumnDepts(Long siteId, Long[] deptIds) {
        List<CmsColumn> list = getCmsColumnsBySite(siteId);
        List<CmsColumnDept> records = new ArrayList<>();
        CmsColumnDept cmsColumnDept;
        for (CmsColumn cmsColumn : list) {
            for (Long deptId: deptIds) {
                cmsColumnDept = new CmsColumnDept();
                cmsColumnDept.setColumnId(cmsColumn.getColumnId());
                cmsColumnDept.setDeptId(deptId);
                records.add(cmsColumnDept);
            }
        }
        return records;
    }

    /**
     * 查询站点下属栏目
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
     * 站点栏目部门关系分页查询
     *
     * @param cmsSiteColumnQuery 站点栏目部门关系分页查询对象
     * @return 分页结果
     */
    @Override
    public CommonPage<CmsSiteColumnDeptVo> getPage(CmsSiteColumnQuery cmsSiteColumnQuery) {
        //部门
        if (0 == cmsSiteColumnQuery.getType()) {
            Long siteId = Long.valueOf(cmsSiteColumnQuery.getId().split("_")[0]);
            List<CmsColumn> cmsColumnsBySite = getCmsColumnsBySite(siteId);
            return cmsColumnDeptService.getPageByColumnIds(cmsColumnsBySite, cmsSiteColumnQuery);
        }
        //栏目
        Long columnId = Long.valueOf(cmsSiteColumnQuery.getId().split("_")[1]);
        cmsSiteColumnQuery.setColumnId(columnId);
        return cmsColumnDeptService.getPageByColumnId(cmsSiteColumnQuery);
    }

    /**
     * 删除站点栏目对部门的授权
     *
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 删除结果
     */
    @Override
    public boolean delete(CmsSiteColumnDeptAo cmsSiteColumnDeptAo) {
        //部门
        if (0 == cmsSiteColumnDeptAo.getType()) {
            Long siteId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[0]);
            Long[] deptIds = cmsSiteColumnDeptAo.getDeptId();
            List<CmsColumnDept> records = getCmsColumnDepts(siteId, deptIds);
            return cmsColumnDeptService.deleteRelations(records);
        }
        //栏目
        CmsColumnDeptAo cmsColumnDeptAo = new CmsColumnDeptAo();
        Long columnId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[1]);
        cmsColumnDeptAo.setColumnId(columnId);
        cmsColumnDeptAo.setDeptId(cmsSiteColumnDeptAo.getDeptId());
        return cmsColumnDeptService.deleteRelation(cmsColumnDeptAo);
    }
}
