package com.meidianyi.shop.service.pojo.shop.distribution;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** *
 * 佣金统计出参
 * @author 常乐
 * 2019年8月8日
 */
@ExcelSheet
@Data
public class BrokerageListExportVo {
    /** 分销员ID*/
    @ExcelIgnore
    private Integer distributorId;
    @ExcelIgnore
    private Integer rebateLevel;
    @ExcelIgnore
    private Integer settlementFlag;
    /** 下单用户id*/
    @ExcelIgnore
    private Integer userId;
    /** 分销员昵称*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_DISTRIBUTOR_NAME,columnIndex = 0)
	private String distributorName;
	/** 分销员手机号*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_DISTRIBUTOR_MOBILE,columnIndex = 1)
	private String distributorMobile;
	/** 真实姓名*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_REAL_NAME,columnIndex = 2)
	private String realName;
	/** 分组名称*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_GROUP_NAME,columnIndex = 3)
	private String groupName;
	/** 返利订单号*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_ORDER_SN,columnIndex = 4)
	private String orderSn;
	/** 订单金额*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_ORDER_AMOUNT,columnIndex = 5)
	private BigDecimal orderAmount;
	/** 下单人手机号*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_USER_MOBILE,columnIndex = 6)
	private String userMobile;
	/** 下单用户昵称*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_USER_NAME,columnIndex = 7)
	private String orderUserName;
	/** 返利关系*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_REBATE_LEVEL,columnIndex = 8)
	private String rebateLevelName;
	/** 返利商品金额*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_TOTAL_REBATE_MONRY,columnIndex = 9)
	private BigDecimal totalRebateMoney;
	/** 返利佣金金额*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_REAL_REBATE_MONRY,columnIndex = 10)
	private BigDecimal realRebateMoney;
	/** 创建时间*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_CREATE_TIME,columnIndex = 11)
	private Timestamp createTime;
	/** *返利状态*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_SETTLEMENT_FLAG,columnIndex = 12)
	private String settlementName;
    /** 返利时间*/
    @ExcelColumn(columnName = JsonResultMessage.BROKERAGE_LIST_REBATE_TIME,columnIndex = 13)
    private Timestamp rebateTime;
}
