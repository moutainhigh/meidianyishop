package com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-03-10 16:30
 **/
@Setter
@Getter
public class PurchaseChangeGoodsVo {
    private List<Goods> list;
    private Integer alreadyChangeNum;
    private Short maxChangePurchase;

    @Setter
    @Getter
    public static final class Goods{
        private Byte isChecked = 0;
        private Integer purchaseRuleId;

        /**
         * 0满tipMoney可换购，1已经可以选择商品换购
         */
        private Byte tip;
        private BigDecimal tipMoney;

        //商品原数据
        private Integer goodsId;
        private String goodsName;
        /**
         * 商品主图
         */
        private String goodsImg;
        /**
         * 商品价格，商品规格中的最低价格，（对于默认规格和自定义规格计算方式是一样的）
         */
        private BigDecimal shopPrice;
        /**
         * 市场价格
         */
        private BigDecimal marketPrice;
        private Byte isOnSale;
        private Byte isDelete;
        private Integer prdId;
        private String prdDesc;
        private String prdImg;
        private Integer prdNumber;
        private BigDecimal prdPrice;
    }
}
