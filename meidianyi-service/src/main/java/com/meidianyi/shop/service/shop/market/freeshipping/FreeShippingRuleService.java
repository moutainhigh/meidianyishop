package com.meidianyi.shop.service.shop.market.freeshipping;

import com.meidianyi.shop.db.shop.tables.records.FreeShippingRuleRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.jooq.DeleteConditionStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.shop.tables.FreeShippingRule.FREE_SHIPPING_RULE;

/**
 * @author 孔德成
 * @date 2019/7/31 14:03
 */
@Service
public class FreeShippingRuleService extends ShopBaseService {
    public final static Integer CONTYPE_MONEY=0;
    public final static Integer CONTYPE_NUM=1;
    public final static Integer CONTYPE_NUM_MONEY=2;


    @Autowired
    private FreeShippingService freeShippingService;

    /**
     * 根据freeShippingId查询freeShipppingrule
     *
     * @param ruleId
     * @return
     */
    public FreeShippingRuleRecord getRuleByRuleId(Integer ruleId) {
        FreeShippingRuleRecord result = db().selectFrom(FREE_SHIPPING_RULE)
                .where(FREE_SHIPPING_RULE.ID.eq(ruleId))
                .fetchAny();
        return result;
    }

    /**
     * 获取轰动规则
     *
     * @param freeShipId
     * @return
     */
    public Result<FreeShippingRuleRecord> getFreeShippingRule(Integer freeShipId) {
        return db()
                .selectFrom(FREE_SHIPPING_RULE)
                .where(FREE_SHIPPING_RULE.SHIPPING_ID.eq(freeShipId))
                .fetch();
    }

    /**
     * 根据规则获取活动商品
     *
     * @param ruleId
     */
    public void getFreeDetailByRule(int ruleId) {
        Record record = freeShippingService.getInfoByRule(ruleId);
        if (record != null) {

        }

        return;
    }

    /**
     * 获取规则详情
     *
     * @param ruleId
     * @return
     */
    public FreeShippingRuleRecord getRuleDetailByRule(Integer ruleId) {
        return db().selectFrom(FREE_SHIPPING_RULE).where(FREE_SHIPPING_RULE.ID.eq(ruleId)).fetchOne();
    }

    /**
     * 删除不在关联中的规则
     *
     * @param freeShippingId
     * @return
     */
    public int deleteByid(Integer freeShippingId, List<?> ruleList) {
        DeleteConditionStep<FreeShippingRuleRecord> select = db().deleteFrom(FREE_SHIPPING_RULE)
                .where(FREE_SHIPPING_RULE.SHIPPING_ID.eq(freeShippingId));
        if (ruleList != null && ruleList.size() > 0) {
            select.and(FREE_SHIPPING_RULE.ID.notIn(ruleList));
        }
        return select.execute();

    }


}
