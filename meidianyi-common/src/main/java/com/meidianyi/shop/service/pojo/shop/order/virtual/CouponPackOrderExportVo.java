package com.meidianyi.shop.service.pojo.shop.order.virtual;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-04-29 15:29
 **/
@ExcelSheet
@Data
public class CouponPackOrderExportVo {

    /**
     * 优惠劵礼包名称 b2c_coupon_pack
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_PACK_NAME, columnIndex = 0)
    private String packName;
    /**
     * 订单号
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_ORDER_SN, columnIndex = 1)
    private String orderSn;
    /**
     * 单价
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_PRICE, columnIndex = 2)
    private String price;

    /**
     * 用户名称
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_USERNAME, columnIndex = 3)
    private String username;
    /**
     * 用户电话
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_MOBILE, columnIndex = 4)
    private String mobile;
    /**
     * 下单时间
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_CREATE_TIME, columnIndex = 5)
    private Timestamp createTime;

    /**
     * 订单付款现金金额
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_MONEY_PAID, columnIndex = 6)
    private BigDecimal moneyPaid;
    /**
     * 用户余额消费金额
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_USE_ACCOUNT, columnIndex = 7)
    private BigDecimal useAccount;

    /**
     * 用户消费的会员卡 金额
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_MEMBER_CARD_BALANCE, columnIndex = 8)
    private BigDecimal memberCardBalance;

    /**
     * 用户消费的积分
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_USE_SCORE, columnIndex = 9)
    private Integer useScore;

    /**
     * 订单状态
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_ORDER_STATUS_NAME, columnIndex = 10)
    private String orderStatusName;

    /**
     * 退款时间
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_RETURN_TIME, columnIndex = 11)
    private Timestamp returnTime;

    /**
     * 已退积分
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_RETURN_SCORE, columnIndex = 12)
    private Integer returnScore;
    /**
     * 已退余额
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_RETURN_ACOUNT, columnIndex = 13)
    private BigDecimal returnAccount;
    /**
     * 已退现金
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_RETURN_MONEY, columnIndex = 14)
    private BigDecimal returnMoney;
    /**
     * 会员卡已退金额
     */
    @ExcelColumn(columnName = JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_RETURN_CARD_BALANCE, columnIndex = 15)
    private BigDecimal returnCardBalance;

    @ExcelIgnore
    private Byte orderStatus;
    @ExcelIgnore
    private Byte returnFlag;
    /**
     * 币种
     */
    @ExcelIgnore
    private String currency;
    @ExcelIgnore
    private BigDecimal orderAmount;
}
