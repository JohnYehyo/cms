package com.rongji.rjsoft.ao.content;

import lombok.Data;

/**
 * @description: 删除栏目参数
 * @author: JohnYehyo
 * @create: 2021-10-26 14:16:55
 */
@Data
public class CmsColumnDeleteAo {

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 栏目id
     */
    private Long[] columnIds;
}