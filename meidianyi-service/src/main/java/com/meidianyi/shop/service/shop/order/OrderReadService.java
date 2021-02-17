package com.meidianyi.shop.service.shop.order;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.foundation.util.api.ApiBasePageParam;
import com.meidianyi.shop.common.foundation.util.api.ApiPageResult;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateParam;
import com.meidianyi.shop.common.pojo.saas.api.ApiJsonResult;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.common.pojo.shop.table.ReturnOrderDo;
import com.meidianyi.shop.common.pojo.shop.table.ReturnOrderGoodsDo;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.order.OrderInfoDao;
import com.meidianyi.shop.dao.shop.order.ReturnOrderDao;
import com.meidianyi.shop.dao.shop.order.ReturnOrderGoodsDao;
import com.meidianyi.shop.dao.shop.patient.UserPatientCoupleDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.dao.shop.rebate.PrescriptionRebateDao;
import com.meidianyi.shop.db.main.tables.records.SystemChildAccountRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRebateRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderRefundRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnStatusChangeRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.ShopManageVo;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.express.ExpressVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupOrderVo;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderQueryVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderSimpleInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveOrderList;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiOrderGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiOrderListVo;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiOrderPageResult;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiOrderQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiReturnGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiReturnOrderListVo;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiReturnOrderPageResult;
import com.meidianyi.shop.service.pojo.shop.order.export.OrderExportQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.export.OrderExportVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;
import com.meidianyi.shop.service.pojo.shop.order.must.OrderMustVo;
import com.meidianyi.shop.service.pojo.shop.order.rebate.OrderRebateVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OperatorRecord;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderConciseRefundInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnListVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.ReturnOrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.ReturnOrderParam;
import com.meidianyi.shop.service.pojo.shop.order.shipping.BaseShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.InsteadPayDetailsParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.InsteadPayDetailsVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.InsteadPayOrderDetails;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit.OrderGoodsSimpleAuditVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipFailModel;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipListParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipListVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.bo.PrescriptionItemBo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateParam;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticAddVo;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticParam;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticPayVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.pojo.wxapp.comment.CommentListVo;
import com.meidianyi.shop.service.pojo.wxapp.footprint.FootprintDayVo;
import com.meidianyi.shop.service.pojo.wxapp.footprint.FootprintListVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoByOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoByOsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.market.groupbuy.GroupBuyUserInfo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderCenter;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderInfoMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderListMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.refund.AfterSaleServiceVo;
import com.meidianyi.shop.service.pojo.wxapp.order.refund.ReturnOrderListMp;
import com.meidianyi.shop.service.saas.privilege.ChildAccountService;
import com.meidianyi.shop.service.saas.shop.ShopAccountService;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.ShopReturnConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.distribution.OrderGoodsRebateService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.express.ExpressService;
import com.meidianyi.shop.service.shop.goods.FootPrintService;
import com.meidianyi.shop.service.shop.goods.GoodsCommentService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyListService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
import com.meidianyi.shop.service.shop.market.groupdraw.GroupDrawService;
import com.meidianyi.shop.service.shop.market.presale.PreSaleService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.order.action.ReturnService;
import com.meidianyi.shop.service.shop.order.action.ShipService;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.AdminMarketOrderInfoService;
import com.meidianyi.shop.service.shop.order.info.MpOrderInfoService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.invoice.InvoiceService;
import com.meidianyi.shop.service.shop.order.must.OrderMustService;
import com.meidianyi.shop.service.shop.order.record.ReturnStatusChangeService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import com.meidianyi.shop.service.shop.order.refund.goods.ReturnOrderGoodsService;
import com.meidianyi.shop.service.shop.order.refund.record.OrderRefundRecordService;
import com.meidianyi.shop.service.shop.order.refund.record.RefundAmountRecordService;
import com.meidianyi.shop.service.shop.order.ship.BulkshipmentRecordDetailService;
import com.meidianyi.shop.service.shop.order.ship.BulkshipmentRecordService;
import com.meidianyi.shop.service.shop.order.ship.ShipInfoService;
import com.meidianyi.shop.service.shop.order.store.StoreOrderService;
import com.meidianyi.shop.service.shop.order.sub.SubOrderService;
import com.meidianyi.shop.service.shop.recommend.RecommendService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.ORDER_GOODS;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.NO;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.YES;

/**
 * 	订单模块普通查询service
 * @author 常乐 2019年6月27日;王帅 2019/7/10
 */
