package com.meidianyi.shop.service.shop.summary.visit;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.db.shop.tables.records.MpDailyVisitRecord;
import com.meidianyi.shop.service.pojo.shop.summary.RefDateRecord;
import com.meidianyi.shop.service.pojo.shop.summary.visit.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.MpDailyVisit.MP_DAILY_VISIT;
import static com.meidianyi.shop.service.pojo.shop.summary.visit.VisitStatisticsParam.*;

/**
 * 折线图
 *
 * @author 郑保乐
 * @date 2019年7月11日
 */
@Service
public class AmountService extends BaseVisitService {
    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;
    /** 总数默认值 */
    private static final Double TOTAL_NUM = 0.0;
    /** 粒度 日/周/月 1/7/30 */
    private static final int GRADING_DAY = 1;
    private static final int GRADING_WEEK = 7;
    private static final int GRADING_MONTH = 30;
    private static final String GRADING_DAY_STRING = "日";
    private static final String GRADING_WEEK_STRING = "周";
    private static final String GRADING_MONTH_STRING = "月";
    public void addTestDailyVisit() {
        LocalDate dateToday = LocalDate.now();
        LocalDate i;
        int count = 100;
        for (int j = 0; j < count; j++) {
            i = dateToday.minusDays(1);
            String dateString = formatDate(i);
            db().insertInto(MP_DAILY_VISIT, MP_DAILY_VISIT.REF_DATE, MP_DAILY_VISIT.SESSION_CNT, MP_DAILY_VISIT.VISIT_PV,
                    MP_DAILY_VISIT.VISIT_UV, MP_DAILY_VISIT.VISIT_UV_NEW, MP_DAILY_VISIT.STAY_TIME_SESSION, MP_DAILY_VISIT.VISIT_DEPTH)
                    .values(dateString, 10, 20, 30, 40, 128.23, 256.29).execute();
        }
    }
    /**
     *得到之前的某一天(字符串类型)
     *@param days N天前
     *@return preDay(String)
     */
    public String getDate(Integer days) {
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        Calendar c = Calendar.getInstance();
        //计算指定日期
        c.add(Calendar.DATE, - days);
        Date time = c.getTime();
        //返回格式化后的String日期
        return sdf.format(time);
    }

