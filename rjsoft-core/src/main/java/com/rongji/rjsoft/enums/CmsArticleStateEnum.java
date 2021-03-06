package com.rongji.rjsoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 文章状态
 * @author: JohnYehyo
 * @create: 2021-09-22 15:49:48
 */
@Getter
@AllArgsConstructor
public enum CmsArticleStateEnum {

    DRAFT(0, "待提交"),
    TO_AUDIT(1, "待审核"),
    NO_PASS(2, "审核不通过"),
    ENABLE(3, "已发布"),
    DISABLE(4, "禁用");

    private Integer state;

    private String notes;

}
