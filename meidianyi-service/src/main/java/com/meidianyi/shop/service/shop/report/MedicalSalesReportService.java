package com.meidianyi.shop.service.shop.report;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.dao.shop.order.OrderInfoDao;
import com.meidianyi.shop.dao.shop.order.ReturnOrderDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.report.MedicalOrderReportVo;
import com.meidianyi.shop.service.pojo.shop.report.MedicalSalesReportParam;
import com.meidianyi.shop.service.pojo.shop.report.MedicalSalesReportVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 药品销售报表
 * @author 孔德成
 * @date 2020/7/31 15:12
 */
@Service
public class MedicalSalesReportService extends ShopBaseService {

    @Autowired
    private OrderInfoDao orderInfoDao;
    @Autowired
    private ReturnOrderDao returnOrderDao;


    /**
     * 药品销售报表
     * @param param
     * @return
     */
     public MedicalSalesReportVo medicalSalesReport(MedicalSalesReportParam param){
         buildSalesReportDate(param);
         Timestamp startDate = param.getStartTime();
         Timestamp endDate = param.getEndTime();
         Map<Timestamp, Timestamp> map = param.getMap();
         logger().info("开始时间{},结束时间{}",startDate,endDate);
         Page page = getPage(param, param.getTotalRows());
         Map<Date, MedicalOrderReportVo> orderMap = orderInfoDao.orderSalesReport(startDate, endDate);
         Map<Date, MedicalOrderReportVo> returnOrderMap = returnOrderDao.medicalOrderSalesReport(startDate, endDate);
         List<MedicalOrderReportVo> list  =new ArrayList<>();
         map.forEach((startDate2,endDate2)->{
             MedicalOrderReportVo report = getMedicalOrderReportVo(startDate2,endDate2, orderMap, returnOrderMap);
             list.add(report);
         });
         MedicalSalesReportVo vo =new MedicalSalesReportVo();
         vo.setDataList(list);
         vo.setPage(page);
         return vo;
     }

    /**
     * 计算报表时间(分页优化)
     * @param param
     */
    public void buildSalesReportDate(MedicalSalesReportParam param) {
        DateTime startDate =DateUtil.beginOfDay(param.getStartTime());
        param.setStartTime(startDate.toTimestamp());
        DateTime endDate=DateUtil.endOfDay(param.getEndTime());
        param.setEndTime(endDate.toTimestamp());
        Map<Timestamp,Timestamp> map =new LinkedHashMap<>();
        switch (param.getAnalyzeType()){
            case MedicalSalesReportParam.ANALYZE_TYPE_DAY:
                buildDaySalesReportDate(param, endDate, startDate, map);
                break;
            case MedicalSalesReportParam.ANALYZE_TYPE_WEEK:
                buildWeekSalesReportDate(param, endDate, startDate, map);
                break;
            case MedicalSalesReportParam.ANALYZE_TYPE_MONTH:
                buildMonthSalesReportDate(param, endDate, startDate, map);
                break;
            case MedicalSalesReportParam.ANALYZE_TYPE_QUARTER:
                buildQuarterSalesReportDate(param, endDate, startDate, map);
                break;
            case MedicalSalesReportParam.ANALYZE_TYPE_YEAR:
                buildYearSalesReportDate(param, endDate, startDate, map);
                break;
            default:
        }

    }

    private void buildYearSalesReportDate(MedicalSalesReportParam param, DateTime endDate, DateTime startDate, Map<Timestamp, Timestamp> map) {
        DateTime startDate2;
        DateTime endDate2;
        long totalRows;
        DateTime endDate3;
        startDate2 = DateUtil.beginOfYear(startDate);
        endDate2 = DateUtil.beginOfYear(endDate);
        totalRows = DateUtil.betweenYear(startDate2, endDate2,true)+1;
        if (param.getCurrentPage()>1){
            startDate2 = DateUtil.offset(startDate2, DateField.YEAR, ((param.getCurrentPage() - 1) * param.getPageRows()));
        }else {
            startDate2 =startDate;
        }
        logger().info("开始时间{},结束时间{}",startDate2,endDate2);
        endDate2 = DateUtil.offset(startDate2, DateField.YEAR, (param.getPageRows()-1));
        endDate2 = DateUtil.endOfYear(endDate2);
        if (endDate2.after(endDate)) {
            endDate2 = endDate;
        }
        param.setStartTime(startDate2.toTimestamp());
        while (endDate2.compareTo(startDate2)>=0){
            endDate3 = DateUtil.endOfYear(startDate2);
            if (endDate3.after(endDate2)){
                endDate3 =endDate2;
            }
            map.put(startDate2.toTimestamp(), endDate3.toTimestamp());
            logger().info("每条数据-开始时间{},结束时间{}",startDate2,endDate3);
            startDate2 = DateUtil.offset(DateUtil.beginOfYear(startDate2),DateField.YEAR, 1);
        }
        param.setEndTime(endDate2.toTimestamp());
        param.setMap(map);
        param.setTotalRows(totalRows);
    }

