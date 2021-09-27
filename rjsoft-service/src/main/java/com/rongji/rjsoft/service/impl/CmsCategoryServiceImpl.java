package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsCategoryAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsCategory;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsCategoryMapper;
import com.rongji.rjsoft.query.content.CmsCategoryQuery;
import com.rongji.rjsoft.service.ICmsCategoryService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsCategorySimpleVo;
import com.rongji.rjsoft.vo.content.CmsCategoryVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 文章类别信息表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-15
 */
@Service
@AllArgsConstructor
public class CmsCategoryServiceImpl extends ServiceImpl<CmsCategoryMapper, CmsCategory> implements ICmsCategoryService {

    private final CmsCategoryMapper cmsCategoryMapper;

    /**
     * 添加文章类别
     *
     * @param cmsCategoryAo 文章类别表单
     * @return 添加结果
     */
    @Override
    public boolean add(CmsCategoryAo cmsCategoryAo) {
        CmsCategory category = getCmsCategory(cmsCategoryAo);
        if (null != category) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "系统中已存在该类别!");
        }
        CmsCategory cmsCategory = new CmsCategory();
        BeanUtil.copyProperties(cmsCategoryAo, cmsCategory);
        return cmsCategoryMapper.insert(cmsCategory) > 0;
    }

    private CmsCategory getCmsCategory(CmsCategoryAo cmsCategoryAo) {
        LambdaQueryWrapper<CmsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsCategory::getCategoryName, cmsCategoryAo.getCategoryName()).last(" limit 0, 1");
        CmsCategory category = cmsCategoryMapper.selectOne(wrapper);
        return category;
    }

    /**
     * 编辑文章类别
     *
     * @param cmsCategoryAo 文章类别表单
     * @return 编辑结果
     */
    @Override
    public boolean edit(CmsCategoryAo cmsCategoryAo) {
        CmsCategory category = getCmsCategory(cmsCategoryAo);
        if (null != category && category.getCategoryId().longValue() != cmsCategoryAo.getCategoryId().longValue()) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "系统中已存在该类别!");
        }
        CmsCategory cmsCategory = new CmsCategory();
        BeanUtil.copyProperties(cmsCategoryAo, cmsCategory);
        return cmsCategoryMapper.updateById(cmsCategory) > 0;
    }

    /**
     * 删除文章类别
     *
     * @param categoryId 文章类别id
     * @return 删除结果
     */
    @Override
    public boolean delete(Long[] categoryId) {
        return cmsCategoryMapper.deleteBatchIds(Arrays.asList(categoryId)) > 0;
    }

    /**
     * 禁用文章类别
     *
     * @param categoryId 文章类别id
     * @return 禁用结果
     */
    @Override
    public boolean disable(Long[] categoryId) {
        return cmsCategoryMapper.updateStatus(categoryId);
    }

    /**
     * 文章类别分页结果
     *
     * @param cmsCategoryQuery 查询对象
     * @return 文章类别分页结果
     */
    @Override
    public CommonPage<CmsCategoryVo> pageList(CmsCategoryQuery cmsCategoryQuery) {
        IPage<CmsCategoryVo> page = new Page<>(cmsCategoryQuery.getCurrent(), cmsCategoryQuery.getPageSize());
        page = cmsCategoryMapper.getPages(page, cmsCategoryQuery);
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 文章类别信息列表
     * @param categoryName 类别名
     * @return 文章类别信息列表
     */
    @Override
    public List<CmsCategorySimpleVo> listOfCategory(String categoryName) {
        return cmsCategoryMapper.listOfCategory(categoryName);
    }
}
