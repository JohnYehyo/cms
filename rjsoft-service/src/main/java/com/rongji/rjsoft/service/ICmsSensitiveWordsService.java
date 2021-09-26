package com.rongji.rjsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rongji.rjsoft.ao.content.CmsSensitiveWordsAo;
import com.rongji.rjsoft.entity.content.CmsSensitiveWords;
import com.rongji.rjsoft.query.content.CmsSensitiveWordsQuery;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSensitiveWordsVo;

import java.util.List;

/**
 * <p>
 * 敏感词 服务类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-26
 */
public interface ICmsSensitiveWordsService extends IService<CmsSensitiveWords> {

    /**
     * 添加敏感词
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 添加结果
     */
    boolean saveWord(CmsSensitiveWordsAo cmsSensitiveWordsAo);

    /**
     * 编辑敏感词
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 编辑结果
     */
    boolean updateWord(CmsSensitiveWordsAo cmsSensitiveWordsAo);

    /**
     * 删除敏感词
     * @param wordId 敏感词id
     * @return 删除结果
     */
    boolean deleteWords(Long[] wordId);

    /**
     * 敏感词列表
     * @param cmsSensitiveWordsQuery 查询对象
     * @return 敏感词列表
     */
    CommonPage<CmsSensitiveWordsVo> listOfWords(CmsSensitiveWordsQuery cmsSensitiveWordsQuery);

    /**
     * 批量添加敏感词
     * @param fileUrl 文件地址
     * @return 添加结果
     */
    boolean batchSaveWords(String fileUrl);

    /**
     * 刷新关键词缓存
     */
    List<String> refreshCache();
}
