package com.meidianyi.shop.common.foundation.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @author 赵晓东
 * @description map工具类
 * @create 2020-09-22 17:29
 **/

public class MapUtil {

    /**
     * 合并两个map，使第二个map中字段添加至第一个map中，如果第二个map有第一个map没有那么在第一个map中新增entry
     * @param resultMap 原map
     * @param partMap 添加map
     * @param <T>
     * @param <E>
     */
    public static <T, E> void merge2ResultMap(Map<T, E> resultMap, Map<T, E> partMap) {
        for (Map.Entry<T, E> entry : partMap.entrySet()) {
            T key = entry.getKey();
            if (resultMap.containsKey(key)) {
                FieldsUtil.assign(partMap.get(key),resultMap.get(key));
            } else {
                resultMap.put(key, entry.getValue());
            }
        }
    }
}
