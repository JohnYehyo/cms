package com.rongji.rjsoft.service.impl;

import com.rongji.rjsoft.query.content.CmsSiteQuery;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.service.ICmsSiteColumnService;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.vo.content.CmsSiteColumnTreeVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 站点栏目
 * @author: JohnYehyo
 * @create: 2022-02-21 17:07:29
 */
@Service
@AllArgsConstructor
public class CmsSiteColumnServiceImpl implements ICmsSiteColumnService {

    private final ICmsSiteService cmsSiteService;

    private final ICmsColumnService cmsColumnService;

    /**
     * 站点栏目树
     * @param siteColumnId 站点栏目id
     * @return 站点栏目树
     */
    @Override
    public List<CmsSiteColumnTreeVo> tree(String siteColumnId) {
        CmsSiteQuery cmsSiteQuery = new CmsSiteQuery();
        if(StringUtils.isEmpty(siteColumnId)){
          siteColumnId = "0_0";
        }
        String[] ids = siteColumnId.split("_");
        String siteId = ids[0];
        if("0".equals(ids[1])) {
            //1. 参数只有站点id
            cmsSiteQuery.setSiteId(Long.parseLong(siteId));
            //1.1 查询站点树
            List<CmsSiteColumnTreeVo> siteTreeList = cmsSiteService.getListBySite(cmsSiteQuery);
            //排除顶级站点
            if (!siteId.equals("0")) {
                //1.2 查询站点树节点对应的栏目树
                List<CmsSiteColumnTreeVo> columnTreeList = cmsColumnService.getListBySite(Long.valueOf(siteId), null);
                siteTreeList.addAll(columnTreeList);
            }
            return siteTreeList;
        }
        //2. 参数包含站点id+栏目id
        //下属栏目树
        String columnId = ids[1];
        List<CmsSiteColumnTreeVo> columnTreeList = cmsColumnService.getListBySite(Long.valueOf(siteId), Long.valueOf(columnId));;
        return columnTreeList;
    }

    /**
     * 站点栏目权限树
     * @param siteColumnId 站点栏目id
     * @return 站点栏目树
     */
    @Override
    public List<CmsSiteColumnTreeVo> limitTree(String siteColumnId) {
        CmsSiteQuery cmsSiteQuery = new CmsSiteQuery();
        if(StringUtils.isEmpty(siteColumnId)){
            siteColumnId = "0_0";
        }
        String[] ids = siteColumnId.split("_");
        String siteId = ids[0];
        if("0".equals(ids[1])) {
            //1. 参数只有站点id
            cmsSiteQuery.setSiteId(Long.parseLong(siteId));
            //1.1 查询站点树
            List<CmsSiteColumnTreeVo> siteTreeList = cmsSiteService.getLimitListBySite(cmsSiteQuery);
            //排除顶级站点
            if (!siteId.equals("0")) {
                //1.2 查询站点树节点对应的栏目树
                List<CmsSiteColumnTreeVo> columnTreeList = cmsColumnService.getLimitListBySite(Long.valueOf(siteId), null);
                siteTreeList.addAll(columnTreeList);
            }
            return siteTreeList;
        }
        //2. 参数包含站点id+栏目id
        //下属栏目树
        String columnId = ids[1];
        List<CmsSiteColumnTreeVo> columnTreeList = cmsColumnService.getLimitListBySite(Long.valueOf(siteId), Long.valueOf(columnId));;
        return columnTreeList;
    }
}
