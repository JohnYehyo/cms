package com.rongji.rjsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rongji.rjsoft.ao.content.CmsColumnDeptAo;
import com.rongji.rjsoft.common.security.util.TokenUtils;
import com.rongji.rjsoft.common.util.ServletUtils;
import com.rongji.rjsoft.entity.content.CmsColumnDept;
import com.rongji.rjsoft.enums.ResponseEnum;
import com.rongji.rjsoft.exception.BusinessException;
import com.rongji.rjsoft.mapper.CmsColumnDeptMapper;
import com.rongji.rjsoft.mapper.SysDeptMapper;
import com.rongji.rjsoft.query.content.CmsColumnDeptQuery;
import com.rongji.rjsoft.service.ICmsColumnDeptService;
import com.rongji.rjsoft.vo.CommonPage;
import com.rongji.rjsoft.vo.content.CmsColumnDeptVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final TokenUtils tokenUtils;

    private final SysDeptMapper sysDeptMapper;

    /**
     * 添加栏目部门关系
     *
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(CmsColumnDeptAo cmsColumnDeptAo) {
        if (null == cmsColumnDeptAo || null == cmsColumnDeptAo.getDeptId()) {
            throw new BusinessException(ResponseEnum.FAIL);
        }
        LambdaQueryWrapper<CmsColumnDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsColumnDept::getColumnId, cmsColumnDeptAo.getColumnId())
                .in(CmsColumnDept::getDeptId, cmsColumnDeptAo.getDeptId())
                .last(" limit 0, 1");
        CmsColumnDept cmsColumnDept = cmsColumnDeptMapper.selectOne(wrapper);
        if (null != cmsColumnDept) {
            throw new BusinessException(ResponseEnum.FAIL.getCode(), "记录中已存在该栏目部门关系,请核实后重新建立!");
        }
        return insertRecords(cmsColumnDeptAo);
    }

    private boolean insertRecords(CmsColumnDeptAo cmsColumnDeptAo) {
        List<CmsColumnDept> list = new ArrayList<>();
        CmsColumnDept cmsColumnDept;
        for (Long deptId : cmsColumnDeptAo.getDeptId()) {
            cmsColumnDept = new CmsColumnDept();
            cmsColumnDept.setColumnId(cmsColumnDeptAo.getColumnId());
            cmsColumnDept.setDeptId(deptId);
            list.add(cmsColumnDept);
        }
        return cmsColumnDeptMapper.batchInsert(list) > 0;
    }

    /**
     * 更新栏目部门关系
     *
     * @param cmsColumnDeptAo 栏目部门关系参数体
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(CmsColumnDeptAo cmsColumnDeptAo) {
        LambdaUpdateWrapper<CmsColumnDept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CmsColumnDept::getColumnId, cmsColumnDeptAo.getColumnId());
        cmsColumnDeptMapper.delete(wrapper);
        return insertRecords(cmsColumnDeptAo);
    }

    /**
     * 删除栏目部门关系
     *
     * @param columnIds 栏目id集合
     * @return 删除结果
     */
    @Override
    public boolean delete(Long[] columnIds) {
        return cmsColumnDeptMapper.deleteBatch(columnIds) > 0;
    }

    /**
     * 栏目部门关系分页查询
     *
     * @param cmsColumnDeptQuery 查询对象
     * @return
     */
    @Override
    public CommonPage<CmsColumnDeptVo> getPage(CmsColumnDeptQuery cmsColumnDeptQuery) {
        //查询所有下属部门
        if (null == cmsColumnDeptQuery.getDeptId()) {
            cmsColumnDeptQuery.setDeptId(tokenUtils.getLoginUser(ServletUtils.getRequest()).getSysDept().getDeptId());
        }
        List<Long> deptIds = sysDeptMapper.selectDeptIdsByDeptId(cmsColumnDeptQuery.getDeptId());
        deptIds.add(cmsColumnDeptQuery.getDeptId());

        //处理栏目部门关系列表数据
        List<CmsColumnDeptVo> CmsColumnDeptVos = dataProcessing(cmsColumnDeptQuery, deptIds);

        //组装分页
        return assemblyPage(cmsColumnDeptQuery, CmsColumnDeptVos);
    }

    private List<CmsColumnDeptVo> dataProcessing(CmsColumnDeptQuery cmsColumnDeptQuery, List<Long> deptIds) {
        List<CmsColumnDeptVo> list = cmsColumnDeptMapper.getList(cmsColumnDeptQuery, deptIds);
        Map<Long, List<CmsColumnDeptVo>> map = list.stream().collect(Collectors.groupingBy(CmsColumnDeptVo::getColumnId));
        List<CmsColumnDeptVo> CmsColumnDeptVos = new ArrayList<>();
        map.forEach((k, v) -> {
            List<Long> deptIdList = new ArrayList<>();
            CmsColumnDeptVo ccdVo = new CmsColumnDeptVo();
            for (CmsColumnDeptVo cmsColumnDeptVo: v) {
                ccdVo.setColumnId(cmsColumnDeptVo.getColumnId());
                ccdVo.setColumnName(cmsColumnDeptVo.getColumnName());
                ccdVo.setSiteName(cmsColumnDeptVo.getSiteName());
                deptIdList.add(cmsColumnDeptVo.getDeptId());
            }
            ccdVo.setDeptIds(deptIdList);
            CmsColumnDeptVos.add(ccdVo);
        });
        return CmsColumnDeptVos;
    }

    private CommonPage<CmsColumnDeptVo> assemblyPage(CmsColumnDeptQuery cmsColumnDeptQuery, List<CmsColumnDeptVo> CmsColumnDeptVos) {
        CommonPage<CmsColumnDeptVo> result = new CommonPage<>();
        int size = CmsColumnDeptVos.size();
        int totalPage = size / (cmsColumnDeptQuery.getPageSize()) > 0 ? size / (cmsColumnDeptQuery.getPageSize()) : size / (cmsColumnDeptQuery.getPageSize()) + 1;
        result.setTotalPage((long) totalPage);
        result.setTotal((long) CmsColumnDeptVos.size());
        result.setCurrent(cmsColumnDeptQuery.getCurrent().longValue());
        result.setPageSize(cmsColumnDeptQuery.getPageSize().longValue());
        result.setList(CmsColumnDeptVos);
        return result;
    }

}
