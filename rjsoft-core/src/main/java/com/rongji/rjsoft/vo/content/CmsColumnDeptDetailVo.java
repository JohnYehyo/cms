package com.rongji.rjsoft.vo.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 栏目部门关系详情视图
 * @author: JohnYehyo
 * @create: 2021-10-26 16:26:15
 */
@Data
@ApiModel(value = "栏目部门关系视图")
public class CmsColumnDeptDetailVo implements Serializable {

    /**
     * 站点id
     */
    @JsonIgnore
    @ApiModelProperty(value = "站点id")
    private Long siteId;

    /**
     * 栏目id
     */
    @JsonIgnore
    @ApiModelProperty(value = "栏目id")
    private Long columnId;

    /**
     * 部门id集合
     */
    @ApiModelProperty(value = "部门id集合")
    private List<Long> deptId;

}