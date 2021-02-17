package com.meidianyi.shop.service.pojo.shop.market.groupdraw.join;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 拼团抽奖 - 参与用户 列表入参
 *
 * @author 郑保乐
 */
@Getter
@Setter
public class JoinUserListParam extends BasePageParam {

    /** 用户昵称 **/
    private String nickName;
    /** 手机号 **/
    private String mobile;
    /** 参与开始时间 **/
    private Timestamp startTime;
    /** 参与结束时间 **/
    private Timestamp endTime;
    /** 订单流水号 **/
    private String orderSn;
    /** 是否已成团 **/
    private Boolean grouped;
    /** 最小邀请用户数 **/
    private Integer minInviteUserCount;
    /** 最大邀请用户数 **/
    private Integer maxInviteUserCount;
    /** 是否团长 **/
    private Boolean isGrouper;
    /** 团 ID **/
    private Integer groupId;
    /** 拼团抽奖活动 ID **/
    private Integer groupDrawId;
}
