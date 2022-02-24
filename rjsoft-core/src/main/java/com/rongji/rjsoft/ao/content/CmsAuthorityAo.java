package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 授权参数
 * @author: JohnYehyo
 * @create: 2021-10-26 14:58:27
 */
@Data
@ApiModel(value = "站点栏目部门关系入参")
public class CmsAuthorityAo {

    /**
     * 站点或栏目id
     */
    @ApiModelProperty(value = "站点或栏目id", required = true)
    @NotNull(message = "站点或栏目不能为空")
    private String id;

    /**
     * 类型 0 站点 1 栏目
     */
    @ApiModelProperty(value = "类型", required = true)
    @NotNull(message = "类型不能为空")
    private int type;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id集合", required = true)
    @NotNull(message = "部门不能为空")
    private Long[] deptId;
}