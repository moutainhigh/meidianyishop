package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.TradesRecord;
import com.meidianyi.shop.db.shop.tables.TradesRecordSummary;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.asset.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record10;
import org.jooq.Record4;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.sum;

/**
 * @author liufei
 * @date 2019/8/2
 * @description 资产管理
 */
@Service
public class AssetManagementService extends ShopBaseService {
    private static TradesRecordSummary trs = TradesRecordSummary.TRADES_RECORD_SUMMARY.as("trs");
    private static TradesRecord tr = TradesRecord.TRADES_RECORD.as("tr");
    private static User u = User.USER.as("u");

    /**
     * 营收概况-现金/积分
     *
     * @param param 入参
     * @return 统计数据和折线图数据列表
     */
    public RevenueProfileVo revenueprofile(RevenueProfileParam param) {
        byte screeningTime = param.getScreeningTime();
        RevenueProfileVo vo;
        if (screeningTime > 0) {
            Date current = Util.getEarlySqlDate(new java.util.Date(), 0);
            Date prior = Util.getEarlySqlDate(new java.util.Date(), -screeningTime);
            // 拿到当前数值
            vo = getRevenueDate(current, screeningTime);
            // 拿到较前N日的数值
            RevenueProfileVo tempPre = getRevenueDate(prior, screeningTime);
            // 计算增长率
            calGrowthRate(vo, tempPre);
            // 获取折线图数据
            vo.setRevenueDates(getRevenueDateList(prior, current));
            vo.setStartTime(prior);
            vo.setEndTime(current);
        } else {
            // 自定义日期统计数据
            Date startDate = Optional.of(param.getStartTime()).orElse(Util.getEarlySqlDate(new java.util.Date(), 0));
            //结束日期不能大于当前日期
            Date endDate = Optional.of(param.getEndTime()).orElse(Util.getEarlySqlDate(new java.util.Date(), 0));
            LocalDate endLocalDate = endDate.toLocalDate();
            endLocalDate = endLocalDate.isBefore(LocalDate.now()) ? endLocalDate.plusDays(INTEGER_ONE) : LocalDate.now();
            endDate = Date.valueOf(endLocalDate);
            int day = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
            vo = getRevenueDate(startDate, endDate);
            RevenueProfileVo tempPre = getRevenueDate(Util.getEarlySqlDate(startDate, -day), startDate);
            calGrowthRate(vo, tempPre);
            vo.setRevenueDates(getRevenueDateList(startDate, endDate));
            vo.setStartTime(param.getStartTime());
            vo.setEndTime(param.getEndTime());
        }
        return vo;
    }
    public RevenueProfileScoreVo revenueprofileScore(RevenueProfileParam param) {
        byte screeningTime = param.getScreeningTime();
        RevenueProfileScoreVo vo;
        if (screeningTime > 0) {
            Date current = Util.getEarlySqlDate(new java.util.Date(), 0);
            Date prior = Util.getEarlySqlDate(new java.util.Date(), -screeningTime);
            vo = getRevenueScoreDate(current, screeningTime);
            RevenueProfileScoreVo tempPre = getRevenueScoreDate(prior, screeningTime);
            calScoreGrowthRate(vo, tempPre);
            vo.setRevenueDates(getRevenueScoreDateList(prior, current));
            vo.setStartTime(prior);
            vo.setEndTime(current);
        } else {
            Date startDate = Optional.of(param.getStartTime()).orElse(Util.getEarlySqlDate(new java.util.Date(), 1));
//            结束日期不能大于当前日期
            Date endDate = Optional.of(param.getEndTime()).orElse(Util.getEarlySqlDate(new java.util.Date(), 0));
            LocalDate endLocalDate = endDate.toLocalDate();
            endLocalDate = endLocalDate.isBefore(LocalDate.now()) ? endLocalDate.plusDays(INTEGER_ONE) : LocalDate.now();
            endDate = Date.valueOf(endLocalDate);
            int day = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
            vo = getRevenueScoreDate(startDate, endDate);
            RevenueProfileScoreVo tempPre = getRevenueScoreDate(Util.getEarlySqlDate(startDate, -day), startDate);
            calScoreGrowthRate(vo, tempPre);
            vo.setRevenueDates(getRevenueScoreDateList(startDate, endDate));
            vo.setStartTime(param.getStartTime());
            vo.setEndTime(param.getEndTime());
        }
        return vo;
    }

