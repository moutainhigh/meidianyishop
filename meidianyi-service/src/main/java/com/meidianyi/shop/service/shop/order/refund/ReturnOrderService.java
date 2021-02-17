package com.meidianyi.shop.service.shop.order.refund;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BigDecimalPlus;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.Operator;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.api.ApiBasePageParam;
import com.meidianyi.shop.common.foundation.util.api.ApiPageResult;
import com.meidianyi.shop.db.shop.tables.ReturnOrder;
import com.meidianyi.shop.db.shop.tables.ReturnOrderGoods;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiReturnOrderListVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnListVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam.ReturnGoods;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundVo.RefundVoGoods;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.order.refund.ReturnOrderListMp;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.ReturnOrder.RETURN_ORDER;
import static com.meidianyi.shop.db.shop.tables.ReturnOrderGoods.RETURN_ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_AUDITING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_AUDIT_NOT_PASS;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_AUDIT_PASS;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_CLOSE;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_FINISH;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_REFUSE;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.STATE_COLLECTION_1;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.STATE_COLLECTION_2;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.STATE_COLLECTION_3;

/**
 * Table:return_order
 * @author 王帅
 *
 */
@Service
public class ReturnOrderService extends ShopBaseService{

	public final ReturnOrder TABLE = RETURN_ORDER;
    public final ReturnOrderGoods SUB_TABLE = RETURN_ORDER_GOODS;

	/**
	 * 	通过订单[]查询其下退货订单信息
	 * @param arrayToSearch
	 * @return Result<?>
	 */
	public Result<ReturnOrderRecord> getRefundByOrderSn(String... arrayToSearch) {
		Result<ReturnOrderRecord> goods = db().selectFrom(TABLE)
				.where(TABLE.ORDER_SN.in(arrayToSearch))
				.orderBy(TABLE.RET_ID.desc())
				.fetch();
		return goods;
	}

