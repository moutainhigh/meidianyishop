package com.meidianyi.shop.service.pojo.shop.store.authority;

import lombok.Data;

import java.util.List;

/**
 * 
 * @author zhaojianqiang
 * @time 下午3:22:19
 */
@Data
public class StoreAuthListPage {
	private Integer currentPage;
	private Integer pageRows;
	private String accountName;
	private String mobile;
	private Byte accountType = 0;
	private Byte status = -1;
	private List<Integer> storeIds;
}
