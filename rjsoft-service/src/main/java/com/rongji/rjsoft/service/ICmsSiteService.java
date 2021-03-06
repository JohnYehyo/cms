package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsSiteAo;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.query.content.CmsSiteQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.*;

import java.util.List;

/**
 * <p>
 * 站点信息表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-15
 */
public interface ICmsSiteService extends IService<CmsSite> {

    /**
     * 新增站点信息
     * @param cmsSiteAo 站点信息
     * @return 新增结果
     */
    boolean add(CmsSiteAo cmsSiteAo);

    /**
     * 编辑站点信息
     * @param cmsSiteAo 站点信息
     * @return 编辑结果
     */
    boolean edit(CmsSiteAo cmsSiteAo);

    /**
     * 删除站点信息
     * @param siteId 站点id
     * @return 删除结果
     */
    boolean delete(Long[] siteId);

    /**
     * 站点信息分页列表
     * @param cmsSiteQuery 查询条件
     * @return 站点信息分页列表
     */
    CommonPage<CmsSiteVo> pageList(CmsSiteQuery cmsSiteQuery);

    /**
     * 站点树
     * @param cmsSiteQuery 查询条件
     * @return 站点树
     */
    List<CmsSiteTreeVo> tree(CmsSiteQuery cmsSiteQuery);

    /**
     * 刷新站点缓存
     */
    void refreshCache();

    /**
     * 站点同步树
     * @param cmsSiteQuery 查询条件
     * @return 站点树
     */
    CmsSiteAllTreeVo allTree(CmsSiteQuery cmsSiteQuery);

    /**
     * 获取站点详情
     * @param siteId 站点id
     * @return 站点详情
     */
    CmsSiteDetailsVo getDetails(Long siteId);

    /**
     * 通过站点及栏目获取栏目异步树
     * @param cmsSiteQuery 查询条件
     * @return 栏目异步树
     */
    List<CmsSiteColumnTreeVo> getListBySite(CmsSiteQuery cmsSiteQuery);

    /**
     * 通过站点及栏目获取有权限栏目异步树
     * @param cmsSiteQuery 查询条件
     * @return 栏目异步树
     */
    List<CmsSiteColumnTreeVo> getLimitListBySite(CmsSiteQuery cmsSiteQuery);

    /**
     * 已授权的站点树(异步实现)
     *
     * @param cmsSiteQuery 查询条件
     * @return 站点树
     */
    List<CmsSiteTreeVo> limitTree(CmsSiteQuery cmsSiteQuery);
}
