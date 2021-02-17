package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The type Order process param.
 *
 * @author liufei
 * @date 2019 /7/11
 */
@Data
public class OrderProcessParam {
    /**
     * The Express.是否启用快递
     */
    @JsonProperty(value = "express")
    public Byte express;
    /**
     * The Fetch.是否启用自提
     */
    @JsonProperty(value = "fetch")
    public Byte fetch;
    /**
     * 门店配送
     */
    @JsonProperty(value = "store_express")
    public Byte storeExpress;
    @JsonProperty(value = "is_lock")
    public Byte isLock;
    /**
     * The Cancel time.拍下未付款订单12小时10分钟内未付款，自动取消订单
     */
    @JsonProperty(value = "cancel_time")
    public Integer cancelTime;
    /**
     * The Drawback days.发货后drawback_days天，自动确认收货
     */
    @JsonProperty(value = "drawback_days")
    public Integer drawbackDays;
    /**
     * The Order timeout days.确认收货后order_timeout_days天，订单完成
     */
    @JsonProperty(value = "order_timeout_days")
    public Integer orderTimeoutDays;
    /**
     * The Extend receive goods.申请延长收货配置,开关开启，用户可在前端申请延长收货时间
     */
    @JsonProperty(value = "extend_receive_goods")
    public Byte extendReceiveGoods;
    /**
     * The Extend receive days.用户对单笔订单可申请一次延长收货时间，申请后可延长3天,默认延长3天,上限为30天
     */
    @JsonProperty(value = "extend_receive_days")
    public Integer extendReceiveDays;
    /**
     * The Invoice.发票展示设置,开关开启，用户在购买时可以使用发票功能
     */
    @JsonProperty(value = "invoice")
    public Byte invoice;
    /**
     * The Service terms.服务条款设置开关开启，结算页会展示服务条款，用户需勾选“同意”才可继续下单
     */
    @JsonProperty(value = "service_terms")
    public Byte serviceTerms;
    /**
     * The Service name.服务条款名称，展示在结算页的服务条款名称
     */
    @JsonProperty(value = "service_name")
    public String serviceName;
    /**
     * The Service choose.服务条款设置首次下单是否默认勾选
     */
    @JsonProperty(value = "service_choose")
    public Byte serviceChoose;
    /**
     * The Order real name.下单人真实姓名
     */
    @JsonProperty(value = "order_real_name")
    public Byte orderRealName;
    /**
     * The Order cid.下单人身份证号码
     */
    @JsonProperty(value = "order_cid")
    public Byte orderCid;
    /**
     * The Consignee real name.收货人真实姓名
     */
    @JsonProperty(value = "consignee_real_name")
    public Byte consigneeRealName;
    /**
     * The Consignee cid.收货人身份证号码
     */
    @JsonProperty(value = "consignee_cid")
    public Byte consigneeCid;
    /**
     * The Custom.自定义信息
     */
    @JsonProperty(value = "custom")
    public Byte custom;
    /**
     * The Custom title.自定义信息标题：限制输入不超过6个字
     */
    @JsonProperty(value = "custom_title")
    public String customTitle;
    /**
     * The Order require goods package.选择下单需要填写必填信息的商品
     */
    @JsonProperty(value = "order_require_goods_package")
    public GoodsPackageParam orderRequireGoodsPackage;
    /**
     * The Shipping express.微信物流助手对接配置
     */
    @JsonProperty(value = "shipping_express")
    public Byte shippingExpress;
    /**
     * The Shop address.发货地址
     */
    @JsonProperty(value = "shop_address")
    public ShopAddress shopAddress;
    /**
     * city_service 同城配送
     */
    @JsonProperty(value = "city_service")
    public Byte cityService;
}
