package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsMedicalInfoRecord;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.GOODS_MEDICAL_INFO;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Repository
public class GoodsMedicalInfoDao extends ShopBaseDao{

    /**
     * 插入商品的药品相关信息
     * @param goodsMedicalInfoDo
     */
    public void insert(GoodsMedicalInfoDo goodsMedicalInfoDo) {
        GoodsMedicalInfoRecord goodsMedicalInfoRecord = db().newRecord(GOODS_MEDICAL_INFO);
        FieldsUtil.assign(goodsMedicalInfoDo,goodsMedicalInfoRecord);
        goodsMedicalInfoRecord.insert();
        goodsMedicalInfoDo.setId(goodsMedicalInfoRecord.getId());
    }

    /**
     * 修改商品的药品相关信息
     * @param goodsMedicalInfoDo
     */
    public void update(GoodsMedicalInfoDo goodsMedicalInfoDo) {
        GoodsMedicalInfoRecord goodsMedicalInfoRecord = new GoodsMedicalInfoRecord();
        FieldsUtil.assign(goodsMedicalInfoDo,goodsMedicalInfoRecord);
        db().executeUpdate(goodsMedicalInfoRecord);
    }

    /**
     * 批量新增
     * @param goodsMedicalInfoDos
     */
    public void batchInsert(List<GoodsMedicalInfoDo> goodsMedicalInfoDos) {
        List<GoodsMedicalInfoRecord> goodsMedicalInfoRecords = new ArrayList<>(goodsMedicalInfoDos.size());

        for (GoodsMedicalInfoDo goodsMedicalInfoDo : goodsMedicalInfoDos) {
            GoodsMedicalInfoRecord goodsMedicalInfoRecord = new GoodsMedicalInfoRecord();
            FieldsUtil.assign(goodsMedicalInfoDo,goodsMedicalInfoRecord);
            goodsMedicalInfoRecords.add(goodsMedicalInfoRecord);
        }
        db().batchInsert(goodsMedicalInfoRecords).execute();
    }

    public void batchUpdate(List<GoodsMedicalInfoDo> goodsMedicalInfoDos) {
        List<GoodsMedicalInfoRecord> goodsMedicalInfoRecords = new ArrayList<>(goodsMedicalInfoDos.size());

        for (GoodsMedicalInfoDo goodsMedicalInfoDo : goodsMedicalInfoDos) {
            GoodsMedicalInfoRecord goodsMedicalInfoRecord = new GoodsMedicalInfoRecord();
            FieldsUtil.assign(goodsMedicalInfoDo,goodsMedicalInfoRecord);
            goodsMedicalInfoRecords.add(goodsMedicalInfoRecord);
        }
        db().batchUpdate(goodsMedicalInfoRecords).execute();
    }

