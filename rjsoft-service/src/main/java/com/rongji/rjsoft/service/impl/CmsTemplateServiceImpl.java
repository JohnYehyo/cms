package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsTemplateAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.entity.system.SysCommonFile;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.enums.TableFileTypeEnum;
import com.rongji.rjsoft.enums.TableTypeEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsTemplateMapper;
import com.rongji.rjsoft.query.content.CmsTemplateQuery;
import com.rongji.rjsoft.service.ICmsTemplateService;
import com.rongji.rjsoft.service.ISysCommonFileService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsTemplateVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final ISysCommonFileService sysCommonFileService;

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

        //保存附件上传记录
        saveFiles(cmsTemplateAo, cmsTemplate);

        return insert > 0;
    }

    private void saveFiles(CmsTemplateAo cmsTemplateAo, CmsTemplate cmsTemplate) {
        SysCommonFile sysCommonFile = new SysCommonFile();
        sysCommonFile.setTableId(cmsTemplate.getTemplateId());
        sysCommonFile.setTableType(TableTypeEnum.CMS_TEMPLATE.getValue());
        //保存文章模板
        sysCommonFile.setFileName(cmsTemplateAo.getArticleTemplate().getFileName());
        sysCommonFile.setFileUrl(cmsTemplateAo.getArticleTemplate().getFileUrl());
        sysCommonFile.setFileType(TableFileTypeEnum.TEMPLATE_HTML_ARTICLE.getCode());
        sysCommonFileService.save(sysCommonFile);
        //保存栏目模板
        sysCommonFile.setFileName(cmsTemplateAo.getArticleTemplate().getFileName());
        sysCommonFile.setFileUrl(cmsTemplateAo.getArticleTemplate().getFileUrl());
        sysCommonFile.setFileType(TableFileTypeEnum.TEMPLATE_HTML_COLUMN.getCode());
        sysCommonFileService.save(sysCommonFile);
        //保存缩略图
        sysCommonFile.setFileType(TableFileTypeEnum.TEMPLATE_IMG.getCode());
        cmsTemplateAo.getTemplateImg().forEach(k -> {
            sysCommonFile.setFileName(k.getFileName());
            sysCommonFile.setFileUrl(k.getFileUrl());
            sysCommonFileService.save(sysCommonFile);
        });
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
        int update = cmsTemplateMapper.updateById(cmsTemplate);

        deleteFiles(cmsTemplateAo.getTemplateId());

        saveFiles(cmsTemplateAo, cmsTemplate);

        return update > 0;
    }

    private void deleteFiles(Long tableId) {
        LambdaUpdateWrapper<SysCommonFile> wrapper = new LambdaUpdateWrapper();
        wrapper.eq(SysCommonFile::getTableId, tableId)
                .eq(SysCommonFile::getTableType, TableTypeEnum.CMS_TEMPLATE.getValue());
        sysCommonFileService.remove(wrapper);
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
        deleteFiles(templateId);
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
        List<CmsTemplateVo> records = new ArrayList<>();
        List<CmsTemplateVo> recordsTemp = page.getRecords();
        Map<String, List<CmsTemplateVo>> map = recordsTemp.stream().collect(Collectors.groupingBy(CmsTemplateVo::getTemplateId));
        CmsTemplateVo cmsTemplateVo;
        String imgStr = "";
        for (Map.Entry<String, List<CmsTemplateVo>> entry : map.entrySet()) {
            cmsTemplateVo = new CmsTemplateVo();
            for (CmsTemplateVo cms: entry.getValue()) {
                cmsTemplateVo.setTemplateId(cms.getTemplateId());
                cmsTemplateVo.setTemplateName(cms.getTemplateName());
                imgStr = imgStr + "," + cms.getTemplateImg();
            }
            cmsTemplateVo.setTemplateImg(imgStr.substring(1));
            records.add(cmsTemplateVo);
        }
        page.setRecords(records);
        return CommonPageUtils.assemblyPage(page);
    }
}
