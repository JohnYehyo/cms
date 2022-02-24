package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsTemplateAo;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.query.content.CmsTemplateListQuery;
import com.rongji.rjsoft.query.content.CmsTemplateQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsTemplateListVo;
import com.rongji.rjsoft.vo.content.CmsTemplateVo;

import java.util.List;

/**
 * <p>
 * 模板表 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-18
 */
public interface ICmsTemplateService extends IService<CmsTemplate> {

    /**
     * 添加模板
     * @param cmsTemplateAo 模板参数
     * @return 添加结果
     */
    boolean add(CmsTemplateAo cmsTemplateAo);

    /**
     * 编辑模板
     * @param cmsTemplateAo 模板参数
     * @return 编辑结果
     */
    boolean edit(CmsTemplateAo cmsTemplateAo);

    /**
     * 删除模板
     * @param templateId 模板参数
     * @return 删除结果
     */
    boolean delete(Long templateId);

    /**
     * 模板分页查询
     * @param cmsTemplateQuery 模板分页查询条件
     * @return 模板分页查询结果
     */
    CommonPage<CmsTemplateVo> getPage(CmsTemplateQuery cmsTemplateQuery);

    /**
     * 模板分列表查询
     * @param cmsTemplateListQuery 模板列表查询条件
     * @return 模板列表查询结果
     */
    List<CmsTemplateListVo> listOfTemplate(CmsTemplateListQuery cmsTemplateListQuery);

    /**
     * 获取模板路径
     * @param templateId 模板id
     * @return 模板路径
     */
    String getTemplateUrl(Long templateId);
}
