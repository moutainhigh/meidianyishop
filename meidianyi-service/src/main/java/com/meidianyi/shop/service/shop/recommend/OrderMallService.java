package com.meidianyi.shop.service.shop.recommend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo.Goods;
import com.meidianyi.shop.service.pojo.shop.payment.PayCode;
import com.meidianyi.shop.service.pojo.shop.recommend.SendOrderBean;
import com.meidianyi.shop.service.pojo.shop.recommend.order.AttrName;
import com.meidianyi.shop.service.pojo.shop.recommend.order.AttrValue;
import com.meidianyi.shop.service.pojo.shop.recommend.order.BrandInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ContactDetailPage;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ExpressGoodsInfoList;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ExpressInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ExpressPackageInfoList;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ExpressPage;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ExpressParam;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ExtInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.order.InvoiceDetailPage;
import com.meidianyi.shop.service.pojo.shop.recommend.order.InvoiceInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ItemDetailPage;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ItemList;
import com.meidianyi.shop.service.pojo.shop.recommend.order.JsonRootBean;
import com.meidianyi.shop.service.pojo.shop.recommend.order.OrderDetailPage;
import com.meidianyi.shop.service.pojo.shop.recommend.order.OrderList;
import com.meidianyi.shop.service.pojo.shop.recommend.order.ProductInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.order.PromotionInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.order.StockAttrInfo;
import com.meidianyi.shop.service.saas.categroy.SysCateService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.ship.ShipInfoService;
import com.meidianyi.shop.service.shop.payment.PaymentService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import com.meidianyi.shop.service.wechat.api.impl.WxOpenMaServiceExtraImpl;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 好物圈订单操作
 *
 * @author zhaojianqiang
 *
 *         2019年11月12日 上午10:15:22
 */
@Service
@Slf4j
public class OrderMallService extends ShopMallBaseService {
	@Autowired
	private UserService userService;
	@Autowired
	private OrderInfoService orderInfo;
	@Autowired
	private PaymentService payService;
	@Autowired
	private OrderGoodsService orderGoodsService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private SysCateService sysCateService;
	@Autowired
	private ShipInfoService shipInfo;

	/**
	 * 添加好物圈订单
	 *
	 * @param userId
	 * @param orderSns
	 * @return
	 */
	public Boolean addCommonOrders(Integer userId, List<String> orderSns) {
		Boolean addCommOrder=false;
		try {
			addCommOrder = addCommOrder(userId, orderSns);
		} catch (Exception e) {
			logger().info(e.getMessage(),e);
		}
		return addCommOrder;
	}


	private Boolean addCommOrder(Integer userId, List<String> orderSns) {
		logger().info("添加好物圈订单");
		String openId = check(userId);
		if (null == openId) {
			return false;
		}
		OrderList orderList = new OrderList();
		for (String orderSn : orderSns) {
			OrderInfoRecord order = orderInfo.getOrderByOrderSn(orderSn);
			Result<OrderGoodsRecord> orderGoods = orderGoodsService.getOrderGoods(orderSn);
			Byte orderStatus = order.getOrderStatus();
			Integer status = getStatus(orderStatus);
			if (status == null) {
				continue;
			}
			orderList.setOrderId(orderSn);
			orderList.setCreateTime(order.getCreateTime().getTime() / 1000L);
			orderList.setPayFinishTime(null == order.getPayTime() ? order.getCreateTime().getTime() / 1000L
					: order.getPayTime().getTime() / 1000L);
			orderList.setDesc(order.getAddMessage());
			orderList.setFee(order.getMoneyPaid().multiply(new BigDecimal(100)));
			orderList.setTransId(getTransId(order));
			orderList.setStatus(status);
			ExtInfo extInfo = new ExtInfo();
			extInfo.setProductInfo(new ProductInfo(getProductInfo(order, orderGoods)));
			extInfo.setExpressInfo(getExpressInfo(order));
			extInfo.setPromotionInfo(getPromotionInfo(orderGoods));
			extInfo.setBrandInfo(getBrandInfo());
			extInfo.setInvoiceInfo(getInvoiceInfo(order));
			extInfo.setPaymentMethod(order.getPayCode().equals(PayCode.PAY_CODE_WX_PAY) ? 1 : 2);
			extInfo.setUserOpenId(openId);
			extInfo.setOrderDetailPage(
					new OrderDetailPage("/pages/orderinfo/orderinfo?orderSn=" + order.getOrderSn()));
			orderList.setExtInfo(extInfo);
		}
		logger().info("要发送的数据"+orderList);
		List<OrderList> list = new ArrayList<OrderList>();
		list.add(orderList);
		JsonRootBean jsonRootBean = new JsonRootBean(list);
		SendOrderBean bean=new SendOrderBean(1, jsonRootBean,getShopId(),null);
		saas.taskJobMainService.dispatchImmediately(bean,SendOrderBean.class.getName(),getShopId(),TaskJobEnum.WX_IMPORTORDER.getExecutionType());
		logger().info("添加好物圈订单发队列了");
		return true;
	}


