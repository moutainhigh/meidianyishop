package com.meidianyi.shop.service.shop.market.groupdraw;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.order.OrderExport;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.order.OrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.order.OrderListVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.export.OrderExportVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.JoinDrawList.JOIN_DRAW_LIST;
import static com.meidianyi.shop.db.shop.tables.JoinGroupList.JOIN_GROUP_LIST;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.OrderMust.ORDER_MUST;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author 郑保乐
 */
@Service
public class GroupDrawOrderService extends ShopBaseService {
	public PageResult<OrderListVo> getGroupDrawOrderList(OrderListParam param) {
		SelectConditionStep<Record13<String, String, String, Integer, Integer, Timestamp, String, String, Byte, String, Integer, Byte, Boolean>> select = getSelect(
				param);
		return getPageResult(select, param, OrderListVo.class);
	}

	private SelectConditionStep<Record13<String, String, String, Integer, Integer, Timestamp, String, String, Byte, String, Integer, Byte, Boolean>> getSelect(
			OrderListParam param) {
		SelectConditionStep<Record13<String, String, String, Integer, Integer, Timestamp, String, String, Byte, String, Integer, Byte, Boolean>> select = db()
				.select(JOIN_GROUP_LIST.ORDER_SN, ORDER_GOODS.GOODS_NAME, ORDER_GOODS.GOODS_IMG,
						JOIN_GROUP_LIST.USER_ID, ORDER_GOODS.ORDER_ID, ORDER_INFO.CREATE_TIME, ORDER_INFO.MOBILE,
						ORDER_INFO.CONSIGNEE.as("consigneeRealName"), JOIN_GROUP_LIST.IS_WIN_DRAW,
						ORDER_INFO.COMPLETE_ADDRESS, DSL.count(JOIN_DRAW_LIST.USER_ID).as("codeCount"),
						ORDER_INFO.ORDER_STATUS,
						DSL.iif(JOIN_GROUP_LIST.STATUS.eq((byte) 1), true, false).as("grouped"))
				.from(JOIN_GROUP_LIST).leftJoin(ORDER_INFO).on(JOIN_GROUP_LIST.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
				.leftJoin(JOIN_DRAW_LIST)
				.on(JOIN_GROUP_LIST.USER_ID.eq(JOIN_DRAW_LIST.USER_ID)
						.and(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(JOIN_DRAW_LIST.GROUP_DRAW_ID)))
				.leftJoin(ORDER_GOODS).on(JOIN_GROUP_LIST.ORDER_SN.eq(ORDER_GOODS.ORDER_SN)).where();
		buildOptions(select, param);
		select.orderBy(JOIN_GROUP_LIST.CREATE_TIME.desc());
		return select;
	}
	
	public List<OrderListVo> getGroupDrawOrderListNoPage(OrderListParam param) {
		return  getSelect(param).fetchInto(OrderListVo.class);
	}

	private void buildOptions(
			SelectConditionStep<Record13<String, String, String, Integer, Integer, Timestamp, String, String, Byte, String, Integer, Byte, Boolean>> select,
			OrderListParam param) {
		Integer groupDrawId = param.getGroupDrawId();
		String goodsName = param.getGoodsName();
		String consigneeName = param.getConsigneeName();
		String mobile = param.getMobile();
		Timestamp createTime = param.getCreateTime();
		Byte orderStatus = param.getOrderStatus();
		String orderSn = param.getOrderSn();
		select.and(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId));
		if (isNotEmpty(mobile)) {
			select.and(ORDER_INFO.MOBILE.like(this.likeValue(mobile)));
		}
		if (isNotEmpty(orderSn)) {
			select.and(JOIN_GROUP_LIST.ORDER_SN.like(this.likeValue(orderSn)));
		}
		if (isNotEmpty(goodsName)) {
			select.and(ORDER_GOODS.GOODS_NAME.like(this.likeValue(goodsName)));
		}
		if (isNotEmpty(consigneeName)) {
			select.and(ORDER_INFO.CONSIGNEE.like(this.likeValue(consigneeName)));
		}
		if (orderStatus != -1) {
			select.and(ORDER_INFO.ORDER_STATUS.eq(orderStatus));
		}
		if (null != createTime) {
			select.and(DSL.date(ORDER_INFO.CREATE_TIME).eq(new Date(createTime.getTime())));
		}
		if(null!=param.getProvinceCode()) {
			select.and(ORDER_INFO.PROVINCE_CODE.eq(param.getProvinceCode()));
		}
		if(null!=param.getCityCode()) {
			select.and(ORDER_INFO.CITY_CODE.eq(param.getCityCode()));
		}
		if(null!=param.getDistrictCode()) {
			select.and(ORDER_INFO.DISTRICT_CODE.eq(param.getDistrictCode()));
		}
		select.groupBy(JOIN_GROUP_LIST.USER_ID, JOIN_GROUP_LIST.ORDER_SN, JOIN_GROUP_LIST.GOODS_ID,
				ORDER_GOODS.GOODS_IMG, ORDER_GOODS.GOODS_NAME, ORDER_INFO.CREATE_TIME, ORDER_INFO.MOBILE,
				ORDER_INFO.CONSIGNEE, JOIN_GROUP_LIST.CREATE_TIME, JOIN_GROUP_LIST.IS_WIN_DRAW, ORDER_INFO.ORDER_STATUS,
				ORDER_GOODS.ORDER_ID, JOIN_GROUP_LIST.STATUS, ORDER_INFO.COMPLETE_ADDRESS);
	}
	/**
	 * 订单导出
	 * 
	 * @param param 查询信息
	 * @param lang  语言
	 * @return 表格信息
	 */
	public Workbook orderExport(OrderListParam param, String lang) {
		List<OrderExport> orderExport = new ArrayList<>();
		List<OrderListVo> tempList = getGroupDrawOrderListNoPage(param);
		tempList.forEach(item -> {
			OrderExport tempExport = new OrderExport();
			tempExport.setOrderSn(item.getOrderSn());
			tempExport.setGoodsName(item.getGoodsName());
			if (item.getGrouped()) {
				tempExport.setGrouped(Util.translateMessage(lang, JsonResultMessage.YES , OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
			} else {
				tempExport.setGrouped(Util.translateMessage(lang, JsonResultMessage.NO ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
			}
			tempExport.setUserInfo(item.getConsigneeRealName() + ":" + item.getMobile());
			if (item.getIsWinDraw()) {
				tempExport.setIsWinDraw(Util.translateMessage(lang, JsonResultMessage.YES ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
			} else {
				tempExport.setIsWinDraw(Util.translateMessage(lang, JsonResultMessage.NO ,OrderExportVo.LANGUAGE_TYPE_EXCEL,OrderExportVo.LANGUAGE_TYPE_EXCEL));
			}
			tempExport.setCreateTime(item.getCreateTime());
			tempExport.setCodeCount(item.getCodeCount());
            tempExport.setOrderStatus(OrderConstant.getOrderStatusName(item.getOrderStatus(),lang));

			orderExport.add(tempExport);
		});
		// 表格导出
		Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
		ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
		excelWriter.writeModelList(orderExport, OrderExport.class);
		return workbook;
	}
	
	/**
	 * 查询结束时间
	 * @param orderSn
	 * @return
	 */
	public Timestamp getEndTime(String orderSn) {
		return db().select(JOIN_GROUP_LIST.END_TIME).from(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.ORDER_SN.eq(orderSn)).fetchAnyInto(Timestamp.class);
	}
}
