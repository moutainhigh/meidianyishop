
package com.meidianyi.shop.service.shop.order.action;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.AbstractExcelDisposer;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiJsonResult;
import com.meidianyi.shop.dao.main.StoreAccountDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PartOrderGoodsShipRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.express.ExpressVo;
import com.meidianyi.shop.service.pojo.shop.maptemplate.OrderDeliverParam;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiShippingParam;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.ShipParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.ShipParam.ShipGoods;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.ShipVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.shop.express.ExpressService;
import com.meidianyi.shop.service.shop.maptemplatesend.MapTemplateSendService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.record.OrderActionService;
import com.meidianyi.shop.service.shop.order.ship.ShipInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.ReturnOrderGoods.RETURN_ORDER_GOODS;
/**
 * 	发货
 * @author 王帅
 *
 */
@Component
public class ShipService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam, ShipParam> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ShipInfoService shipInfo;
	@Autowired
	OrderActionService orderAction;
	@Autowired
	public RecordAdminActionService record;
	@Autowired
	public OrderGoodsService orderGoods;
    @Autowired
    public OrderOperateSendMessage sendMessage;
    @Autowired
    public StoreAccountDao storeAccountDao;
    @Autowired
    private PrescriptionDao prescriptionDao;


	@Override
	public OrderServiceCode getServiceCode() {
		return OrderServiceCode.ADMIN_SHIP;
	}

	@Override
	public ExecuteResult execute(ShipParam param) {
		logger.info("发货参数为:"+param.toString());
		//是否存在可发货数>当前发货数的标识
		boolean flag = false;
		//可发货商品
		List<OrderGoodsVo> canBeShipped = canBeShipped(param.getOrderSn());
		if(canBeShipped == null || canBeShipped.size() == 0) {
			//无可发货信息
			logger.error("发货时无可发货商品");
			return ExecuteResult.create(JsonResultCode.CODE_ORDER, null);
		}
		if(ExpressService.NO_2_EXPRESS.equals(param.getShippingId())) {
            //无单号物流发货时置单号为空
            param.setShippingNo(StringUtils.EMPTY);
        }
		handleShipAccountId(param);
		Map<Integer, OrderGoodsVo> cbsMap = canBeShipped.stream().collect(Collectors.toMap(OrderGoodsVo::getRecId, Function.identity()));
		ShipGoods[] shipGoods = param.getShipGoods();
		//构建商品行查询条件
		ArrayList<Integer> recIds = new ArrayList<>(shipGoods.length);
		for (ShipGoods sTemp : shipGoods) {
			recIds.add(sTemp.getRecId());
		}
		//查询商品行,返回的OrderGoodsRecord
		Map<Integer, OrderGoodsRecord> goods = db().fetch(ORDER_GOODS,ORDER_GOODS.REC_ID.in(recIds)).intoMap(OrderGoodsRecord::getRecId);
		//构造主表基本信息 b2c_order_info
		OrderInfoRecord orderRecord = db().fetchOne(ORDER_INFO, ORDER_INFO.ORDER_SN.eq(param.getOrderSn()));
		//构造_添加部分发货信息 b2c_part_order_goods_ship
		List<PartOrderGoodsShipRecord> shipInfoList = new ArrayList<>(shipGoods.length);
		//声明存放OrderGoodsRecord的list
		ArrayList<OrderGoodsRecord> recordList = new ArrayList<>(shipGoods.length);
		//发货批次号,同一批次为同一快递
		String batchNo = orderRecord.getOrderSn() + "_" + DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		for (ShipGoods oneGoods : shipGoods) {
			Integer sendNumber = oneGoods.getSendNumber();
			//校验_商品发货数量
			Integer recId = oneGoods.getRecId();
			if(cbsMap.get(recId) == null) {
				logger.error("商品不可发货或已发货,order_goods_rec_id:"+recId);
				//该商品不可发货或已发货
				return ExecuteResult.create(JsonResultCode.CODE_ORDER, null);
			}else if(cbsMap.get(recId).getGoodsNumber() < sendNumber ) {
				logger.error("商品发货数量大于可发货数量,order_goods_rec_id:"+recId);
				//发货数量大于可发货数量
				return ExecuteResult.create(JsonResultCode.CODE_ORDER, null);
			}else if(cbsMap.get(recId).getGoodsNumber() > sendNumber ) {
				flag = true;
			}
			//下次可发货数 = 可发货数-当前发货数,计算是否可以切换状态为已发货
			cbsMap.get(recId).setGoodsNumber(cbsMap.get(recId).getGoodsNumber() - sendNumber);
			//构造参数
			OrderGoodsRecord orderGoodsVo = goods.get(oneGoods.getRecId());
			orderGoodsVo.setSendNumber((orderGoodsVo.getSendNumber() + sendNumber));
			recordList.add(orderGoodsVo);
			shipInfoList.add(shipInfo.addRecord(orderGoodsVo,orderRecord, batchNo, param, sendNumber));
		}
		//更新处方药师签名
        setPharmacistSignature(recordList,param);
		//判断此次发货是否为部分发货
		byte partShipFlag = OrderConstant.NO_PART_SHIP;
		if(canBeShipped.size() > shipGoods.length || flag) {
			partShipFlag = OrderConstant.PART_SHIP;
		}
		//设置是否部分发货
		if(partShipFlag == OrderConstant.PART_SHIP) {
			orderRecord.setPartShipFlag(OrderConstant.PART_SHIP);
		}
		orderRecord.setShippingTime(DateUtils.getSqlTimestamp());
		orderRecord.setShippingNo(param.getShippingNo());
		orderRecord.setShippingId(param.getShippingId());
		transaction(()->{
			//添加（部分）发货信息 b2c_part_order_goods_ship
			db().batchInsert(shipInfoList).execute();
			//更新发货数量 b2c_order_goods
			db().batchUpdate(recordList).execute();
			//判断此次发货是否全部发货
			if(setOrderStatus(orderRecord)) {
				orderRecord.setOrderStatus(OrderConstant.ORDER_SHIPPED);
			}
			//更新主表基本信息 b2c_order_info
			db().executeUpdate(orderRecord, ORDER_INFO.ORDER_SN.eq(param.getOrderSn()));

		});
		//action操作
		orderAction.addRecord(orderRecord, param, OrderConstant.ORDER_WAIT_DELIVERY, orderRecord.getOrderStatus() == OrderConstant.ORDER_SHIPPED ? "全部发货 " : "部分发货");
		//操作记录
		record.insertRecord(Collections.singletonList(RecordContentTemplate.ORDER_SHIP.code), param.getOrderSn());
		//发送消息模板
        sendMessage.send(orderRecord, recordList);
		logger.info("发货完成");
		return null;
	}


	/**
	 * 处理账户的id
	 * @param param
	 */
	public void handleShipAccountId(ShipParam param) {
		if (param.getPlatform().equals(OrderConstant.PLATFORM_ADMIN)){
			param.setShipAccountId(param.getAdminInfo().getSysId());
			param.setMobile(param.getAdminInfo().getMobile());
		}else if (param.getPlatform().equals(OrderConstant.PLATFORM_STORE)){
			param.setShipAccountId(param.getStoreInfo().getStoreAccountId());
			param.setShipUserId(param.getStoreInfo().getStoreAuthInfoVo().getStoreAccountInfo().getUserId());
			param.setMobile(param.getStoreInfo().getStoreAuthInfoVo().getStoreAccountInfo().getMobile());
		}else if (param.getPlatform().equals(OrderConstant.PLATFORM_WXAPP)){
			param.setShipUserId(param.getWxUserInfo().getUserId());
			param.setMobile(param.getWxUserInfo().getWxUser().getMobile());
		}else if (param.getPlatform().equals(OrderConstant.PLATFORM_WXAPP_STORE)){
			param.setShipAccountId(param.getWxUserInfo().getStoreAccountId());
			param.setShipUserId(param.getWxUserInfo().getUserId());
			StoreAccountVo oneInfo = storeAccountDao.getOneInfo(param.getWxUserInfo().getStoreAccountId());
			param.setMobile(oneInfo.getMobile());
		}else {
			param.setShipAccountId(0);
			param.setShipUserId(0);
			param.setMobile("");
		}
	}

    /**
     * 更新处方药师签名
     * @param list
     */
	public void setPharmacistSignature(List<OrderGoodsRecord> list,ShipParam param){
	    List<String> prescriptionCodeList=list.stream().map(OrderGoodsRecord::getPrescriptionCode).collect(Collectors.toList());
        prescriptionCodeList= prescriptionCodeList.stream().distinct().collect(Collectors.toList());
        for(String preCode:prescriptionCodeList){
            PrescriptionVo prescriptionVo=prescriptionDao.getDoByPrescriptionNo(preCode);
            StoreAccountVo oneInfo=storeAccountDao.getOneInfo(param.getShipAccountId());
            if(prescriptionVo!=null&&oneInfo!=null){
                prescriptionDao.updatePharmacistSignature(preCode,oneInfo.getSignature());
            }
        }
    }
	/**
	 * 发货查询
	 * @param param
	 * @return ShipVo :
	 */
	@Override
	public Object query(OrderOperateQueryParam param) {
		ShipVo shipVo = null;
		logger.info("获取可发货信息参数为:" + param.toString());
		// 订单信息
		shipVo = db().select(ORDER_INFO.ORDER_SN,ORDER_INFO.MAIN_ORDER_SN,ORDER_INFO.CONSIGNEE, ORDER_INFO.MOBILE, ORDER_INFO.COMPLETE_ADDRESS).from(ORDER_INFO)
				.where(ORDER_INFO.ORDER_SN.eq(param.getOrderSn()).and(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY))).fetchOneInto(ShipVo.class);

		if(shipVo == null || shipVo.getOrderSn().equals(shipVo.getMainOrderSn())) {
			return null;
		}
		// 设置可发货信息
		shipVo.setOrderGoodsVo(canBeShipped(param.getOrderSn()));
		logger.info("获取可发货信息完成");
		return shipVo;
	}

	/**
	 * 获取该订单下可发货商品列表
	 */
	public List<OrderGoodsVo> canBeShipped(String orderSn) {
		// TODO 修改select*
		//该单是否支持发货

		//TODO Short.valueOf("0")正常商品行
		List<OrderGoodsVo> orderGoods = db().select(ORDER_GOODS.asterisk()).from(ORDER_GOODS)
				.where(ORDER_GOODS.ORDER_SN.eq(orderSn).and(ORDER_GOODS.SEND_NUMBER.eq(0))).fetchInto(OrderGoodsVo.class);
		// 查询退货中信息
		Map<Integer, List<OrderReturnGoodsVo>> returnOrderGoods = db().select(RETURN_ORDER_GOODS.asterisk()).select()
				.from(RETURN_ORDER_GOODS)
				.where(RETURN_ORDER_GOODS.ORDER_SN.eq(orderSn),
						RETURN_ORDER_GOODS.SUCCESS.eq(OrderConstant.SUCCESS_RETURNING))
				.fetchGroups(RETURN_ORDER_GOODS.REC_ID, OrderReturnGoodsVo.class);
		Iterator<OrderGoodsVo> iterator = orderGoods.iterator();
		while (iterator.hasNext()) {
			OrderGoodsVo vo = (OrderGoodsVo) iterator.next();
			// 可发货数量=总数-退货(退货完成)-发货-退货(退货中)
			int numTemp;
			List<OrderReturnGoodsVo> orgTemp = returnOrderGoods.get(vo.getRecId());
			int sum = orgTemp == null ? 0 : orgTemp.stream().mapToInt(OrderReturnGoodsVo::getGoodsNumber).sum();
			if ((numTemp = vo.getGoodsNumber() - vo.getReturnNumber() - vo.getSendNumber() - sum) > 0) {
				vo.setGoodsNumber(numTemp);
			} else {
				iterator.remove();
			}
		}
		return orderGoods;
	}

	public boolean setOrderStatus(OrderInfoRecord order) {
		Result<OrderGoodsRecord> goods = orderGoods.getByOrderId(order.getOrderId());
		for (OrderGoodsRecord goodsRecord : goods) {
			if(goodsRecord.getGoodsNumber() > goodsRecord.getSendNumber() + goodsRecord.getReturnNumber()) {
				return false;
			}
		}
		return true;
	}


    /**
     * 发货统一对外接口
     * @param param
     * @return
     */
    public ApiJsonResult shippingApi(ApiShippingParam param) throws MpException {
        ApiJsonResult result = new ApiJsonResult();
        if (param == null) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("content为空");
            return result;
        }
        if(StringUtils.isBlank(param.getOrderSn())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数order_sn为空");
            return result;
        }
        if(StringUtils.isBlank(param.getLogisticsCode())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数logistics_code为空");
            return result;
        }
        if(StringUtils.isBlank(param.getLogisticsNo())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数logistics_no为空");
            return result;
        }
        OrderInfoRecord order = saas().getShopApp(getShopId()).readOrder.orderInfo.getOrderByOrderSn(param.getOrderSn());
        if(order == null ) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("发货订单不存在");
            return result;
        }
        //此次发货商品
        List<ShipParam.ShipGoods> shipGoodsInfo = new ArrayList<>();
        //参数中的发货商品
        List<Integer> recIds = param.getRecIds();
        //发货信息查询
        OrderOperateQueryParam queryParam = new OrderOperateQueryParam();
        queryParam.setOrderId(order.getOrderId());
        queryParam.setOrderSn(order.getOrderSn());
        ShipVo shipInfo = (ShipVo)query(queryParam);
        for (OrderGoodsVo goods : shipInfo.getOrderGoodsVo()) {
            if(CollectionUtils.isEmpty(recIds) || recIds.contains(goods.getRecId())) {
                ShipGoods shipGoods = new ShipGoods();
                shipGoods.setRecId(goods.getRecId());
                shipGoods.setSendNumber(goods.getGoodsNumber());
                shipGoodsInfo.add(shipGoods);
            }
        }
        if(CollectionUtils.isEmpty(shipGoodsInfo)) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("无可发货商品");
            return result;
        }
        ShipParam shipParam = new ShipParam();
        shipParam.setOrderId(order.getOrderId());
        shipParam.setOrderSn(order.getOrderSn());
        shipParam.setAction((byte) OrderServiceCode.ADMIN_SHIP.ordinal());
        shipParam.setIsMp(OrderConstant.IS_MP_ERP_OR_EKB);
        shipParam.setShippingNo(param.getLogisticsNo());
        shipParam.setShipGoods(shipGoodsInfo.toArray(new ShipParam.ShipGoods[0]));
        //获取快递公司
        ExpressVo expressVo = saas().getShopApp(getShopId()).readOrder.expressService.getByCode(param.getLogisticsCode());
        if(expressVo == null) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("快递公司不存在");
            return result;
        }
        shipParam.setShippingId(expressVo.getShippingId());
        ExecuteResult executeResult = saas().getShopApp(getShopId()).orderActionFactory.orderOperate(shipParam);
        if(executeResult == null || executeResult.isSuccess()) {
            result.setCode(ApiExternalGateConstant.ERROR_CODE_SUCCESS);
        }else {
            logger.error("外服系统调用发货接口失败，executeResult：{}", executeResult);
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg(Util.translateMessage(AbstractExcelDisposer.DEFAULT_LANGUAGE, executeResult.getErrorCode().getMessage(), JsonResult.LANGUAGE_TYPE_MSG, executeResult.getErrorParam()));
        }
        return result;
    }
}
