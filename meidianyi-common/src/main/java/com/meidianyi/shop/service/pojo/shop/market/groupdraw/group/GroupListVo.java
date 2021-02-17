package com.meidianyi.shop.service.pojo.shop.market.groupdraw.group;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 成团明细出参
 *
 * @author 郑保乐
 */
@Data
public class GroupListVo {

    private Integer groupId;
    private Integer userCount;
    private String goodsImg;
    private String goodsName;
    /** 开团时间 **/
    private Timestamp openTime;
    /** 成团时间 **/
    private Timestamp endTime;
    /** 团长id **/
    private Integer userId;
    /** 团长昵称 **/
    private String userName;
    /** 团长手机号 **/
    private String mobile;
}
