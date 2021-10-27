package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 文章互动信息参数体
 * @author: JohnYehyo
 * @create: 2021-10-27 14:58:26
 */
@Data
@ApiModel(value = "文章互动信息参数体")
public class CmsArticleActionAo {

    /**
     * 互动类型
     */
    @ApiModelProperty(value = "互动类型", required = true)
    @NotNull(message = "缺少互动类型")
    private Integer type;

    /**
     * 文章id
     */
    @ApiModelProperty(value = "文章id", required = true)
    @NotNull(message = "文章id不能为空")
    private Long articleId;
}