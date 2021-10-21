package com.rongji.rjsoft.vo.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 站点详情视图
 * @author: JohnYehyo
 * @create: 2021-09-15 18:55:00
 */
@Data
@ApiModel(value = "站点详情视图")
public class CmsSiteDetailsVo extends CmsSiteVo implements Serializable {

    private static final long serialVersionUID = 2953467267863632596L;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id")
    private List<Long> columnIds;

}
