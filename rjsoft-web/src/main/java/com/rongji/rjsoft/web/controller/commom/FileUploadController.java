package com.rongji.rjsoft.web.controller.commom;

import com.rongji.rjsoft.ao.common.FileUploadAo;
import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.common.util.file.FileUploadUtils;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.OperatorTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @description: 文件上传
 * @author: JohnYehyo
 * @create: 2021-09-24 12:59:19
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping(value = "upload")
public class FileUploadController {


    /**
     * 文件上传
     * @param file 文件
     * @param fileUploadAo 上传目标信息
     * @return 上传结果
     */
    @ApiOperation(value = "文件上传")
    @PostMapping
    @LogAction(module = "文件上传", method = "上传文件", logType = LogTypeEnum.UPLOAD, operatorType = OperatorTypeEnum.WEB)
    public Object upload(@RequestParam("file") MultipartFile file, @Valid FileUploadAo fileUploadAo) throws IOException {
        String uploadDir = FileUploadUtils.upload(fileUploadAo.getBusinessType(), file);
        return ResponseVo.response(ResponseEnum.SUCCESS, uploadDir);
    }
}
