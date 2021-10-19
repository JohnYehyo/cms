package com.rongji.rjsoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 表类型
 * @author: JohnYehyo
 * @create: 2021-10-19 09:16:49
 */
@Getter
@AllArgsConstructor
public enum TableTypeEnum {


    SYS_USER("SysUser"),
    CMS_TEMPLATE("CmsTemplate");

    private final String value;

}
