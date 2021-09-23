package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 标签信息分页视图
 * @author: JohnYehyo
 * @create: 2021-09-18 09:20:04
 */
@Data
public class CmsTagsVo {

    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id")
    private Long tagId;

    /**
     * 标签名
     */
    @ApiModelProperty(value = "标签名")
    private String tagName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
}
