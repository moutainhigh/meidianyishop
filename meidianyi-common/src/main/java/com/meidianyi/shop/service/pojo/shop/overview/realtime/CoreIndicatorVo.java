package com.meidianyi.shop.service.pojo.shop.overview.realtime;

import lombok.Data;

import javax.validation.constraints.Digits;
import java.util.List;

/**
 * @Author:liufei
 * @Date:2019/7/29
 * @Description:
 */
@Data
public class CoreIndicatorVo {
    /** 付款金额 */
    @Digits(integer = 20,fraction=2)
    private double totalPaidMoney;
    /** 访问付款转化率 */
    @Digits(integer = 5,fraction=2)
    private double uv2Paid;
    /** 客单价 */
    @Digits(integer = 20,fraction=2)
    private double pct;
    /** 付款订单数 */
    private int payOrderNum;
    /** 付款用户数 */
    private int payUserNum;
    /** 访客量 */
    private int uv;
    /** 浏览量 */
    private int pv;
    /** 折线图数据 */

    /** 较上一段相同的日期的增长率 */
    @Digits(integer = 5,fraction=2)
    private double totalPaidMoneyIncr;
    @Digits(integer = 5,fraction=2)
    private double uv2PaidIncr;
    @Digits(integer = 5,fraction=2)
    private double pctIncr;
    @Digits(integer = 5,fraction=2)
    private double payOrderNumIncr;
    @Digits(integer = 5,fraction=2)
    private double payUserNumIncr;
    @Digits(integer = 5,fraction=2)
    private double uvIncr;
    @Digits(integer = 5,fraction=2)
    private double pvIncr;

    /** 折线图数据 */
    private List<LineChartVo> lineChartVos;
}
