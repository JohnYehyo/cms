package com.rongji.rjsoft.ao.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description: 附件参数
 * @author: JohnYehyo
 * @create: 2021-09-24 14:52:28
 */
@Data
public class FileAo {

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名", required = true)
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    /**
     * 文件存储地址
     */
    @ApiModelProperty(value = "文件路径", required = true)
    @NotBlank(message = "文件路径不能为空")
    private String fileUrl;
}
