package com.meidianyi.shop.service.shop.medicine;


import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.dao.shop.medical.MedicalHistoryDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.*;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientOneParam;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.FETCH_HIS_NULL;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-07 10:29
 */
@Service
public class MedicalHistoryService extends ShopBaseService {

    @Autowired
    private MedicalHistoryDao medicalHistoryDao;
    @Autowired
    public PatientService patientService;
    @Autowired
    public PrescriptionService prescriptionService;

    /**
     * 查询单条病历详情
     * @param medicalHistoryListParam 病历详情入参
     * @return MedicalHistoryListVo
     */
    public MedicalHistoryListVo getMedicalHistoryDetail(MedicalHistoryListParam medicalHistoryListParam){
        return medicalHistoryDao.getMedicalHistoryDetail(medicalHistoryListParam);
    }

    /**
     * 分页查询病历展示页面字段
     * @param medicalHistoryPageInfoParam 病历表分页入参
     * @return PageResult<MedicalHistoryPageInfoVo>
     */
    public MedicalHistoryPageInfoVoPage<MedicalHistoryPageInfoVo> getMedicalHistoryPageInfoNew(MedicalHistoryPageInfoParam medicalHistoryPageInfoParam){
        if (medicalHistoryPageInfoParam.getUserId() != null) {
            List<String> prescriptionNos = prescriptionService.getPrescriptionNosByUserId(medicalHistoryPageInfoParam.getUserId());
            medicalHistoryPageInfoParam.setPrescriptionNos(prescriptionNos);
        }
        List<PatientOneParam> patientOneParams = patientService.listPatientByUserId(medicalHistoryPageInfoParam.getUserId());
        PageResult<MedicalHistoryPageInfoVo> medicalHistoryPageInfo = medicalHistoryDao.getMedicalHistoryPageInfo(medicalHistoryPageInfoParam);
        MedicalHistoryPageInfoVoPage medicalHistoryPageInfoVoPage = new MedicalHistoryPageInfoVoPage();
        medicalHistoryPageInfoVoPage.setPageResult(medicalHistoryPageInfo);
        if (patientOneParams.isEmpty()) {
            medicalHistoryPageInfoVoPage.setIsHavePatient((byte)0);
        }else {
            medicalHistoryPageInfoVoPage.setIsHavePatient((byte)1);
        }
        return medicalHistoryPageInfoVoPage;
    }

    /**
     * 分页查询病历展示页面字段
     * @param medicalHistoryPageInfoParam 病历表分页入参
     * @return PageResult<MedicalHistoryPageInfoVo>
     */
    public PageResult<MedicalHistoryPageInfoVo> getMedicalHistoryPageInfo(MedicalHistoryPageInfoParam medicalHistoryPageInfoParam){
        if (medicalHistoryPageInfoParam.getUserId() != null) {
            List<String> prescriptionNos = prescriptionService.getPrescriptionNosByUserId(medicalHistoryPageInfoParam.getUserId());
            medicalHistoryPageInfoParam.setPrescriptionNos(prescriptionNos);
        }
        PageResult<MedicalHistoryPageInfoVo> medicalHistoryPageInfo = medicalHistoryDao.getMedicalHistoryPageInfo(medicalHistoryPageInfoParam);
        return medicalHistoryPageInfo;
    }

    /**
     * 拉取病历列表
     * @param fetchMedicalHistoryParam 拉取病历信息
     * @return JsonResult
     */
    public JsonResult pullExternalMedicalHistoryList(UserPatientOneParam fetchMedicalHistoryParam) {
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_FETCH_MEDICAL_HISTORY_INFOS;

        //增量
        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(ApiExternalRequestConstant.APP_ID_HIS,
            shopId, serviceName);
        fetchMedicalHistoryParam.setStartTime(lastRequestTime);

        //拉取数据
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService
            .externalRequestGate(appId, shopId, serviceName, Util.toJson(fetchMedicalHistoryParam));

        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }
        if (apiExternalRequestResult.getData() == null) {
            return new JsonResult().fail("zh_CN", FETCH_HIS_NULL);
        }
        //得到Data
        String dataJson = apiExternalRequestResult.getData();
        List<FetchMedicalHistoryVo> fetchMedicalHistoryVos = Util.parseJson(dataJson, new TypeReference<List<FetchMedicalHistoryVo>>() {
        });

        // 数据库新增或更新
        assert fetchMedicalHistoryVos != null;
        UserPatientOneParam patientParam = new UserPatientOneParam();
        patientParam.setName(fetchMedicalHistoryParam.getName());
        patientParam.setMobile(fetchMedicalHistoryParam.getMobile());
        patientParam.setIdentityCode(fetchMedicalHistoryParam.getIdentityCode());
        PatientOneParam patientInfo = patientService.getPatientByNameAndMobile(patientParam);
        for (FetchMedicalHistoryVo fetchMedicalHistoryVo : fetchMedicalHistoryVos) {
            //如果没有当前病历就新增
            fetchMedicalHistoryVo.setPatientId(patientInfo.getId());
            fetchMedicalHistoryVo.setPatientName(patientInfo.getName());
            if (medicalHistoryDao.getMedicalHistoryDetailByCode(fetchMedicalHistoryVo.getPosCode()) == null) {
                medicalHistoryDao.addHisMedicalHistory(fetchMedicalHistoryVo);

            } else {  //否则就修改
                medicalHistoryDao.updateHisMedicalHistory(fetchMedicalHistoryVo);
            }
        }
        return JsonResult.success();
    }
}
