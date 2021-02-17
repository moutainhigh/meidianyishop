package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author changle
 * @date 2020/6/27 6:10 下午
 */
@Data
public class InviteUserVo {
    /** 用户Id*/
    private Integer userId;
    /** 用户昵称*/
    private String username;
    /** 用户头像*/
    private String userAvatar;
    /** 是否为分销员 0：否；1：是*/
    private Integer isDistributor;
    /** 邀请时间*/
    private Timestamp inviteTime;
    /** 邀请类型 0：直接邀请；1：间接邀请*/
    private Byte inviteType = 0;
    /** 邀请保护到期时间*/
    private Timestamp inviteProtectDate = null;
    /** 邀请返利到期时间*/
    private Timestamp inviteExpiryDate = null;
    /** 是否已失效 0：否；1：是*/
    private Integer isFailture = 0;
    /** 剩余保护时间 -1:永久保护*/
    private Integer leftProtectDay = -1;
    /** 剩余返利时间 -1：永久保护*/
    private Integer leftExpiryDay = -1;
    /** 等级名称*/
    private String levelName;
    /** 邀请人ID*/
    private Integer inviteId;
    /** 邀请人昵称*/
    private String inviteName;
    /** 累积返利订单数*/
    private Integer orderNumber;
    /** 累积佣金*/
    private BigDecimal totalFanliMoney;
}

