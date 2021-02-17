package com.meidianyi.shop.service.pojo.saas.shop;

import lombok.Data;

/**
 * 店铺发布列表入参
 * @author zhaojianqiang
 *
 * 2019年11月22日 上午11:30:59
 */
@Data
public class ShopMpListParam {
	/**店铺ID或者店铺名称 */
	private String keywords;
	/**店铺类型*/
	private String shopType;
	/**支付类型*/
	private Byte openPay;
	/**禁用类型 */
	private Byte isEnabled;
	
    private Integer currentPage;
    private Integer pageRows;
}
