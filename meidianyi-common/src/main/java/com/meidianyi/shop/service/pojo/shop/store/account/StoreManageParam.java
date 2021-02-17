package com.meidianyi.shop.service.pojo.shop.store.account;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 管理和创建的入参
 * 
 * @author zhaojianqiang
 * @time 下午2:27:58
 */
@Data
public class StoreManageParam {
	/** StoreConstant */
	@NotNull
	private String act;
	@NotNull
	private Integer accountId;
	private Integer[] storeList;

}
