package com.meidianyi.shop.service.pojo.shop.market.gift;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 赠送明细列表入参
 *
 * @author 郑保乐
 */
@Data
public class GiftDetailListVo {

    /** 用户id **/
    private Integer userId;
    /** 用户昵称 **/
    private String username;
    /** 手机号 **/
    private String mobile;
    /** 订单号 **/
    private String orderSn;
    /** 赠送时间 **/
    private Timestamp createTime;
    /** 赠品件数 **/
    private Integer giftAmount;
}
