package com.meidianyi.shop.service.shop.doctor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.*;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorDo;
import com.meidianyi.shop.common.pojo.shop.table.DoctorDutyRecordDo;
import com.meidianyi.shop.common.pojo.shop.table.DoctorSummaryTrendDo;
import com.meidianyi.shop.common.pojo.shop.table.UserDoctorAttentionDo;
import com.meidianyi.shop.config.SmsApiConfig;
import com.meidianyi.shop.dao.shop.user.UserDao;
import com.meidianyi.shop.dao.shop.department.DepartmentDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorDepartmentCoupleDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorDutyRecordDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorLoginLogDao;
import com.meidianyi.shop.dao.shop.order.InquiryOrderDao;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.dao.shop.user.UserDoctorAttentionDao;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsListParam;
import com.meidianyi.shop.service.pojo.shop.auth.AuthConstant;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageTemplateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentListVo;
import com.meidianyi.shop.service.pojo.shop.doctor.*;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.user.UserDoctorParam;
import com.meidianyi.shop.service.shop.anchor.AnchorPointsService;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.department.DepartmentService;
import com.meidianyi.shop.service.shop.order.inquiry.InquiryOrderService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import com.meidianyi.shop.service.shop.rebate.InquiryOrderRebateService;
import com.meidianyi.shop.service.shop.rebate.PrescriptionRebateService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import com.meidianyi.shop.service.shop.title.TitleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.util.MapUtil.merge2ResultMap;
import static com.meidianyi.shop.service.shop.anchor.AnchorPointsEvent.DOCTOR_ENTER_IN;

/**
 * @author chenjie
 */
@Service
public class DoctorService extends BaseShopConfigService {
    /**
     * 自动推荐最大数量
     */
    public static final int RECOMMEND_MAX_NUM = 10;
    public static final String HOSPITAL_NAME = "未知医院";
    public static final float ANSWER_TIME_TEN_MUNITES = 10;
    public static final float ANSWER_TIME_HALF_HOUR = 30;
    public static final float ANSWER_TIME_ONE_HOUR = 60;

    private static final Byte DOCTOR_STATUS_DISABLE = 0;

    @Autowired
    protected DoctorDao doctorDao;
    @Autowired
    protected DoctorDepartmentCoupleDao doctorDepartmentCoupleDao;
    @Autowired
    protected DepartmentDao departmentDao;
    @Autowired
    protected DepartmentService departmentService;
    @Autowired
    protected TitleService titleService;
    @Autowired
    protected UserDao userDao;
    @Autowired
    protected JedisManager jedisManager;
    @Autowired
    protected UserDoctorAttentionDao userDoctorAttentionDao;
    @Autowired
    protected DoctorDutyRecordDao doctorDutyRecordDao;
    @Autowired
    public DoctorCommentService doctorCommentService;
    @Autowired
    private AnchorPointsService anchorPointsService;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private InquiryOrderService inquiryOrderService;
    @Autowired
    private InquiryOrderRebateService inquiryOrderRebateService;
    @Autowired
    private PrescriptionRebateService prescriptionRebateService;
    @Autowired
    private DoctorLoginLogService doctorLoginLogService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private InquiryOrderDao inquiryOrderDao;
    @Autowired
    private PatientDao patientDao;
    @Autowired
    private DoctorLoginLogDao doctorLoginLogDao;
    @Autowired
    private DoctorStatisticService doctorStatisticService;

    @Autowired
    public StoreService storeService;


    public static final int ZERO = 0;

    private static final Integer ONE_MINUTES = 60;

    private static final Integer HALF_HOUR = 1800;

    private static final Integer ONE_HOUR = 3600;

    public PageResult<DoctorOneParam> getDoctorList(DoctorListParam param) {
        if (param.getDepartmentName() != null) {
            List<Integer> departmentIds = departmentDao.getDepartmentIdsByName(param.getDepartmentName());
            List<Integer> doctorIds = doctorDepartmentCoupleDao.getDoctorIdsByDepartmentIds(departmentIds);
            param.setDoctorIds(doctorIds);
        }
        PageResult<DoctorOneParam> doctorList = doctorDao.getDoctorList(param);
        for (DoctorOneParam list : doctorList.dataList) {
            List<String> departmentList = doctorDepartmentCoupleDao.getDepartmentNamesByDoctorId(list.getId());
            list.setDepartmentNames(departmentList);
            String titleName = titleService.getTitleName(list.getTitleId());
            list.setTitleName(titleName);
            Integer hour = list.getAvgAnswerTime() / 3600;
            Integer munite = list.getConsultationNumber() == 0 ? -1 : (list.getAvgAnswerTime() - 3600 * hour) / 60;
            list.setAnswerHourInt(hour);
            list.setAnswerMunite(munite);
        }

        return doctorList;
    }

