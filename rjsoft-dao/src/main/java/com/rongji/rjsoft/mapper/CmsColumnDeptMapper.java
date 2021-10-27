package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.vo.content.CmsColumnDeptVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 批量插入栏目部门关系
     * @param list 栏目部门关系
     * @return 插入结果
     */
    int batchInsert(List<CmsColumnDept> list);

    /**
     * 栏目部门列表查询
     * @param cmsColumnDeptQuery 查询对象
     * @param deptIds 下属部门id
     * @return 栏目部门列表
     */
    List<CmsColumnDeptVo> getList(@Param("param1") CmsColumnDeptQuery cmsColumnDeptQuery, @Param("param2") List<Long> deptIds);

}
