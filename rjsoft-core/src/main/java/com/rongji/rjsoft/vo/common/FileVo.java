package com.rongji.rjsoft.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 文件返回视图
 * @author: JohnYehyo
 * @create: 2021-09-24 14:36:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVo {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件存储地址
     */
    private String fileUrl;
}
