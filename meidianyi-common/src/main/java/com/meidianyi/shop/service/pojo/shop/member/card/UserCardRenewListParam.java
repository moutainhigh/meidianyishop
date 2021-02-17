package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2020-05-18 11:26
 **/
@Getter
@Setter
public class UserCardRenewListParam {

    /**
     * 指定用户
     */
    private Integer userId;
    /**
     * 续费单号
     */
    private String renewOrderSn;

    /**
     * 会员卡名称
     */
    private String cardName;

    /**
     * 卡ID
     */
    private Integer cardId;

    /**
     * 会员昵称/手机号
     */
    private String userInfo;

    /**
     * 续费时间-开始
     */
    private Timestamp startTime;

    /**
     * 续费时间-结束
     */
    private Timestamp endTime;

    /**
     * 续费金额-min
     */
    private BigDecimal renewMoneyMin;

    /**
     * 续费金额-max
     */
    private BigDecimal renewMoneyMax;

    /**
     * 续费金额类型 0:现金 1：积分
     */
    private Byte renewType;

    /**
     * 续费后有效期-min
     */
    private Timestamp renewTimeMin;

    /**
     * 续费后有效期-max
     */
    private Timestamp renewTimeMax;

    /**
     * 分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;

}
