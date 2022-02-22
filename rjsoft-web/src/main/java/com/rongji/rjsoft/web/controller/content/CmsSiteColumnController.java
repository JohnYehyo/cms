package com.rongji.rjsoft.web.controller.content;

import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.service.ICmsSiteColumnService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 站点栏目
 * @author: JohnYehyo
 * @create: 2022-02-21 17:04:09
 */
@Api(tags = "CMS-站点栏目")
@RequestMapping(value = "siteColumn")
@RestController
@AllArgsConstructor
public class CmsSiteColumnController {

    private final ICmsSiteColumnService cmsSiteColumnService;


    /**
     * 站点栏目树
     * @return 站点树
     */
    @ApiOperation(value = "站点栏目树")
    @GetMapping(value = "tree")
    public ResponseVo tree(String siteColumnId){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsSiteColumnService.tree(siteColumnId));
    }
}
