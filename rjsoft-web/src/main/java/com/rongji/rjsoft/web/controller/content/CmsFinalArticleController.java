package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsCategoryArticleQuery;
import com.rongji.rjsoft.query.content.CmsColumnArticleQuery;
import com.rongji.rjsoft.query.content.CmsSliderArticleQuery;
import com.rongji.rjsoft.query.content.CmsTagArticleQuery;
import com.rongji.rjsoft.service.ICmsArticleService;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.ResponseVo;
import com.rongji.rjsoft.vo.content.CmsArticlePortalVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
     * @param cmsSliderArticleQuery 查询对象
     * @return 文章列表
     */
    @ApiOperation(value = "查询轮播文章列表")
    @GetMapping(value = "slider")
    @ResponseBody
    public Object getArticlesBySlider(CmsSliderArticleQuery cmsSliderArticleQuery) {
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsArticleService.getArticlesBySlider(cmsSliderArticleQuery));
    }

}
