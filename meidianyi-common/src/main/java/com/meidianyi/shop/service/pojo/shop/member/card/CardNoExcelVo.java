package com.meidianyi.shop.service.pojo.shop.member.card;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumnNotNull;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
 * 会员卡领取码
 * @author zhaojianqiang
 * @time   下午2:29:52
 */
@Data
@ExcelSheet
public class CardNoExcelVo {
	/** 领取码 */
	@ExcelColumn(columnIndex = 0, columnName = "cardReceive.import.cardNo")
	@ExcelColumnNotNull
	private String code;
}
