package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.CmsArticleActionAo;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.service.ICmsArticleActionService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 文章操作表 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-27
 */
@Api(tags = "CMS-文章操作")
@RestController
@RequestMapping("/cmsArticleAction")
@AllArgsConstructor
public class CmsArticleActionController {

    private ICmsArticleActionService cmsArticleActionService;

    /**
     * 文章互动信息
     * @param articleId 文章id
     * @return 文章互动信息
     */
    @ApiOperation(value = "文章互动信息")
    @ApiImplicitParam(name = "articleId", value = "文章id", required = true)
    @GetMapping(value = "/{articleId}")
    public Object getActionByArticle(@PathVariable Long articleId){
        return ResponseVo.response(ResponseEnum.SUCCESS, cmsArticleActionService.getActionByArticle(articleId));
    }

    /**
     * 文章互动
     * @param cmsArticleActionAo 参数体
     * @return 互动结果
     */
    @ApiOperation(value = "文章互动信息")
    @PostMapping
    public Object action(@Valid @RequestBody CmsArticleActionAo cmsArticleActionAo){
        if(cmsArticleActionService.action(cmsArticleActionAo)){
            return ResponseVo.success();
        }
        return ResponseVo.error();
    }

}
