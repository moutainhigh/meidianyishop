package com.meidianyi.shop.dao.shop.label;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.pojo.shop.table.GoodsLabelDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsLabelRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelAddAndUpdateParam;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelBase;
import com.meidianyi.shop.service.pojo.shop.medical.label.MedicalLabelConstant;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL;
import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL_COUPLE;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Repository
public class GoodsLabelDao extends ShopBaseDao {

    public static final Byte IS_CHRONIC = 1;
    /**
     * 根据标签id集合查询有效标签信息
     * @param labelIds
     * @return
     */
    public List<GoodsLabelDo> getLabelByLabelIds(List<Integer> labelIds) {
        return db().selectDistinct(GOODS_LABEL.asterisk())
            .from(GOODS_LABEL)
            .where(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS_LABEL.ID.in(labelIds)))
            .fetchInto(GoodsLabelDo.class);
    }

    /**
     * 获取不同类型的商品标签
     * @param gatIds  待限制类型id
     * @param gtaType 限制的类型
     * @return
     */
    public List<GoodsLabelDo> getLabelByGtaInfo(List<Integer> gatIds, Byte gtaType) {
        Condition baseCondition = GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS_LABEL_COUPLE.TYPE.eq(gtaType));

        if (!MedicalLabelConstant.GTA_ALL.equals(gtaType)) {
            baseCondition = baseCondition.and(GOODS_LABEL_COUPLE.LABEL_ID.in(gatIds));
        }

        List<GoodsLabelDo> goodsLabelDos = db().select(GOODS_LABEL.asterisk())
            .from(GOODS_LABEL).innerJoin(GOODS_LABEL_COUPLE).on(GOODS_LABEL.ID.eq(GOODS_LABEL_COUPLE.LABEL_ID))
            .where(baseCondition).fetchInto(GoodsLabelDo.class);

        return goodsLabelDos;
    }

    /**
     * 新增商品标签
     *
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelAddAndUpdateParam}
     */
    public void insertOnly(GoodsLabelAddAndUpdateParam param) {
        transaction(() -> {
            GoodsLabelRecord record = db().newRecord(GOODS_LABEL, param);
            record.insert();
        });
    }

    public List<GoodsLabelBase> listChronicLabels(){
        return db().select(GOODS_LABEL.NAME,GOODS_LABEL.ID)
            .from(GOODS_LABEL)
            .where(GOODS_LABEL.IS_CHRONIC.eq(IS_CHRONIC))
            .fetchInto(GoodsLabelBase.class);
    }

    public GoodsLabelBase getLabelInfo(Integer labelId){
        return db().select(GOODS_LABEL.NAME,GOODS_LABEL.ID)
            .from(GOODS_LABEL)
            .where(GOODS_LABEL.ID.eq(labelId))
            .fetchAnyInto(GoodsLabelBase.class);
    }
}
