package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 栏目部门关系视图
 * @author: JohnYehyo
 * @create: 2021-10-26 16:26:15
 */
@Data
@ApiModel(value = "栏目部门关系视图")
public class CmsColumnDeptVo implements Serializable {

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id")
    private Long columnId;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    private Long deptId;

    /**
     * 栏目名称
     */
    @ApiModelProperty(value = "栏目名称")
    private String columnName;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;


}