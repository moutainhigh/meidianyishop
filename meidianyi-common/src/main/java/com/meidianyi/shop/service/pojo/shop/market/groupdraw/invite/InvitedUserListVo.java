package com.meidianyi.shop.service.pojo.shop.market.groupdraw.invite;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 郑保乐
 */
@Data
public class InvitedUserListVo {

    /** 新用户id **/
    private Integer userId;
    /** 新用户昵称 **/
    private String userName;
    /** 新用户手机号 **/
    private String mobile;
    /** 邀请人id **/
    private Integer inviteId;
    /** 邀请人昵称 **/
    private String inviteUserName;
    /** 注册时间 **/
    private Timestamp createTime;
}
