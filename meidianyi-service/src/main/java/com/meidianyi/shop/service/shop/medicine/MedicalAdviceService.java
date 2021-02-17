package com.meidianyi.shop.service.shop.medicine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.dao.foundation.transactional.DbTransactional;
import com.meidianyi.shop.dao.foundation.transactional.DbType;
import com.meidianyi.shop.dao.shop.medical.MedicalAdviceDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.medical.FetchMedicalAdviceParam;
import com.meidianyi.shop.service.pojo.shop.medical.FetchMedicalAdviceVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientOneParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.FETCH_HIS_NULL;

/**
 * @author 赵晓东
 * @description 拉取医嘱列表
 * @create 2020-07-24 09:35
 **/

@Service
public class MedicalAdviceService extends ShopBaseService {

    @Autowired
    private MedicalAdviceDao medicalAdviceDao;

    /**
     * 拉取医嘱列表
     * @param fetchMedicalAdviceParam 拉取医嘱信息
     * @return JsonResult
     */
    public JsonResult pullExternalMedicalAdviceList(UserPatientOneParam fetchMedicalAdviceParam) {
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_FETCH_MEDICAL_ADVICE_INFOS;

        //增量
        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(ApiExternalRequestConstant.APP_ID_HIS,
            shopId, serviceName);
        fetchMedicalAdviceParam.setStartTime(lastRequestTime);
        System.out.println(fetchMedicalAdviceParam.toString());
        //拉取数据
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService
            .externalRequestGate(appId, shopId, serviceName, Util.toJson(fetchMedicalAdviceParam));

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
        List<FetchMedicalAdviceVo> fetchMedicalAdviceVos = Util.parseJson(dataJson, new TypeReference<List<FetchMedicalAdviceVo>>() {
        });

        // 数据库新增或更新
        assert fetchMedicalAdviceVos != null;
        for (FetchMedicalAdviceVo fetchMedicalAdviceVo : fetchMedicalAdviceVos) {
            //如果没有当前医嘱就新增
            Integer medicalAdviceByCode = medicalAdviceDao.getMedicalAdviceByCode(fetchMedicalAdviceVo.getPosCode());
            if (medicalAdviceByCode == null || medicalAdviceByCode == 0) {
                medicalAdviceDao.addHisMedicalAdvice(fetchMedicalAdviceVo);
            } else {  //否则就修改
                medicalAdviceDao.updateHisMedicalAdvice(fetchMedicalAdviceVo);
            }
        }
        return JsonResult.success();
    }

}
