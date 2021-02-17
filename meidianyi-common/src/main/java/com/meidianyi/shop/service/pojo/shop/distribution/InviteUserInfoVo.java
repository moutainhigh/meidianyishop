package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-05-12
 */
@Data
public class InviteUserInfoVo {
    /**用户ID*/
    private Integer userId;
    /**用户昵称*/
    private String username;
    /**手机号*/
    private String mobile;
    /**注册时间*/
    private Timestamp createTime;
    /**累积返利订单数*/
    private Integer orderNumber;
    /**累积订单返利商品总金额*/
    private BigDecimal totalCanFanliMoney;
    /**累积返利佣金*/
    private BigDecimal totalFanliMoney;
    /**返利有效日期*/
    private Date inviteExpiryDate;
    /**邀请保护日期*/
    private Date inviteProtectDate;
    /**邀请时间*/
    private Timestamp inviteTime;
    /**真是姓名*/
    private String realName;
}
