package com.meidianyi.shop.service.shop.order.refund.record;

import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.shop.tables.RefundAmountRecord;
import com.meidianyi.shop.db.shop.tables.records.RefundAmountRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.sub.SubOrderService;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.meidianyi.shop.db.shop.tables.RefundAmountRecord.REFUND_AMOUNT_RECORD;

;

/**
 * Table:REFUND_AMOUNT_RECORD
 * @author 王帅
 *
 */
@Service
public class RefundAmountRecordService extends ShopBaseService{

	public final RefundAmountRecord TABLE = REFUND_AMOUNT_RECORD;
	/**会员卡余额退款*/
	public static String MEMBER_CARD_BALANCE = "member_card_balance";
	/**余额退款*/
	public static String USE_ACCOUNT = "use_account";
	/**积分退款*/
	public static String SCORE_DISCOUNT = "score_discount";
	/**微信退款*/
	public static String MONEY_PAID = "money_paid";
	@Autowired
	private OrderInfoService orderInfo;
	@Autowired
	private SubOrderService subOrderService;

	/**
	 * 	获取该订单退款汇总信息(存在优先级)
	 *
	 * @param orderSns
	 * @return Map<支付种类(细分) , 金额>
	 */
	public LinkedHashMap<String , BigDecimal> getReturnAmountMap(List<String> orderSns , Integer retId, Integer ignoreRetid){
		//构造成功退款的汇总
		LinkedHashMap<String, BigDecimal> result = new LinkedHashMap<String , BigDecimal>(orderInfo.PAY_SUBDIVISION.length);
		Map<String, Result<Record2<String, BigDecimal>>> map = getOrderRefundAmount(orderSns , retId, ignoreRetid);
		for (String key : orderInfo.PAY_SUBDIVISION) {
			if(map.get(key) == null || map.get(key).size() == 0) {
				result.put(key, BigDecimal.ZERO);
			}else {
				for (Record2<String, BigDecimal> record : map.get(key)) {
					if(map.get(key) == null) {
						result.put(key, record.value2());
					}else {
						result.put(key, BigDecimalUtil.add(result.get(key),record.value2()));
					}
				}
			}
		}
		return result;
	}

    /**
     * 退款执行顺序
     * @return
     */
    public LinkedHashMap<String , BigDecimal> executeRefundRecord(){
        LinkedHashMap<String, BigDecimal> map = Maps.newLinkedHashMapWithExpectedSize(orderInfo.REFUND_SUBDIVISION.length);
        for (String key : orderInfo.REFUND_SUBDIVISION) {
            map.put(key, null);
        }
        return map;
    }
	/**
	 * 	获取该订单退款记录map
	 * @param orderSns
	 * @return Map<String, Record2<String, BigDecimal>>>
	 */
	public Map<String, Result<Record2<String, BigDecimal>>> getOrderRefundAmount(List<String> orderSns ,Integer retId, Integer ignoreRetid){
		SelectConditionStep<Record2<String, BigDecimal>> where = db().select(TABLE.REFUND_FIELD,TABLE.REFUND_MONEY).from(TABLE).where(TABLE.ORDER_SN.in(orderSns));
		if(Objects.nonNull(retId)) {
			where.and(TABLE.RET_ID.eq(retId));
		}
		if(ignoreRetid != null) {
            where.and(TABLE.RET_ID.notEqual(ignoreRetid));
        }
		Map<String, Result<Record2<String, BigDecimal>>> map = where.fetchGroups(TABLE.REFUND_FIELD);
		return map;
	}

    /**
     * 退款动作完成记录留痕
     * @param orderSn 订单号
     * @param userId 用户id
     * @param refundField 类型
     * @param money 金额
     * @param retId 退款订单id
     */
	public void addRecord(String orderSn , Integer userId ,String refundField ,BigDecimal money ,Integer retId) {
		RefundAmountRecordRecord record = db().newRecord(TABLE);
		record.setOrderSn(orderSn);
		record.setUserId(userId);
		record.setRefundField(refundField);
		record.setRefundMoney(money);
		record.setRetId(retId);
		record.insert();
	}

    /**
     * 	获取该订单退款总金额
     * @param orderSn
     * @return BigDecimal
     */
    public BigDecimal getOrderRefundAmount(String orderSn){
		List<String> subOrderSn = subOrderService.getSubOrderSn(orderSn);
		List<BigDecimal> result = db().select(TABLE.REFUND_MONEY).from(TABLE).where(TABLE.ORDER_SN.eq(orderSn).or(TABLE.ORDER_SN.in(subOrderSn))).fetchInto(BigDecimal.class);
        return result.stream().reduce(BigDecimalUtil.BIGDECIMAL_ZERO, BigDecimalUtil::add);
    }
}
