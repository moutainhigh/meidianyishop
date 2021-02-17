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
public class CardNoPwdExcelFailVo {
	/** 卡号 */
	@ExcelColumn(columnIndex = 0, columnName = "cardReceive.import.codeNo")
	@ExcelColumnNotNull
	private String codeNo;
	
	
	/** 密码 */
	@ExcelColumn(columnIndex = 1, columnName = "cardReceive.import.cardPwd")
	@ExcelColumnNotNull
	private String cardPwd;
	
	/** 错误信息 */
	@ExcelColumn(columnIndex = 2, columnName = "user.import.errorMsg")
	@ExcelColumnNotNull
	private String errorMsg;
}
