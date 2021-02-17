package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * The type Submit reservation param.
 *
 * @author liufei
 * @date 11 /7/19
 */
@Data
public class SubmitReservationParam {
    /**
     * The Client ip.客户端ip地址
     */
    private String clientIp;
    /**
     * The Store id.门店id
     */
    @PositiveOrZero
    private Integer storeId;
    /**
     * The Service id.服务id
     */
    @PositiveOrZero
    private Integer serviceId;
    /**
     * The User id.用户id
     */
    @PositiveOrZero
    private Integer userId;
    /**
     * The Technician id.技师id(服务类型是有技师的话)
     */
    private Integer technicianId;
    /**
     * The Technician name.技师名称
     */
    private String technicianName;
    /**
     * The Subscriber.预约姓名
     */
    @NotBlank
    private String subscriber;
    /**
     * The Mobile.预约电话
     */
    @NotBlank
    private String mobile;
    /**
     * The Add message.留言
     */
    private String addMessage;
    /**
     * The Service date.服务日期
     */
    @NotBlank
    private String serviceDate;
    /**
     * The Service period.服务时间段
     */
    @NotBlank
    private String servicePeriod;
    /**
     * The Use account.用户余额抵扣金额
     */
    private BigDecimal useAccount;
    /**
     * The Member card no.会员卡号
     */
    private String memberCardNo;
    /**
     * The Member card balance.会员卡抵扣金额
     */
    private BigDecimal memberCardBalance;
    /**
     * The Money Paid.应付金额
     */
    @PositiveOrZero
    private BigDecimal moneyPaid;
    /**
     * The Coupon id.优惠券id
     */
    private Integer couponId;
    /**
     * The Discount.优惠券抵扣金额
     */
    private BigDecimal discount;

    /**
     * The Order amount.订单总金额
     */
    @PositiveOrZero
    public BigDecimal orderAmount;
}