@Service
public class OrderReadService extends ShopBaseService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public OrderInfoService orderInfo;
    @Autowired
    public AdminMarketOrderInfoService marketOrderInfo;
	@Autowired
	public OrderGoodsService orderGoods;
	@Autowired
	private ShipInfoService shipInfo;
	@Autowired
	private ReturnOrderService returnOrder;
	@Autowired
	private ReturnOrderGoodsService returnOrderGoods;
	@Autowired
	private ReturnOrderGoodsDao returnOrderGoodsDao;
	@Autowired
	private StoreOrderService storeOrder;
	@Autowired
	private UserService user;
	@Autowired
	private RefundAmountRecordService refundAmountRecord;
	@Autowired
	private ReturnStatusChangeService returnStatusChange;
	@Autowired
	private ShopReturnConfigService shopReturnConfig;
	@Autowired
	private ShipService ship;
	@Autowired
	private MpOrderInfoService mpOrderInfo;
	@Autowired
	private TradeService trade;
	@Autowired
	private GroupBuyListService groupBuyList;
	@Autowired
	private GroupBuyService groupBuyService;
	@Autowired
	private PreSaleService preSale;
	@Autowired
	private StoreService store;
	@Autowired
	private InvoiceService invoice;
    @Autowired
    private OrderMustService orderMust;
    @Autowired
    private GoodsCommentService goodsComment;
    @Autowired
    private GoodsMpService goodsMpService;
    @Autowired
    private FootPrintService footPrintService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    public ExpressService expressService;
    @Autowired
    private ConfigService configService;
    @Autowired
    public SubOrderService subOrderService;
    @Autowired
    private GroupDrawService groupDrawService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private MemberService member;
    @Autowired
    private OrderGoodsRebateService orderGoodsRebate;
    @Autowired
    private BulkshipmentRecordService batchRecord;
    @Autowired
    private BulkshipmentRecordDetailService batchDetailRecord;
    @Autowired
    private ShopAccountService shopAccount;
    @Autowired
    private ChildAccountService childAccount;
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private UserCardService userCard;
    @Autowired
    private OrderRefundRecordService orderRefundRecord;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private UserPatientCoupleDao userPatientCoupleDao;
    @Autowired
    private ReturnOrderDao returnOrderDao;
    @Autowired
    private OrderInfoDao orderInfoDao;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;
    @Autowired
    private PrescriptionRebateDao prescriptionRebateDao;
    @Autowired
    private DoctorService doctorService;

	/**
	 * 订单查询
	 * @param param
	 * @return PageResult
	 */
	public OrderQueryVo getPageList(OrderPageListQueryParam param) {
		logger.info("订单综合查询开始");
		OrderQueryVo result = new OrderQueryVo();
		//退款退货订单查询(其主表不同,所以走分支逻辑)
		if(param.searchType != null && param.searchType == 1) {
            result.setList(getReturnPageList(param));
			return result;
		}

		PageResult<OrderListInfoVo> pageResult = new PageResult<>();
        result.setList(pageResult);
		//得到订单号(包含主订单和正常订单)
		PageResult<String> orderSn = orderInfo.getOrderSns(param, result);
		pageResult.setPage(orderSn.getPage());
		if(orderSn.getDataList().size() < 1) {
			return result;
		}
		//查询出全部订单按照主订单分组，正常订单的key为orderSn
		Map<String, List<OrderListInfoVo>> allOrder = orderInfo.getOrders(orderSn.getDataList());
		//构造展示商品的订单:MainOrderCount.count=1的可能为正常订单或处于未子订单未被拆分,>1的为已经拆分
		Map<Integer,OrderListInfoVo> goodsList = new HashMap<Integer,OrderListInfoVo>(allOrder.size());
		//主订单或正常订单
		ArrayList<OrderListInfoVo> mainOrderList = new ArrayList<OrderListInfoVo>(orderSn.getDataList().size());
		//现子订单数>0的主订单
		ArrayList<Integer> orderCountMoreZero = new ArrayList<Integer>();
		for (String moc : orderSn.getDataList()) {
			List<OrderListInfoVo> list = allOrder.get(moc);
			int size = list.size();
			OrderListInfoVo mOrder = null;
			List<OrderListInfoVo> cList = size > 1 ? new ArrayList<OrderListInfoVo>(size - 1) : null;
			for (OrderListInfoVo order : list) {
				//将所有订单id放入goodsList,在后续向订单添加商品时增加过滤主订单下与子订单重复的商品
				goodsList.put(order.getOrderId(),order);
				if(order.getOrderSn().equals(moc)) {
					//设置订单支付方式（无子单）
					orderInfo.setPayCodeList(order);
					mOrder = order;
					if(size ==1) {
						break;
					}
				}else {
					cList.add(order);
				}
			}
			if(cList != null) {
				orderCountMoreZero.add(mOrder.getOrderId());
			}
			mOrder.setChildOrders(cList);
			mainOrderList.add(mOrder);
		}
		//需要查询商品的订单
		Integer[] allOrderSn = goodsList.keySet().toArray(new Integer[0]);
		//key为order_id,v为其下商品
		Map<Integer, List<OrderGoodsVo>> goods = orderGoods.getByOrderIds(allOrderSn).intoGroups(orderGoods.TABLE.ORDER_ID,OrderGoodsVo.class);
		Set<Entry<Integer, List<OrderGoodsVo>>> entrySet = goods.entrySet();
		for (Entry<Integer, List<OrderGoodsVo>> entry : entrySet) {
			//过滤主订单中已经拆到子订单的商品(依赖于orderinfo表自增id,当循环到主订单时其子订单下的商品都已插入到childOrders.goods里)
			if(orderCountMoreZero.contains(entry.getKey())) {
				orderInfo.filterMainOrderGoods(goodsList.get(entry.getKey()),entry.getValue());
				continue;
			}
			goodsList.get(entry.getKey()).setGoods(entry.getValue());
		}
		//查询订单订单是否存在退款中订单
		Map<Integer, Integer> returningCount = returnOrder.getOrderCount(allOrderSn, OrderConstant.REFUND_STATUS_AUDITING , OrderConstant.REFUND_STATUS_AUDIT_PASS , OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
		//设置订单操作
		for (List<OrderListInfoVo> orderList: allOrder.values()) {
			for(OrderListInfoVo order : orderList) {
				OrderOperationJudgment.operationSet(order,returningCount.get(order.getOrderId()),ship.canBeShipped(order.getOrderSn()));
			}
		}
		// 获取订单门店信息
        getStoreInfo(mainOrderList);
        pageResult.setDataList(mainOrderList);
		logger.info("订单综合查询结束");
		return result;
	}

    /**
     * 获取当前订单所属门店信息
     * @param mainOrderList
     */
    private void getStoreInfo(ArrayList<OrderListInfoVo> mainOrderList) {
        List<Integer> storeIds = mainOrderList.stream().map(OrderListInfoVo::getStoreId).distinct().collect(Collectors.toList());
        List<StoreOrderVo> storeInfoByIds = store.getStoreInfoByIds(storeIds);
        for (OrderListInfoVo orderListInfoVo : mainOrderList) {
            storeInfoByIds.stream().filter(storeInfo -> storeInfo.getStoreId()
                .equals(orderListInfoVo.getStoreId())).forEach(orderListInfoVo::setStoreOrderVo);
        }
    }


	/**
	 * 订单详情
	 * @param orderSn
	 * @return
	 */
	public OrderInfoVo get(String orderSn) {
		List<OrderInfoVo> orders = orderInfo.getOrdersByCondition(orderInfo.TABLE.MAIN_ORDER_SN.eq(orderSn).or(orderInfo.TABLE.ORDER_SN.eq(orderSn)) , OrderInfoVo.class);
		int size = orders.size();
		if(size == 0) {
			return null;
		}
		//OrderIds
		List<Integer> orderIds = new ArrayList<Integer>(size);
		//配送信息orderSn
		List<String> sOrderSns = new ArrayList<String>();
		//退货款信息ids
		List<String> rOrderSns = new ArrayList<String>();
		//子订单
		List<OrderInfoVo> childOrders = size <=1 ? null : new ArrayList<OrderInfoVo>(size -1);
		//主订单(正常订单mainOrder=正常订单)
		OrderInfoVo mainOrder = null;
		//构造参数
		for (OrderInfoVo order : orders) {
			if(orderInfo.isMainOrder(order) || StringUtils.isBlank(order.getMainOrderSn())) {
				mainOrder = order;
			}else{
				childOrders.add(order);
			}
			//所有订单sn
			orderIds.add(order.getOrderId());
			//add配送信息
			sOrderSns.add(order.getOrderSn());
			//add退货款信息
			if(order.getRefundStatus() != OrderConstant.REFUND_DEFAULT_STATUS) {
				rOrderSns.add(order.getOrderSn());
			}
		}
		//查询商品行
		Map<Integer, List<OrderGoodsVo>> goods = orderGoods.getByOrderIds(orderIds.toArray(new Integer[orderIds.size()])).intoGroups(orderGoods.TABLE.ORDER_ID,OrderGoodsVo.class);
		//查询配送信息
		Map<String, List<ShippingInfoVo>> shippingByOrderSn = shipInfo.getShippingByOrderSn(sOrderSns);
		//查询退款订单信息
		Map<String, List<OrderConciseRefundInfoVo>> refundByOrderSn = returnOrder.getRefundByOrderSn(rOrderSns.toArray(new String[rOrderSns.size()])).intoGroups(returnOrder.TABLE.ORDER_SN,OrderConciseRefundInfoVo.class);
		//查询退货款商品信息
		Map<Integer, List<OrderReturnGoodsVo>> refundGoodsByOrderSn = returnOrderGoods.getByOrderSn(rOrderSns.toArray(new String[rOrderSns.size()])).intoGroups(returnOrderGoods.TABLE.RET_ID,OrderReturnGoodsVo.class);
		//把退*商品信息插入退*订单信息中
		refundByOrderSn.forEach((k,v)->
			v.forEach(rOrder->
				rOrder.setOrderReturnGoodsVo(refundGoodsByOrderSn.get(rOrder.getRetId()))
			)
		);
		//查询订单订单是否存在退款中订单
		Map<Integer, Integer> returningCount = returnOrder.getOrderCount(orderIds.toArray(new Integer[orderIds.size()]), OrderConstant.REFUND_STATUS_AUDITING , OrderConstant.REFUND_STATUS_AUDIT_PASS , OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
		//构造order
        buildOrders(orders, goods, shippingByOrderSn, refundByOrderSn, returningCount);
        //设置订单支付方式（无子单）
		orderInfo.setPayCodeList(mainOrder);
		//设置核销员
		if(mainOrder.getVerifierId() > 0) {
			mainOrder.setVerifierName(user.getUserByUserId(mainOrder.getVerifierId()).getUsername());
		}
		//设置
		mainOrder.setChildOrders(childOrders);
		if(size > 1) {
			//过滤主订单下被拆出的goods
			orderInfo.filterMainOrderGoods(mainOrder, goods.get(mainOrder.getOrderId()));
		}
        mainOrder.setCardName(userCard.memberCardService.getCardNameByNo(mainOrder.getCardNo()));
		//拼团订单设置拼团中时间
        mainOrder.setPinStartTime(mainOrder.getPayTime());
        mainOrder.setRebateList(getOrderRebateInfo(mainOrder));
        mainOrder.setPinEndTime(groupDrawService.groupDrawOrders.getEndTime(mainOrder.getOrderSn()));
        mainOrder.setAffirmTime(mainOrder.getConfirmTime());
        //设置代付明细
        mainOrder.setInsteadPayInfo(subOrderService.paymentDetails(mainOrder.getOrderSn()));
        //处方药
        if (mainOrder.getOrderMedicalType().equals(OrderConstant.MEDICAL_TYPE_RX)){
			List<OrderGoodsSimpleAuditVo> allGoods = orderGoodsDao.listSimpleAuditByOrderId(mainOrder.getOrderId());
			List<String> oldCodes = allGoods.stream().map(OrderGoodsSimpleAuditVo::getPrescriptionOldCode).collect(Collectors.toList());
			List<String> codes = allGoods.stream().map(OrderGoodsSimpleAuditVo::getPrescriptionCode).collect(Collectors.toList());
			List<PrescriptionDo> prescriptionOldDoList = prescriptionDao.listPrescriptionByCode(oldCodes, PrescriptionDo.class);
			List<PrescriptionDo> prescriptionDoList = prescriptionDao.listPrescriptionByCode(codes, PrescriptionDo.class);
			mainOrder.setPrescriptionDoList(prescriptionDoList);
			mainOrder.setPrescriptionOldDoList(prescriptionOldDoList);
			//患者
			UserPatientDetailVo patientInfo = userPatientCoupleDao.getUserPatientInfo(mainOrder.getUserId(), mainOrder.getPatientId());
			mainOrder.setPatientInfo(patientInfo);
		}
        //处方明细，包含处方返利信息
        mainOrder.setPrescriptionItemList(getPrescriptionItemList(mainOrder));
		return mainOrder;
	}

    /**
     * 获取医师处方药品返利信息
     * @param order
     * @return
     */
	private  List<PrescriptionItemBo> getPrescriptionItemList(OrderInfoVo order){
        List<String> preCodeList=orderGoodsDao.getPrescriptionCodeListByOrderSn(order.getOrderSn());
        preCodeList=preCodeList.stream().distinct().collect(Collectors.toList());
        List<PrescriptionItemBo> boList=new ArrayList<>();
        for(String preCode:preCodeList){
            PrescriptionVo prescriptionVo= prescriptionDao.getDoByPrescriptionNo(preCode);
            PrescriptionRebateParam rebate = prescriptionRebateDao.getRebateByPrescriptionCode(preCode);
            if(prescriptionVo==null||rebate==null){
                continue;
            }
            PrescriptionItemBo prescriptionItemBo=new PrescriptionItemBo();

            List<PrescriptionItemDo> list=prescriptionItemDao.listOrderGoodsByPrescriptionCode(preCode);
            DoctorOneParam doctor=doctorService.getDoctorByCode(prescriptionVo.getDoctorCode());
            if(doctor!=null){
                prescriptionItemBo.setDoctorId(doctor.getId());
            }
            prescriptionItemBo.setDoctorCode(prescriptionVo.getDoctorCode());
            prescriptionItemBo.setItemList(list);
            prescriptionItemBo.setPrescriptionCode(preCode);
            prescriptionItemBo.setDoctorName(prescriptionVo.getDoctorName());
            prescriptionItemBo.setSettlementFlag(prescriptionVo.getSettlementFlag());
            boList.add(prescriptionItemBo);
        }
        return boList;
    }

    private void buildOrders(List<OrderInfoVo> orders, Map<Integer, List<OrderGoodsVo>> goods, Map<String, List<ShippingInfoVo>> shippingByOrderSn, Map<String, List<OrderConciseRefundInfoVo>> refundByOrderSn, Map<Integer, Integer> returningCount) {
        for (OrderInfoVo vo : orders) {
            vo.setShippingList(shippingByOrderSn.get(vo.getOrderSn()));
            vo.setRefundList(refundByOrderSn.get(vo.getOrderSn()));
            vo.setGoods(goods.get(vo.getOrderId()));
            //设置订单操作
            OrderOperationJudgment.operationSet(vo,returningCount.get(vo.getOrderId()),ship.canBeShipped(vo.getOrderSn()));
            //手动退款退货按钮显示
showManualReturn(vo);
        }
    }

    /**
     * 售后中心退款订单显示订单简略信息
     * @param orderSn
     * @return
     */
    public OrderSimpleInfoVo getSimpleInfo(String orderSn) {
        OrderInfoVo orderInfoVo = get(orderSn);
        OrderSimpleInfoVo simple = new OrderSimpleInfoVo();
        BeanUtils.copyProperties(orderInfoVo, simple);
        return simple;
    }    /**
    /**
     * admin显示手动退款退货按钮
     * @param vo
     */
    private void showManualReturn(OrderInfoVo vo) {
        //微信支付超一年不可退款
        if(OrderConstant.PAY_CODE_WX_PAY.equals(vo.getPayCode()) && vo.getPayTime() != null) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(vo.getPayTime().getTime());
            instance.add(Calendar.YEAR, 1);
            if(instance.getTimeInMillis() < System.currentTimeMillis()) {
                vo.setShowManualReturn(false);
                return;
            }
        }
        //订单状态：待付款、取消、关闭时不可退款
        if(vo.getOrderStatus() == OrderConstant.ORDER_WAIT_PAY || vo.getOrderStatus() == OrderConstant.ORDER_CANCELLED || vo.getOrderStatus() == OrderConstant.ORDER_CLOSED) {
            vo.setShowManualReturn(false);
            return;
        }
        //订单无可退商品且无可退金额
        if(!orderGoods.canReturnGoodsNumber(vo.getOrderSn()) && BigDecimalUtil.compareTo(orderInfo.getOrderFinalAmount(vo , Boolean.TRUE), refundAmountRecord.getOrderRefundAmount(vo.getOrderSn())) < 1) {
            vo.setShowManualReturn(false);
            return;
        }
        vo.setShowManualReturn(true);
    }

    /**
	 * 退货、款订单
	 * @return
	 */
	public PageResult<OrderReturnListVo> getReturnPageList(OrderPageListQueryParam param) {
		PageResult<OrderReturnListVo> result = returnOrder.getPageList(param);
		List<String> collect;
		List<OrderReturnListVo> dataList = result.dataList;
		if(dataList != null && dataList.size() > 0 ) {
			collect = dataList.stream().map(OrderReturnListVo::getOrderSn).collect(Collectors.toList());
		}else {
			return result;
		}
		//获取订单再分组
		Map<Integer, List<OrderReturnGoodsVo>> goods = returnOrderGoods.getByOrderSn(collect.toArray(new String[collect.size()])).intoGroups(returnOrderGoods.TABLE.RET_ID,OrderReturnGoodsVo.class);;
		for (OrderReturnListVo order : dataList) {
			order.setGoods(goods.get(order.getRetId()));
		}
		return result;
	}

	/**
	 * 	退款订单详情
	 * @param param
	 * @return
	 * @throws MpException
	 */
	public ReturnOrderInfoVo getReturnOrder(ReturnOrderParam param) throws MpException{
		ReturnOrderRecord rOrder = returnOrder.getByReturnOrderSn(param.getReturnOrderSn());
		if(Objects.isNull(rOrder)) {
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_RETURN_ORDER_NOT_EXIST);
		}
		OrderInfoRecord order = orderInfo.getOrderByOrderSn(rOrder.getOrderSn());
		if(Objects.isNull(order)) {
			throw new MpException(JsonResultCode.CODE_ORDER_NOT_EXIST);
		}
		//init vo
		ReturnOrderInfoVo vo = rOrder.into(ReturnOrderInfoVo.class);
		//vo set order
		vo.setOrderInfo(order.into(OrderInfoVo.class));
		//set can return shipping fee
		//获取已退运费
		BigDecimal returnShipingFee = returnOrder.getReturnShippingFee(rOrder.getOrderSn());
		//退运费校验
		if(OrderOperationJudgment.adminIsReturnShipingFee(vo.getOrderInfo().getShippingFee(), returnShipingFee, true)){
			vo.setCanReturnShippingFee(order.getShippingFee().subtract(returnShipingFee));
		} else {
            vo.setCanReturnShippingFee(BigDecimal.ZERO);
        }
		//退款商品
		if(rOrder.getReturnType() != OrderConstant.RT_ONLY_SHIPPING_FEE) {
			List<OrderReturnGoodsVo> goods = returnOrderGoods.getReturnGoods(rOrder.getOrderSn(),rOrder.getRetId()).into(OrderReturnGoodsVo.class);
            Map<Integer, OrderGoodsMpVo> keyMapByIds = orderGoods.getKeyMapByIds(order.getOrderId());
            goods.forEach(x->x.setIsGift(keyMapByIds.get(x.getRecId()).getIsGift()));
            vo.setReturnGoods(goods);
		}
		//快递消息
        ExpressVo shippingInfo = getShippingInfo(rOrder);
		if(shippingInfo != null) {
            vo.setShippingCode(shippingInfo.getShippingCode());
            vo.setShippingName(shippingInfo.getShippingName());
        }
		//金额计算
		setCalculateMoney(vo);
		//获取该退款订单操作记录
		List<OperatorRecord> operatorRecord = returnStatusChange.getOperatorRecord(rOrder.getRetId());
		vo.setOperatorRecord(operatorRecord);
		//获取最后一次操作此订单type
		if(operatorRecord.size() != 0) {
			vo.setOperatorLastType(operatorRecord.get(operatorRecord.size() - 1).getType());
		}
		//设置自动处理时间
		setReturnCfg(vo, rOrder);
		//设置订单类型
        vo.setOrderType(OrderInfoService.orderTypeToArray(order.getGoodsType()));
        //客服按钮展示开关
        vo.setReturnService(shopCommonConfigService.getReturnService());
        UserInfo userInfo = user.getUserInfo(rOrder.getUserId());
        vo.setUsername(userInfo.getUsername());
        vo.setMobile(userInfo.getMobile());
        vo.setReasonTypeDesc(OrderConstant.getReturnReasonDesc(vo.getReasonType().intValue()));
        //退款失败处理展示数据
        showRefundFailInfo(vo);
		return vo;
	}

	/**
	 * 	设置自动处理时间
	 * @param vo
	 * @param rOrder
	 */
	public void setReturnCfg(ReturnOrderInfoVo vo , ReturnOrderRecord rOrder) {
		if(rOrder.getIsAutoReturn() == NO) {
			return;
		}
        long currentTimeMillis = System.currentTimeMillis();
		//以下自动处理时间为时间间隔单位毫秒
        if (rOrder.getReturnType() != OrderConstant.RT_GOODS
				&& rOrder.getRefundStatus() == OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING
				&& shopReturnConfig.getReturnMoneyDays() != null) {
			//买家发起仅退款申请后，商家在return_money_days日内未处理，系统将自动退款
			vo.setReturnMoneyDays(rOrder.getShippingOrRefundTime().toInstant()
					.plus(Duration.ofDays(shopReturnConfig.getReturnMoneyDays())).toEpochMilli() - currentTimeMillis);
			return;
		}
		if (rOrder.getReturnType() == OrderConstant.RT_GOODS) {
			if(rOrder.getRefundStatus() == OrderConstant.REFUND_STATUS_AUDITING
					&& shopReturnConfig.getReturnAddressDays() != null) {
				//商家已发货，买家发起退款退货申请，商家在return_address_days日内未处理，系统将默认同意退款退货，并自动向买家发送商家的默认收货地址
				vo.setReturnAddressDays(rOrder.getApplyTime().toInstant()
					.plus(Duration.ofDays(shopReturnConfig.getReturnAddressDays())).toEpochMilli() - currentTimeMillis);
				return;
			}
			if(rOrder.getRefundStatus() == OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING
                && shopReturnConfig.getReturnShippingDays() != null) {
				//买家已提交物流信息，商家在return_shopping_days日内未处理，系统将默认同意退款退货，并自动退款给买家。
                vo.setReturnShippingDays(rOrder.getShippingOrRefundTime().toInstant()
                    .plus(Duration.ofDays(shopReturnConfig.getReturnShippingDays())).toEpochMilli() - currentTimeMillis);
				return;
			}
			if(rOrder.getRefundStatus() == OrderConstant.REFUND_STATUS_AUDIT_PASS) {
				//商家同意退款退货，买家在7日内未提交物流信息，且商家未确认收货并退款，退款申请将自动完成。
				vo.setReturnAuditPassNotShoppingDays(rOrder.getApplyPassTime().toInstant()
						.plus(Duration.ofDays(shopReturnConfig.getReturnPassDays())).toEpochMilli() - currentTimeMillis);
				return;
			}
		}

	}
	/**
	 * 	金额计算
	 * @param vo
	 */
	public void setCalculateMoney (ReturnOrderInfoVo vo) {
		if(vo.getRefundStatus() == OrderConstant.REFUND_STATUS_FINISH) {
			//成功状态查此次退款记录
			List<String> subOrderSn = subOrderService.getSubOrderSn(vo.getOrderSn());
			if (subOrderSn!=null&&subOrderSn.size()>0){
				subOrderSn.add(vo.getOrderSn());
			}else {
				subOrderSn =Arrays.asList(vo.getOrderSn());
			}
			vo.setCalculateMoney(refundAmountRecord.getReturnAmountMap(subOrderSn,vo.getRetId(), null));
			return;
		}
		if(vo.getRefundStatus() == OrderConstant.REFUND_STATUS_AUDIT_PASS || vo.getRefundStatus() == OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING) {
			//查询可退金额
			LinkedHashMap<String, BigDecimal> returnAmountMap = refundAmountRecord.getReturnAmountMap(Arrays.asList(vo.getOrderSn()),null, null);
			final BigDecimal amount = orderInfo.getOrderFinalAmount(vo.getOrderInfo() , Boolean.TRUE);
			vo.setCalculateMoney(orderInfo.getCanReturn(vo.getOrderInfo() , amount , returnAmountMap));
		}
		//其他情况不用查询
	}
	/**
	 * 	买单订单查询
	 *
	 * @param param
	 * @return
	 */
	 public PageResult<StoreOrderListInfoVo> getPageList(StoreOrderPageListQueryParam param) {
		PageResult<StoreOrderListInfoVo> result = storeOrder.getPageList(param);
		return result;
	 }

	/**
	 * 买单订单详情
	 * @param orderSn
	 * @return
	 */
	 public StoreOrderInfoVo getStoreOrder(String orderSn) {
		return storeOrder.get(orderSn);
	}

	/**
	 * mp端查询订单
	 * @param param
	 */
	public OrderCenter getPageList(OrderListParam param) {
		logger.info("mp订单列表查询"+param.toString());
        OrderCenter result = new OrderCenter();
        PageResult<OrderListMpVo> orders = mpOrderInfo.getPageList(param);
        result.setOrders(orders);
        Map<Byte, Integer> orderStatusNum = mpOrderInfo.getOrderStatusNum(param.getWxUserInfo().getUserId(), param.getSearch(), false, 0);
        result.setOrderStatuCount(orderStatusNum);
        if(CollectionUtils.isEmpty(orders.dataList)) {
		 return result;
		}
		//设置退款信息
		List<String> orderSnList = orders.getDataList().stream().map(OrderListMpVo::getOrderSn).collect(Collectors.toList());
		Map<String, List<ReturnOrderDo>>  returnOrderDoMap = getListReturnInfo(orderSnList, param.getStoreId());
        //商品
		Map<Integer, List<OrderGoodsMpVo>> goods = orderGoods.getByOrderIds(orders.dataList.stream().map(OrderListMpVo::getOrderId).toArray(Integer[]::new)).intoGroups(orderGoods.TABLE.ORDER_ID,OrderGoodsMpVo.class);
		for(OrderListMpVo order : orders.dataList) {
			//订单类型
			order.setOrderType(Arrays.asList(OrderInfoService.orderTypeToByte(order.getGoodsType())));
			//奖品订单判断
			order.setIsLotteryGift(isAwardOrder(order.getOrderType()) ? YES : NO);
			//设置商品
			order.setGoods(goods.get(order.getOrderId()));
			//订单操作设置（商品订单类型需要提前计算好）
			setMpOrderOperation(order);
            //积分兑换商品价格小程序端特殊展示
            editShowGoodsPrice(order);
			//拼团
			if(order.getOrderType().contains(BaseConstant.ACTIVITY_TYPE_GROUP_BUY)) {
				order.setGroupBuyInfo(groupBuyList.getByOrder(order.getOrderSn()));
			}
			//是否退过款
			order.setIsReturn(order.getRefundStatus() != OrderConstant.REFUND_DEFAULT_STATUS ? YES : NO);
			List<ReturnOrderDo> returnOrderDoList = returnOrderDoMap.get(order.getOrderSn());
			order.setReturnOrderDoList(returnOrderDoList);
		}
		return result;
	}

	/**
	 * 设置退款需要的信息
	 * @param orderSnList
	 * @param storeId
	 * @return
	 */
	private Map<String, List<ReturnOrderDo>>  getListReturnInfo(List<String> orderSnList, Integer storeId) {
		//获取退款订单
		Map<String, List<ReturnOrderDo>> returnOrderDoMap = returnOrderDao.listPendingReturnOrderDo(orderSnList);
		//订单商品
		List<Integer> returnOrderIds =new ArrayList<>();
		returnOrderDoMap.values().forEach(item->{
			returnOrderIds.addAll(item.stream().map(ReturnOrderDo::getRetId).collect(Collectors.toList()));
		});
		Map<String, List<ReturnOrderGoodsDo>> returnOrderGoodsMap = returnOrderGoodsDao.listReturnOrderGoods(returnOrderIds);

		returnOrderDoMap.forEach((orderSn,item) -> {
			item.forEach(returnOrderDo -> returnOrderDo.setReturnOrderGoodsDoList(returnOrderGoodsMap.get(orderSn)));
		});
		return returnOrderDoMap;
	}


	private void setBkPayOperation(OrderListMpVo order) {
		//有效时间区间
		Record2<Timestamp, Timestamp> timeInterval = preSale.getTimeInterval(order.getActivityId());
		order.setPreSaleTimeInterval(new Timestamp[] {timeInterval.value1(),timeInterval.value2()});
		long currenTmilliseconds  = Instant.now().toEpochMilli();
		if(timeInterval.value1().getTime() < currenTmilliseconds && currenTmilliseconds < timeInterval.value2().getTime() ) {
            order.setPayOperationTime(timeInterval.value2().getTime() - currenTmilliseconds);
			order.setIsPayEndPayment(NumberUtils.BYTE_ONE);
		}else {
            order.setPayOperationTime(0L);
			order.setIsPayEndPayment(NumberUtils.BYTE_ZERO);
		}
	}

	/**
	 * mp订单详情
	 * @param param
	 * @return
	 * @throws MpException
	 */
	public OrderInfoMpVo mpGet(OrderParam param) throws MpException {
		List<OrderInfoMpVo> orders = orderInfo.getByOrderSn(param.getOrderSn() , OrderInfoMpVo.class);
		if(CollectionUtils.isEmpty(orders)) {
			throw new MpException(JsonResultCode.CODE_ORDER_NOT_EXIST);
		}
		OrderInfoMpVo order = orders.get(0);
        List<Byte> orderType = Arrays.asList(OrderInfoService.orderTypeToByte(order.getGoodsType()));
		//商品
		Map<Integer, OrderGoodsMpVo> goods = orderGoods.getKeyMapByIds(order.getOrderId());
		//set orderType
		order.setOrderType(orderType);
		List<OrderGoodsMpVo> goodsList = new ArrayList<OrderGoodsMpVo>(goods.values());
		//set goods
		order.setGoods(goodsList);
		//奖品订单判断
		order.setIsLotteryGift(isAwardOrder(order.getOrderType()) ? YES : NO);
		//订单操作设置
		setMpOrderOperation(order);
		//是否退过款
		order.setIsReturn(order.getRefundStatus() != OrderConstant.REFUND_DEFAULT_STATUS ? YES : NO);
		//门店信息
		order.setStoreInfo(order.getStoreId() > 0 ? store.getStore(order.getStoreId()) : null);
		//发票
		order.setInvoiceInfo(order.getInvoiceId() > 0 ? invoice.get(order.getInvoiceId()) : null);

		//当前订单配送信息
		order.setShippingInfo(getMpOrderShippingInfo(order.getOrderSn(), goods));
		//核销员信息
		if(order.getVerifierId() > 0) {
			UserRecord userInfo = user.getUserByUserId(order.getVerifierId());
			order.setVerifierInfo(userInfo.getUsername(), userInfo.getMobile());
		}
		//子单
		if(orderType.contains(BaseConstant.ACTIVITY_TYPE_GIVE_GIFT) && order.getOrderSn().equals(order.getMainOrderSn()) && orders.size() > 1) {
			//只显示生成订单的子订单
			order.setSubOrder(getSubOrder(orders.subList(1, orders.size())));
		}
		//TODO 好物圈
		// 拼团
		if(orderType.contains(BaseConstant.ACTIVITY_TYPE_GROUP_BUY)){
			GroupOrderVo groupOrder = groupBuyList.getByOrder(order.getOrderSn());
			Integer groupBuyLimitAmout = groupBuyService.getGroupBuyLimitAmout(groupOrder.getActivityId());
			List<GroupBuyUserInfo> pinUserList = groupBuyList.getGroupUserList(groupOrder.getGroupId());
			order.setGroupBuyUserInfos(pinUserList);
			order.setGroupId(groupOrder.getGroupId());
			GroupOrderVo groupOrderVo =new GroupOrderVo();
			groupOrderVo.setStatus(groupOrder.getStatus());
			groupOrderVo.setGroupBuyLimitAmout(groupBuyLimitAmout);
			order.setGroupBuyInfo(groupOrderVo);
		}else if(orderType.contains(BaseConstant.ACTIVITY_TYPE_GROUP_DRAW)) {
			//拼团抽奖
			GroupDrawInfoByOrderVo groupDraw=new GroupDrawInfoByOrderVo();
			GroupDrawInfoByOsVo groupByOrderSn = groupDrawService.getGroupByOrderSn(order.getOrderSn(), false);
			groupDraw.setPinGroup(groupByOrderSn);
			GroupDrawInfoVo into = groupDrawService.getById(groupByOrderSn.getActivityId()).into(GroupDrawInfoVo.class);
			groupDraw.setPinGroupInfo(into);
			groupDraw.setPinUserGroup(groupDrawService.getGroupList(groupByOrderSn.getActivityId(), groupByOrderSn.getGroupId(), null));
			order.setGroupDraw(groupDraw);
		}
		//TODO 优惠卷
        //客服按钮展示开关
        order.setOrderDetailService(shopCommonConfigService.getOrderDetailService());
        order.setShowMall(recommendService.goodsMallService.check("1"));
        //积分兑换商品价格小程序端特殊展示
        editShowGoodsPrice(order);
		//处方信息
		getPrescriptionInfo(order, goodsList);
		//退款订单增加详情
		returnOrderInfo(order);
		return order;
	}

    /**
     * 订单添加退款信息
     * @param order 订单入参
     */
	private void returnOrderInfo(OrderInfoMpVo order) {
		if (order.getOrderStatus().equals(OrderConstant.ORDER_RETURN_FINISHED)||order.getOrderStatus().equals(OrderConstant.ORDER_REFUND_FINISHED)){
			List<ReturnOrderListMp> returnOrderListMps = returnOrderDao.listByOrderSn(order.getOrderSn());
			returnOrderListMps.forEach(item->{
                item.setReasonTypeDesc(OrderConstant.getReturnReasonDesc(item.getReasonType().intValue()));
			});
			order.setReturnOrderList(returnOrderListMps);
		}
	}

	/**
	 * 处方信息
	 */
	private void getPrescriptionInfo(OrderInfoMpVo order, List<OrderGoodsMpVo> goodsList) {
		Set<String> prescriptionCodeSet = goodsList.stream().map(OrderGoodsMpVo::getPrescriptionCode).collect(Collectors.toSet());
		if (!prescriptionCodeSet.isEmpty()){
			List<PrescriptionVo> prescriptionVos = prescriptionDao.listPrescriptionList(prescriptionCodeSet);
			order.setPrescriptionList(prescriptionVos);
		}
	}

	private void editShowGoodsPrice(OrderListMpVo order) {
        if(order.getOrderType().contains(BaseConstant.ACTIVITY_TYPE_INTEGRAL)) {
            order.getGoods().forEach(x-> {
                if(x.getIsGift().equals((int) NO)) {
                    x.setGoodsPrice(BigDecimalUtil.subtrac(x.getDiscountedGoodsPrice(), BigDecimalUtil.divide(new BigDecimal(x.getGoodsScore()), new BigDecimal(order.getScoreProportion()))));
                } }
             );
        }
	}

    /**
	 * mp订单操作设置
	 * @param order
	 */
	private void setMpOrderOperation(OrderListMpVo order) {
		//1.延长收货
		order.setIsExtendReceive(OrderOperationJudgment.isExtendReceive(order, getExtendReceiveDays()) ? YES : NO);
		//2.确认收货(order_status==4可以判断)
		//3.好友代付（order_pay_way == 2）;好友代付与立即支付关联加入到4处理
		//4.待支付状态处理order_status==0 => 去付尾款(bk_order_paid == 1) 、 去付款
		if(order.getOrderStatus() == OrderConstant.ORDER_WAIT_PAY) {
			setPayOperation(order);
		}
		//5.(退货中心-退款 退货) 6. 取消 7删除
		OrderOperationJudgment.operationSet(order);
		//8.再次购买
		order.setIsShowAgainBuy(OrderOperationJudgment.isShowAgainBuy(order) ? YES : NO);
		//9.提醒发货
		order.setIsRemindShip(OrderOperationJudgment.isShowRemindShip(order) ? YES : NO);
		//10.评价（查看评价、评价有礼/商品评价）
		order.setIsShowCommentType(getCommentType(order));
		//好友代付
        if(order.getOrderPayWay().equals(OrderConstant.PAY_WAY_FRIEND_PAYMENT)) {
            order.setPayOperationTime(order.getExpireTime().getTime() - Instant.now().toEpochMilli());
            order.setIsShowFriendPay(order.getPayOperationTime() > 0 ? YES : NO);
        }
		//TODO 幸运大抽奖 分享优惠卷。。。。
		/**按钮-end*/
	}

	/**
	 * 奖品订单
	 * @param orderType
	 */
	private boolean isAwardOrder(List<Byte> orderType) {
		for (Byte type : orderType) {
			if(OrderConstant.AWARD_ORDER.contains(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 订单待支付状态处理时间（包括预售、定金）
	 * @param order
	 */
	private void setPayOperation(OrderListMpVo order) {
		long currenTmilliseconds  = Instant.now().toEpochMilli();
		if(order.getBkOrderPaid() == OrderConstant.BK_PAY_FRONT) {
            //补款设置时间与补款是否可支付
            setBkPayOperation(order);
		} else if(order.getOrderPayWay().equals(OrderConstant.PAY_WAY_FRIEND_PAYMENT)) {
		    //好友代付在外层处理
        } else {
			//普通订单待支付取消时间
			order.setPayOperationTime(order.getExpireTime().getTime() - currenTmilliseconds);
            order.setIsShowPay(order.getPayOperationTime() > 0 ? YES : NO);
		}
	}

	/**
	 * 当前订单配送信息
	 * @param orderSn
	 * @param goods
	 * @return
	 */
	public List<ShippingInfoVo> getMpOrderShippingInfo(String orderSn , Map<Integer, OrderGoodsMpVo> goods){
		Map<String, List<ShippingInfoVo>> shippingMap = shipInfo.getShippingByOrderSn(Collections.singletonList(orderSn));
		List<ShippingInfoVo> result = shippingMap.get(orderSn);
		if(CollectionUtils.isEmpty(result)) {
			return null;
		}else {
            List<Byte> collect = result.stream().map(ShippingInfoVo::getShippingId).collect(Collectors.toList());
            Map<Byte, ExpressVo> express = expressService.gets(collect);
            result.forEach(x -> {
                if(express.get(x.getShippingId()) != null && !StringUtils.isBlank(express.get(x.getShippingId()).getShippingName())){
                    x.setShippingName(express.get(x.getShippingId()).getShippingName());
                }else{
                    //正常情况不会出现这种情况
                    x.setShippingName(StringUtils.EMPTY);
                }
				x.getGoods().forEach(y->{
					y.setGoodsImg(goods.get(y.getOrderGoodsId()).getGoodsImg());
					y.setGoodsPrice(goods.get(y.getOrderGoodsId()).getGoodsPrice());
				});
			});
		}
		return result;
	}

	/**
	 * 延长收货
	 * @return
	 */
	public Integer getExtendReceiveDays() {
		Byte switchFlag = trade.getExtendReceiveGoods();
		if(switchFlag == 0) {
			return 0;
		}
		Integer days = trade.getExtendReceiveDays();
		return days;
	}

	/**
	 * 子订单
	 * @param subOrder
	 * @return
	 */
	public List<OrderInfoMpVo> getSubOrder(List<OrderInfoMpVo> subOrder) {
		List<Integer> ids = subOrder.stream().map(OrderListMpVo::getOrderId).collect(Collectors.toList());
		Map<Integer, List<OrderGoodsMpVo>> subOrderGoods = orderGoods.getByOrderIds(ids.toArray(new Integer[ids.size()])).intoGroups(orderGoods.TABLE.ORDER_ID,OrderGoodsMpVo.class);
		subOrder.forEach(x->x.setGoods(subOrderGoods.get(x.getOrderId())));
		return subOrder;

	}

	/**
	 * 小程序展示评价相关
	 * @param order
	 * @return 0不展示 1查看评价 2评价有礼 3商品评价
	 */
	public Byte getCommentType(OrderListMpVo order){
		if(order.getOrderStatus() != OrderConstant.ORDER_RECEIVED &&  order.getOrderStatus() != OrderConstant.ORDER_FINISHED) {
			//0不展示
			return NO;
		}
		if(order.getCommentFlag() > 0) {
			//1查看评价
			return 1;
		}
		List<OrderGoodsMpVo> goods = order.getGoods();
		List<CommentListVo> converGoods = new ArrayList<CommentListVo>();
		//转化类型
		goods.forEach(x->converGoods.add(new CommentListVo(x.getGoodsId())));
		if(goodsComment.orderIsCommentAward(converGoods)) {
			//2评价有礼
			return 2;
		}
		//3商品评价
		return 3;
	}

	/**
	 * 统计订单各个状态的数量(个人中心用)
	 * @param userId
	 * @return
	 */
	public Map<Byte, Integer> statistic(Integer userId) {
        int returnCount = returnOrder.getReturnOrderCount(userId, null);
        return mpOrderInfo.getOrderStatusNum(userId, null, false, returnCount);
	}

    /**
     * 小程序端订单列表/详情点击售后中心展示数据(曾经退过)
     * @param param
     */
    public AfterSaleServiceVo mpOrderReturnList(OrderParam param) throws MpException {
        AfterSaleServiceVo vo = new AfterSaleServiceVo();
        OrderInfoRecord order = orderInfo.getOrderByOrderSn(param.getOrderSn());
        vo.setOrderSn(order.getOrderSn());
        vo.setCreateTime(order.getCreateTime());
        //展示退款操作页面
        OrderOperateQueryParam query = new OrderOperateQueryParam();
        query.setIsMp(OrderConstant.IS_MP_Y);
        query.setOrderSn(order.getOrderSn());
        query.setOrderId(order.getOrderId());
        RefundVo operateInfo = (RefundVo) returnService.query(query);
        if(operateInfo != null && CollectionUtils.isNotEmpty(operateInfo.getRefundGoods())){
            vo.setReturnFlag(YES);
        }else {
            vo.setReturnFlag(NO);
        }
        //获取已退运费
        BigDecimal returnShipingFee = returnOrder.getReturnShippingFee(param.getOrderSn());
        //退运费校验
        if(OrderOperationJudgment.adminIsReturnShipingFee(order.getShippingFee(), returnShipingFee, true)){
            vo.setCanReturnShippingFee(order.getShippingFee().subtract(returnShipingFee));
        }else {
            vo.setCanReturnShippingFee(BigDecimal.ZERO);
        }
        //退款记录
        Result<ReturnOrderRecord> rOrders = returnOrder.getRefundByOrderSn(param.getOrderSn());
        //买家提交物流快递名称

        vo.setReturnOrderlist(new ArrayList<>(rOrders.size()));
        rOrders.forEach(rOrder->{
            ReturnOrderListMp returnOrderListMp = rOrder.into(ReturnOrderListMp.class);
            //买家；商家（包含定时任务）
                ReturnStatusChangeRecord lastOperator = returnStatusChange.getLastOperator(rOrder.getRetId());
                returnOrderListMp.setRole(OrderConstant.IS_MP_Y == lastOperator.getType() ? OrderConstant.IS_MP_Y : OrderConstant.IS_MP_ADMIN);
                returnOrderListMp.setFinishTime(lastOperator.getCreateTime());
                if(!StringUtils.isBlank(returnOrderListMp.getShippingType())) {
                    ExpressVo expressVo = expressService.get(Byte.valueOf(returnOrderListMp.getShippingType()));
                    returnOrderListMp.setShippingName(expressVo == null ? null : expressVo.getShippingName());
                }
                vo.getReturnOrderlist().add(returnOrderListMp);
        });
        return vo;
    }

    /**
     * 小程序售后中心
     * @param param
     * @return
     */
    public PageResult<ReturnOrderListMp> mpReturnList(OrderListParam param) {
        PageResult<ReturnOrderListMp> result = returnOrder.getPageList(param);
        List<Integer> collect;
        List<ReturnOrderListMp> dataList = result.dataList;
        if(dataList != null && dataList.size() > 0 ) {
            collect = dataList.stream().map(ReturnOrderListMp::getRetId).collect(Collectors.toList());
        }else {
            return result;
        }
        //获取订单再分组
            Map<Integer, List<OrderReturnGoodsVo>> goods = returnOrderGoods.getByRetIds(collect.toArray(new Integer[]{})).intoGroups(returnOrderGoods.TABLE.RET_ID,OrderReturnGoodsVo.class);
        for (ReturnOrderListMp order : dataList) {
            order.setGoods(goods.get(order.getRetId()));
        }
        return result;
    }

    /**
     * 获取赠品订单数
     * @param giftId 赠品id
     * @param isIncludeReturn 是否包含退款赠品
     */
    public Integer getGiftOrderCount(Integer giftId, boolean isIncludeReturn){
        List<String> giftOrderSns = orderGoods.getGiftOrderSns(giftId, isIncludeReturn);
        return orderInfo.getGiftOrderCount(giftOrderSns);
    }

    /**
     * 	获取该退款订单物流code(快递100对应code)
     * @param returnOrder
     * @return
     */
    private ExpressVo getShippingInfo(ReturnOrderRecord returnOrder) {
        if(!StringUtils.isBlank(returnOrder.getShippingType())) {
            return expressService.get(Byte.valueOf(returnOrder.getShippingType()));
        }else {
            return null;
        }
    }

    public InsteadPayOrderDetails insteadPayInfo(InsteadPayDetailsParam param) throws MpException {
        InsteadPayOrderDetails result = new InsteadPayOrderDetails();
        //订单
        OrderInfoMpVo order = mpGet(param);
        result.setOrder(order);
        //代付信息
        PageResult<InsteadPayDetailsVo> insteadPayDetailsVoPageResult = subOrderService.paymentDetails(param.getOrderSn(), param.getCurrentPage(), param.getPageRows());
        result.setInsteadPayDetails(insteadPayDetailsVoPageResult);
        //订单
        OrderInfoRecord orderRecord = orderInfo.getOrderByOrderSn(param.getOrderSn());
        //获取已付金额
        result.setAmountPaid(orderInfo.getOrderFinalAmount(orderRecord.into(OrderListInfoVo.class), true));
        //待支付金额
        result.setWaitPayMoney(BigDecimalUtil.subtrac(orderRecord.getInsteadPayMoney(), orderRecord.getMoneyPaid()));
        //代付配置
        result.setInsteadPayCfg(Util.parseJson(orderRecord.getInsteadPay(), InsteadPay.class));
        //是否本人
        result.setIsSelf(param.getWxUserInfo().getUserId().equals(orderRecord.getUserId()) ? OrderConstant.YES : OrderConstant.NO);
        //默认消息
        result.setMessage(orderRecord.getInsteadPayNum() == 0 ? result.getInsteadPayCfg().getOrderUserMessageMultiple() : result.getInsteadPayCfg().getOrderUserMessageSingle());
        //订单拥有者
        result.setUserInfo(user.getUserInfo(orderRecord.getUserId()));
        return result;
    }

    /**
     * admin订单详情展示分销信息
     * @param order
     * @return
     */
    private List<OrderRebateVo> getOrderRebateInfo(OrderInfoVo order) {
        if(order.getFanliType() != null && order.getFanliType() > OrderConstant.FANLI_TYPE_DEFAULT) {
            List<OrderRebateVo> rebateVos = orderGoodsRebate.getByOrderSn(order.getOrderSn());
            for (OrderRebateVo vo: rebateVos) {
                if(order.getSettlementFlag() != null && order.getSettlementFlag().equals(OrderConstant.SETTLEMENT_NOT)) {
                    vo.setRebateTotalMoney(BigDecimalUtil.BIGDECIMAL_ZERO);
                    vo.setRealRebateMoney(BigDecimalUtil.BIGDECIMAL_ZERO);
                    vo.setCanRebateTotalMoney(BigDecimalUtil.BIGDECIMAL_ZERO);
                }else {
                    vo.setRebateTotalMoney(BigDecimalUtil.multiply(vo.getGoodsPrice(), new BigDecimal(vo.getGoodsNumber() - vo.getReturnNumber())));
                    vo.setCanRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                        BigDecimalUtil.BigDecimalPlus.create(vo.getCanCalculateMoney(), BigDecimalUtil.Operator.multiply),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(vo.getGoodsNumber() - vo.getReturnNumber()), BigDecimalUtil.Operator.divide),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(vo.getGoodsNumber())))
                    );
                    if(vo.getRealRebateMoney() != null) {
                        vo.setRealRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                            BigDecimalUtil.BigDecimalPlus.create(vo.getTotalRebateMoney(), BigDecimalUtil.Operator.multiply),
                            BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(vo.getGoodsNumber() - vo.getReturnNumber()), BigDecimalUtil.Operator.divide),
                            BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(vo.getGoodsNumber())))
                        );
                    }
                }
                vo.setCostPrice(BigDecimalUtil.multiply(vo.getCostPrice(), BigDecimalUtil.valueOf(vo.getGoodsNumber() - vo.getReturnNumber())));
            }
            List<Integer> rebateUserIds = rebateVos.stream().map(OrderRebateVo::getRebateUserId).distinct().collect(Collectors.toList());
            //分销员真实姓名展示优先级：提现申请填写信息>成为分销员申请表填写信息>用户信息
            HashMap<Integer, String> name = new HashMap<>(rebateUserIds.size());
            for (Integer userId: rebateUserIds ) {
                //提现申请填写信息
                String realName = saas().getShopApp(getShopId()).withdraw.getUserRealName(userId);
                if(!StringUtils.isBlank(realName)) {
                    name.put(userId, realName);
                    continue;
                }
                if(StringUtils.isBlank(realName)) {
                    //成为分销员申请表填写信息
                    realName = saas().getShopApp(getShopId()).mpDistribution.getDistributorRealName(userId);
                    if(!StringUtils.isBlank(realName)) {
                        name.put(userId, realName);
                        continue;
                    }
                }
                if(StringUtils.isBlank(realName)) {
                    //用户信息
                    UserInfo userInfo = user.getUserInfo(userId);
                    if(userInfo != null && !StringUtils.isBlank(userInfo.getRealName())) {
                        name.put(userId, userInfo.getRealName());
                    }
                }
            }
            //set realname
            rebateVos.forEach(x->x.setRealName(name.get(x.getRebateUserId())));
            //排序
            rebateVos.sort(Comparator.comparing(OrderRebateVo::getRebateUserId));
            return rebateVos;
        }
        return null;
    }


    public PageResult<BatchShipListVo> batchShipList(BatchShipListParam param) {
        PageResult<BatchShipListVo> result = batchRecord.batchShipList(param);
        if(CollectionUtils.isEmpty(result.dataList)) {
            return result;
        }
        List<Integer> sysIds = result.dataList.stream().filter(x -> x.getSysId() != null && x.getSysId() > 0).map(BatchShipListVo::getSysId).collect(Collectors.toList());
        List<Integer> accountIds = result.dataList.stream().filter(x -> x.getAccountId() != null && x.getAccountId() > 0).map(BatchShipListVo::getAccountId).collect(Collectors.toList());
        Map<Integer, ShopManageVo> sys = shopAccount.getByIds(sysIds).stream().collect(Collectors.toMap(ShopManageVo::getSysId, Function.identity()));
        Map<Integer, SystemChildAccountRecord> account = childAccount.getByAccountIds(accountIds).stream().collect(Collectors.toMap(SystemChildAccountRecord::getAccountId, Function.identity()));
        result.dataList.forEach(
            x->{
                if(x.getSysId() != null && x.getSysId() > 0) {
                    ShopManageVo vo = sys.get(x.getSysId());
                    if(vo != null) {
                        x.setUserName(vo.getUserName());
                        x.setMobile(vo.getMobile());
                    }
                }else if(x.getAccountId() != null && x.getAccountId() > 0) {
                    SystemChildAccountRecord vo = account.get(x.getAccountId());
                    if(vo != null) {
                        x.setChildUserName(vo.getAccountName());
                        x.setChildMobile(vo.getMobile());
                    }
                }
            }
        );
        return result;
    }

    public Workbook downloadFailData(Integer batchId, String lang) {
        List<BatchShipFailModel> data = batchDetailRecord.getFailDataByBatchId(batchId);
        for (BatchShipFailModel vo : data) {
            String messages = Util.translateMessage(lang, vo.getFailReason(), null, "messages");
            vo.setFailReason(messages);
        }
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(data, BatchShipFailModel.class);
        return workbook;
    }
    /**
     * api单个订单
     * @param gateParam
     * @return
     */
    public ApiJsonResult apiGet(ApiExternalGateParam gateParam) {
        ApiOrderQueryParam param = Util.parseJson(gateParam.getContent(), ApiOrderQueryParam.class);
        ApiJsonResult result = new ApiJsonResult();
        if (param == null) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("content为空");
            return result;
        }
        if (StringUtils.isBlank(param.getOrderSn())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数order_sn为空");
            return result;
        }
        ApiPageResult<ApiOrderListVo> pageResult = getApiOrderListVos(param);
        if(CollectionUtils.isEmpty(pageResult.getDataList())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("查无此单");
            return result;
        }
        return new ApiJsonResult(pageResult.getDataList().get(0));
    }

    /**
     * api订单列表
     * @param gateParam
     * @return
     */
    public ApiJsonResult getPageList(ApiExternalGateParam gateParam) {
        ApiOrderQueryParam param = Util.parseJson(gateParam.getContent(), ApiOrderQueryParam.class);
        if (param == null) {
            param = new ApiOrderQueryParam();
        }
        param.setOrderSn(null);
        ApiPageResult<ApiOrderListVo> pageResult = getApiOrderListVos(param);
        ApiOrderPageResult result = new ApiOrderPageResult();
        result.setCurPageNo(pageResult.getCurPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalOrderCount(pageResult.getTotalCount());
        result.setOrderList(pageResult.getDataList());
        return new ApiJsonResult(result);
    }

    /**
     * api退款订单列表
     * @param gateParam
     * @return
     */
    public ApiJsonResult getReturnPageList(ApiExternalGateParam gateParam) {
        ApiBasePageParam param = Util.parseJson(gateParam.getContent(), ApiBasePageParam.class);
        if (param == null) {
            param = new ApiOrderQueryParam();
        }
        ApiPageResult<ApiReturnOrderListVo> pageResult = returnOrder.getPageList(param, ApiReturnOrderListVo.class);
        List<ApiReturnOrderListVo> dataList = pageResult.getDataList();
        //设置退款商品
        setReturnGoods(dataList);
        //erp需要的一些数值转化
        dataTransform(dataList);
        ApiReturnOrderPageResult result = new ApiReturnOrderPageResult();
        result.setCurPageNo(pageResult.getCurPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalOrderCount(pageResult.getTotalCount());
        result.setOrderList(pageResult.getDataList());
        return new ApiJsonResult(result);
    }

    private ApiPageResult<ApiOrderListVo> getApiOrderListVos(ApiOrderQueryParam param) {
        ApiPageResult<ApiOrderListVo> pageResult = orderInfo.getOrders(param, ApiOrderListVo.class);
        //订单
        List<ApiOrderListVo> orders = pageResult.getDataList();
        //快递公司
        Map<Byte, ExpressVo> expressMap = expressService.getAll().stream().collect(Collectors.toMap(ExpressVo::getShippingId, Function.identity()));
        //订单信息转化
        orderDataTransform(orders);
        //设置快递公司名称
        setShippingName(orders, expressMap);
        //用户手机号昵称
        setUserInfo(orders);
        //下单真实信息
        setOrderMustInfo(orders);
        //发票
        setInvoiceInfo(orders);
        //商品
        setGoodsInfo(orders, expressMap);
        //退款信息
        setRetuenInfo(orders, expressMap);
        return pageResult;
    }

    /**
     * set ShippingName
     * @param orders
     * @param expressMap
     */
    private void setShippingName(List<ApiOrderListVo> orders, Map<Byte, ExpressVo> expressMap) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        orders.forEach(x->{
            ExpressVo vo = expressMap.get(x.getShippingId());
            if(vo != null) {
                x.setShippingName(vo.getShippingName());
            }
        });
    }

    /**
     * set username、Mobile
     * @param orders
     */
    private void setUserInfo(List<ApiOrderListVo> orders) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        List<UserRecord> users = member.getUserRecordByIds(orders.stream().map(ApiOrderListVo::getUserId).collect(Collectors.toList()));
        Map<Integer, UserRecord> usersMap = users.stream().collect(Collectors.toMap(UserRecord::getUserId, Function.identity()));
        orders.forEach(x->{
            UserRecord vo = usersMap.get(x.getUserId());
            if(vo != null) {
                x.setUsername(StringUtils.join(vo.getMobile() , vo.getUsername()));
            }
        });
    }

    /**
     * set OrderMustInfo
     * @param orders
     */
    private void setOrderMustInfo(List<ApiOrderListVo> orders) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        List<OrderMustVo> orderMustInfo = orderMust.getOrderMustByOrderSns(orders.stream().map(ApiOrderListVo::getOrderSn).collect(Collectors.toList()));
        Map<String, OrderMustVo> orderMustMap = orderMustInfo.stream().collect(Collectors.toMap(OrderMustVo::getOrderSn, Function.identity()));
        orders.forEach(x->{
            OrderMustVo vo = orderMustMap.get(x.getOrderSn());
            if(vo != null) {
                x.setOrderRealName(vo.getOrderRealName());
                x.setOrderCid(vo.getOrderCid());
                x.setConsigneeRealName(vo.getConsigneeRealName());
                x.setConsigneeCid(vo.getConsigneeCid());
            }
        });
    }

    /**
     * set InvoiceInfo TODO 2.14新加发票测试后删除TODO
     * @param orders
     */
    private void setInvoiceInfo(List<ApiOrderListVo> orders) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        orders.forEach(
            x->{
                if(!StringUtils.isBlank(x.getInvoiceTitle())) {
                    x.setInvoiceInfo(Util.parseJson(x.getInvoiceTitle(), InvoiceVo.class));
                }
            }
        );
    }

    /**
     * setGoodsInfo
     * @param orders
     * @param expressMap
     */
    private void setGoodsInfo(List<ApiOrderListVo> orders, Map<Byte, ExpressVo> expressMap) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        Map<String, List<ApiOrderGoodsListVo>> goodsInfo = orderGoods.getGoodsByOrderSns(orders.stream().map(ApiOrderListVo::getOrderSn).collect(Collectors.toList()));
        //发货信息
        Map<String, List<ShippingInfoVo>> shippingInfo = shipInfo.getShippingByOrderSn(orders.stream().map(ApiOrderListVo::getOrderSn).collect(Collectors.toList()));
        for (ApiOrderListVo order: orders) {
            //商品
            List<ApiOrderGoodsListVo> goods = goodsInfo.get(order.getOrderSn());
            //积分兑换价格转化
            goods.forEach(x->{
                if(x.getGoodsScore() != null && x.getGoodsScore() > 0) {
                    x.setGoodsPrice(x.getDiscountedGoodsPrice());
                }
            });
            order.setOrderGoodsInfo(goods);
            //商品设置配送信息
            List<ShippingInfoVo> goodsShipping = shippingInfo.get(order.getOrderSn());
            if (CollectionUtils.isNotEmpty(goodsShipping)) {
                for (ShippingInfoVo shippingInfoVo : goodsShipping) {
                    List<Integer> recIds = shippingInfoVo.getGoods().stream().map(ShippingInfoVo.Goods::getOrderGoodsId).collect(Collectors.toList());
                    for(ApiOrderGoodsListVo goodsVo : goods) {
                        if(goodsVo.getSendNumber() > 0 && recIds.contains(goodsVo.getRecId())) {
                            goodsVo.setShippingNo(shippingInfoVo.getShippingNo());
                            goodsVo.setShippingTime(shippingInfoVo.getShippingTime());
                            ExpressVo expressVo = expressMap.get(shippingInfoVo.getShippingId());
                            goodsVo.setShippingName(expressVo == null ? null : expressVo.getShippingName());
                        }
                    }
                }
            }
        }
    }

    /**
     * set RetuenInfo
     * @param orders
     * @param expressMap
     */
    private void setRetuenInfo(List<ApiOrderListVo> orders, Map<Byte, ExpressVo> expressMap) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        Map<String, List<ApiReturnOrderListVo>> returnOrderInfo = returnOrder.getOrderByOrderSns(orders.stream().map(ApiOrderListVo::getOrderSn).collect(Collectors.toList()));

        for (ApiOrderListVo order : orders) {
            List<ApiReturnOrderListVo> returnOrders = returnOrderInfo.get(order.getOrderSn());
            if(CollectionUtils.isEmpty(returnOrders)) {
                continue;
            }
            order.setReturnInfo(returnOrders);
            for (ApiReturnOrderListVo ro : returnOrders) {
                ro.setReason(OrderConstant.getReturnReasonDesc(ro.getReasonType().intValue()));
                String shippingName = null;
                if(!StringUtils.isBlank(ro.getShippingType())) {
                    ExpressVo expressVo = expressMap.get(Byte.parseByte(ro.getShippingType()));
                    shippingName = expressVo == null ? null : expressVo.getShippingName();
                }
                ro.setShippingName(shippingName);
                if(!StringUtils.isBlank(ro.getGoodsImages())) {
                    ArrayList goodsImagesArray = Util.parseJson(ro.getGoodsImages(), ArrayList.class);
                    if(CollectionUtils.isNotEmpty(goodsImagesArray)) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < goodsImagesArray.size(); i++) {
                            sb.append(imageUrl(goodsImagesArray.get(i).toString()));
                            if(i != goodsImagesArray.size() -1) {
                                sb.append(",");
                            }
                        }
                        ro.setGoodsImages(sb.toString());
                    }
                }
                if(!StringUtils.isBlank(ro.getVoucherImages())) {
                    ArrayList voucherImagesArray = Util.parseJson(ro.getVoucherImages(), ArrayList.class);
                    if(CollectionUtils.isNotEmpty(voucherImagesArray)) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < voucherImagesArray.size(); i++) {
                            sb.append(imageUrl(voucherImagesArray.get(i).toString()));
                            if(i != voucherImagesArray.size() -1) {
                                sb.append(",");
                            }
                        }
                        ro.setVoucherImages(sb.toString());
                    }
                }
            }
        }
    }

    /**
     * setReturnGoods
     * @param dataList
     */
    private void setReturnGoods(List<ApiReturnOrderListVo> dataList) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        Map<Integer, List<ApiReturnGoodsListVo>> refundGoods = returnOrderGoods.getByOrderSn(dataList.stream().map(ApiReturnOrderListVo::getOrderSn).toArray(String[]::new)).intoGroups(returnOrderGoods.TABLE.RET_ID, ApiReturnGoodsListVo.class);
        dataList.forEach(x->x.setReturnGoodsList(refundGoods.get(x.getRetId())));
    }

    /**
     * data Transform
     * @param dataList
     */
    private void dataTransform(List<ApiReturnOrderListVo> dataList) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        //获取退款订单对于的订单
        Map<String, List<OrderListInfoVo>> orders = orderInfo.getOrders(dataList.stream().map(ApiReturnOrderListVo::getOrderSn).collect(Collectors.toList()));
        for (ApiReturnOrderListVo returnOrderVo : dataList) {
            //售前、售后状态
            List<OrderListInfoVo> orderListInfoVos = orders.get(returnOrderVo.getOrderSn());
            if(CollectionUtils.isNotEmpty(orderListInfoVos)) {
                Timestamp confirmTime = orderListInfoVos.get(0).getConfirmTime();
                if(confirmTime != null && returnOrderVo.getCreateTime().after(confirmTime)) {
                    returnOrderVo.setAfterSales((byte)2);
                }else {
                    returnOrderVo.setAfterSales((byte)1);
                }
            }
            returnOrderVo.setAfterSales(null);
            //erp退款状态转化
            returnOrderVo.setErpRefundStatus(OrderConstant.getErpReturnStatus(returnOrderVo.getRefundStatus(), !StringUtils.isBlank(returnOrderVo.getShippingNo())));
        }
    }

    /**
     * data Transform
     * @param dataList
     */
    private void orderDataTransform(List<ApiOrderListVo> dataList) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        for (ApiOrderListVo order : dataList) {
            order.setDiscountAmount(
                BigDecimalUtil.addOrSubtrac(
                    BigDecimalUtil.BigDecimalPlus.create(order.getDiscount(), BigDecimalUtil.Operator.add),
                    BigDecimalUtil.BigDecimalPlus.create(order.getScoreDiscount(), BigDecimalUtil.Operator.add),
                    BigDecimalUtil.BigDecimalPlus.create(order.getPromotionReduce(), BigDecimalUtil.Operator.add),
                    BigDecimalUtil.BigDecimalPlus.create(order.getPackageDiscount(), BigDecimalUtil.Operator.add),
                    BigDecimalUtil.BigDecimalPlus.create(order.getGrouperCheapReduce(), BigDecimalUtil.Operator.add),
                    BigDecimalUtil.BigDecimalPlus.create(order.getMemberCardReduce())
                ));
        }
    }

    /**
     * 展示退款失败信息
     * @param vo
     */
    private void showRefundFailInfo(ReturnOrderInfoVo vo) {
        if(orderRefundRecord.isExistFail(vo.getRetId())) {
            List<OrderRefundRecordRecord> successRecords = orderRefundRecord.getSuccessRecord(vo.getRetId());
            BigDecimal successMoney = successRecords.stream().map(OrderRefundRecordRecord::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimalUtil::add);
            vo.setShowRefundFailInfo(YES);
            vo.setSuccessMoney(successMoney);
            vo.setFailMoney(BigDecimalUtil.addOrSubtrac(
                BigDecimalUtil.BigDecimalPlus.create(vo.getMoney(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(vo.getShippingFee(), BigDecimalUtil.Operator.subtrac),
                BigDecimalUtil.BigDecimalPlus.create(successMoney)
            ));
            vo.setFailDesc(orderRefundRecord.getFailRecord(vo.getRetId()).getRemark1());
        }
    }
    /*********************************************************************************************************/

	/**
	 * 分裂营销活动的活动数据分析的订单部分数据
	 * @param param
	 * @return
	 */
	 public Map<Date,Integer> getMarketOrderAnalysis(MarketAnalysisParam param){
		 return marketOrderInfo.getMarketOrderAnalysis(param);
	 }


	/**
	 *
	 *	活动实付总金额 活动优惠总金额
	 * @param goodType
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<ActiveDiscountMoney> getActiveDiscountMoney(Byte goodType, Integer activityId, Timestamp startTime, Timestamp  endTime){
		return marketOrderInfo.getActiveDiscountMoney(goodType, activityId, startTime, endTime);
	}

	/**
	 *
	 *  活动新用户订单
	 *
	 *  @param goodType
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public ActiveOrderList getActiveOrderList(Byte goodType, Integer activityId, Timestamp startTime, Timestamp  endTime) {
        if (BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(goodType)) {
            //限时降价的订单
            return marketOrderInfo.getActiveOrderList2(goodType, activityId, startTime, endTime);
        }
        return marketOrderInfo.getActiveOrderList(goodType, activityId, startTime, endTime);
    }

	 /**
     * 营销活动订单查询
     *
     * @param param
      * @param goodsType 参考OrderConstant类中的常量
     * @return
     */
    public PageResult<MarketOrderListVo> getMarketOrderList(MarketOrderListParam param, byte goodsType) {
        return marketOrderInfo.getMarketOrderPageList(param,goodsType);
    }

    /**
     * 订单导出数据的条数
     * @param param
     * @return
     */
    public int getExportOrderListSize(OrderExportQueryParam param) {
        return orderInfo.getExportOrderListSize(param);
    }

    /**
     *	 订单列表导出
     * @param param
     * @param lang
     * @return workbook
     */
    public Workbook exportOrderList(OrderExportQueryParam param,List<String> columns, String lang) {
        List<OrderExportVo> orderList = orderInfo.getExportOrderList(param);

        //循环处理需要处理的列
        for(OrderExportVo order : orderList){
            if(columns.contains(OrderExportVo.IS_NEW)){
                //是否新用户
                if(orderInfo.getUserOrderNumber(order.getUserId(),null,order.getCreateTime()) > 0){
                    order.setIsNew(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_REGULAR_USER ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
                }else {
                    order.setIsNew(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_NEW_USER ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
                }
            }
            processPayName(columns, lang, order);

            processOrderAndGoods(columns, lang, order);

            processOther(columns, lang, order);
        }

        //返利金额列特殊处理
        if(columns.contains(OrderExportVo.REBATE)){
            columns.remove(OrderExportVo.REBATE);
            columns.add(OrderExportVo.REBATE_LEVEL_ONE);
            columns.add(OrderExportVo.REBATE_LEVEL_TWO);
        }

        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(orderList, OrderExportVo.class,columns);
        return workbook;
    }

    private void processOther(List<String> columns, String lang, OrderExportVo order) {
        if(columns.contains(OrderExportVo.CUSTOM)){
            //下单必填信息
            OrderMustVo orderMustVo = orderMust.getOrderMustByOrderSn(order.getOrderSn());
            if(orderMustVo != null){
                orderMustVo.setLang(lang);
                order.setCustom(orderMustVo.toString());
            }
        }
        if(columns.contains(OrderExportVo.USER_SOURCE)){
            //下单用户来源
            UserRecord userRecord = user.getUserByUserId(order.getUserId());
            if(userRecord != null){
                MemberInfoVo memberInfo = userRecord.into(MemberInfoVo.class);
                order.setUserSourceString(saas.getShopApp(getShopId()).member.getSourceName(lang,memberInfo));
            }
        }
        if(columns.contains(OrderExportVo.DELIVER_TYPE_NAME)){
            //配送类型
            switch (order.getDeliverType()){
                case OrderConstant.DELIVER_TYPE_COURIER:
                    order.setDeliverTypeName(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_DELIVER_TYPE_COURIER ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
                    break;
                case OrderConstant.DELIVER_TYPE_SELF:
                    order.setDeliverTypeName(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_DELIVER_TYPE_SELF ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
                    break;
                case OrderConstant.CITY_EXPRESS_SERVICE:
                    order.setDeliverTypeName(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_CITY_EXPRESS_SERVICE ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
                    break;
                default:
            }
        }
        if(columns.contains(OrderExportVo.USER_TAG)){
            //用户标签
            List<TagVo> tagList = saas.getShopApp(getShopId()).member.getTagForMember(order.getUserId());
            StringBuffer tags = new StringBuffer();
            for(TagVo tag : tagList){
                tags.append(tag.getTagName()).append(";");
            }
            order.setUserTag(tags.toString());
        }
        if(columns.contains(OrderExportVo.RETURN_TIME)){
            //退款时间
            ReturnOrderGoodsRecord returnOrderGoodsRecord = returnOrderGoods.getByRecId(order.getRecId());
            if(returnOrderGoodsRecord != null){
                order.setReturnTime(returnOrderGoodsRecord.getCreateTime());
                order.setReturnOrderMoney(returnOrderGoodsRecord.getReturnMoney());
            }
        }
        if(columns.contains(OrderExportVo.SHIPPING_NAME)){
            //发货时间
            if(order.getShippingId() != null && order.getShippingId() > 0){
                order.setShippingName(expressService.get(order.getShippingId()).getShippingName());
            }
        }
        if(columns.contains(OrderExportVo.REBATE)) {
            //返利金额，最多有两级
            Result<OrderGoodsRebateRecord> orderRebate = orderGoodsRebate.get(order.getOrderSn(), order.getRecId());
            if (orderRebate.size() == DistributionConstant.REBATE_LEVEL_1) {
                order.setRebateLevelOne(orderRebate.get(0).getRebateMoney());
                order.setRebateLevelTwo(orderRebate.get(1).getRebateMoney());
            } else if (orderRebate.size() == 1) {
                order.setRebateLevelOne(orderRebate.get(0).getRebateMoney());
            }
        }
    }

    private void processOrderAndGoods(List<String> columns, String lang, OrderExportVo order) {
        if(columns.contains(OrderExportVo.PRD_COST_PRICE)){
            //成本价
            order.setPrdCostPrice(saas.getShopApp(getShopId()).goods.goodsPrice.getCostPrice(order.getProductId()));
        }
        if(columns.contains(OrderExportVo.PRD_WEIGHT)){
            //规格重量（暂时取商品重量）
            order.setPrdWeight(saas.getShopApp(getShopId()).goods.getGoodsWeightById(order.getGoodsId()));
        }
        if(columns.contains(OrderExportVo.ORDER_STATUS_NAME)){
            //订单状态
            order.setOrderStatusName(OrderConstant.getOrderStatusName(order.getOrderStatus(),lang));
        }
        if(order.getPartShipFlag() == OrderConstant.PART_SHIP){
            //部分发货
            BaseShippingInfoVo shipping = shipInfo.getOrderGoodsShipping(order.getOrderSn(),order.getRecId());
            if(shipping != null){
                if(shipping.getConfirmTime() != null){
                    order.setOrderStatusName(OrderConstant.getOrderStatusName(OrderConstant.ORDER_RECEIVED,lang));
                }else{
                    order.setOrderStatusName(OrderConstant.getOrderStatusName(OrderConstant.ORDER_WAIT_DELIVERY,lang));
                }
                order.setShippingTime(shipping.getShippingTime());
                order.setShippingName(shipping.getShippingName());
                order.setShippingNo(shipping.getShippingNo());
            }else{
                order.setShippingTime(null);
                order.setShippingName("");
                order.setShippingNo("");
            }
        }
        if(columns.contains(OrderExportVo.RETURN_SHIPPING_FEE)){
            //退运费
            order.setReturnShippingFee(returnOrder.getReturnShippingFee(order.getOrderSn()));
        }
        if(columns.contains(OrderExportVo.RETURN_TIME) || columns.contains(OrderExportVo.RETURN_FINISH_TIME) || columns.contains(OrderExportVo.RETURN_ORDER_MONEY)){
            //退货退款信息
            OrderConciseRefundInfoVo returnInfo = returnOrderGoods.getOrderGoodsReturnInfo(order.getRecId());
            if(returnInfo != null){
                order.setReturnTime(OrderConstant.RT_ONLY_MONEY == returnInfo.getReturnType() ? returnInfo.getApplyTime() : returnInfo.getShippingOrRefundTime());
                order.setReturnFinishTime(returnInfo.getRefundSuccessTime());
                order.setReturnOrderMoney(returnOrderGoods.getReturnGoodsMoney(order.getRecId()));
            }
        }
        if(columns.contains(OrderExportVo.IS_COD)){
            //是否货到付款
            order.setIsCodString(OrderConstant.IS_COD_YES.equals(order.getIsCod()) ? Util.translateMessage(lang, JsonResultMessage.YES ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL) : Util.translateMessage(lang, JsonResultMessage.NO ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
        }
        if(columns.contains(OrderExportVo.SOURCE)){
            //商品来源
            GoodsRecord goods= saas.getShopApp(getShopId()).goods.getGoodsById(order.getGoodsId()).get();
            order.setSource(goods.getSource() > 0 ? Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_GOODS_SOURCE_SELF_OPERATED ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL) : Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_GOODS_SOURCE_PLATFORM ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
        }
    }

    private void processPayName(List<String> columns, String lang, OrderExportVo order) {
        if(columns.contains(OrderExportVo.PAY_NAMES)){
            //支付方式
            StringBuffer payNames = new StringBuffer();
            if(order.getIsCod() > 0){
                //货到付款
                payNames.append(",");
                payNames.append(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_PAY_TYPE_COD ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
            }
            if(order.getMoneyPaid().compareTo(BigDecimal.ZERO) > 0){
                //微信支付
                payNames.append(",");
                payNames.append(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_PAY_TYPE_WXPAY ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
            }
            if(order.getUseAccount().compareTo(BigDecimal.ZERO) > 0){
                //余额支付
                payNames.append(",");
                payNames.append(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_PAY_TYPE_BALANCE ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
            }
            if(order.getScoreDiscount().compareTo(BigDecimal.ZERO) > 0){
                //积分支付
                payNames.append(",");
                payNames.append(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_PAY_TYPE_SCORE ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
            }
            if(order.getMemberCardReduce().compareTo(BigDecimal.ZERO) > 0){
                //会员卡支付
                payNames.append(",");
                payNames.append(Util.translateMessage(lang, JsonResultMessage.ORDER_EXPORT_PAY_TYPE_MEMBER_CARD ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
            }
            if(payNames.length() > 0){
                payNames.deleteCharAt(0);
            }
            order.setPayNames(payNames.toString());
        }
    }

    /**
	 *  购买商品记录(三个月内)
	 * @param userId  用户ID
	 * @param keyWord 关键字
	 * @param currentPages 当前页
	 * @param pageRows 每页行数
	 * @return OrderGoodsHistoryBo
	 * @author kdc
	 */
	public FootprintListVo buyingHistoryGoodsList(Integer userId, String keyWord, Integer currentPages, Integer pageRows){
		FootprintListVo footprintListVo =new FootprintListVo();
		List<FootprintDayVo> footprintDaylist =new ArrayList<>();
		footprintListVo.setDay(footprintDaylist);
		Result<? extends Record> records = orderGoods.buyingHistoryGoodsList(userId, keyWord, currentPages, pageRows);
		Integer totalRows = orderGoods.buyingHistoryGoodsCount(userId, keyWord);
		List<Integer> goodsIdList = Arrays.asList(records.intoArray(ORDER_GOODS.GOODS_ID));
		List<FootprintDayVo> orderGoodsHistoryVos =records.into(FootprintDayVo.class);
		Page page = Page.getPage(totalRows, currentPages, pageRows);
		footprintListVo.setPage(page);
		List<? extends GoodsListMpVo> goodsListMpVos = goodsMpService.getGoodsListNormal(goodsIdList, userId,null,null);
		Map<Integer, GoodsListMpVo> goodsListMpVoMap = goodsListMpVos.stream().collect(Collectors.toMap(GoodsListMpVo::getGoodsId, goods->goods));
		orderGoodsHistoryVos.forEach(orderGoods->{
			GoodsListMpVo goodsListMpVo = goodsListMpVoMap.get(orderGoods.getGoodsId());
			orderGoods.getGoodsList().add(goodsListMpVo);
		});
		// 安装日期分组
		footPrintService.byDateGroup(orderGoodsHistoryVos,footprintDaylist);
		//是否显示划线价开关
		Byte delMarket = configService.shopCommonConfigService.getDelMarket();
		//是否显示购买按钮
		ShowCartConfig showCart = configService.shopCommonConfigService.getShowCart();
		footprintListVo.setShowCart(showCart);
		footprintListVo.setDelMarket(delMarket);
		return footprintListVo;
	}

    /**
     * 检查核销码是否正确
     * @param verifyCode 核销码
     * @param orderSn 订单唯一id
     * @return boolean
     * @author 赵晓东
     */
    public boolean checkVerifyCode(String verifyCode, String orderSn) {
        return orderInfoDao.checkVerifyCode(verifyCode, orderSn);
    }

    /**
     * 获取门店支付统计数据
     * @param param
     * @return
     */
    public StatisticPayVo getStoreOrderPayData(StatisticParam param) {
        return orderInfoDao.getStoreOrderPayData(param);
    }

    /**
     * 获取门店下单统计数据
     * @param param
     * @return
     */
    public StatisticAddVo getStoreOrderAddData(StatisticParam param) {
        return orderInfoDao.getStoreOrderAddData(param);
    }

    /**
     * 获取门店配送单待发货单量
     * @param storeIds
     * @return
     */
    public Integer getStoreOrderWaitDeliver(List<Integer> storeIds) {
        return orderInfoDao.getStoreOrderWaitDeliver(storeIds);
    }

    /**
     * 获取门店自提单待核销单量
     * @param storeIds
     * @return
     */
    public Integer getStoreOrderWaitVerify(List<Integer> storeIds) {
        return orderInfoDao.getStoreOrderWaitVerify(storeIds);
    }
}
