package com.meidianyi.shop.service.shop.patient;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RandomUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.*;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsDo;
import com.meidianyi.shop.config.SmsApiConfig;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.dao.shop.patient.UserPatientCoupleDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.patient.*;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionNoParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.sms.SmsCheckParam;
import com.meidianyi.shop.service.pojo.shop.sms.template.SmsTemplate;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.inquiry.InquiryOrderService;
import com.meidianyi.shop.service.shop.sms.SmsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.meidianyi.shop.config.SmsApiConfig.REDIS_KEY_SMS_USER_CHECK_NUM;
import static com.meidianyi.shop.service.shop.prescription.FetchPatientInfoConstant.ALREADY_FETCH;

/**
 * @author chenjie
 */
@Service
public class PatientService extends BaseShopConfigService{
    public static final String STRING_BLANK = "";
    public static final String NOTHING = "无";
    @Autowired
    private MpAuthShopService mpAuthShopService;
    @Autowired
    protected PatientDao patientDao;
    @Autowired
    protected UserPatientCoupleDao userPatientCoupleDao;
    @Autowired
    protected BaseShopConfigService baseShopConfigService;
    @Autowired
    protected SmsService smsService;
    @Autowired
    protected JedisManager jedisManager;
    @Autowired
    protected PrescriptionDao prescriptionDao;
    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private InquiryOrderService inquiryOrderService;

    public static final int ZERO = 0;

    /**
     * 患者列表
     * @param param 查询患者入参
     * @return PageResult<PatientOneParam>
     */
    public PageResult<PatientOneParam> getPatientList(PatientListParam param) {
        if (param.getUserId() > 0) {
            List<Integer> patientIds = userPatientCoupleDao.listPatientIdsByUserId(param.getUserId());
            if (patientIds.size() > 0) {
                param.setPatientIds(patientIds);
            }
        }
        PageResult<PatientOneParam> patientList = patientDao.getPatientList(param);
        patientList.dataList.forEach(patientOneParam -> {
            List<UserDo> userDos = userPatientCoupleDao.listUsersByPatientId(patientOneParam.getId());
            List<PatientOneParam.UserParam> collect = userDos.stream().map(userDo -> new PatientOneParam.UserParam(userDo.getUserId(), userDo.getUsername())).collect(Collectors.toList());
            patientOneParam.setWxNickName(userDos.stream().map(UserDo::getUsername).collect(Collectors.toList()));
            patientOneParam.setUserIdNew(userDos.stream().map(UserDo::getUserId).collect(Collectors.toList()));
            patientOneParam.setUserParamList(collect);
        });
        Map<Integer, String> diseaseMap = getDiseaseMap();
        for (PatientOneParam patient : patientList.dataList) {
            getPatientDiseaseStr(patient, diseaseMap);
            patient.setAge(DateUtils.getAgeByBirthDay(patient.getBirthday()));
            patient.setCountPrescription(prescriptionDao.countPrescriptionByPatient(patient.getId()));
        }
        return patientList;
    }

    public Integer insertPatient(PatientDo param) {
        transaction(() -> {
            int patientId = patientDao.insertPatient(param);
            param.setId(patientId);
        });
        return param.getId();
    }

    public Integer updatePatient(PatientDo param) {
        patientDao.updatePatient(param);
        return param.getId();
    }

    public PatientOneParam getOneInfo(Integer patientId) {
        PatientOneParam patient = patientDao.getOneInfo(patientId);
        Map<Integer, String> diseaseMap = getDiseaseMap();
        getPatientDiseaseStr(patient,diseaseMap);
        return patient;
    }


    public UserPatientDetailVo getOneInfoForWx(Integer userId, Integer patientId) {
        UserPatientDetailVo patientInfo = userPatientCoupleDao.getUserPatientInfo(userId, patientId);
        if (patientInfo!=null){
            patientInfo.setAge(DateUtil.ageOfNow(patientInfo.getBirthday()));
        }
        return patientInfo;
    }
    public UserPatientDetailVo getOneInfoForWx(UserPatientParam userPatientParam) {
        return userPatientCoupleDao.getUserPatientInfo(userPatientParam);
    }

