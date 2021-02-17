package com.meidianyi.shop.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.ActInfoVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAct;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarkActivityListParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketCalendarInfoVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketcalendarParam;

/**
 * 营销日历相关
 * 
 * @author zhaojianqiang 2020年4月20日下午2:47:07
 */
@RestController
public class AdminMarketcalendarController extends AdminBaseController {

	/**
	 * 新建和编辑
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/admin/calendar/market/up")
	public JsonResult marketCalendarUp(@RequestBody MarketcalendarParam param) {
		String act = param.getAct();
		for (CalendarAct calendarAct : param.getCalendarAct()) {
			String[] strings = saas.shop.version.verifyVerPurview(shopId(), calendarAct.getActivityType());
			if("false".equals(strings[0])) {
				 return this.fail(JsonResultCode.SOME_NO_AUTH,""+calendarAct.getActivityType());
			}
		}
		if (act.equals(CalendarAction.ADD)) {
			logger().info("新建");
			boolean addCalendar = shop().calendarService.addCalendar(param);
			if (addCalendar) {
				return success();
			}
		}
		if (act.equals(CalendarAction.EDIT)) {
			logger().info("更新");
			if (param.getCalendarId() == null) {
				// id不能为0
				return fail();
			}
			boolean edit = shop().calendarService.edit(param);
			if (edit) {
				return success();
			}
		}
		return fail();
	}

	/**
	 * 删除
	 * 
	 * @param calendarId
	 * @return
	 */
	@GetMapping(value = "/api/admin/calendar/market/del/{calendarId}")
	public JsonResult markCalendarDel(@PathVariable Integer calendarId) {
		boolean del = shop().calendarService.del(calendarId);
		if (del) {
			return success();
		}
		return fail();
	}

	/**
	 * 营销日历列表
	 * 
	 * @param year
	 * @return
	 */
	@GetMapping(value = "/api/admin/calendar/list/{year}")
	public JsonResult calendarList(@PathVariable String year) {
		return success(shop().calendarService.getListByYear(year));
	}

	/**
	 * 营销日历详情
	 * 
	 * @param calendarId
	 * @return
	 */
	@GetMapping(value = "/api/admin/calendar/info/{calendarId}")
	public JsonResult calendarInfo(@PathVariable Integer calendarId) {
		MarketCalendarInfoVo calendarInfo = shop().calendarService.getCalendarInfo(calendarId);
		if (calendarInfo != null) {
			return success(calendarInfo);
		}
		return fail();
	}
	
	/**
	 * 查询营销活动对应的所有可用活动
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/admin/calendar/market/list")
	public JsonResult getMarketActivity(@RequestBody MarkActivityListParam param) {
		MarketParam marketParam = new MarketParam();
		marketParam.setCurrentPage(param.getCurrentPage());
		marketParam.setPageRows(param.getPageRows());
		ActInfoVo actInfo = shop().calendarService.getActInfo(param.getActivityType(), null,CalendarAction.LIST, marketParam);
		return success(actInfo.getList());
	}
	

	/**
	 * 删除营销日历活动
	 * @param calActId
	 * @return
	 */
	@GetMapping(value = "/admin/calendar/market/act/del/{calActId}")
	public JsonResult marketCalendarActDel(@PathVariable Integer calActId) {
		boolean delInfo = shop().calendarService.calendarActivityService.delInfo(calActId);
		if(delInfo) {
			return success();
		}
		return fail();
	}
	
	
	/**
	 * 营销日历列表-概览里用
	 * 
	 * @param year
	 * @return
	 */
	@GetMapping(value = "/api/admin/calendar/limitlist")
	public JsonResult calendarList() {
		return success(shop().calendarService.getOverviewList());
	}
	
}
