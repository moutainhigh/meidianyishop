package com.meidianyi.shop.service.saas.marketcalendar;

import static com.meidianyi.shop.db.main.tables.MarketCalendar.MARKET_CALENDAR;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.main.tables.records.MarketCalendarActivityRecord;
import com.meidianyi.shop.db.main.tables.records.MarketCalendarRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarParam;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarSysAllVo;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarSysVo;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketMqParam;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketSysActivityMqParam;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.SysCalendarActVo;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketCalendarVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketListData;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketListDataVo;

/**
 * system营销日历
 *
 * @author zhaojianqiang 2020年4月24日下午2:34:17
 */
@Service
public class MarketSysCalendarService extends MainBaseService {
	@Autowired
	public MarketSysCalendarActivityService calendarActivityService;

	/**
	 * 营销日历列表
	 *
	 * @param year
	 * @return
	 */
	public MarketListDataVo getListByYear(String year) {
		if (StringUtils.isEmpty(year)) {
			year = String.valueOf(LocalDate.now().getYear());
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

	public MarketCalendarVo eventStatus(MarketCalendarVo param) {
		logger().info("开始判断状态");
		// 1未开始，2进行中，3失效，4已结束
		Date nowDate = Date.valueOf(LocalDate.now());
		if (param.getEventTime().after(nowDate)) {
			param.setEventStatus(CalendarAction.ONE);
		} else {
			param.setEventStatus(CalendarAction.TWO);
		}
		return param;
	}

	public List<String> getYearList() {
		Field<String> field = DSL.left(MARKET_CALENDAR.EVENT_TIME.cast(String.class), 4).as("year");
		return db().select(field).from(MARKET_CALENDAR).where(MARKET_CALENDAR.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.groupBy(field).orderBy(field.desc()).fetchInto(String.class);
	}

	/**
	 * 创建日历活动
	 *
	 * @param param
	 * @return
	 */
	public boolean addCalendar(MarketCalendarParam param) {
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
	public boolean edit(MarketCalendarParam param) {
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
		toEditNoPush(calendarId);
		return update == 1 ? true : false;
	}


	public MarketCalendarRecord getInfoById(Integer id) {
		return db().selectFrom(MARKET_CALENDAR).where(MARKET_CALENDAR.ID.eq(id)).fetchAny();
	}

	/**
	 * 同步消息
	 * @param calendarId
	 */
	public void toPush(Integer calendarId) {
		MarketCalendarRecord record = getInfoById(calendarId);
		if (record != null&&record.getPubFlag().equals(CalendarAction.ZERO)) {
			//创建个队列任务去同步
			push(record);
			record.setPubFlag(CalendarAction.ONE);
			int update = record.update();
			logger().info("更新状态为已同步：{}",update);
		}
	}

	/**
	 * 同步
	 * @param record
	 */
	private void push(MarketCalendarRecord record) {
		MarketMqParam param=new MarketMqParam();
		Result<MarketCalendarActivityRecord> result = calendarActivityService.getInfoByCalendarId(record.getId());
		List<MarketSysActivityMqParam> list = result.into(MarketSysActivityMqParam.class);
		param.setList(list);
		MarketCalendarSysVo into = record.into(MarketCalendarSysVo.class);
		param.setVo(into);
		logger().info("准备发队列");
		saas.taskJobMainService.dispatchImmediately(param,MarketMqParam.class.getName(),0,TaskJobEnum.SYS_CALENDAR_MQ.getExecutionType());
	}

	/**
	 * 编辑时候推送过的再推送
	 * @param calendarId
	 */
	public void toEditNoPush(Integer calendarId) {
		logger().info("toEditNoPush同步");
		MarketCalendarRecord record = getInfoById(calendarId);
		if (record != null&&record.getPubFlag().equals(CalendarAction.ONE)) {
			push(record);
		}
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public boolean del(Integer id) {
		int execute = db().update(MARKET_CALENDAR).set(MARKET_CALENDAR.DEL_FLAG, DelFlag.DISABLE_VALUE)
				.where(MARKET_CALENDAR.ID.eq(id)).execute();
		boolean flag=execute > 0 ? true : false;
		if(flag) {
			toEditNoPush(id);
		}
		return flag;
	}

	/**
	 * 单个信息
	 * @param calendarId
	 * @return
	 */
	public MarketCalendarSysAllVo getCalendarInfo(Integer calendarId) {
		MarketCalendarRecord record = getInfoById(calendarId);
		if(record==null) {
			return null;
		}
		MarketCalendarSysAllVo info = record.into(MarketCalendarSysAllVo.class);
		info.setCalendarId(calendarId);
		List<SysCalendarActVo> calendarActList = calendarActivityService.calendarActList(calendarId);
		info.setActInfo(calendarActList);
		return info;
	}
}
