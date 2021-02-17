package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;

/**
 * 购物车策略
 *
 * @author 孔德成
 * @date 2019/11/6 15:24
 */
public interface ActivityCartListStrategy extends Processor{

    /**
     * 执行业务的处理方法
     * @param cartBo 业务数据类
     */
    public void doCartOperation(WxAppCartBo cartBo);
}