    private void buildQuarterSalesReportDate(MedicalSalesReportParam param, DateTime endDate, DateTime startDate, Map<Timestamp, Timestamp> map) {
        DateTime startDate2;
        DateTime endDate2;
        long totalRows;
        DateTime endDate3;
        startDate2 = DateUtil.beginOfQuarter(startDate);
        endDate2 = DateUtil.beginOfQuarter(endDate);
        totalRows = (DateUtil.betweenMonth(startDate2, endDate2,true))/3+1;
        if (param.getCurrentPage()>1){
            startDate2 = DateUtil.offset(startDate2, DateField.MONTH, ((param.getCurrentPage() - 1) * param.getPageRows())*3);
        }else {
            startDate2 =startDate;
        }
        endDate2 = DateUtil.offset(startDate2, DateField.MONTH, (param.getPageRows()-1)*3);
        endDate2 = DateUtil.endOfQuarter(endDate2);
        if (endDate2.after(endDate)) {
            endDate2 = endDate;
        }
        logger().info("开始时间{},结束时间{}",startDate2,endDate2);
        param.setStartTime(startDate2.toTimestamp());
        while (endDate2.compareTo(startDate2)>=0){
            endDate3 = DateUtil.endOfQuarter(startDate2);
            if (endDate3.after(endDate2)){
                endDate3 =endDate2;
            }
            map.put(startDate2.toTimestamp(), endDate3.toTimestamp());
            logger().info("每条数据-开始时间{},结束时间{}",startDate2,endDate3);
            startDate2 = DateUtil.offset(DateUtil.beginOfQuarter(startDate2),DateField.MONTH, 3);
        }
        param.setEndTime(endDate2.toTimestamp());
        param.setMap(map);
        param.setTotalRows(totalRows);
    }

    private void buildMonthSalesReportDate(MedicalSalesReportParam param, DateTime endDate, DateTime startDate, Map<Timestamp, Timestamp> map) {
        DateTime startDate2;
        DateTime endDate2;
        long totalRows;
        DateTime endDate3;
        startDate2 = DateUtil.beginOfMonth(startDate);
        endDate2 = DateUtil.beginOfMonth(endDate);
        totalRows = DateUtil.betweenMonth(startDate2, endDate2,true)+1;
        if (param.getCurrentPage()>1){
            startDate2 = DateUtil.offset(startDate2, DateField.MONTH, ((param.getCurrentPage() - 1) * param.getPageRows()));
        }else {
            startDate2 =startDate;
        }
        endDate2 = DateUtil.offset(startDate2, DateField.MONTH, (param.getPageRows()-1));
        endDate2 = DateUtil.endOfMonth(endDate2);
        if (endDate2.after(endDate)) {
            endDate2 = endDate;
        }
        logger().info("开始时间{},结束时间{}",startDate2,endDate2);

        param.setStartTime(startDate2.toTimestamp());
        while (endDate2.compareTo(startDate2)>=0){
            endDate3 = DateUtil.endOfMonth(startDate2);
            if (endDate3.after(endDate2)){
                endDate3 =endDate2;
            }
            map.put(startDate2.toTimestamp(), endDate3.toTimestamp());
            logger().info("每条数据-开始时间{},结束时间{}",startDate2,endDate3);
            startDate2 = DateUtil.offset(DateUtil.beginOfMonth(startDate2),DateField.MONTH, 1);
        }
        param.setEndTime(endDate2.toTimestamp());
        param.setMap(map);
        param.setTotalRows(totalRows);
    }

