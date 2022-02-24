package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsTemplateAo;
import com.rongji.rjsoft.common.config.FileConfig;
import com.rongji.rjsoft.entity.content.CmsColumn;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.entity.content.CmsTemplate;
import com.rongji.rjsoft.entity.system.SysCommonFile;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.enums.TableFileTypeEnum;
import com.rongji.rjsoft.enums.TableTypeEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsColumnMapper;
import com.rongji.rjsoft.mapper.CmsFinalArticleMapper;
import com.rongji.rjsoft.mapper.CmsSiteMapper;
import com.rongji.rjsoft.mapper.CmsTemplateMapper;
import com.rongji.rjsoft.query.content.CmsTemplateListQuery;
import com.rongji.rjsoft.query.content.CmsTemplateQuery;
import com.rongji.rjsoft.service.ICmsTemplateService;
import com.rongji.rjsoft.service.ISysCommonFileService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.common.FileVo;
import com.rongji.rjsoft.vo.content.CmsTemplateListVo;
import com.rongji.rjsoft.vo.content.CmsTemplateVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private final CmsSiteMapper siteMapper;

    private final CmsColumnMapper columnMapper;

    private final CmsFinalArticleMapper finalArticleMapper;

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
//        sysCommonFile.setFileType(TableFileTypeEnum.TEMPLATE_IMG.getCode());
        sysCommonFile.setFileName(cmsTemplateAo.getTemplate().getFileName());
        sysCommonFile.setFileUrl(cmsTemplateAo.getTemplate().getFileUrl());
        sysCommonFileService.save(sysCommonFile);
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
        LambdaQueryWrapper<CmsSite> wrapper1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CmsColumn> wrapper2 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CmsFinalArticle> wrapper3 = new LambdaQueryWrapper<>();
        wrapper1.eq(CmsSite::getSiteTemplate, templateId).or().eq(CmsSite::getListTemplate, templateId).or().eq(CmsSite::getArticleTemplate, templateId).last(" limit 0, 1");
        wrapper2.eq(CmsColumn::getSiteTemplate, templateId).or().eq(CmsColumn::getListTemplate, templateId).or().eq(CmsColumn::getArticleTemplate, templateId).last(" limit 0, 1");
        wrapper3.eq(CmsFinalArticle::getSiteTemplate, templateId).or().eq(CmsFinalArticle::getListTemplate, templateId).or().eq(CmsFinalArticle::getArticleTemplate, templateId).last(" limit 0, 1");
        CmsSite cmsSite = siteMapper.selectOne(wrapper1);
        CmsColumn cmsColumn = columnMapper.selectOne(wrapper2);
        CmsFinalArticle cmsFinalArticle = finalArticleMapper.selectOne(wrapper3);
        if (null != cmsSite || null != cmsColumn || null != cmsFinalArticle) {
            throw new BusinessException(ResponseEnum.TAKE_UP);
        }
        deleteFiles(templateId);
        boolean result = cmsTemplateMapper.deleteById(templateId) > 0;
        //todo 删除附件
        return result;
    }

    /**
     * 模板分页查询
     *
     * @param cmsTemplateQuery 模板分页查询条件
     * @return 模板分页查询结果
     */
    @Override
    public CommonPage<CmsTemplateVo> getPage(CmsTemplateQuery cmsTemplateQuery) {
        cmsTemplateQuery.setBusinessType(CmsTemplate.class.getSimpleName());
        List<CmsTemplateVo> list = cmsTemplateMapper.getList(cmsTemplateQuery);
        CommonPage<CmsTemplateVo> page = reorganization(list, cmsTemplateQuery.getCurrent(), cmsTemplateQuery.getPageSize());
        return page;
    }


    /**
     * 重组分页结果
     *
     * @param recordsTemp 总记录
     * @param current     当前页
     * @param pageSize    分页数目
     * @return
     */
    private CommonPage<CmsTemplateVo> reorganization(List<CmsTemplateVo> recordsTemp, int current, int pageSize) {
        if (CollectionUtil.isEmpty(recordsTemp)) {
            return null;
        }
        for (CmsTemplateVo cms : recordsTemp) {
            cms.setTemplateImg(cms.getFileUrl() + "/" + FileConfig.getSample());
            FileVo fileVo;
            //站点模板
            if (cms.getType() == TableFileTypeEnum.TEMPLATE_SITE.getCode()) {
                fileVo = new FileVo();
                fileVo.setFileUrl(cms.getFileUrl() + "/" + FileConfig.getSiteTemplate());
                fileVo.setFileName(cms.getFileName());
                cms.setTemplate(fileVo);
                continue;
            }
            //列表模板
            if (cms.getType() == TableFileTypeEnum.TEMPLATE_COLUMN.getCode()) {
                fileVo = new FileVo();
                fileVo.setFileUrl(cms.getFileUrl() + "/" + FileConfig.getListTemplate());
                fileVo.setFileName(cms.getFileName());
                cms.setTemplate(fileVo);
                continue;
            }
            //文章模板
            if (cms.getType() == TableFileTypeEnum.TEMPLATE_ARTICLE.getCode()) {
                fileVo = new FileVo();
                fileVo.setFileUrl(cms.getFileUrl() + "/" + FileConfig.getArticleTemplate());
                fileVo.setFileName(cms.getFileName());
                cms.setTemplate(fileVo);
                continue;
            }
        }
        return assemblyPage(current, pageSize, recordsTemp);
    }

    /**
     * 组装分页
     *
     * @param current  当前页
     * @param pageSize 每页数量
     * @param records  总记录
     * @return
     */
    private CommonPage<CmsTemplateVo> assemblyPage(int current, int pageSize, List<CmsTemplateVo> records) {
        CommonPage<CmsTemplateVo> page = new CommonPage<>();
        int totalPage = 0;
        if ((records.size() / pageSize) == 0) {
            totalPage = records.size() / pageSize;
        } else {
            totalPage = records.size() / pageSize + 1;

        }
        page.setTotalPage((long) totalPage);
        page.setTotal((long) records.size());
        page.setCurrent((long) current);
        page.setPageSize((long) pageSize);

        int startIndex = (current - 1) * pageSize;
        int endIndex = startIndex + pageSize;

        if (endIndex > records.size()) {
            endIndex = records.size();
        }

        List<CmsTemplateVo> pageList = records.subList(startIndex, endIndex);

        page.setList(pageList);
        return page;
    }

    /**
     * 模板列表查询
     *
     * @param cmsTemplateListQuery 模板列表查询条件
     * @return 模板分页查询结果
     */
    @Override
    public List<CmsTemplateListVo> listOfTemplate(CmsTemplateListQuery cmsTemplateListQuery) {
        CmsTemplateQuery cmsTemplateQuery = new CmsTemplateQuery();
        BeanUtil.copyProperties(cmsTemplateListQuery, cmsTemplateQuery);
        cmsTemplateQuery.setBusinessType(CmsTemplate.class.getSimpleName());
        List<CmsTemplateVo> list = cmsTemplateMapper.getList(cmsTemplateQuery);
        return reorganization(list);
    }

    private List<CmsTemplateListVo> reorganization(List<CmsTemplateVo> recordsTemp) {
        List<CmsTemplateListVo> records = new ArrayList<>();
        if (CollectionUtil.isEmpty(recordsTemp)) {
            return null;
        }
        CmsTemplateListVo cmsTemplateListVo;
        for (CmsTemplateVo cms : recordsTemp) {
            cmsTemplateListVo = new CmsTemplateListVo();
            cmsTemplateListVo.setTemplateId(cms.getTemplateId());
            cmsTemplateListVo.setTemplateName(cms.getTemplateName());
            cmsTemplateListVo.setTemplateImg(cms.getFileUrl() + "/" + FileConfig.getSample());
            records.add(cmsTemplateListVo);
        }
        return records;
    }

    /**
     * 获取模板路径
     * @param templateId 模板id
     * @return 模板路径
     */
    @Override
    public String getTemplateUrl(Long templateId) {
        return cmsTemplateMapper.getTemplateUrl(templateId);
    }
}
