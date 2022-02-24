package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsAuthorityAo;
import com.rongji.rjsoft.entity.content.CmsAuthority;
import com.rongji.rjsoft.query.content.CmsAuthorityQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsAuthorityVo;

/**
 * <p>
 *  站点栏目授权服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-24
 */
public interface ICmsAuthorityService extends IService<CmsAuthority> {

    /**
     * 授权
     * @param cmsAuthorityAo 授权关系参数体
     * @return 授权结果
     */
    boolean authorization(CmsAuthorityAo cmsAuthorityAo);

    /**
     * 授权关系分页查询
     * @param cmsAuthorityQuery 授权分页查询对象
     * @return 分页结果
     */
    CommonPage<CmsAuthorityVo> getPage(CmsAuthorityQuery cmsAuthorityQuery);

    /**
     * 删除授权
     * @param cmsAuthorityAo 授权参数体
     * @return 删除结果
     */
    boolean delete(CmsAuthorityAo cmsAuthorityAo);
}
