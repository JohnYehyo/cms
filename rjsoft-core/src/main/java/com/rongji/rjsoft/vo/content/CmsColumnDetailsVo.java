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

    @ApiModelProperty(value = "模板id")
    private Long templateId;
}
