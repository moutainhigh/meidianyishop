package com.meidianyi.shop.service.pojo.wxapp.goods.goods.list;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 拼团活动vo
 * @author 李晓冰
 * @date 2019年11月14日
 */
@Getter
@Setter
public class GroupBuyListMpVo extends GoodsActivityBaseMp {
    private BigDecimal discountPrice;
}
