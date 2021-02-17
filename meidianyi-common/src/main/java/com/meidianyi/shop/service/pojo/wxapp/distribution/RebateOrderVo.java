package com.meidianyi.shop.service.pojo.wxapp.distribution;

import com.meidianyi.shop.common.foundation.util.PageResult;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-04-11
 */
@Data
public class RebateOrderVo {
    /** 时间段内累积返利总额*/
    private BigDecimal partRebateMoney;
    /** 时间段内返利商品总额*/
    private BigDecimal partRebateGoodsMoney;

    /** 全部订单返利总额*/
    private BigDecimal allRebateMoney;
    /** 待返利佣金*/
    private BigDecimal waitRebateMoney;

    private PageResult<RebateOrderInfo> rebateOrderInfo;

    @Data
    public static class RebateOrderInfo{
        /** 订单编号*/
        private String orderSn;
        /**  下单时间*/
        private Timestamp createTime;
        /** 订单完成时间*/
        private Timestamp finishedTime;
        /** 下单用户*/
        private String username;
        /** 订单状态编码*/
        private Integer orderStatus;
        /** 订单状态名称*/
        private String orderStatusName;
        /** 返利商品总额*/
        private BigDecimal canCalculateMoney;
        /** 返利佣金*/
        private BigDecimal rebateMoney;
        /** 计算标志 0：未结算；1：已结算*/
        private Byte settlementFlag;
        /** 返利关系 0：自购返利；1：直接返利；2：间接返利*/
        private Integer rebateLevel;
        /** 是否成本价保护 0：否；1；是*/
//        private Byte costPriceProtect = 0;
    }
}
