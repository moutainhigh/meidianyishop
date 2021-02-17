package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import lombok.Getter;

/**
 * @author zhaojianqiang
 */
@Getter
public class CalendarAction {
	public static final String ADD = "add";
	public static final String EDIT = "edit";
	/** 未同步 */
	public static final Byte ZERO = (byte) 0;

	/** 未开始 */
	public static final Byte ONE = (byte) 1;
	/** 进行中 */
	public static final Byte TWO = (byte) 2;
	/** 失效 */
	public static final Byte THREE = (byte) 3;
	/** 已结束 */
	public static final Byte FOUR = (byte) 4;

	public static final String ID = "id";
	public static final String ACTNAME = "actName";
	public static final String STARTTIME = "startTime";
	public static final String ENDTIME = "endtime";
	/** 1代表永久有效 */
	public static final String ISPERMANENT = "isPermanent";

	public static final String INFO = "info";
	public static final String LIST = "list";

	public static final String OVERVIEW = "overview";
}
