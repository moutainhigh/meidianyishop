package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.PatientDo;
import com.meidianyi.shop.common.pojo.shop.table.UserPatientCoupleDo;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.patient.*;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionNoParam;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.prescription.FetchPrescriptionService;
import com.meidianyi.shop.service.shop.sms.SmsAccountService;
import com.meidianyi.shop.service.shop.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.CODE_SUCCESS;
import static com.meidianyi.shop.service.shop.prescription.FetchPatientInfoConstant.*;

/**
 * @author chenjie
 */
@RestController
@Slf4j
public class WxAppPatientController extends WxAppBaseController {
    @Autowired
    private SmsAccountService smsAccountService;

    @Autowired
    private FetchPrescriptionService fetchPrescriptionService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SmsService smsService;

    /**
     * 	获取用户的患者列表
     */
    @PostMapping("/api/wxapp/user/patient/list")
    public JsonResult getUserAccountWithdraw(@RequestBody UserPatientParam userPatient) {
        List<PatientOneParam> patientList = shop().patientService.listPatientByUserId(userPatient.getUserId());
        return success(patientList);
    }

    /**
     * 	切换默认患者
     */
    @PostMapping("/api/wxapp/user/patient/set/default")
    public JsonResult setDefaultPatient(@RequestBody UserPatientParam userPatient) {
        shop().patientService.setDefaultPatient(userPatient);
        return success();
    }

    private static final Map<String, UserPatientOneParam> MAP = new HashMap<>();
    {
        UserPatientOneParam userPatientOneParam1 = new UserPatientOneParam("武帅锦", "15533545085", "130302200610282215");
        UserPatientOneParam userPatientOneParam2 = new UserPatientOneParam("陆思雨", "13292327435", "130321199805060616");
        UserPatientOneParam userPatientOneParam3 = new UserPatientOneParam("俞宸懋", "17733410007", "23071519950208001X");
        UserPatientOneParam userPatientOneParam4 = new UserPatientOneParam("陈力达", "18716006027", "130321198602062115");
        UserPatientOneParam userPatientOneParam5 = new UserPatientOneParam("朱福新", "13643359617", "130302196001011613");
        UserPatientOneParam userPatientOneParam6 = new UserPatientOneParam("刘晓文", "18230318889", "130302197306094813");
        UserPatientOneParam userPatientOneParam7 = new UserPatientOneParam("李宛珊", "13933910537", "130302201103101621");
        UserPatientOneParam userPatientOneParam8 = new UserPatientOneParam("徐秀娟", "13180180373", "130225195609256947");
        UserPatientOneParam userPatientOneParam9 = new UserPatientOneParam("费丽莉", "13784561122", "130302197906200026");
        UserPatientOneParam userPatientOneParam10 = new UserPatientOneParam("曹淑清", "13303358852", "231083196103192124");
        UserPatientOneParam userPatientOneParam11 = new UserPatientOneParam("张新春", "15503351928", "232602196301091848");
        UserPatientOneParam userPatientOneParam12 = new UserPatientOneParam("张翠兰", "13293157229", "130321195305122763");
        UserPatientOneParam userPatientOneParam13 = new UserPatientOneParam("周春梅", "18833516467", "230227198011230144");
        UserPatientOneParam userPatientOneParam14 = new UserPatientOneParam("刘淑芬", "13731398292", "130302196607023926");
        MAP.put(userPatientOneParam1.getMobile(), userPatientOneParam1);
        MAP.put(userPatientOneParam2.getMobile(), userPatientOneParam2);
        MAP.put(userPatientOneParam3.getMobile(), userPatientOneParam3);
        MAP.put(userPatientOneParam4.getMobile(), userPatientOneParam4);
        MAP.put(userPatientOneParam5.getMobile(), userPatientOneParam5);
        MAP.put(userPatientOneParam6.getMobile(), userPatientOneParam6);
        MAP.put(userPatientOneParam7.getMobile(), userPatientOneParam7);
        MAP.put(userPatientOneParam8.getMobile(), userPatientOneParam8);
        MAP.put(userPatientOneParam9.getMobile(), userPatientOneParam9);
        MAP.put(userPatientOneParam10.getMobile(), userPatientOneParam10);
        MAP.put(userPatientOneParam11.getMobile(), userPatientOneParam11);
        MAP.put(userPatientOneParam12.getMobile(), userPatientOneParam12);
        MAP.put(userPatientOneParam13.getMobile(), userPatientOneParam13);
        MAP.put(userPatientOneParam14.getMobile(), userPatientOneParam14);
    }

    /**
     * 	拉取患者信息
     */
    @PostMapping("/api/wxapp/user/patient/get/info")
    public JsonResult getPatientInfo(@RequestBody @Validated UserPatientOneParam userPatientOneParam) {
        if (MAP.get(userPatientOneParam.getMobile()) == null) {
            // 校验验证码
            boolean b = fetchPrescriptionService.checkMobileCode(userPatientOneParam);
            if (!b) {
                return fail(JsonResultCode.PATIENT_MOBILE_CHECK_CODE_ERROR);
            }
        }
        // 拉取患者信息
        Integer info = fetchPrescriptionService.fetchPatientInfo(userPatientOneParam);
        if (FETCH_HIS_NO_PATIENT.equals(info)) {
            return fail(JsonResultCode.FETCH_HIS_NO_PATIENT);
        }
        return success();
    }

