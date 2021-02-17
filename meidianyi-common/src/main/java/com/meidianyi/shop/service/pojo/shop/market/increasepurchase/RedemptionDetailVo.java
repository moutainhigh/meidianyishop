package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/15
 * @description
 */
@Data
@ExcelSheet
public class RedemptionDetailVo {
    /** 用户id */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.REDEMPTION_DETAIL_USER_ID)
    private Integer userId;
    /** 昵称 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.REDEMPTION_DETAIL_USERNAME)
    private String username;
    /** 手机号 */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.REDEMPTION_DETAIL_MOBILE)
    private String mobile;
    /** 订单编号 */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.REDEMPTION_DETAIL_ORDER_SN)
    private String orderSn;
    /** 换购时间 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.REDEMPTION_DETAIL_CREATE_TIME)
    private Timestamp createTime;

    /** 辅助参数 */
    @JsonIgnore
    @ExcelIgnore
    private String concatPrices;
    @JsonIgnore
    @ExcelIgnore
    private String concatNumbers;
    @JsonIgnore
    @ExcelIgnore
    private String activityRules;


    /** 主商品总金额 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.REDEMPTION_DETAIL_MAIN_GOODS_TOTAL_MONEY)
    private BigDecimal mainGoodsTotalMoney;
    /** 换购数量 */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.REDEMPTION_DETAIL_REDEMPTION_NUM)
    private Integer redemptionNum;
    /** 换购总金额 */
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.REDEMPTION_DETAIL_REDEMPTION_TOTAL_MONEY)
    private BigDecimal redemptionTotalMoney;

}
