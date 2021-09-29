package com.rongji.rjsoft.service.impl;

import com.rongji.rjsoft.entity.content.CmsFinalArticle;
import com.rongji.rjsoft.mapper.CmsFinalArticleMapper;
import com.rongji.rjsoft.service.ICmsFinalArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
@Service
@AllArgsConstructor
public class CmsFinalArticleServiceImpl extends ServiceImpl<CmsFinalArticleMapper, CmsFinalArticle> implements ICmsFinalArticleService {

    private final CmsFinalArticleMapper cmsFinalArticleMapper;

    /**
     * 生成门户页
     * @return
     */
    @Override
    public boolean generate() {
        return false;
    }
}
