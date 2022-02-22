package com.rongji.rjsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsSiteDeptAo;
import com.rongji.rjsoft.common.security.util.TokenUtils;
import com.rongji.rjsoft.common.util.CommonPageUtils;
import com.rongji.rjsoft.common.util.ServletUtils;
import com.rongji.rjsoft.entity.content.CmsSiteDept;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsSiteDeptMapper;
import com.rongji.rjsoft.mapper.SysDeptMapper;
import com.rongji.rjsoft.query.content.CmsSiteColumnQuery;
import com.rongji.rjsoft.query.content.CmsSiteDeptQuery;
import com.rongji.rjsoft.service.ICmsSiteDeptService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsSiteColumnDeptVo;
import com.rongji.rjsoft.vo.content.CmsSiteDeptVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 站点部门管理关系表 服务实现类
 * </p>
 *
 * @author JohnYehyo
 * @since 2022-02-22
 */
@Service
@AllArgsConstructor
public class CmsSiteDeptServiceImpl extends ServiceImpl<CmsSiteDeptMapper, CmsSiteDept> implements ICmsSiteDeptService {


    private final CmsSiteDeptMapper cmsSiteDeptMapper;

    private final TokenUtils tokenUtils;

    private final SysDeptMapper sysDeptMapper;

    /**
     * 添加站点部门关系
     * @param cmsSiteDeptAo 站点部门关系参数体
     * @return 添加结果
     */
    @Override
    public boolean add(CmsSiteDeptAo cmsSiteDeptAo) {
        if (null == cmsSiteDeptAo || null == cmsSiteDeptAo.getDeptId()) {
            throw new BusinessException(ResponseEnum.FAIL);
        }
        LambdaQueryWrapper<CmsSiteDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsSiteDept::getSiteId, cmsSiteDeptAo.getSiteId())
                .in(CmsSiteDept::getDeptId, cmsSiteDeptAo.getDeptId())
                .last(" limit 0, 1");
        Integer count = cmsSiteDeptMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "记录中已存在该栏目部门关系,请核实后重新建立!");
        }
        return insertRecords(cmsSiteDeptAo);
    }

    private boolean insertRecords(CmsSiteDeptAo cmsSiteDeptAo) {
        List<CmsSiteDept> list = new ArrayList<>();
        CmsSiteDept cmsSiteDept;
        for (Long deptId : cmsSiteDeptAo.getDeptId()) {
            cmsSiteDept = new CmsSiteDept();
            cmsSiteDept.setSiteId(cmsSiteDeptAo.getSiteId());
            cmsSiteDept.setDeptId(deptId);
            list.add(cmsSiteDept);
        }
        return cmsSiteDeptMapper.batchInsert(list) > 0;
    }

    /**
     * 更新站点部门关系
     * @param cmsSiteDeptAo 站点部门关系参数体
     * @return 更新结果
     */
    @Override
    public boolean edit(CmsSiteDeptAo cmsSiteDeptAo) {
        LambdaUpdateWrapper<CmsSiteDept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsSiteDept::getSiteId, cmsSiteDeptAo.getSiteId());
        cmsSiteDeptMapper.delete(wrapper);
        return insertRecords(cmsSiteDeptAo);
    }

    /**
     * 删除站点部门关系
     * @param siteIds 站点id几个
     * @return 删除结果
     */
    @Override
    public boolean delete(Long[] siteIds) {
        return cmsSiteDeptMapper.deleteBatch(siteIds) > 0;
    }

    /**
     * 站点部门关系分页查询
     * @param cmsSiteDeptQuery 站点部门关系分页查询对象
     * @return 分页结果
     */
    @Override
    public Object getPage(CmsSiteDeptQuery cmsSiteDeptQuery) {
        //查询所有下属部门
        if (null == cmsSiteDeptQuery.getDeptId()) {
            cmsSiteDeptQuery.setDeptId(tokenUtils.getLoginUser(ServletUtils.getRequest()).getSysDept().getDeptId());
        }
        List<Long> deptIds = sysDeptMapper.selectDeptIdsByDeptId(cmsSiteDeptQuery.getDeptId());
        deptIds.add(cmsSiteDeptQuery.getDeptId());

        //处理栏目部门关系列表数据
//        List<CmsSiteDeptVo> cmsSiteDeptVos = dataProcessing(cmsSiteDeptQuery, deptIds);
        List<CmsSiteDeptVo> cmsSiteDeptVos = null;

        //组装分页
        return assemblyPage(cmsSiteDeptQuery, cmsSiteDeptVos);
    }


    private CommonPage<CmsSiteDeptVo> assemblyPage(CmsSiteDeptQuery cmsSiteDeptQuery, List<CmsSiteDeptVo> cmsSiteDeptVos) {
        CommonPage<CmsSiteDeptVo> result = new CommonPage<>();
        int size = cmsSiteDeptVos.size();
        int totalPage = size / (cmsSiteDeptQuery.getPageSize()) > 0 ? size / (cmsSiteDeptQuery.getPageSize()) : size / (cmsSiteDeptQuery.getPageSize()) + 1;
        result.setTotalPage((long) totalPage);
        result.setTotal((long) cmsSiteDeptVos.size());
        result.setCurrent(cmsSiteDeptQuery.getCurrent().longValue());
        result.setPageSize(cmsSiteDeptQuery.getPageSize().longValue());
        result.setList(cmsSiteDeptVos);
        return result;
    }

    /**
     * 通过站点获取部门同步树
     * @param siteId 站点id
     * @return 部门信息
     */
    @Override
    public Object allDeptTree(Long siteId) {
        return null;
    }

    /**
     * 通过站点id查询授权部门
     * @param cmsSiteColumnQuery 查询条件
     * @return 授权关系分页
     */
    @Override
    public CommonPage<CmsSiteColumnDeptVo> getPageBySiteId(CmsSiteColumnQuery cmsSiteColumnQuery) {
        IPage<CmsSiteColumnDeptVo> page = new Page<>(cmsSiteColumnQuery.getCurrent(), cmsSiteColumnQuery.getPageSize());
        page = cmsSiteDeptMapper.getPageBySiteId(page, cmsSiteColumnQuery.getSiteId());
        return CommonPageUtils.assemblyPage(page);
    }

    /**
     * 删除站点部门关系
     * @param cmsSiteDeptAo 参数体
     * @return 删除结果
     */
    @Override
    public boolean deleteRelation(CmsSiteDeptAo cmsSiteDeptAo) {
        return cmsSiteDeptMapper.deleteRelation(cmsSiteDeptAo.getSiteId(), cmsSiteDeptAo.getDeptId());
    }
}
