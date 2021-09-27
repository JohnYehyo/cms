package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 文章类别树视图
 * @author: JohnYehyo
 * @create: 2021-09-16 14:06:07
 */
@Data
public class CmsCategorySimpleVo implements Serializable {

    private static final long serialVersionUID = -136941801905312782L;

    /**
     * 类别id
     */
    @ApiModelProperty(value = "类别id")
    private Long categoryId;

    /**
     * 类型名
     */
    @ApiModelProperty(value = "类型名")
    private String categoryName;

}
