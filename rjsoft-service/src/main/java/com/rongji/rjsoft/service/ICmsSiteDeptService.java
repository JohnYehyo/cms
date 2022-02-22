package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.entity.content.CmsSiteDept;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.query.content.CmsSiteDeptQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;

/**
 * <p>
 * 站点部门管理关系表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-22
 */
public interface ICmsSiteDeptService extends IService<CmsSiteDept> {

    /**
     * 添加站点部门关系
     * @param cmsSiteDeptAo 站点部门关系参数体
     * @return 添加结果
     */
    boolean add(CmsSiteDeptAo cmsSiteDeptAo);

    /**
     * 更新站点部门关系
     * @param cmsSiteDeptAo 站点部门关系参数体
     * @return 更新结果
     */
    boolean edit(CmsSiteDeptAo cmsSiteDeptAo);

    /**
     * 删除站点部门关系
     * @param siteIds 站点id几个
     * @return 删除结果
     */
    boolean delete(Long[] siteIds);

    /**
     * 站点部门关系分页查询
     * @param cmsSiteDeptQuery 站点部门关系分页查询对象
     * @return 分页结果
     */
    Object getPage(CmsSiteDeptQuery cmsSiteDeptQuery);

    /**
     * 通过站点获取部门同步树
     * @param siteId 站点id
     * @return 部门信息
     */
    Object allDeptTree(Long siteId);

    /**
     * 通过站点id查询授权部门
     * @param cmsSiteColumnQuery 查询条件
     * @return 授权关系分页
     */
    CommonPage<CmsSiteColumnDeptVo> getPageBySiteId(CmsSiteColumnQuery cmsSiteColumnQuery);

    /**
     * 删除站点部门关系
     * @param cmsSiteDeptAo 参数体
     * @return 删除结果
     */
    boolean deleteRelation(CmsSiteDeptAo cmsSiteDeptAo);
}
