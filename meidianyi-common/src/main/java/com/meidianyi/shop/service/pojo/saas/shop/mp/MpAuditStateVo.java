package com.meidianyi.shop.service.pojo.saas.shop.mp;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年08月19日
 */
@Data
public class MpAuditStateVo {

    /**
     * 小程序id
     */
    private String appId;
    /**
     * 提交审核ID
     */
    private Long auditId;
    /**
     * 提交审核状态
     */
    private Byte auditState;
    /**
     * 提交审核时间
     */
    private Timestamp submitAuditTime;
    /**
     *   审批成功时间
     */
    private Timestamp auditOkTime;
    /**
     * 审批失败原因
     */
    private String auditFailReason;
}
