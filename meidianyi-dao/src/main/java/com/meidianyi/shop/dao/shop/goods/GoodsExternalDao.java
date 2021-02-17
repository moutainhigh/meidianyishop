package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsExternalDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsFromHisRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsFromStoreRecord;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.ExternalMatchedGoodsParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.FailMatchedParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.GoodsExternalPageParam;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.tables.GoodsFromHis.GOODS_FROM_HIS;
import static com.meidianyi.shop.db.shop.tables.GoodsFromStore.GOODS_FROM_STORE;

/**
 * @author 李晓冰
 * @date 2020年10月13日
 */
@Repository
public class GoodsExternalDao extends ShopBaseDao {

    public void insertExternalFromHis(GoodsExternalDo externalDo) {
        GoodsFromHisRecord goodsFromHisRecord = db().newRecord(GOODS_FROM_HIS);
        FieldsUtil.assignNotNull(externalDo, goodsFromHisRecord);
        goodsFromHisRecord.insert();
    }

    public void updateExternalFromHis(GoodsExternalDo externalDo) {
        GoodsFromHisRecord goodsFromHisRecord = db().newRecord(GOODS_FROM_HIS);
        FieldsUtil.assignNotNull(externalDo, goodsFromHisRecord);
        goodsFromHisRecord.update();
    }

    public void insertExternalFromStore(GoodsExternalDo externalDo) {
        GoodsFromStoreRecord goodsFromStoreRecord = db().newRecord(GOODS_FROM_STORE);
        FieldsUtil.assign(externalDo, goodsFromStoreRecord);
        goodsFromStoreRecord.insert();
    }

    public void updateExternalFromStore(GoodsExternalDo externalDo) {
        GoodsFromStoreRecord goodsFromStoreRecord = db().newRecord(GOODS_FROM_STORE);
        FieldsUtil.assign(externalDo, goodsFromStoreRecord);
        goodsFromStoreRecord.update();
    }

    public void updateExternalInfoToMatched(Integer goodsFromHisId, Integer goodsFromStoreId) {
        db().update(GOODS_FROM_HIS).set(GOODS_FROM_HIS.IS_MATCH, MedicalGoodsConstant.ALREADY_MATCHED)
            .where(GOODS_FROM_HIS.ID.eq(goodsFromHisId)).execute();
        db().update(GOODS_FROM_STORE).set(GOODS_FROM_STORE.IS_MATCH, MedicalGoodsConstant.ALREADY_MATCHED)
            .where(GOODS_FROM_STORE.ID.eq(goodsFromStoreId)).execute();
    }

    public GoodsExternalDo getExternalFromHis(String medicalKey) {
        Condition condition = GOODS_FROM_HIS.GOODS_COMMON_NAME.concat(GOODS_FROM_HIS.GOODS_QUALITY_RATIO.concat(GOODS_FROM_HIS.GOODS_PRODUCTION_ENTERPRISE)).eq(medicalKey);
        GoodsExternalDo goodsExternalDo = db().select(GOODS_FROM_HIS.ID).from(GOODS_FROM_HIS).where(GOODS_FROM_HIS.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(condition))
            .fetchAnyInto(GoodsExternalDo.class);

        return goodsExternalDo;
    }

    public GoodsExternalDo getExternalFromStore(String medicalKey) {
        Condition condition = GOODS_FROM_STORE.GOODS_COMMON_NAME.concat(GOODS_FROM_STORE.GOODS_QUALITY_RATIO.concat(GOODS_FROM_STORE.GOODS_PRODUCTION_ENTERPRISE)).eq(medicalKey);
        GoodsExternalDo goodsExternalDo = db().select(GOODS_FROM_STORE.ID).from(GOODS_FROM_STORE).where(GOODS_FROM_STORE.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(condition))
            .fetchAnyInto(GoodsExternalDo.class);

        return goodsExternalDo;
    }

    public boolean isAlreadyDisposed(ExternalMatchedGoodsParam param) {
        int i = db().fetchCount(GOODS_FROM_HIS, GOODS_FROM_HIS.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(GOODS_FROM_HIS.IS_MATCH.ne(MedicalGoodsConstant.NOT_MATCHED)).and(GOODS_FROM_HIS.ID.eq(param.getFromHisId())));
        int j = db().fetchCount(GOODS_FROM_STORE, GOODS_FROM_STORE.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(GOODS_FROM_STORE.IS_MATCH.ne(MedicalGoodsConstant.NOT_MATCHED)).and(GOODS_FROM_STORE.ID.eq(param.getFromStoreId())));
        return i > 0 || j > 0;
    }

    public Map<Integer, GoodsExternalDo> getExternalHisInfoByHisIds(List<Integer> hisIds) {
        return db().selectFrom(GOODS_FROM_HIS).where(GOODS_FROM_HIS.ID.in(hisIds))
            .fetchMap(GOODS_FROM_HIS.ID, GoodsExternalDo.class);
    }

    public Map<Integer, GoodsExternalDo> getExternalStoreInfoByStoreId(List<Integer> hisIds) {
        return db().selectFrom(GOODS_FROM_STORE).where(GOODS_FROM_STORE.ID.in(hisIds))
            .fetchMap(GOODS_FROM_STORE.ID, GoodsExternalDo.class);
    }


