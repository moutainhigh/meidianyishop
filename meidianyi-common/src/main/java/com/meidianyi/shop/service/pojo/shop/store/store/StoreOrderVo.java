package com.meidianyi.shop.service.pojo.shop.store.store;

import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-04 16:48
 **/
@Data
public class StoreOrderVo {

    private Integer   storeId;
    private String    storeName;
    private Byte      businessState;
    private Byte      businessType;
    private Short     autoPick;
    private Byte      delFlag;

}
