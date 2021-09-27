package com.rongji.rjsoft.query.content;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rongji.rjsoft.query.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @description: 敏感词查询
 * @author: JohnYehyo
 * @create: 2021-09-26 16:51:18
 */
@Data
public class CmsSensitiveWordsQuery extends PageQuery {

    /**
     * 敏感词
     */
    @ApiModelProperty(value = "敏感词")
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
