package com.meidianyi.shop.service.shop.order.goods;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.db.shop.tables.OrderGoods;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiOrderGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.param.SyncHisMedicalOrderRequestParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundVo.RefundVoGoods;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.GoodsAndOrderInfoBo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.record.GoodsOrderRecordSmallVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.impl.DSL;
import org.jooq.impl.TableRecordImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;

/**
 * Table:ORDER_GOODS
 * @author 王帅
 *
 */
@Service
public class OrderGoodsService extends ShopBaseService {

	public final OrderGoods TABLE = ORDER_GOODS;
	@Autowired
	private OrderGoodsDao orderGoodsDao;
	@Autowired
    private PrescriptionItemDao prescriptionItemDao;

	/** 商品数量 发货数量 退款成功数量*/
	public static byte TOTAL_GOODSNUMBER = 0,TOTAL_SENDNUMBER = 1,TOTAL_SUCCESSRETURNNUMBER = 2;

	/**
	 * 	通过订单id[]查询其下商品
	 * @param arrayToSearch
	 * @return  Result<?>
	 */
	public Result<?> getByOrderIds(Integer... arrayToSearch) {
		Result<?> goods = db().select(TABLE.ORDER_ID,TABLE.REC_ID,TABLE.ORDER_SN,TABLE.GOODS_ID,TABLE.GOODS_NAME,TABLE.GOODS_SN,TABLE.GOODS_NUMBER,TABLE.GOODS_PRICE,TABLE.MARKET_PRICE,TABLE.GOODS_ATTR,TABLE.PRODUCT_SN,TABLE.PRODUCT_ID,TABLE.GOODS_IMG,TABLE.MAIN_REC_ID,TABLE.STRA_ID,TABLE.PER_DISCOUNT,TABLE.GOODS_SCORE,TABLE.IS_GIFT,TABLE.GIFT_ID,TABLE.DISCOUNTED_GOODS_PRICE,TABLE.ACTIVITY_TYPE,TABLE.IS_CARD_EXCLUSIVE).from(TABLE)
			.where(TABLE.ORDER_ID.in(arrayToSearch))
			.orderBy(TABLE.ORDER_ID.desc())
			.fetch();
		return goods;
	}

    /**
     * 通过订单id[]查询其下商品
     * @param orderId id
     * @return
     */
	public Result<OrderGoodsRecord> getByOrderId(Integer orderId) {
		return db().selectFrom(TABLE).where(TABLE.ORDER_ID.eq(orderId)).fetch();
	}

    /**
     * 根据处方号查药品列表
     * @param prescriptionCode 处方号
     * @return List<OrderGoodsDo>
     */
	public List<OrderGoodsDo> getByPrescription(String prescriptionCode) {
	    return orderGoodsDao.getByPrescription(prescriptionCode);
    }

	/**
	 * 单个订单商品
	 * @param orderId
	 * @return map<recId,obj>
	 */
	public Map<Integer, OrderGoodsMpVo> getKeyMapByIds(Integer orderId) {
		return db().selectFrom(TABLE).where(TABLE.ORDER_ID.eq(orderId)).fetchMap(TABLE.REC_ID, OrderGoodsMpVo.class);
	}
	/**
	 * 	通过订单sn[]查询其下商品
	 * @param orderSns
	 * @return Map<String, List<RefundVoGoods>>
	 */
	public Map<String, List<RefundVoGoods>> getByOrderSns(List<String> orderSns) {
		return db().select(TABLE.ORDER_ID,TABLE.ORDER_SN,TABLE.REC_ID,TABLE.GOODS_NAME,TABLE.GOODS_NUMBER,TABLE.RETURN_NUMBER,TABLE.GOODS_PRICE,TABLE.GOODS_ATTR,TABLE.DISCOUNTED_GOODS_PRICE,TABLE.PRODUCT_ID,TABLE.IS_CAN_RETURN,TABLE.IS_GIFT,TABLE.DISCOUNTED_TOTAL_PRICE,TABLE.GOODS_ID,TABLE.MARKET_PRICE,TABLE.SEND_NUMBER,TABLE.GOODS_IMG).from(TABLE)
			.where(TABLE.ORDER_SN.in(orderSns))
			.fetchGroups(TABLE.ORDER_SN,RefundVoGoods.class);
	}

