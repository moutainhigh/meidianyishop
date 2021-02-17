package com.meidianyi.shop.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.store.schedule.SchedulePojo;
import com.meidianyi.shop.service.pojo.shop.store.schedule.TechnicianScheduleParam;
import com.meidianyi.shop.service.pojo.shop.store.schedule.TechnicianScheduleSaveParam;
import com.meidianyi.shop.service.pojo.shop.store.schedule.TechnicianScheduleVo;

/**
 * @author 黄荣刚
 * @date 2019年7月16日
 *
 */
@RestController
@RequestMapping("/api/admin/store")
public class AdminServiceScheduleController extends AdminBaseController {
	/**
	 * 查找该店铺下的所有班次
	 * @param storeId
	 * @return
	 */
	@GetMapping("/services/schedule/list/{storeId}")
	public JsonResult getScheduleList(@PathVariable Integer storeId){
		List<SchedulePojo> scheduleList = shop().store.serviceTechnician.scheduleService.getScheduleList(storeId);
		return success(scheduleList);
	}
	/**
	 * 增加新班次
	 * @param schedule
	 * @return
	 */
	@PostMapping("/services/schedule/add")
	public JsonResult insertSchedule(@RequestBody @Valid SchedulePojo schedule) {
		int result = shop().store.serviceTechnician.scheduleService.insertSchedule(schedule);
		if(result == 0) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}
	/**
	 * 删除班次
	 * @param scheduleId
	 * @return
	 */
	@PostMapping("/services/schedule/delete/{scheduleId}")
	public JsonResult deleteSchedule(@PathVariable Byte scheduleId) {
		int result = shop().store.serviceTechnician.scheduleService.deleteSchedule(scheduleId);
		if(result>0) {
			return success(JsonResultCode.CODE_SUCCESS);
		}else {
			return fail(JsonResultCode.CODE_FAIL);
		}
	}
	/**
	 * 修改班次
	 * @param pojo
	 * @return
	 */
	@PostMapping("/services/schedule/update")
	public JsonResult updateSchedule(@RequestBody @Valid SchedulePojo pojo) {
		if(pojo.getScheduleId() == null) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		int result = shop().store.serviceTechnician.scheduleService.updateSchedule(pojo);
		if(result>0) {
			return success(JsonResultCode.CODE_SUCCESS);
		}else {
			return fail(JsonResultCode.CODE_FAIL);
		}
	}
	@PostMapping("/services/technician/schedule/list")
	public JsonResult selectTechnicianSchedule(@RequestBody @Valid TechnicianScheduleParam param) {
		List<TechnicianScheduleVo> list = shop().store.serviceTechnician.scheduleService.selectTechnicianSchedule(param);
		return success(list);
	}
	@PostMapping("/services/technician/schedule/save")
	public JsonResult saveTechnicianSchedule(@RequestBody @Valid TechnicianScheduleSaveParam param) {
		shop().store.serviceTechnician.scheduleService.saveTechnicianSchedule(param);
		return success(JsonResultCode.CODE_SUCCESS);
	}
}
