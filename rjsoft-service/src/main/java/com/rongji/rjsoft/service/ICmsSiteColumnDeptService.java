package com.rongji.rjsoft.service;

import com.rongji.rjsoft.ao.content.CmsSiteColumnDeptAo;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;

/**
 * @description:
 * @author: JohnYehyo
 * @create: 2022-02-22 18:01:01
 */
public interface ICmsSiteColumnDeptService {

    /**
     * 站点栏目授权部门
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 授权结果
     */
    boolean authorization(CmsSiteColumnDeptAo cmsSiteColumnDeptAo);

    /**
     * 站点栏目部门关系分页查询
     * @param cmsSiteColumnQuery 站点栏目部门关系分页查询对象
     * @return 分页结果
     */
    CommonPage<CmsSiteColumnDeptVo> getPage(CmsSiteColumnQuery cmsSiteColumnQuery);

    /**
     * 删除站点栏目对部门的授权
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 删除结果
     */
    boolean delete(CmsSiteColumnDeptAo cmsSiteColumnDeptAo);
}
