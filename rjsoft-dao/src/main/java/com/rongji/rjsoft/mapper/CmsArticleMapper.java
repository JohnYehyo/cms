package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.ao.content.CmsArticleAuditAo;
import com.rongji.rjsoft.entity.content.CmsArticle;
import com.rongji.rjsoft.query.content.CmsArticleQuery;
import com.rongji.rjsoft.vo.content.CmsArticleInfoVo;
import com.rongji.rjsoft.vo.content.CmsArticleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文章信息表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-22
 */
public interface CmsArticleMapper extends BaseMapper<CmsArticle> {

    /**
     * 审核文章
     * @param cmsArticleAuditAo 文章状态信息
     * @return 审核结果
     */
    boolean audit(CmsArticleAuditAo cmsArticleAuditAo);

    /**
     * 文章列表
     * @param page 分页对象
     * @param cmsArticleQuery 查询对象
     * @param deptIds 拥有的部门id
     * @param deptId 本部门id
     * @return 文章列表
     */
    IPage<CmsArticleVo> getPage(IPage<CmsArticleVo> page,
                                @Param("param") CmsArticleQuery cmsArticleQuery,
                                @Param("deptIds") List<Long> deptIds,
                                @Param("deptId") Long deptId);

    /**
     * 文章详情
     * @param articleId 文章id
     * @return 文章详情
     */
    CmsArticleInfoVo getInfo(Long articleId);

}
