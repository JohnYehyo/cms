package com.rongji.rjsoft.query.content;

import com.rongji.rjsoft.query.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 站点栏目部门关系查询
 * @author: JohnYehyo
 * @create: 2022-02-22 21:27:29
 */
@Data
public class CmsSiteColumnQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 7175155095555797590L;

    /**
     * 组合id
     */
    @ApiModelProperty(value = "组合id")
    private String id;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private int type;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id", hidden = true)
    private Long siteId;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id", hidden = true)
    private Long columnId;
}
