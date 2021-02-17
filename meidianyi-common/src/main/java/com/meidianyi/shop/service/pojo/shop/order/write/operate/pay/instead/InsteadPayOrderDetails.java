package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderInfoMpVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class InsteadPayOrderDetails {
    private OrderInfoMpVo order;
    private PageResult<InsteadPayDetailsVo> insteadPayDetails;
    private BigDecimal amountPaid;
    private BigDecimal waitPayMoney;
    private InsteadPay insteadPayCfg;
    private Byte isSelf;
    private String message;
    private UserInfo userInfo;
}
