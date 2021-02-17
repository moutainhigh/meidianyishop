package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author liufei
 * date 2019/7/18
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssiDataGoods implements PendingRule<AssiDataGoods> {
    final Byte BYTE_TWO = 2;
    /**
     * 运费模板设置 非0: 已设置运费模板，0：未设置
     */
    public Metadata shipTemplateConf;
    /**
     * 商品添加 非0: 已添加商品 0：未添加
     */
    public Metadata goodsConf;
    /** 商品库存偏小 0: 商品库存充裕 否有goodsStoreConf个商品库存偏少 */
    public Metadata goodsStoreConf;
    /** 滞销商品 0：商品销售状况良好 否有goodsUnsalableConf个商品滞销 */
    public Metadata goodsUnsalableConf;
    /** 商品评价审核逾期 0: 商品评价审核进度良好 否有goodsComment个商品评价超过3天未审核 */
    public Metadata goodsComment;
    /** 推荐商品 0: 未配置推荐商品 否已配置推荐商品 */
    public Metadata goodsRecommend;
    /** 商家分类 0: 未配置商家分类 否已配置商家分类 */
    public Metadata shopSort;

    @Override
    public AssiDataGoods ruleHandler() {
        handler1(goodsStoreConf, goodsUnsalableConf, goodsComment);
        handler2(shipTemplateConf, goodsConf, goodsRecommend, shopSort);
        return this;
    }

    public AssiDataGoods setType() {
        if (goodsStoreConf.getStatus() == BYTE_ONE) {
            goodsStoreConf.setType(BYTE_ONE);
        } else if (goodsStoreConf.getStatus() == BYTE_ZERO) {
            goodsStoreConf.setType(BYTE_TWO);
        }

        if (goodsUnsalableConf.getStatus() == BYTE_ONE) {
            goodsUnsalableConf.setType(BYTE_ONE);
        } else if (goodsUnsalableConf.getStatus() == BYTE_ZERO) {
            goodsUnsalableConf.setType(BYTE_TWO);
        }

        if (goodsComment.getStatus() == BYTE_ONE) {
            goodsComment.setType(BYTE_ONE);
        } else if (goodsComment.getStatus() == BYTE_ZERO) {
            goodsComment.setType(BYTE_TWO);
        }
        return this;
    }

    @Override
    public int getUnFinished() {
        int num = unFinished(shipTemplateConf, goodsConf, goodsStoreConf, goodsUnsalableConf, goodsComment, goodsRecommend, shopSort);
        log.debug("Goods unFinished Num:{}", num);
        return num;
    }
}
