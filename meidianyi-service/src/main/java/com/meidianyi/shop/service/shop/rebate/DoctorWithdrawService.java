package com.meidianyi.shop.service.shop.rebate;

import cn.hutool.core.date.DateUtil;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.shop.rebate.DoctorTotalRebateDao;
import com.meidianyi.shop.dao.shop.rebate.DoctorWithdrawDao;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfig;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.*;
import com.meidianyi.shop.service.shop.config.RebateConfigService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Service
public class DoctorWithdrawService extends ShopBaseService {
    @Autowired
    private DoctorWithdrawDao doctorWithDrawDao;
    @Autowired
    private DoctorTotalRebateDao doctorTotalRebateDao;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private MpPaymentService mpPaymentService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RebateConfigService rebateConfigService;

    /**
     * 提现记录列表
     * @param param
     * @return
     */
    public PageResult<DoctorWithdrawVo> getPageList(DoctorWithdrawListParam param){
        return doctorWithDrawDao.getPageList(param);
    }

    /**
     *医师提现申请
     * @param param
     */
    public void addDoctorWithdraw(DoctorWithdrawParam param) throws MpException{
        RebateConfig rebateConfig=rebateConfigService.getRebateConfig();
        DoctorTotalRebateVo doctorTotalRebateVo= doctorTotalRebateDao.getRebateByDoctorId(param.getDoctorId());
        //提现申请校验
        checkApply(param,rebateConfig,doctorTotalRebateVo);
        //出账类型 默认小程序出账
        param.setType(DoctorWithdrawConstant.RT_WX_MINI);

        //提现单号
        param.setOrderSn(IncrSequenceUtil.generateOrderSn(DoctorWithdrawConstant.ORDER_SN_PREFIX));
        //提现序号
        param.setWithdrawUserNum(String.valueOf(doctorWithDrawDao.count(param.getDoctorId())+1));
        param.setWithdrawNum(String.valueOf(doctorWithDrawDao.count(null)+1));
        //可提现金额
        param.setWithdraw(doctorTotalRebateVo.getTotalMoney());
        param.setWithdrawSource(Util.toJson(rebateConfig));
        if(RebateConfigConstant.SWITCH_ON.equals(rebateConfig.getIsAutomaticAudit())){
            //自动审核
            automaticAudit(param,doctorTotalRebateVo);
        }else {
            //手动审核
            manualAudit(param,doctorTotalRebateVo);
        }


    }

    /**
     * 手动审核
     * @param param
     * @param doctorTotalRebateVo
     */
    public void manualAudit(DoctorWithdrawParam param,DoctorTotalRebateVo doctorTotalRebateVo){
        param.setStatus(DoctorWithdrawConstant.WITHDRAW_CHECK_WAIT_CHECK);
        transaction(()->{
            doctorWithDrawDao.addDoctorWithdraw(param);
            //修改可提现金额，冻结金额
            doctorTotalRebateDao.updateTotalMoneyBlockedMoney(param.getDoctorId(),doctorTotalRebateVo.getTotalMoney().subtract(param.getWithdrawCash()),doctorTotalRebateVo.getBlockedMoney().add(param.getWithdrawCash()));
        });
    }
    /**
     * 自动审核
     * @param param
     * @throws MpException
     */
    public void automaticAudit(DoctorWithdrawParam param,DoctorTotalRebateVo doctorTotalRebateVo) throws MpException {
        DoctorOneParam doctor=doctorService.getOneInfo(param.getDoctorId());
        param.setStatus(DoctorWithdrawConstant.WITHDRAW_CHECK_PAY_SUCCESS);
        transaction(()->{
            doctorWithDrawDao.addDoctorWithdraw(param);
            DoctorWithdrawVo doctorWithdrawVo= doctorWithDrawDao.getWithdrawByOrderSn(param.getOrderSn());
            //提现出账
            pay2Person(param.getOrderSn(),param.getClientIp(),param.getRealName(),doctor.getUserId(),param.getType(),param.getWithdrawCash(),doctorWithdrawVo);
            //修改可提现金额
            doctorTotalRebateDao.updateTotalMoney(param.getDoctorId(),doctorTotalRebateVo.getTotalMoney().subtract(param.getWithdrawCash()));
        });

}

