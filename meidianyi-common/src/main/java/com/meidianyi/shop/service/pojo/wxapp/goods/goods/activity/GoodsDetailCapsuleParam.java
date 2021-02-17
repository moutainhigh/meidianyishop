package com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity;

import lombok.Data;

/**
 * 商品详情处理器参数
 * @author 李晓冰
 * @date 2019年11月07日
 */
@Data
public class GoodsDetailCapsuleParam{
    private Integer userId;
    public Integer goodsId;
    public Integer catId;
    public Integer sortId;
    public Integer brandId;

    /**
     * 用户当前位置经度
     */
    private String lon;
    /**
     * 用户当前位置纬度
     */
    private String lat;
    /**
     * 指定该商品的详情页营销活动id
     */
    private Integer activityId;
    /**
     * 指定该商品的详情页营销活动类型
     */
    private Byte activityType;

    /**
     * 分享有礼--分享者的ID
     */
    private Integer shareAwardLaunchUserId;
    /**
     * 分享有礼--分享有礼活动ID
     */
    private Integer shareAwardId;
}
