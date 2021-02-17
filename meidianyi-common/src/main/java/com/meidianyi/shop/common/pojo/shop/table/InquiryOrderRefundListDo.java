package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 */
@Data
public class InquiryOrderRefundListDo {
    private static final long serialVersionUID = 1625068988;

    private Integer    id;
    private String     orderSn;
    private Integer    userId;
    private BigDecimal moneyAmount;
    private String     refundReason;
    private Timestamp refundTime;
    private Byte       isSuccess;
    private Byte       isDelete;
    private Timestamp  createTime;
    private Timestamp  updateTime;
}
