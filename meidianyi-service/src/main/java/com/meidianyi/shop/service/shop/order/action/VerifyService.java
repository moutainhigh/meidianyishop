package com.meidianyi.shop.service.shop.order.action;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PartOrderGoodsShipRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.ShipParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.verify.VerifyParam;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.record.OrderActionService;
import com.meidianyi.shop.service.shop.order.ship.ShipInfoService;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 王帅
 */
@Component
public class VerifyService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam, VerifyParam> {

	@Autowired
	private OrderInfoService orderInfo;

	@Autowired
	public OrderGoodsService orderGoods;

	@Autowired
	private ShipInfoService shipInfo;

	@Autowired
	OrderActionService orderAction;

	@Autowired
	public RecordAdminActionService record;

	@Autowired
    private OrderOperateSendMessage sendMessage;
	@Autowired
	private ShipService shipService;

	@Override
	public OrderServiceCode getServiceCode() {
		return OrderServiceCode.VERIFY;
	}

	@Override
	public Object query(OrderOperateQueryParam param) throws MpException {
		return null;
	}

	@Override
	public ExecuteResult execute(VerifyParam param) {

		OrderInfoRecord order = orderInfo.getRecord(param.getOrderId());

		if (!OrderOperationJudgment.isVerify(order.into(OrderInfoVo.class))) {
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_VERIFY_OPERATION_NOT_SUPPORTED, "该订单不能核销", null);
		}

		if(!order.getVerifyCode().equals(param.getVerifyCode()) && param.getIsCheck()) {
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_VERIFY_CODE_ERROR, null);
		}
		//发货批次号,同一批次为同一快递
		String batchNo = order.getOrderSn() + "_" + DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		//order goods
		Result<OrderGoodsRecord> goods = orderGoods.getByOrderId(order.getOrderId());

		ShipParam  shipParam =new ShipParam();
		shipParam.setPlatform(param.getPlatform());
		shipService.handleShipAccountId(shipParam);
		//构造_添加部分发货信息 b2c_part_order_goods_ship
		List<PartOrderGoodsShipRecord> shipInfoList = new ArrayList<PartOrderGoodsShipRecord>(goods.size());
		for (OrderGoodsRecord temp : goods) {
			temp.setSendNumber(temp.getGoodsNumber());
			shipInfoList.add(shipInfo.addRecord(temp, order, batchNo, shipParam, temp.getGoodsNumber()));
		}
		transaction(()->{
			orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_RECEIVED);
			//添加（部分）发货信息 b2c_part_order_goods_ship
			db().batchInsert(shipInfoList).execute();
			//更新发货数量 b2c_order_goods0
			db().batchUpdate(goods).execute();
		});
		//action操作
		orderAction.addRecord(order, param, OrderConstant.ORDER_WAIT_DELIVERY, param.getIsCheck() ? "核销" : "强制核销");
		record.insertRecord(Arrays.asList(new Integer[] { RecordContentTemplate.ORDER_VERIFY.code }), param.getOrderSn());
		sendMessage.sendSelfPickupSuccess(orderInfo.getRecord(param.getOrderId()));
		return null;
	}

}
