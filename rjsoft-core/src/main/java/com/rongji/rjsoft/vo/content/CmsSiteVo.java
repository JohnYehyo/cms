package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 站点视图
 * @author: JohnYehyo
 * @create: 2021-09-15 18:55:00
 */
@Data
public class CmsSiteVo implements Serializable {

    private static final long serialVersionUID = 2953467267863632596L;

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点id")
    private Long siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名")
    private String siteName;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String description;

    /**
     * 站点父级id
     */
    @ApiModelProperty(value = "父级站点id")
    private Long parentId;

    /**
     * 站点地址
     */
    @ApiModelProperty(value = "站点地址")
    private String siteUrl;

    /**
     * 站点目录
     */
    @ApiModelProperty(value = "站点目录")
    private String siteFile;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "deptId")
    private Long deptId;

}