	/**
	 * 	通过订单sn[]查询其下商品
	 * @param orderSns
	 * @return Map<String, List<OrderGoodsMpVo>>
	 */
	public Map<String, List<OrderGoodsMpVo>> getByOrderGoodsSns(List<String> orderSns) {
		return db().select(TABLE.ORDER_ID,TABLE.ORDER_SN,TABLE.REC_ID,TABLE.GOODS_NAME,TABLE.GOODS_NUMBER,TABLE.RETURN_NUMBER,TABLE.GOODS_PRICE,TABLE.GOODS_ATTR,TABLE.DISCOUNTED_GOODS_PRICE,TABLE.PRODUCT_ID,TABLE.IS_CAN_RETURN,TABLE.IS_GIFT,TABLE.DISCOUNTED_TOTAL_PRICE,TABLE.GOODS_ID,TABLE.MARKET_PRICE,TABLE.SEND_NUMBER,TABLE.GOODS_IMG).from(TABLE)
			.where(TABLE.ORDER_SN.in(orderSns))
			.fetchGroups(TABLE.ORDER_SN,OrderGoodsMpVo.class);
	}

	/**
	 *	计算子订单商品数量(主订单返回的map->size=0)
	 * @param goods
	 * @param currentOrder
	 * @param isMain
	 * @return HashMap<recid, sum>
	 */
	public HashMap<Integer, Integer> countSubOrderGoods(Map<String, List<RefundVoGoods>> goods , OrderListInfoVo currentOrder , Boolean isMain) {
		if(isMain) {
			HashMap<Integer, Integer> count = new HashMap<Integer,Integer>(15);
			for (Entry<String, List<RefundVoGoods>> entry : goods.entrySet()) {
				if(!currentOrder.getOrderSn().equals(entry.getKey())) {
					for (RefundVoGoods oneGoods : entry.getValue()) {
						if(count.get(oneGoods.getRecId()) == null) {
							count.put(oneGoods.getRecId(), oneGoods.getGoodsNumber());
						}else {
							count.put(oneGoods.getRecId(), count.get(oneGoods.getRecId()) + oneGoods.getGoodsNumber());
						}
					}
				}
			}
		}
		return new HashMap<Integer,Integer>(0);
	}

    /**
     * 退款完成后，更新状态及退款退货数量
     * @param orderSn
     * @param returnGoods
     * @param returnOrderRecord
     */
	public void updateInReturn(String orderSn , List<ReturnOrderGoodsRecord> returnGoods , ReturnOrderRecord returnOrderRecord) {
		//退款商品recIds
		List<Integer> recIds = returnGoods.stream().map(ReturnOrderGoodsRecord::getRecId).collect(Collectors.toList());
		//退款退货商品对应的orderGoods商品
        Result<OrderGoodsRecord> orderGoods = getOrderGoods(orderSn, recIds);
        //退款商品map(key->recId)
		Map<Integer, ReturnOrderGoodsRecord> returnGoodsMap = returnGoods.stream().collect(Collectors.toMap(ReturnOrderGoodsRecord::getRecId,Function.identity()));
		for (OrderGoodsRecord goods : orderGoods) {
			switch (returnOrderRecord.getRefundStatus()) {
			//退款订单状态为完成
			case OrderConstant.REFUND_STATUS_FINISH:
				//状态
				goods.set(TABLE.REFUND_STATUS, returnOrderRecord.getRefundStatus());
				//此次退款退货数量
				int returnNum = returnOrderRecord.getReturnType().equals(OrderConstant.RT_MANUAL) ? 0 : (returnGoodsMap.get(goods.getRecId()) == null ? 0 : returnGoodsMap.get(goods.getRecId()).getGoodsNumber());
				//修改退货退款商品数量
				goods.setReturnNumber(goods.getReturnNumber() + returnNum);
				break;
			default:
				goods.set(TABLE.REFUND_STATUS, returnOrderRecord.getRefundStatus());
				break;
			}
		}
		db().batchUpdate(orderGoods).execute();
	}