    /**
     * 计算平均值（保留两位小数）
     * @param totalNum 总数
     * @param num 个数
     * @return 平均值
     */
    public Double getAverageNum(Double totalNum,Integer num){
        if (totalNum==null||num==null||num==0){
            return TOTAL_NUM;
        }else {
            Double averageNum = totalNum/num;
            BigDecimal tempAverageNum = BigDecimal.valueOf(averageNum);
            averageNum = tempAverageNum.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            return averageNum;
        }
    }
    /**
     * 访问统计折线图
     */
    public VisitStatisticsVo getVisitStatistics(VisitStatisticsParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartDate(getDate(param.getType()));
            param.setEndDate(getDate(NumberUtils.INTEGER_ZERO));
        }
        Integer action = param.getAction();
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        Integer grading = param.getGrading();
        Result<MpDailyVisitRecord> result = getSessionCounts(startDate, endDate);
        List<RefDateRecord<Double>> units;
        switch (action) {
            case SESSION_COUNT:
                units = sessionUnits(result);
                break;
            case PV:
                units = pvUnits(result);
                break;
            case UV:
                units = uvUnits(result);
                break;
            case UV_NEW:
                units = uvNewUnits(result);
                break;
            case STAY_TIME_UV:
                units = uvStayUnits(result);
                break;
            case STAY_TIME_SESSION:
                units = sessionStayUnits(result);
                break;
            case VISIT_DEPTH:
                units = visitDepthUnits(result);
                break;
            default:
                throw new IllegalStateException("Unexpected action: " + action);
        }
        VisitStatisticsVo handleData =  getStatisticsVo(units, grading);
        Double totalNum = handleData.getList().stream().reduce(Double::sum).orElse(TOTAL_NUM);
        BigDecimal tempTotalNum = BigDecimal.valueOf(totalNum);
        totalNum = tempTotalNum.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        Double averageNum = getAverageNum(totalNum,handleData.getDate().size());
        VisitStatisticsVo vo = new VisitStatisticsVo();
        vo.setDate(handleData.getDate());
        vo.setList(handleData.getList());
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        vo.setTotalNum(totalNum);
        vo.setAverageNum(averageNum);
        return vo;
    }

    /**
     * 打开次数
     */
    private List<RefDateRecord<Double>> sessionUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = new VisitStatisticsUnit();
            unit.setRefDate(r.getRefDate());
            unit.setValue(r.getSessionCnt().doubleValue());
            return unit;
        });
    }

    /**
     * 访问次数
     */
    private List<RefDateRecord<Double>> pvUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = visitStatisticsUnit(r);
            unit.setValue(r.getVisitPv().doubleValue());
            return unit;
        });
    }

    /**
     * 访问人数
     */
    private List<RefDateRecord<Double>> uvUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = visitStatisticsUnit(r);
            unit.setValue(r.getVisitUv().doubleValue());
            return unit;
        });
    }

    /**
     * 新用户人数
     */
    private List<RefDateRecord<Double>> uvNewUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = visitStatisticsUnit(r);
            unit.setValue(r.getVisitUvNew().doubleValue());
            return unit;
        });
    }

    /**
     * 人均停留时长
     */
    private List<RefDateRecord<Double>> uvStayUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = visitStatisticsUnit(r);
            unit.setValue(r.getStayTimeUv());
            return unit;
        });
    }

    /**
     * 次均停留时长
     */
    private List<RefDateRecord<Double>> sessionStayUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = visitStatisticsUnit(r);
            unit.setValue(r.getStayTimeSession());
            return unit;
        });
    }

    /**
     * 平均访问深度
     */
    private List<RefDateRecord<Double>> visitDepthUnits(Result<MpDailyVisitRecord> result) {
        return result.map(r -> {
            VisitStatisticsUnit unit = visitStatisticsUnit(r);
            unit.setValue(r.getVisitDepth());
            return unit;
        });
    }

    /**
     * 创建带日期的处理单元
     */
    private VisitStatisticsUnit visitStatisticsUnit(MpDailyVisitRecord record) {
        VisitStatisticsUnit unit = new VisitStatisticsUnit();
        unit.setRefDate(record.getRefDate());
        return unit;
    }

    /**
     * 按粒度分组
     *
     * @param visitUnits 日单元
     * @param grading    粒度
     */
    private VisitStatisticsVo getStatisticsVo(List<RefDateRecord<Double>> visitUnits, Integer grading) {
        List<RefDateRecord<Double>> groupedValue = getGroupedValue(visitUnits, grading);
        VisitStatisticsVo vo = new VisitStatisticsVo();
        List<String> dates = groupedValue.stream().map(RefDateRecord::getRefDate).collect(Collectors.toList());
        List<Double> values = groupedValue.stream().map(RefDateRecord::getValue).collect(Collectors.toList());
        Collections.reverse(dates);
        Collections.reverse(values);
        vo.setDate(dates);
        vo.setList(values);
        return vo;
    }

    /**
     * 统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    private Result<MpDailyVisitRecord> getSessionCounts(
            String startDate, String endDate) {
        return db().select(MP_DAILY_VISIT.REF_DATE, MP_DAILY_VISIT.SESSION_CNT, MP_DAILY_VISIT.VISIT_PV,
                MP_DAILY_VISIT.VISIT_UV, MP_DAILY_VISIT.VISIT_UV_NEW, MP_DAILY_VISIT.STAY_TIME_UV,
                MP_DAILY_VISIT.STAY_TIME_SESSION, MP_DAILY_VISIT.VISIT_DEPTH)
                .from(MP_DAILY_VISIT)
                .where(MP_DAILY_VISIT.REF_DATE.between(startDate).and(endDate)).fetch().into(MP_DAILY_VISIT);
    }

    /**
     * 日期格式化（20190711 形式）
     */
    private String formatDate(LocalDate date) {
        return date.toString().replaceAll("-", "");
    }

    /**
     * 统计数据表格导出
     * @param param 日期粒度 起止时间
     * @param lang 语言
     * @return 表格数据
     */
    public Workbook getVisitExportVo(VisitExportParam param,String lang){
        //得到统计数据的出参
        List<VisitExportTempVo> tempVoList = new ArrayList<>();
        //数据处理
        for (int i = SESSION_COUNT; i <= VISIT_DEPTH;i++){
            //得到时间
            if (!param.getType().equals(CUSTOM_DAYS)){
                param.setStartDate(getDate(param.getType()));
                param.setEndDate(getDate(NumberUtils.INTEGER_ZERO));
            }
            VisitStatisticsParam visitStatisticsParam = new VisitStatisticsParam();
            visitStatisticsParam.setStartDate(param.getStartDate());
            visitStatisticsParam.setEndDate(param.getEndDate());
            visitStatisticsParam.setType(param.getType());
            visitStatisticsParam.setGrading(param.getGrading());
            visitStatisticsParam.setAction(i);
            VisitStatisticsVo visitStatisticsVo = getVisitStatistics(visitStatisticsParam);

            VisitExportTempVo tempVo = new VisitExportTempVo();
            tempVo.setGrading(param.getGrading());
            tempVo.setAction(i);
            tempVo.setDate(visitStatisticsVo.getDate());
            tempVo.setList(visitStatisticsVo.getList());

            tempVoList.add(tempVo);
        }
        //得到处理后的数据
        List<VisitExportVo> exportVoList = handleExportData(tempVoList);
        //表格导出
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(exportVoList, VisitExportVo.class);
        return workbook;
    }

    /**
     * 将折线图数据处理成表格需要的数据格式
     * @param tempVoList 折线图数据
     * @return 表格数据
     */
    private List<VisitExportVo> handleExportData(List<VisitExportTempVo> tempVoList){
        //日期粒度下7种数据的出参
        List<VisitExportVo> exportVoList = new ArrayList<>();
        //用时间确定result的长度
        for (String date : tempVoList.get(0).getDate()){
            VisitExportVo exportVo = new VisitExportVo();
            exportVo.setDate(date);
            exportVoList.add(exportVo);
        }
        //为result赋值数据
        for (VisitExportTempVo vo : tempVoList){
            switch (vo.getAction()) {
                case SESSION_COUNT:
                    handleSessionCountExport(exportVoList, vo);
                    break;
                case PV:
                    handlePvExport(exportVoList, vo);
                    break;
                case UV:
                    handleUvExport(exportVoList, vo);
                    break;
                case UV_NEW:
                    handleUvNewExport(exportVoList, vo);
                    break;
                case STAY_TIME_UV:
                    handleStayTimeUvExport(exportVoList, vo);
                    break;
                case STAY_TIME_SESSION:
                    handleStayTimeSessionExport(exportVoList, vo);
                    break;
                case VISIT_DEPTH:
                    handleVisitDepthExport(exportVoList, vo);
                    break;
                default:
                    throw new IllegalStateException("Unexpected action: " + vo.getGrading());
            }
        }
        return exportVoList;
    }

    private void handleVisitDepthExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setVisitDepth(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }

    private void handleStayTimeSessionExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setStayTimeSession(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }

    private void handleStayTimeUvExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setStayTimeUv(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }

    private void handleUvNewExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setUvNew(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }

    private void handleUvExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setUv(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }

    private void handlePvExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setPv(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }

    private void handleSessionCountExport(List<VisitExportVo> exportVoList, VisitExportTempVo vo) {
        for (int i = 0;i < vo.getList().size();i++) {
            exportVoList.get(i).setSessionCount(vo.getList().get(i));
            switch (vo.getGrading()){
                case GRADING_DAY:
                    exportVoList.get(i).setGrading(GRADING_DAY_STRING);
                    break;
                case GRADING_WEEK:
                    exportVoList.get(i).setGrading(GRADING_WEEK_STRING);
                    break;
                case GRADING_MONTH:
                    exportVoList.get(i).setGrading(GRADING_MONTH_STRING);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + vo.getGrading());
            }
        }
    }
}
