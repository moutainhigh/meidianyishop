package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentIdNameVo;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentListParam;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.*;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleOneParam;
import com.meidianyi.shop.service.pojo.shop.user.user.UserDoctorParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chenjie
 */
@RestController
public class WxAppDoctorConsultationController extends WxAppBaseController {
    /**
     * 	获取医师科室列表
     */
    @PostMapping("/api/wxapp/department/list")
    public JsonResult getDoctorDepartmentList(@RequestBody DepartmentListParam departmentListParam) {
        List<DepartmentOneParam> departmentList = shop().departmentService.listDepartmentsByOptions(departmentListParam);
        return success(departmentList);
    }

    /**
     * 	获取医师列表(页面)
     */
    @PostMapping("/api/wxapp/doctor/list")
    public JsonResult getDoctorList(@RequestBody DoctorConsultationParam doctorParam) {
        PageResult<DoctorConsultationOneParam> doctorList = shop().doctorService.listDoctorForConsultation(doctorParam);
        List<DepartmentOneParam> departmentList = shop().departmentService.listDepartmentsSelect();
        List<TitleOneParam> titleList = shop().titleService.listTitlesSelect();
        DoctorConsultationVo data = new DoctorConsultationVo();
        data.setDepartmentList(departmentList);
        data.setTitleList(titleList);
        data.setDoctorList(doctorList);
        return success(data);
    }

    /**
     * 	获取推荐医师列表(页面)
     */
    @PostMapping("/api/wxapp/recommend/doctor/list")
    public JsonResult getDoctorList(@RequestBody UserPatientParam doctorParam) {
        doctorParam.setPatientId(shop().patientService.defaultPatientId(doctorParam.getUserId()));
        List<DepartmentOneParam> recommendDepartment = shop().departmentService.listRecommendDepartment();
        List<DoctorConsultationOneParam> doctorList = shop().doctorService.listRecommendDoctorForConsultation(doctorParam);
        DoctorRecommendVo data = new DoctorRecommendVo();
        data.setDoctorList(doctorList);
        data.setRecommendDepartment(recommendDepartment);
        return success(data);
    }

    /**
     * 	添加/取消关注
     */
    @PostMapping("/api/wxapp/user/doctor/attention/update")
    public JsonResult addAttention(@RequestBody UserDoctorParam param) {
        if (DoctorConstant.ATTENTION.equals(param.getStatus())){
            shop().doctorService.insertUserDoctor(param);
        } else {
            shop().doctorService.deleteUserDoctor(param);
        }
        return success();
    }

    /**
     * 	更新医师上下班
     */
    @PostMapping("/api/wxapp/doctor/on/duty/update")
    public JsonResult updateOnDuty(@RequestBody DoctorDutyParam param) {
        shop().doctorService.updateOnDuty(param);
        return success();
    }

    /**
     * 	获取咨询医师
     */
    @PostMapping("/api/wxapp/consultation/doctor/info")
    public JsonResult getConsultationDoctorInfo(@RequestBody UserDoctorParam param) {
        DoctorOneParam doctorInfo = null;
        try {
            doctorInfo = shop().doctorService.getWxDoctorInfo(param);
        } catch (MpException e) {
            e.printStackTrace();
        }
        InquiryOrderParam inquiryOrderParam = new InquiryOrderParam();
        FieldsUtil.assign(param,inquiryOrderParam);
        InquiryOrderDo inquiryOrderDo = shop().inquiryOrderService.getUndoneOrder(inquiryOrderParam);
        doctorInfo.setHasUndoneOrder(inquiryOrderDo!=null);
        return success(doctorInfo);
    }
}
