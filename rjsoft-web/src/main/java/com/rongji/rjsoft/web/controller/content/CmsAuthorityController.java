package com.rongji.rjsoft.web.controller.content;


import com.rongji.rjsoft.service.ICmsAuthorityService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-24
 */
@Api(tags = "CMS-授权")
@RestController
@RequestMapping(value = "cmsAuthority")
@AllArgsConstructor
public class CmsAuthorityController {

    private final ICmsAuthorityService cmsAuthorityService;

}
