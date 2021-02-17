package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author chenjie
 * @date 2020年08月27日
 */
@Data
@NoArgsConstructor
public class StoreOrderSummaryTrendDo {
    private Integer    id;
    private Date       refDate;
    private Byte       type;
    private Integer   storeId;
    /**
     * 付款人数
     */
    private Integer    orderPayUserNum;
    /**
     * 消费金额
     */
    private BigDecimal totalPaidMoney;
    /**
     * 支付单数
     */
    private Integer    orderPayNum;
    /**
     * 下单数
     */
    private Integer    orderNum;
    /**
     * 下单人数
     */
    private Integer    orderUserNum;
    private Timestamp  createTime;
}
