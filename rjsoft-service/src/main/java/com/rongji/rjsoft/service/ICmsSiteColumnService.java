package com.rongji.rjsoft.service;

import com.rongji.rjsoft.vo.ResponseVo;

/**
 * @description: 站点栏目
 * @author: JohnYehyo
 * @create: 2022-02-21 17:07:09
 */
public interface ICmsSiteColumnService {


    ResponseVo tree(String siteColumnId);
}
