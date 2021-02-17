package com.meidianyi.shop.service.pojo.shop.member;
/**
* @author 黄壮壮
* @Date: 2019年9月4日
* @Description: 用于存放与会员相关的字段状态信号
*/
public class MemberConstant {
	/** 会员是否被删除 | 禁止登录 */
	public static final Byte DELETE_YES = 1;
	public static final Byte DELETE_NO = 0;
	
	/** 禁止登录 */
	public static final Byte LOGIN_FORBID = 1;
	/** 允许登录 */
	public static final Byte LOGIN_PERMIT = 0;
	public static final String INVITE_USERNAME = "inviteUserName";
	public static final String INVITE_MOBILE = "inviteUserMobile";
	
	/** 用于计算时间范围 */
	public static final int MONTH_DAYS = 30;
	public static final int YEAR_DAYS = 365;
	
	/** 时间标记 */
	public static final String DAY_FLAG = "-D";
	public static final String MONTH_FLAG = "-M";
	public static final String ONE_MONTH_FLAG = "1-M";
	public static final String YEAR_FLAG = "-Y";
	
	
}
