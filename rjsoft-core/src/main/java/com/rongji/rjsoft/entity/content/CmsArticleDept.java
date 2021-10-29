package com.rongji.rjsoft.entity.content;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章部门权限表
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsArticleDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 部门id
     */
    private Long deptId;


}
