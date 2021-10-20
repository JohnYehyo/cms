package com.rongji.rjsoft.vo.content;

import com.rongji.rjsoft.vo.system.dept.SysDeptTreeVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 栏目视图
 * @author: JohnYehyo
 * @create: 2021-09-15 18:55:00
 */
@Data
public class CmsColumnVo implements Serializable {

    private static final long serialVersionUID = 2953467267863632596L;

    /**
     * 栏目ID
     */
    @ApiModelProperty(value = "栏目id")
    private Long columnId;

    /**
     * 栏目名称
     */
    @ApiModelProperty(value = "栏目名称")
    private String columnName;

    /**
     * 父级栏目
     */
    @ApiModelProperty(value = "父级栏目")
    private String parentName;

    /**
     * 栏目图片地址
     */
    @ApiModelProperty(value = "栏目图片地址")
    private String imageUrl;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    private String deptId;

    /**
     * 部门
     */
    @ApiModelProperty(value = "部门")
    private List<SysDeptTreeVo> depts;

}
