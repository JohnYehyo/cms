package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 栏目部门关系参数
 * @author: JohnYehyo
 * @create: 2021-10-26 14:58:27
 */
@Data
@ApiModel(value = "栏目部门关系入参")
public class CmsColumnDeptAo {

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id", required = true)
    @NotNull(message = "栏目不能为空")
    private Long columnId;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id集合", required = true)
    @NotNull(message = "部门不能为空")
    private Long[] deptId;
}