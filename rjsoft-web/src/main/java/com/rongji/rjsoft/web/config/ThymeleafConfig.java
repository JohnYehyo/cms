package com.rongji.rjsoft.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

/**
 * @description: thymeleaf配置
 * @author: JohnYehyo
 * @create: 2021-10-28 15:03:22
 */
@Configuration
public class ThymeleafConfig {

    @Value("${spring.thymeleaf.url.prefix}")
    private String urlPrefix;

    @Bean
    public ITemplateResolver iTemplateResolver(){
        UrlTemplateResolver resolver = new UrlTemplateResolver();
        resolver.setPrefix(urlPrefix);
        return resolver;
    }

}