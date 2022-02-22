package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 站点栏目树
 * @author: JohnYehyo
 * @create: 2022-02-21 18:02:49
 */
@Data
public class CmsSiteColumnTreeVo implements Serializable {

    private static final long serialVersionUID = -3993590747215760693L;

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点id")
    private String id;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private String parentId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String name;

    /**
     * 是否叶子节点
     */
    @ApiModelProperty(value = "是否叶子节点")
    private boolean parentNode;
}
