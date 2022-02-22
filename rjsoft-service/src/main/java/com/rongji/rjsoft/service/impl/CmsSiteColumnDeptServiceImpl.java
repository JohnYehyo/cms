package com.rongji.rjsoft.service.impl;

import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.ao.content.CmsSiteColumnDeptAo;
import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.service.ICmsColumnDeptService;
import com.rongji.rjsoft.service.ICmsSiteColumnDeptService;
import com.rongji.rjsoft.service.ICmsSiteDeptService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 站点栏目授权部门
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
            cmsSiteDeptAo.setDeptId(cmsSiteColumnDeptAo.getDeptId());
            return cmsSiteDeptService.add(cmsSiteDeptAo);
        }
        //栏目
        CmsColumnDeptAo cmsColumnDeptAo = new CmsColumnDeptAo();
        Long columnId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[1]);
        cmsColumnDeptAo.setColumnId(columnId);
        cmsColumnDeptAo.setDeptId(cmsSiteColumnDeptAo.getDeptId());
        return cmsColumnDeptService.add(cmsColumnDeptAo);
    }

    /**
     * 站点栏目部门关系分页查询
     * @param cmsSiteColumnQuery 站点栏目部门关系分页查询对象
     * @return 分页结果
     */
    @Override
    public CommonPage<CmsSiteColumnDeptVo> getPage(CmsSiteColumnQuery cmsSiteColumnQuery) {
        //部门
        if (0 == cmsSiteColumnQuery.getType()) {
            Long siteId = Long.valueOf(cmsSiteColumnQuery.getId().split("_")[0]);
            cmsSiteColumnQuery.setSiteId(siteId);
            return cmsSiteDeptService.getPageBySiteId(cmsSiteColumnQuery);
        }
        //栏目
        Long columnId = Long.valueOf(cmsSiteColumnQuery.getId().split("_")[1]);
        cmsSiteColumnQuery.setColumnId(columnId);
        return cmsColumnDeptService.getPageByColumnId(cmsSiteColumnQuery);
    }

    /**
     * 删除站点栏目对部门的授权
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 删除结果
     */
    @Override
    public boolean delete(CmsSiteColumnDeptAo cmsSiteColumnDeptAo) {
        //部门
        if (0 == cmsSiteColumnDeptAo.getType()) {
            CmsSiteDeptAo cmsSiteDeptAo = new CmsSiteDeptAo();
            Long siteId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[0]);
            cmsSiteDeptAo.setSiteId(siteId);
            cmsSiteDeptAo.setDeptId(cmsSiteColumnDeptAo.getDeptId());
            return cmsSiteDeptService.deleteRelation(cmsSiteDeptAo);
        }
        //栏目
        CmsColumnDeptAo cmsColumnDeptAo = new CmsColumnDeptAo();
        Long columnId = Long.valueOf(cmsSiteColumnDeptAo.getId().split("_")[1]);
        cmsColumnDeptAo.setColumnId(columnId);
        cmsColumnDeptAo.setDeptId(cmsSiteColumnDeptAo.getDeptId());
        return cmsColumnDeptService.deleteRelation(cmsColumnDeptAo);
    }
}
