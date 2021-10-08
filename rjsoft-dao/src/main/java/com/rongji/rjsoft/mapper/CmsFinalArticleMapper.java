package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.ao.content.CmsArticleDeleteAo;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import com.rongji.rjsoft.query.content.CmsCategoryArticleQuery;
import com.rongji.rjsoft.query.content.CmsColumnQuery;
import com.rongji.rjsoft.query.content.CmsSliderArticleQuery;
import com.rongji.rjsoft.vo.content.CmsArticleContentVo;
import com.rongji.rjsoft.vo.content.CmsArticlePortalVo;
import com.rongji.rjsoft.vo.content.CmsArticleRefVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
public interface CmsFinalArticleMapper extends BaseMapper<CmsFinalArticle> {

    /**
     * 批量保存文章栏目关系
     * @param list 关系对象集合
     * @return 保存结果
     */
    int batchInsert(List<CmsFinalArticle> list);

    /**
     * 文章引用查询
     * @param articleId 文章ID
     * @return 文章引用列表
     */
    List<CmsArticleRefVo> listOfArticleRef(Long articleId);


    /**
     * 删除文章
     * @param list 删除对象
     * @return 删除结果
     */
    int deleteArticle(CmsArticleDeleteAo[] list);

    /**
     * 通过栏目获取文章分页列表
     * @param page 分页对象
     * @param columnIds 栏目id
     * @param siteId 站点id
     * @return 文章分页列表
     */
    IPage<CmsArticlePortalVo> getArticlePageByColumn(IPage<CmsArticlePortalVo> page,
                                                     @Param("columnIds") List<Long> columnIds,
                                                     @Param("siteId") Long siteId);

    /**
     * 通过标签获取文章分页列表
     * @param page 分页对象
     * @param articleIds 文章id
     * @param siteId 站点id
     * @return 文章分页列表
     */
    IPage<CmsArticlePortalVo> getArticlePageByTag(IPage<CmsArticlePortalVo> page,
                                                  @Param("tagIds") List<Long> articleIds,
                                                  @Param("siteId") Long siteId);

    /**
     * 通过类别获取文章分页列表
     * @param page 分页对象
     * @param cmsCategoryArticleQuery 查询对象
     * @return 文章分页列表
     */
    IPage<CmsArticlePortalVo> getArticlesByCategory(IPage<CmsArticlePortalVo> page,
                                                    @Param("param") CmsCategoryArticleQuery cmsCategoryArticleQuery);

    /**
     * 获取轮播文章
     * @param cmsSliderArticleQuery 查询对象
     * @return 轮播文章
     */
    List<CmsArticlePortalVo> getArticlesBySlider(CmsSliderArticleQuery cmsSliderArticleQuery);

    /**
     * 获取待发布文章信息
     * @return
     */
    List<CmsArticleContentVo> getPublishArticel();
}
