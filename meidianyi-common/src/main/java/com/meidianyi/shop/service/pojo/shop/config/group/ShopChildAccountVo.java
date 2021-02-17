package com.meidianyi.shop.service.pojo.shop.config.group;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对应shop_child_account
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ShopChildAccountVo {
	private Integer accountId;
	private String accountName;
	private String mobile;	
}
