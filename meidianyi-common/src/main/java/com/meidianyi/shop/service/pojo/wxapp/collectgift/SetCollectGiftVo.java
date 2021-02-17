package com.meidianyi.shop.service.pojo.wxapp.collectgift;

import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponDetailVo;
import lombok.Data;

import java.util.List;

/**
 * @Author 常乐
 * @Date 2019-12-27
 */
@Data
public class SetCollectGiftVo {
    /**
     * 返回信息0:成功；1：优惠券不存在；2：优惠券已过期；3：优惠券已停用；4：库存为0
     */
    public Byte msg;
    /**
     * 积分
     */
    public Integer score;

    /**
     * 优惠券
     */
    public List<AvailCouponDetailVo> couponDetail;


}
