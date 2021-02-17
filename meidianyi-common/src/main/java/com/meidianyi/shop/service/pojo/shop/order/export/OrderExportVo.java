package com.meidianyi.shop.service.pojo.shop.order.export;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-10-15 11:27
 *
 * @description 订单导出所有可选的表头
 **/
@ExcelSheet
@Data
public class OrderExportVo {
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ORDER_SN,columnIndex = 0)
    private String orderSn;  //订单号
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ORDER_STATUS_NAME,columnIndex = 1)
    private String orderStatusName;  //订单状态
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PAY_NAMES,columnIndex = 2)
    private String payNames;  //支付方式
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CREATE_TIME,columnIndex = 3)
    private Timestamp createTime;  //订单提交时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PAY_TIME,columnIndex = 4)
    private Timestamp payTime;  //支付时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CLOSE_TIME,columnIndex = 5)
    private Timestamp closedTime;  //订单关闭时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CANCELLED_TIME,columnIndex = 6)
    private Timestamp cancelledTime;  //订单取消时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_FINISHED_TIME,columnIndex = 7)
    private Timestamp finishedTime;  //订单完成时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_IS_COD,columnIndex = 8)
    private String isCodString;  //是否货到付款
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CONSIGNEE,columnIndex = 9)
    private String consignee;  //收货人姓名
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_MOBILE,columnIndex = 10)
    private String mobile;  //手机号
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_COMPLETE_ADDRESS,columnIndex = 11)
    private String completeAddress;  //收货地址
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PROVINCE_NAME,columnIndex = 12)
    private String provinceName;  //收货省份
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CITY_NAME,columnIndex = 13)
    private String cityName;  //收货城市
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_DISTRICT_NAME,columnIndex = 14)
    private String districtName;  //收货地区
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ZIPCODE,columnIndex = 15)
    private String zipcode;  //邮政编码
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_USER_NAME,columnIndex = 16)
    private String userName;  //下单人姓名
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_USER_MOBILE,columnIndex = 17)
    private String userMobile;  //下单人手机号
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_IS_NEW,columnIndex = 18)
    private String isNew;  //新老用户
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_USER_SOURCE,columnIndex = 19)
    private String userSourceString;  //下单人来源
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_USER_TAG,columnIndex = 20)
    private String userTag;  //下单人标签
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ADD_MESSAGE,columnIndex = 21)
    private String addMessage;  //下单人留言
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SHIPPING_TIME,columnIndex = 22)
    private Timestamp shippingTime;  //发货时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SHIPPING_NAME,columnIndex = 23)
    private String shippingName;  //货运名称
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SHIPPING_NO,columnIndex = 24)
    private String shippingNo;  //物流单号
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_DELIVER_TYPE_NAME,columnIndex = 25)
    private String deliverTypeName;  //配送类型
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CONFIRM_TIME,columnIndex = 26)
    private Timestamp confirmTime;  //确认收货时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_STORE_ID,columnIndex = 27)
    private Integer storeId;  //门店ID
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_STORE_NAME,columnIndex = 28)
    private String storeName;  //门店名称
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_GOODS_NAME,columnIndex = 29)
    private String goodsName;  //商品名称
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PRODUCT_SN,columnIndex = 30)
    private String productSn;  //商家编码
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_GOODS_NUMBER,columnIndex = 31)
    private Integer goodsNumber;  //商品数量
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_DISCOUNTED_GOODS_PRICE,columnIndex = 32)
    private BigDecimal discountedGoodsPrice;  //实际售价
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_GOODS_ATTR,columnIndex = 33)
    private String goodsAttr;  //SKU属性
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_GOODS_PRICE,columnIndex = 34)
    private BigDecimal goodsPrice;  //商品售价
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_MARKET_PRICE,columnIndex = 35)
    private BigDecimal marketPrice;  //商品市场价
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_GOODS_SN,columnIndex = 36)
    private String goodsSn;  //商品货号
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_GOODS_ID,columnIndex = 37)
    private Integer goodsId;  //商品ID
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SEND_NUMBER,columnIndex = 38)
    private Integer sendNumber;  //已发货数量
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_RETURN_NUMBER,columnIndex = 39)
    private Integer returnNumber;  //退货数量
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SOURCE,columnIndex = 40)
    private String source;  //商品来源
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PRD_COST_PRICE,columnIndex = 41)
    private BigDecimal prdCostPrice;  //成本价
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PRD_WEIGHT,columnIndex = 42)
    private BigDecimal prdWeight;  //SKU重量
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ORDER_AMOUNT,columnIndex = 43)
    private BigDecimal orderAmount;  //订单总金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_DISCOUNT,columnIndex = 44)
    private BigDecimal discount;  //优惠券优惠金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SHIPPING_FEE,columnIndex = 45)
    private BigDecimal shippingFee;  //邮费
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SCORE_DISCOUNT,columnIndex = 46)
    private BigDecimal scoreDiscount;  //积分抵扣金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_USE_ACCOUNT,columnIndex = 47)
    private BigDecimal useAccount;  //使用账户余额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_MONEY_PAID,columnIndex = 48)
    private BigDecimal moneyPaid;  //微信支付金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_MEMBER_CARD_BALANCE,columnIndex = 49)
    private BigDecimal memberCardBalance;  //使用会员卡余额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_MEMBER_CARD_REDUCE,columnIndex = 50)
    private BigDecimal memberCardReduce;  //会员卡抵扣金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_PROMOTION_REDUCE,columnIndex = 51)
    private BigDecimal promotionReduce;  //满折满减优惠金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_RETURN_TIME,columnIndex = 52)
    private Timestamp returnTime;  //申请退货时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_RETURN_FINISH_TIME,columnIndex = 53)
    private Timestamp returnFinishTime;  //退货完成时间
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_RETURN_ORDER_MONEY,columnIndex = 54)
    private BigDecimal returnOrderMoney;  //退款金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_RETURN_SHIPPING_FEE,columnIndex = 55)
    private BigDecimal returnShippingFee;  //退运费金额
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_SELLER_REMARK,columnIndex = 56)
    private String sellerRemark;  //卖家备注
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ORDER_REAL_NAME,columnIndex = 57)
    private String orderRealName;  //真实姓名
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_ORDER_CID,columnIndex = 58)
    private String orderCid;  //身份证号
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_CUSTOM,columnIndex = 59)
    private String custom;  //自定义下单必填信息
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_REBATE_LEVEL_ONE,columnIndex = 60)
    private BigDecimal rebateLevelOne;  //一级返利
    @ExcelColumn(columnName = JsonResultMessage.ORDER_EXPORT_COLUMN_REBATE_LEVEL_TWO,columnIndex = 61)
    private BigDecimal rebateLevelTwo;  //二级返利

    @ExcelIgnore
    private Integer userId;
    @ExcelIgnore
    private Integer productId;
    @ExcelIgnore
    private Integer recId;
    @ExcelIgnore
    private Byte isCod;
    @ExcelIgnore
    private Byte orderStatus;
    /** 用户来源 */
    @ExcelIgnore
    private Integer userSource;
    /** 部分发货标记 */
    @ExcelIgnore
    private Byte partShipFlag;
    /** 用户邀请来源 */
    @ExcelIgnore
    private String inviteSource;
    /** 邀请来源活动id */
    @ExcelIgnore
    private Integer inviteActId;
    /** 配送类型 */
    @ExcelIgnore
    private Byte deliverType;
    /** 物流公司ID */
    @ExcelIgnore
    private Byte shippingId;

    public static final String ORDER_SN = "orderSn";
    public static final String ORDER_STATUS_NAME = "orderStatusName";
    public static final String PAY_NAMES = "payNames";
    public static final String CREATE_TIME = "createTime";
    public static final String PAY_TIME = "payTime";
    public static final String CLOSED_TIME = "closedTime";
    public static final String CANCELLED_TIME = "cancelledTime";
    public static final String FINISHED_TIME = "finishedTime";
    public static final String IS_COD = "isCodString";
    public static final String CONSIGNEE = "consignee";
    public static final String MOBILE = "mobile";
    public static final String COMPLETE_ADDRESS = "completeAddress";
    public static final String PROVINCE_NAME = "provinceName";
    public static final String CITY_NAME = "cityName";
    public static final String DISTRICT_NAME = "districtName";
    public static final String ZIPCODE = "zipcode";
    public static final String USER_NAME = "userName";
    public static final String USER_MOBILE = "userMobile";
    public static final String IS_NEW = "isNew";
    public static final String USER_SOURCE = "userSourceString";
    public static final String USER_TAG = "userTag";
    public static final String ADD_MESSAGE = "addMessage";
    public static final String SHIPPING_TIME = "shippingTime";
    public static final String SHIPPING_NAME = "shippingName";
    public static final String SHIPPING_NO = "shippingNo";
    public static final String DELIVER_TYPE_NAME = "deliverTypeName";
    public static final String CONFIRM_TIME = "confirmTime";
    public static final String STORE_NAME = "storeName";
    public static final String GOODS_NAME = "goodsName";
    public static final String PRODUCT_SN = "productSn";
    public static final String GOODS_NUMBER = "goodsNumber";
    public static final String DISCOUNT_GOODS_PRICE = "discountedGoodsPrice";
    public static final String GOODS_ATTR = "goodsAttr";
    public static final String GOODS_PRICE = "goodsPrice";
    public static final String MARKET_PRICE = "marketPrice";
    public static final String GOODS_SN = "goodsSn";
    public static final String GOODS_ID = "goodsId";
    public static final String SEND_NUMBER = "sendNumber";
    public static final String RETURN_NUMBER = "returnNumber";
    public static final String SOURCE = "source";
    public static final String PRD_COST_PRICE = "prdCostPrice";
    public static final String PRD_WEIGHT = "prdWeight";
    public static final String ORDER_AMOUNT = "orderAmount";
    public static final String DISCOUNT = "discount";
    public static final String SHIPPING_FEE = "shippingFee";
    public static final String SCORE_DISCOUNT = "scoreDiscount";
    public static final String USE_ACCOUNT = "useAccount";
    public static final String MONEY_PAID = "moneyPaid";
    public static final String MEMBER_CARD_BALANCE = "memberCardBalance";
    public static final String MEMBER_CARD_REDUCE = "memberCardReduce";
    public static final String PROMOTION_REDUCE = "promotionReduce";
    public static final String RETURN_TIME = "returnTime";
    public static final String RETURN_FINISH_TIME = "returnFinishTime";
    public static final String RETURN_ORDER_MONEY = "returnOrderMoney";
    public static final String RETURN_SHIPPING_FEE = "returnShippingFee";
    public static final String SELLER_REMARK = "sellerRemark";
    public static final String ORDER_REAL_NAME = "orderRealName";
    public static final String ORDER_CID = "orderCid";
    public static final String CUSTOM = "custom";
    public static final String REBATE = "rebate";
    public static final String REBATE_LEVEL_ONE = "rebateLevelOne";
    public static final String REBATE_LEVEL_TWO = "rebateLevelTwo";

    public static final String LANGUAGE_TYPE_EXCEL = "excel";

}