	/**
	 * 	通过retid查询退货订单信息
	 * @param retId
	 * @return Result<?>
	 */
	public ReturnOrderRecord getByRetId(Integer retId) {
		 return db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId)).fetchOne();
	}

	/**
	 * 	通过returnordersn查询退货订单信息
	 * @param returnOrderSn
	 * @return Result<?>
	 */
	public ReturnOrderRecord getByReturnOrderSn(String returnOrderSn) {
		 return db().selectFrom(TABLE).where(TABLE.RETURN_ORDER_SN.eq(returnOrderSn)).fetchOne();
	}

	/**
	 * 	综合查询退订单
	 * @param param
	 * @return
	 */
	public PageResult<OrderReturnListVo> getPageList(OrderPageListQueryParam param) {
		SelectJoinStep<Record> select = db().select(TABLE.asterisk(),USER.USERNAME.as(OrderReturnListVo.ORDER_USERNAME),USER.MOBILE.as(OrderReturnListVo.ORDER_MOBILE))
            .from(TABLE)
            .leftJoin(ORDER_INFO).on(TABLE.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .innerJoin(USER).on(TABLE.USER_ID.eq(USER.USER_ID));
        buildOptionsReturn(select, param);
        return getPageResult(select,param.getCurrentPage(),param.getPageRows(),OrderReturnListVo.class);
	}

    public PageResult<ReturnOrderListMp> getPageList(OrderListParam param) {
        SelectJoinStep<Record> select = db().selectDistinct(TABLE.asterisk()).from(TABLE);
        buildOptionsReturn(select, param.getWxUserInfo().getUserId(), param.getSearch());
        return getPageResult(select,param.getCurrentPage(),param.getPageRows(), ReturnOrderListMp.class);
    }

    public SelectJoinStep<?> buildOptionsReturn(SelectJoinStep<?> select, Integer userId, String search) {
        if(userId != null) {
            select.where(TABLE.USER_ID.eq(userId));
        }
        if(!StringUtils.isBlank(search)) {
            select.innerJoin(SUB_TABLE).on(TABLE.RET_ID.eq(SUB_TABLE.RET_ID)).
                where(TABLE.RETURN_ORDER_SN.like(likeValue(search)).
                    or(TABLE.ORDER_SN.like(likeValue(search))).
                    or(SUB_TABLE.GOODS_NAME.like(likeValue(search))));
        }
        select.orderBy(TABLE.RET_ID.desc());
        return select;
    }
	/**
	 * 构造退货、款查询条件
     *
	 * @param select
	 * @param param
	 * @return
	 */
	public SelectWhereStep<?> buildOptionsReturn(SelectJoinStep<?> select, OrderPageListQueryParam param) {
		// 自增id排序
		select.orderBy(TABLE.RET_ID.desc());

		if (!StringUtils.isEmpty(param.getOrderSn())) {
			select.where(TABLE.ORDER_SN.like(likeValue(param.getOrderSn())));
		}
		if (!StringUtils.isEmpty(param.getReturnOrderSn())) {
			select.where(TABLE.RETURN_ORDER_SN.like(likeValue(param.getReturnOrderSn())));
		}
		if (param.getRefundStatus() != null) {
			switch (param.getRefundStatus()) {
			case OrderConstant.SEARCH_RETURNSTATUS_14:
                select.where(TABLE.REFUND_STATUS.eq(REFUND_STATUS_AUDITING).or(TABLE.REFUND_STATUS.eq(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING).and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_ONLY_MONEY))));
				break;
			case OrderConstant.SEARCH_RETURNSTATUS_41:
				select.where(TABLE.REFUND_STATUS.eq(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING).and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_GOODS)));
				break;
			case OrderConstant.SEARCH_RETURNSTATUS_60:
				select.where(TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_REFUSE).and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_ONLY_MONEY)));
				break;
			case OrderConstant.SEARCH_RETURNSTATUS_61:
				select.where(TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_REFUSE).and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_GOODS)));
				break;
			default:
				select.where(TABLE.REFUND_STATUS.eq(param.getRefundStatus()));
			}
		}
		if (param.getReturnType() != null && param.getReturnType().length != 0) {
			select.where(TABLE.RETURN_TYPE.in(param.getReturnType()));
		}
		if (param.getReturnStart() != null) {
			select.where(TABLE.CREATE_TIME.ge(param.getReturnStart()));
		}
		if (param.getReturnEnd() != null) {
            select.where(TABLE.CREATE_TIME.le(param.getReturnEnd()));
        }
        if (param.getRetIds() != null && param.getRetIds().length != 0) {
            select.where(TABLE.RET_ID.in(param.getRetIds()));
        }
        if(OrderConstant.SHOP_HELPER_OVERDUE_RETURN_APPLY.equals(param.getShopHelperAction())) {
            select.where(TABLE.REFUND_STATUS.in(REFUND_STATUS_AUDITING, OrderConstant.REFUND_STATUS_AUDIT_PASS, REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)
                .and(TABLE.CREATE_TIME.add(param.getShopHelperActionDays()).lessThan(Timestamp.valueOf(LocalDateTime.now()))));
        }
        if (param.getReturnSource() != null && param.getReturnSource().length != 0) {
            select.where(TABLE.RETURN_SOURCE.in(param.getReturnSource()));
        }
        if (!Strings.isNullOrEmpty(param.getOrderUserNameOrMobile())){
            select.where(USER.USERNAME.like(likeValue(param.getOrderUserNameOrMobile())).or(USER.MOBILE.like(likeValue(param.getOrderUserNameOrMobile()))));
        }
        if (param.getStoreIds() != null) {
            select.where(ORDER_INFO.STORE_ID.in(param.getStoreIds()));
        }
        if (param.getStateCollection() == null) {
            return select;
        }
        // 添加前端搜索退款订单的stateCollection与sql对应的关系
        if (param.getStateCollection().equals(STATE_COLLECTION_1)) {
            select.where(TABLE.REFUND_STATUS.in(REFUND_STATUS_AUDITING, REFUND_STATUS_APPLY_REFUND_OR_SHIPPING));
        }
        if (param.getStateCollection().equals(STATE_COLLECTION_2)) {
            select.where(TABLE.REFUND_STATUS.eq(REFUND_STATUS_AUDIT_PASS));
        }
        if (param.getStateCollection().equals(STATE_COLLECTION_3)) {
            select.where(TABLE.REFUND_STATUS.in(REFUND_STATUS_AUDIT_NOT_PASS, REFUND_STATUS_FINISH, REFUND_STATUS_REFUSE, REFUND_STATUS_CLOSE));
        }
		return select;
	}

	/**
	 * 	获取该订单的退成功运费记录（送礼订单无运费）
	 * @param orderSn 订单号
	 * @return 存在退运费成功则返回金额，否则为0
	 */
	public BigDecimal getReturnShippingFee(String orderSn) {
		Record1<BigDecimal> fetchOne = db().select(DSL.sum(TABLE.SHIPPING_FEE)).from(TABLE).where(TABLE.ORDER_SN.eq(orderSn).and(TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH))).fetchOne();
		return fetchOne.value1() == null ? BigDecimal.ZERO : fetchOne.value1();
	}

	/**
	 * 	获取该订单的退成功商品金额
	 * @param orderSn 订单号
	 * @return 存在则返回金额，否则为0
	 */
	public BigDecimal getReturnMoney(String orderSn) {
		Record1<BigDecimal> fetchOne = db().select(DSL.sum(TABLE.MONEY)).from(TABLE).where(TABLE.ORDER_SN.eq(orderSn).and(TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH))).fetchOne();
		return fetchOne.value1() == null ? BigDecimal.ZERO : fetchOne.value1();
	}
	/**
	 * 	增加退货/退款申请记录->形成退款/退货订单
	 * 	status：退货 1；else 4
	 * @param param
	 * @param order
	 * @param defaultMoney 申请时计算默认金额,为null取param里的金额
	 * @return returnOrder
	 */
	public ReturnOrderRecord addRecord(RefundParam param , OrderInfoVo order , BigDecimal defaultMoney) {
		ReturnOrderRecord returnOrder = db().newRecord(TABLE);
		if(defaultMoney == null) {
			returnOrder.setMoney(param.getReturnMoney() == null ? BigDecimal.ZERO : param.getReturnMoney());
		}else {
			returnOrder.setMoney(defaultMoney);
		}
		returnOrder.setOrderId(order.getOrderId());
		returnOrder.setOrderSn(order.getOrderSn());
		returnOrder.setReturnOrderSn(IncrSequenceUtil.generateOrderSn(OrderConstant.RETURN_SN_PREFIX));
		returnOrder.setReturnType(param.getReturnType());
		returnOrder.setReasonType(param.getReasonType() == null ? 0 :param.getReasonType());
		returnOrder.setReasonDesc(param.getReasonDesc());
		returnOrder.setGoodsImages(param.getGoodsImages());
		returnOrder.setVoucherImages(param.getVoucherImages());
		returnOrder.setUserId(order.getUserId());
		returnOrder.setShopId(getShopId());
		returnOrder.setCurrency(order.getCurrency());
        returnOrder.setIsAutoReturn(param.getIsAutoReturn());
		//除退货外,refund_status为4
        returnOrder.setRefundStatus(param.getReturnType() == OrderConstant.RT_GOODS ? REFUND_STATUS_AUDITING : REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
		if(param.getReturnType() == OrderConstant.RT_GOODS) {
			//退货->申请时间
			returnOrder.setApplyTime(DateUtils.getSqlTimestamp());
		}else {
			//非退货->申请时间
			returnOrder.setShippingOrRefundTime(DateUtils.getSqlTimestamp());
		}
		//售后方式
        if(Byte.valueOf(OrderConstant.IS_MP_Y).equals(param.getIsMp())) {
            returnOrder.setReturnSource(OrderConstant.RS_MP);
        }else if(Byte.valueOf(OrderConstant.IS_MP_ADMIN).equals(param.getIsMp())) {
            returnOrder.setReturnSource(OrderConstant.RS_ADMIN);
        }else if(Byte.valueOf(OrderConstant.IS_MP_DOCTOR).equals(param.getIsMp())) {
            returnOrder.setReturnSource(OrderConstant.RS_DOCTOR_AUDIT);
        }else if(Byte.valueOf(OrderConstant.IS_MP_STORE_CLERK).equals(param.getIsMp())) {
            returnOrder.setReturnSource(OrderConstant.RS_STORE);
        }else if(Byte.valueOf(OrderConstant.IS_MP_POS).equals(param.getIsMp())) {
            returnOrder.setReturnSource(OrderConstant.RS_POS);
        }else if(Byte.valueOf(OrderConstant.IS_MP_ERP_OR_EKB).equals(param.getIsMp())) {
            returnOrder.setReturnSource(OrderConstant.RS_ERP);
        }else if(Byte.valueOf(OrderConstant.IS_MP_AUTO).equals(param.getIsMp()) && param.getReturnSourceType() != null) {
            returnOrder.setReturnSource(OrderConstant.RS_AUTO);
            returnOrder.setReturnSourceType(param.getReturnSourceType());
        }
		//TODO 同步退款单到CRM
		returnOrder.insert();
		logger().info("新增退款/退货订单:"+returnOrder.toString());
		return returnOrder;
	}

	public void finishReturn(ReturnOrderRecord returnOrder) throws MpException {
		if(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING != returnOrder.getRefundStatus()) {
			throw new MpException(JsonResultCode.CODE_ORDER_FINISH_RETURN_STATUS_ERROR);
		}
		returnOrder.setRefundSuccessTime(DateUtils.getSqlTimestamp());
		returnOrder.setRefundStatus(OrderConstant.REFUND_STATUS_FINISH);
		//TODO 如果存在拆单需要修改一些状态
		returnOrder.update();
	}
	/**
	 * 非仅退运费校验及生成退款订单
	 * @param param
	 * @param order
	 * @param check
	 * @throws MpException
	 */
	public ReturnOrderRecord checkAndCreateOrder(RefundParam param , OrderInfoVo order , RefundVo check) throws MpException{
		//获取调用退款退货查询接口里的可退商品信息
		List<RefundVoGoods> canRefundGoods = check.getRefundGoods();
		//可退商品信息转map方便获取
		Map<Integer, RefundVoGoods> cbsMap = canRefundGoods.stream().collect(Collectors.toMap(RefundVoGoods::getRecId, Function.identity()));
		//当前退款金额默认值计算（不计运费，为了计算一个最大值后续比较后可以直接退款）
		BigDecimal currentMaxReturnMoney = BigDecimal.ZERO;
		//校验
		for (ReturnGoods paramGoods : param.getReturnGoods()) {
			RefundVoGoods checkGoods = cbsMap.get(paramGoods.getRecId());
			if(checkGoods == null) {
				logger().error(
						"退款时订单sn：{},退款类型为：{},商品id：{}商品已退完或者不存在",order.getOrderSn(),param.getReturnType(),paramGoods.getRecId());
				//商品已退完
				throw new MpException(JsonResultCode.CODE_ORDER_RETURN_GOODS_RETURN_COMPLETED);
			}
			//买家校验商家是否配置可退
			if(param.getIsMp() == OrderConstant.IS_MP_Y) {
				if(checkGoods.getIsCanReturn() == OrderConstant.IS_CAN_RETURN_N) {
					logger().error("退款时订单sn:{},退款类型为:{},商品id:{}，商品配置不可退",order.getOrderSn(),param.getReturnType(),paramGoods.getRecId());
					//商品配置不可退
					throw new MpException(JsonResultCode.CODE_ORDER_RETURN_GOODS_NO_CAN_RETURN);
				}
			}
			//if退款类型为手动退款 else退款类型为退款、退货
			if(param.getReturnType() == OrderConstant.RT_MANUAL) {
				//退款类型为手动退款设置退款数量为0
				paramGoods.setReturnNumber(0);
				//当前商品退款最大金额=已退*折后单价
				BigDecimal maxMoney = BigDecimalUtil.multiplyOrDivide(
						BigDecimalPlus.create(new BigDecimal(checkGoods.getReturnNumber()), Operator.multiply),
						BigDecimalPlus.create(checkGoods.getDiscountedGoodsPrice(), null));
				//当前可退=当前商品退款最大金额maxMoney-已退
				BigDecimal canReturnMoney = maxMoney.subtract(checkGoods.getReturnMoney());
				//申请退款金额<=0 || canReturnMoney < 申请退款金额
				if(BigDecimalUtil.compareTo(paramGoods.getMoney() , null) < 1 || BigDecimalUtil.compareTo(canReturnMoney , paramGoods.getMoney()) < 0) {
					logger().error("退款时订单sn:{},退款类型为:{},商品id:{}，申请退款金额不满足条件",order.getOrderSn(),param.getReturnType(),paramGoods.getRecId());
					throw new MpException(JsonResultCode.CODE_ORDER_RETURN_MANUAL_MONEY_ERROR);
				}

				/**
				 * 此次退款金额默认值计算（不计运费）
				 * 规则：已退完：总价-退完成
				 * 	         没退完：单价*已退完数量-退完成
				 */
				if(checkGoods.getReturnable() > 0) {
					//没退完
					currentMaxReturnMoney = BigDecimalUtil.add(currentMaxReturnMoney,
							BigDecimalUtil.subtrac(
									BigDecimalUtil.multiplyOrDivide(
											BigDecimalPlus.create(new BigDecimal(checkGoods.getReturnNumber()), Operator.multiply),
											BigDecimalPlus.create(checkGoods.getDiscountedGoodsPrice(), null)
											),
									checkGoods.getReturnMoney()
									)
							);
				}else {
					//已退完
					currentMaxReturnMoney = BigDecimalUtil.add(currentMaxReturnMoney,
							BigDecimalUtil.subtrac(checkGoods.getDiscountedTotalPrice(),checkGoods.getReturnMoney()));
				}
			}else {
                //申请退货商品数量>可退商品数量 || 退货数量<1 时抛异常
				if(paramGoods.getReturnNumber() < 1 || paramGoods.getReturnNumber() > checkGoods.getReturnable()) {
					//商品退货数量大于可退数量
					throw new MpException(JsonResultCode.CODE_ORDER_RETURN_GOODS_RETURN_NUMBER_ERROR);
				}
				/**
				 * 此次退款金额默认值计算（不计运费）
				 * 规则：最后一次：总价-单价*已退数量(包含退款中)
				 * 	         非最后一次：单价*要退数量
				 */
				if(checkGoods.getReturnable() > paramGoods.getReturnNumber()) {
					//非最后一次
					currentMaxReturnMoney = BigDecimalUtil.add(currentMaxReturnMoney,
							BigDecimalUtil.multiplyOrDivide(
									BigDecimalPlus.create(new BigDecimal(paramGoods.getReturnNumber()), Operator.multiply),
									BigDecimalPlus.create(checkGoods.getDiscountedGoodsPrice(), null)
									)
							);
				}else {
					//最后一次
					currentMaxReturnMoney = BigDecimalUtil.add(currentMaxReturnMoney,
							BigDecimalUtil.subtrac(checkGoods.getDiscountedTotalPrice(),
									BigDecimalUtil.multiplyOrDivide(
											BigDecimalPlus.create(new BigDecimal(checkGoods.getSubmitted()), Operator.multiply),
											BigDecimalPlus.create(checkGoods.getDiscountedGoodsPrice(), null)
											)
									)
							);
				}
			}
		}
		//当前退款金额默认值计算（不计运费）
		return addRecord(param ,order ,currentMaxReturnMoney);
	}

    /**
	 * 获取退款订单特定状态
	 * @param orderIds
	 * @param status
	 * @return
	 */
	public Map<Integer, Integer> getOrderCount(Integer[] orderIds , Byte... status) {
		return db().select(DSL.count(TABLE.ORDER_ID).as("count") , TABLE.ORDER_ID).
		from(TABLE).
		where(TABLE.ORDER_ID.in(orderIds).and(TABLE.REFUND_STATUS.in(status))).
		groupBy(TABLE.ORDER_ID).
		fetch().intoMap(TABLE.ORDER_ID , DSL.count(TABLE.ORDER_ID).as("count"));
	}

    /**
     * 相应订单操作
     * @param param
     * @param returnOrder
     * @throws MpException
     */
    public void responseReturnOperate(RefundParam param , ReturnOrderRecord returnOrder) throws MpException {
        logger().info("相应订单操作start");
		if(param.getIsMp().equals(OrderConstant.IS_MP_Y)) {
			/**mp端操作*/
			switch (param.getReturnOperate()) {
			case OrderConstant.RETURN_OPERATE_MP_SUBMIT_SHIPPING:
				//买家提交物流信息
				submitShipping(returnOrder, param);
				break;
			case OrderConstant.RETURN_OPERATE_MP_REVOKE:
				//买家撤销退款退货
				revokeReturnOrder(returnOrder, param);
				break;
			default:
				break;
			}
		}else if(param.getIsMp().equals(OrderConstant.IS_MP_ADMIN)) {
			/**商家操作*/
			switch (param.getReturnOperate()) {
			case OrderConstant.RETURN_OPERATE_ADMIN_REFUSE:
				//商家拒绝退款退货
				refuseReturn(returnOrder, param);
				break;
			case OrderConstant.RETURN_OPERATE_ADMIN_AGREE_RETURN:
				//商家同意退货申请
				agreeReturn(returnOrder, param);
				break;
			case OrderConstant.RETURN_OPERATE_ADMIN_REFUSE_RETURN_GOODS_APPLY:
				//商家拒绝退货申请
				refuseReturnGoodsApply(returnOrder, param);
				break;
			default:
				break;
			}
		}else if(param.getIsMp().equals(OrderConstant.IS_MP_AUTO)) {
            /**自动任务*/
            switch (param.getReturnOperate()) {
                case OrderConstant.RETURN_OPERATE_ADMIN_AGREE_RETURN:
                    //同意退货申请
                    agreeReturn(returnOrder, param);
                    break;
                case OrderConstant.RETURN_OPERATE_MP_REVOKE:
                    //撤销退款退货
                    revokeReturnOrder(returnOrder, param);
                    break;
                default:
                    break;
            }
        }

        logger().info("相应订单操作end");
	}

    /**
	 * 退货提交物流
	 * @param returnOrder
	 * @param param
     * @throws MpException
	 */
	public void submitShipping(ReturnOrderRecord returnOrder , RefundParam param) throws MpException {
        logger().info("退货提交物流start");
		//校验
        if (returnOrder.getRefundStatus() != OrderConstant.REFUND_STATUS_AUDIT_PASS) {
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_OPERATION_NOT_SUPPORTED_BECAUSE_STATUS_ERROR);
		}
		returnOrder.setShippingType(param.getShippingType());
		returnOrder.setShippingNo(param.getShippingNo());
		returnOrder.setPhone(param.getPhone());
		returnOrder.setVoucherImages(param.getVoucherImages());
		returnOrder.setShippingOrRefundTime(DateUtils.getSqlTimestamp());
		returnOrder.setRefundStatus(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
		returnOrder.update();
        logger().info("退货提交物流end");
	}

    /**
	 * 撤销退款退货
	 * @param returnOrder
	 * @param param
     * @throws MpException
	 */
	public void revokeReturnOrder(ReturnOrderRecord returnOrder , RefundParam param) throws MpException {
        logger().info("撤销退款退货start");
		//校验
		if(returnOrder.getRefundStatus() == OrderConstant.REFUND_STATUS_FINISH) {
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_OPERATION_NOT_SUPPORTED_BECAUSE_STATUS_ERROR);
		}
		returnOrder.setRefundCancelTime(DateUtils.getSqlTimestamp());
		returnOrder.setRefundStatus(OrderConstant.REFUND_STATUS_CLOSE);
		returnOrder.update();
		//TODO php系统撤销未实现
        logger().info("撤销退款退货end");
    }
	/**
	 * 拒绝退款/退货
	 * @param returnOrder
	 * @param param
     * @throws MpException
	 */
	public void refuseReturn(ReturnOrderRecord returnOrder , RefundParam param) throws MpException {
        logger().info("拒绝退款/退货start");
		//校验
		if((returnOrder.getRefundStatus() != OrderConstant.REFUND_STATUS_AUDIT_PASS && returnOrder.getRefundStatus() != REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)) {
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_OPERATION_NOT_SUPPORTED_BECAUSE_STATUS_ERROR);
		}
		returnOrder.setRefundRefuseTime(DateUtils.getSqlTimestamp());
		returnOrder.setRefundStatus(OrderConstant.REFUND_STATUS_REFUSE);
		returnOrder.setRefundRefuseReason(param.getRefundRefuseReason());
		returnOrder.update();
        logger().info("拒绝退款/退货end");
	}

    /**
	 * 审核通过（退货申请）
	 * @param returnOrder
	 * @param param
     * @throws MpException
	 */
	public void agreeReturn(ReturnOrderRecord returnOrder , RefundParam param) throws MpException {
        logger().info("审核通过（退货申请）start");
		//校验
        if (returnOrder.getRefundStatus() != REFUND_STATUS_AUDITING) {
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_OPERATION_NOT_SUPPORTED_BECAUSE_STATUS_ERROR);
		}
		returnOrder.setApplyPassTime(DateUtils.getSqlTimestamp());
        returnOrder.setRefundStatus(OrderConstant.REFUND_STATUS_AUDIT_PASS);
		returnOrder.setConsignee(param.getConsignee());
		returnOrder.setReturnAddress(param.getReturnAddress());
		returnOrder.setMerchantTelephone(param.getMerchantTelephone());
		returnOrder.setZipCode(param.getZipCode());
		returnOrder.update();
        logger().info("审核通过（退货申请）end");
	}

    /**
	 * 商家拒绝退货申请
	 * @param returnOrder
	 * @param param
     * @throws MpException
	 */
	public void refuseReturnGoodsApply(ReturnOrderRecord returnOrder , RefundParam param) throws MpException {
        logger().info("商家拒绝退货申请start");
		//校验
        if (returnOrder.getRefundStatus() != REFUND_STATUS_AUDITING) {
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_OPERATION_NOT_SUPPORTED_BECAUSE_STATUS_ERROR);
		}
		returnOrder.setApplyNotPassTime(DateUtils.getSqlTimestamp());
		returnOrder.setRefundStatus(OrderConstant.REFUND_STATUS_AUDIT_NOT_PASS);
		returnOrder.setApplyNotPassReason(param.getApplyNotPassReason());
		returnOrder.update();
        logger().info("商家拒绝退货申请end");
	}

    /**
     * Refund overdue integer.退款申请逾期
     *
     * @param nDays the n days
     * @return the integer
     */
    public Integer refundOverdue(Integer nDays) {
        return db().fetchCount(TABLE, TABLE.REFUND_STATUS.in(REFUND_STATUS_AUDITING, OrderConstant.REFUND_STATUS_AUDIT_PASS, REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)
            .and(TABLE.CREATE_TIME.add(nDays).lessThan(Timestamp.valueOf(LocalDateTime.now()))));
    }

    /**
     * Refund overdue integer.退款申请逾期订单id列表
     *
     * @param nDays the n days
     * @return the integer
     */
    public Set<Integer> refundOverdueSet(Integer nDays) {
        Condition condition = TABLE.REFUND_STATUS.in(REFUND_STATUS_AUDITING, OrderConstant.REFUND_STATUS_AUDIT_PASS, REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)
            .and(TABLE.CREATE_TIME.add(nDays).lessThan(Timestamp.valueOf(LocalDateTime.now())));
        return db().select(TABLE.RET_ID).from(TABLE).where(condition).fetchSet(TABLE.RET_ID);
    }

    /**
     * 获取自动退款的退款订单
     * @param returnMoneyDays
     * @param returnAddressDays
     * @param returnShoppingDays
     * @param returnPassDays
     * @return Result<ReturnOrderRecord>
     */
    public Result<ReturnOrderRecord> getAutoReturnOrder(Byte returnMoneyDays, Byte returnAddressDays, Byte returnShoppingDays, Byte returnPassDays){
        Instant now = Instant.now();
        Timestamp returnMoneTime = Timestamp.from(now.plusSeconds(-returnMoneyDays * 24 * 60 * 60));
        Timestamp returnAddressTime = Timestamp.from(now.plusSeconds(-returnAddressDays * 24 * 60 * 60));
        Timestamp returnShoppingTime  = Timestamp.from(now.plusSeconds(-returnShoppingDays * 24 * 60 * 60));
        Timestamp returnPassTime  = Timestamp.from(now.plusSeconds(-returnPassDays * 24 * 60 * 60));
        return db().selectFrom(TABLE).where(
            //买家发起退款申请后，商家在 returnMoneyDays 日内未处理，系统将自动退款
            TABLE.REFUND_STATUS.eq(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)
                .and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_ONLY_MONEY))
                .and(TABLE.IS_AUTO_RETURN.eq(OrderConstant.YES)
                .and(TABLE.SHIPPING_OR_REFUND_TIME.le(returnMoneTime)))
        ).or(
            //商家已发货，买家发起退款退货申请，商家在 ? 日内未处理，系统将默认同意退款退货，并自动向买家发送商家的默认收获地址
            TABLE.REFUND_STATUS.eq(REFUND_STATUS_AUDITING)
                .and(TABLE.RETURN_TYPE.in(OrderConstant.RT_GOODS, OrderConstant.RT_CHANGE))
                .and(TABLE.IS_AUTO_RETURN.eq(OrderConstant.YES))
                .and(TABLE.APPLY_TIME.le(returnAddressTime))
        ).or(
            //买家已提交物流信息，商家在 ? 日内未处理，系统将默认同意退款退货，并自动退款给买家
            TABLE.REFUND_STATUS.eq(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)
                .and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_GOODS))
                .and(TABLE.IS_AUTO_RETURN.eq(OrderConstant.YES))
                .and(TABLE.SHIPPING_OR_REFUND_TIME.le(returnShoppingTime))
        ).or(
            //商家同意退款退货，买家在 ? 日内未提交物流信息，且商家未确认收货并退款，退款申请将自动撤销。
            TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_AUDIT_PASS)
                .and(TABLE.RETURN_TYPE.eq(OrderConstant.RT_GOODS))
                .and(TABLE.IS_AUTO_RETURN.eq(OrderConstant.YES))
                .and(TABLE.APPLY_PASS_TIME.le(returnPassTime))
        ).fetch();
    }

    /**
     * 得到退货/退款总金额（包含退款处理中和退款成功，不包含失败情况）
     * @param orderSn
     * @return
     */
    public BigDecimal getReturnOrderMoney(String orderSn) {
		Record1<BigDecimal> fetchOne = db().select(DSL.sum(TABLE.MONEY)).from(TABLE)
				.where(TABLE.ORDER_SN.eq(orderSn).and(TABLE.REFUND_STATUS.in(REFUND_STATUS_AUDITING,
						OrderConstant.REFUND_STATUS_AUDIT_PASS, REFUND_STATUS_APPLY_REFUND_OR_SHIPPING,
						OrderConstant.REFUND_STATUS_FINISH)))
				.fetchOne();
    	return fetchOne.value1() == null ? BigDecimal.ZERO : fetchOne.value1();
    }

    /**
     * 条件查询退款订单数量
     * @param userId
     * @param search
     * @return
     */
    public int getReturnOrderCount(Integer userId, String search) {
        SelectJoinStep<Record1<Integer>> select = db().select(DSL.countDistinct(TABLE.RETURN_ORDER_SN)).from(TABLE);
        if(userId != null) {
            select.where(TABLE.USER_ID.eq(userId));
        }
        if(!StringUtils.isBlank(search)) {
            select.innerJoin(SUB_TABLE).on(TABLE.RET_ID.eq(SUB_TABLE.RET_ID)).
                where(TABLE.RETURN_ORDER_SN.like(search).
                    or(TABLE.ORDER_SN.like(search)).
                    or(SUB_TABLE.GOODS_NAME.like(search)));
        }
        return select.fetchOne(0, int.class);
    }

    /**
     * 获取用户退款订单统计
     * return <累计退款金额,累计退款订单数>
     */
    public Tuple2<BigDecimal, Integer> getUserReturnOrderStatistics(Integer userId) {
        int count = db().select(DSL.countDistinct(TABLE.ORDER_SN))
            .from(TABLE)
            .where(TABLE.USER_ID.eq(userId))
            .and(TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH))
            .fetchOptionalInto(Integer.class)
            .orElse(0);

        if (count > 0) {
            Record2<BigDecimal, BigDecimal> resOne = db().select(DSL.sum(TABLE.MONEY), DSL.sum(TABLE.SHIPPING_FEE))
                .from(TABLE)
                .where(TABLE.USER_ID.eq(userId))
                .and(TABLE.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH))
                .fetchAny();
            BigDecimal orderAmount = BigDecimalUtil.add((BigDecimal) resOne.get(0), (BigDecimal) resOne.get(1));
            return new Tuple2<>(orderAmount, count);
        }
        return new Tuple2<>(BigDecimal.ZERO, count);
    }

    public Map<String, List<ApiReturnOrderListVo>> getOrderByOrderSns(List<String> sns) {
        if(CollectionUtils.isEmpty(sns)) {
            return new HashMap<>(0);
        }
        return db().selectFrom(TABLE).where(TABLE.ORDER_SN.in(sns)).fetchGroups(TABLE.ORDER_SN, ApiReturnOrderListVo.class);
    }

    public <T> ApiPageResult<T> getPageList(ApiBasePageParam param, Class<T> clazz) {
        SelectWhereStep<ReturnOrderRecord> select = db().selectFrom(TABLE);
        buildOptions(select, param);
        return getApiPageResult(select, param.getPage(), param.getPageSize(), clazz);
    }

    private void buildOptions(SelectWhereStep<ReturnOrderRecord> select, ApiBasePageParam param) {
        //只能查询最近30天内的记录
        select.where(TABLE.CREATE_TIME.ge(DateUtils.getBefore30Day()));
        if (param.getStartTime() != null) {
            select.where(TABLE.CREATE_TIME.ge(param.getStartTime()));
        }
        if (param.getEndTime() != null) {
            select.where(TABLE.CREATE_TIME.le(param.getEndTime()));
        }
    }
}


