package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

/**
 * 成交用户分析
 * @author liangchen
 * @date 2019年7月19日
 */
@Data
public class OrderChangeRateVo {
    /** 全部成交客户数 */
    private Double orderUserDataTrend;
    /** 新成交客户数 */
    private Double newOrderUserDataTrend;
    /** 老成交客户数 */
    private Double oldOrderUserDataTrend;

    /** 全部客户数占比 */
    private Double orderUserDataRateTrend;
    /** 新客户数占比 */
    private Double newOrderUserDataRateTrend;
    /** 老客户数占比 */
    private Double oldOrderUserDataRateTrend;

    /** 全部客户单价 */
    private Double unitPriceTrend;
    /** 新客户单价 */
    private Double newUnitPriceTrend;
    /** 老客户单价 */
    private Double oldUnitPriceTrend;

    /** 全部付款金额 */
    private Double totalPaidMoneyTrend;
    /** 新付款金额 */
    private Double newPaidMoneyTrend;
    /** 老付款金额 */
    private Double oldPaidMoneyTrend;

    /** 全部客户数占比 */
    private Double transRateTrend;
    /** 新客户数占比 */
    private Double newTransRateTrend;
    /** 老客户数占比 */
    private Double oldTransRateTrend;

}
