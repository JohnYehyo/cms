package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rongji.rjsoft.ao.content.CmsArticleActionAo;
import com.rongji.rjsoft.common.security.util.TokenUtils;
import com.rongji.rjsoft.common.util.ServletUtils;
import com.rongji.rjsoft.entity.content.CmsArticleAction;
import com.rongji.rjsoft.mapper.CmsArticleActionMapper;
import com.rongji.rjsoft.service.ICmsArticleActionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.vo.content.CmsArticleActionSimpleVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文章操作表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-27
 */
@Service
@AllArgsConstructor
public class CmsArticleActionServiceImpl extends ServiceImpl<CmsArticleActionMapper, CmsArticleAction> implements ICmsArticleActionService {

    private final CmsArticleActionMapper cmsArticleActionMapper;

    private final TokenUtils tokenUtils;
    /**
     * 文章互动信息
     * @param articleId 文章id
     * @return 文章互动信息
     */
    @Override
    public List<CmsArticleActionSimpleVo> getActionByArticle(Long articleId) {
        LambdaQueryWrapper<CmsArticleAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticleAction::getArticleId, articleId);
        List<CmsArticleActionSimpleVo> list = cmsArticleActionMapper.getActionCount(articleId);
        return list;
    }

    /**
     * 文章互动
     * @param cmsArticleActionAo 参数体
     * @return 互动结果
     */
    @Override
    public boolean action(CmsArticleActionAo cmsArticleActionAo) {
        CmsArticleAction cmsArticleAction = new CmsArticleAction();
        BeanUtil.copyProperties(cmsArticleActionAo, cmsArticleAction);
        cmsArticleAction.setUserId(tokenUtils.getLoginUser(ServletUtils.getRequest()).getUser().getUserId());
        return cmsArticleActionMapper.insert(cmsArticleAction) > 0;
    }
}
