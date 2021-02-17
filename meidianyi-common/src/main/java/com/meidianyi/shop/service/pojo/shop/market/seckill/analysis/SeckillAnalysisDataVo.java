package com.meidianyi.shop.service.pojo.shop.market.seckill.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-10-30 10:33
 **/
@Data
public class SeckillAnalysisDataVo {
    /** 活动实付总金额  */
    private List<BigDecimal> paymentAmount = new ArrayList<>();
    /** 活动优惠总金额  */
    private List<BigDecimal> discountAmount = new ArrayList<>();
    /** 费效比  */
    private List<BigDecimal> costEffectivenessRatio = new ArrayList<>();
    /** 付款订单数  */
    private List<Integer> paidOrderNumber = new ArrayList<>();
    /** 付款商品件数  */
    private List<Integer> paidGoodsNumber = new ArrayList<>();
    /** 老成交用户数   */
    private List<Integer> oldUserNumber = new ArrayList<>();
    /** 新成交用户数   */
    private List<Integer> newUserNumber = new ArrayList<>();
    /** 日期列表  */
    private List<String> dateList = new ArrayList<>();

    /** 活动实付总金额  */
    private SeckillAnalysisTotalVo total;
}
