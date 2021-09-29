package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.ao.content.CmsSensitiveWordsAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.query.content.CmsSensitiveWordsQuery;
import com.rongji.rjsoft.service.ICmsSensitiveWordsService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.ResponseVo;
import com.rongji.rjsoft.vo.content.CmsSensitiveWordsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 敏感词 前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-26
 */
@Api(tags = "CMS-敏感词管理")
@RestController
@RequestMapping("/cmsSensitiveWords")
@AllArgsConstructor
public class CmsSensitiveWordsController {

    private final ICmsSensitiveWordsService cmsSensitiveWordsService;

    /**
     * 添加敏感词
     *
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 添加结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "添加敏感词")
    @PostMapping
    @LogAction(module = "敏感词管理", method = "添加敏感词", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object add(@Valid @RequestBody CmsSensitiveWordsAo cmsSensitiveWordsAo) {
        if (cmsSensitiveWordsService.saveWord(cmsSensitiveWordsAo)) {
            return ResponseVo.success("添加敏感词成功");
        }
        return ResponseVo.error("添加敏感词失败");
    }

    /**
     * 编辑敏感词
     *
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 编辑结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "编辑敏感词")
    @PutMapping
    @LogAction(module = "敏感词管理", method = "编辑敏感词", logType = LogTypeEnum.UPDATE, operatorType = OperatorTypeEnum.WEB)
    public Object update(@Valid @RequestBody CmsSensitiveWordsAo cmsSensitiveWordsAo) {
        if (cmsSensitiveWordsService.updateWord(cmsSensitiveWordsAo)) {
            return ResponseVo.success("编辑敏感词成功");
        }
        return ResponseVo.error("编辑敏感词失败");
    }

    /**
     * 删除敏感词
     *
     * @param wordId 敏感词id
     * @return 删除结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('cms_admin')")
    @ApiOperation(value = "删除敏感词")
    @ApiImplicitParam(name = "wordId", value = "敏感词id", required = true)
    @DeleteMapping(value = "{wordId}")
    @LogAction(module = "敏感词管理", method = "删除敏感词", logType = LogTypeEnum.DELETE, operatorType = OperatorTypeEnum.WEB)
    public Object delete(@PathVariable Long[] wordId) {
        if (cmsSensitiveWordsService.deleteWords(wordId)) {
            return ResponseVo.success("删除敏感词成功");
        }
        return ResponseVo.error("删除敏感词失败");
    }

    /**
     * 敏感词列表
     *
     * @param cmsSensitiveWordsQuery 查询对象
     * @return 敏感词列表
     */
    @ApiOperation(value = "敏感词列表")
    @GetMapping
    @LogAction(module = "敏感词管理", method = "查询敏感词列表", logType = LogTypeEnum.SELECT, operatorType = OperatorTypeEnum.WEB)
    public Object list(CmsSensitiveWordsQuery cmsSensitiveWordsQuery) {
        CommonPage<CmsSensitiveWordsVo> page = cmsSensitiveWordsService.listOfWords(cmsSensitiveWordsQuery);
        return ResponseVo.response(ResponseEnum.SUCCESS, page);
    }

    /**
     * 批量添加敏感词
     *
     * @param fileUrl 文件地址
     * @return 添加结果
     */
    @PreAuthorize("@permissionIdentify.hasRole('admin')")
    @ApiOperation(value = "批量添加敏感词")
    @ApiImplicitParam(name = "fileUrl", value = "敏感词文件路径", required = true)
    @PostMapping(value = "batchSaveWords")
    @LogAction(module = "敏感词管理", method = "批量添加敏感词", logType = LogTypeEnum.INSERT, operatorType = OperatorTypeEnum.WEB)
    public Object batchSaveWords(@RequestParam String fileUrl) {
        if (cmsSensitiveWordsService.batchSaveWords(fileUrl)) {
            return ResponseVo.success("添加敏感词成功");
        }
        return ResponseVo.error("添加敏感词失败");
    }

}
