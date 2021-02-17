package com.meidianyi.shop.service.pojo.shop.store.store;

import lombok.Data;

/**
 * 门店自提详情json对象类
 * @author liangchen
 * @date 2020.05.12
 */
@Data
public class StorePickDetailPojo {
    /** 时长 */
    private Integer duration;
    /** 时间类型 1小时 2天*/
    private Byte type;
}
