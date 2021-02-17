package com.meidianyi.shop.service.shop.sms;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RandomUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.SmsApiConfig;
import com.meidianyi.shop.dao.main.SmsConfigDao;
import com.meidianyi.shop.dao.shop.config.ShopCfgDao;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.dao.shop.sms.SmsSendRecordDao;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.patient.PatientSmsCheckNumParam;
import com.meidianyi.shop.service.pojo.shop.patient.PatientSmsCheckParam;
import com.meidianyi.shop.service.pojo.shop.sms.ResponseMsgVo;
import com.meidianyi.shop.service.pojo.shop.sms.SmsBaseParam;
import com.meidianyi.shop.service.pojo.shop.sms.SmsCheckParam;
import com.meidianyi.shop.service.pojo.shop.sms.SmsConfigVo;
import com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordAdminParam;
import com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordAdminVo;
import com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordParam;
import com.meidianyi.shop.service.pojo.shop.sms.base.SmsBaseRequest;
import com.meidianyi.shop.service.pojo.shop.sms.template.SmsTemplate;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import com.meidianyi.shop.service.shop.config.SmsAccountConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.CODE_SUCCESS;
import static com.meidianyi.shop.common.foundation.data.JsonResultCode.SMS_OUT_OF_LIMITS;
import static com.meidianyi.shop.config.SmsApiConfig.REDIS_KEY_SMS_USER_CHECK_NUM;
import static com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordConstant.SMS_FIND_FAIL;
import static com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordConstant.SMS_FIND_SUCCESS;

/**
 * 发送短信
 * @author 孔德成
 * @date 2020/7/24 9:17
 */
@Service
@Slf4j
public class SmsService extends BaseShopConfigService {

    @Autowired
    private MpAuthShopService mpAuthShopService;
    @Autowired
    protected  SmsApiConfig smsApiConfig;
    @Autowired
    protected SmsSendRecordDao smsSendRecordDao;
    @Autowired
    private ShopCfgDao shopCfgDao;
    @Autowired
    private SmsAccountConfigService smsAccountConfigService;
    @Autowired
    private JedisManager jedisManager;
    @Autowired
    private SmsConfigDao smsConfigDao;
    @Autowired
    private PatientDao patientDao;

    /**
     * 发送短信
     */
    public String sendSms(SmsBaseParam param) throws MpException {
        String account = smsAccountConfigService.getShopSmsAccountConfig();
        if (Strings.isBlank(account)){
            return null;
        }
        param.setSid(account);
        //拼写请求内容
        return sendSmsParam(param);
    }


    /**
     * 发送短信
     * @param mobiles 电话  ,分隔
     * @param content 短信内容
     * @param sId 短信账号
     * @param ext 类型
     */
    public String sendSms(Integer userId,String mobiles,String content,String ext,String sId) throws MpException {
        SmsBaseParam param =new SmsBaseParam();
        param.setContent(content);
        param.setMobiles(mobiles);
        param.setSid(sId);
        param.setChannel(SmsApiConfig.CHANNEL_DEFAULT);
        param.setProduct(SmsApiConfig.PRODUCT_WPB_WX);
        param.setExt(ext);
        param.setUserId(userId);
        return sendSms(param);
    }
    /**
     * 发送短信
     *  -默认账号
     * @param mobiles 电话  ,分隔
     * @param content 短信内容
     * @param ext 类型
     */
    public String sendSms(Integer userId,String mobiles,String content,String ext) throws MpException {
        String account = smsAccountConfigService.getShopSmsAccountConfig();
        if (Strings.isBlank(account)){
            return null;
        }
        SmsBaseParam param =new SmsBaseParam();
        param.setContent(content);
        param.setMobiles(mobiles);
        param.setSid(account);
        param.setChannel(SmsApiConfig.CHANNEL_DEFAULT);
        param.setProduct(SmsApiConfig.PRODUCT_WPB_WX);
        param.setExt(ext);
        param.setUserId(userId);
        return sendSms(param);
    }

    /**
     * 发送短信
     * -默认账号
     * -默认验证码方式
     * @param mobiles 电话,分隔
     * @param content 短信内容
     */
    public String sendSms(Integer userId,String mobiles,String content) throws MpException {
        String account = smsAccountConfigService.getShopSmsAccountConfig();
        if (Strings.isBlank(account)){
            return null;
        }
        SmsBaseParam param =new SmsBaseParam();
        param.setContent(content);
        param.setMobiles(mobiles);
        param.setSid(account);
        param.setChannel(SmsApiConfig.CHANNEL_DEFAULT);
        param.setProduct(SmsApiConfig.PRODUCT_WPB_WX);
        param.setExt(SmsApiConfig.EXT_CHECK_CODE);
        param.setUserId(userId);
       return sendSms(param);
    }


    /**
     * 发送短信请求
     * @param param
     * @return
     */
    private String sendSmsParam(SmsBaseParam param) throws MpException {
        long time = System.currentTimeMillis()/1000;
        SmsBaseRequest request  =new SmsBaseRequest();
        request.setSms(Util.toJson(param));
        request.setApiMethod(SmsApiConfig.METHOD_SMS_SEND);
        request.setAppKey(smsApiConfig.getAppKey());
        request.setTimestamp(time);
        Map<String, Object> postBody = Util.transBeanToMap(request);
        postBody.put("sign", generateSing(postBody));
        HttpResponse response = requestApi(postBody);
        SmsSendRecordParam record =new SmsSendRecordParam();
        record.setAccountName(param.getSid());
        record.setUserId(param.getUserId());
        record.setMobile(param.getMobiles());
        record.setExt(param.getExt());
        record.setRequestMsg(param.getContent());
        record.setResponseMsg(response.body());
        record.setResponseCode(response.getStatus()+"");
        smsSendRecordDao.save(record);
        return response.body();
    }

