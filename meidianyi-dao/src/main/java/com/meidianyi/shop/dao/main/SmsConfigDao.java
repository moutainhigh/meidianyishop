package com.meidianyi.shop.dao.main;

import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.service.pojo.shop.sms.SmsConfigParam;
import com.meidianyi.shop.service.pojo.shop.sms.SmsConfigVo;
import lombok.Data;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.main.Tables.SHOP_SMS_CONFIG;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-17 14:13
 **/

@Repository
public class SmsConfigDao extends MainBaseDao {

    /**
     * 修改店铺短信发送配置
     * @param smsConfigParam 店铺配置参数
     */
    public void updateSmsConfig(SmsConfigParam smsConfigParam) {
        db().update(SHOP_SMS_CONFIG)
            .set(SHOP_SMS_CONFIG.USER_CHECK_CODE_NUM, smsConfigParam.getUserCheckCodeNum())
            .set(SHOP_SMS_CONFIG.PATIENT_CHECK_CODE_NUM, smsConfigParam.getPatientCheckCodeNum())
            .set(SHOP_SMS_CONFIG.MARKETING_NUM, smsConfigParam.getMarketingNum())
            .set(SHOP_SMS_CONFIG.INDUSTRY_NUM, smsConfigParam.getIndustryNum())
            .where(SHOP_SMS_CONFIG.SHOP_ID.eq(smsConfigParam.getShopId())).execute();
    }

    /**
     * 打开短信配置页面时显示之前配置内容
     * @param shopId 店铺id
     * @return SmsConfigVo
     */
    public SmsConfigVo selectSmsConfig(Integer shopId) {
        return db().select().from(SHOP_SMS_CONFIG)
            .where(SHOP_SMS_CONFIG.SHOP_ID.eq(shopId))
            .fetchAnyInto(SmsConfigVo.class);
    }

    /**
     * 创建店铺时初始化短信配置``
     * @param shopId 店铺id
     * @return Integer
     */
    public Integer initSmsConfig(Integer shopId) {
        return db().insertInto(SHOP_SMS_CONFIG)
            .set(SHOP_SMS_CONFIG.SHOP_ID, shopId).execute();
    }

}
