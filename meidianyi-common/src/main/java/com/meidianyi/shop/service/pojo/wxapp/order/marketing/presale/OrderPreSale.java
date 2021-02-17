package com.meidianyi.shop.service.pojo.wxapp.order.marketing.presale;

import com.meidianyi.shop.service.pojo.shop.market.presale.PreSaleVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.base.BaseMarketingBaseVo;
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
public class OrderPreSale extends BaseMarketingBaseVo {
    PreSaleVo info;
    /**定金金额*/
    BigDecimal totalPreSaleMoney;
}
