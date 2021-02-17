package com.meidianyi.shop.service.pojo.wxapp.market.sharereward;

import lombok.Data;

/**
 * The type Share param.
 *
 * @author liufei
 * @date 1 /9/20
 */
@Data
public class ShareParam {
    /**
     * 活动id
     */
    private Integer activityId;
    /**
     * 活动类型
     */
    private Integer activityType;
    private Integer userId;
    /**
     * 分享商品id
     */
    private Integer goodsId;

}
