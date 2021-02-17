package com.meidianyi.shop.service.saas.marketcalendar;

import static com.meidianyi.shop.db.main.tables.MarketCalendarActivity.MARKET_CALENDAR_ACTIVITY;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.db.main.tables.records.MarketCalendarActivityRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarParam;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.SysCalendarAct;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.SysCalendarActVo;

/**
 * system用的详情
 * 
 * @author zhaojianqiang 2020年4月24日下午3:31:44
 */
@Service
public class MarketSysCalendarActivityService extends MainBaseService {
	/**
	 * 新增
	 * 
	 * @param param
	 * @param calendarId
	 */
	public void addCalendarAct(MarketCalendarParam param, Integer calendarId) {
		List<SysCalendarAct> calendarActList = param.getCalendarAct();
		for (SysCalendarAct sysCalendarAct : calendarActList) {
			MarketCalendarActivityRecord record = db().newRecord(MARKET_CALENDAR_ACTIVITY, sysCalendarAct);
			record.setCalendarId(calendarId == null ? 0 : calendarId);
			int insert = record.insert();
			logger().info("system新增日历活动id：{}；营销活动：{}；结果：{}", calendarId, sysCalendarAct.getActivityType(), insert);
		}
	}

	/**
	 * 更新
	 * @param param
	 * @param calendarId
	 */
	public void editCalendarAct(MarketCalendarParam param, Integer calendarId) {
		List<SysCalendarAct> calendarActList = param.getCalendarAct();
		for (SysCalendarAct sysCalendarAct : calendarActList) {
			Integer id = sysCalendarAct.getId();
			if (id != null && id != 0) {
				MarketCalendarActivityRecord record = db().selectFrom(MARKET_CALENDAR_ACTIVITY).where(
						MARKET_CALENDAR_ACTIVITY.ID.eq(id).and(MARKET_CALENDAR_ACTIVITY.CALENDAR_ID.eq(calendarId)))
						.fetchAny();
				if (record != null) {
					record.setActivityType(sysCalendarAct.getActivityType());
					record.setRecommendType(sysCalendarAct.getRecommendType());
					record.setRecommendLink(sysCalendarAct.getRecommendLink());
					record.setDelFlag(sysCalendarAct.getDelFlag());
					int update = record.update();
					logger().info("system更新日历活动id：{}；营销活动：{}；结果：{}", calendarId, id, update);
				}else {
					logger().info("system更新日历活动id：{}；营销活动id：{}；不匹配", calendarId, id);
				}
			} else {
				MarketCalendarActivityRecord record = db().newRecord(MARKET_CALENDAR_ACTIVITY, sysCalendarAct);
				record.setCalendarId(calendarId == null ? 0 : calendarId);
				int update = record.insert();
				logger().info("system更新中新增日历活动id：{}；营销活动：{}；结果：{}", calendarId, sysCalendarAct.getActivityType(),
						update);
			}

		}
	}

	public MarketCalendarActivityRecord getInfoById(Integer id) {
		return db().selectFrom(MARKET_CALENDAR_ACTIVITY).where(MARKET_CALENDAR_ACTIVITY.ID.eq(id)).fetchAny();
	}

	public Result<MarketCalendarActivityRecord> getInfoByCalendarId(Integer calendarId) {
		return db().selectFrom(MARKET_CALENDAR_ACTIVITY).where(MARKET_CALENDAR_ACTIVITY.CALENDAR_ID.eq(calendarId))
				.fetch();
	}

	public List<SysCalendarActVo> calendarActList(Integer calendarId) {
		List<SysCalendarActVo> list = db().selectFrom(MARKET_CALENDAR_ACTIVITY).where(MARKET_CALENDAR_ACTIVITY.DEL_FLAG
				.eq(DelFlag.NORMAL_VALUE).and(MARKET_CALENDAR_ACTIVITY.CALENDAR_ID.eq(calendarId)))
				.fetchInto(SysCalendarActVo.class);
		for (SysCalendarActVo item : list) {
			String shopIds = item.getShopIds();
			if(StringUtils.isEmpty(shopIds)) {
				item.setShopNum(0);
			}else {
				String[] split = shopIds.split(",");
				item.setShopNum(split.length);				
			}
		}
		return list;
	}

}
