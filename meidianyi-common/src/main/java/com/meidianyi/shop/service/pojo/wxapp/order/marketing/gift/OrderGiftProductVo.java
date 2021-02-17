package com.meidianyi.shop.service.pojo.wxapp.order.marketing.gift;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.shop.market.gift.ProductVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

/**
 * 下单查询赠品vo
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class OrderGiftProductVo extends ProductVo {
    private Integer goodsId;
    private String goodsSn;
    private Integer productId;
    private String prdSn;
    private BigDecimal shopPrice;
    private String goodsImg;
    private Integer deliverTemplateId;
    private BigDecimal goodsWeight;
    private Integer catId;
    private Integer sortId;

    public OrderGoodsBo toOrderGoodsBo(){
        return OrderGoodsBo.builder()
            .goodsId(goodsId)
            .goodsName(getGoodsName())
            .goodsSn(goodsSn)
            .productId(productId)
            .productSn(prdSn)
            .goodsNumber(NumberUtils.INTEGER_ONE)
            .marketPrice(shopPrice)
            .goodsPrice(BigDecimal.ZERO)
            .goodsAttr(getPrdDesc())
            .goodsImg(StringUtils.isBlank(getPrdImg()) ? goodsImg : getPrdImg())
            .isGift(OrderConstant.IS_GIFT_Y)
            .discountedGoodsPrice(BigDecimal.ZERO)
            .deliverTemplateId(deliverTemplateId)
            .goodsWeight(goodsWeight)
            .catId(catId)
            .sortId(sortId)
            .goodsPriceAction(BaseConstant.ACTIVITY_TYPE_GENERAL)
            .discountedTotalPrice(BigDecimal.ZERO)
            .giftId(getGiftId())
        .build();
    }
}
