package com.rongji.rjsoft.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {

    @Value("${file.path}")
    private void setPath(String path){
        FileConfig.path = path;
    }

    @Value("${file.avator}")
    private void setAvator(String avator){
        FileConfig.avator = avator;
    }

    @Value("${file.download}")
    private void setDownload(String download){
        FileConfig.download = download;
    }

    @Value("${file.upload}")
    private void setUpload(String upload){
        FileConfig.upload = upload;
    }

    @Value("${file.static.folder}")
    private void setFolder(String folder){
        FileConfig.folder = folder;
    }

    @Value("${file.template.site}")
    private void setSiteTemplate(String siteTemplate){
        FileConfig.siteTemplate = siteTemplate;
    }

    @Value("${file.template.list}")
    private void setListTemplate(String listTemplate){
        FileConfig.listTemplate = listTemplate;
    }

    @Value("${file.template.article}")
    private void setArticleTemplate(String articleTemplate){
        FileConfig.articleTemplate = articleTemplate;
    }

    @Value("${file.template.sample}")
    private void setSample(String sample){
        FileConfig.sample = sample;
    }



    /**
     * 上传路径
     */
    private static String path;

    /**
     * 头像上传
     */
    private static String avator;

    /**
     * 下载
     */
    private static String download;

    /**
     * 上传
     */
    private static String upload;

    /**
     * 静态页
     */
    private static String folder;

    /**
     * 站点模板
     */
    private static String siteTemplate;

    /**
     * 列表模板
     */
    private static String listTemplate;

    /**
     * 文章模板
     */
    private static String articleTemplate;

    /**
     * 缩略图
     */
    private static String sample;

    public static String getPath() {
        return path;
    }

    public static String getAvator() {
        return avator;
    }

    public static String getDownload() {
        return download;
    }

    public static String getUpload() {
        return upload;
    }

    public static String getFolder() {
        return folder;
    }

    public static String getSiteTemplate() {
        return siteTemplate;
    }

    public static String getListTemplate() {
        return listTemplate;
    }

    public static String getArticleTemplate() {
        return articleTemplate;
    }

    public static String getSample() {
        return sample;
    }
}
