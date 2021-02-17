package com.meidianyi.shop.service.pojo.saas.shop;

/**
 * 
 * @author zhaojianqiang
 *
 */
public class ShopConst {

	/**
	 * 店铺等级
	 */
	public interface ShopType {
		final static String V_1 = "v1";
		final static String V_2 = "v2";
		final static String V_3 = "v3";
		final static String V_4 = "v4";
	}

	/**
	 * 
	 * 1:体验版 2:付费版
	 */
	public interface ShopTypes {
		final static String TRIAL_VERSION = "1";
		final static String PAID_VERSION = "2";
	}
}
