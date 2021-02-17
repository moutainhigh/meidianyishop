package com.meidianyi.shop.service.pojo.shop.coupon;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 孔德成
 * @date 2019/10/21 10:50
 */
@Getter
@Setter
public class CouponAllParam {

    /**
     * 是否限制库存
     */
    private Boolean isHasStock =true;

    /**
     * 默认进行中中,未开始
     */
    private byte  status = BaseConstant.NAVBAR_TYPE_AVAILABLE;

}
