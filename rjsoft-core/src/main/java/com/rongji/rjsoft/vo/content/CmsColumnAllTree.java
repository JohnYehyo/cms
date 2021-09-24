package com.rongji.rjsoft.vo.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 栏目同步树
 * @author: JohnYehyo
 * @create: 2021-09-24 17:31:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmsColumnAllTree implements Serializable {

    /**
     * 栏目id
     */
    private Long columnId;

    /**
     * 栏目名
     */
    private String columnName;

    /**
     * 父级id
     */
    private Long parentId;

    /**
     * 子集对象
     */
    private List<CmsColumnAllTree> children;
}
