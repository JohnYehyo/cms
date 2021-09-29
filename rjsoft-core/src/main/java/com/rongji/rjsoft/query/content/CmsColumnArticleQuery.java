package com.rongji.rjsoft.query.content;

import com.rongji.rjsoft.query.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description: 栏目文章查询对象
 * @author: JohnYehyo
 * @create: 2021-09-15 18:25:16
 */
@Data
public class CmsColumnArticleQuery extends PageQuery {

    /**
     * 栏目ID
     */
    @ApiModelProperty(value = "栏目ID", required = true)
    @NotBlank(message = "栏目Id不能为空")
    private Long columnId;


    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点ID", required = true)
    @NotBlank(message = "站点Id不能为空")
    private Long siteId;

}
