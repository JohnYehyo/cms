package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 文章删除
 * @author: JohnYehyo
 * @create: 2021-09-26 15:24:04
 */
@Data
public class CmsArticleDeleteAo {

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点ID", required = true)
    @NotNull(message = "站点不能为空")
    private Long siteId;

    /**
     * 栏目ID
     */
    @ApiModelProperty(value = "栏目ID", required = true)
    @NotNull(message = "栏目不能为空")
    private Long columnId;

    /**
     * 文章ID
     */
    @ApiModelProperty(value = "文章ID", required = true)
    @NotNull(message = "文章不能为空")
    private Long articleId;

}
