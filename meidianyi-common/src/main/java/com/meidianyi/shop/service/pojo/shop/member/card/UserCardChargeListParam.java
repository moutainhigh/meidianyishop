package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2020-05-18 15:43
 **/
@Getter
@Setter
public class UserCardChargeListParam {
    /**
     * 指定用户
     */
    private Integer userId;
    /**
     * 充值单号
     */
    private String orderSn;
    /**
     * 会员卡
     */
    private String cardName;
    /**
     * 卡ID
     */
    private Integer cardId;
    /**
     * 充值方式
     * 1发卡 2用户充值 3 管理员操作
     */
    private Byte changeType;
    /**
     * 会员信息 昵称/手机号
     */
    private String userInfo;
    /**
     * 充值时间 min
     */
    private Timestamp createTimeMin;
    /**
     * 充值时间 max
     */
    private Timestamp createTimeMax;
    /**
     * 充值金额 min
     */
    private BigDecimal chargeMin;
    /**
     * 充值金额 max
     */
    private BigDecimal chargeMax;
    /**
     * 充值后卡余额 min
     */
    private BigDecimal afterChargeMoneyMin;
    /**
     * 充值后卡余额 max
     */
    private BigDecimal afterChargeMoneyMax;

    /**
     * 分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
