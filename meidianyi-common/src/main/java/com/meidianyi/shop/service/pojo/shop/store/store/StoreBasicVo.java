package com.meidianyi.shop.service.pojo.shop.store.store;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

/**
* @author 黄壮壮
* @Date: 2019年9月3日
* @Description: 获取门店弹窗出参
*/
@Data
public class StoreBasicVo {
	@JsonProperty("value")
    @JsonAlias({"storeId", "store_id"})
    @PositiveOrZero(groups = AppletStoreInfo.class)
	private Integer storeId;
	@JsonProperty("label")
	private String storeName;
	private String storeCode;
}
