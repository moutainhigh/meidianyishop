package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import com.meidianyi.shop.service.pojo.wxapp.decorate.WxAppPageModuleParam;
import com.meidianyi.shop.service.pojo.wxapp.decorate.WxAppPageParam;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppCommonParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lixinguo
 *
 */
@RestController
public class WxAppConfigController extends WxAppBaseController {

    public static final String PATIENT_TRUE = "1";
    public static final String PATIENT_FALSE = "0";

	/**
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/api/wxapp/cfg/bottom")
	public JsonResult config(@RequestBody WxAppCommonParam param) {
		return success(this.shop().config.getAppConfig(wxAppAuth.user()));
	}

	/**
	 * @param param
	 * @return
	 */
	@PostMapping("/api/wxapp/index")
	public JsonResult index(@RequestBody WxAppPageParam param) {
		return success(this.shop().mpDecoration.getPageInfo(param));
	}

    @PostMapping("/api/wxapp/page/module")
    public JsonResult module(@RequestBody WxAppPageModuleParam param) {
        return success(this.shop().mpDecoration.getPageModuleInfo(param));
    }

    @PostMapping("/api/wxapp/locale/get")
    public JsonResult getLocalePack(@RequestBody WxAppCommonParam param) {
        return success(this.shop().config.getLocalePack(getLang()));
    }

    @PostMapping("/api/wxapp/suspend")
    public JsonResult getSuspendWindowConfig(@RequestBody WxAppPageParam param) {
        return success(this.shop().mpDecoration.getSuspendWindowConfig(param));
    }

    /**
     * 	获取用户的患者列表
     */
    @PostMapping("/api/wxapp/user/patient/pop")
    public JsonResult getUserPatientFlag(@RequestBody UserPatientParam userPatient) {
        String patientFlag = wxAppAuth.getPatientFlag(userPatient.getUserId());
        Byte flag = 0;
        logger().info("patientFlag:"+patientFlag);
        if (PATIENT_TRUE.equals(patientFlag)) {
            List<PatientOneParam> patientList = shop().patientService.listPatientByUserId(userPatient.getUserId());
            if (patientList.size() == 0) {
                flag = 1;
            }
            wxAppAuth.setPatientFlag(userPatient.getUserId(),PATIENT_FALSE);
        }
        return success(flag);
    }
}
