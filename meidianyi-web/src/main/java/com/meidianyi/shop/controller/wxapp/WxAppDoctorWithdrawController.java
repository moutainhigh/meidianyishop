package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.pojo.shop.rebate.DoctorWithdrawListParam;
import com.meidianyi.shop.service.pojo.shop.rebate.DoctorWithdrawParam;
import com.meidianyi.shop.service.pojo.shop.rebate.DoctorWithdrawVo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.shop.rebate.DoctorWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@RestController
public class WxAppDoctorWithdrawController extends WxAppBaseController{
    @Autowired
    private DoctorWithdrawService doctorWithdrawService;

    /**
     * 提现列表
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/doctor/withdraw/list")
    public JsonResult getPageList(@RequestBody DoctorWithdrawListParam param){
        WxAppSessionUser user=wxAppAuth.user();
        param.setDoctorId(user.getDoctorId());
        PageResult<DoctorWithdrawVo> result= doctorWithdrawService.getPageList(param);
        return success(result);
    }

    /**
     * 发起提现申请
     * @param param
     * @return
     */
    @RedisLock(prefix = JedisKeyConstant.NotResubmit.DOCTOR_WITHDRAW_APPLY, noResubmit = true)
    @PostMapping("/api/wxapp/doctor/withdraw/apply")
    public JsonResult apply(@RequestBody DoctorWithdrawParam param){
        WxAppSessionUser user=wxAppAuth.user();
        param.setDoctorId(user.getDoctorId());
        try {
            param.setClientIp(RequestUtil.getIp(request));
            doctorWithdrawService.addDoctorWithdraw(param);
        } catch (MpException e) {
            return fail(e.getErrorCode(),e.getCodeParam());
        }
        return success();
    }
}
