package com.meidianyi.shop.service.pojo.wxapp.order.marketing.process;

import com.meidianyi.shop.service.pojo.wxapp.order.marketing.coupon.OrderCouponVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import lombok.Builder;
import lombok.Data;

/**
 * 临时存储默认营销信息
 * @author 王帅
 */
@Data
@Builder
public class DefaultMarketingProcess {
    private OrderMemberVo card;
    private OrderCouponVo coupon;
    /**会员卡 0 ，优惠券 1*/
    private byte type;
}
