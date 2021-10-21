package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @description: 栏目表单
 * @author: JohnYehyo
 * @create: 2021-09-15 18:07:11
 */
@Data
public class CmsColumnAo {

    public interface insert {

    }

    public interface update {

    }

    /**
     * 栏目ID
     */
    @ApiModelProperty(value = "栏目ID")
    @NotNull(
            groups = {CmsColumnAo.update.class},
            message = "栏目ID不能为空"
    )
    private Long columnId;

    /**
     * 栏目名称
     */
    @ApiModelProperty(value = "栏目名称", required = true)
    @NotEmpty(
            groups = {CmsColumnAo.insert.class, CmsColumnAo.update.class},
            message = "栏目名称不能为空"
    )
    private String columnName;

    /**
     * 栏目父级id
     */
    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    /**
     * 栏目图片地址
     */
    @ApiModelProperty(value = "栏目图片地址")
    private String imageUrl;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id", required = true)
    @NotNull(
            groups = {CmsColumnAo.insert.class, CmsColumnAo.update.class},
            message = "部门不能为空"
    )
    private Long[] deptIds;

    /**
     * 模板id
     */
    @ApiModelProperty(value = "模板id", required = true)
    @NotNull(
            groups = {CmsColumnAo.insert.class, CmsColumnAo.update.class},
            message = "模板不能为空"
    )
    private Long tempId;

}
