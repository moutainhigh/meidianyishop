package com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2019年10月24日
 * 小程序-拼团活动返回信息类 activityType=6
 */
@Deprecated
public class ReducePriceActivityVo extends ActivityBaseVo{
    public ReducePriceActivityVo() {
        activityType = BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE;
    }

    /**
     * 活动是否正在进行中： true 是 false 否
     */
    @Getter
    @Setter
    private Boolean isActive;

    @Getter
    @Setter
    private Timestamp nextActivityStartDate;

    @Getter
    @Setter
    private Timestamp currentActivityEndDate;
}
