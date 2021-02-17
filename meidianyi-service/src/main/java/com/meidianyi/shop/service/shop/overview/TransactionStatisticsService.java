package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.*;
import com.meidianyi.shop.db.shop.tables.records.DistributionTagRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.transaction.*;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.*;

import static com.meidianyi.shop.service.shop.overview.RealTimeOverviewService.div;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.jooq.impl.DSL.*;

/**
 * @author liufei
 * @date 2019/7/31
 * @description 交易统计
 */
@Service
public class TransactionStatisticsService extends ShopBaseService {
    private static final String DEFAULT_SORT_TYPE = "asc";

    private static OrderInfo oi = OrderInfo.ORDER_INFO.as("oi");
    private static OrderGoods og = OrderGoods.ORDER_GOODS.as("og");
    private static UserLoginRecord ulr = UserLoginRecord.USER_LOGIN_RECORD.as("ulr");
    private static UserTag ut = UserTag.USER_TAG.as("ut");
    private static Tag t = Tag.TAG.as("t");
    private static User u = User.USER.as("u");
    private static DistributionTag dt = DistributionTag.DISTRIBUTION_TAG.as("dt");

    /**
     * 地域分布
     *
     * @param param 入参
     * @return 地域分布显示数据集
     */
    public GeographicalCollVo geographical(GeographicalParam param) {
        GeographicalCollVo collVo = new GeographicalCollVo();
        Optional<Date> screeningTime = Optional.of(param.getScreeningTime());
        Timestamp startTime = DateUtils.currentMonthFirstDay(screeningTime.orElse(new Date()));
        Timestamp endTime = DateUtils.nextMonthFirstDay(screeningTime.orElse(new Date()));
        List<GeographicalVo> voList = db().select(min(oi.PROVINCE_NAME).as("province")
                , min(oi.PROVINCE_CODE).as("provinceCode")
                , sum(oi.MONEY_PAID.add(oi.SCORE_DISCOUNT).add(oi.USE_ACCOUNT).add(oi.MEMBER_CARD_BALANCE)).as("totalDealMoney")
                , countDistinct(oi.USER_ID).as("orderUserNum")
                , count(oi.ORDER_ID).as("orderNum"))
                .from(oi)
                .where(commomCondition(startTime, endTime))
                .groupBy(oi.PROVINCE_CODE)
                .fetchInto(GeographicalVo.class);
        for (GeographicalVo vo : voList) {
            vo.setUv(getDistrictUv(startTime, endTime, vo.getProvinceCode()));
            vo.setUv2paid(div(vo.getOrderUserNum(), vo.getUv()));
            // 省份格式化，去掉省字后缀
            vo.setProvince(formatterProvince(vo.getProvince()));
        }
        double maxMoney = voList.stream().max(Comparator.comparing(GeographicalVo::getTotalDealMoney)).orElse(new GeographicalVo()).getTotalDealMoney();
        double minMoney = voList.stream().min(Comparator.comparing(GeographicalVo::getTotalDealMoney)).orElse(new GeographicalVo()).getTotalDealMoney();
        collVo.setMaxTotalDealMoney(maxMoney);
        collVo.setMinTotalDealMoney(minMoney);
        Comparator<GeographicalVo> comparator = Comparator.comparing(GeographicalVo::getTotalDealMoney);
        switch(param.getOrderByField()){
            case "total_deal_money" :
                break;
            case "paid_user_num" :
                comparator = Comparator.comparing(GeographicalVo::getOrderUserNum);
                break;
            case "uv" :
                comparator = Comparator.comparing(GeographicalVo::getUv);
                break;
            case "order_num" :
                comparator = Comparator.comparing(GeographicalVo::getOrderNum);
                break;
            case "uv_2_paid" :
                comparator = Comparator.comparing(GeographicalVo::getUv2paid);
                break;
            default :
                break;
        }
        if(!DEFAULT_SORT_TYPE.equalsIgnoreCase(param.getOrderByType())){
            comparator = comparator.reversed();
        }
        collVo.setVos(voList.stream().sorted(comparator).collect(toList()));
        return collVo;
    }

    private static final String SHENG = "省";

    public String formatterProvince(String name) {
        return name.replace(SHENG, StringUtils.EMPTY);
    }

