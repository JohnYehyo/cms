package com.rongji.rjsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.mapper.CmsColumnDeptMapper;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.service.ICmsColumnDeptService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsColumnDeptVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 栏目部门表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-10-26
 */
@Service
@AllArgsConstructor
public class CmsColumnDeptServiceImpl extends ServiceImpl<CmsColumnDeptMapper, CmsColumnDept> implements ICmsColumnDeptService {

    private final CmsColumnDeptMapper cmsColumnDeptMapper;

    /**
     * 添加栏目部门关系
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(CmsColumnDeptAo cmsColumnDeptAo) {
        CmsColumnDept cmsColumnDept = new CmsColumnDept();
        BeanUtil.copyProperties(cmsColumnDeptAo, cmsColumnDept);
        return cmsColumnDeptMapper.insert(cmsColumnDept) > 0;
    }

    /**
     * 更新栏目部门关系
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(CmsColumnDeptAo cmsColumnDeptAo) {
        LambdaUpdateWrapper<CmsColumnDept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsColumnDept::getColumnId, cmsColumnDeptAo.getColumnId());
        cmsColumnDeptMapper.delete(wrapper);
        CmsColumnDept cmsColumnDept = new CmsColumnDept();
        BeanUtil.copyProperties(cmsColumnDeptAo, cmsColumnDept);
        return cmsColumnDeptMapper.insert(cmsColumnDept) > 0;
    }

    /**
     * 删除栏目部门关系
     * @param columnIds 栏目id集合
     * @return 删除结果
     */
    @Override
    public boolean delete(Long[] columnIds) {
        return cmsColumnDeptMapper.deleteBatch(columnIds) > 0;
    }

    /**
     * 栏目部门关系分页查询
     * @param cmsColumnDeptQuery 查询对象
     * @return
     */
    @Override
    public CommonPage<CmsColumnDeptVo> getPage(CmsColumnDeptQuery cmsColumnDeptQuery) {
        IPage<CmsColumnDeptVo> page = new Page<>();
        page = cmsColumnDeptMapper.getPage(page, cmsColumnDeptQuery);
        return CommonPageUtils.assemblyPage(page);
    }
}
