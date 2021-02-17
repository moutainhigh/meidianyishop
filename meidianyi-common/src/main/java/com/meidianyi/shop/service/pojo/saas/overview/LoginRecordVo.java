package com.meidianyi.shop.service.pojo.saas.overview;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author  常乐
 * @Date 2019-12-04
 */
@Data
public class LoginRecordVo {
    /**
     * 	分页信息
     */
    private int currentPage;
    private int pageRows;
    /**
     * 店铺id
     */
    private Integer shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 主账号ID
     */
    private Integer sysId;
    /**
     * 登录用户ID
     */
    private Integer userId;
    /**
     * 登录用户名
     */
    private String userName;
    /**
     * 登录时间
     */
    private Timestamp addTime;
    /**
     * 用户登录ip
     */
    private String userIp;
    /**
     * 每日登录次数
     */
    private Integer count;
    /**
     * 账户类型
     */
    private Byte accountType;
    /**
     * 小程序昵称
     */
    private String nickName;
    /**
     * 登录开始时间
     */
    private Timestamp startAddTime;
    /**
     * 登录结束时间
     */
    private Timestamp endAddTime;
}
