package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.service.ICmsColumnDeptService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 栏目部门表 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-26
 */
@Api(tags = "CMS-栏目部门维护")
@RestController
@RequestMapping("/cmsColumnDept")
@AllArgsConstructor
public class CmsColumnDeptController {

    private final ICmsColumnDeptService cmsColumnDeptService;

    /**
     * 添加栏目部门关系
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 添加结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "添加栏目部门关系")
    @PostMapping
    @LogAction(module = "栏目部门维护", method = "添加栏目部门关系", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object add(@Valid @RequestBody CmsColumnDeptAo cmsColumnDeptAo){
        if(cmsColumnDeptService.add(cmsColumnDeptAo)){
            return ResponseVo.success("添加成功");
        }
        return ResponseVo.error("添加失败");
    }

    /**
     * 更新栏目部门关系
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 更新结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "编辑栏目部门关系")
    @PutMapping
    @LogAction(module = "栏目部门维护", method = "编辑栏目部门关系", logType = LogTypeEnum.UPDATE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@Valid @RequestBody CmsColumnDeptAo cmsColumnDeptAo){
        if(cmsColumnDeptService.edit(cmsColumnDeptAo)){
            return ResponseVo.success("编辑成功");
        }
        return ResponseVo.error("编辑失败");
    }

    /**
     * 删除栏目部门关系
     * @param columnIds 栏目id几个
     * @return 删除结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "删除栏目部门关系")
    @ApiImplicitParam(name = "columnIds", value = "栏目id集合", required = true)
    @PutMapping("/{columnIds}")
    @LogAction(module = "栏目部门维护", method = "删除栏目部门关系", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@PathVariable Long[] columnIds){
        if(cmsColumnDeptService.delete(columnIds)){
            return ResponseVo.success("删除成功");
        }
        return ResponseVo.error("删除失败");
    }

    /**
     * 栏目部门关系分页查询
     * @param cmsColumnDeptQuery 栏目部门关系分页查询对象
     * @return 分页结果
     */
    @ApiOperation(value = "栏目部门关系分页查询")
    @GetMapping(value ="page")
    public Object page(CmsColumnDeptQuery cmsColumnDeptQuery){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsColumnDeptService.getPage(cmsColumnDeptQuery));
    }

}
