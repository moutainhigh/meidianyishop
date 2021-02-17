package com.meidianyi.shop.service.saas.index.vo;

import java.util.List;

/**
 * system 概况-概览 用户统计信息
 * @author luguangyao
 */
public class UserStatisticsInfo {
    private List<BaseInfo> userNumsInfo;

    private Integer allNum;

    public List<BaseInfo> getUserNumsInfo() {
        return userNumsInfo;
    }

    public void setUserNumsInfo(List<BaseInfo> userNumsInfo) {
        this.userNumsInfo = userNumsInfo;
    }

    public Integer getAllNum() {
        return allNum;
    }

    public void setAllNum(Integer allNum) {
        this.allNum = allNum;
    }
}
