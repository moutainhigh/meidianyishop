package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.pojo.shop.summary.ChartInfo;
import lombok.Getter;

/**
 * 平均访问深度 key 对应关系
 *
 * @author 郑保乐
 */
@Getter
public enum AccessDepth implements ChartInfo {

    /**
     * 1 页
     */
    L1(1, "1 页"),
    /**
     * 2 页
     */
    L2(2, "2 页"),
    /**
     * 3 页
     */
    L3(3, "3 页"),
    /**
     * 4 页
     */
    L4(4, "4 页"),
    /**
     * 5 页
     */
    L5(5, "5 页"),
    /**
     * 6 页
     */
    L6(6, "6-10 页"),
    /**
     * 7 页
     */
    L7(7, ">10 页");

    private Integer index;
    private String duration;

    AccessDepth(Integer index, String duration) {
        this.index = index;
        this.duration = duration;
    }

    @Override
    public String getName() {
        return getDuration();
    }

    @Override
    public Integer getKey() {
        return getIndex();
    }
}
