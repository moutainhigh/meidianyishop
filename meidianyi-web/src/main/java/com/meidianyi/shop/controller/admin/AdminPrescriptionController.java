package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionListParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionPatientListParam;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 药方
 * @author 孔德成
 * @date 2020/7/6 10:16
 */
@RestController
public class AdminPrescriptionController extends AdminBaseController {

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 处方分页
     * @param param
     */
    @PostMapping(value = "/api/admin/prescription/list")
    public JsonResult listPageResult(@RequestBody  PrescriptionListParam param){
        return success(prescriptionService.listPageResult(param));
    }

    /**
     * 处方分页
     * @param param
     */
    @PostMapping(value = "/api/admin/prescription/patient/list")
    public JsonResult listPatientPageResult(@RequestBody @Validated PrescriptionPatientListParam param){
        return success(prescriptionService.listPatientPageResult(param));
    }

    /**
     * 获取处方明细
     * @param code 处方号
     * @return
     */
    @GetMapping("/api/admin/prescription/get/{code}")
    public JsonResult getPrescriptionInfo(@PathVariable("code")String code){
        return success(prescriptionService.getInfoByPrescriptionNo(code));
    }
}
