package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.rebate.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@RestController
@RequestMapping("/api/admin/")
public class AdminDoctorRebateController extends AdminBaseController{

    /**
     * 处方返利列表查询
     * @param param
     * @return
     */
    @PostMapping("/doctor/rebate/prescription/list")
    public JsonResult prescriptionRebateList(@RequestBody PrescriptionRebateListParam param){
        PageResult<PrescriptionRebateVo> result=shop().prescriptionRebateService.getPageList(param);
        return success(result);
    }

    /**
     * 咨询返利列表查询
     * @param param
     * @return
     */
    @PostMapping("/doctor/rebate/inquiryOrder/list")
    public JsonResult inquiryOrderRebateList(@RequestBody InquiryOrderRebateListParam param){
        PageResult<InquiryOrderRebateVo> result=shop().inquiryOrderRebateService.getPageList(param);
        return success(result);
    }

    /**
     * 处方返利报表导出
     * @param param
     * @param response
     */
    @PostMapping("/doctor/rebate/prescription/export")
    public void prescriptionRebateReport(@RequestBody PrescriptionRebateListParam param, HttpServletResponse response){
        Workbook workbook=shop().prescriptionRebateService.listExport(param,getLang());
        String fileName = PrescriptionRebateReportVo.EXPORT_FILE_NAME+ DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }

    /**
     * 咨询返利报表导出
     * @param param
     * @param response
     */
    @PostMapping("/doctor/rebate/inquiryOrder/export")
    public void inquiryOrderRebateReport(@RequestBody InquiryOrderRebateListParam param, HttpServletResponse response){
        Workbook workbook=shop().inquiryOrderRebateService.listExport(param,getLang());
        String fileName =InquiryOrderRebateReportVo.EXPORT_FILE_NAME+DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }
}
