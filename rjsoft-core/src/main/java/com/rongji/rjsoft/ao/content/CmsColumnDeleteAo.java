package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 删除栏目参数
 * @author: JohnYehyo
 * @create: 2021-10-26 14:16:55
 */
@Data
@ApiModel(value = "删除栏目入参")
public class CmsColumnDeleteAo {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    @NotNull(message = "站点不能为空")
    private Long siteId;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id集合")
    @NotNull(message = "栏目不能为空")
    private Long[] columnIds;
}