package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.query.content.CmsTemplateQuery;
import com.rongji.rjsoft.vo.content.CmsTemplateVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 模板表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-18
 */
public interface CmsTemplateMapper extends BaseMapper<CmsTemplate> {

    /**
     * 模板列表查询
     * @param cmsTemplateQuery 查询对象
     * @return 模板列表查询结果
     */
    List<CmsTemplateVo> getList(CmsTemplateQuery cmsTemplateQuery);

    /**
     * 通过栏目id获取模板信息
     * @param columnId 栏目id
     * @return 模板信息
     */
    CmsTemplate getTemplateByColumnId(Long columnId);

    /**
     * 获取模板路径
     * @param templateId 模板id
     * @return 模板路径
     */
    String getTemplateUrl(Long templateId);
}
