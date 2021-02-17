package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsChronicCoupleVo;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author chenjie
 * @date 2020年09月25日
 */
@Repository
public class GoodsChronicCoupleDao extends ShopBaseDao {
    public List<String> listChronicNames(){
        return db().selectDistinct(GOODS_CHRONIC_COUPLE.LABEL_NAME)
            .from(GOODS_CHRONIC_COUPLE)
            .fetchInto(String.class);
    }

    public List<GoodsChronicCoupleVo> listGoodsChronicCouples(){
        return db().select(GOODS_CHRONIC_COUPLE.LABEL_NAME,GOODS_CHRONIC_COUPLE.GOODS_COMMON_NAME,GOODS_LABEL.ID.as("label_id"))
            .from(GOODS_CHRONIC_COUPLE)
            .leftJoin(GOODS_LABEL).on(GOODS_LABEL.NAME.eq(GOODS_CHRONIC_COUPLE.LABEL_NAME).and(GOODS_LABEL.IS_CHRONIC.eq((byte)1)))
            .fetchInto(GoodsChronicCoupleVo.class);
    }

    public List<Integer> listGoodsChronicGoodsCouples(String chronicName){
        return db().select(GOODS_MEDICAL_INFO.GOODS_ID)
            .from(GOODS_CHRONIC_COUPLE)
            .leftJoin(GOODS_MEDICAL_INFO).on(GOODS_MEDICAL_INFO.GOODS_COMMON_NAME.eq(GOODS_CHRONIC_COUPLE.GOODS_COMMON_NAME))
            .where(GOODS_CHRONIC_COUPLE.LABEL_NAME.eq(chronicName))
            .and(GOODS_MEDICAL_INFO.GOODS_ID.gt(0))
            .fetchInto(Integer.class);
    }

}
