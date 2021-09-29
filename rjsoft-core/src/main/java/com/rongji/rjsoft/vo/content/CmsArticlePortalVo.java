package com.rongji.rjsoft.vo.content;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 门户文章列表
 * @author: JohnYehyo
 * @create: 2021-09-28 15:01:48
 */
@Data
public class CmsArticlePortalVo {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 文章创作时间
     */
    private LocalDateTime createTime;
}
