package com.rongji.rjsoft.service;

import com.rongji.rjsoft.vo.content.CmsSiteColumnTreeVo;

import java.util.List;

/**
 * @description: 站点栏目
 * @author: JohnYehyo
 * @create: 2022-02-21 17:07:09
 */
public interface ICmsSiteColumnService {

    /**
     * 站点栏目树
     * @param siteColumnId 站点栏目id
     * @return 站点栏目树
     */
    List<CmsSiteColumnTreeVo> tree(String siteColumnId);

    /**
     * 站点栏目权限树
     * @param siteColumnId 站点栏目id
     * @return 站点栏目树
     */
    List<CmsSiteColumnTreeVo> limitTree(String siteColumnId);
}
