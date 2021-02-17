package com.meidianyi.shop.service.shop.order.action;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderInfoMpVo;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 	提醒发货
 * @author 王帅
 *
 */

@Component
public class RemindService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam, OrderOperateQueryParam>{

	@Autowired
	private OrderInfoService orderInfo;

	@Autowired
	public RecordAdminActionService record;

	@Override
	public OrderServiceCode getServiceCode() {
		return OrderServiceCode.REMIND;
	}

	@Override
	public Object query(OrderOperateQueryParam param) throws MpException {
		return null;
	}

	@Override
	public ExecuteResult execute(OrderOperateQueryParam param) {
		OrderInfoMpVo order = orderInfo.getByOrderId(param.getOrderId(), OrderInfoMpVo.class);
		if(order == null) {
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, null);
		}
		if(!OrderOperationJudgment.isShowRemindShip(order)) {
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_REMIND_OPERATION_NOT_SUPPORTED, null);
		}
        int maxRemindNumber = 3;
        if(order.getOrderRemind() == maxRemindNumber) {
			//限制三次
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_REMIND_OPERATION_LIMIT, null);
		}
		if(order.getOrderRemindTime() != null && DateUtils.timestampIsNowDay(order.getOrderRemindTime())) {
			//限制一天一次
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_REMIND_OPERATION_LIMIT_TODAY, null);
		}
		//提醒
		orderInfo.remind(order);
		//TODO 发送通知
		//操作记录
		record.insertRecord(Arrays.asList(RecordContentTemplate.ORDER_REMIND.code), param.getOrderSn());
		return null;
	}

}
