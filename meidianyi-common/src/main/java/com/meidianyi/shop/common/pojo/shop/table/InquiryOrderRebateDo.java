package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
@Data
@NoArgsConstructor
public class InquiryOrderRebateDo {
    private Integer    id;
    private String     orderSn;
    private Integer    doctorId;
    private BigDecimal totalMoney;
    private BigDecimal totalRebateMoney;
    private BigDecimal platformRebateMoney;
    private Byte       status;
    private String     reason;
    private Byte       isDelete;
    private Timestamp rebateTime;
    private Timestamp  createTime;
    private Timestamp  updateTime;
}
