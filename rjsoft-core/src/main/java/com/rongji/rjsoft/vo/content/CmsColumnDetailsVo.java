package com.rongji.rjsoft.vo.content;

import com.rongji.rjsoft.vo.common.SysCommonFileVo;
import lombok.Data;

import java.util.List;

/**
 * @description: 栏目详情
 * @author: JohnYehyo
 * @create: 2021-10-20 16:14:38
 */
@Data
public class CmsColumnDetailsVo {

    private List<CmsSiteTreeVo> sites;

    private List<SysCommonFileVo> files;
}
