package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.shop.tables.OrderGoods;
import com.meidianyi.shop.db.shop.tables.OrderGoodsRebate;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRebateRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.rebate.OrderRebateVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.rebate.RebateRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品返利详情
 * @author wangshuai
 */
@Service
public class OrderGoodsRebateService extends ShopBaseService {
    final OrderGoodsRebate TABLE = OrderGoodsRebate.ORDER_GOODS_REBATE;
    final User TABLE_USER = User.USER;
    final OrderGoods TABLE_ORDRE_GOODS = OrderGoods.ORDER_GOODS;

    /**
     * 添加记录（此时并不入库）
     * @param rebateRecords
     * @param bo
     * @param canRebateMoney
     * @param check
     * @param orderSn
     * @return
     */
    public ArrayList<OrderGoodsRebateRecord> create(List<RebateRecord> rebateRecords, OrderGoodsBo bo, BigDecimal canRebateMoney, BigDecimal check, String orderSn) {
        ArrayList<OrderGoodsRebateRecord> result = new ArrayList<OrderGoodsRebateRecord>(rebateRecords.size());
        BigDecimal goodsTotalRebateMoney = BigDecimalUtil.BIGDECIMAL_ZERO;
        for (RebateRecord rebateRecord: rebateRecords) {
            OrderGoodsRebateRecord record = db().newRecord(TABLE);
            record.setRebateUserId(rebateRecord.getUserId());
            record.setGoodsId(bo.getGoodsId());
            record.setOrderSn(orderSn);
            record.setRebateLevel(rebateRecord.getRebateLevel());
            record.setProductId(bo.getProductId());
            record.setRebatePercent(rebateRecord.getRatio());
            record.setTotalRebateMoney(BigDecimalUtil.multiply(canRebateMoney, rebateRecord.getRatio()));
            record.setRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                BigDecimalUtil.BigDecimalPlus.create(canRebateMoney, BigDecimalUtil.Operator.multiply),
                BigDecimalUtil.BigDecimalPlus.create(rebateRecord.getRatio(), BigDecimalUtil.Operator.divide),
                BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(bo.getGoodsNumber()))
            ));
            record.setRealRebateMoney(record.getTotalRebateMoney());
            goodsTotalRebateMoney = goodsTotalRebateMoney.add(record.getTotalRebateMoney());
            result.add(record);
        }
        //成本控制
        if(rebateRecords.get(0).getStrategy().getCostProtection() == OrderConstant.YES && BigDecimalUtil.compareTo(goodsTotalRebateMoney, check) > 0) {
            for (OrderGoodsRebateRecord record: result) {
                BigDecimal temp = record.getTotalRebateMoney();
                record.setTotalRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                    BigDecimalUtil.BigDecimalPlus.create(record.getTotalRebateMoney(), BigDecimalUtil.Operator.multiply),
                    BigDecimalUtil.BigDecimalPlus.create(check, BigDecimalUtil.Operator.divide),
                    BigDecimalUtil.BigDecimalPlus.create(goodsTotalRebateMoney))
                );
                record.setRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                    BigDecimalUtil.BigDecimalPlus.create(temp, BigDecimalUtil.Operator.multiply),
                    BigDecimalUtil.BigDecimalPlus.create(check, BigDecimalUtil.Operator.divide),
                    BigDecimalUtil.BigDecimalPlus.create(goodsTotalRebateMoney, BigDecimalUtil.Operator.divide),
                    BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(bo.getGoodsNumber()))
                ));
            }
        }
        return result;
    }

    public Result<OrderGoodsRebateRecord> get(String orderSn) {
        return db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetch();
    }

    public Result<OrderGoodsRebateRecord> get(String orderSn,Integer recId) {
        return db().selectFrom(TABLE)
            .where(TABLE.ORDER_SN.eq(orderSn))
            .and(TABLE.REC_ID.eq(recId))
            .fetch();
    }
    public List<OrderRebateVo> getByOrderSn(String orderSn) {
        return db().select(TABLE.asterisk(),TABLE_USER.USERNAME, TABLE_USER.MOBILE, TABLE_ORDRE_GOODS.GOODS_PRICE, TABLE_ORDRE_GOODS.GOODS_NAME, TABLE_ORDRE_GOODS.CAN_CALCULATE_MONEY, TABLE_ORDRE_GOODS.COST_PRICE,TABLE_ORDRE_GOODS.GOODS_NUMBER, TABLE_ORDRE_GOODS.RETURN_NUMBER, TABLE_ORDRE_GOODS.FANLI_STRATEGY).from(TABLE)
            .leftJoin(TABLE_USER).on(TABLE_USER.USER_ID.eq(TABLE.REBATE_USER_ID))
            .leftJoin(TABLE_ORDRE_GOODS).on(TABLE_ORDRE_GOODS.REC_ID.eq(TABLE.REC_ID))
            .where(TABLE.ORDER_SN.eq(orderSn))
            .orderBy(TABLE.REBATE_LEVEL)
            .fetchInto(OrderRebateVo.class);
    }

    /**
     * processSaveOrderInfo方法数据保存
     * @param bos
     * @param records
     */
    public void save(List<OrderGoodsBo> bos, List<OrderGoodsRecord> records) {
        //更新b2c_order_goods_rebate的rec_id
        List<OrderGoodsRebateRecord> update = new ArrayList<>();
        for (int i = 0, length = records.size(); i < length; i++) {
            ArrayList<OrderGoodsRebateRecord> rebateList = bos.get(i).getRebateList();
            if(CollectionUtils.isNotEmpty(rebateList)) {
                for (OrderGoodsRebateRecord record: rebateList) {
                    record.setRecId(records.get(i).getRecId());
                }
                update.addAll(rebateList);
            }
        }
        if(CollectionUtils.isNotEmpty(update)) {
            db().batchInsert(update).execute();
        }

    }
}
