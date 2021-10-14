package com.rongji.rjsoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 来源
 * @author: JohnYehyo
 * @create: 2021-10-14 10:24:59
 */
@Getter
@AllArgsConstructor
public enum CmsOriginalEnum {

    ORIGINAL(0, "原创"),

    FORWARDING(1, "转发");


    private final int code;
    private final String content;
}