    public List<PatientOneParam> listPatientByUserId (Integer userId) {
        List<PatientOneParam> patientList = userPatientCoupleDao.listPatientIdsByUser(userId);
        Map<Integer, String> diseaseMap = getDiseaseMap();
        for (PatientOneParam patient : patientList) {
            patient.setId(patient.getPatientId());
            getPatientDiseaseStr(patient,diseaseMap);
            patient.setCountPrescription(prescriptionDao.countPrescriptionByPatient(patient.getId()));
        }
        return patientList;
    }

    public Integer defaultPatientId (Integer userId) {
        return userPatientCoupleDao.defaultPatientIdByUser(userId);
    }

    /**
     * 获取默认患者 没有为null
     * @param userId
     * @return
     */
    public UserPatientParam defaultPatientByUser(Integer userId) {
        return userPatientCoupleDao.defaultPatientByUser(userId);
    }

    public void setDefaultPatient (UserPatientParam userPatient) {
        userPatientCoupleDao.initDefaultUserPatient(userPatient.getUserId());
        userPatientCoupleDao.setDefaultPatient(userPatient);
    }

    /**
     * 获取默认患者详情
     * @param userId
     * @return
     */
    public UserPatientDetailVo getDefaultPatient(Integer userId){
        UserPatientParam param = userPatientCoupleDao.defaultPatientByUser(userId);
        return getOneDetail(param);
    }
    /**
     * 拉取患者信息
     */
    public JsonResult getExternalPatientInfo(UserPatientOneParam userPatientOneParam){
        Integer shopId =getShopId();
        PatientExternalRequestParam requestParam=new PatientExternalRequestParam();
        requestParam.setName(userPatientOneParam.getName());
        requestParam.setIdentityCode(userPatientOneParam.getIdentityCode());
        requestParam.setMobile(userPatientOneParam.getMobile());
        String requestJson=Util.toJson(requestParam);
        ApiExternalRequestResult apiExternalRequestResult=saas().apiExternalRequestService.externalRequestGate(ApiExternalRequestConstant.APP_ID_HIS,shopId, ApiExternalRequestConstant.SERVICE_NAME_FETCH_PATIENT_INFO,requestJson);
        if(!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }

        List<PatientExternalVo> patientInfoVoList = Util.parseJson(apiExternalRequestResult.getData(), new TypeReference<List<PatientExternalVo>>(){});
        PatientExternalVo patientInfoVo=new PatientExternalVo();
        if(patientInfoVoList!=null&&patientInfoVoList.size()>0){
            patientInfoVo=patientInfoVoList.get(0);
        }
        PatientDo patientDo=new PatientDo();
        patientDo.setIsFetch(ALREADY_FETCH);
        FieldsUtil.assignWithIgnoreField(patientInfoVo, patientDo,getPatientIgnoreFields());
        PatientOneParam patientOneParam = patientDao.getPatientByNameAndMobile(userPatientOneParam);
        if (patientOneParam == null) {
            patientDao.insertPatient(patientDo);
        } else {
            patientDo.setId(patientOneParam.getId());
            patientDao.updatePatient(patientDo);
        }
        UserPatientCoupleDo userPatientCoupleDo = new UserPatientCoupleDo();
        FieldsUtil.assign(patientDo,userPatientCoupleDo);
        userPatientCoupleDo.setId(null);
        userPatientCoupleDo.setPatientId(patientDo.getId());
        userPatientCoupleDo.setUserId(userPatientOneParam.getUserId());
        userPatientCoupleDo.setIsFetch(PatientConstant.FETCH);
        addPatientUser(userPatientCoupleDo);
        return JsonResult.success();
    }
    /**
     * 拷贝要忽略的字段
     * @return
     */
    private Set<String> getPatientIgnoreFields(){
        Set<String> ignoreField = new HashSet<>(2);
        ignoreField.add("createTime");
        ignoreField.add("lastUpdateTime");
        return ignoreField;
    }

