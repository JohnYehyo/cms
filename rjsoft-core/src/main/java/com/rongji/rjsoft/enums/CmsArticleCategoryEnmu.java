package com.rongji.rjsoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 文章类型枚举
 * @author: JohnYehyo
 * @create: 2021-11-24 09:41:22
 */
@Getter
@AllArgsConstructor
public enum CmsArticleCategoryEnmu {

    WORD(1L, "<p", "文字"),
    PICTURE(2L, "<img", "图片"),
    VIDEO(3L, "<video", "视频"),
    AUDIO(4L, "<audio", "音频"),
    APPENDIX(5L, "", "附件"),
    MULTIMEDIA(6L, "", "多媒体");

    /**
     * 值
     */
    private Long code;

    /**
     * 特征
     */
    private String Signature;

    /**
     * 描述
     */
    private String description;
}
