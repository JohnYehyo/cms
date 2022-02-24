package com.rongji.rjsoft.vo.content;

import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @description: 文章引用视图
 * @author: JohnYehyo
 * @create: 2021-09-26 14:46:10
 */
@Data
public class CmsArticleRefVo extends CmsFinalArticle implements Serializable {

    private static final long serialVersionUID = -6290652088857072484L;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 栏目名称
     */
    private String columnName;

}
