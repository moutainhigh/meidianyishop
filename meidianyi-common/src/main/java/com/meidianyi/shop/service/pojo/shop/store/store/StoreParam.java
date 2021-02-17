package com.meidianyi.shop.service.pojo.shop.store.store;

import com.meidianyi.shop.service.pojo.wxapp.store.ValidCon;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

/**
 * @author 王兵兵
 *
 * 2019年7月9日
 */
@Data
@NoArgsConstructor
public class StoreParam {
    @Positive(groups = ValidCon.class)
    private Integer   storeId;
	private Integer   posShopId;
    @Positive(groups = ValidCon.class)
    private Integer userId;
}
