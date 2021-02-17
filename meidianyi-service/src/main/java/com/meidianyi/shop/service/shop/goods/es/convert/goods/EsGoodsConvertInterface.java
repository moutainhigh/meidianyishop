package com.meidianyi.shop.service.shop.goods.es.convert.goods;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsBaseMp;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;

/**
 * esGoods转换
 * @author 卢光耀
 * @date 2019/11/6 2:32 下午
 *
*/
public interface EsGoodsConvertInterface<T> {

    /**
     * admin列表页vo
     * @param esGoods es查询出来的结果
     * @return
     */
    T convert(EsGoods esGoods);

    /**
     * 通用字段复制
     * @param target 目标
     * @param source 源
     */
    default void copyValues(GoodsBaseMp target,EsGoods source){
        target.setGoodsId(source.getGoodsId());
        target.setGoodsName(source.getGoodsName());
        target.setGoodsSaleNum(source.getGoodsSaleNum());
        target.setDefaultPrd(source.getDefPrd());
    }

}
