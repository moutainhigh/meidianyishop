package com.meidianyi.shop.service.pojo.shop.auth;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopRenewListParam {
	private Integer shopId;
	private Integer sysId;
	private Integer currentPage;
	private Integer pageRows;
}