    /**
     * 刷新患者信息
     * @param userPatientWithoutCheckCodeParam 患者信息
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/user/patient/refresh/info")
    public JsonResult getPatientWithoutCheckCode(@RequestBody @Validated UserPatientWithoutCheckCodeParam userPatientWithoutCheckCodeParam) {
        boolean fetchPatient = fetchPrescriptionService.isFetchPatient(userPatientWithoutCheckCodeParam);
        // 如果没拉取过提示跳转至输入验证码界面
        if (fetchPatient) {
            return fail(JsonResultCode.TO_FETCH_PATIENT);
        } else { // 如果拉取过
            UserPatientOneParam userPatientOneParam = new UserPatientOneParam();
            FieldsUtil.assign(userPatientWithoutCheckCodeParam, userPatientOneParam);
            Integer info = fetchPrescriptionService.fetchPatientInfo(userPatientOneParam);
            if (FETCH_HIS_NO_PATIENT.equals(info)) {
                return fail(JsonResultCode.FETCH_HIS_NO_PATIENT);
            }
            return success();
        }
    }

    /**
     * 根据姓名手机号获取身份证号
     * @param userPatientFetchParam 用户信息入参
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/user/patient/get/id")
    public JsonResult getPatientByNameAndMobile(@RequestBody UserPatientFetchParam userPatientFetchParam) {
        return success(fetchPrescriptionService.getPatientName(userPatientFetchParam));
    }

    /**
     * 发送短信校验
     * @return
     */
    @PostMapping("/api/wxapp/user/patient/send/sms")
    public JsonResult sendCheckSms(@RequestBody @Validated PatientSmsCheckNumParam param){
        // 判断该用户今日验证码发送是否超额
        JsonResultCode jsonResultCode = smsService.checkIsOutOfSmsNum(wxAppAuth.user().getUserId(), "");
        if (!jsonResultCode.equals(CODE_SUCCESS)) {
            return fail(jsonResultCode);
        }
        // 判断该患者今日验证码是否超额
        jsonResultCode = smsService.checkUserSmsNum(param);
        if (!jsonResultCode.equals(CODE_SUCCESS)) {
            return fail(jsonResultCode);
        }
        param.setUserId(wxAppAuth.user().getUserId());
        try {
            shop().patientService.sendCheckSms(param);
        } catch (MpException e) {
            return fail();
        }
        return success();
    }

    /**
     * 	手动添加患者
     */
    @PostMapping("/api/wxapp/user/patient/add")
    public JsonResult addPatient(@RequestBody PatientAddParam patientAddParam) {
        Integer userId = patientAddParam.getUserId();
        PatientDo patientDo = new PatientDo();
        FieldsUtil.assign(patientAddParam,patientDo);
        UserPatientCoupleDo userPatientCoupleDo = new UserPatientCoupleDo();
        if (patientDo.getId() >0) {
            shop().patientService.updatePatient(patientDo);
        } else {
            userPatientCoupleDo.setIsFetch(PatientConstant.UN_FETCH);
            PatientExternalRequestParam param = new PatientExternalRequestParam();
            FieldsUtil.assign(patientDo,param);
            Integer patientId = shop().patientService.getPatientExist(param);
            if (patientId == null) {
                shop().patientService.insertPatient(patientDo);
            } else {
                patientDo.setId(patientId);
                UserPatientParam userPatientParam = new UserPatientParam();
                userPatientParam.setUserId(userId);
                userPatientParam.setPatientId(patientId);
                Boolean isExist = shop().patientService.isExistUserPatient(userPatientParam);
                if (isExist) {
                    return fail(JsonResultCode.PATIENT_IS_EXIST);
                }
                shop().patientService.updatePatient(patientDo);
            }
        }
        FieldsUtil.assign(patientDo,userPatientCoupleDo);
        userPatientCoupleDo.setId(null);
        userPatientCoupleDo.setPatientId(patientDo.getId());
        userPatientCoupleDo.setUserId(userId);
        shop().patientService.addPatientUser(userPatientCoupleDo);
        return success();
    }

    /**
     * 	患者详情
     */
    @PostMapping("/api/wxapp/user/patient/get/detail")
    public JsonResult getPatientDetail(@RequestBody UserPatientParam param) {
        UserPatientDetailVo patientDetail = shop().patientService.getOneDetail(param);
        return success(patientDetail);
    }

    /**
     * 待审核处方查看患者信息
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/user/patient/show/information")
    public JsonResult showPatientInformation(@RequestBody @Validated PrescriptionNoParam param) {
        return success(shop().patientService.auditPatientShow(param));
    }

    /**
     * 获取用户默认患者信息
     * @return
     */
    @PostMapping("/api/wxapp/user/patient/get/default")
    public JsonResult getDefaultPatient(){
        Integer userId=wxAppAuth.user().getUserId();
        return success(shop().patientService.getDefaultPatient(userId));
    }

    /**
     * 	手动删除患者
     */
    @PostMapping("/api/wxapp/user/patient/delete")
    public JsonResult deletePatient(@RequestBody UserPatientParam param) {
        shop().patientService.deleteUserPatient(param);
        return success();
    }
}
