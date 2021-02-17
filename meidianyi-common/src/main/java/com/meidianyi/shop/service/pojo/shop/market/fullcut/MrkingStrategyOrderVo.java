package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import com.meidianyi.shop.service.pojo.shop.market.MarketOrderGoodsListVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-05-11 10:15
 **/
@Getter
@Setter
public class MrkingStrategyOrderVo {
    private String orderSn;
    private Byte orderStatus;
    private String orderStatusName;
    private Integer userId;
    private String username;
    private String mobile;

    /**
     * 行信息
     */
    private List<MarketOrderGoodsListVo> goods;
}