    public Result<OrderGoodsRecord> getOrderGoods(String orderSn, List<Integer> recIds) {
        return db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn).and(TABLE.REC_ID.in(recIds))).fetch();
    }

    /**
	 * 	判断当前订单是否可以退商品（有无可退商品数量）
	 * 	(有状态依赖于ordergoods表的商品数量与已经退货退款数量)
	 * @param orderSn
	 * @return
	 */
	public boolean canReturnGoodsNumber(String orderSn) {
		if(db().fetchCount(TABLE, TABLE.ORDER_SN.eq(orderSn).and(TABLE.GOODS_NUMBER.gt(TABLE.RETURN_NUMBER))) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 	计算订单商品数量 ，发货数量， 退款成功数量
	 * @param orderSn
	 * @return
	 */
	public int[] getTotalNumber(String orderSn) {
		Record3<BigDecimal, BigDecimal, BigDecimal> result = db().select(DSL.sum(TABLE.GOODS_NUMBER),DSL.sum(TABLE.SEND_NUMBER),DSL.sum(TABLE.RETURN_NUMBER)).from(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetchOne();
		int[] total = {TOTAL_GOODSNUMBER ,TOTAL_SENDNUMBER ,TOTAL_SUCCESSRETURNNUMBER};
		if(result != null) {
			total[TOTAL_GOODSNUMBER] = result.value1() != null ? result.value1().intValue() : 0;
			total[TOTAL_SENDNUMBER] = result.value2() != null ? result.value2().intValue() : 0;
			total[TOTAL_SUCCESSRETURNNUMBER] = result.value3() != null ? result.value3().intValue() : 0;
		}
		return total;
	}
	/**
	 * 	计算处于部分发货的情况下是否可以发货
	 * @param orderSn
	 * @return
	 */
	public boolean isCanDeliverOrder(String orderSn) {
		if(db().fetchCount(TABLE,
				TABLE.ORDER_SN.eq(orderSn).
				and(TABLE.GOODS_NUMBER.gt(TABLE.RETURN_NUMBER)).
				and(TABLE.SEND_NUMBER.eq((0))
				)) > 0 ) {
			return true;
		}
		return false;
	}

	public Result<Record> selectWhere(Condition where) {
		return db().select(TABLE.asterisk()).from(TABLE).where(where).fetch();
	}

	public List<OrderGoodsVo> getReturnGoods(String orderSn) {
		return selectWhere(TABLE.ORDER_SN.eq(orderSn).and(TABLE.RETURN_NUMBER.gt(0))).into(OrderGoodsVo.class);
	}

    /**
     * 	营销活动订单-根据orderSn取订单行信息
     * @param orderSn
     * @return  List<MarketOrderGoodsListVo>
     */
    public List<MarketOrderGoodsListVo> getMarketOrderGoodsByOrderSn(String orderSn) {
        return db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetchInto(MarketOrderGoodsListVo.class);
    }

	/**
	 * 根据订单号查询商品
	 * @param orderSn
	 * @return
	 */
	public Result<? extends Record> getGoodsInfoByOrderSn(String orderSn){
		Result<Record6<Integer, String, String, String, BigDecimal, Integer>> record6s = db()
				.select(TABLE.GOODS_ID, TABLE.GOODS_SN, TABLE.GOODS_NAME, TABLE.GOODS_IMG, TABLE.GOODS_PRICE, TABLE.GOODS_NUMBER)
				.from(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetch();
		return record6s;
	}

	/**
	 * 查询商品信息
	 * @return
	 */
	public List<GoodsRecord>  getGoodsInfoRecordByOrderSn(String orderSn){
		return db().select(GOODS.GOODS_ID,GOODS.CAT_ID,GOODS.BRAND_ID,TABLE.GOODS_NUMBER)
				.from(TABLE)
				.leftJoin(GOODS).on(GOODS.GOODS_ID.eq(TABLE.GOODS_ID))
				.where(TABLE.ORDER_SN.eq(orderSn)).fetchInto(GoodsRecord.class);
	}

	/**
	 * 查询商品信息
	 * @param orderSn 订单sn
	 * @return
	 */
	public List<GoodsAndOrderInfoBo> getGoodsInfoAndOrderInfo(String orderSn){
		return db().select(TABLE.GOODS_ID, TABLE.PRODUCT_ID,TABLE.IS_GIFT,TABLE.GOODS_NUMBER, GOODS_SPEC_PRODUCT.PRD_NUMBER, GOODS.IS_ON_SALE, GOODS.DEL_FLAG)
				.from(TABLE)
				.leftJoin(GOODS).on(GOODS.GOODS_ID.eq(TABLE.GOODS_ID))
				.leftJoin(GOODS_SPEC_PRODUCT).on(TABLE.PRODUCT_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
				.where(TABLE.ORDER_SN.eq(orderSn)).fetchInto(GoodsAndOrderInfoBo.class);
	}

	/**
	 * 获取订单规格id
	 * @param orderSn 订单号
	 * @return 规格ids
	 */
	public List<Integer> getProductIdByOrderSn(String orderSn){
		return orderGoodsDao.getProductIdByOrderSn(orderSn);
	}

	/**
	 * 根据订单号查询商品
	 * @param orderSn
	 * @return
	 */
	public Result<OrderGoodsRecord> getOrderGoods(String orderSn) {
		return db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetch();
	}


    /**
     * 商品入库
     * @param order 订单
     * @param bos 商品
     */
    public List<OrderGoodsRecord> addRecords(OrderInfoRecord order, List<OrderGoodsBo> bos){
        List<OrderGoodsRecord> records = new ArrayList<>(bos.size());
        for (OrderGoodsBo bo : bos) {
            bo.setOrderId(order.getOrderId());
            bo.setOrderSn(order.getOrderSn());
            OrderGoodsRecord record = db().newRecord(TABLE, bo);
            if(bo.getFirstSpecialId() != null && bo.getFirstSpecialId() > 0) {
                //设置首单特惠在等等商品表记录，目前orderGoods中actId.type只记录首单特惠，后期考虑记录全部非叠加型活动
                record.setActivityType(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL);
                record.setActivityId(bo.getFirstSpecialId());
            }else if (bo.getPurchasePriceRuleId() != null && bo.getPurchasePriceRuleId() > 0) {
                //加价购
                record.setActivityType(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE);
                record.setActivityId(bo.getPurchasePriceId());
                record.setActivityRule(bo.getPurchasePriceRuleId());
            }else if(bo.getReducePriceId() != null && bo.getReducePriceId() > 0) {
                //限时降价
                record.setActivityType(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE);
                record.setActivityId(bo.getReducePriceId());
            }
            if(bo.getPurchasePriceId() != null) {
                record.setPurchaseId(bo.getPurchasePriceId());
            }
            if (record.getGoodsImg()==null||record.getGoodsImg().trim().isEmpty()){
            	//药品图片如果为空,使用默认图片
				record.setGoodsImg(MedicalGoodsConstant.MEDICAL_GOODS_WXAPP_DEFAULT_IMG);
			}
            records.add(record);
        }
        records.forEach(
            TableRecordImpl::insert
        );
        return records;
    }

    /**
     * 获取订单商品的活动类型
     *
     * @param order
     * @param bos
     * @param insteadPayMoney
     * @return
     */
    public Set<Byte> getGoodsType(OrderInfoRecord order, List<OrderGoodsBo> bos, BigDecimal insteadPayMoney){
        HashSet<Byte> type = new HashSet<>();
        for(OrderGoodsBo bo : bos){
            if(bo.getPurchasePriceRuleId() != null && bo.getPurchasePriceRuleId() > 0){
                //加价购活动
                type.add(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE);
            }
            if(bo.getReducePriceId() != null && bo.getReducePriceId() > 0){
                //限时降价
                type.add(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE);
            }
            if(bo.getFirstSpecialId() != null && bo.getFirstSpecialId() > 0){
                //首单特惠
                type.add(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL);
            }
            if(bo.getGiftId() != null && bo.getGiftId() > 0){
                //赠品
                type.add(BaseConstant.ACTIVITY_TYPE_GIFT);
            }
            if(bo.getFreeShip() != null && bo.getFreeShip() > 0){
                //满包邮
                type.add(BaseConstant.ACTIVITY_TYPE_FREESHIP_ORDER);
            }
        }
        if(BigDecimalUtil.compareTo(insteadPayMoney, null) == 1) {
            type.add(BaseConstant.ACTIVITY_TYPE_PAY_FOR_ANOTHER);
        }
        if(order.getFanliType() != null && order.getFanliType().equals(DistributionConstant.REBATE_ORDER)) {
            type.add(BaseConstant.ACTIVITY_TYPE_REBATE);
        }
        return type;
    }

    /**
     * 获取赠品活动订单号
     * @param giftId 赠品活动id
     * @param isIncludeReturn 是否包含退款
     * @return
     */
    public List<String> getGiftOrderSns(Integer giftId, boolean isIncludeReturn){
        Condition condition = TABLE.IS_GIFT.eq(OrderConstant.IS_GIFT_Y).and(TABLE.GIFT_ID.eq(giftId));
        if(!isIncludeReturn){
            condition.and(TABLE.RETURN_NUMBER.eq(0));
        }
        return db().selectDistinct(TABLE.ORDER_SN).from(TABLE).where(condition).fetchInto(String.class);
    }
	/**
	 *  购买商品记录(三个月内)
	 * @param userId  用户ID
	 * @param keyWord 关键字
	 * @param currentPages 当前页
	 * @param pageRows 每页行数
	 * @return Record
	 * @author kdc
	 */
    public Result<? extends Record> buyingHistoryGoodsList(Integer userId, String keyWord, Integer currentPages, Integer pageRows){
		Timestamp timestamp = DateUtils.getTimeStampPlus(-3, ChronoUnit.MONTHS);
		SelectConditionStep<? extends Record> select = db().select(TABLE.GOODS_ID, DslPlus.dateFormatDay(TABLE.CREATE_TIME).as("date"))
				.from(TABLE)
				.leftJoin(GOODS).on(GOODS.GOODS_ID.eq(TABLE.GOODS_ID))
				.leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(TABLE.ORDER_SN))
				.where(ORDER_INFO.USER_ID.eq(userId))
				.and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.and(ORDER_INFO.ORDER_STATUS.ge(OrderConstant.ORDER_WAIT_DELIVERY))
				.and(ORDER_INFO.CREATE_TIME.gt(timestamp));
		if (!org.apache.commons.lang3.StringUtils.isBlank(keyWord)){
			select.and(GOODS.GOODS_NAME.like(likeValue(keyWord)));
		}
		return select.orderBy(ORDER_GOODS.CREATE_TIME.desc()).limit(pageRows*(currentPages - 1), pageRows).fetch();
	}

	/**
	 *  购买商品记录(三个月内)
	 * @param userId  用户ID
	 * @param keyWord 关键字
	 * @return Record
	 * @author kdc
	 */
	public Integer buyingHistoryGoodsCount(Integer userId, String keyWord){
		Timestamp timestamp = DateUtils.getTimeStampPlus(-3, ChronoUnit.MONTHS);
		SelectConditionStep<Record1<Integer>> select = db().selectCount()
				.from(TABLE)
				.leftJoin(GOODS).on(GOODS.GOODS_ID.eq(TABLE.GOODS_ID))
				.leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(TABLE.ORDER_SN))
				.where(ORDER_INFO.USER_ID.eq(userId))
				.and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.and(ORDER_INFO.ORDER_STATUS.ge(OrderConstant.ORDER_WAIT_DELIVERY))
				.and(ORDER_INFO.CREATE_TIME.gt(timestamp));

		return select.fetchOne().value1();
	}
	public List<Integer> getZhixiaoGoodsIds(){
        Timestamp local = Timestamp.valueOf(LocalDate.now().minusDays(30).atStartOfDay());

        return db().selectDistinct()
            .from(TABLE)
            .where(TABLE.CREATE_TIME.greaterThan(local))
            .fetch()
            .getValues(TABLE.GOODS_ID,Integer.class);
    }

    /**
     * 是否赠品行
     * @param recId
     * @return
     */
    public int isGift(int recId){
	    return db().select(TABLE.IS_GIFT).from(TABLE).where(TABLE.REC_ID.eq(recId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 	通过订单sn[]查询其下商品
     * @param orderSns
     * @return Map<String, List<OrderGoodsMpVo>>
     */
    public Map<String, List<ApiOrderGoodsListVo>> getGoodsByOrderSns(List<String> orderSns) {
        return db().select(TABLE.ORDER_SN, TABLE.REC_ID,TABLE.PRODUCT_ID,TABLE.PRODUCT_SN,TABLE.GOODS_ID,TABLE.GOODS_SN,TABLE.GOODS_NAME,TABLE.GOODS_ATTR,TABLE.GOODS_PRICE, TABLE.DISCOUNTED_GOODS_PRICE,TABLE.GOODS_NUMBER,TABLE.REFUND_STATUS,TABLE.SEND_NUMBER,TABLE.GOODS_SCORE,TABLE.DISCOUNTED_GOODS_PRICE)
            .from(TABLE)
            .where(TABLE.ORDER_SN.in(orderSns))
            .fetchGroups(TABLE.ORDER_SN,ApiOrderGoodsListVo.class);
    }

	/**
	 * 商品购买记录
	 *
	 * @param goodsId 商品id
	 * @return 最进的五条数据
	 */
	public List<GoodsOrderRecordSmallVo> getGoodsRecord(Integer goodsId) {
		SelectHavingStep<Record2<String, BigDecimal>> subQuery1 = db()
				.select(ORDER_INFO.MAIN_ORDER_SN, DSL.sum(TABLE.GOODS_NUMBER).sub(DSL.sum(TABLE.RETURN_NUMBER)).as("remain_num"))
				.from(TABLE)
				.leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(TABLE.ORDER_SN))
				.where(ORDER_INFO.MAIN_ORDER_SN.notEqual(ORDER_INFO.ORDER_SN))
				.and(DSL.length(ORDER_INFO.ORDER_SN).gt(10))
				.and(TABLE.GOODS_ID.eq(goodsId))
				.groupBy(ORDER_INFO.MAIN_ORDER_SN);
		SelectHavingStep<Record2<String, BigDecimal>> subQuery2 = db()
				.select(ORDER_INFO.ORDER_SN, DSL.sum(TABLE.GOODS_NUMBER).sub(DSL.sum(TABLE.RETURN_NUMBER)).as("remain_num"))
				.from(TABLE)
				.leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(TABLE.ORDER_SN))
				.where(TABLE.GOODS_ID.eq(goodsId))
				.and(DSL.length(ORDER_INFO.MAIN_ORDER_SN).lt(10))
				.groupBy(ORDER_INFO.ORDER_SN);
		List<GoodsOrderRecordSmallVo> goodsOrderRecordSmallVos = db().select(USER_DETAIL.USERNAME, USER_DETAIL.USER_AVATAR, ORDER_INFO.CREATE_TIME)
				.from(ORDER_INFO)
				.leftJoin(USER_DETAIL).on(USER_DETAIL.USER_ID.eq(ORDER_INFO.USER_ID))
				.leftJoin(subQuery1).on(subQuery1.field(ORDER_INFO.MAIN_ORDER_SN).eq(ORDER_INFO.ORDER_SN))
				.leftJoin(subQuery2).on(subQuery2.field(ORDER_INFO.ORDER_SN).eq(ORDER_INFO.ORDER_SN))
				.where(ORDER_INFO.ORDER_STATUS.notIn(OrderConstant.ORDER_WAIT_PAY, OrderConstant.ORDER_CANCELLED, OrderConstant.ORDER_CLOSED, OrderConstant.ORDER_REFUND_FINISHED))
				.and(USER_DETAIL.USERNAME.notEqual(""))
				.and(USER_DETAIL.USERNAME.isNotNull())
				.and(subQuery1.field("remain_num", Integer.class).gt(0)
						.or(subQuery2.field("remain_num", Integer.class).gt(0)))
				.orderBy(ORDER_INFO.CREATE_TIME.desc())
				.limit(5).fetchInto(GoodsOrderRecordSmallVo.class);
		return goodsOrderRecordSmallVos;
	}

    /**
     * 用户已对某个限时降价活动下单的商品数量
     * @param userId
     * @param reducePriceId
     * @param prdId
     * @return
     */
	public int getBuyGoodsNumberByReducePriceId(int userId,int reducePriceId,int prdId){
        Timestamp[] periodTime = saas.getShopApp(getShopId()).reducePrice.getCurrentPeriodTime(reducePriceId);
        SelectConditionStep<? extends Record> select = db().select(DSL.sum(TABLE.GOODS_NUMBER)).from(TABLE).leftJoin(ORDER_INFO).on(TABLE.ORDER_ID.eq(ORDER_INFO.ORDER_ID)).
            where(TABLE.ACTIVITY_ID.eq(reducePriceId)).
            and(ORDER_INFO.USER_ID.eq(userId)).
            and(TABLE.PRODUCT_ID.eq(prdId)).
            and(ORDER_INFO.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).
            and(TABLE.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)).
            and(ORDER_INFO.ORDER_STATUS.ge(OrderConstant.ORDER_WAIT_DELIVERY));
	    if(periodTime != null){
            select.and(ORDER_INFO.CREATE_TIME.ge(periodTime[0])).and(ORDER_INFO.CREATE_TIME.le(periodTime[1]));
        }
        return select.fetchAnyInto(int.class);
    }
    /**
     * 拉同步药品出库状态
     * @param
     * @return
     */
    public JsonResult syncMedicalOrderStatus(SyncHisMedicalOrderRequestParam param){
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId =getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_SYNC_MEDICAL_ORDER_STATUS;
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())){
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }
        return JsonResult.success();

    }



    /**
     * 更改处方号
     * @param orderId
     * @param prescriptionCode
     */
    public void updatePrescriptionCode(Integer orderId,String prescriptionCode){
        orderGoodsDao.updatePrescriptionCode(orderId,prescriptionCode);
    }


	public OrderGoodsBo goodsToOrderGoodsBo(OrderBeforeParam.Goods goods) {
		logger().info("initOrderGoods初始化数据开始");
		String goodsImg =null;
		if (StringUtils.isBlank(goods.getProductInfo().getPrdImg()) && StringUtils.isBlank(goods.getGoodsInfo().getGoodsImg())){
			goodsImg =MedicalGoodsConstant.MEDICAL_GOODS_WXAPP_DEFAULT_IMG;
		}else {
			goodsImg = StringUtils.isBlank(goods.getProductInfo().getPrdImg()) ? goods.getGoodsInfo().getGoodsImg() : goods.getProductInfo().getPrdImg();
		}
		OrderGoodsBo bo = OrderGoodsBo.builder().
				goodsId(goods.getGoodsId()).
				goodsName(goods.getGoodsInfo().getGoodsName()).
				goodsSn(goods.getGoodsInfo().getGoodsSn()).
				productId(goods.getProductId()).
				productSn(goods.getProductInfo().getPrdSn()).
				goodsNumber(goods.getGoodsNumber()).
				marketPrice(goods.getProductInfo().getPrdMarketPrice()).
				goodsPrice(goods.getGoodsPrice() == null ? goods.getProductInfo().getPrdPrice() : goods.getGoodsPrice()).
				goodsAttr(goods.getProductInfo().getPrdDesc()).
				//TODO 需要考虑
						goodsAttrId(StringUtils.EMPTY).
						//获取不到商品图片获取默认
						goodsImg(goodsImg).
				//满折满减
						straId(goods.getStraId()).
						perDiscount(goods.getPerDiscount()).
				//当前非赠品（赠品后续初始化）
						isGift(OrderConstant.IS_GIFT_N).
				//TODO 需要考虑 赠品的关联商品
						rGoods(StringUtils.EMPTY).
				//商品积分(积分兑换当前商品需要的积分)
						goodsScore(goods.getGoodsScore()).
				//TODO 需要考虑 商品成长值
						goodsGrowth(0).
						goodsType(goods.getGoodsInfo().getGoodsType()).
						discountedGoodsPrice(goods.getProductPrice()).
						discountedTotalPrice(BigDecimalUtil.multiply(goods.getProductPrice(), new BigDecimal(goods.getGoodsNumber()))).
						costPrice(goods.getProductInfo().getPrdCostPrice()).
				//TODO 逐级计算折扣
						discountDetail(StringUtils.EMPTY).
						deliverTemplateId(goods.getGoodsInfo().getDeliverTemplateId()).
				//商品质量
						goodsWeight(goods.getProductInfo().getPrdWeight()).
				//TODO 后续处理
						userCoupon(null).
						catId(goods.getGoodsInfo().getCatId()).
						sortId(goods.getGoodsInfo().getSortId()).
						brandId(goods.getGoodsInfo().getBrandId()).
						goodsPriceAction(goods.getGoodsPriceAction()).
						reducePriceId(goods.getReducePriceId() == null ? NumberUtils.INTEGER_ZERO : goods.getReducePriceId()).
						purchasePriceId(goods.getPurchasePriceId()).
						purchasePriceRuleId(goods.getPurchasePriceRuleId()).
						firstSpecialId(goods.getFirstSpecialId() == null ? NumberUtils.INTEGER_ZERO : goods.getFirstSpecialId()).
						isCardExclusive(goods.getIsCardExclusive() == null ? OrderConstant.NO : goods.getIsCardExclusive()).
						reducePriceId(goods.getReducePriceId()).
						promoteInfo(null).
				//处方信息
						prescriptionInfo(goods.getPrescriptionInfo()).
						prescriptionOldCode(goods.getPrescriptionOldCode()).
						prescriptionCode(goods.getPrescriptionCode()).
                        prescriptionDetailCode(goods.getPrescriptionDetailCode()).
						medicalAuditStatus(goods.getMedicalAuditStatus()).
						medicalAuditType(goods.getMedicalAuditType()).
                //药品规格
                        goodsQualityRatio(goods.getGoodsQualityRatio()).
                        goodsProductionEnterprise(goods.getGoodsProductionEnterprise()).
                        goodsApprovalNumber(goods.getGoodsApprovalNumber()).
						build();
		if (goods.getMedicalInfo()!=null){
			bo.setIsRx(goods.getMedicalInfo().getIsRx());
		}else {
			bo.setIsRx(BaseConstant.NO);
		}
		//限时降价的ID和TYPE存入order_goods
		if(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(goods.getGoodsPriceAction()) && goods.getReducePriceId() != null){
			bo.setActivityId(goods.getReducePriceId());
			bo.setActivityType(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE);
		}
		logger().info("initOrderGoods初始化数据结束，参数为：{}",bo.toString());
		return bo;
	}
}
