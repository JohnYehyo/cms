package com.rongji.rjsoft.vo.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rongji.rjsoft.vo.common.FileVo;
import io.swagger.annotations.ApiModel;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 模板分页视图
 * @author: JohnYehyo
 * @create: 2021-10-18 15:48:15
 */
@ApiModel(value = "模板分页视图")
@Data
public class CmsTemplateVo implements Serializable {

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

    /**
     * 模板
     */
    private FileVo template;

    /**
     * 模板类型
     */
    private int type;

    /**
     * 附件路径
     */
    @JsonIgnore
    private String fileUrl;

    /**
     * 附件名字
     */
    @JsonIgnore
    private String fileName;

}
