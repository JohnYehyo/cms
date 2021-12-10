package com.rongji.rjsoft.web.controller.system;

import cn.hutool.core.bean.BeanUtil;
import com.rongji.rjsoft.ao.system.LoginAo;
import com.rongji.rjsoft.common.security.entity.LoginUser;
import com.rongji.rjsoft.common.security.entity.SsoLoginUser;
import com.rongji.rjsoft.common.security.util.TokenUtils;
import com.rongji.rjsoft.common.util.ServletUtils;
import com.rongji.rjsoft.entity.system.SysUser;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.service.ISysLoginService;
import com.rongji.rjsoft.service.ISysMenuService;
import com.rongji.rjsoft.vo.ResponseVo;
import com.rongji.rjsoft.vo.system.menu.SysMenuInfoVo;
import com.rongji.rjsoft.vo.system.user.CurrentUserInfoVo;
import com.rongji.rjsoft.vo.system.user.SysUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * @description: 登录
 * @author: JohnYehyo
 * @create: 2021-04-26 15:31:17
 */
@Api(tags = "系统管理-登录管理")
@RestController
@AllArgsConstructor
public class SysLoginController {

    private final ISysLoginService sysLoginService;
    private final TokenUtils tokenUtils;
    private final ISysMenuService sysMenuService;

    /**
     * 登录
     * @param loginAo 登录信息
     * @return 登录结果
     */
    @ApiOperation(value = "登录")
    @PostMapping(value = "login")
    public Object login(@Valid @RequestBody LoginAo loginAo){
        return sysLoginService.login(loginAo);
    }

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @ApiOperation(value = "获取当前用户信息")
    @GetMapping(value = "getUserInfo")
    public Object userInfo(){
        LoginUser loginUser = tokenUtils.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        SysUserVo sysUserVo = new SysUserVo();
        BeanUtil.copyProperties(user, sysUserVo);
        Set<String> roles = loginUser.getRoles();
        //暂时不需要
//        Set<String> menus = sysMenuService.getMenuPermsByUserId(user.getUserId());
        CurrentUserInfoVo currentUserInfoVo = new CurrentUserInfoVo(sysUserVo, roles, null);
        return ResponseVo.response(ResponseEnum.SUCCESS, currentUserInfoVo);
    }

    /**
     * 获取当前用户路由信息
     * @return 当前用户路由信息
     */
    @ApiOperation(value = "获取当前用户路由信息")
    @GetMapping(value = "routes")
    public Object routes(){
        LoginUser loginUser = tokenUtils.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        List<SysMenuInfoVo> list = sysMenuService.getRoutesByUserId(user.getUserId());
        return ResponseVo.response(ResponseEnum.SUCCESS, list);
    }

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
    @PostMapping(value = "tokenLogin")
    public Object tokenLogin(String token){
        SsoLoginUser ssoLoginUser = sysLoginService.tokenLogin(token);
        if(null == ssoLoginUser){
            return ResponseVo.response(ResponseEnum.TOKEN_INVALID);
        }
        return ResponseVo.response(ResponseEnum.SUCCESS, ssoLoginUser);
    }

}
