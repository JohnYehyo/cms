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
     * 发布栏目页
     */
    void generateColumn();

    /**
     * 发布门户页
     */
    void generatePortal();
}
