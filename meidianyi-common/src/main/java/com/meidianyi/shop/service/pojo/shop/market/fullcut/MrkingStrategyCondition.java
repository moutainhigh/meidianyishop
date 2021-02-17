package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import lombok.Data;

import java.math.BigDecimal;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;

/**
 * @author: 王兵兵
 * @create: 2019-08-09 16:38
 **/
@Data
public class MrkingStrategyCondition implements Comparable<MrkingStrategyCondition> {

    /** 满多少金额 */
    private BigDecimal fullMoney;

    /** 减多少钱 */
    private BigDecimal
        reduceMoney;

    /** 满几件 或 第几件（第X件打折） */
    private Integer amount;

    /** 打几折 */
    private BigDecimal discount;

    @Override
    public int compareTo(MrkingStrategyCondition o) {
        //倒序
        if(getFullMoney() != null && BigDecimalUtil.compareTo(getFullMoney(), null) > 0 && o.getFullMoney() != null && BigDecimalUtil.compareTo(o.getFullMoney(), null) > 0) {
            return getFullMoney().compareTo(o.getFullMoney()) > 0 ? -1 : 1;
        }
        if(getAmount() != null && getAmount() > 0 && o.getAmount() != null && o.getAmount() > 0) {
            return getAmount().compareTo(o.getAmount()) > 0 ? -1 : 1;
        }
        //正常情况不会
        return 0;
    }
}
