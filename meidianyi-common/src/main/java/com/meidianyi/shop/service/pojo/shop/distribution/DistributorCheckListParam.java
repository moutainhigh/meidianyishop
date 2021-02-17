package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 分销员审核列表入参类
 * @author 常乐
 * 2019年9月20日
 */
@Data
public class DistributorCheckListParam {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String username;

    /**
     * 申请开始时间
     */
    private Timestamp startTime;

    /**
     * 申请结束时间
     */
    private Timestamp endTime;
    /**
     * 用戶id
     */
    private Integer userId;
    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     * 分销员分组id
     */
    private Integer inviteGroup;
    /**
     * 审核开始时间
     */
    private Timestamp checkStartTime;

    /**
     * 审核结束时间
     */
    private Timestamp checkEndTime;

    private Integer currentPage;
    private Integer pageRows;
    /**
     * tab状态 0：待审核；1：审核通过；2：未通过
     */
    private Byte nav;
    /**
     * 是否从店铺助手过来
     */
    private Integer flag = 0;
    /**
     * 超过几天未审核
     */
    private Integer numberDays = 3;
}
