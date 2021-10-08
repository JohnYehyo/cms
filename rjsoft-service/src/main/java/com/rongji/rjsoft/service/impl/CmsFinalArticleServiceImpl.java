package com.rongji.rjsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.common.config.FileConfig;
import com.rongji.rjsoft.common.util.LogUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import com.rongji.rjsoft.entity.content.CmsSite;
import com.rongji.rjsoft.enums.DelFlagEnum;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsFinalArticleMapper;
import com.rongji.rjsoft.mapper.CmsSiteMapper;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.rongji.rjsoft.service.ICmsSiteService;
import com.rongji.rjsoft.vo.content.CmsArticleContentVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 生成门户页
     *
     * @return
     */
    @Override
    public boolean generate() {
        return false;
    }

    /**
     * 生成文章
     *
     * @return
     */
    @Override
    public void generateHtml() {
        if (StringUtils.isBlank(FileConfig.getFolder())) {
            throw new IllegalArgumentException("请先在Yml配置静态页面生成路径");
        }
        List<CmsArticleContentVo> articles = cmsFinalArticleMapper.getPublishArticel();
        Map<String, String> map = redisCache.existsHash(Constants.SITE_DICT);
        if (null == map || map.size() == 0) {
            cmsSiteService.refreshCache();
            map = redisCache.existsHash(Constants.SITE_DICT);
            if(null == map || map.size() == 0){
                throw new BusinessException(ResponseEnum.CANT_GET_SITES);
            }
        }
        for (CmsArticleContentVo cmsArticleContentVo : articles) {
            cmsArticleContentVo.setSiteFile(map.get(String.valueOf(cmsArticleContentVo.getSiteId())));
            createArticle(cmsArticleContentVo);
        }
    }

    private void createArticle(CmsArticleContentVo article) {
        Map<String, Object> map;
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

        map = new HashMap<>();
        map.put("article", article);

        Context context = new Context();
        context.setVariables(map);

        try (PrintWriter writer = new
                PrintWriter(file)) {
            // 执行页面静态化方法
            templateEngine.process(article.getSiteFile() + File.separator + Constants.FILES_TEMPLATE_NAME, context, writer);
            LogUtils.info("生成静态页面: {}成功", articleUrl);
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.STATIC_HTML_EXCEPTION.getCode(), e.getMessage());
        }
    }
}
