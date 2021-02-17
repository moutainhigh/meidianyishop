package com.meidianyi.shop.service.pojo.wxapp.order.history;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 历史购买
 * @author 孔德成
 * @date 2019/11/5 15:37
 */
@Setter
@Getter
public class OrderGoodsHistoryVo extends GoodsListMpVo {

    /**
     * 时间 yyyy-MM-dd
     */
    private Timestamp creatTime;

}
