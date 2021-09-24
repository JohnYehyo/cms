package com.rongji.rjsoft.ao.common;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description: 文件上传
 * @author: JohnYehyo
 * @create: 2021-09-24 13:04:35
 */
@Data
public class FileUploadAo {

    /**
     *业务类型
     */
    @NotBlank(message = "业务类型不能为空")
    private String businessType;


    /**
     *oss附件对象Id
     */
    @NotBlank(message = "oss附件对象Id不能为空")
    private String fileName;
}