    /**
     * 构造公共查询条件，成交用户数，总成交金额，付款订单数
     */
    private Condition commomCondition(Timestamp startTime, Timestamp endTime) {
        return oi.CREATE_TIME.greaterOrEqual(startTime).and(oi.CREATE_TIME.lessThan(endTime).and(oi.ORDER_STATUS.greaterOrEqual((byte) 3).or(oi.ORDER_STATUS.eq((byte) 0).and(oi.BK_ORDER_PAID.greaterThan((byte) 0)))).and(oi.IS_COD.eq((byte) 0).or(oi.IS_COD.eq((byte) 1).and(oi.SHIPPING_TIME.isNotNull()))));
    }

    /**
     * 获得地区用户uv
     */
    private Integer getDistrictUv(Timestamp startTime, Timestamp endTime, String provinceCode) {
        Optional<Integer> optional = db().select(countDistinct(ulr.USER_ID)).from(ulr).where(ulr.CREATE_TIME.greaterOrEqual(startTime)).and(ulr.CREATE_TIME.lessThan(endTime)).and(ulr.PROVINCE_CODE.eq(provinceCode)).fetchOptionalInto(Integer.class);
        return optional.orElse(0);
    }

    public static final List<Byte> RECENT_DATE = new ArrayList<Byte>() {{
        add(Byte.valueOf("1"));
        add(Byte.valueOf("7"));
        add(Byte.valueOf("30"));
    }};
    /**
     * 标签成交分析
     * @param param 入参
     */
    public PageResult<LabelAnalysisVo> labelAnalysis(LabelAnalysisParam param){
        Byte screenTime = param.getScreeningTime();
        if (screenTime > 0){
            if (!RECENT_DATE.contains(screenTime)) {
                param.setScreeningTime(BYTE_ONE);
            }
            return fixedLabelAnalysis(param);
        }else {
            return customerLabelAnalysis(param);
        }
    }

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";

    public Map<String, java.sql.Date> createDate(LabelAnalysisParam param) {
        if (param.getScreeningTime() > 0) {
            return new HashMap<String, java.sql.Date>(2) {{
                put(START_TIME, DateUtils.yyyyMmDdDate(LocalDate.now().minusDays(param.getScreeningTime())));
                put(END_TIME, DateUtils.yyyyMmDdDate(LocalDate.now()));
            }};
        }
        return new HashMap<String, java.sql.Date>(2) {{
            put(START_TIME, DateUtils.yyyyMmDdDate(param.getStartTime().toLocalDateTime().toLocalDate()));
            put(END_TIME, DateUtils.yyyyMmDdDate(param.getEndTime().toLocalDateTime().toLocalDate()));
        }};
    }

    /**
     * 固定日期数据查询
     */
    private PageResult<LabelAnalysisVo> fixedLabelAnalysis(LabelAnalysisParam param){
        SelectConditionStep<Record7<String, Integer, Integer, BigDecimal, Integer, Integer, Integer>> conditionStep = db().select(dt.TAG_NAME.as("tagName")
                ,dt.PAY_USER_NUM.as("paidUserNum")
                ,dt.PAY_ORDER_NUM.as("paidNum")
                ,dt.PAY_ORDER_MONEY.as("paidMoney")
                ,dt.PAY_GOODS_NUMBER.as("paidGoodsNum")
                ,dt.HAS_MOBILE_NUM.as("userNumWithPhone")
                ,dt.HAS_USER_NUM.as("userNum"))
                .from(dt)
                .where(dt.REF_DATE.eq(Util.getEarlySqlDate(new Date(),0)))
                .and(dt.TYPE.eq(param.getScreeningTime()));
        //默认排序字段为user_num
       TableField<DistributionTagRecord, ?> field = dt.HAS_USER_NUM;
        switch(param.getOrderByField()){
            case "user_num_with_phone" :
                field = dt.HAS_MOBILE_NUM;
                break;
            case "paid_num" :
                field = dt.PAY_ORDER_NUM;
                break;
            case "paid_money" :
                field = dt.PAY_ORDER_MONEY;
                break;
            case "paid_user_num" :
                field = dt.PAY_USER_NUM;
                break;
            case "paid_goods_num" :
                field = dt.PAY_GOODS_NUMBER;
                break;
            default :
                break;
        }
        boolean desc = (!DEFAULT_SORT_TYPE.equalsIgnoreCase(param.getOrderByType()));

        SelectLimitStep<Record7<String, Integer, Integer, BigDecimal, Integer, Integer, Integer>> limitStep = conditionStep.orderBy(desc ? field.desc() : field);
        return getPageResult(limitStep,param.getCurrentPage(),param.getPageRows(),LabelAnalysisVo.class);

    }