	/**
	 * 更新订单信息，商家可以对“已购订单”中的订单信息进行更新，如订单状态改变等
	 * @param userId
	 * @param orderSns
	 * @return
	 */
	public Boolean updateCommonOrders(Integer userId, List<String> orderSns) {
		Boolean commonOrder=false;
		try {
			commonOrder = updateCommonOrder(userId, orderSns);
		} catch (Exception e) {
			logger().info(e.getMessage(),e);
		}
		return commonOrder;
	}


	private Boolean updateCommonOrder(Integer userId, List<String> orderSns) {
		String openId = check(userId);
		if (null == openId) {
			return false;
		}
		OrderList orderList = new OrderList();
		for (String orderSn : orderSns) {
			OrderInfoRecord order = orderInfo.getOrderByOrderSn(orderSn);
			Byte orderStatus = order.getOrderStatus();
			Integer status = getStatus(orderStatus);
			if (status == null) {
				continue;
			}
			orderList.setOrderId(orderSn);
			orderList.setTransId(getTransId(order));
			orderList.setStatus(status);
			ExtInfo extInfo = new ExtInfo();
			extInfo.setInvoiceInfo(getInvoiceInfo(order));
			extInfo.setUserOpenId(openId);
			extInfo.setOrderDetailPage(
					new OrderDetailPage("/pages/orderinfo/orderinfo?orderSn=" + order.getOrderSn()));
			// 退款时，如果没有快递号，则把快递信息删除掉,否则会出错。
			if (!(status == 5 && StringUtils.isEmpty(order.getShippingNo()))) {
				extInfo.setExpressInfo(getExpressInfo(order));
			}
			orderList.setExtInfo(extInfo);

		}
		List<OrderList> list = new ArrayList<OrderList>();
		list.add(orderList);
		JsonRootBean jsonRootBean = new JsonRootBean(list);
		SendOrderBean bean=new SendOrderBean(2, jsonRootBean,getShopId(),null);
		saas.taskJobMainService.dispatchImmediately(bean,SendOrderBean.class.getName(),getShopId(),TaskJobEnum.WX_IMPORTORDER.getExecutionType());
		return true;
	}


