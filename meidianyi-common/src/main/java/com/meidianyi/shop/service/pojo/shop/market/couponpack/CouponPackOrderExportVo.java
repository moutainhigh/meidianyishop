package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-21 17:32
 **/
@ExcelSheet
@Data
public class CouponPackOrderExportVo {

    /** 订单编号 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_ORDER_SN,columnIndex = 0)
    private String orderSn;

    /** 实际微信付款 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_MONEY_PAID,columnIndex = 1)
    private BigDecimal moneyPaid;

    /** 使用余额支付数 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_USE_ACCOUNT,columnIndex = 2)
    private BigDecimal useAccount;

    /** 使用积分支付数 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_USE_SCORE,columnIndex = 3)
    private BigDecimal useScore;

    /** 使用会员卡余额支付数 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_MEMBER_CARD_BALANCE,columnIndex = 4)
    private BigDecimal memberCardBalance;

    /** 昵称 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_USERNAME,columnIndex = 5)
    private String username;

    /** 手机号 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_MOBILE,columnIndex = 6)
    private String mobile;

    /** 下单时间 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_CREATE_TIME,columnIndex = 7)
    private Timestamp createTime;

    /** 订单状态 */
    @ExcelColumn(columnName = JsonResultMessage.COUPON_PACK_ORDER_STATUS,columnIndex = 8)
    private String statusName;

    /**
     *  状态 0待付款，1订单完成
     */
    @ExcelIgnore
    private Byte orderStatus;
}
