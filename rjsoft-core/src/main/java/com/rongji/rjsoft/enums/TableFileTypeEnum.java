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

    TEMPLATE_IMG(0, "模板图片"),
    TEMPLATE_HTML_ARTICLE(1, "文章模板文件"),
    TEMPLATE_HTML_COLUMN(2, "栏目模板文件");


    private final int code;
    private final String value;
}
