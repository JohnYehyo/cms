package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import com.rongji.rjsoft.vo.content.CmsArticleRefVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
public interface CmsFinalArticleMapper extends BaseMapper<CmsFinalArticle> {

    /**
     * 批量保存文章栏目关系
     * @param list 关系对象集合
     * @return 保存结果
     */
    int batchInsert(List<CmsFinalArticle> list);

    /**
     * 文章引用查询
     * @param articleId 文章ID
     * @return 文章引用列表
     */
    List<CmsArticleRefVo> listOfArticleRef(Long articleId);
}
