package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

/**
 * @author: 王兵兵
 * @create: 2020-05-20 09:30
 **/
@Data
@ExcelSheet
public class UserCardRenewExportVo {
    /**
     * 续费单号
     */
    @ExcelColumn(columnIndex = 0, columnName = "user.card.renew.renew_order_sn")
    private String renewOrderSn;
    /**
     * 会员卡名称
     */
    @ExcelColumn(columnIndex = 1, columnName = "user.card.renew.card_name")
    private String cardName;
    /**
     * 卡ID
     */
    @ExcelColumn(columnIndex = 2, columnName = "user.card.renew.card_id")
    private Integer cardId;
    /**
     * 会员昵称
     */
    @ExcelColumn(columnIndex = 3, columnName = "user.card.renew.username")
    private String username;
    /**
     * 会员手机号
     */
    @ExcelColumn(columnIndex = 4, columnName = "user.card.renew.mobile")
    private String mobile;
    /**
     * 续费时间
     */
    @ExcelColumn(columnIndex = 5, columnName = "user.card.renew.add_time")
    private Timestamp addTime;
    /**
     * 续费金额
     */
    @ExcelColumn(columnIndex = 6, columnName = "user.card.renew.renew_money")
    private String renewMoneyString;
    /**
     * 续费时长
     */
    @ExcelColumn(columnIndex = 7, columnName = "user.card.renew.renew_time")
    private String renewTimeString;
    /**
     * 当次续费后有效期
     */
    @ExcelColumn(columnIndex = 8, columnName = "user.card.renew.renew_expire_time")
    private Timestamp renewExpireTime;

    /**
     * 续费时长
     */
    @ExcelIgnore
    private Integer renewTime;
    /**
     * 续费时长的单位
     * 0:日，1:周 2: 月
     */
    @ExcelIgnore
    private Byte renewDateType;
    /**
     * 续费金额
     */
    @ExcelIgnore
    private BigDecimal renewMoney;
    /**
     * 续费金额的单位
     * 0:现金 1：积分
     */
    @ExcelIgnore
    private Byte renewType;

}