    /**
     * 提现申请校验
     * @param param
     * @throws MpException
     */
    public void checkApply(DoctorWithdrawParam param,RebateConfig rebateConfig, DoctorTotalRebateVo doctorTotalRebateVo) throws MpException{
        if(doctorTotalRebateVo==null){
            //不存在可提现金额
            throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_IS_NOT_EXIST);
        }
        if(BigDecimalUtil.compareTo(param.getWithdrawCash(),doctorTotalRebateVo.getTotalMoney())>0){
            //超出可提现金额
            throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_MAXIMUM_LIMIT_MONEY);
        }

        if(rebateConfig!=null){
            if(BigDecimalUtil.compareTo(param.getWithdrawCash(),rebateConfig.getWithdrawCashMix())<0){
                //小于单次提现最小额度限制
                throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_LESS_MINIMUM);
            }

            BigDecimal withdrawSum=doctorWithDrawDao.getWithdrawCashSum(param.getDoctorId(),DoctorWithdrawConstant.WITHDRAW_CHECK_PAY_SUCCESS, DateUtils.getTimestampForTodayStartTime(),DateUtils.getTimestampForTodayEndTime());
            if(withdrawSum!=null&&BigDecimalUtil.compareTo(param.getWithdrawCash().add(withdrawSum),rebateConfig.getWithdrawCashMax())>0){
                //大于每日提现额度限制
                throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_EXCEED_DAY_MAX_LIMIT_MONEY);
            }
        }

    }

    /**
     * 更新状态
     * @param param
     * @throws MpException
     */
    public void audit(DoctorWithdrawUpdateParam param)throws MpException {
        DoctorWithdrawVo doctorWithdrawVo= doctorWithDrawDao.getWithdrawByOrderSn(param.getOrderSn());
        RebateConfig rebateConfig=rebateConfigService.getRebateConfig();
        Byte preStatus=doctorWithdrawVo.getStatus();
        if(doctorWithdrawVo==null){
            throw new MpException(JsonResultCode.CODE_FAIL);
        }
        if(DoctorWithdrawConstant.WITHDRAW_CHECK_REFUSE.equals(doctorWithdrawVo.getStatus())){
            throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_ALREADY_REJECT);
        }
        DoctorOneParam doctor=doctorService.getOneInfo(doctorWithdrawVo.getDoctorId());
        DoctorTotalRebateVo doctorTotalRebateVo= doctorTotalRebateDao.getRebateByDoctorId(doctorWithdrawVo.getDoctorId());
        if(DoctorWithdrawConstant.WITHDRAW_CHECK_PAY_SUCCESS.equals(param.getCheckStatus())){
            if(DoctorWithdrawConstant.WITHDRAW_CHECK_WAIT_PAY.equals(preStatus)||DoctorWithdrawConstant.WITHDRAW_CHECK_PY_FAIL.equals(preStatus)){
                BigDecimal withdrawSum=doctorWithDrawDao.getWithdrawCashSum(doctorWithdrawVo.getDoctorId(),DoctorWithdrawConstant.WITHDRAW_CHECK_PAY_SUCCESS, DateUtils.getTimestampForTodayStartTime(),DateUtils.getTimestampForTodayEndTime());
                if(withdrawSum!=null&&BigDecimalUtil.compareTo(doctorWithdrawVo.getWithdrawCash().add(withdrawSum),rebateConfig.getWithdrawCashMax())>0){
                    //大于每日提现额度限制
                    throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_EXCEED_DAY_MAX_LIMIT_MONEY);
                }
                //出账,暂时注释掉
                pay2Person(param.getOrderSn(),param.getClientIp(),doctorWithdrawVo.getRealName(),doctor.getUserId(),doctorWithdrawVo.getType(),doctorWithdrawVo.getWithdrawCash(),doctorWithdrawVo);
                transaction(()->{
                    //释放冻结金额
                    doctorTotalRebateDao.updateBlockMoney(doctorWithdrawVo.getDoctorId(),doctorTotalRebateVo.getBlockedMoney().subtract(doctorWithdrawVo.getWithdrawCash()));
                    doctorWithDrawDao.update(doctorWithdrawVo.getId(),param.getCheckStatus(),param.getRefuseDesc());
                });
            }

        }else if(DoctorWithdrawConstant.WITHDRAW_CHECK_REFUSE.equals(param.getCheckStatus())){
            //审核驳回
            if(DoctorWithdrawConstant.WITHDRAW_CHECK_WAIT_CHECK.equals(preStatus)||DoctorWithdrawConstant.WITHDRAW_CHECK_WAIT_PAY.equals(preStatus)||DoctorWithdrawConstant.WITHDRAW_CHECK_PY_FAIL.equals(preStatus)){
                transaction(()->{
                    //修改可提现金额，冻结金额
                    doctorTotalRebateDao.updateTotalMoneyBlockedMoney(doctorWithdrawVo.getDoctorId(),doctorTotalRebateVo.getTotalMoney().add(doctorWithdrawVo.getWithdrawCash()),doctorTotalRebateVo.getBlockedMoney().subtract(doctorWithdrawVo.getWithdrawCash()));
                    doctorWithDrawDao.update(doctorWithdrawVo.getId(),param.getCheckStatus(),param.getRefuseDesc());
                });
            }

        }else {
            doctorWithDrawDao.update(doctorWithdrawVo.getId(),param.getCheckStatus(),param.getRefuseDesc());

        }

    }

    /**
     * 提现发红包
     * @param orderSn
     * @param ip
     * @param realName
     * @param userId
     * @param type
     * @param money
     * @throws MpException
     */
    public void pay2Person(String orderSn, String ip, String realName, Integer userId, Byte type, BigDecimal money,DoctorWithdrawVo doctorWithdrawVo) throws MpException {
        logger().info("pay2Person start");
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(getShopId());
        try {
            if(DoctorWithdrawConstant.RT_WX_MINI.equals(type)) {
                UserRecord userRecord = memberService.getUserRecordById(userId);
                mpPaymentService.companyPay(wxapp.getAppId(), ip, orderSn, userRecord.getWxOpenid(), realName, BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(DoctorWithdrawConstant.YUAN_FEN_RATIO).toString())).intValue(), "医师佣金提现");
            }
        } catch (WxPayException e) {
            doctorWithDrawDao.update(doctorWithdrawVo.getId(),DoctorWithdrawConstant.WITHDRAW_CHECK_PY_FAIL,e.getErrCodeDes());
            throw new MpException(JsonResultCode.DOCTOR_WITHDRAW_EX_ERROR,
                e.getMessage(), StringUtils.isBlank(e.getErrCodeDes()) ? e.getCustomErrorMsg() : e.getErrCodeDes());
        }
    }

    /**
     * 获取提现详情
     * @param id
     * @return
     */
    public DoctorWithdrawDetailVo getWithdrawDetail(Integer id){
        DoctorWithdrawDetailVo detail=doctorWithDrawDao.getWithdrawDetailById(id);
        DoctorWithdrawListParam param=new DoctorWithdrawListParam();
        param.setDoctorId(detail.getDoctorId());
        PageResult<DoctorWithdrawVo> withdrawList=getPageList(param);
        detail.setWithdrawList(withdrawList);
        return detail;

    }

    /**
     * 添加提现备注
     * @param param
     */
    public void addDoctorWithdrawDesc(DoctorWithdrawDescParam param){
        doctorWithDrawDao.updateDoctorWithdrawDesc(param);
    }

}
