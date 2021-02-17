package com.meidianyi.shop.service.pojo.shop.market.bargain;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年7月26日
 */
@ExcelSheet
@Data
public class BargainRecordExportVo {
	
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_GOODS_NAME,columnIndex = 0)
	private String goodsName;
	
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_USERNAME,columnIndex = 1)
	private String username;
	
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_MOBILE,columnIndex = 2)
	private String mobile;
	
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_CREATE_TIME,columnIndex = 3)
	private Timestamp createTime;
	
	/**
	 * 已砍金额 
	 */
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_BARGAIN_MONEY,columnIndex = 4)
	private BigDecimal bargainMoney;
	
	/**
	 * 待砍金额 
	 */
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_SURPLUS_MONEY,columnIndex = 5)
	private BigDecimal surplusMoney;
	
	/**
	 * 参与砍价人数 
	 */
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_USER_NUMBER,columnIndex = 6)
	private Integer userNumber;
	
	/**
	 *  状态 0砍价中，1成功，2失败
	 */
	@ExcelColumn(columnName = JsonResultMessage.BARGAIN_RECORD_LIST_STATUS,columnIndex = 7)
	private String statusName;
	
	/**
	 *  状态 0砍价中，1成功，2失败
	 */
	@ExcelIgnore
	private Byte status;
	
	/**
	 * 砍价类型0定人1任意价
	 */
	@ExcelIgnore
	private Byte bargainType;

    @ExcelIgnore
    private Integer goodsId;
	
	/**
	 *  任意低价
	 */
	@ExcelIgnore
	private BigDecimal floorPrice;
	
	@ExcelIgnore
	private BigDecimal goodsPrice;
	
	/**
	 * 固定人数模式， 预期砍价最低金额
	 */
	@ExcelIgnore
	private BigDecimal expectationPrice;
}
