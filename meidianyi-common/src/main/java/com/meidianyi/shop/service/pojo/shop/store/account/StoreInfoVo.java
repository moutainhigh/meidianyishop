package com.meidianyi.shop.service.pojo.shop.store.account;

import java.util.List;

import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 已经设置的店铺和总店铺
 * 
 * @author zhaojianqiang
 * @time 下午2:19:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfoVo {
	/** 自己有的*/
	List<StoreInfo> haveStore;
	/** 自己没有的*/
	List<StoreInfo> lastStore;
	/** 总的*/
	List<StoreInfo> allStore;
}
