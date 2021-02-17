package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead;

import com.meidianyi.shop.service.pojo.wxapp.order.OrderInfoMpVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 代付支付页
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class InsteadPayVo {
    private Byte isShowEdit;
    private BigDecimal moneyPaid;
    private Object[][] threeStages;
    private String message;
    private BigDecimal amountPaid;
    private BigDecimal waitPayMoney;
    private OrderInfoMpVo order;
    private Byte isSelf;
}