    public Integer insertDoctor(DoctorOneParam param) {
        doctorDao.insertDoctor(param);
        setDoctorDepartmentCouples(param.getId(), param.getDepartmentIds());
        return param.getId();
    }

    public Integer updateDoctor(DoctorOneParam param) throws MpException {
        DoctorOneParam doctorInfo = getOneInfo(param.getId());
        if (!doctorInfo.getStatus().equals(param.getStatus())) {
            dealDoctorWx(param.getId(), param.getStatus());
        }
        doctorDao.updateDoctor(param);
        setDoctorDepartmentCouples(param.getId(), param.getDepartmentIds());
        return param.getId();
    }

    public Integer enableDoctor(DoctorOneParam param) throws MpException {
        doctorDao.updateDoctor(param);
        dealDoctorWx(param.getId(), param.getStatus());
        return param.getId();
    }

    public DoctorOneParam getOneInfo(Integer doctorId) throws MpException {
        if (doctorId == null) {
            throw MpException.initErrorResult(JsonResultCode.DOCTOR_ID_IS_NULL, "医师id为null");
        }
        DoctorOneParam doctorInfo = doctorDao.getOneInfo(doctorId);
        if (doctorInfo == null) {
            throw new MpException(JsonResultCode.CODE_FAIL);
        }
        doctorInfo.setAvgAnswerTimeNotSecond(integerTimeToStringTime(doctorInfo.getAvgAnswerTime()));
        List<Integer> departmentIds = doctorDepartmentCoupleDao.getDepartmentIdsByDoctorId(doctorId);
        doctorInfo.setDepartmentIds(departmentIds);
        return doctorInfo;
    }

    /**
     * 将接诊时间(秒)转换为字符串类型展示
     * @param avgAnswerTime 评价响应时间(秒)
     * @return String
     */
    private String integerTimeToStringTime(Integer avgAnswerTime) {
        if (avgAnswerTime < ONE_MINUTES) {
            return "小于一分钟";
        }
        int hour = avgAnswerTime / ONE_HOUR;
        int minute = avgAnswerTime % ONE_HOUR / ONE_MINUTES;
        return hour + "小时" + (minute == 0 ? "" : minute + "分钟");
    }

    public void setDoctorDepartmentCouples(Integer doctorId, List<Integer> departmentIds) {
        doctorDepartmentCoupleDao.deleteDepartmentByDoctor(doctorId);
        if (!departmentIds.isEmpty()) {
            for (Integer departmentId : departmentIds) {
                DoctorDepartmentOneParam doctorDepartment = new DoctorDepartmentOneParam();
                doctorDepartment.setDoctorId(doctorId);
                doctorDepartment.setDepartmentId(departmentId);
                doctorDepartmentCoupleDao.insertDoctorDepartment(doctorDepartment);
            }
        }
    }

    public DoctorOneParam getDoctorByCode(String hospitalCode) {
        return doctorDao.getDoctorByHospitalCode(hospitalCode);
    }

    /**
     * 更新/新增医师
     *
     * @param doctor
     */
    public void synchroDoctor(DoctorOneParam doctor) throws MpException {
        if (getDoctorByCode(doctor.getHospitalCode()) == null) {
            //默认不接诊
            doctor.setCanConsultation(DoctorConstant.CAN_NOT_CONSULTATION);
            //默认不上班
            doctor.setIsOnDuty(DoctorConstant.NOT_ON_DUTY);
            insertDoctor(doctor);
        } else {
            DoctorOneParam oldDepartment = getDoctorByCode(doctor.getHospitalCode());
            doctor.setId(oldDepartment.getId());
            updateDoctor(doctor);
        }
    }

    public void fetchDoctor(String json) throws MpException {
        List<DoctorFetchOneParam> doctorFetchList = Util.parseJson(json, new TypeReference<List<DoctorFetchOneParam>>() {
        });
        List<DoctorFetchOneParam> doctorFetchListNew = listDoctorFetch(doctorFetchList);
        logger().debug(Util.toJson(doctorFetchListNew));
        for (DoctorFetchOneParam list : doctorFetchListNew) {
            DoctorOneParam doctor = new DoctorOneParam();
            doctor.setName(list.getDoctorName());
            doctor.setCertificateCode(list.getCertificateCode());
            doctor.setHospitalCode(list.getDoctorCode());
            doctor.setProfessionalCode(list.getProfessionalCode());
            doctor.setUrl(list.getDocUrl());
            doctor.setMobile(list.getDocPhone());
            doctor.setSex((list.getDoctorSex() == 0) ? (byte) 0 : (byte) 1);
            doctor.setTitleId(titleService.getTitleIdNew(list.getPositionCode()));

            List<String> result = Arrays.asList(list.getDepartCode().split(","));
            List<Integer> departmentIds = new ArrayList<>();
            for (String code : result) {
                if (StringUtils.isBlank(code)) {
                    continue;
                }
                departmentIds.add(departmentService.getDepartmentIdNew(code));
            }
            String departmentStr = Joiner.on(",").join(departmentIds);
            doctor.setDepartmentIdsStr(departmentStr);
            if (StringUtils.isBlank(departmentStr)) {
                doctor.setStatus((byte) 0);
            }
            //是否拉取
            doctor.setIsFetch(DoctorConstant.IS_FETCH);

            synchroDoctor(doctor);
        }
    }

