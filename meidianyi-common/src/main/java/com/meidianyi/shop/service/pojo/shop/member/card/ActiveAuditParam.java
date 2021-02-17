package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * @author 黄壮壮
 * @Date: 2019年9月27日
 * @Description: 激活审核数据-入参
 */
@Data
public class ActiveAuditParam {
    /**
     * -每页总数
     */
    public Integer pageRows;
    /**
     * -当前页
     */
    public Integer currentPage;
    /**
     * - ids
     */
    public List<Integer> ids;
    /**
     * - 订单id
     */
    public Integer id;
    /**
     * 会员卡号
     */
    public String cardNo;
    /**
     * 会员卡id
     */
    private Integer cardId;
    /**
     * 审核状态 1审核中 2通过 3拒绝  {@link com.meidianyi.shop.common.pojo.shop.member.card.CardConstant.UNDER_REVIEW}
     */
    private Byte status;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 申请时间 - 开始
     */
    private Timestamp firstTime;
    /**
     * 申请时间 - 结束
     */
    private Timestamp secondTime;
    /**
     * 审核不通过原因
     */
    private String refuseDesc;
    /**
     * 审核超时
     */
    private Timestamp examineOver;
    /**
     * 开始行
     */
    public Integer startNum;
    /**
     * 结束行
     */
    public Integer endNum;
    /**
     * 操作员Id
     */
    public Integer sysId;

}