package com.meidianyi.shop.common.foundation.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 货币单位
 *
 * @author 郑保乐
 */
@Getter
@AllArgsConstructor
public enum CurrencySymbol {

    /**
     * 人民币元
     */
    RMB("￥"),
    /**
     * 美元
     */
    US_DOLLAR("$");

    private final String symbol;
}
