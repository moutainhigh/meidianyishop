package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.pojo.shop.summary.ChartInfo;

/**
 * 访问时长 key 对应关系
 *
 * @author 郑保乐
 */
public enum VisitDuration implements ChartInfo {

    /** 访问时常 */
    L1(1, "0-2s"),
    L2(2, "3-5s"),
    L3(3, "6-10s"),
    L4(4, "11-20s"),
    L5(5, "20-30s"),
    L6(6, "30-50s"),
    L7(7, "50-100s"),
    L8(8, ">100s");

    private Integer index;
    private String name;

    VisitDuration(Integer index, String duration) {
        this.index = index;
        this.name = duration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getKey() {
        return index;
    }
}
