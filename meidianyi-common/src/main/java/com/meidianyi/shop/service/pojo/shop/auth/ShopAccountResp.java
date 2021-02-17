package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopAccountResp {

	public List<?> dataList;
	public Page page;
	

}
