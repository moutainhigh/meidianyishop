package com.meidianyi.shop.service.shop.goods.mp;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.goods.label.GoodsLabelMpVo;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL;
import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL_COUPLE;

/**
 * @author 李晓冰
 * @date 2019年10月12日
 */
@Service
public class GoodsLabelMpService extends ShopBaseService {

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

    /**
     * 获取小程序商品搜索页-搜索条件-标签信息
     * @return 标签信息{@link GoodsLabelMpVo}
     */
    public List<GoodsLabelMpVo> getGoodsSearchFilterCondition(){
        return db().selectFrom(db().selectDistinct(GOODS_LABEL.ID, GOODS_LABEL.NAME,GOODS_LABEL.LEVEL,GOODS_LABEL.CREATE_TIME)
            .from(GOODS_LABEL).join(GOODS_LABEL_COUPLE).on(GOODS_LABEL.ID.eq(GOODS_LABEL_COUPLE.LABEL_ID))
            .where(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode()).or(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode())))
            .asTable("self_table")
        ).orderBy(DSL.field("self_table.level").desc(), DSL.field("self_table.create_time").desc()).fetchInto(GoodsLabelMpVo.class);
    }

    /**
     * 根据标签id集合，查询对应的标签数据
     * @param labelIds 商品标签id集合
     * @return 商品信息
     */
    public Result<Record2<Integer, Byte>>  getGoodsLabelsCoupleTypeInfoByIds(List<Integer> labelIds) {
        return db().select(GOODS_LABEL_COUPLE.GTA_ID, GOODS_LABEL_COUPLE.TYPE).from(GOODS_LABEL_COUPLE)
            .where(GOODS_LABEL_COUPLE.LABEL_ID.in(labelIds))
            .and(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode())
                .or(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode())))
            .fetch();
    }
}
