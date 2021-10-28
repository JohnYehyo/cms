package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.CmsTemplateAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsTemplateListQuery;
import com.rongji.rjsoft.query.content.CmsTemplateQuery;
import com.rongji.rjsoft.service.ICmsTemplateService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 模板表 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-18
 */
@Api(tags = "CMS-模板管理")
@RestController
@RequestMapping("/cmsTemplate")
public class CmsTemplateController {

    @Autowired
    private ICmsTemplateService cmsTemplateService;

    /**
     * 栏目模板存储目录
     */
    @Value("${file.template.column}")
    private String columnTemplate;

    /**
     * 文章模板存储目录
     */
    @Value("${file.template.article}")
    private String articleTemplate;

    /**
     * 添加模板
     *
     * @param cmsTemplateAo 模板表单参数
     * @return 添加结果
     */
    @ApiOperation(value = "添加模板")
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @PostMapping
    @LogAction(module = "模板管理", method = "添加模板", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object add(@Validated(CmsTemplateAo.add.class) @RequestBody CmsTemplateAo cmsTemplateAo) {
        if (cmsTemplateService.add(cmsTemplateAo)) {
            return ResponseVo.success("添加模板成功");
        }
        return ResponseVo.error("添加模板失败");
    }

    /**
     * 编辑模板
     *
     * @param cmsTemplateAo 模板表单参数
     * @return 编辑结果
     */
    @ApiOperation(value = "编辑模板")
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @PutMapping
    @LogAction(module = "模板管理", method = "更新模板", logType = LogTypeEnum.UPDATE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@Validated(CmsTemplateAo.update.class) @RequestBody CmsTemplateAo cmsTemplateAo) {
        if (cmsTemplateService.edit(cmsTemplateAo)) {
            return ResponseVo.success("更新模板成功");
        }
        return ResponseVo.error("更新模板失败");
    }

    /**
     * 删除模板
     *
     * @param templateId 模板id
     * @return 删除结果
     */
    @ApiOperation(value = "删除模板")
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @DeleteMapping(value = "/{templateId}")
    @ApiImplicitParam(name = "templateId", value = "模板id", required = true)
    @LogAction(module = "模板管理", method = "删除模板", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object delete(@PathVariable Long templateId) {
        if (cmsTemplateService.delete(templateId)) {
            return ResponseVo.success("删除模板成功");
        }
        return ResponseVo.error("删除模板失败");
    }

    /**
     * 模板分页查询
     *
     * @param cmsTemplateQuery 模板分页查询条件
     * @return 模板分页结果
     */
    @ApiOperation(value = "模板分页查询")
    @GetMapping(value = "page")
    public Object page(CmsTemplateQuery cmsTemplateQuery) {
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsTemplateService.getPage(cmsTemplateQuery));
    }

    /**
     * 模板列表
     *
     * @param cmsTemplateListQuery 模板列表查询条件
     * @return 模板列表
     */
    @ApiOperation(value = "模板列表查询")
    @GetMapping(value = "list")
    public Object list(CmsTemplateListQuery cmsTemplateListQuery) {
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsTemplateService.listOfTemplate(cmsTemplateListQuery));
    }

}
