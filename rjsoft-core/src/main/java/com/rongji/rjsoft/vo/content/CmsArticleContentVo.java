package com.rongji.rjsoft.vo.content;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 生成文章
 * @author: JohnYehyo
 * @create: 2021-09-30 10:51:17
 */
@Data
public class CmsArticleContentVo {

    /**
     * 文章Id
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章作者ID
     */
    private Long authorId;

    /**
     * 文章作者ID
     */
    private String authorName;

    /**
     * 来源
     */
    private String source;

    /**
     * 文章路径
     */
    private String articleUrl;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 站点ID
     */
    private Long siteId;

    /**
     * 站点目录
     */
    private String siteFile;

    /**
     * 文章栏目id
     */
    private String columnId;

    /**
     * 文章栏目
     */
    private String column;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 简介描述
     */
    private String description;

    /**
     * 文章创作时间
     */
    private LocalDateTime publishTime;

    /**
     * 附件地址
     */
    private List<String> files;

    /**
     * 模板id
     */
    private Long templateId;

}
