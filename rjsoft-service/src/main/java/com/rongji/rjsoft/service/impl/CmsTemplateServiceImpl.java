package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rongji.rjsoft.ao.content.CmsTemplateAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsTemplateMapper;
import com.rongji.rjsoft.query.content.CmsTemplateQuery;
import com.rongji.rjsoft.service.ICmsTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsTemplateVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 模板表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-18
 */
@Service
@AllArgsConstructor
public class CmsTemplateServiceImpl extends ServiceImpl<CmsTemplateMapper, CmsTemplate> implements ICmsTemplateService {

    private final CmsTemplateMapper cmsTemplateMapper;

    private final CmsColumnMapper columnMapper;

    /**
     * 添加模板
     *
     * @param cmsTemplateAo 模板参数
     * @return 添加结果
     */
    @Override
    public boolean add(CmsTemplateAo cmsTemplateAo) {
        LambdaQueryWrapper<CmsTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsTemplate::getTemplateName, cmsTemplateAo.getTemplateName()).last(" limit 0, 1");
        CmsTemplate cmsTemplate = cmsTemplateMapper.selectOne(wrapper);
        if (null != cmsTemplate) {
            throw new BusinessException(ResponseEnum.DATA_REPEAT);
        }
        cmsTemplate = new CmsTemplate();
        BeanUtil.copyProperties(cmsTemplateAo, cmsTemplate);
        int insert = cmsTemplateMapper.insert(cmsTemplate);
        return insert > 0;
    }

    /**
     * 编辑模板
     *
     * @param cmsTemplateAo 模板参数
     * @return 编辑模板
     */
    @Override
    public boolean edit(CmsTemplateAo cmsTemplateAo) {
        LambdaQueryWrapper<CmsTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsTemplate::getTemplateName, cmsTemplateAo.getTemplateName()).last(" limit 0, 1");
        CmsTemplate cmsTemplate = cmsTemplateMapper.selectOne(wrapper);
        if (null != cmsTemplate && !cmsTemplate.getTemplateId().equals(cmsTemplateAo.getTemplateId())) {
            throw new BusinessException(ResponseEnum.DATA_REPEAT);
        }
        cmsTemplate = new CmsTemplate();
        BeanUtil.copyProperties(cmsTemplateAo, cmsTemplate);
        int insert = cmsTemplateMapper.updateById(cmsTemplate);
        return insert > 0;
    }

    /**
     * 删除模板
     *
     * @param templateId 模板参数
     * @return 删除模板
     */
    @Override
    public boolean delete(Long templateId) {
        LambdaQueryWrapper<CmsColumn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumn::getTemplateId, templateId).last(" limit 0, 1");
        CmsColumn cmsColumn = columnMapper.selectOne(wrapper);
        if (null != cmsColumn) {
            throw new BusinessException(ResponseEnum.TAKE_UP);
        }
        return columnMapper.deleteById(templateId) > 0;
    }

    /**
     * 模板分页查询
     *
     * @param cmsTemplateQuery 模板分页查询条件
     * @return 模板分页查询结果
     */
    @Override
    public CommonPage<CmsTemplateVo> getPage(CmsTemplateQuery cmsTemplateQuery) {
        IPage<CmsTemplateVo> page = new Page<>();
        page = cmsTemplateMapper.getPage(page, cmsTemplateQuery);
        return CommonPageUtils.assemblyPage(page);
    }
}
