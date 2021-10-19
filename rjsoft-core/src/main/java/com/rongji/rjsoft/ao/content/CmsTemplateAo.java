package com.rongji.rjsoft.ao.content;

import com.rongji.rjsoft.ao.system.FileAo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description: 模板入参
 * @author: JohnYehyo
 * @create: 2021-10-18 15:06:27
 */
@Data
@ApiModel(value = "模板入参")
public class CmsTemplateAo {

    public interface add{

    }

    public interface update{

    }

    /**
     * 模板id
     */
    @ApiModelProperty(value = "模板id")
    @NotNull(
            groups = {CmsTemplateAo.update.class},
            message = "模板id不能为空"
    )
    private Long templateId;

    /**
     * 模板名
     */
    @ApiModelProperty(value = "模板名")
    @NotBlank(
            groups = {CmsTemplateAo.add.class, CmsTemplateAo.update.class},
            message = "模板名不能为空"
    )
    private String templateName;

    /**
     * 模板缩略图
     */
    @ApiModelProperty(value = "模板缩略图")
    @NotNull(
            groups = {CmsTemplateAo.add.class, CmsTemplateAo.update.class},
            message = "模板缩略图不能为空"
    )
    private List<FileAo> templateImg;

    /**
     * 栏目模板
     */
    @ApiModelProperty(value = "栏目模板")
    @NotNull(
            groups = {CmsTemplateAo.add.class, CmsTemplateAo.update.class},
            message = "栏目模板不能为空"
    )
    private FileAo columnTemplate;

    /**
     * 文章模板
     */
    @ApiModelProperty(value = "文章模板")
    @NotNull(
            groups = {CmsTemplateAo.add.class, CmsTemplateAo.update.class},
            message = "文章模板不能为空"
    )
    private FileAo articleTemplate;


}
