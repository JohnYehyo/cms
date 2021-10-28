package com.rongji.rjsoft.constants;

import java.nio.charset.Charset;

/**
 * @description: 常量
 * @author: JohnYehyo
 * @create: 2021-04-26 09:47:01
 */
public class Constants {

    /**
     * 默认初始密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 当前用户key
     */
    public static final String CURRENT_USER = "currentUser";

    /**
     * 编码
     */
    public static final Charset DEFAULT_ENCODE = Charset.forName("UTF-8");

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 登录用户令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     *  管理员id
     */
    public static final Long ADMIN_ID = 1L;

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 1;

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/file";

    /**
     * 附件默认路径
     */
    public static final String DEFAULT_PATH = "/common";

    /**
     * 内容管理员
     */
    public static final String CMS_ADMIN = "cms_admin";

    /**
     * 文章管理员
     */
    public static final String ARTICLE_ADMIN = "article_admin";

    /**
     * 文章审核管理员
     */
    public static final String ARTICLE_AUDIT_ADMIN = "article_audit_admin";

    /**
     * 敏感词
     */
    public static final String SENSITIVE_WORDS = "sensitive_words";

    /**
     * 文章生成目录
     */
    public static final String FILES_DESC_URL = "article";

    /**
     * 文章模板名
     */
    public static final String FILES_TEMPLATE_NAME = "article";

    /**
     * 站点 redis key
     */
    public static final String SITE_DICT = "site:dict";

    /**
     *  栏目 redis key
     */
    public static final String COLUMN_DICT = "column:dict";

    /**
     * 栏目缓存信息-模板后缀
     */
    public static final String COLUMN_DICT_TEMPLATE = "_template";
}
