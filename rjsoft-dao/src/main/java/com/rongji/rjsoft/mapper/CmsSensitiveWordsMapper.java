package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rongji.rjsoft.entity.content.CmsSensitiveWords;
import com.rongji.rjsoft.query.content.CmsSensitiveWordsQuery;
import com.rongji.rjsoft.vo.content.CmsSensitiveWordsVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 敏感词 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-26
 */
public interface CmsSensitiveWordsMapper extends BaseMapper<CmsSensitiveWords> {

    /**
     * 删除敏感词
     * @param wordId 敏感词id
     * @return 删除结果
     */
    int deleteWords(Long[] wordId);

    /**
     * 敏感词列表
     * @param page 分页对象
     * @param cmsSensitiveWordsQuery 查询对象
     * @return 敏感词列表
     */
    IPage<CmsSensitiveWordsVo> getPages(IPage<CmsSensitiveWordsVo> page, @Param("param") CmsSensitiveWordsQuery cmsSensitiveWordsQuery);


    /**
     * 批量添加敏感词
     * @param words
     * @return
     */
    boolean batchSaveWords(String[] words);
}
