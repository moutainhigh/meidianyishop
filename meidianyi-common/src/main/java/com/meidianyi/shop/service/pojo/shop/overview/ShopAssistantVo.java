package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liufei
 * date 2019/7/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopAssistantVo implements PendingRule<ShopAssistantVo> {
    private AssiDataShop dataShop;
    private AssiWxDataShop wxDataShop;
    private AssiDataGoods dataGoods;
    private AssiDataOrder dataOrder;
    private AssiDataMarket dataMarket;
    /** 待处理项总数 */
    private int totalNum;

    @Override
    public ShopAssistantVo ruleHandler() {
        int temp = dataShop.getUnFinished() +
            wxDataShop.getUnFinished() +
            dataGoods.getUnFinished() +
            dataOrder.getUnFinished() +
            dataMarket.getUnFinished();
        setTotalNum(temp);
        return this;
    }

    @Override
    public int getUnFinished() {
        return 0;
    }
}
