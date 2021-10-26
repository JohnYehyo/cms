package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.*;
import com.rongji.rjsoft.entity.content.CmsArticle;
import com.rongji.rjsoft.query.content.*;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsArticleInfoVo;
import com.rongji.rjsoft.vo.content.CmsArticlePortalVo;
import com.rongji.rjsoft.vo.content.CmsArticleRefVo;
import com.rongji.rjsoft.vo.content.CmsArticleVo;

import java.util.List;

/**
 * <p>
 * 文章信息表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-22
 */
public interface ICmsArticleService extends IService<CmsArticle> {

    /**
     * 添加文章
     * @param cmsArticleAo 文章表单信息
     * @return 添加结果
     */
    boolean saveArticle(CmsArticleAo cmsArticleAo);

    /**
     * 编辑文章
     * @param cmsArticleAo 文章表单信息
     * @return 编辑结果
     */
    boolean updateArticle(CmsArticleAo cmsArticleAo);

    /**
     * 删除文章
     * @param list 删除条件
     * @return 删除结果
     */
    boolean deleteArticle(CmsArticleDeleteAo[] list);

    /**
     * 审核文章
     * @param cmsArticleAuditAo 文章状态信息
     * @return 审核结果
     */
    boolean audit(CmsArticleAuditAo cmsArticleAuditAo);

    /**
     * 文章列表
     * @param cmsArticleQuery 查询对象
     * @return 文章列表
     */
    CommonPage<CmsArticleVo> getPage(CmsArticleQuery cmsArticleQuery);

    /**
     * 文章详情
     * @param articleId 文章id
     * @return 文章详情
     */
    CmsArticleInfoVo getInfo(Long articleId);

    /**
     * 文章引用查询
     * @param articleId 文章ID
     * @return 文章引用列表
     */
    List<CmsArticleRefVo> listOfArticleRef(Long articleId);

    /**
     * 通过栏目获取文章列表
     * @param cmsColumnArticleQuery 查询对象
     * @return 文章列表
     */
    CommonPage<CmsArticlePortalVo> getArticlesByColumn(CmsColumnArticleQuery cmsColumnArticleQuery);

    /**
     * 通过标签获取文章列表
     * @param cmsTagArticleQuery 查询对象
     * @return 文章列表
     */
    CommonPage<CmsArticlePortalVo> getArticlesByTag(CmsTagArticleQuery cmsTagArticleQuery);

    /**
     * 通过类别获取文章列表
     * @param cmsCategoryArticleQuery 查询对象
     * @return 文章列表
     */
    CommonPage<CmsArticlePortalVo> getArticlesByCategory(CmsCategoryArticleQuery cmsCategoryArticleQuery);

    /**
     * 查询轮播文章
     * @param cmsSliderArticleQuery 查询对象
     * @return 轮播文章
     */
    List<CmsArticlePortalVo> getArticlesBySlider(CmsSliderArticleQuery cmsSliderArticleQuery);

    /**
     * 转发文章
     * @param cmsArticleForWardingAo 转发文章参数体
     * @return 转发结果
     */
    boolean forwarding(CmsArticleForWardingAo cmsArticleForWardingAo);

    /**
     * 移动文章
     * @param cmsArticleForMoveAo 移动文章参数体
     * @return 移动结果
     */
    boolean move(CmsArticleForMoveAo cmsArticleForMoveAo);
}