    public List<DoctorFetchOneParam> listDoctorFetch(List<DoctorFetchOneParam> doctorFetchList) {
        Map<String, List<DoctorFetchOneParam>> doctorCodeMap = doctorFetchList.stream().collect(Collectors.groupingBy(DoctorFetchOneParam::getDoctorCode));

        List<DoctorFetchOneParam> doctorList = new ArrayList<>();
        doctorCodeMap.forEach((k, v) -> {
            List<String> departmentCodes = new ArrayList<>();
            for (DoctorFetchOneParam doctor : v) {
                if (doctor.getState() > 1 || StringUtils.isBlank(doctor.getDepartCode())) {
                    continue;
                }
                departmentCodes.add(doctor.getDepartCode());
            }
            int number = v.size();
            DoctorFetchOneParam newDoctor = new DoctorFetchOneParam();
            FieldsUtil.assign(v.get(number - 1), newDoctor);
            newDoctor.setDepartCode(Joiner.on(",").join(departmentCodes));
            doctorList.add(newDoctor);
        });
        return doctorList;
    }

    /**
     * 拉取医师列表
     *
     * @return
     */
    public JsonResult fetchExternalDoctor() throws MpException {
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_FETCH_DOCTOR_INFOS;

        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(ApiExternalRequestConstant.APP_ID_HIS, shopId, ApiExternalRequestConstant.SERVICE_NAME_FETCH_DOCTOR_INFOS);
        DoctorExternalRequestParam param = new DoctorExternalRequestParam();
        param.setStartTime(null);

        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));

        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }

        fetchDoctor(apiExternalRequestResult.getData());

        return JsonResult.success();
    }

    public boolean isCodeExist(Integer doctorId, String code) {
        boolean flag = doctorDao.isCodeExist(doctorId, code);
        return flag;
    }

    /**
     * @Description
     * @Author 赵晓东
     * @Create 2020-07-22 14:51:11
     * 医师认证
     */
    /**
     * 医师认证
     *
     * @param doctorAuthParam 当前用户姓名、手机号、医师医院唯一编码, 验证码
     * @return 验证信息
     */
    public Integer doctorAuth(DoctorAuthParam doctorAuthParam) {
        boolean b = checkMobileCode(doctorAuthParam);
        if (!b) {
            return null;
        }
        // 查询是否有当前医师信息
        DoctorDo doctorDo = doctorDao.doctorAuth(doctorAuthParam);
        if (doctorDo == null || DOCTOR_STATUS_DISABLE.equals(doctorDo.getStatus())) {
            return null;
        }
        // 如果医师存在且没有被认证过
        if (doctorDo.getUserId() == 0) {
            this.transaction(() -> {
                // 修改user表中用户类型为医师
                userDao.updateDoctorAuth(doctorAuthParam.getUserId());
                // 修改doctor表中userId为当前用户
                doctorDo.setUserId(doctorAuthParam.getUserId());
                //更新是否接诊
                doctorDao.updateCanConsultation(doctorDo.getId(), DoctorConstant.CAN_CONSULTATION);
                //更新医师签名
                doctorDao.updateSignature(doctorDo.getId(), doctorAuthParam.getSignature());
                // 修改医师表用户id为当前验证用户，修改医师手机号为用户验证时填写手机号
                doctorDao.updateUserId(doctorDo, doctorAuthParam.getMobile());
            });
            return doctorDo.getId();
        } else {
            return null;
        }
    }

    /**
     * 短信验证码校验
     *
     * @return
     */
    private boolean checkMobileCode(DoctorAuthParam doctorAuthParam) {
        String key = String.format(SmsApiConfig.REDIS_KEY_SMS_CHECK_DOCTOR_MOBILE, getShopId(), doctorAuthParam.getUserId(), doctorAuthParam.getMobile());
        String s = jedisManager.get(key);
        if (!Strings.isBlank(s) && !Strings.isBlank(doctorAuthParam.getMobileCheckCode())) {
            return s.equals(doctorAuthParam.getMobileCheckCode());
        }
        return false;
    }

    public List<DoctorConsultationOneParam> listRecommendDoctorForConsultation(UserPatientParam doctorParam) {
        List<DoctorConsultationOneParam> historyDoctors = doctorDao.listRecommendDoctors(shopCommonConfigService.getDoctorRecommendType(), shopCommonConfigService.getDoctorRecommendConsultationRate(), shopCommonConfigService.getDoctorRecommendInquiryRate());
        setDoctorDepartmentNames(historyDoctors);
        return historyDoctors;
    }

    public PageResult<DoctorConsultationOneParam> listDoctorForConsultation(DoctorConsultationParam doctorParam) {
        if (doctorParam.getKeyword() != null && doctorParam.getKeyword() != "") {
            List<Integer> departmentIds = departmentDao.getDepartmentIdsByName(doctorParam.getKeyword());
            List<Integer> doctorIds = doctorDepartmentCoupleDao.getDoctorIdsByDepartmentIds(departmentIds);
            doctorParam.setDoctorIds(doctorIds);
        }
        if (doctorParam.getDepartmentId() != null && doctorParam.getDepartmentId() > 0) {
            List<Integer> departmentIdsNew = new ArrayList<>();
            departmentIdsNew.add(doctorParam.getDepartmentId());
            List<Integer> departmentDoctorIds = doctorDepartmentCoupleDao.getDoctorIdsByDepartmentIds(departmentIdsNew);
            doctorParam.setDepartmentDoctorIds(departmentDoctorIds);
        }
        if (DoctorConstant.ATTENTION_TYPE.equals(doctorParam.getType()) && doctorParam.getUserId() > 0) {
            List<Integer> userDoctorIds = userDoctorAttentionDao.listDoctorIdsByUser(doctorParam.getUserId());
            doctorParam.setUserDoctorIds(userDoctorIds);
        }
        PageResult<DoctorConsultationOneParam> list = doctorDepartmentCoupleDao.listDoctorForConsultation(doctorParam);
        setDoctorDepartmentNames(list.getDataList());
        return list;
    }

    /**
     * 查询医师信息集合
     *
     * @param doctorIds 医师id集合
     * @return
     */
    public List<DoctorSimpleVo> listDoctorSimpleInfo(List<Integer> doctorIds) {
        return doctorDao.listDoctorSimpleInfo(doctorIds);
    }

    /**
     * 根据医师信息获取医师所属科室
     *
     * @param doctorId 医师id
     * @return List<Department>
     */
    public List<DepartmentListVo> selectDepartmentsByDoctorId(Integer doctorId) {
        return doctorDao.selectDepartmentsByDoctorId(doctorId);
    }

    /**
     * 根据医师职称id查询职称名
     *
     * @param doctorOneParam 医师职称id
     * @return String
     */
    public String selectDoctorTitle(DoctorOneParam doctorOneParam) {
        return doctorDao.selectDoctorTitle(doctorOneParam);
    }

    public List<DoctorOneParam> getSelectDoctorList(DoctorListParam param) {
        return doctorDao.getSelectDoctorList(param);
    }

    /**
     * 设置医师科室信息，医院信息
     *
     * @param list
     */
    public void setDoctorDepartmentNames(List<DoctorConsultationOneParam> list) {
        for (DoctorConsultationOneParam item : list) {
            item.setHospitalName(getHospitalName());
            List<String> departmentList = doctorDepartmentCoupleDao.getDepartmentNamesByDoctorId(item.getId());
            if (departmentList.size() > 0) {
                item.setDepartmentName(Joiner.on(",").join(departmentList));
            }
        }
    }

    /**
     * 新增用户医师关注
     *
     * @param
     * @return
     */
    public void insertUserDoctor(UserDoctorParam param) {
        UserDoctorAttentionDo userDoctorAttentionDo = new UserDoctorAttentionDo();
        FieldsUtil.assign(param, userDoctorAttentionDo);
        userDoctorAttentionDao.insertUserDoctor(userDoctorAttentionDo);
        updateAttentionNumberByDoctorId(param.getDoctorId());
    }

    /**
     * 解除用户医师关注
     *
     * @param param
     */
    public void deleteUserDoctor(UserDoctorParam param) {
        userDoctorAttentionDao.deleteUserDoctor(param);
        updateAttentionNumberByDoctorId(param.getDoctorId());
    }

    /**
     * 更新医师上班状态
     *
     * @param param
     * @return int
     */
    public void updateOnDuty(DoctorDutyParam param) {
        doctorDao.updateOnDuty(param);
        DoctorDutyRecordParam doctorDutyRecordParam = new DoctorDutyRecordParam();
        doctorDutyRecordParam.setDoctorId(param.getDoctorId());
        doctorDutyRecordParam.setDutyStatus(param.getOnDutyStatus());
        doctorDutyRecordParam.setType(param.getType());
        insertDoctorDutyRecord(doctorDutyRecordParam);
        if (DoctorConstant.NOT_ON_DUTY.equals(param.getOnDutyStatus())) {
            param.setOnDutyTime(DateUtils.getTimeStampPlus(1, ChronoUnit.DAYS));
            doctorDao.updateOnDutyTime(param);
        }
    }

    /**
     * 医师自动上班
     */
    public void onDutyDoctorTask() {
        List<Integer> doctorIds = doctorDao.listNotOnDutyDoctorIds();
        doctorIds.forEach(doctorId -> {
            DoctorDutyParam doctorDuty = new DoctorDutyParam();
            doctorDuty.setDoctorId(doctorId);
            doctorDuty.setOnDutyStatus(DoctorConstant.ON_DUTY);
            doctorDuty.setType(DoctorConstant.DOCTOC_DUTY_AUTOMATIC);
            updateOnDuty(doctorDuty);
        });
    }

    /**
     * 新增医师上下班记录
     *
     * @param
     * @return
     */
    public void insertDoctorDutyRecord(DoctorDutyRecordParam param) {
        DoctorDutyRecordDo doctorDutyRecordDo = new DoctorDutyRecordDo();
        FieldsUtil.assign(param, doctorDutyRecordDo);
        doctorDutyRecordDao.insertDoctorDutyRecord(doctorDutyRecordDo);
    }

    /**
     * 更新医师平均响应时间
     *
     * @param param
     */
    public void updateAvgAnswerTime(DoctorSortParam param) {
        doctorDao.updateAvgAnswerTime(param);
    }

    /**
     * 更新医师接诊数
     *
     * @param param
     */
    public void updateConsultationNumber(DoctorSortParam param) {
        doctorDao.updateConsultationNumber(param);
    }

    /**
     * 更新医师平均评分
     *
     * @param param
     */
    public void updateAvgCommentStar(DoctorSortParam param) {
        doctorDao.updateAvgCommentStar(param);
    }

    /**
     * 更新医师关注数
     *
     * @param param
     */
    public void updateAttentionNumber(DoctorSortParam param) {
        doctorDao.updateAttentionNumber(param);
    }

    /**
     * 更新医师关注数(根据医师Id)
     *
     * @param doctorId
     */
    public void updateAttentionNumberByDoctorId(Integer doctorId) {
        Integer attentionNumber = userDoctorAttentionDao.getAttentionNumber(doctorId);
        DoctorSortParam doctorSortParam = new DoctorSortParam();
        doctorSortParam.setDoctorId(doctorId);
        doctorSortParam.setAttentionNumber(attentionNumber);
        updateAttentionNumber(doctorSortParam);
    }

    /**
     * 更新医师登录token
     *
     * @param doctorId
     * @param userToken
     */
    public void updateUserToken(Integer doctorId, String userToken) {
        doctorDao.updateUserToken(doctorId, userToken);
    }

    /**
     * 处理医师wx账号
     *
     * @param doctorId
     * @param status
     */
    public void dealDoctorWx(Integer doctorId, Byte status) throws MpException {
        DoctorOneParam doctorInfo = getOneInfo(doctorId);
        if (doctorInfo.getUserId() > 0) {
            jedisManager.delete(doctorInfo.getUserToken());
            Byte userType = DoctorConstant.ABLE.equals(status) ? AuthConstant.AUTH_TYPE_DOCTOR_USER : AuthConstant.AUTH_TYPE_NORMAL_USER;
            userDao.updateUserDoctorAuth(doctorInfo.getUserId(), userType);
        }
    }

    /**
     * 根userId更新医师token
     *
     * @param userId
     * @param userToken
     */
    public void updateUserTokenByUserId(Integer userId, String userToken) {
        doctorDao.updateUserTokenByUserId(userId, userToken);
    }

    /**
     * 删除医师登录token
     *
     * @param doctorId
     */
    public void deleteUserToken(Integer doctorId) throws MpException {
        jedisManager.delete(getOneInfo(doctorId).getUserToken());
    }

    /**
     * 咨询医师详情
     *
     * @param param
     * @return
     */
    public DoctorOneParam getWxDoctorInfo(UserDoctorParam param) throws MpException {
        DoctorOneParam doctorInfo = getOneInfo(param.getDoctorId());
        setDoctorDepartmentTitle(doctorInfo);
        doctorInfo.setIsAttention(userDoctorAttentionDao.isAttention(param));
        doctorInfo.setHospitalName(getHospitalName());
        doctorInfo.setAnswerType(getAnswerHour(doctorInfo.getAvgAnswerTime()));
        return doctorInfo;
    }

    private static byte getAnswerHour(Integer seconds) {
        float num = (float) seconds / 60;
        if (num < ANSWER_TIME_TEN_MUNITES) {
            return DoctorConstant.TEN_MUNITE_IN;
        } else if (num < ANSWER_TIME_HALF_HOUR) {
            return DoctorConstant.HALF_HOUR_IN;
        } else if (num < ANSWER_TIME_ONE_HOUR) {
            return DoctorConstant.ONE_HOUR_IN;
        } else {
            return DoctorConstant.ONE_HOUR_OUT;
        }

    }

    /**
     * 设置医师科室信息，医院信息
     *
     * @param param
     */
    public void setDoctorDepartmentTitle(DoctorOneParam param) {
        param.setHospitalName(getHospitalName());
        List<String> departmentList = doctorDepartmentCoupleDao.getDepartmentNamesByDoctorId(param.getId());
        if (departmentList.size() > 0) {
            param.setDepartmentName(Joiner.on(",").join(departmentList));
        }
        String titleName = titleService.getTitleName(param.getTitleId());
        param.setTitleName(titleName);
    }

    /**
     * 医师解绑
     *
     * @param doctorUnbundlingParam doctorId
     */
    public void doctorUnbundling(DoctorUnbundlingParam doctorUnbundlingParam) {
        DoctorOneParam oneInfo = doctorDao.getOneInfo(doctorUnbundlingParam.getDoctorId());
        this.transaction(() -> {
            // 修改数据库表中信息
            userDao.unbundlingUserType(oneInfo.getUserId());
            doctorDao.unbundlingDoctorAuth(doctorUnbundlingParam.getDoctorId());
            this.deleteUserToken(doctorUnbundlingParam.getDoctorId());
            doctorDao.unbundlingDoctorToken(doctorUnbundlingParam.getDoctorId());
        });
        // 删除医师token，让用户重新登录
    }

    /**
     * admin医师是否接诊
     *
     * @param doctorUnbundlingParam doctorId
     */
    public void doctorCanConsultation(DoctorUnbundlingParam doctorUnbundlingParam) {
        doctorDao.canConsultation(doctorUnbundlingParam.getDoctorId());
    }

    public void testTemplateMessage() {
        // 订阅消息
        String[][] maData = new String[][]{
            {"患者信息"},
            {"病情描述"},
            {Util.getdate("yyyy-MM-dd HH:mm:ss")},
            {"温馨提示"}
        };

        List<Integer> arrayList = Collections.<Integer>singletonList(24);
        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        // 公众号消息
        String[][] mpData = new String[][]{
            {"患者信息"},
            {"病情描述"},
            {Util.getdate("yyyy-MM-dd HH:mm:ss")},
            {"温馨提示"}
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.CONSULTATION_ORDER_PAY).data(data).build())
//            .mpTemplateData(
//                MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
            .page("pages/account/account").shopId(getShopId())
            .userIdList(arrayList)
            .type(MessageTemplateConfigConstant.NEW_CONSULTATION).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 更新医师咨询总金额
     *
     * @param param
     */
    public void updateConsultationTotalMoney(DoctorSortParam param) {
        doctorDao.updateConsultationTotalMoney(param);
    }

    /**
     * 获取考勤
     *
     * @return
     */
    public DoctorAttendanceVo getAttendance(Integer userId, String doctorCode, Integer doctorId) {
        DateTime date = DateUtil.date();
        AnchorPointsListParam param = new AnchorPointsListParam();
        param.setEvent(DOCTOR_ENTER_IN.getEvent());
        param.setKey(DOCTOR_ENTER_IN.getKey());
        param.setUserId(1);
        DoctorOneParam daoOneInfo = doctorDao.getOneInfo(doctorId);
        param.setStartTime(DateUtil.beginOfMonth(date).toTimestamp());
        //结束时间不能大于今天
        param.setEndTime(date.toTimestamp());
        Integer prescriptionNum = prescriptionService.countDateByDoctor(doctorCode, param.getStartTime(), param.getEndTime());
        logger().info("医师code:{},处方数量{}", doctorCode, prescriptionNum);
        Integer receivingNumber = inquiryOrderService.countByDateDoctor(doctorId, param.getStartTime(), param.getEndTime());
        logger().info("医师id:{},接诊数{}", doctorId, receivingNumber);
        BigDecimal inquiryOrderRebate = inquiryOrderRebateService.getRealRebateByDoctorDate(doctorId, param.getStartTime(), param.getEndTime());
        BigDecimal prescriptionRebate = prescriptionRebateService.getRealRebateByDoctorDate(doctorId, param.getStartTime(), param.getEndTime());
        logger().info("医师id:{},服务费{}", doctorId, receivingNumber);
        //从注册那天算起
        if (daoOneInfo.getAuthTime()!=null&&daoOneInfo.getAuthTime().after(param.getStartTime())){
            param.setStartTime(daoOneInfo.getAuthTime());
        }
        int attendanceDay = doctorLoginLogDao.getDoctorAttendanceDayNum(doctorId,param.getStartTime(),param.getEndTime());
        /** 应出勤天数*/
        Long  dueAttendanceDay = DateUtil.betweenDay(param.getStartTime(), param.getEndTime(),true)+1;
        String doctorAttendanceRate = BigDecimal.valueOf(attendanceDay).divide(BigDecimal.valueOf(dueAttendanceDay), 3, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        logger().info("医师userId:{},出勤率{}", userId, doctorAttendanceRate);
        DoctorAttendanceVo vo = new DoctorAttendanceVo();
        vo.setDoctorAttendanceRate(doctorAttendanceRate);
        vo.setPrescriptionNum(prescriptionNum);
        vo.setReceivingNumber(receivingNumber);
        vo.setAttendanceDay(attendanceDay);
        vo.setDueAttendanceDay(dueAttendanceDay.intValue());
        vo.setServiceCharge(inquiryOrderRebate.add(prescriptionRebate).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        return vo;
    }

    /**
     * 获取所有医师的信息
     *
     * @return
     */
    public List<DoctorOneParam> getAllDoctor(Integer doctorId) {
        return doctorDao.getAllDoctor(doctorId);
    }

    /**
     * 获取科室处方统计数据
     *
     * @param param
     * @return
     */
    public DoctorStatisticOneParam getDoctorInquiryData(DoctorStatisticParam param) {
        return doctorDao.getDoctorInquiryData(param);
    }

    /**
     * 获取科室接诊统计数据
     *
     * @param param
     * @return
     */
    public Integer getDoctorConsultationData(DoctorStatisticParam param) {
        return doctorDao.getDoctorConsultationData(param);
    }

    /**
     * 获取科室处方统计数据
     *
     * @param param
     * @return
     */
    public DoctorStatisticOneParam getDoctorPrescriptionData(DoctorStatisticParam param) {
        return doctorDao.getDoctorPrescriptionData(param);
    }

    /**
     * 获取科室统计信息
     *
     * @param param
     * @return
     */
    public DoctorSummaryTrendDo getDoctorStatisData(DoctorStatisticParam param) {
        DoctorSummaryTrendDo data = new DoctorSummaryTrendDo();
        data.setConsultationNumber(getDoctorConsultationData(param));

        DoctorStatisticOneParam inquiryData = getDoctorInquiryData(param);
        BigDecimal inquiryMoney = inquiryData.getInquiryMoney() == null ? (new BigDecimal(0.00)) : inquiryData.getInquiryMoney();
        data.setInquiryMoney(inquiryMoney);
        data.setInquiryNumber(inquiryData.getInquiryNumber());

        DoctorStatisticOneParam prescriptionData = getDoctorPrescriptionData(param);
        BigDecimal prescriptionMoney = prescriptionData.getPrescriptionMoney() == null ? (new BigDecimal(0.00)) : prescriptionData.getPrescriptionMoney();
        data.setPrescriptionMoney(prescriptionMoney);
        data.setPrescriptionNum(prescriptionData.getPrescriptionNum());

        data.setConsumeMoney(inquiryMoney.add(prescriptionMoney));
        return data;
    }

    /**
     * 查询医师关联患者信息
     * @param doctorQueryPatientParam 医师查询关联患者列表入参
     * @return PageResult<DoctorQueryPatientVo>
     */
    public PageResult<DoctorQueryPatientVo> getDoctorQueryPatient(DoctorQueryPatientParam doctorQueryPatientParam) {
        List<DoctorQueryPatientVo> doctorQueryPatientWithPrescription = patientDao.getDoctorQueryPatientWithPrescription(doctorQueryPatientParam);
        List<DoctorQueryPatientVo> doctorQueryPatientWithInquiry = patientDao.getDoctorQueryPatientWithInquiry(doctorQueryPatientParam);
        Map<String, DoctorQueryPatientVo> prescriptionCollect = doctorQueryPatientWithPrescription.stream().collect(Collectors.toMap(DoctorQueryPatientVo::getPatientName, Function.identity()));
        Map<String, DoctorQueryPatientVo> inquiryCollect = doctorQueryPatientWithInquiry.stream().collect(Collectors.toMap(DoctorQueryPatientVo::getPatientName, Function.identity()));
        merge2ResultMap(prescriptionCollect, inquiryCollect);
        List<DoctorQueryPatientVo> result = new ArrayList<>(prescriptionCollect.values());
        PageResult<DoctorQueryPatientVo> patientVoPageResult = new PageResult<>();
        Integer pageCount = (Integer)(int)Math.ceil((double) result.size() / Double.valueOf(doctorQueryPatientParam.getPageRows()));
        if (doctorQueryPatientParam.getCurrentPage() > pageCount) {
            doctorQueryPatientParam.setCurrentPage(pageCount);
        }
        if (doctorQueryPatientParam.getCurrentPage() <= 0) {
            doctorQueryPatientParam.setCurrentPage(1);
        }
        Page page = Page.getPage(result.size(), doctorQueryPatientParam.getCurrentPage(), doctorQueryPatientParam.getPageRows());
        patientVoPageResult.setPage(page);
        if (result.size() == 0) {
            patientVoPageResult.setDataList(new ArrayList<>());
            return patientVoPageResult;
        }
        if (doctorQueryPatientParam.getCurrentPage() >= page.getPageCount()) {
            List<DoctorQueryPatientVo> doctorQueryPatientVos = result.subList((doctorQueryPatientParam.getCurrentPage() - 1) * doctorQueryPatientParam.getPageRows(), page.getTotalRows());
            patientVoPageResult.setDataList(doctorQueryPatientVos);
            return patientVoPageResult;
        }
        List<DoctorQueryPatientVo> doctorQueryPatientVos = result.subList((doctorQueryPatientParam.getCurrentPage() - 1) * doctorQueryPatientParam.getPageRows(), (doctorQueryPatientParam.getCurrentPage() * doctorQueryPatientParam.getPageRows()) - 1);
        patientVoPageResult.setDataList(doctorQueryPatientVos);
        return patientVoPageResult;
    }

    /**
     * 查询医师关联问诊信息
     *
     * @param doctorQueryInquiryParam 医师查询关联问诊列表入参
     * @return PageResult<DoctorQueryInquiryVo>
     */
    public PageResult<DoctorQueryInquiryVo> getDoctorQueryInquiry(DoctorQueryInquiryParam doctorQueryInquiryParam) {
        return inquiryOrderDao.getDoctorQueryInquiry(doctorQueryInquiryParam);
    }

    /**
     * 根据医师id查询关联处方
     * @param doctorQueryPrescriptionParam 查询处方入参
     * @return PageResult<DoctorQueryPrescriptionVo>
     */
    public PageResult<DoctorQueryPrescriptionVo> getDoctorQueryPrescription(DoctorQueryPrescriptionParam doctorQueryPrescriptionParam) {
        PageResult<DoctorQueryPrescriptionVo> doctorQueryPrescription = prescriptionDao.getDoctorQueryPrescription(doctorQueryPrescriptionParam);
        doctorQueryPrescription.getDataList().forEach(doctorQueryPrescriptionVo -> {
            List<String> prescriptionGoodsNameByPrescriptionCode = prescriptionItemDao.getPrescriptionGoodsNameByPrescriptionCode(doctorQueryPrescriptionVo.getPrescriptionCode());
            doctorQueryPrescriptionVo.setGoodsNames(prescriptionGoodsNameByPrescriptionCode);
        });
        return doctorQueryPrescription;
    }

    /**
     * 获取医师业绩统计详情
     * @param param
     * @return
     */
    public DoctorDetailPerformanceVo getDoctorPerformanceDetail(DoctorDetailPerformanceParam param){
        DoctorOneParam doctor=doctorDao.getOneInfo(param.getDoctorId());
        DoctorDetailPerformanceVo doctorDetailPerformanceVo=new DoctorDetailPerformanceVo();
        //出勤
        Timestamp authTime=DateUtil.beginOfDay(doctor.getAuthTime()).toTimestamp();
        if(param.getStartTime().before(authTime)){
            param.setStartTime(authTime);
        }
        if(param.getEndTime().after(DateUtil.endOfDay(DateUtils.getLocalDateTime()).toTimestamp())){
            param.setEndTime(DateUtils.getLocalDateTime());
        }
        DoctorAttendanceOneParam doctorAttend = doctorLoginLogDao.getDoctorAttend(param.getDoctorId(), param.getStartTime(), param.getEndTime());
        if(doctorAttend!=null){
            Integer[] timeDifference = DateUtils.getTimeDifference(param.getEndTime(), param.getStartTime());
            doctorAttend.setLoginRate(new BigDecimal(Double.valueOf(doctorAttend.getLoginDays())/Double.valueOf(timeDifference[0]+1)).setScale(2, BigDecimal.ROUND_HALF_UP));
            FieldsUtil.assign(doctorAttend,doctorDetailPerformanceVo);
        }
        //问诊
        DoctorDetailPerformanceVo inquiryCount=inquiryOrderDao.getCountNumByDateDoctorId(param.getDoctorId(),param.getStartTime(),param.getEndTime());
        doctorDetailPerformanceVo.setInquiryMoney(inquiryCount.getInquiryMoney());
        doctorDetailPerformanceVo.setInquiryNumber(inquiryCount.getInquiryNumber());
        Integer receiveCount = inquiryOrderDao.countByDateDoctorId(param.getDoctorId(), param.getStartTime(), param.getEndTime());
        doctorDetailPerformanceVo.setConsultationNumber(receiveCount);
        //处方
        DoctorDetailPerformanceVo prescriptionCount=prescriptionDao.countSumDateByDoctor(doctor.getHospitalCode(),param.getStartTime(),param.getEndTime());
        doctorDetailPerformanceVo.setPrescriptionMoney(prescriptionCount.getPrescriptionMoney());
        doctorDetailPerformanceVo.setPrescriptionNum(prescriptionCount.getPrescriptionNum());
        doctorDetailPerformanceVo.setConsumeMoney(doctorDetailPerformanceVo.getPrescriptionMoney().add(doctorDetailPerformanceVo.getInquiryMoney()));
        return doctorDetailPerformanceVo;
    }

    public String getHospitalName(){
        StorePojo hospitalInfo = storeService.getHospitalInfo();
        if (hospitalInfo == null) {
            return HOSPITAL_NAME;
        } else {
            return hospitalInfo.getStoreName();
        }
    }

}
