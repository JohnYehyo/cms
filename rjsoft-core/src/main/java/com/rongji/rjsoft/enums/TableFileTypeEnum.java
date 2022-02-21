package com.rongji.rjsoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 附件类型
 * @author: JohnYehyo
 * @create: 2021-10-19 09:39:19
 */
@Getter
@AllArgsConstructor
public enum TableFileTypeEnum {

    TEMPLATE_SITE(0, "站点模板"),
    TEMPLATE_COLUMN(1, "列表模板"),
    TEMPLATE_ARTICLE(2, "文章模板");


    private final int code;
    private final String value;
}
