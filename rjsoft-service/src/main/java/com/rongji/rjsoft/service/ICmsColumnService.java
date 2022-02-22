package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsColumnAo;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.query.content.CmsColumnQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.*;

import java.util.List;

/**
 * <p>
 * 栏目信息表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-15
 */
public interface ICmsColumnService extends IService<CmsColumn> {

    /**
     * 添加栏目
     * @param cmsColumnAo 栏目信息
     * @return 添加结果
     */
    boolean add(CmsColumnAo cmsColumnAo);

    /**
     * 编辑栏目
     * @param cmsColumnAo 栏目信息
     * @return 编辑结果
     */
    boolean edit(CmsColumnAo cmsColumnAo);

    /**
     * 删除栏目
     * @param columnIds 站点id集合
     * @return 删除结果
     */
    boolean delete(Long[] columnIds);

    /**
     * 栏目分页列表
     * @param cmsColumnQuery 查询条件
     * @return 栏目分页列表
     */
    CommonPage<CmsColumnVo> pageList(CmsColumnQuery cmsColumnQuery);

    /**
     * 栏目树查询异步
     * @param cmsColumnQuery 查询条件
     * @return 栏目树查询
     */
    List<CmsColumnTreeVo> tree(CmsColumnQuery cmsColumnQuery);

    /**
     * 获取站点下部门拥有的栏目树
     * @param siteId 站点Id
     * @param deptId 部门Id
     * @return 栏目树
     */
    List<CmsColumnAllTree> getColumnTreeBySite(Long siteId, Long deptId);

    /**
     * 获取栏目详情
     * @param columnId 栏目id
     * @return 栏目详情
     */
    CmsColumnDetailsVo getDetails(Long columnId);

    /**
     * 刷新栏目缓存
     */
    void refreshCache();

    /**
     * 通过站点及栏目获取栏目异步树
     * @param siteId 站点id
     * @param columnId 栏目id
     * @return 栏目异步树
     */
    List<CmsSiteColumnTreeVo> getListBySite(Long siteId, Long columnId);
}
