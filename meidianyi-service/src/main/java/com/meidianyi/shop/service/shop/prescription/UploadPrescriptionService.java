package com.meidianyi.shop.service.shop.prescription;

import cn.hutool.json.JSONUtil;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.dao.shop.goods.GoodsDao;
import com.meidianyi.shop.dao.shop.goods.GoodsMedicalInfoDao;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.dao.shop.patient.UserPatientCoupleDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.prescription.UploadPrescriptionGoodsParam;
import com.meidianyi.shop.service.pojo.shop.order.prescription.UploadPrescriptionParam;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.wxapp.order.CreateOrderBo;
import com.meidianyi.shop.service.pojo.wxapp.order.CreateParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.saas.api.ApiExternalRequestService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 上传订单数据(上传处方)
 * @author 孔德成
 * @date 2020/7/16 14:30
 */
@Service
public class UploadPrescriptionService extends ShopBaseService {

    @Autowired
    protected PrescriptionDao prescriptionDao;
    @Autowired
    protected PatientDao patientDao;
    @Autowired
    protected PrescriptionItemDao prescriptionItemDao;
    @Autowired
    protected GoodsMedicalInfoDao goodsMedicalInfoDao;
    @Autowired
    protected UserPatientCoupleDao userPatientCoupleDao;
    @Autowired
    protected GoodsDao goodsDao;

    @Autowired
    protected ApiExternalRequestService apiExternalRequestService;
    @Autowired
    protected OrderInfoService orderInfoService;

    /**
     * 上传处方到his系统.
     */
    public JsonResult uploadPrescription(OrderInfoDo orderInfoDo,List<OrderGoodsBo> orderGoodsBoList) {
        PatientOneParam patient = patientDao.getOneInfo(orderInfoDo.getPatientId());
        List<PrescriptionDo> prescriptionDoList = prescriptionDao.listDiagnosis(orderInfoDo.getPatientId());
        List<String> diagnosisNameList = prescriptionDoList.stream().map(PrescriptionDo::getDiagnosisName).collect(Collectors.toList());
        List<String> posCodeList = prescriptionDoList.stream().map(PrescriptionDo::getPosCode).collect(Collectors.toList());
        List<UploadPrescriptionGoodsParam> goodsParamList =new ArrayList<>();
        for (OrderGoodsBo orderGoodsBo : orderGoodsBoList) {
            toGoodsParam(goodsParamList, orderGoodsBo);
        }
        UploadPrescriptionParam param =new UploadPrescriptionParam();
        param.setOrderSn(orderInfoDo.getOrderSn());
        param.setName(patient.getName());
        param.setMobile(patient.getMobile());
        param.setIdentityCode(param.getIdentityCode());
        param.setSex(param.getSex());
        param.setPrescriptionList(posCodeList);
        param.setDiagnosisNameList(diagnosisNameList);
        param.setGoodsMedicalList(goodsParamList);
        return uploadPrescription(param);
    }

    /**
     * 上传处方到his系统.
     */
    public JsonResult uploadPrescription(UploadPrescriptionParam param){
        String requestContentJson =JSONUtil.toJsonStr(param);
        ApiExternalRequestResult apiExternalRequestResult = apiExternalRequestService.externalRequestGate(ApiExternalRequestConstant.APP_ID_HIS, getShopId(), ApiExternalRequestConstant.SERVICE_NAME_UPLOAD_ORDER_PRESCRIPTION, requestContentJson);
        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())){
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }
        return JsonResult.success();
    }

    /**
     * 上传处方到his系统
     * @param createParam
     * @param orderBo
     * @param order
     * @return
     */
    public JsonResult uploadPrescription(CreateParam createParam, CreateOrderBo orderBo, OrderInfoRecord order) throws MpException {
        try {
            PatientOneParam patient = patientDao.getOneInfo(createParam.getPatientId());
            List<PrescriptionDo> prescriptionDoList = prescriptionDao.listDiagnosis(createParam.getPatientId());
            List<String> diagnosisNameList = prescriptionDoList.stream().map(PrescriptionDo::getDiagnosisName).collect(Collectors.toList());
            List<String> posCodeList = prescriptionDoList.stream().map(PrescriptionDo::getPosCode).collect(Collectors.toList());
            List<UploadPrescriptionGoodsParam> goodsParamList =new ArrayList<>();
            for (OrderGoodsBo orderGoodsBo : orderBo.getOrderGoodsBo()) {
                toGoodsParam(goodsParamList, orderGoodsBo);
            }
            UploadPrescriptionParam param =new UploadPrescriptionParam();
            param.setOrderSn(order.getOrderSn());
            param.setName(patient.getName());
            param.setMobile(patient.getMobile());
            param.setIdentityCode(param.getIdentityCode());
            param.setSex(param.getSex());
            param.setPrescriptionList(posCodeList);
            param.setDiagnosisNameList(diagnosisNameList);
            param.setGoodsMedicalList(goodsParamList);
           return uploadPrescription(param);
        }catch (Exception e){
            throw new MpException(JsonResultCode.CODE_ACCOUNT_SAME,null);
        }
    }

    private void toGoodsParam(List<UploadPrescriptionGoodsParam> goodsParamList, OrderGoodsBo orderGoodsBo) {
        GoodsMedicalInfoDo medicalInfoDo = goodsMedicalInfoDao.getByGoodsId(orderGoodsBo.getGoodsId());
        if (medicalInfoDo!=null){
            UploadPrescriptionGoodsParam goodsParam = new UploadPrescriptionGoodsParam();
            goodsParam.setGoodsCode(medicalInfoDo.getGoodsCode());
            goodsParam.setGoodsCommonName(medicalInfoDo.getGoodsCommonName());
            goodsParam.setGoodsAliasName(medicalInfoDo.getGoodsAliasName());
            goodsParam.setGoodsQualityRatio(medicalInfoDo.getGoodsQualityRatio());
            goodsParam.setOrderNumber(orderGoodsBo.getGoodsNumber());
            goodsParam.setIsRx(medicalInfoDo.getIsRx());
            goodsParamList.add(goodsParam);
        }
    }
}
