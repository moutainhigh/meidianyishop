package com.meidianyi.shop.dao.shop.label;

import com.meidianyi.shop.common.pojo.shop.table.GoodsLabelCoupleDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsLabelCoupleRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL_COUPLE;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */

@Repository
public class GoodsLabelCoupleDao extends ShopBaseDao {

    /**
     * 批量新增关联关系
     * @param goodsLabelCoupleDos
     */
    public void batchInsert(List<GoodsLabelCoupleDo> goodsLabelCoupleDos) {
        List<GoodsLabelCoupleRecord> goodsLabelCoupleRecords = new ArrayList<>(goodsLabelCoupleDos.size());

        for (GoodsLabelCoupleDo goodsLabelCoupleDo : goodsLabelCoupleDos) {
            GoodsLabelCoupleRecord record = new GoodsLabelCoupleRecord();
            record.setLabelId(goodsLabelCoupleDo.getLabelId());
            record.setGtaId(goodsLabelCoupleDo.getGtaId());
            record.setType(goodsLabelCoupleDo.getType());
            goodsLabelCoupleRecords.add(record);
        }

        db().batchInsert(goodsLabelCoupleRecords).execute();
    }

    /**
     * 删除标签关联信息
     * @param gtaIds
     * @param gtaType
     */
    public void deleteCouple(List<Integer> gtaIds, Byte gtaType) {
        db().deleteFrom(GOODS_LABEL_COUPLE).where(GOODS_LABEL_COUPLE.GTA_ID.in(gtaIds).and(GOODS_LABEL_COUPLE.TYPE.eq(gtaType)))
            .execute();
    }

    /**
     * 根据标签id和标签类型获取对应的gtaId集合
     * @param labelId
     * @param type
     * @return
     */
    public List<GoodsLabelCoupleDo> listByLabelIdAndType(Integer labelId, Byte type) {
        return db().selectFrom(GOODS_LABEL_COUPLE)
            .where(GOODS_LABEL_COUPLE.TYPE.eq(type).and(GOODS_LABEL_COUPLE.LABEL_ID.eq(labelId)))
            .fetchInto(GoodsLabelCoupleDo.class);
    }

    /**
     * 根据gtaid集合和对应的类型 获取信息
     * @param gtaIds
     * @param type
     * @return
     */
    public List<GoodsLabelCoupleDo> listByGtaIdsAndType(List<Integer> gtaIds, Byte type) {
        return db().selectFrom(GOODS_LABEL_COUPLE).where(GOODS_LABEL_COUPLE.TYPE.eq(type).and(GOODS_LABEL_COUPLE.GTA_ID.in(gtaIds)))
            .fetchInto(GoodsLabelCoupleDo.class);
    }

    /**
     * 根据类型获取所有关联信息
     * @param type
     * @return
     */
    public List<GoodsLabelCoupleDo> listByType(Byte type) {
        return db().selectFrom(GOODS_LABEL_COUPLE).where(GOODS_LABEL_COUPLE.TYPE.eq(type))
            .fetchInto(GoodsLabelCoupleDo.class);
    }

}
