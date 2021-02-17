package com.meidianyi.shop.service.shop.recharge;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.shop.recharge.SmsRechargeDao;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.recharge.RechargeParam;
import com.meidianyi.shop.service.pojo.shop.sms.recharge.SmsAccountRechargeListVo;
import com.meidianyi.shop.service.pojo.shop.sms.recharge.SmsAccountRechargeRecordParam;
import com.meidianyi.shop.service.pojo.shop.sms.recharge.SmsRechargeRecordVo;
import com.meidianyi.shop.service.shop.config.SmsAccountConfigService;
import com.meidianyi.shop.service.shop.sms.SmsAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author 赵晓东
 * @description 短信平台充值
 * @create 2020-07-27 16:47
 **/

@Service
public class RechargeService extends ShopBaseService {

    @Autowired
    private SmsRechargeDao rechargeDao;

    @Autowired
    private SmsAccountService smsAccountService;

    @Autowired
    private SmsAccountConfigService smsAccountConfigService;

    /**
     * 同步本地库充值记录
     * @param smsAccountRechargeListVo 调取二方库回参
     */
    protected void addRechargeList(SmsAccountRechargeListVo smsAccountRechargeListVo){
        rechargeDao.fetchRechargeList(smsAccountRechargeListVo);
    }

    /**
     * admin端查询充值记录时，先调取二方库增量拉取充值信息到本地，再将数据回传给前端
     * @param rechargeParam 前端调取入参
     * @return RechargeVo
     */
    public PageResult<SmsRechargeRecordVo> getRechargeList(RechargeParam rechargeParam) {
        //查询当前账户id
        SmsAccountRechargeRecordParam smsAccountRechargeRecordParam = new SmsAccountRechargeRecordParam();
        smsAccountRechargeRecordParam.setSid(smsAccountConfigService.getShopSmsAccountConfig());
        FieldsUtil.assign(rechargeParam, smsAccountRechargeRecordParam);
        SmsAccountRechargeListVo smsAccountRechargeListVo = new SmsAccountRechargeListVo();
        try {
            //从二方库拉取数据
            smsAccountRechargeListVo = smsAccountService.listSmsAccountRechargeRecord(smsAccountRechargeRecordParam);
            //向本地库同步
            if (smsAccountRechargeListVo != null && smsAccountRechargeListVo.getData() != null) {
                addRechargeList(smsAccountRechargeListVo);
            }
        } catch (MpException e) {
            e.printStackTrace();
        }
        //向前端返回回参
        PageResult<SmsRechargeRecordVo> rechargePage = rechargeDao.getRechargePage(rechargeParam);
        return rechargePage;
    }

}
