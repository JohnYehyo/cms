package com.rongji.rjsoft.web.controller.portal;

import com.rongji.rjsoft.common.annotation.LogAction;
import com.rongji.rjsoft.common.security.entity.SsoLoginUser;
import com.rongji.rjsoft.enums.LogStatusEnum;
import com.rongji.rjsoft.enums.LogTypeEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.service.ISysLoginInfoService;
import com.rongji.rjsoft.service.ISysLoginService;
import com.rongji.rjsoft.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 门户对接接口
 * @author: JohnYehyo
 * @create: 2022-01-18 11:16:21
 */
@Api(tags = "门户")
@RequestMapping(value = "portal")
@RestController
@AllArgsConstructor
public class PortalController {

    private final ISysLoginService sysLoginService;
    private final ISysLoginInfoService sysLoginInfoService;

    /**
     * 获取SSO Token
     * @return
     */
    @ApiOperation(value = "获取SSO Token")
    @GetMapping(value = "ssoToken")
    public Object creatSsoToken(){
        return ResponseVo.response(ResponseEnum.SUCCESS, sysLoginService.creatSsoToken());
    }

    /**
     * SSO登录
     * @param token token
     * @return 登录结果
     */
    @ApiOperation(value = "SSO登录")
    @ApiImplicitParam(name = "token", value = "token", required = true)
    @PostMapping(value = "tokenLogin/{token}")
    @LogAction(module = "门户", method = "sso登录", logType = LogTypeEnum.DOWNLOAD)
    public Object tokenLogin(@PathVariable("token") String token){
        SsoLoginUser ssoLoginUser = sysLoginService.tokenLogin(token);
        if(null == ssoLoginUser){
            sysLoginInfoService.saveLoginInfo(token, LogStatusEnum.FAIL.getCode(), ResponseEnum.TOKEN_INVALID.getValue());
            return ResponseVo.response(ResponseEnum.TOKEN_INVALID);
        }
        sysLoginInfoService.saveLoginInfo(ssoLoginUser.getUserName(), LogStatusEnum.LOGIN_SUCCESS.getCode(),
                LogStatusEnum.LOGIN_SUCCESS.getValue());
        return ResponseVo.response(ResponseEnum.SUCCESS, ssoLoginUser);
    }
}
