package com.rongji.rjsoft.vo.content;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 敏感词视图
 * @author: JohnYehyo
 * @create: 2021-09-26 16:51:18
 */
@Data
public class CmsSensitiveWordsVo {

    /**
     * 敏感词
     */
    private String word;

    /**
     * 开始生效时间
     */
    private LocalDateTime startTime;

    /**
     * 结束生效时间
     */
    private LocalDateTime endTime;

    /**
     * 伪删除
     */
    private int delFlag;

}
