package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.*;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.CmsArticlePublishTypeEnum;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.query.content.CmsArticleQuery;
import com.rongji.rjsoft.service.ICmsArticleService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.ResponseVo;
import com.rongji.rjsoft.vo.content.CmsArticleInfoVo;
import com.rongji.rjsoft.vo.content.CmsArticleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 文章信息表 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-22
 */
@Api(tags = "CMS-文章管理")
@RestController
@RequestMapping("/cmsArticle")
@AllArgsConstructor
public class CmsArticleController {

    private final ICmsArticleService cmsArticleService;

    /**
     * 添加文章
     *
     * @param cmsArticleAo 文章表单信息
     * @return 添加结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "添加文章")
    @PostMapping(value = "article")
    @LogAction(module = "文章管理", method = "添加文章", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object add(@Validated(CmsArticleAo.insert.class) @RequestBody CmsArticleAo cmsArticleAo) {
        if(cmsArticleAo.getPublishType() == CmsArticlePublishTypeEnum.AUTOMATIC.getCode()
                && null == cmsArticleAo.getPublishTime()){
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "请填写发布时间!");
        }
        if (cmsArticleService.saveArticle(cmsArticleAo)) {
            return ResponseVo.success("添加文章成功");
        }
        return ResponseVo.error("添加文章失败");
    }

    /**
     * 编辑文章
     *
     * @param cmsArticleAo 文章表单信息
     * @return 添加结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "编辑文章")
    @PutMapping(value = "article")
    @LogAction(module = "文章管理", method = "编辑文章", logType = LogTypeEnum.UPDATE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@Validated(CmsArticleAo.update.class) @RequestBody CmsArticleAo cmsArticleAo) {
        if(cmsArticleAo.getPublishType() == CmsArticlePublishTypeEnum.AUTOMATIC.getCode()
                && null == cmsArticleAo.getPublishTime()){
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "请填写发布时间!");
        }
        if (cmsArticleService.updateArticle(cmsArticleAo)) {
            return ResponseVo.success("编辑文章成功");
        }
        return ResponseVo.error("编辑文章失败");
    }

    /**
     * 删除文章
     *
     * @param list 删除条件
     * @return 删除结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "删除文章")
    @DeleteMapping(value = "article")
    @LogAction(module = "文章管理", method = "删除文章", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object audit(@Valid @RequestBody CmsArticleDeleteAo[] list) {
        if (cmsArticleService.deleteArticle(list)) {
            return ResponseVo.success("删除文章成功");
        }
        return ResponseVo.error("删除文章失败");
    }

    /**
     * 审核文章
     *
     * @param cmsArticleAuditAo 文章状态信息
     * @return 审核结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('articel_audit_admin')")
    @ApiOperation(value = "审核文章")
    @PutMapping(value = "audit")
    @LogAction(module = "文章管理", method = "审核文章", logType = LogTypeEnum.UPDATE, operatorType = OperatorTypeEnum.WEB)
    public Object audit(@Valid @RequestBody CmsArticleAuditAo cmsArticleAuditAo) {
        if (cmsArticleService.audit(cmsArticleAuditAo)) {
            return ResponseVo.success("审核文章成功");
        }
        return ResponseVo.error("审核文章失败");
    }

    /**
     * 文章列表
     *
     * @param cmsArticleQuery 查询对象
     * @return 文章列表
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "文章列表")
    @GetMapping(value = "list")
    public Object list(CmsArticleQuery cmsArticleQuery) {
        CommonPage<CmsArticleVo> page = cmsArticleService.getPage(cmsArticleQuery);
        return ResponseVo.response(ResponseEnum.SUCCESS, page);
    }

    /**
     * 文章详情
     *
     * @param articleId 文章id
     * @return 文章详情
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "文章详情")
    @ApiImplicitParam(name = "articleId", value = "文章ID", required = true)
    @GetMapping(value = "article/{articleId}")
    public Object info(@PathVariable Long articleId) {
        CmsArticleInfoVo cmsArticleInfoVo = cmsArticleService.getInfo(articleId);
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsArticleInfoVo);
    }


    /**
     * 转发文章
     * @param cmsArticleForWardingAo 转发文章参数体
     * @return 转发结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "转发文章")
    @PostMapping(value = "forwarding")
    @LogAction(module = "文章管理", method = "转发文章", logType = LogTypeEnum.FORWARDING, operatorType = OperatorTypeEnum.WEB)
    public Object forwarding(@Valid @RequestBody CmsArticleForWardingAo cmsArticleForWardingAo){
        if(cmsArticleService.forwarding(cmsArticleForWardingAo)){
            return ResponseVo.success("转发成功");
        }
        return ResponseVo.error("转发失败,请重试");
    }

    /**
     * 移动文章
     * @param cmsArticleForMoveAo 移动文章参数体
     * @return 转发结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('article_admin')")
    @ApiOperation(value = "移动文章")
    @PostMapping(value = "move")
    @LogAction(module = "文章管理", method = "移动文章", logType = LogTypeEnum.MOVE, operatorType = OperatorTypeEnum.WEB)
    public Object move(@Valid @RequestBody CmsArticleForMoveAo cmsArticleForMoveAo){
        if(cmsArticleService.move(cmsArticleForMoveAo)){
            return ResponseVo.success("移动成功");
        }
        return ResponseVo.error("移动失败,请重试");
    }

}
