package com.meidianyi.shop.dao.shop.goods;

import cn.hutool.core.util.StrUtil;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsPageListCondition;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsSortItem;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsMatchParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.MedicalGoodsBatchOperateParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsStatusVo;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SelectSeekStepN;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS_MEDICAL_INFO;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;

/**
 * 商品dao
 * @author 李晓冰
 * @date 2020年07月01日
 */
@Repository
public class GoodsDao extends ShopBaseDao {
    /**
     * 通过goodsId获取推广语
     * @param goodsId 商品id
     * @return
     */
    public String getPromotionLanguage(Integer goodsId) {
        return db().select(GOODS.PROMOTION_LANGUAGE).from(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).fetchAnyInto(String.class);
    }

    /**
     * 通过分类id集合获取商品id集合
     * @param sortId
     * @return
     */
    public List<Integer> listGoodsId(List<Integer> sortId) {

        return db().select(GOODS.GOODS_ID).from(GOODS)
            .where(GOODS.SORT_ID.in(sortId)).fetch(GOODS.GOODS_ID);
    }

    /**
     * 商品新增
     * @param goodsDo 商品数据
     * @return 商品id
     */
    public void insert(GoodsDo goodsDo) {
        GoodsRecord goodsRecord = db().newRecord(GOODS);
        FieldsUtil.assign(goodsDo, goodsRecord);
        // 临时添加
        goodsRecord.setShareConfig("{\"shareAction\":1,\"shareDoc\":null,\"shareImgAction\":1,\"shareImgUrl\":null,\"shareImgPath\":null}");
        goodsRecord.insert();
        goodsDo.setGoodsId(goodsRecord.getGoodsId());
    }

    public void update(GoodsDo goodsDo) {
        GoodsRecord goodsRecord = new GoodsRecord();
        FieldsUtil.assign(goodsDo, goodsRecord);
        // 临时添加
        goodsRecord.setShareConfig("{\"shareAction\":1,\"shareDoc\":null,\"shareImgAction\":1,\"shareImgUrl\":null,\"shareImgPath\":null}");
        db().executeUpdate(goodsRecord);
    }

    /**
     * 批量插入商品信息
     * @param goodsDos
     */
    public void batchInsert(List<GoodsDo> goodsDos) {
        List<String> goodsSns = new ArrayList<>(goodsDos.size());
        List<GoodsRecord> goodsRecords = new ArrayList<>(goodsDos.size());

        for (GoodsDo goodsDo : goodsDos) {
            goodsSns.add(goodsDo.getGoodsSn());
            GoodsRecord goodsRecord = new GoodsRecord();
            FieldsUtil.assign(goodsDo, goodsRecord);
            // 临时添加
            goodsRecord.setShareConfig("{\"shareAction\":1,\"shareDoc\":null,\"shareImgAction\":1,\"shareImgUrl\":null,\"shareImgPath\":null}");
            goodsRecords.add(goodsRecord);
        }
        db().batchInsert(goodsRecords).execute();

        Map<String, Integer> goodsSnIdMap = db().select(GOODS.GOODS_ID, GOODS.GOODS_SN).from(GOODS).where(GOODS.GOODS_SN.in(goodsSns)).fetchMap(GOODS.GOODS_SN, GOODS.GOODS_ID);

        for (GoodsDo goodsDo : goodsDos) {
            goodsDo.setGoodsId(goodsSnIdMap.get(goodsDo.getGoodsSn()));
        }
    }

    /**
     * 批量更新
     * @param goodsDos
     */
    public void batchUpdate(List<GoodsDo> goodsDos) {
        List<GoodsRecord> goodsRecords = new ArrayList<>(goodsDos.size());
        for (GoodsDo goodsDo : goodsDos) {
            GoodsRecord goodsRecord = new GoodsRecord();
            FieldsUtil.assign(goodsDo, goodsRecord);
            goodsRecords.add(goodsRecord);
        }
        db().batchUpdate(goodsRecords).execute();
    }


    public void deleteByGoodsId(Integer goodsId) {
        db().update(GOODS)
            .set(GOODS.GOODS_SN, DSL.concat(DelFlag.DEL_ITEM_PREFIX, GOODS.GOODS_SN).concat(GOODS.GOODS_ID))
            .set(GOODS.GOODS_NAME, DSL.concat(DelFlag.DEL_ITEM_PREFIX, GOODS.GOODS_NAME).concat(GOODS.GOODS_ID))
            .set(GOODS.DEL_FLAG, DelFlag.DISABLE_VALUE)
            .where(GOODS.GOODS_ID.eq(goodsId))
            .execute();
    }

