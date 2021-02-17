package com.meidianyi.shop.service.pojo.wxapp.order;

import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 下单vo
 * @author 王帅
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderVo {
    private String orderSn;
    private WebPayVo webPayVo;
}
