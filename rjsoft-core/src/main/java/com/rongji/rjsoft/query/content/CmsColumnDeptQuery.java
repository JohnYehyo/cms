package com.rongji.rjsoft.query.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 栏目部门关系查询对象
 * @author: JohnYehyo
 * @create: 2021-10-26 16:40:34
 */
@ApiModel(value ="栏目部门关系查询对象")
@Data
public class CmsColumnDeptQuery {

    /**
     * 栏目名
     */
    @ApiModelProperty(value = "栏目id")
    private Long columnId;

    /**
     * 部门名
     */
    @ApiModelProperty(value = "部门id")
    private Long deptId;
}