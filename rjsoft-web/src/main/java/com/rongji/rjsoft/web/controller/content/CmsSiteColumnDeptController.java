package com.rongji.rjsoft.web.controller.content;

import com.rongji.rjsoft.ao.content.CmsSiteColumnDeptAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.service.ICmsSiteColumnDeptService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 站点栏目部门管理关系 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-22
 */
@Api(tags = "CMS-站点栏目部门授权维护")
@RestController
@RequestMapping("/siteColumnDept")
@AllArgsConstructor
public class CmsSiteColumnDeptController {
    
    private final ICmsSiteColumnDeptService cmsSiteColumnDeptService;

    /**
     * 站点栏目授权部门
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 授权结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "站点栏目授权部门")
    @PostMapping
    @LogAction(module = "站点栏目部门授权维护", method = "站点栏目授权部门", logType = LogTypeEnum.AUTHORIZATION, operatorType = OperatorTypeEnum.WEB)
    public Object authorization(@Valid @RequestBody CmsSiteColumnDeptAo cmsSiteColumnDeptAo){
        if(cmsSiteColumnDeptService.authorization(cmsSiteColumnDeptAo)){
            return ResponseVo.success("授权成功");
        }
        return ResponseVo.error("授权失败");
    }

    /**
     * 站点栏目部门关系分页查询
     * @param cmsSiteColumnQuery 站点栏目部门关系分页查询对象
     * @return 分页结果
     */
    @ApiOperation(value = "站点栏目部门关系分页查询")
    @GetMapping(value ="page")
    public Object page(CmsSiteColumnQuery cmsSiteColumnQuery){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsSiteColumnDeptService.getPage(cmsSiteColumnQuery));
    }

    /**
     * 删除站点栏目对部门的授权
     * @param cmsSiteColumnDeptAo 站点栏目部门关系参数体
     * @return 删除结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "站点栏目授权部门")
    @DeleteMapping
    @LogAction(module = "站点栏目部门授权维护", method = "删除站点栏目对部门的授权", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object delete(@Valid @RequestBody CmsSiteColumnDeptAo cmsSiteColumnDeptAo){
        if(cmsSiteColumnDeptService.delete(cmsSiteColumnDeptAo)){
            return ResponseVo.success("删除成功");
        }
        return ResponseVo.error("删除失败");
    }


}
