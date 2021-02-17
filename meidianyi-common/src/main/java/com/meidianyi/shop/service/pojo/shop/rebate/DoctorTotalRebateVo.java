package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.pojo.shop.table.DoctorTotalRebateDo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Data
public class DoctorTotalRebateVo extends DoctorTotalRebateDo {
    /**
     * 累计提现金额
     */
    private BigDecimal accruingWithdrawCash=BigDecimal.ZERO;
    /**
     * 待提现金额
     */
    private BigDecimal waitWithdrawCash=BigDecimal.ZERO;

    /**
     *可提现最小额度
     */
    private BigDecimal withdrawCashMix;
    /**
     * 每日可提现最大额度
     */
    private BigDecimal withdrawCashMax;
}
