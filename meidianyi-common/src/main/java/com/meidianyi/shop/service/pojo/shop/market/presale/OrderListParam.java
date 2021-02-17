package com.meidianyi.shop.service.pojo.shop.market.presale;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 定金膨胀活动订单入参
 *
 * @author 郑保乐
 */
@Getter
@Setter
public class OrderListParam extends BasePageParam {

    /** 活动id **/
    @NotNull
    private Integer id;
    /** 订单号 **/
    private String orderSn;
    /** 用户昵称 **/
    private String username;
    /** 收货人手机号 **/
    private String mobile;
}
