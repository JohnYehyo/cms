package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsColumnDeptVo;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;
import com.rongji.rjsoft.vo.system.dept.SysDeptAllTreeVo;

/**
 * <p>
 * 栏目部门表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-26
 */
public interface ICmsColumnDeptService extends IService<CmsColumnDept> {

    /**
     * 添加栏目部门关系
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 添加结果
     */
    boolean add(CmsColumnDeptAo cmsColumnDeptAo);

    /**
     * 更新栏目部门关系
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 更新结果
     */
    boolean edit(CmsColumnDeptAo cmsColumnDeptAo);

    /**
     * 删除栏目部门关系
     * @param columnIds 栏目id集合
     * @return 删除结果
     */
    boolean delete(Long[] columnIds);

    /**
     * 栏目部门关系分页查询
     * @param cmsColumnDeptQuery 查询对象
     * @return 分页结果
     */
    CommonPage<CmsColumnDeptVo> getPage(CmsColumnDeptQuery cmsColumnDeptQuery);

    /**
     * 通过站点获取部门同步树
     * @param siteId 站点id
     * @return 部门同步树
     */
    SysDeptAllTreeVo allDeptTree(Long siteId);

    /**
     * 通过栏目id查询授权部门
     * @param cmsSiteColumnQuery 查询条件
     * @return 授权关系分页
     */
    CommonPage<CmsSiteColumnDeptVo> getPageByColumnId(CmsSiteColumnQuery cmsSiteColumnQuery);

    /**
     * 删除栏目部门关系
     * @param cmsColumnDeptAo 参数体
     * @return 删除结果
     */
    boolean deleteRelation(CmsColumnDeptAo cmsColumnDeptAo);
}
