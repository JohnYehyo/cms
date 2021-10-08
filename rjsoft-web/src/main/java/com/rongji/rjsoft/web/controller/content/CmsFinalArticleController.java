package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
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
@Api(tags = "门户管理")
@Controller
@RequestMapping(value = "portal")
@AllArgsConstructor
public class CmsFinalArticleController {

    private final ICmsFinalArticleService cmsFinalArticleService;


    /**
     * 生成门户
     *
     * @return 响应
     */
//    @ApiOperation(value = "生成门户")
    @PostMapping(value = "generate")
    @ResponseBody
    @LogAction(module = "门户管理", method = "生成门户", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object generatePortal() {
        if (cmsFinalArticleService.generate()) {
            return ResponseVo.success();
        }
        return ResponseVo.error();
    }


    /**
     * 生成文章
     *
     * @return 响应
     */
    @ApiOperation(value = "生成文章")
    @PostMapping(value = "generateArticle")
    @ResponseBody
    @LogAction(module = "门户管理", method = "生成文章", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public void generateArticle() {
        cmsFinalArticleService.generateArticle();
    }

}
