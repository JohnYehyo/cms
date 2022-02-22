package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 站点栏目部门关系分页视图
 * @author: JohnYehyo
 * @create: 2022-02-22 21:32:37
 */
@Data
public class CmsSiteColumnDeptVo implements Serializable {

    private static final long serialVersionUID = -6418035684599630817L;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    private Long deptId;

    /**
     * 部门名字
     */
    @ApiModelProperty(value = "部门名字")
    private String deptName;
}
