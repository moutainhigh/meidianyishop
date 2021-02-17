package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author liufei
 * @date 2019/7/15
 * 商城概览-数据展示-出参
 */
@Data
@Component
public class DataDemonstrationVo {
    /** 用户访问数 */
    private int userVisitNum;
    /** 支付订单数 */
    private int paidOrderNum;
    /** 下单用户数 */
    private int orderUserNum;
    /**
     * 下单人数
     */
    private int orderPeopleNum;
    /**
     * 下单笔数
     */
    private int orderNum;
    /** 总支付金额 */
    private BigDecimal totalPaidSum;
    /** 支付用户数 */
    private int paidUserNum;
    /**
     * 支付人数
     */
    private int payPeopleNum;
    /** 访问下单转化率 */
    private BigDecimal uv2order;
    /** 访问支付转化率*/
    private BigDecimal uv2paid;
    /** 下单支付转化率*/
    private BigDecimal order2paid;
}
