package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsColumnDeptDetailVo;
import com.rongji.rjsoft.vo.content.CmsColumnDeptVo;

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

}