    /**
     * 升序
     * @param map
     * @return
     */
    public static  Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap= new TreeMap<>(String::compareTo);
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 生成签名
     * @param map
     * @return
     */
    public String generateSing(Map<String, Object> map){
        StringBuilder str = new StringBuilder(smsApiConfig.getAppSecret());
        map.forEach((k, v)-> str.append(k).append(v));
        str.append(smsApiConfig.getAppSecret());
        return Util.md5(str.toString()).toUpperCase();
    }

    /**
     * 调用接口,并保存发送记录
     * @param postBody
     * @return
     */
    public HttpResponse requestApi(Map<String, Object> postBody) throws MpException {
        log.info("短信request--{}",postBody);
        HttpResponse response;
        try {
             response = HttpRequest.post(smsApiConfig.getSmsUrl())
                    .header(Header.CONTENT_TYPE, "multipart/form-data")
                    .form(postBody)
                    .timeout(20000)
                    .execute();
        }catch (Exception e){
            e.printStackTrace();
            throw new MpException(JsonResultCode.CODE_ACCOUNT_SAME);
        }
        log.info("短信resPonse--{}",response.body());
        return response;
    }

    /**
     * @author 赵晓东
     * @create 2020-07-27 13:53:10
     */
    /**
     * admin端分页显示短信信息
     * @param smsSendRecordAdminParam 条件查询入参
     * @return PageResult<SmsSendRecordAdminVo>
     */
    public PageResult<SmsSendRecordAdminVo> getAdminSmsSendRecordPageList(SmsSendRecordAdminParam smsSendRecordAdminParam) {
        PageResult<SmsSendRecordAdminVo> smsSendRecordAdminVoPageResult = smsSendRecordDao.selectSmsSendRecordAdmin(smsSendRecordAdminParam);
        List<SmsSendRecordAdminVo> dataList = smsSendRecordAdminVoPageResult.dataList;
        for (SmsSendRecordAdminVo smsSendRecordAdminVo : dataList) {
            ResponseMsgVo parse = Util.json2Object(smsSendRecordAdminVo.getResponseMsg(), ResponseMsgVo.class, false);
            assert parse != null;
            if (!SMS_FIND_SUCCESS.equals(parse.getCode())) {
                smsSendRecordAdminVo.setResponseMsgCode(SMS_FIND_FAIL);
            }
        }
        return smsSendRecordAdminVoPageResult;
    }

    /**
     * 发送短信校验码
     * @param param
     */
    public void sendCheckSms(PatientSmsCheckParam param,String checkMobileFormat,String keysSms) throws MpException {
        //0000-9999
        Integer intRandom = RandomUtil.getIntRandom(RandomUtil.MIN_RANDOM_100000, RandomUtil.MAX_RANDOM_999999);
        MpAuthShopRecord mpAuthShopRecord = mpAuthShopService.getAuthShopByShopId(getShopId());
        String smsContent = String.format(checkMobileFormat, mpAuthShopRecord.getNickName(), intRandom);
        sendSms(param.getUserId(), param.getMobile(), smsContent);
        String key = String.format(keysSms, getShopId(), param.getUserId(), param.getMobile());
        jedisManager.set(key, intRandom.toString(), 600);
    }

    /**
     * 校验当前患者验证码发送数量
     */
    public JsonResultCode checkUserSmsNum(PatientSmsCheckNumParam patientSmsCheckNumParam) {
        Integer patientId = patientDao.getPatientIdByIdentityCode(patientSmsCheckNumParam.getIdentityCode());
        String key = String.format(REDIS_KEY_SMS_USER_CHECK_NUM, getShopId(), patientId);
        String s = jedisManager.get(key);
        SmsConfigVo smsConfigVo = smsConfigDao.selectSmsConfig(getShopId());
        // 如果之前不存在缓存
        if (s == null || "".equals(s)) {
            SmsCheckParam smsCheckParam = new SmsCheckParam();
            smsCheckParam.setCheckNum(smsConfigVo.getUserCheckCodeNum());
            jedisManager.set(key, Util.toJson(smsCheckParam), this.getSecondsToMorning().intValue());
        }
        s = jedisManager.get(key);
        SmsCheckParam smsCheckParam = Util.json2Object(s, SmsCheckParam.class, false);
        assert smsCheckParam != null;
        if (smsCheckParam.getCheckNum() <= 0) {
            return SMS_OUT_OF_LIMITS;
        }
        return CODE_SUCCESS;
    }

    /**
     * 计算当前时间至凌晨的时间差
     * @return Long
     */
    public Long getSecondsToMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 获取今天该用户已发送消息数
     * @param userId 用户id
     * @return Integer
     */
    public Integer getTodaySms(Integer userId, String ext) {
        ext = "checkcode";
        return smsSendRecordDao.selectTodaySms(userId,ext);
    }

    /**
     * 检查行业短信。营销短信，用户接收验证码是否超额
     * @param userId 用户id
     * @param ext 短信类型
     * @return JsonResultCode
     */
    public JsonResultCode checkIsOutOfSmsNum(Integer userId, String ext) {
        // TODO: 添加行业短信、营销短信
        ext = "checkcode";
        SmsConfigVo smsConfigVo = smsConfigDao.selectSmsConfig(getShopId());
        Integer smsNum = smsSendRecordDao.selectTodaySms(userId, ext);
        if (smsNum>=smsConfigVo.getUserCheckCodeNum()) {
            return SMS_OUT_OF_LIMITS;
        }
        else {
            return CODE_SUCCESS;
        }
    }
}
