package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
public interface ICmsFinalArticleService extends IService<CmsFinalArticle> {

    /**
     * 发布文章
     * @return
     */
    void generateArticle();

    /**
     * 发布文章
     * @param articleId 文章id
     */
    void generateArticle(Long articleId);

    /**
     * 发布文章
     * @param articleIds 文章集合
     */
    void generateArticle(List<Long> articleIds);

    /**
     * 获取栏目模板
     * @param columnId 栏目id
     * @return 栏目模板
     */
    String getTemplateByColumn(Long columnId);
}
