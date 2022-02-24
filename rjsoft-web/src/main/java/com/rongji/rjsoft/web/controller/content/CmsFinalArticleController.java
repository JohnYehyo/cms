package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.*;
import com.rongji.rjsoft.service.ICmsArticleService;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.ResponseVo;
import com.rongji.rjsoft.vo.content.CmsArticlePortalVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 最终文章表 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
@Api(tags = "CMS-发布内容管理")
@Controller
@RequestMapping(value = "portal")
@AllArgsConstructor
public class CmsFinalArticleController {

    private final ICmsFinalArticleService cmsFinalArticleService;
    private final ICmsArticleService cmsArticleService;


    /**
     * 生成文章
     *
     * @return 响应
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "生成文章")
    @PostMapping(value = "generateArticle")
    @ResponseBody
    @LogAction(module = "门户管理", method = "生成文章", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public void generateArticle() {
        cmsFinalArticleService.generateArticle();
    }

    /**
     * 通过栏目获取文章列表
     *
     * @param cmsColumnArticleQuery 查询对象
     * @return 文章列表
     */
    @ApiOperation(value = "通过栏目获取文章列表")
    @GetMapping(value = "column")
    @ResponseBody
    public Object getArticlesByColumn(CmsColumnArticleQuery cmsColumnArticleQuery) {
        CommonPage<CmsArticlePortalVo> page = cmsArticleService.getArticlesByColumn(cmsColumnArticleQuery);
        return ResponseVo.response(ResponseEnum.SUCCESS, page);
    }

    /**
     * 通过标签获取文章列表
     *
     * @param cmsTagArticleQuery 查询对象
     * @return 文章列表
     */
    @ApiOperation(value = "通过标签获取文章列表")
    @GetMapping(value = "tag")
    @ResponseBody
    public Object getArticlesByColumn(CmsTagArticleQuery cmsTagArticleQuery) {
        CommonPage<CmsArticlePortalVo> page = cmsArticleService.getArticlesByTag(cmsTagArticleQuery);
        return ResponseVo.response(ResponseEnum.SUCCESS, page);
    }

    /**
     * 通过类别获取文章列表
     *
     * @param cmsCategoryArticleQuery 查询对象
     * @return 文章列表
     */
    @ApiOperation(value = "通过类别获取文章列表")
    @GetMapping(value = "category")
    @ResponseBody
    public Object getArticlesByCategory(CmsCategoryArticleQuery cmsCategoryArticleQuery) {
        CommonPage<CmsArticlePortalVo> page = cmsArticleService.getArticlesByCategory(cmsCategoryArticleQuery);
        return ResponseVo.response(ResponseEnum.SUCCESS, page);
    }

    /**
     * 查询轮播文章列表
     *
     * @param cmsSliderArticleQuery 查询对象
     * @return 文章列表
     */
    @ApiOperation(value = "查询轮播文章列表")
    @GetMapping(value = "slider")
    @ResponseBody
    public Object getArticlesBySlider(CmsSliderArticleQuery cmsSliderArticleQuery) {
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsArticleService.getArticlesBySlider(cmsSliderArticleQuery));
    }

    /**
     * 按部门查询文章
     *
     * @param cmsDeptArticleQuerys 查询对象
     * @return 文章列表
     */
    @ApiOperation(value = "按部门查询文章")
    @GetMapping(value = "dept")
    @ResponseBody
    public Object getArticlesByDept(CmsDeptArticleQuery cmsDeptArticleQuerys) {
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsArticleService.getArticlesByDept(cmsDeptArticleQuerys));
    }

    /**
     * 生成栏目页
     *
     * @return 响应
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "生成栏目")
    @ApiImplicitParam(name = "columnId", value = "栏目路径", required = true)
    @PostMapping(value = "column/{columnId}")
    @LogAction(module = "门户管理", method = "生成栏目", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public ModelAndView generateColumn(@PathVariable Long columnId, ModelAndView mav) {
        //获取模板
        String model = cmsFinalArticleService.getTemplateByColumn(columnId);
        ModelAndView modelAndView = new ModelAndView(model);
        //todo 加载数据
        return modelAndView;
    }

    /**
     * 生成门户页
     *
     * @return 响应
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "生成门户")
    @ApiImplicitParam(name = "siteId", value = "站点路径", required = true)
    @PostMapping(value = "site/{siteId}")
    @LogAction(module = "门户管理", method = "生成门户", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public String generatePortal(@PathVariable Long siteId) {
        //获取模板
//        CmsSite cmsSite = cmsFinalArticleService.getTemplateBySite(siteId);
        //todo 加载数据
        return "";
    }

}
