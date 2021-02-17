package com.meidianyi.shop.service.shop.activity.processor;

/**
 * @author 李晓冰
 * @date 2019年11月12日
 */
public interface Processor {
    /**
     * 活动排序优先级
     * @return
     */
    Byte getPriority();

    /**
     * 获取活动类型,方便查找
     * @return 活动类型
     */
    Byte getActivityType();
}
