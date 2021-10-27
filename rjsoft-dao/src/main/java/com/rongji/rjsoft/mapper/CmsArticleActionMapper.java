package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rongji.rjsoft.entity.content.CmsArticleAction;
import com.rongji.rjsoft.vo.content.CmsArticleActionSimpleVo;

import java.util.List;

/**
 * <p>
 * 文章操作表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-27
 */
public interface CmsArticleActionMapper extends BaseMapper<CmsArticleAction> {

    /**
     * 文章互动信息
     * @param articleId 文章id
     * @return 文章互动信息
     */
    List<CmsArticleActionSimpleVo> getActionCount(Long articleId);
}
