package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsSensitiveWordsAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.LogUtils;
import com.rongji.rjsoft.common.util.RedisCache;
import com.rongji.rjsoft.constants.Constants;
import com.rongji.rjsoft.entity.content.CmsSensitiveWords;
import com.rongji.rjsoft.mapper.CmsSensitiveWordsMapper;
import com.rongji.rjsoft.query.content.CmsSensitiveWordsQuery;
import com.rongji.rjsoft.service.ICmsSensitiveWordsService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSensitiveWordsVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    private final RedisCache redisCache;

    /**
     * 添加敏感词
     *
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 添加结果
     */
    @Override
    public boolean saveWord(CmsSensitiveWordsAo cmsSensitiveWordsAo) {
        CmsSensitiveWords cmsSensitiveWords = new CmsSensitiveWords();
        BeanUtil.copyProperties(cmsSensitiveWordsAo, cmsSensitiveWords);
        boolean result = cmsSensitiveWordsMapper.insert(cmsSensitiveWords) > 0;
        if(result){
            ThreadUtil.execute(() -> refreshCache());
        }
        return result;
    }

    /**
     * 编辑敏感词
     *
     * @param cmsSensitiveWordsAo 敏感词信息
     * @return 编辑结果
     */
    @Override
    public boolean updateWord(CmsSensitiveWordsAo cmsSensitiveWordsAo) {
        CmsSensitiveWords cmsSensitiveWords = new CmsSensitiveWords();
        BeanUtil.copyProperties(cmsSensitiveWordsAo, cmsSensitiveWords);
        boolean result = cmsSensitiveWordsMapper.updateById(cmsSensitiveWords) > 0;
        if(result){
            ThreadUtil.execute(() -> refreshCache());
        }
        return result;
    }

    /**
     * 删除敏感词
     *
     * @param wordId 敏感词id
     * @return 删除结果
     */
    @Override
    public boolean deleteWords(Long[] wordId) {
        boolean result = cmsSensitiveWordsMapper.deleteWords(wordId) > 0;
        ThreadUtil.execute(() -> refreshCache());
        return result;
    }

    /**
     * 敏感词列表
     *
     * @param cmsSensitiveWordsQuery 查询对象
     * @return 敏感词列表
     */
    @Override
    public CommonPage<CmsSensitiveWordsVo> listOfWords(CmsSensitiveWordsQuery cmsSensitiveWordsQuery) {
        IPage<CmsSensitiveWordsVo> page = new Page<>();
        page = cmsSensitiveWordsMapper.getPages(page, cmsSensitiveWordsQuery);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 批量添加敏感词
     *
     * @param fileUrl 文件地址
     * @return 添加结果
     */
    @Override
    public boolean batchSaveWords(String fileUrl) {
        boolean result = false;
        File file = new File(fileUrl);
        StringBuffer sbf = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            String[] words = sbf.toString().replace(" ", "").replace("\"", "").split(",");
            result = cmsSensitiveWordsMapper.batchSaveWords(words);
        } catch (IOException e) {
            LogUtils.error("批量添加敏感词失败:{}", e.getMessage(), e);
        }
        ThreadUtil.execute(() -> refreshCache());
        return result;
    }

    @Override
    public List<String> refreshCache() {
        LambdaQueryWrapper<CmsSensitiveWords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSensitiveWords::getDelFlag, 0);
        List<CmsSensitiveWords> cmsSensitiveWords = cmsSensitiveWordsMapper.selectList(wrapper);
        List<String> list = cmsSensitiveWords.stream().map(k -> k.getWord()).collect(Collectors.toList());
        redisCache.setCacheList(Constants.SENSITIVE_WORDS, list);
        return list;
    }
}
