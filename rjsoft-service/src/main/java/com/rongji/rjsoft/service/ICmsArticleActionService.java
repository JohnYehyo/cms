package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsArticleActionAo;
import com.rongji.rjsoft.entity.content.CmsArticleAction;
import com.rongji.rjsoft.vo.content.CmsArticleActionSimpleVo;

import java.util.List;

/**
 * <p>
 * 文章操作表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-27
 */
public interface ICmsArticleActionService extends IService<CmsArticleAction> {

    /**
     * 文章互动信息
     * @param articleId 文章id
     * @return 文章互动信息
     */
    List<CmsArticleActionSimpleVo> getActionByArticle(Long articleId);

    /**
     * 文章互动
     * @param cmsArticleActionAo 参数体
     * @return 互动结果
     */
    boolean action(CmsArticleActionAo cmsArticleActionAo);
}
