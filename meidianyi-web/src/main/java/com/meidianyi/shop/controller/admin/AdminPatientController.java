package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.patient.*;
import com.meidianyi.shop.service.shop.ShopApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author chenjie
 */
@RestController
public class AdminPatientController extends AdminBaseController {

    /**
     * 患者列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/patient/list")
    public JsonResult patientList(@RequestBody PatientListParam param) {
        PageResult<PatientOneParam> patientList = shop().patientService.getPatientList(param);
        return this.success(patientList);
    }

    /**
     * 根据id获取患者信息
     * @param patientId 患者id
     */
    @GetMapping("/api/admin/patient/info/{patientId}")
    public JsonResult getPatient(@PathVariable  Integer patientId) {
        if (patientId == null) {
            return fail(JsonResultCode.PATIENT_IS_NOT_EXIST);
        }
        return success(shop().patientService.getOneInfo(patientId));
    }

    /**
     * 根据患者id查询患者购药记录
     * @param patientMedicineParam 患者id
     * @return JsonResult
     */
    @PostMapping("/api/admin/patient/query/medicine")
    public JsonResult getPatientBuyMedicineRecord(@Validated @RequestBody PatientMedicineParam patientMedicineParam) {
        return success(shop().patientService.getPatientBuyMedicineRecord(patientMedicineParam));
    }

    /**
     * 根据患者id查询关联医师
     * @param patientQueryDoctorParam 患者查询关联医师入参
     * @return JsonResult
     */
    @PostMapping("/api/admin/patient/query/doctor")
    public JsonResult getPatientRelevanceDoctor(@Validated @RequestBody PatientQueryDoctorParam patientQueryDoctorParam) {
        return success(shop().patientService.getPatientQueryDoctorInfo(patientQueryDoctorParam));
    }


    /**
     * 根据患者id查询关联处方
     * @param patientPrescriptionParam 查询处方入参
     * @return JsonResult
     */
    @PostMapping("/api/admin/patient/query/prescription")
    public JsonResult getPatientPrescription(@Validated @RequestBody PatientPrescriptionParam patientPrescriptionParam) {
        return success(shop().patientService.getPatientPrescription(patientPrescriptionParam));
    }

    /**
     * 根据患者id查询关联问诊
     * @param patientPrescriptionParam 查询问诊入参
     * @return JsonResult
     */
    @PostMapping("/api/admin/patient/query/inquiry")
    public JsonResult getPatientInquiry(@Validated @RequestBody PatientPrescriptionParam patientPrescriptionParam) {
        return success(shop().patientService.getPatientInquiry(patientPrescriptionParam));
    }

}
