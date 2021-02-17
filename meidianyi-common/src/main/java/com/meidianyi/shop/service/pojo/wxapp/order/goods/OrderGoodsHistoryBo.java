package com.meidianyi.shop.service.pojo.wxapp.order.goods;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/11/4 15:47
 */
@Getter
@Setter
public class OrderGoodsHistoryBo {

    private Integer goodsId;

    private Timestamp createTime;
}
