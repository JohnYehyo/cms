package com.rongji.rjsoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 发布类型
 * @author: JohnYehyo
 * @create: 2021-10-28 18:16:27
 */
@Getter
@AllArgsConstructor
public enum CmsArticlePublishTypeEnum {

    MANUAL(0, "手动实时发布"),
    AUTOMATIC(1, "定时发布");

    private int code;
    private String description;
}