    public PageResult<GoodsExternalDo> getExternalPageList(GoodsExternalPageParam param) {
        if (MedicalGoodsConstant.PAGE_LIST_FROM_HIS.equals(param.getPageListFrom())) {
            return getExternalPageListFromHis(param);
        } else {
            return getExternalPageListFromStore(param);
        }
    }

    private PageResult<GoodsExternalDo> getExternalPageListFromHis(GoodsExternalPageParam param) {
        Condition baseCondition = GOODS_FROM_HIS.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(GOODS_FROM_HIS.IS_MATCH.eq(MedicalGoodsConstant.NOT_MATCHED)).and(GOODS_FROM_HIS.STATE.eq(BaseConstant.EXTERNAL_ITEM_STATE_ENABLE));

        GoodsExternalDo goodsExternalDo = null;
        if (MedicalGoodsConstant.DESC.equals(param.getDirection())) {
            goodsExternalDo = db().selectFrom(GOODS_FROM_HIS).where(baseCondition).orderBy(GOODS_FROM_HIS.ID.desc()).fetchAnyInto(GoodsExternalDo.class);
        } else {
            goodsExternalDo = db().selectFrom(GOODS_FROM_HIS).where(baseCondition).orderBy(GOODS_FROM_HIS.ID.asc()).fetchAnyInto(GoodsExternalDo.class);
        }
        List<GoodsExternalDo> list = new ArrayList<>();
        if (goodsExternalDo != null) {
            list.add(goodsExternalDo);
        }
        PageResult<GoodsExternalDo> pageResult = new PageResult<>();
        pageResult.setDataList(list);
        return pageResult;
    }


    private PageResult<GoodsExternalDo> getExternalPageListFromStore(GoodsExternalPageParam param) {
        Condition baseCondition = GOODS_FROM_STORE.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(GOODS_FROM_STORE.IS_MATCH.eq(MedicalGoodsConstant.NOT_MATCHED)).and(GOODS_FROM_STORE.STATE.eq(BaseConstant.EXTERNAL_ITEM_STATE_ENABLE));

        Condition paramCondition = DSL.noCondition();
        if (StringUtils.isNotBlank(param.getGoodsCommonName())) {
            String[] ss = param.getGoodsCommonName().split(" ");
            Condition condition = DSL.falseCondition();
            for (String s : ss) {
                condition = condition.or(GOODS_FROM_STORE.GOODS_COMMON_NAME.like(likeValue(s)));
            }
            paramCondition = paramCondition.and(condition);
        }

        if (StringUtils.isNotBlank(param.getGoodsQualityRatio())) {
            String[] ss = param.getGoodsQualityRatio().split(" ");
            Condition condition = DSL.falseCondition();
            for (String s : ss) {
                condition = condition.or(GOODS_FROM_STORE.GOODS_QUALITY_RATIO.like(likeValue(s)));
            }
            paramCondition = paramCondition.and(condition);
        }

        if (StringUtils.isNotBlank(param.getGoodsProductionEnterprise())) {
            String[] ss = param.getGoodsProductionEnterprise().split(" ");
            Condition condition = DSL.falseCondition();
            for (String s : ss) {
                condition = condition.or(GOODS_FROM_STORE.GOODS_PRODUCTION_ENTERPRISE.like(likeValue(s)));
            }
            paramCondition = paramCondition.and(condition);
        }

        if (StringUtils.isNotBlank(param.getGoodsAliasName())) {
            paramCondition = paramCondition.and(GOODS_FROM_STORE.GOODS_ALIAS_NAME.like(likeValue(param.getGoodsAliasName())));
        }

        if (StringUtils.isNotBlank(param.getGoodsApprovalNumber())) {
            paramCondition = GOODS_FROM_STORE.GOODS_APPROVAL_NUMBER.like(likeValue(param.getGoodsApprovalNumber())).or(paramCondition);
        }

        SelectConditionStep<GoodsFromStoreRecord> select = db().selectFrom(GOODS_FROM_STORE).where(baseCondition.and(paramCondition));
        PageResult<GoodsExternalDo> pageResult = getPageResult(select, param.getCurrentPage(), param.getPageRows(), GoodsExternalDo.class);
        return pageResult;
    }

    public void failMatchGoods(FailMatchedParam param) {
        db().update(GOODS_FROM_HIS).set(GOODS_FROM_HIS.IS_MATCH, MedicalGoodsConstant.FAIL_MATCHED)
            .where(GOODS_FROM_HIS.ID.eq(param.getHisId()))
            .execute();
    }

    public List<GoodsExternalDo> listStoreCanUpGoods(Integer startRows, Integer pageRows) {
        return db().selectFrom(GOODS_FROM_STORE)
            .where(GOODS_FROM_STORE.IS_MATCH.eq(MedicalGoodsConstant.NOT_MATCHED).and(GOODS_FROM_STORE.STATE.eq(BaseConstant.EXTERNAL_ITEM_STATE_ENABLE)))
            .limit(startRows,pageRows)
            .fetchInto(GoodsExternalDo.class);
    }
}
