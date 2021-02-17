package com.meidianyi.shop.service.pojo.shop.coupon;

import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponDetailVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 优惠券详情
 * @author 孔德成
 * @date 2020/4/16
 */
@Getter
@Setter
@ToString
public class MpGetCouponVo {



    /**
     * 用户信息
     */
    private List<UserInfo> userInfos;
    /**
     * 单位
     */
    private String unit;
    /**
     * 领取人数
     */
    private Integer haveNum;
    /**
     * 是自己发的优惠券 1 自己的 0 别人分享的
     */
    private Byte isOneself =0;
    /**
     * 状态 1正在 2 已领取过 3已领完 4优惠券过期 5优惠券删除
     */
    private Byte status =(byte)1;
    /**
     * 会员卡信息
     */
    AvailCouponDetailVo couponInfo;

    @Getter
    @Setter
    public static class UserInfo{
        /**用户名*/
        private String username;
        /*用户头像*/
        private String userAvatar;
        /**领取时间*/
        private Timestamp createTime;
        private BigDecimal amount;
        /**
         * 时间 天 时 秒
         */
        private Integer[] time;

    }
}
