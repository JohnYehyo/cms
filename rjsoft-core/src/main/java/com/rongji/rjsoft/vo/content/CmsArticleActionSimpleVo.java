package com.rongji.rjsoft.vo.content;

import lombok.Data;

/**
 * @description: 文章互动情况简单视图
 * @author: JohnYehyo
 * @create: 2021-10-27 14:47:27
 */
@Data
public class CmsArticleActionSimpleVo {

    /**
     * 操作类型
     */
    private int type;

    /**
     * 操作次数
     */
    private long count;
}