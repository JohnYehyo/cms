package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 站点栏目关系表单
 * @author: JohnYehyo
 * @create: 2021-09-15 18:07:11
 */
@Data
public class CmsSiteColumnAo {

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点ID")
    @NotNull(message = "站点ID不能为空")
    private Long siteId;

    /**
     * 栏目
     */
    @ApiModelProperty(value = "栏目", required = true)
    @NotNull(message = "栏目不能为空")
    private Long[] columnId;

}
