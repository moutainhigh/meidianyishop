package com.meidianyi.shop.service.shop.marketcalendar;

import static com.meidianyi.shop.db.shop.tables.MarketCalendar.MARKET_CALENDAR;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.MarketCalendarRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarSysVo;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketMqParam;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionName;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.ActInfoVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketCalendarActivityVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketCalendarInfoVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketCalendarVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketListData;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketListDataVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketcalendarParam;
import com.meidianyi.shop.service.shop.ShopApplication;

/**
 * @author zhaojianqiang
 */
@Service
public class MarketCalendarService extends ShopBaseService {
	@Autowired
	public MarketCalendarActivityService calendarActivityService;

	/**
	 * 创建日历活动
	 *
	 * @param param
	 * @return
	 */
	public boolean addCalendar(MarketcalendarParam param) {
		MarketCalendarRecord record = db().newRecord(MARKET_CALENDAR, param);
		int insert = record.insert();
		logger().info("创建营销活动：{}，结果：{}", param.getEventName(), insert);
		if (insert < 0) {
			return false;
		}
		calendarActivityService.addCalendarAct(param, record.getId());
		return insert == 1 ? true : false;
	}

	/**
	 * 编辑日历活动
	 *
	 * @param param
	 * @return
	 */
	public boolean edit(MarketcalendarParam param) {
		Integer calendarId = param.getCalendarId();
		if (calendarId == null || calendarId.equals(0)) {
			logger().info("活动名称：{}，id为0或不存在，不能更新", param.getEventName());
			return false;
		}
		MarketCalendarRecord record = db().newRecord(MARKET_CALENDAR, param);
		record.setId(calendarId);
		int update = record.update();
		if (update < 0) {
			return false;
		}
		logger().info("更新营销活动id:{},名称：{}，结果：{}", calendarId, param.getEventName(), update);
		calendarActivityService.editCalendarAct(param, calendarId);
		return update == 1 ? true : false;
	}

	/**
	 * 删除
	 *
	 * @param id
	 * @return
	 */
	public boolean del(Integer id) {
		int execute = db().update(MARKET_CALENDAR).set(MARKET_CALENDAR.DEL_FLAG, DelFlag.DISABLE_VALUE)
				.where(MARKET_CALENDAR.ID.eq(id)).execute();
		return execute > 0 ? true : false;
	}

	/**
	 * 营销日历列表 里边的
	 *
	 * @param year
	 * @return
	 */
	public MarketListDataVo getListByYear(String year) {
		if(StringUtils.isEmpty(year)) {
			year=String.valueOf(LocalDate.now().getYear());
		}
		List<MarketListData> list = new LinkedList<MarketListData>();
		List<MarketCalendarVo> calendarList = db().selectFrom(MARKET_CALENDAR)
				.where(MARKET_CALENDAR.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(DSL.left(MARKET_CALENDAR.EVENT_TIME.cast(String.class), 4).eq(year)))
				.orderBy(MARKET_CALENDAR.EVENT_TIME.asc()).fetchInto(MarketCalendarVo.class);
		Date nowDate = DateUtils.yyyyMmDdDate(LocalDate.now());
		for (int i = 1; i < 13; i++) {
			MarketListData data = new MarketListData();
			if (i < 10) {
				data.setMonth("0" + i);
			} else {
				data.setMonth(String.valueOf(i));
			}
			List<MarketCalendarVo> dataList = new LinkedList<MarketCalendarVo>();
			data.setData(dataList);
			list.add(data);
		}
		for (MarketCalendarVo item : calendarList) {
			item = eventStatus(item);
			if (item.getEventStatus().equals(CalendarAction.ONE)) {
				int days = (int) ((item.getEventTime().getTime() - nowDate.getTime()) / (1000 * 60 * 60 * 24L));
				item.setDownTime(days);
			}
			Date eventTime = item.getEventTime();
			LocalDate localDate = eventTime.toLocalDate();
			int month = localDate.getMonth().getValue();
			MarketListData marketListData = list.get(month - 1);
			List<MarketCalendarVo> data = marketListData.getData();
			data.add(item);
		}
		return new MarketListDataVo(list, nowDate, getYearList());
	}

