package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.query.content.CmsColumnQuery;
import com.rongji.rjsoft.vo.content.CmsColumnAllTree;
import com.rongji.rjsoft.vo.content.CmsColumnVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 栏目信息表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-15
 */
public interface CmsColumnMapper extends BaseMapper<CmsColumn> {

    /**
     * 查询栏目分页列表
     * @param page 分页对象
     * @param cmsColumnQuery 查询条件
     * @return 栏目分页列表
     */
    IPage<CmsColumnVo> getPage(IPage<CmsColumnVo> page, @Param("param") CmsColumnQuery cmsColumnQuery);

    /**
     * 查询该节点下栏目节点
     * @param columnId 栏目id
     * @return 该节点下的所有节点
     */
    List<CmsColumn> selectChildrenByColumnId(Long columnId);

    /**
     * 更新该节点下所有节点的Ancestors
     * @param list 该节点下所有节点
     * @return 更新结果
     */
    int batchUpdateChildreAncestors(List<CmsColumn> list);

    /**
     * 删除栏目
     * @param columnId 栏目ID
     * @return 删除结果
     */
    int batchDeleteColumn(Long[] columnId);

    /**
     * 通过父节点获取父节点树信息
     * @param parentId 父节点ID
     * @return 父节点树信息
     */
    CmsColumnAllTree selectParentTreeNode(Long parentId);

    /**
     * 获取站点下的栏目树
     * @param siteId 站点ID
     * @return 栏目树
     */
    List<CmsColumnAllTree> getColumnTreeBySite(Long siteId);

}