    private void buildWeekSalesReportDate(MedicalSalesReportParam param, DateTime endDate, DateTime startDate, Map<Timestamp, Timestamp> map) {
        DateTime startDate2;
        DateTime endDate2;
        long totalRows;
        DateTime endDate3;
        startDate2 = DateUtil.beginOfWeek(startDate);
        endDate2 = DateUtil.beginOfWeek(endDate);
        totalRows = DateUtil.betweenWeek(startDate2, endDate2,true)+1;
        if (param.getCurrentPage()>1){
            startDate2 = DateUtil.offset(startDate2, DateField.WEEK_OF_YEAR,(param.getCurrentPage() - 1) * param.getPageRows());
        }else {
            startDate2 =startDate;
        }
        endDate2 = DateUtil.offset(startDate2,DateField.WEEK_OF_YEAR,  (param.getPageRows()-1));
        endDate2 = DateUtil.endOfWeek(endDate2);
        if (endDate2.after(endDate)) {
            endDate2 = endDate;
        }
        logger().info("开始时间{},结束时间{}",startDate2,endDate2);
        param.setStartTime(startDate2.toTimestamp());
        while (endDate2.compareTo(startDate2)>=0){
            endDate3 = DateUtil.endOfWeek(startDate2);
            if (endDate3.after(endDate2)){
                endDate3 =endDate2;
            }
            map.put(startDate2.toTimestamp(), endDate3.toTimestamp());
            logger().info("每条数据-开始时间{},结束时间{}",startDate2,endDate3);
            startDate2 = DateUtil.offset(DateUtil.beginOfWeek(startDate2),DateField.WEEK_OF_YEAR, 1);
        }
        param.setEndTime(endDate2.toTimestamp());
        param.setMap(map);
        param.setTotalRows(totalRows);
    }

    private void buildDaySalesReportDate(MedicalSalesReportParam param, DateTime endDate, DateTime startDate, Map<Timestamp, Timestamp> map) {
        long totalRows;
        DateTime startDate2;
        DateTime endDate2;
        DateTime startDate3;
        totalRows = DateUtil.between(startDate, endDate, DateUnit.DAY,true)+1;
        startDate2 = DateUtil.offset(startDate, DateField.DAY_OF_YEAR, (param.getCurrentPage() - 1) * param.getPageRows());
        endDate2 = DateUtil.offset(startDate2,  DateField.DAY_OF_YEAR,param.getPageRows()-1);
        if (endDate2.after(endDate)) {
            endDate2 = DateTime.of(param.getEndTime());
        }
        param.setStartTime(startDate2.toTimestamp());
        while (endDate2.compareTo(startDate2)>=0){
            startDate3 = DateUtil.endOfDay(startDate2);
            map.put(startDate2.toTimestamp(), startDate3.toTimestamp());
            startDate2 = DateUtil.offset(startDate2,DateField.DAY_OF_YEAR, 1);
        }
        param.setEndTime(endDate2.toTimestamp());
        param.setMap(map);
        param.setTotalRows(totalRows);
    }

    public Page getPage(MedicalSalesReportParam param, long totalRows) {
        Page page =new Page();
        page.setCurrentPage(param.getCurrentPage());
        page.setPageRows(param.getPageRows());
        page.setTotalRows((int) totalRows);
        page.setFirstPage(1);
        page.setLastPage(param.getCurrentPage()-1);
        page.setNextPage(page.getCurrentPage()+1);
        if (totalRows%param.getPageRows()>0){
            page.setPageCount((int) (totalRows/param.getPageRows()+1));
        }else {
            page.setPageCount((int) (totalRows/param.getPageRows()));
        }
        return page;
    }

