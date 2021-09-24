package com.rongji.rjsoft.entity.content;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 站点栏目关系表
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsSiteColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 栏目id
     */
    private Long columnId;


}
