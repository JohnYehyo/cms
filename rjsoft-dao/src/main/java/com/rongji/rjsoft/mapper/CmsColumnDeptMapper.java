package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.vo.content.CmsColumnDeptVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 栏目部门表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-26
 */
public interface CmsColumnDeptMapper extends BaseMapper<CmsColumnDept> {

    /**
     * 删除栏目部门关系
     *
     * @param columnIds
     * @return
     */
    int deleteBatch(Long[] columnIds);

    /**
     * 栏目部门分页查询
     *
     * @param page               分页对象
     * @param cmsColumnDeptQuery 查询对象
     * @return 分页结果
     */
    IPage<CmsColumnDeptVo> getPage(IPage<CmsColumnDeptVo> page, @Param("param") CmsColumnDeptQuery cmsColumnDeptQuery);
}
