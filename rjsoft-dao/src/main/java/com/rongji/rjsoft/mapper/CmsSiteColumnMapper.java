package com.rongji.rjsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rongji.rjsoft.entity.content.CmsSiteColumn;
import com.rongji.rjsoft.vo.content.CmsSiteTreeVo;

import java.util.List;

/**
 * <p>
 * 站点栏目关系表 Mapper 接口
 * </p>
 *
 * @author JohnYehyo
 * @since 2021-09-24
 */
public interface CmsSiteColumnMapper extends BaseMapper<CmsSiteColumn> {

    /**
     * 保存站点栏目关系
     * @param list 站点信息
     * @return 保存结果
     */
    boolean batchInsert(List<CmsSiteColumn> list);

    /**
     * 通过栏目id删除站点栏目关系
     * @param columnId 站点id
     * @return 删除结果
     */
    int deleteSiteColumnByColumnId(Long[] columnId);

    /**
     * 通过站点id删除站点栏目关系
     * @param siteId 站点id
     * @return 删除结果
     */
    int deleteSiteColumnBySiteId(Long[] siteId);

}
