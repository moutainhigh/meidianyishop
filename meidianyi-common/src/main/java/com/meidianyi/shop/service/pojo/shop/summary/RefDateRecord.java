package com.meidianyi.shop.service.pojo.shop.summary;

/**
 * 通过 ref_date 字段表示日期的 POJO
 *
 * @param <T> 实际类型的包装类
 *
 * @author 郑保乐
 */
public interface RefDateRecord<T> {

    /**
     * 获取日期
     *
     * @return 日期
     */
    String getRefDate();

    /**
     * 获取数值
     *
     * @return 数值
     */
    T getValue();
}
