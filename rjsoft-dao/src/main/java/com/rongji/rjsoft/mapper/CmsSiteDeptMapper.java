package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.entity.content.CmsSiteDept;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 站点部门管理关系表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-22
 */
public interface CmsSiteDeptMapper extends BaseMapper<CmsSiteDept> {

    /**
     * 批量插入站点部门关系
     *
     * @param list 站点部门关系
     * @return 插入结果
     */
    int batchInsert(List<CmsSiteDept> list);

    /**
     * 删除站点部门关系
     *
     * @param siteIds 部门id
     * @return 删除结果
     */
    int deleteBatch(Long[] siteIds);

    /**
     * 通过站点id查询授权部门
     *
     * @param page   分页对象
     * @param siteId 站点id
     * @return 分页数据
     */
    IPage<CmsSiteColumnDeptVo> getPageBySiteId(IPage<CmsSiteColumnDeptVo> page, @Param("siteId") Long siteId);

    /**
     * 删除站点部门关系
     *
     * @param siteId  站点id
     * @param deptIds 部门id集合
     * @return 删除结果
     */
    boolean deleteRelation(@Param("siteId") Long siteId, @Param("deptIds") Long[] deptIds);
}