    /**
     * 根据姓名手机号身份证号查询患者信息
     * @param patientInfoParam
     * @return
     */
    public PatientOneParam getPatientByNameAndMobile(UserPatientOneParam patientInfoParam){
        return patientDao.getPatientByNameAndMobile(patientInfoParam);
    }

    /**
     * 获取疾病史选中List
     * @param diseaseStr
     * @return
     */
    public List<PatientMoreInfoParam> listDiseases(String diseaseStr) {
        List<PatientMoreInfoParam> diseaseList = Util.parseJson(get("diseases"), new TypeReference<List<PatientMoreInfoParam>>() {
        });
        if (diseaseStr == null || "".equals(diseaseStr)){
            return diseaseList;
        }
        List<String> diseases = Arrays.asList(diseaseStr.split(","));
        for (PatientMoreInfoParam disease : diseaseList) {
            if (diseases.contains(String.valueOf(disease.getId()))) {
                disease.setChecked((byte) 1);
            }
        }
        return diseaseList;
    }
    public String strDisease(String diseaseStr){
        List<PatientMoreInfoParam> diseaseList = Util.parseJson(get("diseases"), new TypeReference<List<PatientMoreInfoParam>>() {
        });
        if (diseaseStr == null || "".equals(diseaseStr)){
            return "";
        }
        List<String> strList=new ArrayList<>();
        List<String> diseases = Arrays.asList(diseaseStr.split(","));
        for (PatientMoreInfoParam disease : diseaseList) {
            if (diseases.contains(String.valueOf(disease.getId()))) {
                strList.add(disease.getName());
            }
        }
        return StringUtils.join(strList.toArray(),",");
    }
    /**
     * 获取患者详情信息(小程序前端)
     * @param userPatientParam
     * @return
     */
    public UserPatientDetailVo getOneDetail(UserPatientParam userPatientParam) {
        if (userPatientParam != null && userPatientParam.getPatientId() != 0) {
            UserPatientDetailVo userPatientDetail = getOneInfoForWx(userPatientParam);
            if (userPatientDetail!=null){
                //根据出生日期获取年龄
                userPatientDetail.setAge(DateUtils.getAgeByBirthDay(userPatientDetail.getBirthday()));
                userPatientDetail.setDiseaseHistoryList(listDiseases(userPatientDetail.getDiseaseHistory()));
                userPatientDetail.setFamilyDiseaseHistoryList(listDiseases(userPatientDetail.getFamilyDiseaseHistory()));
                userPatientDetail.setDiseaseHistoryStr(strDisease(userPatientDetail.getDiseaseHistory()));
                userPatientDetail.setFamilyDiseaseHistoryStr(strDisease(userPatientDetail.getFamilyDiseaseHistory()));
                userPatientDetail.setId(userPatientDetail.getPatientId());
                return userPatientDetail;
            }
        }
        UserPatientDetailVo userPatientDetail = new UserPatientDetailVo();
        userPatientDetail.setDiseaseHistoryList(listDiseases(null));
        userPatientDetail.setFamilyDiseaseHistoryList(listDiseases(null));
        return userPatientDetail;
    }

    /**
     * 待审核处方展示患者信息
     * @param prescriptionNoParam 处方号
     * @return UserPatientDetailVo
     */
    public PrescriptionShowPatientDetailsParam auditPatientShow(PrescriptionNoParam prescriptionNoParam) {
        // 根据处方号获取该患者信息
        PrescriptionShowPatientDetailsParam prescriptionShowPatientDetailsParam = new PrescriptionShowPatientDetailsParam();
        PrescriptionVo doByPrescriptionNo = prescriptionDao.getDoByPrescriptionNo(prescriptionNoParam.getPrescriptionCode());
        UserPatientParam userPatientParam = new UserPatientOneParam();
        userPatientParam.setPatientId(doByPrescriptionNo.getPatientId());
        userPatientParam.setUserId(doByPrescriptionNo.getUserId());
        UserPatientDetailVo oneDetail = getOneDetail(userPatientParam);
        FieldsUtil.assign(oneDetail, prescriptionShowPatientDetailsParam);
        return prescriptionShowPatientDetailsParam;
    }

