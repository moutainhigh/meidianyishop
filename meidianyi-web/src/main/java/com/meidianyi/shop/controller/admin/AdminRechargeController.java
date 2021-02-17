package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.recharge.RechargeParam;
import com.meidianyi.shop.service.pojo.shop.sms.recharge.SmsAccountRechargeListVo;
import com.meidianyi.shop.service.pojo.shop.sms.recharge.SmsRechargeRecordVo;
import com.meidianyi.shop.service.shop.recharge.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-27 17:33
 **/

@RestController
@RequestMapping("/api/admin/recharge")
public class AdminRechargeController extends AdminBaseController{

    @Autowired
    private RechargeService rechargeService;

    @RequestMapping("/list")
    public JsonResult rechargeList(@RequestBody RechargeParam rechargeParam){
        PageResult<SmsRechargeRecordVo> rechargeList = rechargeService.getRechargeList(rechargeParam);
        return super.success(rechargeList);
    }

}
