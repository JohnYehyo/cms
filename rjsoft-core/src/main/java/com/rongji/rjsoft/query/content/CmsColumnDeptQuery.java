package com.rongji.rjsoft.query.content;

import com.rongji.rjsoft.query.common.PageQuery;
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
public class CmsColumnDeptQuery extends PageQuery {

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