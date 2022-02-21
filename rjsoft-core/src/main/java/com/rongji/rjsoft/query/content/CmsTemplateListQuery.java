package com.rongji.rjsoft.query.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 模板列表条件
 * @author: JohnYehyo
 * @create: 2021-10-18 15:44:15
 */
@Data
@ApiModel(value = "模板查询条件")
public class CmsTemplateListQuery {

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板类型")
    private String type;

    @ApiModelProperty(value = "附件业务分类")
    private String businessType;

}
