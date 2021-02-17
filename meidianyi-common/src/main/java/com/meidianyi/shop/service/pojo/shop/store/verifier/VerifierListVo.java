package com.meidianyi.shop.service.pojo.shop.store.verifier;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王兵兵
 *
 * 2019年7月11日
 */
@Data
@NoArgsConstructor
public class VerifierListVo {
	public Integer userId;
	public String username;
	public String mobile;
	/**
	 * 核销订单数
	 */
	public Integer verifyOrders;
}
