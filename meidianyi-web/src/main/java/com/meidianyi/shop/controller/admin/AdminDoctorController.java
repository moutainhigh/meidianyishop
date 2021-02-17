package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentListVo;
import com.meidianyi.shop.service.pojo.shop.doctor.*;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit.DoctorAuditedPrescriptionParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionListParam;
import com.meidianyi.shop.service.shop.doctor.DoctorStatisticService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjie
 */
@RestController
public class AdminDoctorController extends AdminBaseController {
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private DoctorStatisticService doctorStatisticService;
    /**
     * 医师列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctors/list")
    public JsonResult doctorList(@RequestBody DoctorListParam param) {
        PageResult<DoctorOneParam> doctorList = shop().doctorService.getDoctorList(param);
        return this.success(doctorList);
    }

    /**
     *  医生新增
     * @param param {@link DoctorOneParam}
     */
    @PostMapping("/api/admin/doctors/add")
    public JsonResult insert(@RequestBody DoctorOneParam param) {
        if (param.getName()==null) {
            return fail(JsonResultCode.DOCTOR_NAME_IS_NULL);
        }

        boolean isExist = shop().doctorService.isCodeExist(null,param.getHospitalCode());
        if (isExist) {
            return fail(JsonResultCode.DOCTOR_CODE_IS_EXIST);
        }

        shop().doctorService.insertDoctor(param);

        return success();
    }

    /**
     *  医生新增
     * @param param {@link DoctorOneParam}
     */
    @PostMapping("/api/admin/doctors/update")
    public JsonResult update(@RequestBody DoctorOneParam param) {
        if (param.getId()==null) {
            return fail(JsonResultCode.DOCTOR_ID_IS_NULL);
        }

        boolean isExist = shop().doctorService.isCodeExist(param.getId(),param.getHospitalCode());
        if (isExist) {
            return fail(JsonResultCode.DOCTOR_CODE_IS_EXIST);
        }
        try {
            shop().doctorService.updateDoctor(param);
        } catch (MpException e) {
            e.printStackTrace();
        }

        return success();
    }

    /**
     * 根据id获取医师信息
     * @param doctorId 医师id
     */
    @GetMapping("/api/admin/doctors/info/{doctorId}")
    public JsonResult getDoctor(@PathVariable Integer doctorId) {
        if (doctorId == null) {
            return fail(JsonResultCode.DOCTOR_ID_IS_NULL);
        }
        DoctorOneParam doctorInfo = null;
        try {
            doctorInfo = shop().doctorService.getOneInfo(doctorId);
        } catch (MpException e) {
            e.printStackTrace();
        }
        if (doctorInfo.getTitleId() == 0) {
            doctorInfo.setTitleId(null);
        }
        return success(doctorInfo);
    }

    /**
     * 医生停用启用
     *
     * @param param {@link DoctorOneParam}
     */
    @PostMapping("/api/admin/doctors/enable")
    public JsonResult enableDoctor(@RequestBody DoctorOneParam param) {
        if (param.getId() == null) {
            return fail(JsonResultCode.DOCTOR_ID_IS_NULL);
        }
        try {
            shop().doctorService.enableDoctor(param);
        } catch (MpException e) {
            e.printStackTrace();
        }
        return success();
    }

    /**
     *  医师拉取
     */
    @PostMapping("/api/admin/doctors/fetch")
    public JsonResult fetchDoctors() {
        try {
            return shop().doctorService.fetchExternalDoctor();
        } catch (MpException e) {
            e.printStackTrace();
        }
        return fail();
    }

    /**
     * 医师下拉列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctors/select/list")
    public JsonResult doctorSelectList(@RequestBody DoctorListParam param) {
        List<DoctorOneParam> doctorList = shop().doctorService.getSelectDoctorList(param);
        return this.success(doctorList);
    }

    /**
     * 医师解绑
     * @param doctorUnbundlingParam doctorId
     * @return JsonResult
     */
    @PostMapping("/api/admin/doctor/unbundling")
    public JsonResult doctorUnbundling (@RequestBody DoctorUnbundlingParam doctorUnbundlingParam) {
        shop().doctorService.doctorUnbundling(doctorUnbundlingParam);
        return success();
    }

    /**
     * 医师停止接诊
     * @param doctorUnbundlingParam 医师id
     * @return JsonResult
     */
    @PostMapping("/api/admin/doctor/consultation")
    public JsonResult doctorCanConsultation(@RequestBody DoctorUnbundlingParam doctorUnbundlingParam) {
        shop().doctorService.doctorCanConsultation(doctorUnbundlingParam);
        return success();
    }

    /**
     *
     * @return
     */
    public JsonResult listDoctorComment(@RequestBody @Validated DoctorAuditedPrescriptionParam param){
        return success(prescriptionService.auditedPrescriptionList(param));
    }

