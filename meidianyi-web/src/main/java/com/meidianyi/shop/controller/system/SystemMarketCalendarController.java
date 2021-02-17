package com.meidianyi.shop.controller.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarParam;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketCalendarSysAllVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;

/**
 * system营销日历
 * 
 * @author zhaojianqiang 2020年4月24日下午2:29:04
 */
@RestController
public class SystemMarketCalendarController extends SystemBaseController {

	/**
	 * 营销日历列表
	 * 
	 * @param year
	 * @return
	 */
	@GetMapping(value = "/api/system/calendar/list/{year}")
	public JsonResult calendarList(@PathVariable String year) {
		return success(saas.shop.calendarService.getListByYear(year));
	}

	
	/**
	 * 更新和新建
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/system/calendar/market/up")
	public JsonResult marketCalendarUp(@RequestBody MarketCalendarParam param) {
		String act = param.getAct();
		if (act.equals(CalendarAction.ADD)) {
			logger().info("新建");
			boolean addCalendar = saas.shop.calendarService.addCalendar(param);
			if(addCalendar) {
				return success();
			}
		}
		if (act.equals(CalendarAction.EDIT)) {
			logger().info("更新");
			if (param.getCalendarId() == null) {
				// id不能为0
				return fail();
			}
			boolean edit = saas.shop.calendarService.edit(param);
			if (edit) {
				return success();
			}
		}
		return fail();

	}
	
	/**
	 *发布 
	 * @param calendarId
	 * @return
	 */
	@GetMapping(value = "/api/system/calendar/market/pub/{calendarId}")
	public JsonResult marketCalendarPub(@PathVariable Integer calendarId) {
		saas.shop.calendarService.toPush(calendarId);
		return success();
	}
	
	
	/**
	 * 删除
	 * 
	 * @param calendarId
	 * @return
	 */
	@GetMapping(value = "/api/system/calendar/market/del/{calendarId}")
	public JsonResult markCalendarDel(@PathVariable Integer calendarId) {
		boolean del = saas.shop.calendarService.del(calendarId);
		if (del) {
			return success();
		}
		return fail();
	}

	/**
	 * 营销日历详情
	 * 
	 * @param calendarId
	 * @return
	 */
	@GetMapping(value = "/api/system/calendar/info/{calendarId}")
	public JsonResult calendarInfo(@PathVariable Integer calendarId) {
		MarketCalendarSysAllVo info = saas.shop.calendarService.getCalendarInfo(calendarId);
		if(info==null) {
			return fail();
		}
		return success(info);
	}
}
