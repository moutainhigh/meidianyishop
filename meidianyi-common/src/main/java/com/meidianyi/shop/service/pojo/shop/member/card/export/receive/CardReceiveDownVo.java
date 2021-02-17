package com.meidianyi.shop.service.pojo.shop.member.card.export.receive;

import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
 * 下载
 * 
 * @author zhaojianqiang
 * @date 2020年5月14日下午4:24:53
 */
@ExcelSheet
@Data
public class CardReceiveDownVo {
	/** - 批次名称 */
	@ExcelColumn(columnIndex = 0, columnName = "card.export.name")
	private String name;
	/** - 会员卡领取码表id */
	@ExcelColumn(columnIndex = 1, columnName = "card.export.id")
	private Integer id;

	/** - 用户名 */
	@ExcelColumn(columnIndex = 2, columnName = "card.export.username")
	private String username;
	/** -手机号 */
	@ExcelColumn(columnIndex = 3, columnName = "card.export.mobile")
	private String mobile;
	/** - 领取时间 */
	@ExcelColumn(columnIndex = 4, columnName = "card.export.receiveTime")
	private Timestamp receiveTime;
	/** - 卡号 */
	@ExcelIgnore
	private String cardNo;
	/** -领取码 */
	@ExcelIgnore
	private String code;
	/** -卡密码 */
	@ExcelIgnore
	private String cardPwd;
	/** 领取码 / 卡号+密码 */
	@ExcelColumn(columnIndex = 5, columnName = "card.export.cardMsg")
	private String cardMsg;
	@ExcelIgnore
	private Byte delFlag;

	/** 领取状态 1：已领取 2：未领取 */
	@ExcelColumn(columnIndex = 6, columnName = "card.export.reveiveStatus")
	private String sReveiveStatus;
	@ExcelIgnore
	private Byte reveiveStatus;
	/** 使用状态 1：正常 2：已废除 */
	@ExcelColumn(columnIndex = 7, columnName = "card.export.delStatus")
	private String sDelStatus;
	@ExcelIgnore
	private Byte delStatus;

}