    /**
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/test")
    public JsonResult testDoctor(@RequestBody @Validated DoctorAuditedPrescriptionParam param){
        shop().doctorService.testTemplateMessage();
        return success();
    }

    /**
     * 查看医师详情
     * @param doctorUnbundlingParam doctorId
     * @return JsonResult
     */
    @PostMapping("/api/admin/doctors/details/show")
    public JsonResult adminDoctorDetails(@RequestBody DoctorUnbundlingParam doctorUnbundlingParam) {
        // 医师科室
        List<DepartmentListVo> departmentListVos =
            shop().doctorService.selectDepartmentsByDoctorId(doctorUnbundlingParam.getDoctorId());
        List<String> departmentNames = departmentListVos.stream().map(DepartmentListVo::getName).collect(Collectors.toList());
        DoctorOneParam oneInfo = null;
        try {
            oneInfo = shop().doctorService.getOneInfo(doctorUnbundlingParam.getDoctorId());
        } catch (MpException e) {
            e.printStackTrace();
        }
        //添加医师职称
        String title = shop().doctorService.selectDoctorTitle(oneInfo);
        oneInfo.setTitleName(title);
        oneInfo.setDepartmentNames(departmentNames);
        return success(oneInfo);
    }

    /**
     * 医师统计列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctors/summary/list")
    public JsonResult doctorSummaryList(@RequestBody DoctorStatisticParam param) {
        PageResult<DoctorStatisticListVo> doctorSummaryList = shop().doctorStatisticService.getDoctorSummaryList(param);
        return this.success(doctorSummaryList);
    }

    /**
     * 医师业绩统计列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctors/attendance/summary/list")
    public JsonResult doctorAttendanceSummaryList(@RequestBody DoctorAttendanceListParam param) {
        PageResult<DoctorAttendanceOneParam> doctorAttendanceSummaryList = shop().doctorLoginLogService.getDoctorAttendancePage(param);
        return this.success(doctorAttendanceSummaryList);
    }

    /**
     * 医师业绩统计分布
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctors/attendance/summary/divide")
    public JsonResult doctorAttendanceSummaryDivide(@RequestBody DoctorAttendanceListParam param) {
        DoctorAttendanceDivideVo doctorAttendanceSummaryDivide = shop().doctorLoginLogService.getDoctorAttendanceDivide(param.getType());
        return this.success(doctorAttendanceSummaryDivide);
    }

    /**
     * 医师业绩统计分布
     * @return
     */
    @PostMapping("/api/admin/doctors/summary/test")
    public JsonResult doctorSummaryTest(@RequestBody DoctorStatisticTestParam param) {
        doctorStatisticService.doctorStatistics(param);
        return this.success();
    }

    /**
     * 查询医师关联咨询记录
     * @return
     */
    @PostMapping("/api/admin/doctor/query/inquiry")
    public JsonResult doctorQueryInquiry(@Validated @RequestBody DoctorQueryInquiryParam doctorQueryInquiryParam) {
        return success(shop().doctorService.getDoctorQueryInquiry(doctorQueryInquiryParam));
    }

    /**
     * 查询医师相关处方记录
     * @param doctorQueryPrescriptionParam 查询处方入参
     * @return JsonResult
     */
    @PostMapping("/api/admin/doctor/query/prescription")
    public JsonResult doctorQueryPrescription(@Validated @RequestBody DoctorQueryPrescriptionParam doctorQueryPrescriptionParam) {
        PrescriptionListParam prescriptionListParam = new PrescriptionListParam();
        prescriptionListParam.setDoctorCode(doctorQueryPrescriptionParam.getDoctorCode());
        prescriptionListParam.setDepartmentName(doctorQueryPrescriptionParam.getDepartmentName());
        prescriptionListParam.setAuditType(doctorQueryPrescriptionParam.getAuditType());
        prescriptionListParam.setDiagnoseStartTime(doctorQueryPrescriptionParam.getStartTime());
        prescriptionListParam.setDiagnoseEndTime(doctorQueryPrescriptionParam.getEndTime());
        return success(shop().prescriptionService.listPageResult(prescriptionListParam));
    }

    /**
     * 查询医师相关患者记录
     * @param doctorQueryPatientParam 查询患者入参
     * @return JsonResult
     */
    @PostMapping("/api/admin/doctor/query/patient")
    public JsonResult doctorQueryPatient(@Validated @RequestBody DoctorQueryPatientParam doctorQueryPatientParam) {
        return success(shop().doctorService.getDoctorQueryPatient(doctorQueryPatientParam));
    }

    /**
     * 查询医师业绩详情
     * @return
     */
    @PostMapping("/api/admin/doctor/query/performance")
    public JsonResult getDoctorPerformanceDetail(@RequestBody DoctorDetailPerformanceParam param){
        return success(shop().doctorService.getDoctorPerformanceDetail(param));
    }
}