    /**
     * 查询商品详情
     * @param goodsId 商品id
     * @return null表示不存在
     */
    public GoodsDo getByGoodsId(Integer goodsId) {
        GoodsDo goodsDo = db().selectFrom(GOODS).where(GOODS.GOODS_ID.eq(goodsId).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchAnyInto(GoodsDo.class);

        return goodsDo;
    }

    /**
     * 商品列表分页查询（以商品为维度）
     * @param goodsPageListCondition 分页查询条件
     * @param curPage                当前页 1开始
     * @param pageRows               展示数量
     * @return 分页结果
     */
    public PageResult<GoodsDo> getGoodsPageList(GoodsPageListCondition goodsPageListCondition, Integer curPage, Integer pageRows) {
        Condition condition = buildGoodsPageListCondition(goodsPageListCondition);
        List<SortField<?>> sortFields = buildGoodsPageOrderField(goodsPageListCondition.getPageSortItems());
        SelectSeekStepN<GoodsRecord> selectStep = db().selectFrom(GOODS).where(condition).orderBy(sortFields);
        PageResult<GoodsDo> pageResult = getPageResult(selectStep, curPage, pageRows, GoodsDo.class);
        return pageResult;
    }

    /**
     * 商品分页拼接过滤条件
     * @param goodsPageListCondition 过滤条件
     * @return 过滤条件condition
     */
    private Condition buildGoodsPageListCondition(GoodsPageListCondition goodsPageListCondition) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE);

        if (StrUtil.isNotBlank(goodsPageListCondition.getGoodsName())) {
            condition = condition.and(GOODS.GOODS_NAME.like(likeValue(goodsPageListCondition.getGoodsName())));
        }

        if (StrUtil.isNotBlank(goodsPageListCondition.getGoodsSn())) {
            condition = condition.and(GOODS.GOODS_SN.like(likeValue(goodsPageListCondition.getGoodsSn())));
        }

        if (goodsPageListCondition.getBrandIds() != null) {
            condition = condition.and(GOODS.BRAND_ID.in(goodsPageListCondition.getBrandIds()));
        }

        if (goodsPageListCondition.getSortIds() != null) {
            condition = condition.and(GOODS.SORT_ID.in(goodsPageListCondition.getSortIds()));
        }

        if (goodsPageListCondition.getLowShopPrice() != null) {
            condition = condition.and(GOODS.SHOP_PRICE.ge(goodsPageListCondition.getLowShopPrice()));
        }

        if (goodsPageListCondition.getHighShopPrice() != null) {
            condition = condition.and(GOODS.SHOP_PRICE.le(goodsPageListCondition.getHighShopPrice()));
        }

        if (goodsPageListCondition.getIsOnSale() != null) {
            condition = condition.and(GOODS.IS_ON_SALE.eq(goodsPageListCondition.getIsOnSale()));
        }

        if (goodsPageListCondition.getIsSaleOut() != null) {
            if (MedicalGoodsConstant.SALE_OUT_YES.equals(goodsPageListCondition.getIsSaleOut())) {
                condition = condition.and(GOODS.GOODS_NUMBER.eq(0));
            } else {
                condition = condition.and(GOODS.GOODS_NUMBER.gt(0));
            }
        }

        if (goodsPageListCondition.getIsMedical() != null) {
            condition = condition.and(GOODS.IS_MEDICAL.eq(goodsPageListCondition.getIsMedical()));
        }

        if (goodsPageListCondition.getGoodsIdsLimit() != null) {
            condition = condition.and(GOODS.GOODS_ID.in(goodsPageListCondition.getGoodsIdsLimit()));
        }
        // 查询his独有药品
        if (MedicalGoodsConstant.ONLY_IN_HIS.equals(goodsPageListCondition.getSource())) {
            condition = condition.and(GOODS.HIS_STATUS.isNotNull().and(GOODS.STORE_CODE.isNull()));
        }
        // 查询药店独有药品
        if (MedicalGoodsConstant.ONLY_IN_STORE.equals(goodsPageListCondition.getSource())) {
            condition = condition.and(GOODS.HIS_STATUS.isNull().and(GOODS.STORE_CODE.isNotNull()));
        }
        // 查询双方匹配成功的药品
        if (MedicalGoodsConstant.IN_BOTH.equals(goodsPageListCondition.getSource())) {
            condition = condition.and(GOODS.HIS_STATUS.isNotNull().and(GOODS.STORE_CODE.isNotNull()));
        }

