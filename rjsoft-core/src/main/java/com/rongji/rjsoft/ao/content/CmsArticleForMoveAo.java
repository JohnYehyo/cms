package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 移动文章
 * @author: JohnYehyo
 * @create: 2021-10-14 10:03:31
 */
@Data
@ApiModel(value = "移动文章传参")
public class CmsArticleForMoveAo extends CmsArticleForWardingAo{

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    @NotNull(message = "站点ID不能为空")
    private Long siteId;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id")
    @NotNull(message = "栏目ID不能为空")
    private Long columnId;

}
