package com.meidianyi.shop.service.shop.marketcalendar;

import static com.meidianyi.shop.db.shop.tables.MarketCalendarActivity.MARKET_CALENDAR_ACTIVITY;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.db.shop.tables.records.MarketCalendarActivityRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarSysVo;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketMqParam;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketSysActivityMqParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAct;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketCalendarActivityVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketcalendarParam;

/**
 * @author zhaojianqiang
 */
@Service
public class MarketCalendarActivityService extends ShopBaseService {

	/**
	 * 新增
	 * 
	 * @param param
	 * @param calendarId
	 */
	public void addCalendarAct(MarketcalendarParam param, Integer calendarId) {
		List<CalendarAct> calendarActList = param.getCalendarAct();
		for (CalendarAct calendarAct : calendarActList) {
			MarketCalendarActivityRecord record = db().newRecord(MARKET_CALENDAR_ACTIVITY, calendarAct);
			record.setCalendarId(calendarId == null ? 0 : calendarId);
			int insert = record.insert();
			logger().info("更新日历活动id：{}；营销活动：{}；结果：{}", calendarId, calendarAct.getActivityId(), insert);
		}
	}

	/**
	 * 更新
	 * 
	 * @param param
	 * @param calendarId
	 */
	public void editCalendarAct(MarketcalendarParam param, Integer calendarId) {
		List<CalendarAct> calendarActList = param.getCalendarAct();
		for (CalendarAct calendarAct : calendarActList) {
			Integer id = calendarAct.getCalActId();
			if (id > 0) {
				logger().info("编辑的id：{}", id);
				MarketCalendarActivityRecord activityRecord = db().selectFrom(MARKET_CALENDAR_ACTIVITY).where(
						MARKET_CALENDAR_ACTIVITY.ID.eq(id).and(MARKET_CALENDAR_ACTIVITY.CALENDAR_ID.eq(calendarId)))
						.fetchAny();
				Integer sysCalActId = activityRecord.getSysCalActId();
				if (sysCalActId > 0) {
					logger().info("为推荐活动calendar_activity的id:{}", sysCalActId);
					Integer activityId = calendarAct.getActivityId();
					activityRecord.setActivityId(activityId);
					Byte isSync = activityRecord.getIsSync();
					if (isSync == 0 && activityId > 0) {
						// 没同步到system
						logger().info("没有同步到system");
						com.meidianyi.shop.db.main.tables.records.MarketCalendarActivityRecord sysCalActInfo = saas.shop.calendarService.calendarActivityService
								.getInfoById(sysCalActId);
						if (sysCalActInfo != null) {
							String shopIds = sysCalActInfo.getShopIds();
							if (!Objects.equals(shopIds, "")) {
								shopIds = "," + getShopId();
							} else {
								shopIds = String.valueOf(getShopId());
							}
							sysCalActInfo.setShopUseNum(sysCalActInfo.getShopUseNum() + 1);
							sysCalActInfo.setShopIds(shopIds);
							int update = sysCalActInfo.update();
							logger().info("更新主库calendar_activity的活动id：{}；结果：{}", sysCalActId, update);
							if (update > 0) {
								activityRecord.setIsSync(CalendarAction.ONE);
							}
						}
					}

					int update = activityRecord.update();
					logger().info("更新推荐的日历活动id：{}；calendarId:{},营销活动：{}；结果：{}", id, calendarId, update);
				} else {
					logger().info("更新");
					activityRecord.setDelFlag(calendarAct.getDelFlag());
					activityRecord.setActivityId(calendarAct.getActivityId());
					activityRecord.setActivityType(calendarAct.getActivityType());
					int update = activityRecord.update();
					logger().info("更新id为：{}的数据，结果：{}", id, update);
				}

			} else {
				// 新建的
				logger().info("新建的");
				MarketCalendarActivityRecord record = db().newRecord(MARKET_CALENDAR_ACTIVITY, calendarAct);
				record.setCalendarId(calendarId == null ? 0 : calendarId);
				int insert = record.insert();
				logger().info("插入日历活动id：{}；营销活动：{}；结果：{}", calendarId, calendarAct.getActivityId(), insert);
			}
		}
	}

	public List<MarketCalendarActivityVo> calendarActList(Integer calendarId) {
		return db().selectFrom(MARKET_CALENDAR_ACTIVITY)
				.where(MARKET_CALENDAR_ACTIVITY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(MARKET_CALENDAR_ACTIVITY.CALENDAR_ID.eq(calendarId)))
				.fetchInto(MarketCalendarActivityVo.class);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public boolean delInfo(Integer id) {
		int execute = db().update(MARKET_CALENDAR_ACTIVITY)
				.set(MARKET_CALENDAR_ACTIVITY.DEL_FLAG, DelFlag.DISABLE_VALUE).where(MARKET_CALENDAR_ACTIVITY.ID.eq(id))
				.execute();
		return execute == 1;
	}

	/**
	 * 同步system的消息
	 * 
	 * @param param
	 * @return 
	 */
	public boolean getPushInfoInner(MarketMqParam param,Integer calendarId) {
		List<MarketSysActivityMqParam> list = param.getList();
		MarketCalendarSysVo vo = param.getVo();
		boolean flag = true;
		for (MarketSysActivityMqParam item : list) {
			MarketCalendarActivityRecord record = db().selectFrom(MARKET_CALENDAR_ACTIVITY)
					.where(MARKET_CALENDAR_ACTIVITY.SYS_CAL_ACT_ID.eq(item.getId())).fetchAny();
			if (record != null) {
				// 更新
				record.setCalendarId(calendarId);
				record.setActivityType(item.getActivityType());
				if (!record.getActivityType().equals(item.getActivityType())) {
					record.reset(MARKET_CALENDAR_ACTIVITY.ACTIVITY_ID);
				}
				record.setRecommendType(item.getRecommendType());
				record.setRecommendLink(item.getRecommendLink());
				record.setDelFlag(item.getDelFlag());
				int update = record.update();
				logger().info("更新system的推送到店铺：{}，system对应id为：{}，结果：{}", getShopId(), item.getId(), update);
				if (update <= 0) {
					flag = false;
				}
			} else {
				record = db().newRecord(MARKET_CALENDAR_ACTIVITY, item);
				record.setCalendarId(calendarId);
				record.setSysCalActId(item.getId());
				record.reset(MARKET_CALENDAR_ACTIVITY.ID);
				int insert = record.insert();
				logger().info("新建system的推送到店铺：{}，system对应id为：{}，结果：{}", getShopId(), item.getId(), insert);
				if (insert <= 0) {
					flag = false;
				}
			}
		}
		return flag;
	}
}
