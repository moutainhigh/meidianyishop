package com.meidianyi.shop.service.shop.member.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelBinder;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户导入异常处理
 * 
 * @author zhaojianqiang
 * @time 上午9:23:48
 */
@Slf4j
public class UserImExcelWrongHandler implements IllegalExcelHandler {
	List<Row> rows = new ArrayList<>();

	@Override
	public void handleIllegalParse(IllegalExcelBinder binder) {
		switch (binder.getIllegalExcelEnum()) {
		case ILLEGEL_SHEET_POSITION:
			log.info("sheet位置错误");
			break;
		case ILLEGAL_SHEET_HEAD:
			log.info("sheet 头信息错误");
			break;
		case SHEET_HEAD_NULL:
			log.info("sheet 没头信息");
			break;
		case ILLEGAL_SHEET_DATA:
			log.info(binder.getUserDataRow().getRowNum() + "行，数据错误");
			rows.add(binder.getUserDataRow());
			break;
		default:
			break;
		}

	}

}