    /**
     * 导出销售报表数据
     * @param param
     * @param lang
     * @return
     */
    public Workbook medicalSalesReportExport(MedicalSalesReportParam param, String lang) {
        buildSalesReportDate(param);
        param.setPageRows(Integer.MAX_VALUE);
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        Map<Timestamp, Timestamp> map = param.getMap();
        logger().info("开始时间{},结束时间{}",startDate,endDate);

        Page page = getPage(param, param.getTotalRows());
        Map<Date, MedicalOrderReportVo> orderMap = orderInfoDao.orderSalesReport(startDate, endDate);
        Map<Date, MedicalOrderReportVo> returnOrderMap = returnOrderDao.medicalOrderSalesReport(startDate, endDate);
        List<MedicalOrderReportVo> list  =new ArrayList<>();
        map.forEach((startDate2,endDate2)->{
            MedicalOrderReportVo report = getMedicalOrderReportVo(startDate2,endDate2, orderMap, returnOrderMap);
            list.add(report);
        });


        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(list, MedicalOrderReportVo.class);
        return workbook;
    }

    public MedicalOrderReportVo getMedicalOrderReportVo(Timestamp startDate,Timestamp endDate,  Map<Date, MedicalOrderReportVo> orderMap, Map<Date, MedicalOrderReportVo> returnOrderMap) {
        MedicalOrderReportVo report = new MedicalOrderReportVo();
        report.setTime(DateUtil.formatDate(startDate)+"~"+DateUtil.formatDate(endDate));
        BigDecimal orderAmount = BigDecimal.ZERO;
        int orderNumber=0;
        BigDecimal moneyPaid=BigDecimal.ZERO;
        BigDecimal useAccount =BigDecimal.ZERO;
        BigDecimal shippingFee =BigDecimal.ZERO;
        BigDecimal returnAmount =BigDecimal.ZERO;
        Integer prescriptionNum =0;
        BigDecimal prescriptionNumAmount =BigDecimal.ZERO;
        int returnNumber =0;
        while (endDate.compareTo(startDate) >= 0) {
            Date date = DateUtil.date(startDate).toSqlDate();
            MedicalOrderReportVo orderReport = orderMap.get(date);
            MedicalOrderReportVo returnReport = returnOrderMap.get(date);
            if (orderReport == null) {
                orderReport = new MedicalOrderReportVo();
            }
            if (returnReport == null) {
                returnReport = new MedicalOrderReportVo();
            }
            orderAmount = orderAmount.add(Optional.ofNullable(orderReport.getOrderAmount()).orElse(BigDecimal.ZERO));
            orderNumber =orderNumber+ Optional.ofNullable(orderReport.getOrderNumber()).orElse(0);
            moneyPaid = moneyPaid.add(Optional.ofNullable(orderReport.getMoneyPaid()).orElse(BigDecimal.ZERO));
            useAccount =useAccount.add(Optional.ofNullable(orderReport.getUseAccount()).orElse(BigDecimal.ZERO));
            shippingFee = shippingFee.add(Optional.ofNullable(orderReport.getShippingFee()).orElse(BigDecimal.ZERO));
            returnAmount = returnAmount.add(Optional.ofNullable(returnReport.getReturnAmount()).orElse(BigDecimal.ZERO));
            returnNumber =returnNumber+ Optional.ofNullable(returnReport.getReturnNumber()).orElse(0);
            startDate = DateUtil.offset(startDate,DateField.DAY_OF_YEAR,1).toTimestamp();
            prescriptionNum= prescriptionNum+ Optional.ofNullable(orderReport.getPrescriptionOrderNum()).orElse(0);
            prescriptionNumAmount = prescriptionNumAmount.add(Optional.ofNullable(orderReport.getPrescriptionOrderAmount()).orElse(BigDecimal.ZERO));
        }
        //笔单价 =净销售额/订单数量
        BigDecimal orderAvga =BigDecimal.ZERO;
        if (orderNumber>0){
            orderAvga = orderAmount.divide(BigDecimal.valueOf(orderNumber),2,BigDecimal.ROUND_HALF_UP);
        }
        report.setOrderAmount(orderAmount);
        report.setOrderNumber(orderNumber);
        report.setShippingFee(shippingFee);
        report.setMoneyPaid(moneyPaid);
        report.setUseAccount(useAccount);
        report.setNetSales(orderAmount.subtract(returnAmount));
        report.setOrderAvg(orderAvga.setScale(2,BigDecimal.ROUND_HALF_UP));
        report.setReturnAmount(returnAmount);
        report.setReturnNumber(returnNumber);
        report.setPrescriptionOrderNum(prescriptionNum);
        report.setPrescriptionOrderAmount(prescriptionNumAmount);
        return report;
    }
}
