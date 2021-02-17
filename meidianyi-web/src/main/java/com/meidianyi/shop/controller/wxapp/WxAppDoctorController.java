package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.shop.table.DoctorDo;
import com.meidianyi.shop.common.pojo.shop.table.DoctorLoginLogDo;
import com.meidianyi.shop.config.SmsApiConfig;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.auth.AuthConstant;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentListVo;
import com.meidianyi.shop.service.pojo.shop.doctor.*;
import com.meidianyi.shop.service.pojo.shop.message.DoctorMainShowParam;
import com.meidianyi.shop.service.pojo.shop.message.DoctorMessageCountVo;
import com.meidianyi.shop.service.pojo.shop.patient.PatientSmsCheckParam;
import com.meidianyi.shop.service.pojo.shop.sms.template.SmsTemplate;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.shop.doctor.DoctorLoginLogService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.message.UserMessageService;
import com.meidianyi.shop.service.shop.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.*;
import static com.meidianyi.shop.service.pojo.shop.auth.AuthConstant.AUTH_TYPE_DOCTOR_USER;

/**
 * @Description 医师端
 * @Author 赵晓东
 * @Create 2020-07-22 14:15
 **/

@RestController
public class WxAppDoctorController extends WxAppBaseController {


    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserMessageService messageService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private DoctorLoginLogService doctorLoginLogService;

    /**
     * 医师认证接口
     * @param doctorAuthParam 认证医师姓名，手机号，医师医院唯一编码
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/doctor/auth")
    public JsonResult doctorAuth(@Validated @RequestBody DoctorAuthParam doctorAuthParam) {
        if (wxAppAuth.user().getUserType() != null && !AuthConstant.AUTH_TYPE_NORMAL_USER.equals(wxAppAuth.user().getUserType())) {
            return fail(AUTH_ALREADY_AUTHED);
        }
        doctorAuthParam.setUserId(wxAppAuth.user().getUserId());
        Integer doctorId = doctorService.doctorAuth(doctorAuthParam);
        // 如果医师id!=null 更新缓存
        if (doctorId != null) {
            wxAppAuth.updateUserType(doctorId);
            return success();
        } else {
            return fail(DOCTOR_LOGIN_AUTH_ERROR);
        }
    }

    /**
     * 发送验证码
     * @param param 电话号
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/doctor/send/check/code")
    public JsonResult sendCheckSms(@RequestBody @Validated PatientSmsCheckParam param){
        param.setUserId(wxAppAuth.user().getUserId());
        try {
            JsonResultCode jsonResultCode = smsService.checkIsOutOfSmsNum(wxAppAuth.user().getUserId(), "");
            if (!jsonResultCode.equals(CODE_SUCCESS)){
                return fail(jsonResultCode);
            }
            smsService.sendCheckSms(param, SmsTemplate.DOCTOR_CHECK_MOBILE, SmsApiConfig.REDIS_KEY_SMS_CHECK_DOCTOR_MOBILE);
        } catch (MpException e) {
            return fail(e.getErrorCode());
        }
        return success();
    }

    /**
     * 获取当前医师登录的信息
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/doctor/auth/info")
    public JsonResult getDoctorAuthInfo(){
        WxAppSessionUser user=wxAppAuth.user();
        DoctorOneParam doctor= null;
        try {
            doctor = doctorService.getOneInfo(user.getDoctorId());
        } catch (MpException e) {
            return fail(e.getErrorCode());
        }
        return success(doctor);
    }

    /**
     * 医师端首页信息展示 消息统计和医师个人信息
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/doctor/main")
    public JsonResult doctorMainShow(@RequestBody DoctorMainShowParam doctorMainShowParam){
        // 获取缓存中当前用户信息
        WxAppSessionUser user = wxAppAuth.user();
        // 获取页面消息统计信息
        DoctorMessageCountVo doctorMessageCountVo =
            messageService.countDoctorMessage(user.getDoctorId(), doctorMainShowParam);
        // 获取医师首页个人信息
        DoctorOneParam oneInfo = null;
        try {
            oneInfo = doctorService.getOneInfo(user.getDoctorId());
        } catch (MpException e) {
            e.printStackTrace();
        }
        DoctorMainShowVo doctorMainShowVo = new DoctorMainShowVo();
        //添加医师职称
        String duty = doctorService.selectDoctorTitle(oneInfo);
        doctorMainShowVo.setDoctorTitle(duty);
        FieldsUtil.assign(oneInfo, doctorMainShowVo);
        doctorMainShowVo.setDoctorMessageCountVo(doctorMessageCountVo);
        // 获取医师所属科室列表
        List<DepartmentListVo> departmentListVos =
            doctorService.selectDepartmentsByDoctorId(user.getDoctorId());
        List<String> list = new ArrayList<>();
        for (DepartmentListVo departmentListVo : departmentListVos){
            list.add(departmentListVo.getName());
        }
        doctorMainShowVo.setDepartmentName(list);
        DoctorAttendanceVo attendance = doctorService.getAttendance(user.getUserId(), oneInfo.getHospitalCode(), user.getDoctorId());
        doctorMainShowVo.setDoctorMonthData(attendance);
        return super.success(doctorMainShowVo);
    }

    /**
     * 医师登录记录
     * -每次切换到医师端就增加记录
     * @return
     */
    @PostMapping("/api/wxapp/doctor/main/log")
    public JsonResult doctorLoginLog(){
        WxAppSessionUser user = wxAppAuth.user();
        if (user.getUserType().equals(AUTH_TYPE_DOCTOR_USER)){
            DoctorLoginLogDo param =new DoctorLoginLogDo();
            param.setDoctorId(user.getDoctorId());
            param.setUserId(user.getUserId());
            param.setIp(Util.getCleintIp(request));
            doctorLoginLogService.save(param);
        }
        return success();
    }

    /**
     * 小程序端修改医师信息
     * @param doctorOneParam 医师信息入参
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/doctor/update/Information")
    public JsonResult addDoctorInformation(@RequestBody DoctorOneParam doctorOneParam) {
        if (doctorOneParam.getId() == null) {
            return fail(JsonResultCode.DOCTOR_ID_IS_NULL);
        }
        try {
            doctorService.updateDoctor(doctorOneParam);
            return success();
        } catch (MpException e) {
            return fail();
        }
    }

    /**
     * 小程序端展示医师详情
     * @param doctorOneParam 医师id
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/doctor/show/Information")
    public JsonResult adminDoctorDetails(@RequestBody DoctorOneParam doctorOneParam) {
        // 医师科室
        List<DepartmentListVo> departmentListVos =
            doctorService.selectDepartmentsByDoctorId(doctorOneParam.getId());
        List<String> departmentNames = departmentListVos.stream().map(DepartmentListVo::getName).collect(Collectors.toList());
        DoctorOneParam oneInfo = null;
        try {
            oneInfo = doctorService.getOneInfo(doctorOneParam.getId());
        } catch (MpException e) {
            return fail();
        }
        //添加医师职称
        String title = doctorService.selectDoctorTitle(oneInfo);
        assert oneInfo != null;
        oneInfo.setTitleName(title);
        oneInfo.setDepartmentNames(departmentNames);
        return success(oneInfo);
    }
}
