package com.rongji.rjsoft.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @description: 语言转化
 * @author: JohnYehyo
 * @create: 2021-10-27 17:52:13
 */
public class LanguageUtil {

    public static String hanToPinyin(String content) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        //拼音小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //不带声调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //要转换的中文，格式，转换之后的拼音的分隔符，遇到不能转换的是否保留   wo,shi,zhong,guo,ren,，hello
        return PinyinHelper.toHanYuPinyinString(content, format, "", true);
    }

}