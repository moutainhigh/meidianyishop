package com.meidianyi.shop.service.pojo.shop.store.store;

import lombok.Data;

/**
 * @author liuruilin
 */
@Data
public class StoreConfigVo {
    /**是否启用自提*/
    private Byte fetch;
    /**是否启用同城配送*/
    private Byte cityService;
    /**门店配送*/
    private Byte storeExpress;
}
