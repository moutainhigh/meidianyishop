package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2019-12-31 09:13
 **/
@Getter
@Setter
public class BargainCutVo {
    /**
     * 帮助砍价结果
     * 0砍价成功 12用户砍价次数已达到上限
     */
    private Byte state;
    /**
     * 砍掉的金额
     */
    private BigDecimal bargainMoney;
}
