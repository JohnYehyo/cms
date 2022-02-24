package com.rongji.rjsoft.entity.content;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 栏目id
     */
    private Long columnId;

    /**
     * 部门id
     */
    private Long deptId;

}