    /**
     * 获取患者信息
     * @param patientIds id集合
     * @return
     */
    public List<PatientSimpleInfoVo> listPatientInfo(List<Integer> patientIds){
        return patientDao.listPatientInfo(patientIds);
    }

    /**
     * 发送短信校验码
     * @param param
     */
    public void sendCheckSms(PatientSmsCheckNumParam param) throws MpException {
        //0000-9999
        int intRandom = RandomUtil.getIntRandom();
        MpAuthShopRecord mpAuthShopRecord = mpAuthShopService.getAuthShopByShopId(getShopId());
        String smsContent = String.format(SmsTemplate.PATIENT_CHECK_MOBILE, mpAuthShopRecord.getNickName(), intRandom);
        smsService.sendSms(param.getUserId(), param.getMobile(), smsContent);
        Integer patientId = patientDao.getPatientIdByIdentityCode(param.getIdentityCode());
        String checkKey = String.format(REDIS_KEY_SMS_USER_CHECK_NUM, getShopId(), patientId);
        String s = jedisManager.get(checkKey);
        SmsCheckParam smsCheckParam = Util.json2Object(s, SmsCheckParam.class, false);
        if (smsCheckParam != null) {
            smsCheckParam.setCheckNum(smsCheckParam.getCheckNum() - 1);
        }
        jedisManager.set(checkKey, Util.toJson(smsCheckParam), smsService.getSecondsToMorning().intValue());
        String key = String.format(SmsApiConfig.REDIS_KEY_SMS_CHECK_PATIENT_MOBILE, getShopId(), param.getUserId(), param.getMobile());
        jedisManager.set(key, intRandom + "", 600);

    }

    /**
     * 患者是否存在，用来新增检查
     * @param param
     * @return
     */
    public Integer getPatientExist(PatientExternalRequestParam param) {
        return patientDao.getPatientExist(param);
    }

    public void addPatientUser(UserPatientCoupleDo userPatientCoupleDo) {
        List<PatientOneParam> patientList=userPatientCoupleDao.listPatientIdsByUser(userPatientCoupleDo.getUserId());
        if(patientList.size()==0) {
            userPatientCoupleDo.setIsDefault((byte) 1);
        }
        saveUserPaitientCouple(userPatientCoupleDo);
    }

    public boolean isExistUserPatient(UserPatientParam param) {
        return userPatientCoupleDao.isExistUserPatient(param);
    }

    public void saveUserPaitientCouple(UserPatientCoupleDo userPatientCoupleDo) {
        UserPatientParam param = new UserPatientParam();
        FieldsUtil.assign(userPatientCoupleDo,param);
        Integer userPatientId = userPatientCoupleDao.getUserPatientId(param);
        if (userPatientId == null) {
            userPatientCoupleDao.save(userPatientCoupleDo);
        } else {
            userPatientCoupleDo.setId(userPatientId);
            userPatientCoupleDao.updateUserPatient(userPatientCoupleDo);
        }
    }

    /**
     * 接触用户患者关联
     * @param param
     */
    public void deleteUserPatient(UserPatientParam param) {
        userPatientCoupleDao.deleteUserPatient(param);
        userPatientCoupleDao.defaultPatientIdByUser(param.getUserId());
        if (userPatientCoupleDao.defaultPatientIdByUser(param.getUserId())==0) {
            List<PatientOneParam> patients = listPatientByUserId(param.getUserId());
            if (patients.size() > 0) {
                UserPatientParam userPatient = new UserPatientParam();
                userPatient.setUserId(patients.get(0).getUserIdNew().get(0));
                userPatient.setPatientId(patients.get(0).getPatientId());
                userPatientCoupleDao.setDefaultPatient(userPatient);
            }
        }
    }

    /**
     * 根据用户患者Id获取用户患者
     * @param param
     * @return
     */
    public UserPatientParam getUserPatient(UserPatientParam param) {
        return userPatientCoupleDao.getUserPatient(param);
    }

