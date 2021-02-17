package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 订单批量更新商品，规格库存和销量参数
 * @author 李晓冰
 * @date 2019年11月18日
 */
@Data
@Builder
public class BatchUpdateGoodsNumAndSaleNumForOrderParam {
    private Integer goodsId;
    private Integer goodsNum;
    private Integer saleNum;
    @Builder.Default
    private List<ProductNumInfo> productsInfo = new ArrayList<>();

    /**
     * 该类重写equals、hashCode使用时需注意
     */
    @Data
    @Builder
    public static class ProductNumInfo {
        private Integer prdId;
        private Integer prdNum;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ProductNumInfo that = (ProductNumInfo) o;
            return Objects.equals(prdId, that.prdId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prdId);
        }
    }

    /**
     * 同已订单规格重复处理
     * @param info
     */
    public void addProductsInfo(ProductNumInfo info){
        int i = productsInfo.indexOf(info);
        if(i == -1) {
            productsInfo.add(info);
        }else {
            productsInfo.get(i).setPrdNum(info.getPrdNum());
        }
    }
}
