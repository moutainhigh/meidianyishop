package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.jooq.Record3;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;

/**
 * @author 李晓冰
 * @date 2019年11月04日
 */
@Service
public class GoodsPrdProcessorDao extends ShopBaseService {

    /**
     * 获取集合内商品id对应的规格价格信息
     * @param goodsIds 商品id集合
     * @return key: 商品id，value:List<Record3<Integer, BigDecimal,String>>
     */
    public Map<Integer, List<Record3<Integer, BigDecimal,String>>> getGoodsPrdInfo(List<Integer> goodsIds) {
        return db().select(GOODS_SPEC_PRODUCT.GOODS_ID, GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS_SPEC_PRODUCT.PRD_DESC)
            .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds))
            .orderBy(GOODS_SPEC_PRODUCT.PRD_PRICE.desc())
            .fetch().stream().collect(Collectors.groupingBy(x -> x.get(GOODS_SPEC_PRODUCT.GOODS_ID)));
    }

    /**
     * 获取商品的所有规格信息
     * @param goodsId
     * @return
     */
    public List<GoodsSpecProductRecord> getGoodsDetailPrds(Integer goodsId){
        return db().select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE,GOODS_SPEC_PRODUCT.PRD_NUMBER,
            GOODS_SPEC_PRODUCT.PRD_WEIGHT,GOODS_SPEC_PRODUCT.PRD_SPECS, GOODS_SPEC_PRODUCT.PRD_DESC,GOODS_SPEC_PRODUCT.PRD_IMG)
            .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).orderBy(GOODS_SPEC_PRODUCT.PRD_ID)
            .fetchInto(GoodsSpecProductRecord.class);
    }
}
