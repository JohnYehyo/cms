package com.rongji.rjsoft.web.job;

import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @description: cms定时任务
 * @author: JohnYehyo
 * @create: 2021-10-08 10:54:58
 */
@Component
@AllArgsConstructor
public class CmsJobController {

    private final ICmsFinalArticleService cmsFinalArticleService;


    /**
     * 生成静态文章
     * @throws Exception
     */
    @XxlJob("createArticle")
    public void createArticle() throws Exception {
        XxlJobHelper.log("cms定时任务, 生成静态文章");
        cmsFinalArticleService.generateArticle();
    }
}
