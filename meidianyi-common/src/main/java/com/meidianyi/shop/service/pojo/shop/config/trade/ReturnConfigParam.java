package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/7/10
 */
@Data
public class ReturnConfigParam {
    @JsonProperty(value = "auto_return")
    public Byte autoReturn;
    @JsonProperty(value = "auto_return_time")
    public Timestamp autoReturnTime;
    @JsonProperty(value = "return_money_days")
    public Byte returnMoneyDays;
    @JsonProperty(value = "return_address_days")
    public Byte returnAddressDays;
    @JsonProperty(value = "return_shipping_days")
    public Byte returnShippingDays;
    @JsonProperty(value = "return_pass_days")
    public Byte returnPassDays;
    @JsonProperty(value = "is_refund_coupon")
    public Byte isReturnCoupon;
    @JsonProperty(value = "business_address")
    public ReturnBusinessAddressParam businessAddress;
    @JsonProperty(value = "post_sale_status")
    public Byte postSaleStatus;
    @JsonProperty(value = "order_can_exchange")
    public Byte orderCanExchange;
    @JsonProperty(value = "return_change_goods_status")
    public Byte returnChangeGoodsStatus;
    @JsonProperty(value = "order_return_goods_package")
    public GoodsPackageParam orderReturnGoodsPackage;
    @JsonProperty(value = "auto_return_goods_stock")
    public Byte autoReturnGoodsStock;
}
