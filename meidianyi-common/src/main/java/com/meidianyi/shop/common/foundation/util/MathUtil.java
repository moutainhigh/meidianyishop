package com.meidianyi.shop.common.foundation.util;

import java.math.BigDecimal;

/**
 * 数字处理
 * @author 卢光耀
 * @date 2019-09-03 10:23
 *
*/
public class MathUtil {

    /**
     * 两整数相除保留N位小数
     * @param top 被除
     * @param below 除
     * @param length 保留几位
     * @return 保留两位小数的结果
     */
    public static double deciMal(int top, int below,int length) {
        return  new BigDecimal((float)top / below)
            .setScale(length, BigDecimal.ROUND_HALF_UP)
            .doubleValue();

    }
    /**
     * 两整数相除保留N位小数
     * @param top 被除
     * @param below 除
     * @param length 保留几位
     * @return 保留两位小数的结果
     */
    public static double deciMal(Long top, Long below,int length) {
        return  new BigDecimal((float)top / below)
            .setScale(length, BigDecimal.ROUND_HALF_UP)
            .doubleValue();

    }
    /**
     * 两整数相除保留两位小数
     * @param top 被除
     * @param below 除
     * @return 保留两位小数的结果
     */
    public static double deciMal(Long top, Long below) {
        return  deciMal(top,below,2);

    }
    /**
     * 两整数相除保留两位小数
     * @param top 被除
     * @param below 除
     * @return 保留两位小数的结果
     */
    public static double deciMal(int top, int below) {
        return  deciMal(top,below,2);

    }

}
