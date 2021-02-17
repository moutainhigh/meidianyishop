package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.db.shop.tables.records.GoodsLabelCoupleRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCouple;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;
import com.meidianyi.shop.service.shop.goods.es.goods.label.EsGoodsLabelCreateService;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL_COUPLE;

/**
 * @author 黄荣刚
 * @date 2019年7月5日
 */
@Service

public class GoodsLabelCoupleService extends ShopBaseService {


    @Autowired
    EsGoodsLabelCreateService esGoodsLabelCreateService;

    /**
     * 批量插入商品标签所关联的商品ID信息，注意没有开启事务，须由调用者开启
     *
     * @param labelId  商品标签ID
     * @param goodsIdList 商品ID列表
     */
    public void batchInsertGoodsTypeGoodsLabelCouple(Integer labelId, List<Integer> goodsIdList) {
        if (labelId == null || goodsIdList == null) {
            return;
        }
        batchInsertGoodsLabelCouple(labelId,goodsIdList,GoodsLabelCoupleTypeEnum.GOODSTYPE);
    }

    /**
     * 批量插入商品标签所关联的商家分类ID信息
     * @param labelId         商品标签ID
     * @param sortIdList 商家分类ID列表
     */
    public void batchInsertSortTypeGoodsLabelCouple(Integer labelId, List<Integer> sortIdList) {
        if (labelId == null || sortIdList == null) {
            return;
        }
        batchInsertGoodsLabelCouple(labelId,sortIdList,GoodsLabelCoupleTypeEnum.SORTTYPE);
    }

    /**
     * 批量插入商品标签所关联的平台分类ID信息
     * @param labelId        商品标签ID
     * @param catIdList 平台分类ID列表
     */
    public void batchInsertCatTypeGoodsLabelCouple(Integer labelId, List<Integer> catIdList) {
        if (labelId == null || catIdList == null) {
            return;
        }
        batchInsertGoodsLabelCouple(labelId,catIdList,GoodsLabelCoupleTypeEnum.CATTYPE);
    }

    /**
     * 批量插入商品标签关联所有商品信息
     * @param labelId        商品标签ID
     */
    public void insertAllGoodsLabelCouple(Integer labelId) {
        if (labelId == null) {
            return;
        }
        batchInsertGoodsLabelCouple(labelId,null,GoodsLabelCoupleTypeEnum.ALLTYPE);
    }
    /**
     * 批量增加商品标签的关联信息
     * @param labelId 标签id
     * @param gtaIdList 关联项id集合
     * @param typeEnum 插入的关联类型
     */
    private void batchInsertGoodsLabelCouple(Integer labelId, List<Integer> gtaIdList, GoodsLabelCoupleTypeEnum typeEnum) {
        if (GoodsLabelCoupleTypeEnum.ALLTYPE.equals(typeEnum)) {
            GoodsLabelCoupleRecord record = new GoodsLabelCoupleRecord();
            record.setLabelId(labelId);
            record.setType(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode());
            db().executeInsert(record);
        } else {
            List<GoodsLabelCoupleRecord> recordList = new ArrayList<>();
            gtaIdList.forEach(gtaId->{
                GoodsLabelCoupleRecord record = new GoodsLabelCoupleRecord();
                record.setLabelId(labelId);
                record.setType(typeEnum.getCode());
                record.setGtaId(gtaId);
                recordList.add(record);
            });
            db().batchInsert(recordList).execute();
        }
    }

    /**
     * 批量增加商品标签的关联信息
     *
     * @param goodsLabelCoupleList
     */
    public void batchInsert(List<GoodsLabelCouple> goodsLabelCoupleList) {
        if (goodsLabelCoupleList == null || goodsLabelCoupleList.size() == 0) {
            return;
        }
        List<GoodsLabelCoupleRecord> labelCoupleRecordList = new ArrayList<GoodsLabelCoupleRecord>(goodsLabelCoupleList.size());
        for (GoodsLabelCouple goodsLabelCouple : goodsLabelCoupleList) {
            labelCoupleRecordList.add(goodsLabelCouple.toRecord());
        }
        db().batchInsert(labelCoupleRecordList).execute();
    }