    /**
     * 根据商品id查询
     * @param goodsId
     * @return
     */
    public GoodsMedicalInfoDo getByGoodsId(Integer goodsId) {
        GoodsMedicalInfoDo goodsMedicalInfoDo = db().selectFrom(GOODS_MEDICAL_INFO)
            .where(GOODS_MEDICAL_INFO.GOODS_ID.eq(goodsId).and(GOODS_MEDICAL_INFO.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchAnyInto(GoodsMedicalInfoDo.class);

        return goodsMedicalInfoDo;
    }

    /**
     * 根据商品id获取对应的药品信息
     * @param goodsIds 商品id集合
     * @return 药品结合
     */
    public  List<GoodsMedicalInfoDo> listByGoodsIds(List<Integer> goodsIds) {
        return db().selectFrom(GOODS_MEDICAL_INFO)
            .where(GOODS_MEDICAL_INFO.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(GOODS_MEDICAL_INFO.GOODS_ID.in(goodsIds)))
            .fetchInto(GoodsMedicalInfoDo.class);
    }

    public List<GoodsMedicalInfoDo> listIdWithGoodsId(List<Integer> goodsId){
         return  db().select(GOODS_MEDICAL_INFO.GOODS_ID, GOODS_MEDICAL_INFO.ID).from(GOODS_MEDICAL_INFO)
            .where(GOODS_MEDICAL_INFO.IS_DELETE.eq(DelFlag.NORMAL_VALUE).and(GOODS_MEDICAL_INFO.GOODS_ID.in(goodsId)))
            .fetchInto(GoodsMedicalInfoDo.class);
    }

    public GoodsMedicalInfoDo getByHisInfo(String medicalKey){
        Condition baseCondition = GOODS_MEDICAL_INFO.IS_DELETE.eq(DelFlag.NORMAL_VALUE);
        Condition condition = GOODS_MEDICAL_INFO.GOODS_COMMON_NAME.concat(GOODS_MEDICAL_INFO.GOODS_QUALITY_RATIO.concat(GOODS_MEDICAL_INFO.GOODS_PRODUCTION_ENTERPRISE)).eq(medicalKey);
        condition = condition.or(GOODS_MEDICAL_INFO.GOODS_COMMON_NAME.eq(medicalKey));
        baseCondition =baseCondition.and(condition);

        GoodsMedicalInfoDo goodsMedicalInfoDo = db().select(GOODS_MEDICAL_INFO.ID,GOODS_MEDICAL_INFO.GOODS_ID, GOODS_MEDICAL_INFO.GOODS_APPROVAL_NUMBER, GOODS_MEDICAL_INFO.GOODS_COMMON_NAME, GOODS_MEDICAL_INFO.GOODS_QUALITY_RATIO, GOODS_MEDICAL_INFO.GOODS_PRODUCTION_ENTERPRISE)
            .from(GOODS_MEDICAL_INFO).where(baseCondition).fetchAnyInto(GoodsMedicalInfoDo.class);

        return goodsMedicalInfoDo;
    }

    /**
     * 根据药品通用名称，规格系数，生产企业,查询his中的药品id对应关系
     * @param goodsKeys
     * @return
     */
    public Map<String, Integer> mapGoodsHisKeyToGoodsId(List<String> goodsKeys) {
        Condition medicalCondition = GOODS_MEDICAL_INFO.IS_DELETE.eq(DelFlag.NORMAL_VALUE);
        medicalCondition = medicalCondition.and(GOODS_MEDICAL_INFO.GOODS_COMMON_NAME.concat(GOODS_MEDICAL_INFO.GOODS_QUALITY_RATIO.concat(GOODS_MEDICAL_INFO.GOODS_PRODUCTION_ENTERPRISE)).in(goodsKeys));

        List<GoodsMedicalInfoDo> goodsMedicalInfoDos = db().select(GOODS_MEDICAL_INFO.GOODS_ID, GOODS_MEDICAL_INFO.GOODS_COMMON_NAME, GOODS_MEDICAL_INFO.GOODS_QUALITY_RATIO, GOODS_MEDICAL_INFO.GOODS_PRODUCTION_ENTERPRISE)
            .from(GOODS_MEDICAL_INFO).where(medicalCondition).fetchInto(GoodsMedicalInfoDo.class);

        Map<String, Integer> medicalInfos = goodsMedicalInfoDos.stream().collect(Collectors.toMap(x -> x.getGoodsCommonName() + x.getGoodsQualityRatio() + x.getGoodsProductionEnterprise(), GoodsMedicalInfoDo::getGoodsId, (x1, x2) -> x1));

        Condition normalGoodsCondition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE);
        normalGoodsCondition=normalGoodsCondition.and(GOODS.GOODS_NAME.in(goodsKeys));
        List<GoodsDo> goodsDos = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME).from(GOODS).where(normalGoodsCondition).fetchInto(GoodsDo.class);
        Map<String, Integer> normalGoodsInfos = goodsDos.stream().collect(Collectors.toMap(GoodsDo::getGoodsName, GoodsDo::getGoodsId, (x1, x2) -> x1));

        medicalInfos.putAll(normalGoodsInfos);

        return medicalInfos;
    }

    public void deleteByGoodsId(Integer goodsId) {
        db().update(GOODS_MEDICAL_INFO)
            .set(GOODS_MEDICAL_INFO.IS_DELETE, DelFlag.DISABLE_VALUE)
            .where(GOODS_MEDICAL_INFO.GOODS_ID.eq(goodsId))
            .execute();
    }

}
