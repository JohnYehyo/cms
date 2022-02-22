package com.rongji.rjsoft.query.content;

import com.rongji.rjsoft.query.common.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 站点部门关系查询对象
 * @author: JohnYehyo
 * @create: 2021-10-26 16:40:34
 */
@ApiModel(value ="栏目部门关系查询对象")
@Data
public class CmsSiteDeptQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = -2110814399053540159L;

    /**
     * 站点
     */
    @ApiModelProperty(value = "站点id")
    private Long siteId;

    /**
     * 栏目
     */
    @ApiModelProperty(value = "栏目id")
    private Long columnId;

    /**
     * 部门名
     */
    @ApiModelProperty(value = "部门id")
    private Long deptId;
}