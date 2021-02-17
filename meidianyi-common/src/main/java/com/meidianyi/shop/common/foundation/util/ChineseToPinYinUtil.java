package com.meidianyi.shop.common.foundation.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.slf4j.LoggerFactory;

/**
 * @author 李晓冰
 * @date 2019年10月17日
 */
public class ChineseToPinYinUtil {

    private static final HanyuPinyinOutputFormat FORMAT;
    public static final String OTHER_CHARACTER = "#";

    static {
        FORMAT = new HanyuPinyinOutputFormat();
        FORMAT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    private static final String CHINESE_RANGE = "[\\u4E00-\\u9FA5]";
    /**
     * 获取中文名对应的拼音名的首字母
     * @param chinese 待转换字符串
     * @return 转换后的中文拼音首字母字符串
     */
    public static String getStartAlphabet(String chinese) {


        if (chinese == null ||chinese.length()==0) {
            return OTHER_CHARACTER;
        }
        StringBuilder pinName = new StringBuilder();
        char[] chineseChar = chinese.toCharArray();

        char startChar = chineseChar[0];
        boolean isEnglishCharacter = ('a' <= startChar && startChar <= 'z') || ('A' <= startChar && startChar <= 'Z');
        if (Character.toString(startChar).matches(CHINESE_RANGE)){
            try {
                pinName.append(PinyinHelper.toHanyuPinyinStringArray(startChar, FORMAT)[0].charAt(0));
            } catch (BadHanyuPinyinOutputFormatCombination message) {
                LoggerFactory.getLogger(ChineseToPinYinUtil.class).warn("中文转拼音错误："+message.getMessage());
                pinName.append(OTHER_CHARACTER);
            }
        } else if (isEnglishCharacter) {
            pinName.append(Character.toUpperCase(startChar));
        } else {
            pinName.append(OTHER_CHARACTER);
        }

        return pinName.toString();
    }

    public static String getPinYin(String chinese) {
        if (chinese == null ||chinese.length()==0) {
            return OTHER_CHARACTER;
        }
        StringBuilder pinName = new StringBuilder();
        char[] chineseChar = chinese.toCharArray();
        for (int i = 0; i < chineseChar.length; i++) {
            if (Character.toString(chineseChar[i]).matches("[\\u4E00-\\u9FA5]")) {
                try {
                   pinName.append( PinyinHelper.toHanyuPinyinStringArray(chineseChar[i], FORMAT)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination message) {
                    LoggerFactory.getLogger(ChineseToPinYinUtil.class).warn("中文转拼音错误："+message.getMessage());
                    pinName.append(OTHER_CHARACTER);
                }
            } else {
                pinName.append(chineseChar[i]);
            }
        }
        return pinName.toString();
    }
}
