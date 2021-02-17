package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

/**
 * @author: 王兵兵
 * @create: 2020-05-20 16:35
 **/
@Data
@ExcelSheet
public class UserCardChargeExportVo {

    /**
     * 充值单号
     */
    @ExcelColumn(columnIndex = 0, columnName = "user.card.charge.order_sn")
    private String orderSn;
    /**
     * 会员卡名称
     */
    @ExcelColumn(columnIndex = 1, columnName = "user.card.charge.card_name")
    private String cardName;
    /**
     * 卡ID
     */
    @ExcelColumn(columnIndex = 2, columnName = "user.card.charge.card_id")
    private Integer cardId;
    /**
     * 会员昵称
     */
    @ExcelColumn(columnIndex = 3, columnName = "user.card.charge.username")
    private String username;
    /**
     * 会员手机号
     */
    @ExcelColumn(columnIndex = 4, columnName = "user.card.charge.mobile")
    private String mobile;
    /**
     * 充值时间
     */
    @ExcelColumn(columnIndex = 5, columnName = "user.card.charge.create_time")
    private Timestamp createTime;
    /**
     * 充值金额
     */
    @ExcelColumn(columnIndex = 6, columnName = "user.card.charge.charge")
    private BigDecimal charge;
    /**
     * 当次充值后卡余额
     */
    @ExcelColumn(columnIndex = 7, columnName = "user.card.charge.after_charge_money")
    private BigDecimal afterChargeMoney;
    /**
     * 充值原因
     */
    @ExcelColumn(columnIndex = 8, columnName = "user.card.charge.reason")
    private String reason;

    /**
     *
     */
    @ExcelIgnore
    private Integer reasonId;

}
