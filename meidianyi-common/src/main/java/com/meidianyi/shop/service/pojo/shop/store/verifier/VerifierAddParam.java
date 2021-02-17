package com.meidianyi.shop.service.pojo.shop.store.verifier;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月12日
 */
@Data
public class VerifierAddParam {
	
	/**
	 * 被设置为核销员的userId数组
	 */
	@NotNull
	@Size(min=1)
	private Integer[] userIds;
	
	@NotNull
	@Min(1)
	private Integer storeId;
}
