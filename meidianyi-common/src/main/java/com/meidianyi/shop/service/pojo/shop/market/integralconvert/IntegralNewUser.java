package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 积分兑换新用户
 * @author liangchen
 * @date 2020.03.19
 */
@Data
public class IntegralNewUser {
    /** 活动名称 */
    private String actName;
    /** ID */
    private Integer userId;
    /** 昵称 */
    private String userName;
    /** 手机号 */
    private String mobile;
    /** 创建时间 */
    private Timestamp createTime;
    /** 邀请人 */
    private String inviteUserName;

}
