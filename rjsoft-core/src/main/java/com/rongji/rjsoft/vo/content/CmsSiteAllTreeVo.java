package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 站点同步树
 * @author: JohnYehyo
 * @create: 2021-09-15 19:30:49
 */
@Data
public class CmsSiteAllTreeVo implements Serializable {

    /**
     * 站点ID
     */
    @ApiModelProperty(value = "站点id")
    private Long siteId;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private Long parentId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 子集对象
     */
    @ApiModelProperty(value = "子集")
    private List<CmsSiteAllTreeVo> children;
}
