package com.meidianyi.shop.service.shop.rebate;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.shop.rebate.InquiryOrderRebateDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateListParam;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateReportVo;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateVo;
import com.meidianyi.shop.service.pojo.shop.rebate.RebateReportConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Service
public class InquiryOrderRebateService extends ShopBaseService {

    @Autowired
    private InquiryOrderRebateDao inquiryOrderRebateDao;

    /**
     * 分页查询列表
     * @param param
     * @return
     */
    public PageResult<InquiryOrderRebateVo> getPageList(InquiryOrderRebateListParam param){
        PageResult<InquiryOrderRebateVo> result=inquiryOrderRebateDao.getPageList(param);
        return result;
    }

    /**
     * 报表导出
     * @param param
     * @param lang
     * @return
     */
    public Workbook listExport(InquiryOrderRebateListParam param,String lang){
        List<InquiryOrderRebateReportVo> list=inquiryOrderRebateDao.getList(param);
        list.stream().forEach(vo -> {
            if(InquiryOrderRebateConstant.TO_REBATE.equals(vo.getStatus())){
                vo.setStatusName(RebateReportConstant.WAIT_REBATE);
            }else if(InquiryOrderRebateConstant.REBATED.equals(vo.getStatus())){
                vo.setStatusName(RebateReportConstant.REBATED);
            }else if(InquiryOrderRebateConstant.REBATE_FAIL.equals(vo.getStatus())){
                vo.setStatusName(RebateReportConstant.REBATE_FAIL);
            }
        });
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(list,InquiryOrderRebateReportVo.class);
        return workbook;
    }


    public BigDecimal getRealRebateByDoctorDate(Integer doctorId, Timestamp startTime, Timestamp endTime) {
        return inquiryOrderRebateDao.getRealRebateByDoctorDate(doctorId,startTime,endTime);
    }
}
