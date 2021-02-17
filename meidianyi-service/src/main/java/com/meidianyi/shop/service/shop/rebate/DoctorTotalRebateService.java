package com.meidianyi.shop.service.shop.rebate;

import com.meidianyi.shop.dao.shop.rebate.DoctorTotalRebateDao;
import com.meidianyi.shop.dao.shop.rebate.DoctorWithdrawDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfig;
import com.meidianyi.shop.service.pojo.shop.rebate.DoctorTotalRebateVo;
import com.meidianyi.shop.service.pojo.shop.rebate.DoctorWithdrawConstant;
import com.meidianyi.shop.service.shop.config.RebateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Service
public class DoctorTotalRebateService extends ShopBaseService {
    @Autowired
    private DoctorTotalRebateDao doctorTotalRebateDao;
    @Autowired
    private DoctorWithdrawDao doctorWithdrawDao;
    @Autowired
    private RebateConfigService rebateConfigService;

    /**
     * 根据医师id查询
     * @param doctorId
     * @return
     */
    public DoctorTotalRebateVo getRebateByDoctorId(Integer doctorId){
        DoctorTotalRebateVo doctorTotalRebateVo=doctorTotalRebateDao.getRebateByDoctorId(doctorId);
        if(doctorTotalRebateVo==null){
            return new DoctorTotalRebateVo();
        }
        //配置中的提现额度大小限制
        RebateConfig rebateConfig=rebateConfigService.getRebateConfig();
        if(rebateConfig!=null){
            doctorTotalRebateVo.setWithdrawCashMix(rebateConfig.getWithdrawCashMix());
            doctorTotalRebateVo.setWithdrawCashMax(rebateConfig.getWithdrawCashMax());
        }
        //累计提现金额
        BigDecimal accruingWithdrawCash=BigDecimal.ZERO;
        BigDecimal paySuccessWithDrawCash=doctorWithdrawDao.getWithdrawCashSum(doctorId, DoctorWithdrawConstant.WITHDRAW_CHECK_PAY_SUCCESS,null,null);
        if(paySuccessWithDrawCash!=null){
            accruingWithdrawCash=accruingWithdrawCash.add(paySuccessWithDrawCash);
        }
        doctorTotalRebateVo.setAccruingWithdrawCash(accruingWithdrawCash);
        //待提现金额
        BigDecimal waitWithdrawCash=BigDecimal.ZERO;
        BigDecimal waitCheckWithdrawCash=doctorWithdrawDao.getWithdrawCashSum(doctorId,DoctorWithdrawConstant.WITHDRAW_CHECK_WAIT_CHECK,null,null);
        BigDecimal waitPayWithdrawCash=doctorWithdrawDao.getWithdrawCashSum(doctorId,DoctorWithdrawConstant.WITHDRAW_CHECK_WAIT_PAY,null,null);
        if(waitCheckWithdrawCash!=null){
            waitWithdrawCash=waitWithdrawCash.add(waitCheckWithdrawCash);
        }
        if(waitPayWithdrawCash!=null){
            waitWithdrawCash=waitWithdrawCash.add(waitPayWithdrawCash);
        }
        doctorTotalRebateVo.setWaitWithdrawCash(waitWithdrawCash);
        return doctorTotalRebateVo;
    }
}
