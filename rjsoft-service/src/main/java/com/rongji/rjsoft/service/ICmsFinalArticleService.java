package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;

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
     * 生成门户
     * @return
     */
    boolean generate();

    /**
     * 生成文章
     * @return
     */
    void generateHtml();
}
