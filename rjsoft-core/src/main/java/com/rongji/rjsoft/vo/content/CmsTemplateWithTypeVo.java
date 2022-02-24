package com.rongji.rjsoft.vo.content;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 模板
 * @author: JohnYehyo
 * @create: 2022-02-23 20:41:58
 */
@Data
public class CmsTemplateWithTypeVo implements Serializable {

    private static final long serialVersionUID = -2827392545184060081L;

    /**
     * 站点模板
     */
    private Long siteTemplate;

    /**
     * 列表模板
     */
    private Long listTemplate;

    /**
     * 文章模板
     */
    private Long articleTemplate;
}
