package com.meidianyi.shop.service.pojo.wxapp.order.marketing.fullreduce;

import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.base.BaseMarketingBaseVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class OrderFullReduce extends BaseMarketingBaseVo {
    MrkingStrategyVo info;
}
