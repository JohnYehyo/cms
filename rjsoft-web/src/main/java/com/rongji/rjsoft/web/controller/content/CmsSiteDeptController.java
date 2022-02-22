package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsSiteDeptQuery;
import com.rongji.rjsoft.service.ICmsSiteDeptService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 站点部门管理关系表 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-22
 */
@Api(tags = "CMS-站点部门维护")
@Controller
@RequestMapping("/cmsSiteDept")
@AllArgsConstructor
public class CmsSiteDeptController {
    
    private final ICmsSiteDeptService cmsSiteDeptService;

    /**
     * 添加站点部门关系
     * @param cmsSiteDeptAo 站点部门关系参数体
     * @return 添加结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "添加站点部门关系")
    @PostMapping
    @LogAction(module = "站点部门维护", method = "添加站点部门关系", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object add(@Valid @RequestBody CmsSiteDeptAo cmsSiteDeptAo){
        if(cmsSiteDeptService.add(cmsSiteDeptAo)){
            return ResponseVo.success("添加成功");
        }
        return ResponseVo.error("添加失败");
    }

    /**
     * 更新站点部门关系
     * @param cmsSiteDeptAo 站点部门关系参数体
     * @return 更新结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "编辑站点部门关系")
    @PutMapping
    @LogAction(module = "站点部门维护", method = "编辑站点部门关系", logType = LogTypeEnum.UPDATE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@Valid @RequestBody CmsSiteDeptAo cmsSiteDeptAo){
        if(cmsSiteDeptService.edit(cmsSiteDeptAo)){
            return ResponseVo.success("编辑成功");
        }
        return ResponseVo.error("编辑失败");
    }

    /**
     * 删除站点部门关系
     * @param siteIds 站点id集合
     * @return 删除结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "删除站点部门关系")
    @ApiImplicitParam(name = "siteIds", value = "站点id集合", required = true)
    @DeleteMapping("/{siteIds}")
    @LogAction(module = "站点部门维护", method = "删除站点部门关系", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@PathVariable Long[] siteIds){
        if(cmsSiteDeptService.delete(siteIds)){
            return ResponseVo.success("删除成功");
        }
        return ResponseVo.error("删除失败");
    }

    /**
     * 站点部门关系分页查询
     * @param cmsSiteDeptQuery 站点部门关系分页查询对象
     * @return 分页结果
     */
    @ApiOperation(value = "站点部门关系分页查询")
    @GetMapping(value ="page")
    public Object page(CmsSiteDeptQuery cmsSiteDeptQuery){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsSiteDeptService.getPage(cmsSiteDeptQuery));
    }

    /**
     * 通过站点获取部门同步树
     * @param siteId 站点id
     * @return 部门信息
     */
    @ApiOperation(value = "通过站点获取部门同步树")
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    @GetMapping(value = "allTree/{siteId}")
    public Object allTree(@PathVariable Long siteId){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsSiteDeptService.allDeptTree(siteId));
    }

}
