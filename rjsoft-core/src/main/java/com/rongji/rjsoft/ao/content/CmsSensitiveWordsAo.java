package com.rongji.rjsoft.ao.content;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @description: 敏感词表单
 * @author: JohnYehyo
 * @create: 2021-09-26 16:45:49
 */
@Data
public class CmsSensitiveWordsAo {

    public interface insert {

    }

    public interface update {

    }


    /**
     * 敏感词ID
     */
    @ApiModelProperty(value = "敏感词ID")
    @NotNull(
            groups = {CmsSensitiveWordsAo.update.class},
            message = "敏感词ID不能为空"
    )
    private Long wordId;

    /**
     * 敏感词
     */
    @ApiModelProperty(value = "敏感词", required = true)
    @NotBlank(groups = {CmsSensitiveWordsAo.insert.class, CmsSensitiveWordsAo.update.class}, message = "敏感词不能为空")
    private String word;

    /**
     * 开始生效时间
     */
    @ApiModelProperty(value = "开始生效时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束生效时间
     */
    @ApiModelProperty(value = "结束生效时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
