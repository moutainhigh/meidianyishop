package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-02-27 11:37
 **/
@Getter
@Setter
public class CouponPackActInfoVo {

    /**  活动配置信息 */
    private CouponPackActBaseVo packInfo;
    /**  活动下属优惠券 */
    private List<CouponPackVoucherVo> packList;
    /**  用户已购买或领取的礼包数量 */
    private Integer buyCount;
}