    /**
     * 批量更新标签关联表内信息，先删除旧的相关信息，再插入新的相关信息
     * @param gtas
     * @param labels
     * @param type
     * @author 李晓冰
     * @return
     */
    public void batchUpdateLabelCouple(List<Integer> gtas, List<Integer> labels, GoodsLabelCoupleTypeEnum type) {
        transaction(()->{
            db().delete(GOODS_LABEL_COUPLE).where(GOODS_LABEL_COUPLE.GTA_ID.in(gtas).and(GOODS_LABEL_COUPLE.TYPE.eq(type.getCode())))
                .execute();
            List<GoodsLabelCoupleRecord> goodsLabelCoupleRecords=new ArrayList<>(labels.size()*gtas.size());
            for (Integer gta : gtas) {
                for (Integer labelId : labels) {
                    GoodsLabelCoupleRecord record = new GoodsLabelCoupleRecord();
                    record.setType(type.getCode());
                    record.setGtaId(gta);
                    record.setLabelId(labelId);
                    goodsLabelCoupleRecords.add(record);
                }
            }
            db().batchInsert(goodsLabelCoupleRecords).execute();
        });
    }

    /**
     * 根据商品ids 删除对应标签对应关系 并对外提供统一事务入口
     * @param goodsIds
     * @author 李晓冰
     */
    public void deleteByGoodsIds(List<Integer> goodsIds) {
        db().delete(GOODS_LABEL_COUPLE)
                .where(GOODS_LABEL_COUPLE.GTA_ID.in(goodsIds))
                .and(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode()))
                .execute();
    }

    public void updateByGoodsId(Integer goodsId, List<Integer> labelIds) {
        db().delete(GOODS_LABEL_COUPLE)
            .where(GOODS_LABEL_COUPLE.GTA_ID.eq(goodsId))
            .and(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode()))
            .execute();
        List<GoodsLabelCoupleRecord> list=new ArrayList<>(labelIds.size());
        labelIds.forEach(labelId ->{
            GoodsLabelCoupleRecord record=new GoodsLabelCoupleRecord();
            record.setGtaId(goodsId);
            record.setLabelId(labelId);
            record.setType(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode());
            list.add(record);
        });
        db().batchInsert(list).execute();
        try {
            esGoodsLabelCreateService.createEsLabelIndexForGoodsId(Collections.singletonList(goodsId), DBOperating.UPDATE);
        } catch (Exception e) {
            logger().debug(e.getMessage());
        }
    }

    /**
     * 根据标签id活动所有关联的商品ids或分类ids
     *
     * @param labelIds
     * @return
     */
    public Map<Byte,List<Integer>> selectGatIdsByLabelIds(List<Integer> labelIds) {
        Map<Byte, List<Integer>> byteListMap = db().select(GOODS_LABEL_COUPLE.GTA_ID, GOODS_LABEL_COUPLE.TYPE).from(GOODS_LABEL_COUPLE)
            .where(GOODS_LABEL_COUPLE.LABEL_ID.in(labelIds))
            .fetch().intoGroups(GOODS_LABEL_COUPLE.TYPE, GOODS_LABEL_COUPLE.GTA_ID);

        return byteListMap;
    }

    /**
     * 根据商品标签ID删除所有关联该标签的所有信息
     *
     * @param id
     */
    public void deleteByGoodsLabelId(Integer id) {
        db().deleteFrom(GOODS_LABEL_COUPLE)
                .where(GOODS_LABEL_COUPLE.LABEL_ID.eq(id))
                .execute();
    }

    public List<GoodsLabelCouple> getGoodsLabelCouple(Condition param){
        return db().select(GOODS_LABEL_COUPLE.ID, GOODS_LABEL_COUPLE.LABEL_ID, GOODS_LABEL_COUPLE.GTA_ID, GOODS_LABEL_COUPLE.TYPE)
            .from(GOODS_LABEL_COUPLE)
            .where(param)
            .fetchInto(GoodsLabelCouple.class);
    }
    /**
     * 根据标签id 和 对应类型获取相关链的目标id集合
     * @param labelIds 标签id集合
     * @param type 标签关联的类型
     * @return 相关联的目标id集合
     */
    public List<Integer> getGoodsLabelCouple(List<Integer> labelIds,Byte type) {
        List<Integer> gtaIds = db().select(GOODS_LABEL_COUPLE.GTA_ID).from(GOODS_LABEL_COUPLE)
            .where(GOODS_LABEL_COUPLE.LABEL_ID.in(labelIds)).and(GOODS_LABEL_COUPLE.TYPE.eq(type)).fetchInto(Integer.class);
        return gtaIds;
    }
}
