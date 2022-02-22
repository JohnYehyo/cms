package com.rongji.rjsoft.entity.content;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 栏目部门管理关系表
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsSiteDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 栏目id
     */
    private Long siteId;

    /**
     * 部门id
     */
    private Long deptId;


}
