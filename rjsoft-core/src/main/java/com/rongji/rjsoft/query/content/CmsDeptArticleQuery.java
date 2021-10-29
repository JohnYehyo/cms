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
public class CmsDeptArticleQuery extends PageQuery {

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点ID", required = true)
    @NotBlank(message = "站点Id不能为空")
    private Long siteId;

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "部门ID", required = true)
    @NotBlank(message = "部门不能为空")
    private Long deptId;

}
