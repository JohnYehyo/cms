package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.entity.content.CmsAuthority;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.query.content.CmsAuthorityQuery;
import com.rongji.rjsoft.vo.content.CmsAuthorityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  站点栏目授权Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-24
 */
public interface CmsAuthorityMapper extends BaseMapper<CmsAuthority> {

    /**
     * 批量插入授权关系
     * @param list 授权关系
     * @return 插入结果
     */
    int batchInsert(List<CmsAuthority> list);

    /**
     * 通过栏目id查询授权列表
     *
     * @param siteId 站点id
     * @param columns 栏目集合
     * @return 授权列表
     */
    List<CmsAuthorityVo> getPageByColumnIds(@Param("siteId") Long siteId, @Param("columns") List<CmsColumn> columns);

    /**
     * 通过栏目id查询授权分页类表
     *
     * @param page 分页对象
     * @param cmsAuthorityQuery 查询条件对象
     * @return 授权关系分页
     */
    IPage<CmsAuthorityVo> getPageByColumnId(IPage<CmsAuthorityVo> page, @Param("param") CmsAuthorityQuery cmsAuthorityQuery);

    /**
     * 删除授权关系
     * @param records 授权关系
     * @return 删除结果
     */
    boolean deleteRelations(@Param("records") List<CmsAuthority> records);

    /**
     * 删除授权
     *
     * @param siteId    站点id
     * @param columnId  栏目id
     * @param deptIds   部门集合
     * @return 删除结果
     */
    boolean deleteRelation(@Param("siteId") Long siteId, @Param("columnId") Long columnId, @Param("deptIds") Long[] deptIds);
}
