package com.rongji.rjsoft.vo.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 站点部门关系视图
 * @author: JohnYehyo
 * @create: 2021-10-26 16:26:15
 */
@Data
@ApiModel(value = "站点部门关系视图")
public class CmsSiteDeptVo implements Serializable {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private Long siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 部门id
     */
    @JsonIgnore
    @ApiModelProperty(value = "部门id")
    private Long deptId;

    /**
     * 部门id集合
     */
    @ApiModelProperty(value = "部门id集合")
    private List<Long> deptIds;


}