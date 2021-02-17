package com.meidianyi.shop.service.shop.sms;

import com.meidianyi.shop.dao.main.SmsConfigDao;
import com.meidianyi.shop.service.pojo.shop.sms.SmsConfigParam;
import com.meidianyi.shop.service.pojo.shop.sms.SmsConfigVo;
import com.meidianyi.shop.service.pojo.shop.sms.account.SmsAccountInfoVo;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-17 14:12
 **/

@Service
public class SmsConfigService {

    @Autowired
    private SmsConfigDao smsConfigDao;

    @Autowired
    private ShopCommonConfigService shopCommonConfigService;

    /**
     * 修改店铺短信发送配置
     * @param smsConfigParam 短信配置参数
     */
    public void updateSmsConfig(SmsConfigParam smsConfigParam) {
        smsConfigDao.updateSmsConfig(smsConfigParam);
    }

    /**
     * 打开短信配置页面时显示之前配置内容
     * @param shopId 店铺id
     * @return SmsConfigVo
     */
    public SmsConfigVo selectSmsConfig(Integer shopId) {
        SmsAccountInfoVo smsAccountInfo = shopCommonConfigService.getSmsAccountInfo();
        SmsConfigVo smsConfigVo = smsConfigDao.selectSmsConfig(shopId);
        smsConfigVo.setSmsAccountInfo(smsAccountInfo);
        return smsConfigVo;
    }

}
