package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.CmsAuthorityAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsAuthorityQuery;
import com.rongji.rjsoft.service.ICmsAuthorityService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 *  站点栏目授权 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-24
 */
@Api(tags = "CMS-站点栏目授权")
@RestController
@RequestMapping(value = "cmsAuthority")
@AllArgsConstructor
public class CmsAuthorityController {

    private final ICmsAuthorityService cmsAuthorityService;

    /**
     * 授权
     * @param cmsAuthorityAo 授权关系参数体
     * @return 授权结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "站点栏目授权部门")
    @PostMapping
    @LogAction(module = "站点栏目授权", method = "授权", logType = LogTypeEnum.AUTHORIZATION, operatorType = OperatorTypeEnum.WEB)
    public Object authorization(@Valid @RequestBody CmsAuthorityAo cmsAuthorityAo){
        if(cmsAuthorityService.authorization(cmsAuthorityAo)){
            return ResponseVo.success("授权成功");
        }
        return ResponseVo.error("授权失败");
    }

    /**
     * 授权关系分页查询
     * @param cmsAuthorityQuery 授权分页查询对象
     * @return 分页结果
     */
    @ApiOperation(value = "授权关系分页查询")
    @GetMapping(value ="page")
    public Object page(CmsAuthorityQuery cmsAuthorityQuery){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsAuthorityService.getPage(cmsAuthorityQuery));
    }

    /**
     * 删除授权
     * @param cmsAuthorityAo 授权参数体
     * @return 删除结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "删除授权")
    @DeleteMapping
    @LogAction(module = "站点栏目授权", method = "删除授权", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object delete(@Valid @RequestBody CmsAuthorityAo cmsAuthorityAo){
        if(cmsAuthorityService.delete(cmsAuthorityAo)){
            return ResponseVo.success("删除成功");
        }
        return ResponseVo.error("删除失败");
    }

}
