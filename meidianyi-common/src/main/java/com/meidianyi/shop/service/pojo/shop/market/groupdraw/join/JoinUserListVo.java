package com.meidianyi.shop.service.pojo.shop.market.groupdraw.join;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 拼团抽奖 - 参与用户 列表出参
 *
 * @author 郑保乐
 */
@Data
public class JoinUserListVo {

    /** 用户id **/
    private Integer userId;
    /** 昵称 **/
    private String userName;
    /** 手机号 **/
    private String mobile;
    /** 参与时间 **/
    private Timestamp openTime;
    /** 订单号 **/
    private String orderSn;
    /** 是否团长 **/
    private Boolean isGrouper;
    /** 团id **/
    private Integer groupId;
    /** 成团时间 **/
    private Timestamp endTime;
    /** 邀请用户数量 **/
    private Integer inviteNum;

    /** 抽奖码数量 **/
    private Short codeCount;
    
    private Integer groupDrawId;
    
    private Integer goodsId;
    
    private Integer drawNum;
}
