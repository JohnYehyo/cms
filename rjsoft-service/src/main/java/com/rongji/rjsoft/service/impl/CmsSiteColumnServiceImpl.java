package com.rongji.rjsoft.service.impl;

import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsSiteQuery;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.service.ICmsSiteColumnService;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.vo.ResponseVo;
import com.rongji.rjsoft.vo.content.CmsSiteColumnTreeVo;
import com.rongji.rjsoft.vo.content.CmsSiteTreeVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final ICmsColumnService columnService;

    @Override
    public ResponseVo tree(String siteColumnId) {
        CmsSiteQuery cmsSiteQuery = new CmsSiteQuery();
        if(StringUtils.isEmpty(siteColumnId)){
          siteColumnId = "0";
        }
        String[] ids = siteColumnId.split("_");
        String siteId = ids[0];
        if(ids.length == 1) {
            //1. 参数只有站点id
            cmsSiteQuery.setSiteId(Long.parseLong(siteId));
            //1.1 查询站点树
            List<CmsSiteTreeVo> siteTree = cmsSiteService.tree(cmsSiteQuery);
            List<CmsSiteColumnTreeVo> siteTreeList = new ArrayList<>();
            CmsSiteColumnTreeVo cmsSiteColumnTreeVo;
            for (CmsSiteTreeVo cmsSiteTreeVo : siteTree) {
                cmsSiteColumnTreeVo = new CmsSiteColumnTreeVo();
                cmsSiteColumnTreeVo.setId(cmsSiteTreeVo.getSiteId() + "_" + 0);
                cmsSiteColumnTreeVo.setParentId(String.valueOf(cmsSiteTreeVo.getParentId()) + "_" + 0);
                cmsSiteColumnTreeVo.setName(cmsSiteTreeVo.getSiteName());
                cmsSiteColumnTreeVo.setParentNode(cmsSiteTreeVo.isParentNode());
                siteTreeList.add(cmsSiteColumnTreeVo);
            }
            //排除顶级站点
            if (!siteId.equals("0")) {
                //1.2 查询站点树节点对应的栏目树
                List<CmsSiteColumnTreeVo> columnTreeList = columnService.getListBySite(Long.valueOf(siteId), null);
                siteTreeList.addAll(columnTreeList);
            }
            return ResponseVo.response(ResponseEnum.SUCCESS, siteTreeList);
        }
        //2. 参数包含站点id+栏目id
        //下属栏目树
        String columnId = ids[1];
        List<CmsSiteColumnTreeVo> columnTreeList = columnService.getListBySite(Long.valueOf(siteId), Long.valueOf(columnId));;
        return ResponseVo.response(ResponseEnum.SUCCESS, columnTreeList);
    }
}
