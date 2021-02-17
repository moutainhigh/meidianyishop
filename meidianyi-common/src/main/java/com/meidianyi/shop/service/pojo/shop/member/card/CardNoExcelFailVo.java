package com.meidianyi.shop.service.pojo.shop.member.card;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumnNotNull;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
 * 导入失败的
 * @author zhaojianqiang
 * @time   下午3:01:52
 */
@Data
@ExcelSheet
public class CardNoExcelFailVo {
	/** 领取码 */
	@ExcelColumn(columnIndex = 0, columnName = "cardReceive.import.cardNo")
	@ExcelColumnNotNull
	private String code;
	
	/** 错误信息 */
	@ExcelColumn(columnIndex = 1, columnName = "user.import.errorMsg")
	@ExcelColumnNotNull
	private String errorMsg;
}
