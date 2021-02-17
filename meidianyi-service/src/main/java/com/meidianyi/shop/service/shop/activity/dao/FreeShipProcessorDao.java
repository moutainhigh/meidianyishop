package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.FreeShipPromotion;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.FreeShipping.FREE_SHIPPING;
import static com.meidianyi.shop.db.shop.tables.FreeShippingRule.FREE_SHIPPING_RULE;

/**
 * 满包邮dao
 * @author 李晓冰
 * @date 2020年01月13日
 */
@Service
public class FreeShipProcessorDao extends ShopBaseService {

    /**
     * 获取满包邮信息
     * @param goodsId 商品ID
     * @param catId 平台分类ID
     * @param sortId 商家分类ID
     * @param now 时间
     * @return List<FreeShipPromotion>
     */
    public List<FreeShipPromotion> getFreeShipProcessorForDetail(Integer goodsId, Integer catId, Integer sortId, Timestamp now){

        Condition timeCondition = FREE_SHIPPING.EXPIRE_TYPE.eq(BaseConstant.ACTIVITY_IS_FOREVER).or(FREE_SHIPPING.EXPIRE_TYPE.eq(BaseConstant.ACTIVITY_NOT_FOREVER)
            .and(FREE_SHIPPING.START_TIME.le(now).and(FREE_SHIPPING.END_TIME.ge(now))));

        Condition pointCondition = buildCondition(goodsId,catId,sortId);

        Result<Record5<Integer, String, Integer, BigDecimal, Integer>> result = db().select(FREE_SHIPPING_RULE.ID, FREE_SHIPPING_RULE.AREA,
            FREE_SHIPPING_RULE.CON_TYPE, FREE_SHIPPING_RULE.MONEY, FREE_SHIPPING_RULE.NUM).from(FREE_SHIPPING).innerJoin(FREE_SHIPPING_RULE).on(FREE_SHIPPING_RULE.SHIPPING_ID.eq(FREE_SHIPPING.ID))
            .where(FREE_SHIPPING.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(FREE_SHIPPING.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(timeCondition).and(pointCondition))
            .orderBy(FREE_SHIPPING.LEVEL.desc(), FREE_SHIPPING.CREATE_TIME.desc()).fetch();

        List<FreeShipPromotion> returnList = new ArrayList<>(result.size());

        for (int i = 0; i < result.size(); i++) {
            Record5<Integer, String, Integer, BigDecimal, Integer> record5 = result.get(i);
            FreeShipPromotion promotion =new FreeShipPromotion();
            promotion.setPromotionId(record5.get(FREE_SHIPPING_RULE.ID));
            promotion.setConType(record5.get(FREE_SHIPPING_RULE.CON_TYPE));
            promotion.setMoney(record5.get(FREE_SHIPPING_RULE.MONEY));
            promotion.setNum(record5.get(FREE_SHIPPING_RULE.NUM));
            promotion.setIsFullArea(StringUtils.isBlank(record5.get(FREE_SHIPPING_RULE.AREA)));
            returnList.add(promotion);
        }
        return returnList;
    }

    private Condition buildCondition(Integer goodsId,Integer catId,Integer sortId){
        Condition condition = DSL.noCondition();

        if (goodsId != null) {
            condition = condition.or(DslPlus.findInSet(goodsId,FREE_SHIPPING.RECOMMEND_GOODS_ID));
        }

        if (catId != null) {
            condition = condition.or(DslPlus.findInSet(catId,FREE_SHIPPING.RECOMMEND_CAT_ID));
        }

        if (sortId != null) {
            condition = condition.or(DslPlus.findInSet(sortId,FREE_SHIPPING.RECOMMEND_SORT_ID));
        }

        condition = FREE_SHIPPING.TYPE.eq(BaseConstant.GOODS_AREA_TYPE_ALL).or(condition);

        return condition;
    }
}
