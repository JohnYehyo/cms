package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @description: 站点表单
 * @author: JohnYehyo
 * @create: 2021-09-15 18:07:11
 */
@Data
public class CmsSiteAo {

    public interface insert {

    }

    public interface update {

    }

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点ID")
    @NotNull(
            groups = {CmsSiteAo.update.class},
            message = "站点ID不能为空"
    )
    private Long siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称", required = true)
    @NotEmpty(groups = {CmsSiteAo.insert.class, CmsSiteAo.update.class}, message = "站点名称不能为空")
    private String siteName;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String description;

    /**
     * 站点父级id
     */
    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    /**
     * 站点地址
     */
    @ApiModelProperty(value = "站点地址")
    private String siteUrl;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id", required = true)
    @NotNull(message = "部门不能为空")
    private Long deptId;

    /**
     * 站点模板id
     */
    @ApiModelProperty(value = "站点模板")
    private Long siteTemplate;

    /**
     * 列表模板id
     */
    @ApiModelProperty(value = "列表模板")
    private Long listTemplate;

    /**
     * 文章模板id
     */
    @ApiModelProperty(value = "文章模板")
    private Long articleTemplate;

}