    /**
     * 计算增长率
     */
    private void calGrowthRate(RevenueProfileVo vo, RevenueProfileVo tempPre) {
        vo.setIncomeRealMoneyPer(getRate(vo.getIncomeRealMoney(), tempPre.getIncomeRealMoney()));
        vo.setIncomeTotalMoneyPer(getRate(vo.getIncomeTotalMoney(), tempPre.getIncomeTotalMoney()));
        vo.setOutgoMoneyPer(getRate(vo.getOutgoMoney(), tempPre.getOutgoMoney()));
    }
    private void calScoreGrowthRate(RevenueProfileScoreVo vo, RevenueProfileScoreVo tempPre) {
        vo.setIncomeRealScorePer(getRate(vo.getIncomeRealScore(), tempPre.getIncomeRealScore()));
        vo.setIncomeTotalScorePer(getRate(vo.getIncomeTotalScore(), tempPre.getIncomeTotalScore()));
        vo.setOutgoScorePer(getRate(vo.getOutgoScore(), tempPre.getOutgoScore()));
    }

    private BigDecimal getRate(BigDecimal vo, BigDecimal tempPre) {
        if (Objects.isNull(vo) || Objects.isNull(tempPre)) {
            return ZERO;
        }
        if (vo.subtract(tempPre).compareTo(ZERO) == 0 || tempPre.compareTo(ZERO) == 0) {
            return ZERO;
        }
        return vo.subtract(tempPre).divide(tempPre, 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取指定时间段数据
     */
    private RevenueProfileVo getRevenueDate(Date date, byte type) {
        return getSelectConditon().and(trs.REF_DATE.eq(date)).and(trs.TYPE.eq(type)).fetchOptionalInto(RevenueProfileVo.class).orElse(new RevenueProfileVo());
    }
    private RevenueProfileScoreVo getRevenueScoreDate(Date date, byte type) {
        return getSelectConditon().and(trs.REF_DATE.eq(date)).and(trs.TYPE.eq(type)).fetchOptionalInto(RevenueProfileScoreVo.class).orElse(new RevenueProfileScoreVo());
    }

    /**
     * 获取自定义时间数据
     */
    private RevenueProfileVo getRevenueDate(Date startDate, Date endDate) {
        return getSelectConditonWithSum().and(trs.REF_DATE.greaterOrEqual(startDate))
            .and(trs.REF_DATE.lessThan(endDate)).and(trs.TYPE.eq((byte) 1)).fetchOptionalInto(RevenueProfileVo.class).orElse(new RevenueProfileVo());
    }
    private RevenueProfileScoreVo getRevenueScoreDate(Date startDate, Date endDate) {
        return getScoreSelectConditonWithSum().and(trs.REF_DATE.greaterOrEqual(startDate))
            .and(trs.REF_DATE.lessThan(endDate)).and(trs.TYPE.eq((byte) 1)).fetchOptionalInto(RevenueProfileScoreVo.class).orElse(new RevenueProfileScoreVo());
    }

    /**
     * 获取折线图数据
     */
    private List<RevenueDate> getRevenueDateList(Date startDate, Date endDate) {
        return getSelectConditon().and(trs.REF_DATE.greaterThan(startDate)).and(trs.REF_DATE.le(endDate)).and(trs.TYPE.eq((byte) 1)).fetchInto(RevenueDate.class);
    }
    private List<RevenueScoreDate> getRevenueScoreDateList(Date startDate, Date endDate) {
        return getScoreSelectConditon().and(trs.REF_DATE.greaterThan(startDate)).and(trs.REF_DATE.le(endDate)).and(trs.TYPE.eq((byte) 1)).fetchInto(RevenueScoreDate.class);
    }

    private SelectConditionStep<Record4<Date, BigDecimal, BigDecimal, BigDecimal>> getSelectConditon() {
        return db().select(trs.REF_DATE, trs.INCOME_REAL_MONEY, trs.INCOME_TOTAL_MONEY, trs.OUTGO_MONEY).from(trs).where();
    }
    private SelectConditionStep<Record4<Date, BigDecimal, BigDecimal, BigDecimal>> getScoreSelectConditon() {
        return db().select(trs.REF_DATE, trs.INCOME_REAL_SCORE, trs.INCOME_TOTAL_SCORE, trs.OUTGO_SCORE).from(trs).where();
    }

    private SelectConditionStep<Record4<Date, BigDecimal, BigDecimal, BigDecimal>> getSelectConditonWithSum() {
        return db().select(min(trs.REF_DATE).as("refDate"), sum(trs.INCOME_REAL_MONEY).as("incomeRealMoney"), sum(trs.INCOME_TOTAL_MONEY).as("incomeTotalMoney"), sum(trs.OUTGO_MONEY).as("outgoMoney")).from(trs).where();
    }
    private SelectConditionStep<Record4<Date, BigDecimal, BigDecimal, BigDecimal>> getScoreSelectConditonWithSum() {
        return db().select(min(trs.REF_DATE).as("refDate"), sum(trs.INCOME_REAL_SCORE).as("incomeRealScore"), sum(trs.INCOME_TOTAL_SCORE).as("incomeTotalScore"), sum(trs.OUTGO_SCORE).as("outgoScore")).from(trs).where();
    }

    /**
     * 现金/积分资产管理明细
     */
    public PageResult<AssetDetailVo> assetManageDetail(AssetDetailParam param) {
        return getPageResult(getSelectConditionStep(param).orderBy(tr.TRADE_TIME.desc()), param.getCurrentPage(), param.getPageRows(), AssetDetailVo.class);
    }

    private SelectConditionStep<Record10<Timestamp, String, BigDecimal, Integer, Byte, Byte, Byte, String, String, String>> getSelectConditionStep(AssetDetailParam param) {
        SelectConditionStep<Record10<Timestamp, String, BigDecimal, Integer, Byte, Byte, Byte, String, String, String>> conditionStep = db()
            .select(tr.TRADE_TIME, tr.TRADE_SN, tr.TRADE_NUM, tr.USER_ID, tr.TRADE_TYPE, tr.TRADE_FLOW, tr.TRADE_STATUS, u.USERNAME, u.MOBILE, USER_DETAIL.REAL_NAME)
            .from(tr).leftJoin(u).on(tr.USER_ID.eq(u.USER_ID))
            .leftJoin(USER_DETAIL).on(USER_DETAIL.USER_ID.eq(u.USER_ID))
            .where(tr.TRADE_CONTENT.eq(param.getTradeContent()));

        if (StringUtils.isNotBlank(param.getTradeSn())) {
            conditionStep = conditionStep.and(tr.TRADE_SN.like(this.likeValue(param.getTradeSn())));
        }
        if (StringUtils.isNotBlank(param.getUsername())) {
            conditionStep = conditionStep.and(u.USERNAME.like(this.likeValue(param.getUsername())));
        }
        if (StringUtils.isNotBlank(param.getRealName())) {
            conditionStep = conditionStep.and(USER_DETAIL.REAL_NAME.like(this.likeValue(param.getRealName())));
        }
        if (StringUtils.isNotBlank(param.getMobile())) {
            conditionStep = conditionStep.and(u.MOBILE.like(this.likeValue(param.getMobile())));
        }
        if (param.getStartTime() != null) {
            conditionStep = conditionStep.and(tr.TRADE_TIME.greaterOrEqual(param.getStartTime()));
        }
        if (param.getEndTime() != null) {
            conditionStep = conditionStep.and(tr.TRADE_TIME.lessOrEqual(param.getEndTime()));
        }
        if (param.getLowerLimit() != null) {
            conditionStep = conditionStep.and(tr.TRADE_NUM.greaterOrEqual(param.getLowerLimit()));
        }
        if (param.getUpperLimit() != null) {
            conditionStep = conditionStep.and(tr.TRADE_NUM.lessOrEqual(param.getUpperLimit()));
        }
        if (param.getTradeType() != null && param.getTradeType() > 0) {
            conditionStep = conditionStep.and(tr.TRADE_TYPE.eq(param.getTradeType()));
        }
        if (param.getTradeFlow() != null && param.getTradeFlow() != -1) {
            conditionStep = conditionStep.and(tr.TRADE_FLOW.eq(param.getTradeFlow()));
        }
        return conditionStep;
    }

    /**
     * excel导出数据
     *
     * @param param 导出数据筛选条件
     */
    public Workbook export2Excel(AssetDetailParam param, String lang) {
        List<AssetDetailExportVo> list = getSelectConditionStep(param).orderBy(tr.TRADE_TIME.desc())
            .limit(param.getExportRowStart() - 1, param.getExportRowEnd() - param.getExportRowStart() + 1)
            .fetchInto(AssetDetailExportVo.class);
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(list, AssetDetailExportVo.class);
        return workbook;
    }

}
