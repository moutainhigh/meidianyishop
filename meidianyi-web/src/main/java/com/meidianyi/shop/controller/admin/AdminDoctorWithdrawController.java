package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.rebate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@RestController
public class AdminDoctorWithdrawController extends AdminBaseController{

    /**
     * 提现列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctor/withdraw/list")
    public JsonResult getPageList(@RequestBody DoctorWithdrawListParam param){
        PageResult<DoctorWithdrawVo> result=shop().doctorWithdrawService.getPageList(param);
        return success(result);
    }

    /**
     * 提现详情
     * @param id
     * @return
     */
    @GetMapping("/api/admin/doctor/withdraw/detail")
    public JsonResult getDetail(Integer id){
        DoctorWithdrawDetailVo detail=shop().doctorWithdrawService.getWithdrawDetail(id);
        return success(detail);
    }

    /**
     * 提现审核
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctor/withdraw/audit")
    public JsonResult audit(@RequestBody DoctorWithdrawUpdateParam param){
        try {
            param.setClientIp(RequestUtil.getIp(request));
            shop().doctorWithdrawService.audit(param);
        } catch (MpException e) {
            return fail(e.getErrorCode(),e.getCodeParam());
        }
        return success();
    }

    /**
     * 添加提现备注
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctor/withdraw/desc/add")
    public JsonResult doctorWithdrawDesc(@RequestBody DoctorWithdrawDescParam param){
        shop().doctorWithdrawService.addDoctorWithdrawDesc(param);
        return success();
    }
}
