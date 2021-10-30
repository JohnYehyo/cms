package com.rongji.rjsoft.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.common.config.FileConfig;
import com.rongji.rjsoft.common.util.LogUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.enums.TableFileTypeEnum;
import com.rongji.rjsoft.enums.TableTypeEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsFinalArticleMapper;
import com.rongji.rjsoft.query.common.SysCommonFileQuery;
import com.rongji.rjsoft.service.ICmsColumnService;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.service.ISysCommonFileService;
import com.rongji.rjsoft.vo.common.SysCommonFileVo;
import com.rongji.rjsoft.vo.content.CmsArticleContentVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
@Service
@AllArgsConstructor
public class CmsFinalArticleServiceImpl extends ServiceImpl<CmsFinalArticleMapper, CmsFinalArticle> implements ICmsFinalArticleService {

    private final CmsFinalArticleMapper cmsFinalArticleMapper;

    private final TemplateEngine templateEngine;

    private final ICmsSiteService cmsSiteService;

    private final RedisCache redisCache;

    private final ICmsColumnService cmsColumnService;

    private final ISysCommonFileService sysCommonFileService;

    /**
     * 发布文章
     *
     * @return
     */
    @Override
    public void generateArticle() {
        if (StringUtils.isBlank(FileConfig.getFolder())) {
            LogUtils.error("请先在Yml配置静态页面生成路径");
            throw new IllegalArgumentException("请先在Yml配置静态页面生成路径");
        }

        //筛选待发布文章信息
        List<CmsArticleContentVo> articles =
                cmsFinalArticleMapper.getPublishArticel(DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        if(CollectionUtil.isEmpty(articles)){
            return;
        }

        //发布文章
        publishArticle(articles);

    }

    private void publishArticle(List<CmsArticleContentVo> articles) {
        //发布文章
        List<CmsArticleContentVo> publishedList = publishingArticles(articles);

        //更新发布标记
        if (CollectionUtil.isEmpty(publishedList)) {
            return;
        }
        cmsFinalArticleMapper.batchPublished(publishedList);
    }

    /**
     * 发布文章
     * @param articleId 文章id
     */
    @Override
    public void generateArticle(Long articleId) {
        if (StringUtils.isBlank(FileConfig.getFolder())) {
            LogUtils.error("请先在Yml配置静态页面生成路径");
            throw new IllegalArgumentException("请先在Yml配置静态页面生成路径");
        }

        //筛选待发布文章信息
        List<CmsArticleContentVo> articles =
                cmsFinalArticleMapper.getSingleCurrentPublishArticel(articleId);
        if(CollectionUtil.isEmpty(articles)){
            return;
        }

        //发布文章
        publishArticle(articles);
    }

    /**
     * 发布文章
     * @param articleIds 文章集合
     */
    @Override
    public void generateArticle(List<Long> articleIds) {
        if (StringUtils.isBlank(FileConfig.getFolder())) {
            LogUtils.error("请先在Yml配置静态页面生成路径");
            throw new IllegalArgumentException("请先在Yml配置静态页面生成路径");
        }

        //筛选待发布文章信息
        List<CmsArticleContentVo> articles =
                cmsFinalArticleMapper.getCurrentPublishArticel(articleIds);
        if(CollectionUtil.isEmpty(articles)){
            return;
        }

        //发布文章
        publishArticle(articles);
    }

    /**
     * 发布文章
     * @param articles 文章集合
     * @return
     */
    private List<CmsArticleContentVo> publishingArticles(List<CmsArticleContentVo> articles) {
        Map<String, String> siteMap = getSiteMap();
        Map<String, Object> columnMap = getColumnMap();

        List<CmsArticleContentVo> publishedList = new ArrayList<>();
        for (CmsArticleContentVo cmsArticleContentVo : articles) {
            cmsArticleContentVo.setSiteFile(siteMap.get(String.valueOf(cmsArticleContentVo.getSiteId())));
            cmsArticleContentVo.setTemplateId((Long) columnMap.get(cmsArticleContentVo.getColumnId() + Constants.COLUMN_DICT_TEMPLATE));
            createArticle(cmsArticleContentVo, publishedList);
        }
        return publishedList;
    }

    /**
     * 获取站点信息
     * @return 站点信息
     */
    private Map<String, String> getSiteMap() {
        Map<String, String> map = redisCache.existsHash(Constants.SITE_DICT);
        if (CollectionUtil.isEmpty(map)) {
            cmsSiteService.refreshCache();
            map = redisCache.existsHash(Constants.SITE_DICT);
            if(CollectionUtil.isEmpty(map)){
                throw new BusinessException(ResponseEnum.CANT_GET_SITES);
            }
        }
        return map;
    }

    /**
     * 获取栏目信息
     * @return 站点信息
     */
    private Map<String, Object> getColumnMap() {
        Map<String, Object> map = redisCache.existsHash(Constants.COLUMN_DICT);
        if (CollectionUtil.isEmpty(map)) {
            cmsColumnService.refreshCache();
            map = redisCache.existsHash(Constants.COLUMN_DICT);
            if(CollectionUtil.isEmpty(map)){
                throw new BusinessException(ResponseEnum.CANT_GET_COLUMNS);
            }
        }
        return map;
    }

    private void createArticle(CmsArticleContentVo article, List<CmsArticleContentVo> publishedList) {

        String articleUrl = article.getArticleUrl();
        //文件生成路径
        String templateUrl = StringUtils.removeStart(Constants.FILES_DESC_URL, File.separator);
        LogUtils.info("开始生成静态页面: {}", articleUrl);
        String fileUrl = StringUtils.appendIfMissing(FileConfig.getFolder(), File.separator) +
                templateUrl + File.separator + article.getSiteFile() + File.separator + articleUrl + ".html";
        // 自动创建上层文件夹
        File directory = new File(StringUtils.substringBeforeLast(fileUrl, File.separator));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(fileUrl);

        Context context = new Context();
        Map<String, Object> map;
        map = new HashMap<>();
        map.put("article", article);
        context.setVariables(map);

        //获取模板
        String template = getTemplate(article);

        try (PrintWriter writer = new
                PrintWriter(file)) {
            // 执行页面静态化方法
            templateEngine.process(template, context, writer);
            publishedList.add(article);
            LogUtils.info("生成静态页面: {}成功", articleUrl);
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.STATIC_HTML_EXCEPTION.getCode(), e.getMessage());
        }
    }

    private String getTemplate(CmsArticleContentVo article) {
        SysCommonFileQuery sysCommonFileQuery = new SysCommonFileQuery();
        sysCommonFileQuery.setTableId(article.getTemplateId());
        sysCommonFileQuery.setTableType(TableTypeEnum.CMS_TEMPLATE.getValue());
        sysCommonFileQuery.setFileType(TableFileTypeEnum.TEMPLATE_HTML_ARTICLE.getCode());
        List<SysCommonFileVo> files = sysCommonFileService.getFiles(sysCommonFileQuery);
        if(CollectionUtil.isEmpty(files)){
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "系统未查询到该模板");
        }
        return files.get(0).getFileUrl();
    }
}
