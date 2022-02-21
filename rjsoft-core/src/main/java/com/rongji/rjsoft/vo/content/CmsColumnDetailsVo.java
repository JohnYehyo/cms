package com.rongji.rjsoft.vo.content;

import com.rongji.rjsoft.vo.common.SysCommonFileVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 栏目详情
 * @author: JohnYehyo
 * @create: 2021-10-20 16:14:38
 */
@Data
@ApiModel
public class CmsColumnDetailsVo {

    @ApiModelProperty(value = "附件信息")
    private List<SysCommonFileVo> files;

    /**
     * 站点模板id
     */
    @ApiModelProperty(value = "站点模板id")
    private Long siteTemplate;

    /**
     * 列表模板id
     */
    @ApiModelProperty(value = "列表模板id")
    private Long listTemplate;

    /**
     * 文章模板id
     */
    @ApiModelProperty(value = "文章模板id")
    private Long articleTemplate;
}
