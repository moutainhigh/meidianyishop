package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.medical.FetchMedicalAdviceParam;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.FetchMedicalHistoryParam;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.MedicalHistoryListParam;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.MedicalHistoryPageInfoParam;
import com.meidianyi.shop.service.pojo.shop.prescription.FetchPrescriptionOneParam;
import com.meidianyi.shop.service.pojo.shop.prescription.FetchPrescriptionParam;
import com.meidianyi.shop.service.shop.medicine.MedicalAdviceService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 赵晓东
 * @description 小程序病历接口
 * @create 2020-07-09 09:03
 */
@RestController
@RequestMapping(value = "/api/wxapp/medicine/history")
public class WxAppMedicalHistoryController extends WxAppBaseController {

    @Autowired
    private MedicalAdviceService medicalAdviceService;

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 病历详情
     * @param medicalHistoryListParam 病历详情入参
     * @return JsonResult
     */
    @RequestMapping("/detail")
    public JsonResult medicalHistoryDetail(@RequestBody MedicalHistoryListParam medicalHistoryListParam) {
        return success(shop().medicalHistoryService.getMedicalHistoryDetail(medicalHistoryListParam));
    }

    /**
     * 分页展示病历列表
     * @param medicalHistoryPageInfoParam 病历列表入参
     * @return JsonResult
     */
    @RequestMapping("/list")
    public JsonResult getHistoryInfo(@RequestBody MedicalHistoryPageInfoParam medicalHistoryPageInfoParam) {
        medicalHistoryPageInfoParam.setUserId(wxAppAuth.user().getUserId());
        return success(shop().medicalHistoryService.getMedicalHistoryPageInfoNew(medicalHistoryPageInfoParam));
    }


    /**
     * 拉取his系统医嘱明细信息
     * @param fetchMedicalAdviceParam 拉取医嘱明细
     * @return JsonResult
     */
    @RequestMapping("/fetch/external/medical/advice")
    public JsonResult pullMedicalAdvice(@RequestBody FetchMedicalAdviceParam fetchMedicalAdviceParam){
        return this.success(medicalAdviceService.pullExternalMedicalAdviceList(fetchMedicalAdviceParam));
    }

    /**
     * 拉取his系统处方列表信息
     * @param fetchPrescriptionParam 拉取处方列表
     * @return JsonResult
     */
    @RequestMapping("/fetch/prescription")
    public JsonResult pullPrescription(@RequestBody FetchPrescriptionParam fetchPrescriptionParam){
        return this.success(prescriptionService.pullExternalAllPrescriptionInfo(fetchPrescriptionParam));
    }

    /**
     * 根据患者编号拉取处方
     * @param fetchPrescriptionParam 拉取单条处方
     * @return JsonResult
     */
    @RequestMapping("/fetch/prescription/item")
    public JsonResult pullPrescriptionItem(@RequestBody FetchPrescriptionOneParam fetchPrescriptionParam){
        return this.success(prescriptionService.pullExternalOnePrescriptionInfo(fetchPrescriptionParam));
    }
}
