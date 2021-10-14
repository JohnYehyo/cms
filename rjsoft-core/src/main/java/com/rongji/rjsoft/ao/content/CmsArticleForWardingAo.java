package com.rongji.rjsoft.ao.content;

import com.rongji.rjsoft.entity.content.CmsSiteColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 转发文章
 * @author: JohnYehyo
 * @create: 2021-10-14 10:03:31
 */
@Data
@ApiModel(value = "转发文章传参")
public class CmsArticleForWardingAo {

    /**
     * 文章ID
     */
    @ApiModelProperty(value = "文章ID")
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /**
     * 站点栏目
     */
    @ApiModelProperty(value = "站点栏目", required = true)
    @NotNull(message = "站点栏目不能为空")
    private List<CmsSiteColumn> list;

}
