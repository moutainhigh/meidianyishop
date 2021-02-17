package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-05-15 16:51
 * 图表数据分析的出参
 **/
@Getter
@Setter
public class AnalysisVo {
    /**
     * 成功支付单数
     */
    private List<Integer> paidOrderNumber = new ArrayList<>();
    /**
     * 成功支付人数
     */
    private List<Integer> paidUserNumber = new ArrayList<>();
    /**
     * 支付金额
     */
    private List<BigDecimal> paymentAmount = new ArrayList<>();
    /**
     * 退款金额
     */
    private List<BigDecimal> returnAmount = new ArrayList<>();

    /**
     * 日期列表
     */
    private List<String> dateList = new ArrayList<>();

    /**
     * 统计总数
     */
    private AnalysisTotalVo total;

    @Setter
    @Getter
    public static class AnalysisTotalVo {
        private Integer totalPaidOrderNumber;
        private Integer totalPaidUserNumber;
        private BigDecimal totalPaymentAmount;
        private BigDecimal totalReturnAmount;
    }
}
