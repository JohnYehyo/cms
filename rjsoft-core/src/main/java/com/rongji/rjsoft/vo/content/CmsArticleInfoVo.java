package com.rongji.rjsoft.vo.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rongji.rjsoft.vo.common.FileVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: JohnYehyo
 * @create: 2021-09-22 17:54:05
 */
@Data
public class CmsArticleInfoVo extends CmsArticleVo implements Serializable {

    private static final long serialVersionUID = 3311777979233516292L;

    /**
     * 文章专属二维码地址
     */
    private String qrcodePath;

    /**
     * 附件地址
     */
    @JsonIgnore
    private String files;

    /**
     * 附件地址
     */
    private List<FileVo> file;

    /**
     * 轮播图地址
     */
    private String sliderImg;

    /**
     * 文章简介，最多200字
     */
    private String description;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 文章引用情况
     */
    private List<CmsArticleRefVo> siteColumns;

}