    public PatientOneParam getPatientDiseaseStr(PatientOneParam patient,Map<Integer,String> diseaseMap){
        if (patient.getDiseaseHistory() == null) {
            patient.setDiseaseHistoryNameStr(STRING_BLANK);
        } else if (STRING_BLANK.equals(patient.getDiseaseHistory())) {
            patient.setDiseaseHistoryNameStr(NOTHING);
        } else {
            List<String> diseaseIds = Arrays.asList(patient.getDiseaseHistory().split(","));
            List<String> diseaseNameArr = new ArrayList<>();
            for (String diseaseId :diseaseIds) {
                diseaseNameArr.add(diseaseMap.get(Integer.parseInt(diseaseId)));
            }
            patient.setDiseaseHistoryNameStr(Joiner.on(",").join(diseaseNameArr));
        }
        return patient;
    }

    public Map<Integer,String> getDiseaseMap(){
        List<PatientMoreInfoParam> diseaseList = Util.parseJson(get("diseases"), new TypeReference<List<PatientMoreInfoParam>>() {
        });
        Map<Integer, String> diseaseMap = diseaseList.stream().collect(Collectors.toMap(PatientMoreInfoParam::getId, PatientMoreInfoParam::getName));
        return diseaseMap;
    }

    /**
     * 用户绑定的患者数
     * @param userId
     * @return
     */
    public Integer countPatientByUser(Integer userId) {
        return patientDao.countPatientByUser(userId);
    }

    /**
     * 根据患者id查询用户购药记录
     * @param patientMedicineParam 用户查询购药记录入参
     * @return PageResult<PatientMedicineVo>
     */
    public PageResult<PatientMedicineVo> getPatientBuyMedicineRecord(PatientMedicineParam patientMedicineParam) {
        return patientDao.getPatientMedicine(patientMedicineParam);
    }

    /**
     * 根据患者id查询关联医师信息
     * @param patientQueryDoctorParam 用户查询关联医师入参
     * @return PageResult<PatientQueryDoctorVo>
     */
    public PageResult<PatientQueryDoctorVo> getPatientQueryDoctorInfo(PatientQueryDoctorParam patientQueryDoctorParam) {
        PageResult<PatientQueryDoctorVo> patientQueryDoctor = patientDao.getPatientQueryDoctor(patientQueryDoctorParam);
        patientQueryDoctor.getDataList().forEach(patientQueryDoctorVo -> {
            PatientInquiryOrderVo inquiryNumberByPatient = inquiryOrderService.getInquiryNumberByPatient(patientQueryDoctorParam.getPatientId(), patientQueryDoctorVo.getDoctorId());
            if (inquiryNumberByPatient != null) {
                patientQueryDoctorVo.setInquiryNumber(inquiryNumberByPatient.getInquiryCount());
                patientQueryDoctorVo.setInquiryConsumptionAmount(inquiryNumberByPatient.getTotalAmount());
                patientQueryDoctorVo.setConsumptionAmount(patientQueryDoctorVo.getInquiryConsumptionAmount().add(patientQueryDoctorVo.getPrescriptionConsumptionAmount()));
            }
        });
        return patientQueryDoctor;
    }

    /**
     * 根据患者id查询关联处方
     * @param patientPrescriptionParam 用户查询关联处方入参
     * @return PageResult<PatientPrescriptionVo>
     */
    public PageResult<PatientPrescriptionVo> getPatientPrescription(PatientPrescriptionParam patientPrescriptionParam) {
        PageResult<PatientPrescriptionVo> patientPrescription = patientDao.getPatientPrescription(patientPrescriptionParam);
        patientPrescription.getDataList().forEach(patientPrescriptionVo -> {
            List<OrderGoodsDo> byPrescription = orderGoodsService.getByPrescription(patientPrescriptionVo.getPrescriptionCode());
            patientPrescriptionVo.setGoodsList(byPrescription);
        });
        return patientPrescription;
    }

    /**
     * 根据患者id查询咨询订单
     * @param patientPrescriptionParam 咨询查询入参
     * @return PageResult<InquiryOrderDo>
     */
    public PageResult<PatientInquiryVo> getPatientInquiry(PatientPrescriptionParam patientPrescriptionParam) {
        return patientDao.getPatientInquiry(patientPrescriptionParam);
    }
}