        return condition;
    }

    /**
     * 商品分页排序条件生成
     * @param goodsSortItems
     */
    private List<SortField<?>> buildGoodsPageOrderField(List<GoodsSortItem> goodsSortItems) {
        List<SortField<?>> sortFields = new ArrayList<>(0);

        if (goodsSortItems == null || goodsSortItems.size() == 0) {
            sortFields.add(GOODS.CREATE_TIME.desc());
        } else {
            Field<?> sortField;
            for (GoodsSortItem goodsSortItem : goodsSortItems) {

                switch (goodsSortItem.getColumnName()) {
                    case "shopPrice":
                        sortField = GOODS.SHOP_PRICE;
                        break;
                    case "goodsNumber":
                        sortField = GOODS.GOODS_NUMBER;
                        break;
                    case "goodsSaleNum":
                        sortField = GOODS.GOODS_SALE_NUM;
                        break;
                    default:
                        sortField = null;
                }

                if (sortField != null) {
                    if (goodsSortItem.isAsc()) {
                        sortFields.add(sortField.asc());
                    } else {
                        sortFields.add(sortField.desc());
                    }
                }
            }
        }

        return sortFields;
    }

    /**
     * 判断goodsSn是否存在
     * @param goodsSn
     * @return true 是 false 否
     */
    public boolean isGoodsSnExist(String goodsSn, Integer goodsId) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.GOODS_SN.eq(goodsSn));
        if (goodsId != null) {
            condition = condition.and(GOODS.GOODS_ID.ne(goodsId));
        }
        int count = db().fetchCount(GOODS, condition);
        return count > 0;
    }

    /**
     * 统计商品数量，包含已删除的
     * @return 商品数量
     */
    public int countAllGoods() {
        return db().fetchCount(GOODS);
    }



    /**
     * 查询所有已有goodsSn
     * @param goodsSn
     * @param isMedical
     * @return
     */
    public Map<String, Integer> mapGoodsSnToGoodsId(List<String> goodsSn, Byte isMedical) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.GOODS_SN.in(goodsSn));
        List<GoodsDo> goodsDos = db().select(GOODS.GOODS_ID, GOODS.GOODS_SN).from(GOODS).where(condition).fetchInto(GoodsDo.class);

        return goodsDos.stream().collect(Collectors.toMap(GoodsDo::getGoodsSn, GoodsDo::getGoodsId, (x1, x2) -> x1));
    }

    /**
     * 查询商品id和价格的映射
     * @param goodsIds
     * @return
     */
    public Map<Integer, BigDecimal> mapGoodsIdToGoodsPrice(Collection<Integer> goodsIds) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.GOODS_ID.in(goodsIds));
        return db().select(GOODS.GOODS_ID, GOODS.SHOP_PRICE).from(GOODS).where(condition).fetchMap(GOODS.GOODS_ID, GOODS.SHOP_PRICE);
    }

    /**
     * 查询商品Sn和商品id
     * @param goodsCodes
     * @return
     */
    public Map<String, Integer> mapGoodsSnToGoodsId(Collection<String> goodsCodes) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.GOODS_SN.in(goodsCodes));
        return db().select(GOODS.GOODS_SN, GOODS.GOODS_ID).from(GOODS).where(condition).fetchMap(GOODS.GOODS_SN, GOODS.GOODS_ID);
    }

    /**
     * 根据goodsId,goodsCommonName,goodsQualityRatio,productionEnterprise匹配药品Id
     * @param goodsMatchParam
     * @return
     */
    public GoodsStatusVo getGoodsIdByInfo(GoodsMatchParam goodsMatchParam) {
        Condition condition = DSL.noCondition();
        if (goodsMatchParam.getGoodsId() != null && goodsMatchParam.getGoodsId() > 0) {
            condition = condition.and(GOODS_MEDICAL_INFO.GOODS_ID.eq(goodsMatchParam.getGoodsId()));
        } else {
            if (goodsMatchParam.getGoodsCommonName() != null && !GoodsConstant.BLACK.equals(goodsMatchParam.getGoodsCommonName())) {
                condition = condition.and(GOODS_MEDICAL_INFO.GOODS_COMMON_NAME.eq(goodsMatchParam.getGoodsCommonName()));
            }
            if (goodsMatchParam.getGoodsQualityRatio() != null && !GoodsConstant.BLACK.equals(goodsMatchParam.getGoodsQualityRatio())) {
                condition = condition.and(GOODS_MEDICAL_INFO.GOODS_QUALITY_RATIO.eq(goodsMatchParam.getGoodsQualityRatio()));
            }
            if (goodsMatchParam.getProductionEnterprise() != null && !GoodsConstant.BLACK.equals(goodsMatchParam.getProductionEnterprise())) {
                condition = condition.and(GOODS_MEDICAL_INFO.GOODS_PRODUCTION_ENTERPRISE.eq(goodsMatchParam.getProductionEnterprise()));
            }
        }
        return db().select(GOODS.GOODS_ID).from(GOODS)
            .leftJoin(GOODS_MEDICAL_INFO).on(GOODS_MEDICAL_INFO.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(condition)
            .fetchAnyInto(GoodsStatusVo.class);
    }

    /**
     * 根据prdId获取商品名称
     * @param prdId
     * @return
     */
    public String getGoodsNameByPrdId(Integer prdId) {
        return db().select(GOODS.GOODS_NAME).from(GOODS)
            .leftJoin(GOODS_SPEC_PRODUCT).on(GOODS_SPEC_PRODUCT.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
            .fetchAnyInto(String.class);
    }

    /**
     * 批量修改所有商品的上下架状态
     * @param isOnSale
     */
    public void switchSaleStatusAllGoods(Byte isOnSale, Byte source) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE);
        // 下架his的所有药品
        if (MedicalGoodsConstant.SOURCE_FROM_HIS.equals(source)) {
            condition = condition.and(GOODS.SOURCE.eq(source));
        }
        // 下架药房的所有药品
        if (MedicalGoodsConstant.SOURCE_FROM_STORE.equals(source)) {
            condition = condition.and(GOODS.STORE_CODE.isNotNull());
        }
        db().update(GOODS).set(GOODS.IS_ON_SALE, isOnSale).where(condition).execute();
    }

    /**
     * 批量上架可用的商品信息
     */
    public void batchUpStoreAndMedicalGoods() {
        db().update(GOODS).set(GOODS.IS_ON_SALE, MedicalGoodsConstant.ON_SALE)
            .where(GOODS.STORE_CODE.isNotNull()
                .and(GOODS.HIS_STATUS.eq(BaseConstant.EXTERNAL_ITEM_STATE_ENABLE.byteValue()))
                .and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .execute();
    }

    public void batchOperate(MedicalGoodsBatchOperateParam param) {
        if (param.getIsOnSale() != null) {
            db().update(GOODS).set(GOODS.IS_ON_SALE, param.getIsOnSale()).where(GOODS.GOODS_ID.in(param.getGoodsIds())).execute();
        }

        if (param.getBatchUpOrDownHisGoods() != null) {
            db().update(GOODS).set(GOODS.IS_ON_SALE, param.getBatchUpOrDownHisGoods())
                .where(getHisGoodsCondition())
                .execute();
            return;
        }

        if (param.getBatchUpOrDownStoreGoods() != null) {
            db().update(GOODS).set(GOODS.IS_ON_SALE, param.getBatchUpOrDownStoreGoods())
                .where(getStoreGoodsCondition())
                .execute();
            return;
        }

        if (param.getBatchUpOrDownBothInGoods() != null) {
            db().update(GOODS).set(GOODS.IS_ON_SALE, param.getBatchUpOrDownBothInGoods())
                .where(getBothInGoodsCondition())
                .execute();
            return;
        }
    }

    private Condition getHisGoodsCondition(){
        return GOODS.HIS_STATUS.isNotNull().and(GOODS.STORE_CODE.isNull());
    }

    private Condition getStoreGoodsCondition(){
        return GOODS.HIS_STATUS.isNull().and(GOODS.STORE_CODE.isNotNull());
    }

    private Condition getBothInGoodsCondition(){
        return GOODS.HIS_STATUS.eq(BaseConstant.EXTERNAL_ITEM_STATE_ENABLE.byteValue()).and(GOODS.STORE_CODE.isNotNull());
    }
}
