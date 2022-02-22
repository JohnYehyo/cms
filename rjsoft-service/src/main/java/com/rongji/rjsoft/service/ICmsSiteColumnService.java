package com.rongji.rjsoft.service;

import com.rongji.rjsoft.vo.ResponseVo;
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
}
