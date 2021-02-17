package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.GoodsSpecProductDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.service.pojo.shop.medical.sku.vo.GoodsSpecProductDetailVo;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Repository
public class GoodsSpecProductDao extends ShopBaseDao {

    /**
     * 商品sku新增
     * @param goodsSpecProductDos sku数据集合
     */
    public void batchInsert(List<GoodsSpecProductDo> goodsSpecProductDos) {
        DSLContext db = db();
        for (GoodsSpecProductDo goodsSpecProductDo : goodsSpecProductDos) {
            GoodsSpecProductRecord goodsSpecProductRecord =db.newRecord(GOODS_SPEC_PRODUCT);
            FieldsUtil.assign(goodsSpecProductDo, goodsSpecProductRecord);
            goodsSpecProductRecord.insert();
            goodsSpecProductDo.setPrdId(goodsSpecProductRecord.getPrdId());
        }
    }

    /**
     * 商品sku修改
     * @param goodsSpecProductDos sku数据集合
     */
    public void batchUpdate(List<GoodsSpecProductDo> goodsSpecProductDos) {
        List<GoodsSpecProductRecord> goodsSpecProductRecords = new ArrayList<>(goodsSpecProductDos.size());

        for (GoodsSpecProductDo goodsSpecProductDo : goodsSpecProductDos) {
            GoodsSpecProductRecord goodsSpecProductRecord = new GoodsSpecProductRecord();
            FieldsUtil.assign(goodsSpecProductDo, goodsSpecProductRecord);
            goodsSpecProductRecords.add(goodsSpecProductRecord);
        }
        db().batchUpdate(goodsSpecProductRecords).execute();
    }

    /**
     * 根据商品id获取其sku信息
     * @param goodsId
     * @return
     */
    public List<GoodsSpecProductDo> getSkuByGoodsId(Integer goodsId) {
        return db().selectFrom(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId).and(GOODS_SPEC_PRODUCT.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(GoodsSpecProductDo.class);
    }

    public Map<Integer, List<GoodsSpecProductDetailVo>> groupGoodsIdToSku(List<Integer> goodsIds) {
        Condition condition = GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds);
        return db().select(GOODS_SPEC_PRODUCT.GOODS_ID, GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_SN)
            .from(GOODS_SPEC_PRODUCT).where(condition)
            .fetchGroups(GOODS_SPEC_PRODUCT.GOODS_ID, GoodsSpecProductDetailVo.class);
    }

    /**
     * 根据商品id集合获取其sku信息
     * @param goodsIds
     * @return
     */
    public List<GoodsSpecProductDo> listSkuByGoodsIds(List<Integer> goodsIds) {
        return db().selectFrom(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds).and(GOODS_SPEC_PRODUCT.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(GoodsSpecProductDo.class);
    }

    public void deleteByGoodsId(Integer goodsId) {
        db().update(GOODS_SPEC_PRODUCT)
            .set(GOODS_SPEC_PRODUCT.PRD_SN, DSL.concat(DelFlag.DEL_ITEM_PREFIX).concat(GOODS_SPEC_PRODUCT.PRD_SN))
            .set(GOODS_SPEC_PRODUCT.PRD_CODES, DSL.concat(DelFlag.DEL_ITEM_PREFIX).concat(GOODS_SPEC_PRODUCT.PRD_CODES))
            .set(GOODS_SPEC_PRODUCT.DEL_FLAG, DelFlag.DISABLE_VALUE)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .execute();
    }


    public void updateExternalSku(Integer goodsId, BigDecimal shopPrice, BigDecimal marketPrice, BigDecimal costPrice){
        db().update(GOODS_SPEC_PRODUCT)
            .set(GOODS_SPEC_PRODUCT.PRD_PRICE,shopPrice)
            .set(GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE,marketPrice)
            .set(GOODS_SPEC_PRODUCT.PRD_COST_PRICE,costPrice)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .execute();
    }
}
