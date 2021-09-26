package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rongji.rjsoft.ao.content.CmsSensitiveWordsAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsSensitiveWords;
import com.rongji.rjsoft.mapper.CmsSensitiveWordsMapper;
import com.rongji.rjsoft.mapper.CmsSiteColumnMapper;
import com.rongji.rjsoft.query.content.CmsSensitiveWordsQuery;
import com.rongji.rjsoft.service.ICmsSensitiveWordsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSensitiveWordsVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * <p>
 * 敏感词 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-26
 */
@Service
@AllArgsConstructor
public class CmsSensitiveWordsServiceImpl extends ServiceImpl<CmsSensitiveWordsMapper, CmsSensitiveWords> implements ICmsSensitiveWordsService {

    private final CmsSensitiveWordsMapper cmsSensitiveWordsMapper;

    /**
     * 添加敏感词
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 添加结果
     */
    @Override
    public boolean saveWord(CmsSensitiveWordsAo cmsSensitiveWordsAo) {
        CmsSensitiveWords cmsSensitiveWords = new CmsSensitiveWords();
        BeanUtil.copyProperties(cmsSensitiveWordsAo, cmsSensitiveWords);
        return cmsSensitiveWordsMapper.insert(cmsSensitiveWords) > 0;
    }

    /**
     * 编辑敏感词
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 编辑结果
     */
    @Override
    public boolean updateWord(CmsSensitiveWordsAo cmsSensitiveWordsAo) {
        CmsSensitiveWords cmsSensitiveWords = new CmsSensitiveWords();
        BeanUtil.copyProperties(cmsSensitiveWordsAo, cmsSensitiveWords);
        return cmsSensitiveWordsMapper.updateById(cmsSensitiveWords) > 0;
    }

    /**
     * 删除敏感词
     * @param wordId 敏感词id
     * @return 删除结果
     */
    @Override
    public boolean deleteWords(Long[] wordId) {
        return cmsSensitiveWordsMapper.deleteWords(wordId) > 0;
    }

    /**
     * 敏感词列表
     * @param cmsSensitiveWordsQuery 查询对象
     * @return 敏感词列表
     */
    @Override
    public CommonPage<CmsSensitiveWordsVo> listOfWords(CmsSensitiveWordsQuery cmsSensitiveWordsQuery) {
        IPage<CmsSensitiveWordsVo> page = new Page<>();
        page = cmsSensitiveWordsMapper.getPages(page, cmsSensitiveWordsQuery);
        return CommonPageUtils.assemblyPage(page);
    }
}
