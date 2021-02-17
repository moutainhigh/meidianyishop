package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class DistributorListVo {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 分销员申请表最终更新时间，若审核通过，即成为分销员时间
     */
    private Timestamp updateTime;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 分组姓名
     */
    private String groupName;
    /**
     * 等级名称
     */
    private String levelName;
    /**
     * 邀请人ID
     */
    private Integer inviteId;
    /**
     * 邀请人姓名
     */
    private String inviteName;
    /**
     * 直接邀请人
     */
    private String sublayerNumber;

    /**
     * 间接邀请人数
     */
    private Integer nextNumber;

    /**
     * 累积返利商品总额
     */
    private BigDecimal totalCanFanliMoney;

    /**
     * 累积返利佣金金额
     */
    private BigDecimal totalFanliMoney;

    /**
     * 待返利佣金金额
     */
    private BigDecimal waitFanliMoney;
    /**
     * 备注数量
     */
    private Integer remarkNum;
}
