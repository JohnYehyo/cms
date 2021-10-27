package com.rongji.rjsoft.entity.content;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *  最终文章表
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsFinalArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 栏目id
     */
    private Long columnId;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 伪删除标记
     */
    private int delFlag;

    /**
     * 发布标记
     */
    private int published;

    /**
     * 是否原创 0: 是 1:否
     */
    private int original;

}
