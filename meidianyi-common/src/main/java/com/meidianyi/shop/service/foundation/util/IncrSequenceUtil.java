package com.meidianyi.shop.service.foundation.util;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单号序列工具
 * @author 孔德成
 * @date 2019/11/27 10:21
 */
@Component
public class IncrSequenceUtil {

    public static final String DATE_FORMAT_FULL_NO_UNDERLINE = "yyyyMMddHHmmss";

    public static final String INCR_SEQUENCE="IncrSequence:";
    private static JedisManager jedisManager;

    @Autowired
    public void setJedisManager(JedisManager jedisManager){
        IncrSequenceUtil.jedisManager =jedisManager;
    }


    /**
     * 订单号序列
     * @param prefix
     * @param dateFormat
     * @param key
     * @return
     */
    public static String generateOrderSn(String prefix,String dateFormat,String key){
        return new StringBuilder(prefix)
                .append(DateUtils.dateFormat(dateFormat))
                .append(jedisManager.getIncrSequence(INCR_SEQUENCE+key)).toString();
    }

    /**
     * 订单号序列
     * @param prefix
     * @return
     */
    public static String generateOrderSn(String prefix){
        return generateOrderSn(prefix,DATE_FORMAT_FULL_NO_UNDERLINE,prefix);
    }
    /**
     * 处方号生成
     * @param prefix
     * @return
     */
    public static String generatePrescriptionCode(String prefix,String dateFormat,String key){
        return new StringBuilder(prefix)
            .append(DateUtils.dateFormat(dateFormat))
            .append(jedisManager.getIncrSequence(INCR_SEQUENCE+key)).toString();
    }
    /**
     * 处方号生成
     * @param prefix
     * @return
     */
    public static String generatePrescriptionCode(String prefix){
        return generateOrderSn(prefix,DATE_FORMAT_FULL_NO_UNDERLINE,prefix);
    }
}
