package com.rongji.rjsoft.entity.content;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 栏目部门表
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsColumnDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 栏目id
     */
    private Long columnId;

    /**
     * 部门id
     */
    private Long deptId;


}
