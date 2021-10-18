package com.rongji.rjsoft.query.content;

import com.rongji.rjsoft.query.common.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 模板查询条件
 * @author: JohnYehyo
 * @create: 2021-10-18 15:44:15
 */
@Data
@ApiModel(value = "模板查询条件")
public class CmsTemplateQuery extends PageQuery {

    @ApiModelProperty(value = "模板名称")
    private String templateName;

}