	/**
	 * 更新商品，消费者，不要直接调用
	 *
	 * @param jsonRootBean
	 * @return
	 */
	public WxOpenResult importOrderUpdate(JsonRootBean jsonRootBean) {
		WxOpenMaServiceExtraImpl maService = open.getMaExtService();
		MpAuthShopRecord shop = saas.shop.mp.getAuthShopByShopId(getShopId());
		WxOpenResult result = null;
		try {
			String jsonNotNull = Util.toJsonNotNull(jsonRootBean);
			log.info("发送的报文" + jsonNotNull);
			result = maService.importorderAdd(shop.getAppId(), jsonNotNull);
			log.info(" 添加好物圈订单");
			log.info(result.getErrmsg(), result.getErrcode());
		} catch (WxErrorException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 导入商品，消费者，不要直接调用
	 *
	 * @param jsonRootBean
	 * @return
	 */
	public WxOpenResult importOrderAdd(JsonRootBean jsonRootBean) {
		WxOpenMaServiceExtraImpl maService = open.getMaExtService();
		MpAuthShopRecord shop = saas.shop.mp.getAuthShopByShopId(getShopId());
		WxOpenResult result = null;
		try {
			String jsonNotNull = Util.toJsonNotNull(jsonRootBean);
			log.info("发送的报文" + jsonNotNull);
			result = maService.importorderAdd(shop.getAppId(), jsonNotNull);
			log.info(" 添加好物圈订单");
			log.info(result.getErrmsg(), result.getErrcode());
		} catch (WxErrorException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 得到商家信息
	 *
	 * @return
	 */
	private BrandInfo getBrandInfo() {
		ShopRecord shop = saas().shop.getShopById(getShopId());
		return new BrandInfo(shop.getMobile(), new ContactDetailPage("/pages/index/index", 1));
	}

	/**
	 * 得到优惠信息
	 *
	 * @param orderGoods
	 * @return
	 */
	private PromotionInfo getPromotionInfo(Result<OrderGoodsRecord> orderGoods) {
		BigDecimal totalDiscountFee = new BigDecimal(0);
		for (OrderGoodsRecord orderGood : orderGoods) {
			BigDecimal multiply = orderGood.getGoodsPrice()
					.multiply(new BigDecimal(orderGood.getGoodsNumber()).subtract(orderGood.getDiscountedTotalPrice()));
			totalDiscountFee = totalDiscountFee.add(multiply.multiply(new BigDecimal(100)));
		}
		return new PromotionInfo(totalDiscountFee);
	}

	/**
	 * 得到发票信息
	 *
	 * @param order
	 * @return
	 */
	private InvoiceInfo getInvoiceInfo(OrderInfoRecord order) {
		String invoiceTitle = order.getInvoiceTitle();
		if (StringUtils.isEmpty(invoiceTitle)) {
			return null;
		}
		InvoiceVo invoice = Util.parseJson(invoiceTitle, InvoiceVo.class);
		InvoiceInfo info = new InvoiceInfo();
		FieldsUtil.assignNotNull(invoice, info);
		info.setInvoiceDetailPage(new InvoiceDetailPage("/pages/orderinfo/orderinfo?orderSn=" + order.getOrderSn()));
		return info;
	}

	/**
	 * 得到快递相关信息
	 *
	 * @param order
	 * @return
	 */
	private ExpressInfo getExpressInfo(OrderInfoRecord order) {
		ExpressInfo info = new ExpressInfo();
		info.setName(order.getConsignee());
		info.setPhone(order.getMobile());
		info.setAddress(order.getCompleteAddress());
		info.setPrice(order.getShippingFee().multiply(new BigDecimal(100)));
		info.setNationalCode(String.valueOf(order.getDistrictCode()));
		info.setCountry("中国");
		info.setProvince(order.getProvinceName());
		info.setCity(order.getCityName());
		info.setDistrict(order.getDistrictName());
		info.setExpressPackageInfoList(getExpressPackageInfoList(order));
		return info;
	}

	/**
	 * 得到包裹信息
	 *
	 * @param order
	 * @return
	 */
	private List<ExpressPackageInfoList> getExpressPackageInfoList(OrderInfoRecord order) {
		if (StringUtils.isEmpty(order.getShippingNo())) {
			return null;
		}
		Map<String, List<ShippingInfoVo>> shippingByOrderSn = shipInfo.getShippingByOrderSn(Collections.singletonList(order.getOrderSn()));
		List<ShippingInfoVo> partShips = shippingByOrderSn.get(order.getOrderSn());
		if (partShips == null || partShips.size() == 0) {
			return null;
		}
		List<ExpressPackageInfoList> expressInfo = new ArrayList<ExpressPackageInfoList>();
		// ExpressPackageInfoList itemList=new ExpressPackageInfoList();
		for (ShippingInfoVo partShip : partShips) {
			ExpressPackageInfoList param = new ExpressPackageInfoList();
			List<ExpressGoodsInfoList> itemList = new ArrayList<ExpressGoodsInfoList>();
			ExpressGoodsInfoList item = new ExpressGoodsInfoList();
			List<Goods> goods = partShip.getGoods();
			for (Goods orderGood : goods) {
				item.setItemCode(String.valueOf(orderGood.getGoodsId()));
				item.setSkId(String.valueOf(orderGood.getProductId()));
			}
			itemList.add(item);
			param.setExpressGoodsInfoList(itemList);
			ExpressParam expressCompany = getExpressCompany(partShip.getShippingId(), partShip.getShippingName());
			param.setExpressCompanyId(expressCompany.getCode());
			param.setExpressCompanyName(expressCompany.getName());
			param.setExpressCode(partShip.getShippingNo());
			param.setShipTime(partShip.getShippingTime().getTime() / 1000L);
			param.setExpressPage(new ExpressPage(
					"/pages/express/express?order_sn=" + order.getOrderSn() + "&ex_no=" + order.getShippingNo()));
			expressInfo.add(param);
		}
		return expressInfo;
	}

	public ExpressParam getExpressCompany(Byte shippingId, String shippingName) {
		Map<Integer, ExpressParam> map2 = new HashMap<Integer, ExpressParam>();
		map2.put(5, new ExpressParam(2000, "EMS"));
		map2.put(2, new ExpressParam(2001, "圆通"));
		// map2.put(1,new ExpressParam(2002,"DHL"));
		map2.put(4, new ExpressParam(2004, "中通"));
		map2.put(3, new ExpressParam(2005, "韵达"));
		// map2.put(1,new ExpressParam(2006,"畅灵"));
		map2.put(7, new ExpressParam(2008, "百世汇通"));
		map2.put(11, new ExpressParam(2009, "德邦"));
		map2.put(1, new ExpressParam(2010, "申通"));
		map2.put(6, new ExpressParam(2011, "顺丰速运"));
		// map2.put(1,new ExpressParam(2012,"顺兴"));
		map2.put(15, new ExpressParam(2014, "如风达"));
		map2.put(22, new ExpressParam(2015, "优速"));
		map2.put(9999, new ExpressParam(9999, shippingName));

		ExpressParam vo = map2.get((int)shippingId);
		if (null == vo) {
			vo = new ExpressParam(9999, shippingName);

		}
		return vo;
	}

	private List<ItemList> getProductInfo(OrderInfoRecord order, Result<OrderGoodsRecord> orderGoods) {
		List<ItemList> itemList = new ArrayList<ItemList>();
		for (OrderGoodsRecord orderGood : orderGoods) {
			ItemList item = new ItemList();
			GoodsRecord goods = goodsService.getGoodsById(orderGood.getGoodsId()).get();
			Integer catId = null == goods.getCatId() ? 0 : goods.getCatId();
			item.setItemCode(String.valueOf(orderGood.getGoodsId()));
			item.setSkuId(String.valueOf(orderGood.getProductId()));
			item.setAmount(orderGood.getGoodsNumber());
			item.setTotalFee(orderGood.getGoodsPrice().multiply(new BigDecimal(orderGood.getGoodsNumber()))
					.multiply(new BigDecimal(100)));
			item.setThumbUrl(userService.image.imageUrl(orderGood.getGoodsImg()));
			item.setTitle(orderGood.getGoodsName());
			item.setDesc("");
			item.setUnitPrice(orderGood.getGoodsPrice().multiply(new BigDecimal(100)));
			item.setOriginalPrice(orderGood.getMarketPrice().compareTo(BigDecimal.ZERO) == 1
					? orderGood.getMarketPrice().multiply(new BigDecimal(100))
					: orderGood.getGoodsPrice().multiply(new BigDecimal(100)));
			item.setStockAttrInfo(getOrderGoodsAttr(orderGood.getGoodsAttr()));
			List<String> list = sysCateService.getCategories(catId);
			if (list == null || list.size() == 0) {
				list = new ArrayList<String>();
				list.add("未知");
			}
			item.setCategoryList(list);
			item.setItemDetailPage(new ItemDetailPage("/pages/item/item?gid=" + orderGood.getGoodsId()));
			itemList.add(item);
		}
		return itemList;

	}

	private List<StockAttrInfo> getOrderGoodsAttr(String goodsAttr) {
		if (StringUtils.isEmpty(goodsAttr)) {
			return null;
		}
		// 颜色:黑色
		List<StockAttrInfo> stockAttrInfo = new ArrayList<StockAttrInfo>();
		String[] goodsAttrs = goodsAttr.split(";");
		for (String goods : goodsAttrs) {
			StockAttrInfo sAttrInfo = new StockAttrInfo();
			String[] split = goods.split(":");
			sAttrInfo.setAttrName(new AttrName(split[0]));
			sAttrInfo.setAttrValue(new AttrValue(split[1]));
			stockAttrInfo.add(sAttrInfo);
		}

		return stockAttrInfo;
	}

	private String getTransId(OrderInfoRecord order) {
		if (StringUtils.isEmpty(order.getPaySn())) {
			return "";
		} else {
			PaymentRecordRecord paRecord = payService.record.getPaymentRecordByPaySn(order.getPaySn());
			return paRecord == null ? "" : paRecord.getTradeNo();
		}
	}


	/**
	 * 得到微信已购订单状态
	 *
	 * @param orderStatus
	 * @return
	 */
	private Integer getStatus(Byte orderStatus) {
		Map<Byte, Integer> map = new HashMap<Byte, Integer>();
		map.put(OrderConstant.ORDER_WAIT_DELIVERY, 3);
		map.put(OrderConstant.ORDER_SHIPPED, 4);
		map.put(OrderConstant.ORDER_RECEIVED, 4);
		map.put(OrderConstant.ORDER_REFUND_FINISHED, 5);
		map.put(OrderConstant.ORDER_RETURN_FINISHED, 5);
		map.put(OrderConstant.ORDER_FINISHED, 100);
		Integer integer = map.get(orderStatus);
		return integer;
	}

}
