package com.meidianyi.shop.service.shop.order.action;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.extend.ExtendReceiveParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderInfoMpVo;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * 	延长收货时间
 * @author 王帅
 *
 */

@Component
public class ExtendReceiveService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam, OrderOperateQueryParam>{
	
	@Autowired
	private OrderInfoService orderInfo;
	
	@Autowired
	public RecordAdminActionService record;
	
	@Autowired
	private OrderReadService orderRead;
	
	/**仅仅商家延长收货*/
	public static byte OPERATION_ONLY_ADMIN = 1;
	/**买家操作后商家操作*/
	public static byte OPERATION_ALL = 2;
	/**买家操作*/
	public static byte OPERATION_MP = 3;
	
	@Override
	public OrderServiceCode getServiceCode() {
		return OrderServiceCode.EXTEND_RECEIVE;
	}

	@Override
	public Object query(OrderOperateQueryParam param) throws MpException {
		return null;
	}
	
	@Override
	public ExecuteResult execute(OrderOperateQueryParam param) {
		logger().info("订单延长收货时间开始,参数为:{}",param.toString());
		//TODO 延长收货
		//后台输入延长时间
		Timestamp extendTime = null;
		if(param.getIsMp() == OrderConstant.IS_MP_ADMIN) {
			extendTime = ((ExtendReceiveParam)param).getExtendTime();
		}
		OrderInfoMpVo order = orderInfo.getByOrderId(param.getOrderId(), OrderInfoMpVo.class);
		if(order == null) {
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, null);
		}
		if(order.getOrderStatus() == OrderConstant.ORDER_SHIPPED) {
			//已发货订单才可以延迟收货时间
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_EXTEND_RECEIVE_NO_SHIPPED, null);
		}
		//店铺配置延长收货天数
		int extendReceiveDays = orderRead.getExtendReceiveDays();
		if(extendReceiveDays == 0) {
			//不可以延长收货(商家配置时间为0)
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_EXTEND_RECEIVE_NOT_SUPPORTED, null);
		}
		if(order.getExtendReceiveAction() > OPERATION_ONLY_ADMIN && param.getIsMp() == OrderConstant.IS_MP_Y) {
			//买家仅一次机会
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_EXTEND_RECEIVE_ONLY_ONE, null);
		}
		//自动收货时间
		Instant autoReceive = order.getShippingTime().toInstant().plusSeconds(Duration.ofDays(order.getReturnDaysCfg()).getSeconds());
		if(param.getIsMp() == OrderConstant.IS_MP_ADMIN) {
			if(extendTime.getTime() < autoReceive.toEpochMilli()) {}
			//延长收货时间不能小于自动收货时间
			return ExecuteResult.create(JsonResultCode.CODE_ORDER_EXTEND_RECEIVE_TIME_NOT_LT_AUTOTIME, null);
		}
		//mp申请时间
		long mpApplyTime = 0;
		if(param.getIsMp() == OrderConstant.IS_MP_Y) {
			if(!OrderOperationJudgment.isExtendReceive(order, extendReceiveDays)) {
				//据自动确认收货时间大于2天，不可延长收货
				return ExecuteResult.create(JsonResultCode.CODE_ORDER_EXTEND_RECEIVE_NOW_AUTOTIME_INTERVAL_GT_TWO_DAYS, null);
			}
			mpApplyTime = autoReceive.plusSeconds(Duration.ofDays(extendReceiveDays).getSeconds()).toEpochMilli();
			if(order.getExtendReceiveAction() > 0 && order.getExtendReceiveTime().getTime() > mpApplyTime) {
				//商家已操作更长的收货时间，请勿再操作
				return ExecuteResult.create(JsonResultCode.CODE_ORDER_EXTEND_RECEIVE_ADMIN_SET_MORE_TIME, null);
			}
		}
		//最终延长时间
		extendTime = param.getIsMp() == OrderConstant.IS_MP_Y ? new Timestamp(mpApplyTime) : extendTime;
		
		if(param.getIsMp() == OrderConstant.IS_MP_ADMIN) {
			//商家操作
			if(order.getExtendReceiveAction() > OPERATION_ONLY_ADMIN) {
				//买家延迟过
				order.setExtendReceiveAction(OPERATION_ALL);
			}else {
				//仅仅商家操作过
				order.setExtendReceiveAction(OPERATION_ONLY_ADMIN);
			}
		}else if(param.getIsMp() == OrderConstant.IS_MP_Y) {
			//买家操作
			order.setExtendReceiveAction(OPERATION_MP);
		}
		//调用方法
		orderInfo.extendReceive(order);
		//TODO 发送通知
		//操作记录
		record.insertRecord(Arrays.asList(new Integer[] { RecordContentTemplate.ORDER_EXTEND_RECEIVE.code }), new String[] {param.getOrderSn()});
		logger().info("订单延长收货时间结束,时间修改为:{}->{}",order.getExtendReceiveTime(),extendTime);
		return null;
	}

}
