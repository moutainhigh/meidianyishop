package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import lombok.Data;

/**
 * @author liufei
 * @date 2019 /8/20
 * @description
 */
@Data
public class ShareReceiveDetailParam {
    /**
     * 分享有礼活动id
     */
    public Integer shareId;
    /**
     * 用户昵称
     */
    public String username;
    /**
     * 手机号
     */
    public String mobile;
    /**
     * 商品名称
     */
    public String goodsName;
    /**
     * 奖励级别
     */
    public Byte rewardLevel;

    /**
     * 当前页
     */
    public Integer currentPage;
    /**
     * 页行数
     */
    public Integer pageRows;
}
