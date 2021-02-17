package com.meidianyi.shop.dao.shop.store;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.StoreGoodsRecord;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.store.goods.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.STORE;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.StoreGoods.STORE_GOODS;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-20 14:49
 **/
@Repository
public class StoreGoodsDao extends ShopBaseDao {

    /**
     * 商品在售
     */
    private static final Byte IS_ON_SALE = 1;

    /**
     * 查询商品主表中需要同步至门店的信息
     * @param param 更新入参
     * @return List<StoreGoods>
     */
    public List<StoreGoods> selectMainGoods(StoreGoodsUpdateTimeParam param) {
        SelectConditionStep<Record6<Integer, Byte, Integer, String, Integer, BigDecimal>> select
            = db().select(GOODS.GOODS_ID, GOODS.IS_ON_SALE, GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_SN,
            GOODS_SPEC_PRODUCT.PRD_NUMBER.as("product_number"),
            GOODS_SPEC_PRODUCT.PRD_PRICE.as("product_price"))
            .from(GOODS).innerJoin(GOODS_SPEC_PRODUCT)
            .on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
            .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        if (param.getUpdateTye() == 0) {
            select.and(GOODS_SPEC_PRODUCT.UPDATE_TIME.between(param.getUpdateBegin(), param.getUpdateEnd()));
        }
        return select.fetchInto(StoreGoods.class);
    }

    /*****************药房药品数据*****************/
    /**
     * 查询已有的商品id信息
     * @param goodsIds
     * @param storeId
     * @return
     */
    public List<Integer> selectExistStoreGoodsIds(List<Integer> goodsIds, Integer storeId) {
        Condition condition = STORE_GOODS.STORE_ID.eq(storeId).and(STORE_GOODS.GOODS_ID.in(goodsIds));
        return db().select(STORE_GOODS.GOODS_ID).from(STORE_GOODS).where(condition).fetch(STORE_GOODS.GOODS_ID);
    }

    public void batchUpdate(List<StoreGoods> storeGoodsList) {
        List<StoreGoodsRecord> records = convertStoreGoodsToRecord(storeGoodsList);
        db().batchUpdate(records).execute();
    }

    public void batchInsert(List<StoreGoods> storeGoodsList){
        List<StoreGoodsRecord> records = convertStoreGoodsToRecord(storeGoodsList);
        db().batchInsert(records).execute();
    }

    public StoreGoods getStoreGoodsByMedicalKey(String medicalKey,Integer storeId){
        StoreGoods storeGoods = db().select(STORE_GOODS.ID).from(STORE_GOODS)
            .where(STORE_GOODS.GOODS_COMMON_NAME.concat(STORE_GOODS.GOODS_QUALITY_RATIO.concat(STORE_GOODS.GOODS_APPROVAL_NUMBER)).eq(medicalKey).and(STORE_GOODS.STORE_ID.eq(storeId)))
            .fetchAnyInto(StoreGoods.class);

        return storeGoods;
    }

    private List<StoreGoodsRecord> convertStoreGoodsToRecord(List<StoreGoods> storeGoodsList){
        return storeGoodsList.stream().map(x -> {
            StoreGoodsRecord storeGoodsRecord = new StoreGoodsRecord();
            FieldsUtil.assign(x,storeGoodsRecord);
            return storeGoodsRecord;
        }).collect(Collectors.toList());
    }

    /**
     * 查询更新的商品数量
     * @param param
     * @return
     */
    public List<Integer> selectGoodsPrdIdsForUpdate(StoreGoodsUpdateTimeParam param) {
        return db().select(STORE_GOODS.PRD_ID)
            .from(STORE_GOODS)
            .where(STORE_GOODS.STORE_ID.eq(param.getStoreId()))
            .fetchInto(Integer.class);
    }

    /**
     * 门店商品分页查询
     * @param param
     * @return
     */
    public PageResult<StoreGoodsListQueryVo> getGoodsPageList(StoreGoodsListQueryParam param){
        Condition condition = buildCondition(param);
        SelectSeekStep1<Record, Timestamp> select = db().select(STORE_GOODS.asterisk(), STORE.STORE_NAME).from(STORE_GOODS).innerJoin(STORE).on(STORE_GOODS.STORE_ID.eq(STORE.STORE_ID))
            .where(condition)
            .orderBy(STORE_GOODS.CREATE_TIME);

        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), StoreGoodsListQueryVo.class);
    }

    private Condition buildCondition(StoreGoodsListQueryParam param){
        Condition condition = STORE_GOODS.IS_DELETE.eq(DelFlag.NORMAL_VALUE);

        if (param.getKeywords() != null) {
            condition = condition.and(STORE_GOODS.GOODS_COMMON_NAME.like(likeValue(param.getKeywords())));
        }
        if (param.getStoreId() != null) {
            condition = condition.and(STORE_GOODS.STORE_ID.eq(param.getStoreId()));
        }
        if (param.getLimitedStoreIds() != null) {
            condition = condition.and(STORE_GOODS.STORE_ID.in(param.getLimitedStoreIds()));
        }
        if (param.getIsOnSale() != null) {
            condition = condition.and(STORE_GOODS.IS_ON_SALE.eq(param.getIsOnSale()));
        }
        if (MedicalGoodsConstant.SALE_OUT_YES.equals(param.getIsSaleOut())) {
            condition = condition.and(STORE_GOODS.PRODUCT_NUMBER.eq(0));
        }
        return condition;
    }

    /**
     * 查询该商品在哪家门店上架
     * @param storeGoodsBaseCheckInfoList 商品列表
     * @return List<Integer>
     */
    public List<String> checkStoreGoodsIsOnSale(List<StoreGoodsBaseCheckInfo> storeGoodsBaseCheckInfoList) {
        List<Integer> prdId = storeGoodsBaseCheckInfoList.stream().map(StoreGoodsBaseCheckInfo::getProductId).collect(Collectors.toList());
        return db().select(STORE.STORE_CODE)
            .from(STORE)
            .leftJoin(STORE_GOODS)
            .on(STORE.STORE_ID.eq(STORE_GOODS.STORE_ID))
            .where(STORE_GOODS.PRD_ID.in(prdId))
            .and(STORE_GOODS.PRODUCT_NUMBER.gt(0))
            .and(STORE_GOODS.IS_ON_SALE.eq(IS_ON_SALE))
            .groupBy(STORE.STORE_CODE)
            .having(DSL.count(STORE.STORE_CODE).eq(storeGoodsBaseCheckInfoList.size()))
            .fetchInto(String.class);
    }
}
