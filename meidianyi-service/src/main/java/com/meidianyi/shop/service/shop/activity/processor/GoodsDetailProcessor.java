package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;

/**
 * 小程序-商品详情处理器接口
 * @author 李晓冰
 * @date 2019年11月07日
 */
public interface GoodsDetailProcessor {
    /**
     * 商品详情处理器处理函数
     * @param capsule  商品详情对象{@link com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo}
     * @param param
     */
    void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param);
}
