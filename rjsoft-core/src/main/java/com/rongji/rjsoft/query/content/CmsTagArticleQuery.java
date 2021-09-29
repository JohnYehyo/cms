package com.rongji.rjsoft.query.content;

import com.rongji.rjsoft.query.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description: 标签文章查询对象
 * @author: JohnYehyo
 * @create: 2021-09-15 18:25:16
 */
@Data
public class CmsTagArticleQuery extends PageQuery {

    /**
     * 标签ID
     */
    @ApiModelProperty(value = "标签ID", required = true)
    @NotBlank(message = "标签Id不能为空")
    private Long tagId;


    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点ID", required = true)
    @NotBlank(message = "站点Id不能为空")
    private Long siteId;

}