    /**
     * 自定义时间实时查询数据
     */
    private PageResult<LabelAnalysisVo> customerLabelAnalysis(LabelAnalysisParam param){
        if (param.getStartTime() == null || param.getEndTime() == null) {
            param.setScreeningTime(BYTE_ONE);
            return fixedLabelAnalysis(param);
        }
        Timestamp startTime = param.getStartTime();
        Timestamp endTime = param.getEndTime();
        /* 先查用户数，放入分页集合中，然后循环设置其他数据 */
        SelectHavingStep<Record3<Integer, String, Integer>> limitStep = db().select(min(ut.TAG_ID).as("tagId"),min(t.TAG_NAME).as("tagName"),count(ut.USER_ID).as("userNum"))
                .from(ut).leftJoin(t).on(ut.TAG_ID.eq(t.TAG_ID))
                .where(ut.CREATE_TIME.greaterOrEqual(startTime))
                .and(ut.CREATE_TIME.lessThan(endTime))
                .groupBy(ut.TAG_ID);
        PageResult<LabelAnalysisVo> result = getPageResult(limitStep,param.getCurrentPage(),param.getPageRows(),LabelAnalysisVo.class);

        Map<Integer,Record5<Integer,BigDecimal,Integer,Integer,BigDecimal>> temp = db()
                .select(min(ut.TAG_ID).as("tag_id"),sum(oi.MONEY_PAID.add(oi.SCORE_DISCOUNT).add(oi.USE_ACCOUNT).add(oi.MEMBER_CARD_BALANCE)).as("paidMoney")
                , countDistinct(oi.USER_ID).as("paidUserNum")
                , count(oi.ORDER_ID).as("paidNum")
                , sum(og.GOODS_NUMBER).as("paidGoodsNum"))
                .from(ut)
                .leftJoin(oi)
                .on(ut.USER_ID.eq(oi.USER_ID))
                .leftJoin(og)
                .on(oi.ORDER_SN.eq(og.ORDER_SN))
                .where(commomCondition(startTime,endTime))
                .groupBy(ut.TAG_ID)
                .fetchMap(ut.TAG_ID);

        Map<Integer,Record2<Integer,Integer>> temp1 = db().select(min(ut.TAG_ID).as("tag_id"),countDistinct(u.USER_ID).as("userNumWithPhone")).from(ut)
                .leftJoin(u)
                .on(ut.USER_ID.eq(u.USER_ID))
                .where(u.CREATE_TIME.greaterOrEqual(startTime))
                .and(u.CREATE_TIME.lessThan(endTime))
                .and(u.MOBILE.isNotNull())
                .groupBy(ut.TAG_ID)
                .fetchMap(ut.TAG_ID);
        for (LabelAnalysisVo vo : result.getDataList()){
            if (temp.containsKey(vo.getTagId())){
                Record5<Integer,BigDecimal,Integer,Integer,BigDecimal> record4 = temp.get(vo.getTagId());
                vo.setPaidMoney(record4.value2());
                vo.setPaidUserNum(record4.value3());
                vo.setPaidNum(record4.value4());
                vo.setPaidGoodsNum(record4.value5() != null ? record4.value5().intValue() : 0);
            }
            if (temp1.containsKey(vo.getTagId())){
                Record2<Integer,Integer> record1 = temp1.get(vo.getTagId());
                vo.setUserNumWithPhone(record1.value2());
            }
        }
        result.setDataList(sortBy(param,result.getDataList()));
        return result;
    }
    private List<LabelAnalysisVo> sortBy(LabelAnalysisParam param,List<LabelAnalysisVo> result){
        //默认排序字段为user_num
        Comparator<LabelAnalysisVo> comparator = Comparator.comparing(LabelAnalysisVo::getUserNum);
        switch(param.getOrderByField()){
            case "user_num_with_phone" :
                comparator = Comparator.comparing(LabelAnalysisVo::getUserNumWithPhone);
                break;
            case "paid_num" :
                comparator = Comparator.comparing(LabelAnalysisVo::getPaidNum);
                break;
            case "paid_money" :
                comparator = Comparator.comparing(LabelAnalysisVo::getPaidMoney);
                break;
            case "paid_user_num" :
                comparator = Comparator.comparing(LabelAnalysisVo::getPaidUserNum);
                break;
            case "paid_goods_num" :
                comparator = Comparator.comparing(LabelAnalysisVo::getPaidGoodsNum);
                break;
            default :
                break;
        }
        if(!DEFAULT_SORT_TYPE.equalsIgnoreCase(param.getOrderByType())){
            comparator = comparator.reversed();
        }
        return result.stream().sorted(comparator).collect(toList());
    }
}
