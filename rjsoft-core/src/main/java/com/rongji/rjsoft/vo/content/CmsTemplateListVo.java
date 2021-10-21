package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 模板列表视图
 * @author: JohnYehyo
 * @create: 2021-10-18 15:48:15
 */
@ApiModel(value = "模板列表视图")
@Data
public class CmsTemplateListVo implements Serializable {

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板图片
     */
    private String templateImg;

}
