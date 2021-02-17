package com.meidianyi.shop.service.pojo.saas.user;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 主库用户表
 * @author 李晓冰
 * @date 2020年08月17日
 */
@Data
public class MainUserVo {
    private Long       id;
    private Integer    shopId;
    private Long       userId;
    private String     username;
    private String     userPwd;
    private String     userCid;
    private String     mobile;
    private String     userCode;
    private String     wxOpenid;
    private String     email;
    private Timestamp createTime;
    private String     wechat;
    private Integer    fanliGrade;
    private Integer    userGrade;
    private Integer    invite;
    private String     inviteSource;
    private Integer    invitationCode;
    private BigDecimal account;
    private Integer    discount;
    private Integer    discountGrade;
    private Byte       delFlag;
    private Timestamp deleteTime;
    private Integer    growth;
    private Integer    score;
    private Integer    source;
    private Integer    inviteId;
    private Date inviteExpiryDate;
    private String     wxUnionId;
    private Timestamp  updateTime;
    private Byte       isDistributor;
    private Integer    inviteActId;
    private Byte       distributorLevel;
    private String     aliUserId;
    private String shopName;
}
