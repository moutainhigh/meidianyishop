package com.meidianyi.shop.service.pojo.shop.store.authority;

/**
 * 一些值的定义
 * 
 * @author zhaojianqiang
 * @time 上午10:54:44
 */
public class StoreConstant {

	public static final Byte CHECK_ZERO = 0;
	public static final Byte CHECK_ONE = 1;

	public static final Byte ISONLYZ_ERO = 0;
	public static final Byte ISONLY_ONE = 1;
	public static final Byte ISONLY_TWO = 2;
	/** 账户状态1:启用，0：禁用 */
	public static final Byte STATRT = 1;
	public static final Byte STOP = 0;

	/** act的参数 */
	/** config 门店的设置 */
	public static final String ACT_CONFIG = "config";

	/** 删除 */
	public static final String ACT_DEL = "del";

	/** 停用 */
	public static final String ACT_STOP = "stop";
	
	/** 启动 */
	public static final String ACT_STATRT = "start";
	
	/** 添加*/
	public static final String ACT_ADD = "add";
	
	/** 编辑*/
	public static final String ACT_EDIT = "edit";
}