	public List<String> getYearList() {
		Field<String> field = DSL.left(MARKET_CALENDAR.EVENT_TIME.cast(String.class), 4).as("year");
		return db().select(field).from(MARKET_CALENDAR).where(MARKET_CALENDAR.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.groupBy(field).orderBy(field.desc()).fetchInto(String.class);
	}

	public MarketCalendarRecord getInfoById(Integer id) {
		return db().selectFrom(MARKET_CALENDAR).where(MARKET_CALENDAR.ID.eq(id)).fetchAny();
	}

	public MarketCalendarVo eventStatus(MarketCalendarVo param) {
		logger().info("开始判断状态");
		// 1未开始，2进行中，3失效，4已结束
		int hasAct = 0;
		byte status = 2;
		int overNum = 0;
		Date nowDate = Date.valueOf(LocalDate.now());
		if (param.getEventTime().after(nowDate)) {
			param.setEventStatus(CalendarAction.ONE);
			return param;
		}
		List<MarketCalendarActivityVo> calendarActList = calendarActivityService.calendarActList(param.getId());
		for (MarketCalendarActivityVo item : calendarActList) {
			if (item.getActivityId() > 0) {
				hasAct++;
				ActInfoVo info = getActInfo(item.getActivityType(), item.getActivityId(), CalendarAction.INFO, null);
				MarketVo actInfo = info.getActInfo();
				byte actStatus = actInfo.getActStatus();
				if (Objects.equal(actStatus, CalendarAction.TWO)) {
					status = 2;
				}
				if (Objects.equal(actStatus, CalendarAction.FOUR)) {
					overNum++;
				}
			}
		}

		if (Objects.equal(hasAct, 0) && param.getEventTime().before(DateUtils.yyyyMmDdDate(LocalDate.now()))) {
			status = CalendarAction.THREE;
		}
		if (Objects.equal(overNum, calendarActList.size()) && overNum > 0) {
			status = CalendarAction.FOUR;
		}
		param.setEventStatus(status);
		logger().info("日历：{}，状态：{}", param.getEventName(), status);
		return param;
	}

	public ActInfoVo getActInfo(String actType, Integer actId, String type, MarketParam param) {
		ShopApplication shopApp = saas.getShopApp(getShopId());
		// VsName
		logger().info("传入的类型：{}", actType);
		MarketVo actInfo = new MarketVo();
		PageResult<MarketVo> list = new PageResult<MarketVo>();
		Page page=new Page();
		List<MarketVo> dataList=new ArrayList<MarketVo>();
		list.setPage(page);
		list.setDataList(dataList);
		switch (actType) {
		case VersionName.SUB_4_PIN_GROUP:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.groupBuy.getActInfo(actId);
			} else {
				list = shopApp.groupBuy.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_BARGAIN:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.bargain.getActInfo(actId);
			} else {
				list = shopApp.bargain.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_GROUP_DRAW:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.groupDraw.getActInfo(actId);
			} else {
				list = shopApp.groupDraw.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_PIN_INTEGRATION:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.groupIntegration.getActInfo(actId);
			} else {
				list = shopApp.groupIntegration.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_LOTTERY:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.lottery.getActInfo(actId);
			} else {
				list = shopApp.lottery.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_PROMOTE:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.friendPromoteService.getActInfo(actId);
			} else {
				list = shopApp.friendPromoteService.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_GIVE_GIFT:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.giveGift.getActInfo(actId);
			} else {
				list = shopApp.giveGift.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_SHARE_AWARD:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.shareRewardService.getActInfo(actId);
			} else {
				list = shopApp.shareRewardService.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_COUPON_PACKAGE:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.couponPack.getActInfo(actId);
			} else {
				list = shopApp.couponPack.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_REDUCE_PRICE:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.reducePrice.getActInfo(actId);
			} else {
				list = shopApp.reducePrice.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_PAY_REWARD:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.payAward.getActInfo(actId);
			} else {
				list = shopApp.payAward.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_FIRST_SPECIAL:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.firstSpecial.getActInfo(actId);
			} else {
				list = shopApp.firstSpecial.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_PRE_SALE:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.preSale.getActInfo(actId);
			} else {
				list = shopApp.preSale.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_SECKILL_GOODS:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.seckill.getActInfo(actId);
			} else {
				list = shopApp.seckill.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_GIFT:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.gift.getActInfo(actId);
			} else {
				list = shopApp.gift.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_FULL_CUT:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.mrkingStrategy.getActInfo(actId);
			} else {
				list = shopApp.mrkingStrategy.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_PACKAGE_SALE:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.packSale.getActInfo(actId);
			} else {
				list = shopApp.packSale.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_PURCHASE_PRICE:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.increaseService.getActInfo(actId);
			} else {
				list = shopApp.increaseService.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_FREE_SHIP:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.freeShipping.getActInfo(actId);
			} else {
				list = shopApp.freeShipping.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_INTEGRAL_GOODS:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.integralConvertService.getActInfo(actId);
			} else {
				list = shopApp.integralConvertService.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_ACTIVITY_REWARD:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.coopen.getActInfo(actId);
			} else {
				list = shopApp.coopen.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_COMMENT_GIFT:
			if (type.equals(CalendarAction.INFO)) {
				actInfo = shopApp.commentAward.getActInfo(actId);
			} else {
				list = shopApp.commentAward.getListNoEnd(param);
			}
			break;
		case VersionName.SUB_4_ASSESS:
			// 这个功能还没写
			break;
		default:
			break;
		}
		if (type.equals(CalendarAction.INFO)) {
			byte actStatus = getActStatus(actInfo);
			actInfo.setActStatus(actStatus);
			actInfo.setActivityType(actType);
		} else {
			for (MarketVo vo : list.getDataList()) {
				byte actStatus = getActStatus(vo);
				vo.setActStatus(actStatus);
				vo.setActivityType(actType);
			}
		}
		return new ActInfoVo(actInfo == null ? new MarketVo() : actInfo, list);
	}

	/**
	 * 返回活动状态
	 *
	 * @param actInfo
	 * @return
	 */
	public byte getActStatus(MarketVo actInfo) {
		if (actInfo.getIsPermanent().equals(CalendarAction.ONE)) {
			return CalendarAction.TWO;
		}
		Timestamp statrtTime = actInfo.getStartTime();
		Timestamp endTime = actInfo.getEndTime();
		Timestamp nowTime = DateUtils.getSqlTimestamp();
		if (null == statrtTime || null == endTime) {
			logger().info("时间为null");
			return CalendarAction.FOUR;
		}
		if (statrtTime.before(nowTime) && endTime.after(nowTime)) {
			return CalendarAction.TWO;
		}
		if (statrtTime.after(nowTime)) {
			return CalendarAction.ONE;
		}
		return CalendarAction.FOUR;
	}

	/**
	 * 获取日历活动
	 *
	 * @param calendarId
	 * @return
	 */
	public MarketCalendarInfoVo getCalendarInfo(Integer calendarId) {
		MarketCalendarRecord record = getInfoById(calendarId);
		if (record == null) {
			return null;
		}
		MarketCalendarInfoVo vo = record.into(MarketCalendarInfoVo.class);
		vo.setCalendarId(record.getId());
		List<MarketCalendarActivityVo> calendarActList = calendarActivityService.calendarActList(calendarId);
		List<MarketVo> actInfoList = new ArrayList<MarketVo>();
		for (MarketCalendarActivityVo item : calendarActList) {
			String[] verPurview = saas.shop.version.verifyVerPurview(getShopId(), item.getActivityType());
            String strTrue = "true";
            if (verPurview[0].equals(strTrue)) {
				vo.setHasAct(true);
				if (item.getActivityId() > 0) {
					vo.setHasAct(true);
					ActInfoVo info = getActInfo(item.getActivityType(), item.getActivityId(), CalendarAction.INFO,null);
					MarketVo actInfo = info.getActInfo();
					actInfo.setRecommendLink(item.getRecommendLink());
					actInfo.setRecommendType(item.getRecommendType());
					actInfo.setCalActId(item.getId());
					actInfo.setIsRecommend(item.getSysCalActId() > 0 ? true : false);
					actInfoList.add(actInfo);
				}else {

					MarketVo actInfo=new MarketVo();
					actInfo.setActivityType(item.getActivityType());
					actInfo.setRecommendLink(item.getRecommendLink());
					actInfo.setRecommendType(item.getRecommendType());
					actInfo.setCalActId(item.getId());
					actInfo.setIsRecommend(item.getSysCalActId() > 0 ? true : false);
					actInfoList.add(actInfo);

				}
			}else {
				vo.setHasAct(false);
				logger().info("没有权限：{}",item.getActivityType());
			}
		}
		vo.setActInfo(actInfoList);
		return vo;
	}

	/**
	 * 概览用的营销日历
	 * @return
	 */
	public List<MarketCalendarVo> getOverviewList() {
		Date nowDate = DateUtils.yyyyMmDdDate(LocalDate.now());
		List<MarketCalendarVo> calendarList = db().selectFrom(MARKET_CALENDAR)
				.where(MARKET_CALENDAR.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(MARKET_CALENDAR.EVENT_TIME.ge(nowDate)))
				.orderBy(MARKET_CALENDAR.EVENT_TIME.asc()).fetchInto(MarketCalendarVo.class);
		List<MarketCalendarVo> dataList=new ArrayList<MarketCalendarVo>();
		for (MarketCalendarVo item : calendarList) {
			item = eventStatus(item);
			if (item.getEventStatus().equals(CalendarAction.ONE)) {
				int days = (int) ((item.getEventTime().getTime() - nowDate.getTime()) / (1000 * 60 * 60 * 24L));
				item.setDownTime(days);
			}
			if((!item.getEventStatus().equals(CalendarAction.THREE))&&(!item.getEventStatus().equals(CalendarAction.FOUR))) {
				dataList.add(item);
			}
			if(dataList.size()==3) {
				return dataList;
			}
		}
		return dataList;
	}

	/**
	 * 同步和推送
	 * @param param
	 * @return
	 */
	public boolean getPushInfo(MarketMqParam param) {
		MarketCalendarSysVo vo = param.getVo();
		MarketCalendarRecord record = db().selectFrom(MARKET_CALENDAR).where(MARKET_CALENDAR.SOURCE_ID.eq(vo.getId())).fetchAny();
		boolean flag=true;
		if(record!=null) {
			//之前发布
			record.setEventTime(vo.getEventTime());
			record.setEventName(vo.getEventName());
			record.setEventDesc(vo.getEventDesc());
			record.setDelFlag(vo.getDelFlag());
			int update = record.update();
			logger().info("店铺活动:{}，更新主库活动：{}，结果：{}",record.getId(),vo.getId(),update);
			if(update<=0) {
				flag=false;
			}
		}else {
			record = db().newRecord(MARKET_CALENDAR,vo);
			record.reset(MARKET_CALENDAR.ID);
			record.setSource(CalendarAction.ONE);
			record.setSourceId(vo.getId());
			int insert = record.insert();
			logger().info("店铺活动:{}，创建主库活动：{}，结果：{}",record.getId(),vo.getId(),insert);
			if(insert<=0) {
				flag=false;
			}
		}
		if(flag) {
			flag = calendarActivityService.getPushInfoInner(param,record.getId());
		}
		return flag;
	}

}
