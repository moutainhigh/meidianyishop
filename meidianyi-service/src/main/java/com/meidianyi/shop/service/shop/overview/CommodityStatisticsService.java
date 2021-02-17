package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.GoodsOverviewSummary;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.commodity.*;
import com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.divideWithOutCheck;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsBrand.GOODS_BRAND;
import static com.meidianyi.shop.db.shop.tables.GoodsLabel.GOODS_LABEL;
import static com.meidianyi.shop.db.shop.tables.GoodsLabelCouple.GOODS_LABEL_COUPLE;
import static com.meidianyi.shop.db.shop.tables.GoodsSummary.GOODS_SUMMARY;
import static com.meidianyi.shop.db.shop.tables.Sort.SORT;
import static com.meidianyi.shop.service.pojo.shop.config.trade.TradeConstant.FIELD_CLAZZ;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.BYTE_THREE;
import static com.meidianyi.shop.service.pojo.shop.overview.OverviewConstant.STRING_ZERO;
import static com.meidianyi.shop.service.pojo.shop.overview.commodity.CharConstant.*;
import static com.meidianyi.shop.service.shop.store.store.StoreWxService.BYTE_TWO;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.*;

/**
 * The type Commodity statistics service.
 *
 * @author liufei
 * @date 2019 /7/22
 */
@Slf4j
@Service
public class CommodityStatisticsService extends ShopBaseService {
    @Autowired
    private GoodsStatisticTaskService goodsStatisticTaskService;

    /**
     * goods_summary表默认排序字段
     */
    private static final String DEFAULT_ORDER_BY_FIELD = "uv";
    /**
     * 默认排序规则，升序asc
     */
    private static final String DEFAULT_ORDER_BY_TYPE = "asc";
    private static final String STRING_DATE = "Date";

    /**
     * The constant GOS.
     */
    public static final GoodsOverviewSummary GOS = GoodsOverviewSummary.GOODS_OVERVIEW_SUMMARY.as("GOS");

    /**
     * 查询指定时间段商品概览，例如最近一天，七天，一个月
     *
     * @param param the param
     * @return the product overview vo
     */
    public ProductOverviewVo fixedDayOverview(ProductOverviewParam param) {
        byte type = param.getDynamicDate();
        LocalDate now = LocalDate.now();
        LocalDate prefix = now.minusDays(type);
        ProductOverviewVo nowData = getGoodsOverviewSummary(DateUtils.yyyyMmDdDate(now), type);
        if (Objects.isNull(nowData)) {
            return ProductOverviewVo.builder().build();
        }
        nowData.setStartTime(Date.valueOf(prefix));
        nowData.setEndTime(Date.valueOf(now));
        nowData.setVisit2paid(divideWithOutCheck(BigDecimal.valueOf(nowData.getPaidGoodsNum()), BigDecimal.valueOf(nowData.getVisitedGoodsNum())));
        ProductOverviewVo prefixData = getGoodsOverviewSummary(DateUtils.yyyyMmDdDate(prefix), type);
        if (Objects.isNull(prefixData)) {
            return nowData;
        }
        prefixData.setVisit2paid(divideWithOutCheck(BigDecimal.valueOf(prefixData.getPaidGoodsNum()), BigDecimal.valueOf(prefixData.getVisitedGoodsNum())));
        // 获取变化率数据
        nowData.setChangeRate(getChangeRateData(nowData, prefixData));
        return nowData;
    }

    private static final String CHANGE_RATE = "changeRate";
    /**
     * 计算统计数据变化率
     */
    private Map<String, BigDecimal> getChangeRateData(ProductOverviewVo nowData, ProductOverviewVo prefixData) {
        Map<String, BigDecimal> changeRate = new HashMap<>(16);
        // 计算变化率
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(nowData.getClass());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            Arrays.stream(descriptors)
                .filter((e) -> !StringUtils.equals(CHANGE_RATE, e.getName()))
                .filter((e) -> !FIELD_CLAZZ.equalsIgnoreCase(e.getName()))
                .forEach((d) -> {
                    try {
                        // 拿到属性名称
                        String fieldName = d.getName();
                        Method method = d.getReadMethod();
                        Object o1 = method.invoke(nowData);
                        Object o2 = method.invoke(prefixData);
                        BigDecimal result = getRate(o1, o2);
                        log.debug("属性 {} 对应上一时间段的变化率为 {} !", fieldName, result);
                        changeRate.put(fieldName, result);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("计算变化率过程失败, 原因如下：{}", e.getMessage());
                        throw new BusinessException(JsonResultCode.CODE_FAIL);
                    }
                });
        } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        return changeRate;
    }

    /**
     * 计算统计数据变化率公式 (now-prefix)/prefix
     */
    private BigDecimal getRate(Object now, Object prefix) {
        if(now instanceof  Date) {
            return new BigDecimal("0");
        }
        BigDecimal n;
        BigDecimal p;
        if (Objects.isNull(now)) {
            n = BIGDECIMAL_ZERO;
        } else {
            n = BigDecimal.valueOf(Double.parseDouble(now.toString()));
        }
        if (Objects.isNull(prefix)) {
            p = BIGDECIMAL_ZERO;
        } else {
            p = BigDecimal.valueOf(Double.parseDouble(prefix.toString()));
        }
        return BigDecimalUtil.divideWithOutCheck(n.subtract(p), p);
    }

    /**
     * 获取指定时间下的商品概览统计数据(查询统计表)
     */
    private ProductOverviewVo getGoodsOverviewSummary(Date date, byte type) {
        return db().selectFrom(GOS)
            .where(GOS.REF_DATE.eq(date))
            .and(GOS.TYPE.eq(type))
            .limit(INTEGER_ONE)
            .fetchOneInto(ProductOverviewVo.class);
    }

    /**
     * 查询自定义时间段商品概览  @param param the param
     *
     * @param param the param
     * @return the product overview vo
     */
    public ProductOverviewVo customizeDayOverview(ProductOverviewParam param) {
        LocalDate start = param.getStartTime().toLocalDateTime().toLocalDate();
        LocalDate end = param.getEndTime().toLocalDateTime().toLocalDate();
        long days = end.toEpochDay() - start.toEpochDay();
        LocalDate prefixStart = start.minusDays(days);

        ProductOverviewVo data = getCustomizeGoodsOverviewSummary(start, end);
        if (Objects.isNull(data)) {
            return ProductOverviewVo.builder().build();
        }
        data.setStartTime(Date.valueOf(start));
        data.setEndTime(Date.valueOf(end));
        data.setVisit2paid(divideWithOutCheck(BigDecimal.valueOf(data.getPaidGoodsNum()), BigDecimal.valueOf(data.getVisitedGoodsNum())));
        ProductOverviewVo prefixData = getCustomizeGoodsOverviewSummary(prefixStart, start);
        if (Objects.isNull(prefixData)) {
            return data;
        }
        data.setVisit2paid(divideWithOutCheck(BigDecimal.valueOf(data.getPaidGoodsNum()), BigDecimal.valueOf(data.getVisitedGoodsNum())));
        // 获取变化率数据
        data.setChangeRate(getChangeRateData(data, prefixData));
        return data;
    }

    /**
     * 获取自定义时间下的商品概览统计数据(查询统计表)
     */
    private ProductOverviewVo getCustomizeGoodsOverviewSummary(LocalDate start, LocalDate end) {
        return db().select(
            sum(GOS.ON_SHELF_GOODS_NUM).as("onShelfGoodsNum")
            , sum(GOS.SOLD_GOODS_NUM).as("soldGoodsNum")
            , sum(GOS.VISITED_GOODS_NUM).as("visitedGoodsNum")
            , sum(GOS.GOODS_USER_VISIT).as("goodsUserVisit")
            , sum(GOS.GOODS_PAGEVIEWS).as("goodsPageviews")
            , sum(GOS.PURCHASE_NUM).as("purchaseNum")
            , sum(GOS.PURCHASE_QUANTITY).as("purchaseQuantity")
            , sum(GOS.PAID_GOODS_NUM).as("paidGoodsNum")
            , sum(GOS.ORDER_GOODS_NUM).as("orderGoodsNum"))
            .from(GOS)
            .where(GOS.REF_DATE.greaterThan(Date.valueOf(start)))
            .and(GOS.REF_DATE.le(Date.valueOf(end)))
            .and(GOS.TYPE.eq(BYTE_ONE))
            .fetchOneInto(ProductOverviewVo.class);
    }

    /**
     * 条件查询商品概览  @param param the param
     *
     * @param param the param
     * @return the product overview vo
     */
    public ProductOverviewVo conditionOverview(ProductOverviewParam param) {
        ProductOverviewVo data = ProductOverviewVo.builder()
            // 在架商品数
            .onShelfGoodsNum(goodsStatisticTaskService.getSaleGoodsNumber(param))
            // 动销商品数
            .soldGoodsNum(goodsStatisticTaskService.getDySoldGoodsNum(param))
            // 被访问商品数
            .visitedGoodsNum(goodsStatisticTaskService.getGoodsNumByVisit(param))
            // 商品访客数 (商品UV)
            .goodsUserVisit(goodsStatisticTaskService.getGoodsUv(param))
            // 商品浏览量 (商品pv)
            .goodsPageviews(goodsStatisticTaskService.getGoodsPv(param))
            // 加购人数
            .purchaseNum(goodsStatisticTaskService.addCartUserNum(param))
            // 加购件数
            .purchaseQuantity(goodsStatisticTaskService.getAddCartGoodsNumber(param))
            // 付款商品数
            .paidGoodsNum(goodsStatisticTaskService.paidGoodsNum(param))
            // 下单商品数
            .orderGoodsNum(goodsStatisticTaskService.getPayOrderGoodsNum(param))
            .build();
        if (Objects.isNull(data)) {
            return ProductOverviewVo.builder().build();
        }
        // 商品访问付款转化率
        data.setVisit2paid(divideWithOutCheck(BigDecimal.valueOf(data.getPaidGoodsNum()), BigDecimal.valueOf(data.getVisitedGoodsNum())));

        LocalDate start = param.getStartTime().toLocalDateTime().toLocalDate();
        LocalDate end = param.getEndTime().toLocalDateTime().toLocalDate();
        data.setStartTime(Date.valueOf(start));
        data.setEndTime(Date.valueOf(end));
        // 计算起始日期和结束日期相差的天数
        long days = end.toEpochDay() - start.toEpochDay();
        LocalDate prefixStart = start.minusDays(days);
        // 计算得到前一段等长的起止日期
        param.setEndTime(param.getStartTime());
        param.setStartTime(Timestamp.valueOf(prefixStart.atStartOfDay()));

        ProductOverviewVo prefixData = ProductOverviewVo.builder()
            .onShelfGoodsNum(goodsStatisticTaskService.getSaleGoodsNumber(param))
            .soldGoodsNum(goodsStatisticTaskService.getDySoldGoodsNum(param))
            .visitedGoodsNum(goodsStatisticTaskService.getGoodsNumByVisit(param))
            .goodsUserVisit(goodsStatisticTaskService.getGoodsUv(param))
            .goodsPageviews(goodsStatisticTaskService.getGoodsPv(param))
            .purchaseNum(goodsStatisticTaskService.addCartUserNum(param))
            .purchaseQuantity(goodsStatisticTaskService.getAddCartGoodsNumber(param))
            .paidGoodsNum(goodsStatisticTaskService.paidGoodsNum(param))
            .orderGoodsNum(goodsStatisticTaskService.getPayOrderGoodsNum(param))
            .build();
        if (Objects.isNull(prefixData)) {
            return data;
        }
        prefixData.setVisit2paid(divideWithOutCheck(BigDecimal.valueOf(prefixData.getPaidGoodsNum()), BigDecimal.valueOf(prefixData.getVisitedGoodsNum())));

        data.setChangeRate(getChangeRateData(data, prefixData));
        return data;
    }

    /**
     * 查询指定时间段商品效果，例如最近一天，七天，一个月  @param param the param
     *
     * @param param the param
     * @return the page result
     */
    public PageResult<ProductEffectVo> fixedDayEffect(ProductEffectParam param) {
        /** 必要筛选条件 */
        Condition baseCondition = (GOODS_SUMMARY.REF_DATE.eq(DateUtils.yyyyMmDdDate(LocalDate.now())))
            .and(GOODS_SUMMARY.TYPE.eq(param.getDynamicDate()));
        SelectJoinStep<?> joinStep = db().select(GOODS_SUMMARY.GOODS_ID
            , GOODS.GOODS_NAME
            , GOODS.GOODS_IMG
            , GOODS.SHOP_PRICE
            , GOODS_SUMMARY.NEW_USER_NUMBER
            , GOODS_SUMMARY.OLD_USER_NUMBER
            , GOODS_SUMMARY.PV
            , GOODS_SUMMARY.UV
            , GOODS_SUMMARY.CART_UV
            , GOODS_SUMMARY.PAID_UV
            , GOODS_SUMMARY.PAID_GOODS_NUMBER
            , GOODS_SUMMARY.GOODS_SALES
            , GOODS_SUMMARY.RECOMMEND_USER_NUM
            , GOODS_SUMMARY.COLLECT_USE_NUM
            , GOODS_SUMMARY.SHARE_PV
            , GOODS_SUMMARY.SHARE_UV
        )
            .from(GOODS_SUMMARY)
            .leftJoin(GOODS)
            .on(GOODS.GOODS_ID.eq(GOODS_SUMMARY.GOODS_ID));

        PageResult<ProductEffectVo> pageResult = this.getPageResult(createEffectSelect(param, joinStep, baseCondition, true), param.getCurrentPage(), param.getPageRows(), ProductEffectVo.class);

        for (ProductEffectVo vo : pageResult.dataList) {
            vo.setNewUserPercentage(BigDecimalUtil.divideWithOutCheck(vo.getNewUserNumber(), vo.getPaidUv()));
            vo.setOldUserPercentage(BigDecimalUtil.divideWithOutCheck(vo.getOldUserNumber(), vo.getPaidUv()));
            vo.setUv2paidGoods(BigDecimalUtil.divideWithOutCheck(vo.getPaidGoodsNumber(), vo.getUv()));
        }
        return pageResult;
    }

    private SortField<Object> getSortField(Optional<String> field, Optional<String> sortType, boolean isFixedDay) {
        if (!sortType.isPresent() || DEFAULT_ORDER_BY_TYPE.equalsIgnoreCase(sortType.get())) {
            if (isFixedDay) {
                return DSL.field(name(field.orElse(DEFAULT_ORDER_BY_FIELD))).asc();
            }
            return DSL.field(name(field.orElse(DEFAULT_ORDER_BY_FIELD))).as(Util.underlineToHump(field.orElse(DEFAULT_ORDER_BY_FIELD))).asc();
        }
        if (isFixedDay) {
            return DSL.field(name(field.orElse(DEFAULT_ORDER_BY_FIELD))).desc();
        }
        return DSL.field(name(field.orElse(DEFAULT_ORDER_BY_FIELD))).as(Util.underlineToHump(field.orElse(DEFAULT_ORDER_BY_FIELD))).desc();
    }

    /**
     * 查询自定义时间段商品效果  @param param the param
     *
     * @param param the param
     * @return the page result
     */
    public PageResult<ProductEffectVo> customizeDayEffect(ProductEffectParam param) {
        // 必要筛选条件
        Condition baseCondition = (GOODS_SUMMARY.REF_DATE.greaterThan(new Date(param.getStartTime().getTime())))
            .and(GOODS_SUMMARY.REF_DATE.le(new Date(param.getEndTime().getTime())))
            .and(GOODS_SUMMARY.TYPE.eq(BYTE_ONE));
        SelectJoinStep<?> joinStep = db().select(max(GOODS_SUMMARY.GOODS_ID).as("goodsId")
            , max(GOODS.GOODS_NAME).as("goodsName")
            , max(GOODS.GOODS_IMG).as("goodsImg")
            , max(GOODS.SHOP_PRICE).as("shopPrice")
            , sum(GOODS_SUMMARY.NEW_USER_NUMBER).as("newUserNumber")
            , sum(GOODS_SUMMARY.OLD_USER_NUMBER).as("oldUserNumber")
            , sum(GOODS_SUMMARY.PV).as("pv")
            , sum(GOODS_SUMMARY.UV).as("uv")
            , sum(GOODS_SUMMARY.CART_UV).as("cartUv")
            , sum(GOODS_SUMMARY.PAID_UV).as("paidUv")
            , sum(GOODS_SUMMARY.PAID_GOODS_NUMBER).as("paidGoodsNumber")
            , sum(GOODS_SUMMARY.GOODS_SALES).as("goodsSales")
            , sum(GOODS_SUMMARY.RECOMMEND_USER_NUM).as("recommendUserNum")
            , sum(GOODS_SUMMARY.COLLECT_USE_NUM).as("collectUserNum")
            , sum(GOODS_SUMMARY.SHARE_PV).as("sharePv")
            , sum(GOODS_SUMMARY.SHARE_UV).as("shareUv")
        )
            .from(GOODS_SUMMARY)
            .leftJoin(GOODS)
            .on(GOODS.GOODS_ID.eq(GOODS_SUMMARY.GOODS_ID));
        PageResult<ProductEffectVo> pageResult = this.getPageResult(createEffectSelect(param, joinStep, baseCondition, false), param.getCurrentPage(), param.getPageRows(), ProductEffectVo.class);
        for (ProductEffectVo vo : pageResult.dataList) {
            vo.setNewUserPercentage(BigDecimalUtil.divideWithOutCheck(vo.getNewUserNumber(), vo.getPaidUv()));
            vo.setOldUserPercentage(BigDecimalUtil.divideWithOutCheck(vo.getOldUserNumber(), vo.getPaidUv()));
            vo.setUv2paidGoods(BigDecimalUtil.divideWithOutCheck(vo.getPaidGoodsNumber(), vo.getUv()));
        }
        return pageResult;
    }

    /**
     * 创建商品效果查询select
     *
     * @param param         the param
     * @param joinStep      the join step
     * @param baseCondition the base condition
     * @param isFixedDay    true为指定时间，false为自定义时间
     * @return select limit step
     */
    private SelectLimitStep<?> createEffectSelect(ProductEffectParam param, SelectJoinStep<?> joinStep, Condition baseCondition, boolean isFixedDay) {
        Optional<String> field = Optional.ofNullable(param.getOrderByField());
        Optional<String> sortType = Optional.ofNullable(param.getOrderByType());
        // 动态排序字段，规则
        SortField<?> sortField = getSortField(field, sortType, isFixedDay);

        // 查询筛选条件，商品品牌，商家分类
        Condition brandCondition = GOODS.BRAND_ID.eq(param.getBrandId());
        Condition sortCondition = GOODS.SORT_ID.eq(param.getSortId());

        //  标签条件
        Condition labelCondtion = GOODS_LABEL_COUPLE.ID.eq(param.getLabelId()).and(GOODS_LABEL_COUPLE.TYPE.eq(BYTE_ONE));

        if (param.getBrandId() > 0) {
            baseCondition = baseCondition.and(brandCondition);
        }
        if (param.getSortId() > 0) {
            baseCondition = baseCondition.and(sortCondition);
        }

        /** 条件连表查询，根据不同的条件构造出不同阶段的select */
        SelectConditionStep<?> conditionStep;/** 构造筛选条件 */
        SelectLimitStep<?> limitStep;/** 完善筛选条件 */

        if (param.getLabelId() > 0) {
            conditionStep = joinStep.leftJoin(GOODS_LABEL_COUPLE)
                .on(GOODS_LABEL_COUPLE.GTA_ID.eq(GOODS_SUMMARY.GOODS_ID))
                .where(labelCondtion);
        } else {
            conditionStep = joinStep.where();
        }
        limitStep = isFixedDay ? conditionStep.and(baseCondition).orderBy(sortField) : conditionStep.and(baseCondition).groupBy(GOODS_SUMMARY.GOODS_ID).orderBy(sortField);
        return limitStep;
    }

    /**
     * Export 2 excel workbook.商品效果数据导出
     *
     * @param param the param
     * @return the workbook
     */
    public Workbook export2Excel(ProductEffectParam param) {
        Condition tempGoodsCondition = DSL.trueCondition();
        if (param.getBrandId() > 0) {
            tempGoodsCondition = tempGoodsCondition.and(GOODS_BRAND.ID.eq(param.getBrandId()));
        }
        if (param.getSortId() > 0) {
            tempGoodsCondition = tempGoodsCondition.and(SORT.SORT_ID.eq(param.getSortId()));
        }
        // 左连接以商品为主，获取商品对应的品牌名称和商家分类名称
        SelectConditionStep<Record6<Integer, String, String, BigDecimal, String, String>> tempGoods = db()
            .select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.SHOP_PRICE, GOODS_BRAND.BRAND_NAME, SORT.SORT_NAME)
            .from(GOODS)
            .leftJoin(GOODS_BRAND).on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID))
            .leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID))
            .where(tempGoodsCondition);

        Condition tempGoodsLabelCondition = DSL.trueCondition();
        SelectConditionStep<Record3<String, Integer, Integer>> tempLabel = db()
            .select(GOODS_LABEL.NAME, GOODS_LABEL.ID, GOODS_LABEL_COUPLE.GTA_ID)
            .from(GOODS_LABEL_COUPLE)
            .leftJoin(GOODS_LABEL).on(GOODS_LABEL_COUPLE.LABEL_ID.eq(GOODS_LABEL.ID))
            .where(GOODS_LABEL_COUPLE.TYPE.eq(BYTE_ONE));
        if (param.getLabelId() > 0) {
            tempGoodsLabelCondition = tempGoodsLabelCondition.and(tempLabel.field(GOODS_LABEL.ID).eq(param.getLabelId()));
        }
        SelectConditionStep<Record2<Integer, String>> tempGoodsLabel = db()
            .select(GOODS.GOODS_ID, tempLabel.field(GOODS_LABEL.NAME)).from(GOODS)
            .leftJoin(tempLabel).on(GOODS.GOODS_ID.eq(tempLabel.field(GOODS_LABEL_COUPLE.GTA_ID)))
            .where(tempGoodsLabelCondition);

        SelectLimitStep<?> limitStep;
        if (param.getDynamicDate() > 0) {
//        全连接：取交集
            limitStep = getStatByDynamicDate(param, tempGoods, tempGoodsLabel);
        } else {
            /** 自定义时间 */
            limitStep = getStatByCustomDatePeriod(param, tempGoods, tempGoodsLabel);
        }
        List<ProductEffectExportVo> exportVos = limitStep.fetchInto(ProductEffectExportVo.class);
        for (ProductEffectExportVo vo : exportVos) {
            vo.setGoodsInfo(vo.getGoodsName() + "  " + vo.getShopPrice());
        }
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(workbook);
        excelWriter.writeModelList(exportVos, ProductEffectExportVo.class);
        return workbook;
    }

    private SelectLimitStep<?> getStatByCustomDatePeriod(ProductEffectParam param, SelectConditionStep<Record6<Integer, String, String, BigDecimal, String, String>> tempGoods, SelectConditionStep<Record2<Integer, String>> tempGoodsLabel) {
        SelectLimitStep<?> limitStep;
        limitStep = db().select(min(GOODS_SUMMARY.GOODS_ID).as("goodsId")
            , min(tempGoods.field(GOODS.GOODS_NAME)).as("goodsName")
            , min(tempGoods.field(GOODS.GOODS_IMG)).as("goodsImg")
            , min(tempGoods.field(GOODS.SHOP_PRICE)).as("shopPrice")
            , min(tempGoods.field(GOODS_BRAND.BRAND_NAME)).as("brandName")
            , min(tempGoods.field(SORT.SORT_NAME)).as("sortName")
            , DSL.groupConcatDistinct(tempGoodsLabel.field(GOODS_LABEL.NAME)).as("name")
            , sum(GOODS_SUMMARY.NEW_USER_NUMBER).as("newUserNumber")
            , sum(GOODS_SUMMARY.OLD_USER_NUMBER).as("oldUserNumber")
            , sum(GOODS_SUMMARY.PV).as("pv")
            , sum(GOODS_SUMMARY.UV).as("uv")
            , sum(GOODS_SUMMARY.CART_UV).as("cartUv")
            , sum(GOODS_SUMMARY.PAID_UV).as("paidUv")
            , sum(GOODS_SUMMARY.PAID_GOODS_NUMBER).as("paidGoodsNumber")
            , sum(GOODS_SUMMARY.GOODS_SALES).as("goodsSales")
            , sum(GOODS_SUMMARY.RECOMMEND_USER_NUM).as("recommendUserNum")
            , sum(GOODS_SUMMARY.COLLECT_USE_NUM).as("collectUserNum")
            , sum(GOODS_SUMMARY.SHARE_PV).as("sharePv")
            , sum(GOODS_SUMMARY.SHARE_UV).as("shareUv")
        )
            .from(GOODS_SUMMARY)
            .innerJoin(tempGoods).on(tempGoods.field(GOODS.GOODS_ID).eq(GOODS_SUMMARY.GOODS_ID))
            .innerJoin(tempGoodsLabel).on(tempGoodsLabel.field(GOODS.GOODS_ID).eq(GOODS_SUMMARY.GOODS_ID))
            .where(GOODS_SUMMARY.REF_DATE.greaterThan(Date.valueOf(param.getStartTime().toLocalDateTime().toLocalDate())))
            .and(GOODS_SUMMARY.REF_DATE.le(Date.valueOf(param.getEndTime().toLocalDateTime().toLocalDate())))
            .and(GOODS_SUMMARY.TYPE.eq(BYTE_ONE))
            .groupBy(GOODS_SUMMARY.GOODS_ID);
        return limitStep;
    }

    private SelectLimitStep<?> getStatByDynamicDate(ProductEffectParam param, SelectConditionStep<Record6<Integer, String, String, BigDecimal, String, String>> tempGoods, SelectConditionStep<Record2<Integer, String>> tempGoodsLabel) {
        SelectLimitStep<?> limitStep;
        limitStep = db().select(min(GOODS_SUMMARY.GOODS_ID).as("goodsId")
            , min(tempGoods.field(GOODS.GOODS_NAME)).as("goodsName")
            , min(tempGoods.field(GOODS.GOODS_IMG)).as("goodsImg")
            , min(tempGoods.field(GOODS.SHOP_PRICE)).as("shopPrice")
            , min(tempGoods.field(GOODS_BRAND.BRAND_NAME)).as("brandName")
            , min(tempGoods.field(SORT.SORT_NAME)).as("sortName")
            , DslPlus.groupConCat(tempGoodsLabel.field(GOODS_LABEL.NAME)).as("name")
            , min(GOODS_SUMMARY.NEW_USER_NUMBER).as("newUserNumber")
            , min(GOODS_SUMMARY.OLD_USER_NUMBER).as("oldUserNumber")
            , min(GOODS_SUMMARY.PV).as("pv")
            , min(GOODS_SUMMARY.UV).as("uv")
            , min(GOODS_SUMMARY.CART_UV).as("cartUv")
            , min(GOODS_SUMMARY.PAID_UV).as("paidUv")
            , min(GOODS_SUMMARY.PAID_GOODS_NUMBER).as("paidGoodsNumber")
            , min(GOODS_SUMMARY.GOODS_SALES).as("goodsSales")
            , min(GOODS_SUMMARY.RECOMMEND_USER_NUM).as("recommendUserNum")
            , min(GOODS_SUMMARY.COLLECT_USE_NUM).as("collectUserNum")
            , min(GOODS_SUMMARY.SHARE_PV).as("sharePv")
            , min(GOODS_SUMMARY.SHARE_UV).as("shareUv")
        )
            .from(GOODS_SUMMARY)
            .innerJoin(tempGoods).on(tempGoods.field(GOODS.GOODS_ID).eq(GOODS_SUMMARY.GOODS_ID))
            .innerJoin(tempGoodsLabel).on(tempGoodsLabel.field(GOODS.GOODS_ID).eq(GOODS_SUMMARY.GOODS_ID))
            .where(GOODS_SUMMARY.REF_DATE.eq(Date.valueOf(LocalDate.now())))
            .and(GOODS_SUMMARY.TYPE.eq(param.getDynamicDate()))
            .groupBy(GOODS_SUMMARY.GOODS_ID);
        return limitStep;
    }

    /**
     * Gets goods ranking.商品排行
     * b2c_goods_summary
     *
     * @param param the param
     * @return the goods ranking
     */
    public RankingVo getGoodsRanking(RankingParam param) {
        if (Objects.isNull(param.getUnit())) {
            return getAllData(param);
        }
        return null;
    }

    private RankingVo getAllData(RankingParam param) {
        // 销售额
        Map<Integer, Result<Record4<Date, Integer, BigDecimal, String>>> sales = getGoodsGroupData(param, GOODS_SUMMARY.GOODS_SALES);
        // 销售订单
        Map<Integer, Result<Record4<Date, Integer, Integer, String>>> salesOrder = getGoodsGroupData(param, GOODS_SUMMARY.PAID_GOODS_NUMBER);

        Map<String, ChartData> salesChart = getAllChartData(sales, param, GOODS_SUMMARY.GOODS_SALES);
        Map<String, ChartData> salesOrderChart = getAllChartData(salesOrder, param, GOODS_SUMMARY.PAID_GOODS_NUMBER);

        return RankingVo.builder()
            .salesChar(salesChart)
            .salesOrderChar(salesOrderChart)
            .salesTable(getAllTableData(param, salesChart))
            .salesOrderTable(getAllTableData(param, salesOrderChart))
            .build();
    }

    /**
     * Gets chart data.使用于：天，月，年
     *
     * @param <T>    the type parameter
     * @param source the source
     * @param param  the param
     * @param field  the field
     * @param unit   the unit
     * @param rule   the rule
     * @return the chart data
     */
    private <T extends Number> ChartData getChartData(Map<Integer, Result<Record4<Date, Integer, T, String>>> source,
                                                      RankingParam param,
                                                      Field<T> field, byte unit,
                                                      BiPredicate<Record4<Date, Integer, T, String>, LocalDate> rule,
                                                      Function<LocalDate, String> func,
                                                      BiPredicate<String, LocalDate> compare) {
        List<String> columns = new ArrayList<String>() {{
            add(STRING_DATE);
        }};
        List<Map<String, Object>> rows = new ArrayList<>();
        source.forEach((k, v) -> {
            columns.add(v.getValue(INTEGER_ZERO, GOODS.GOODS_NAME));
            param.setUnit(unit);
            dayCharData(rows, v, getRefDateList(param), rule, field, func, compare);
        });
        return ChartData.builder().columns(columns).rows(rows).build();
    }

    /**
     * Gets chart data 1.使用于：周
     *
     * @param <T>    the type parameter
     * @param source the source
     * @param param  the param
     * @param field  the field
     * @return the chart data 1
     */
    private <T extends Number> ChartData getChartData1(Map<Integer, Result<Record4<Date, Integer, T, String>>> source,
                                                       RankingParam param,
                                                       Field<T> field) {
        List<String> columns = new ArrayList<String>() {{
            add(STRING_DATE);
        }};
        List<Map<String, Object>> rows = new ArrayList<>();
        source.forEach((k, v) -> {
            columns.add(v.getValue(INTEGER_ZERO, GOODS.GOODS_NAME));
            param.setUnit(BYTE_THREE);
            weekCharData(rows, v, getRefDateList1(param), this::weekRule, field);
        });
        return ChartData.builder().columns(columns).rows(rows).build();
    }

    private <T extends Number> Map<String, ChartData> getAllChartData(Map<Integer, Result<Record4<Date, Integer, T, String>>> source,
                                                                      RankingParam param,
                                                                      Field<T> field) {
        List<String> columns = new ArrayList<String>() {{
            add(STRING_DATE);
        }};
        List<Map<String, Object>> dayRows = new ArrayList<>();
        List<Map<String, Object>> weekRows = new ArrayList<>();
        List<Map<String, Object>> monthRows = new ArrayList<>();
        List<Map<String, Object>> yearRows = new ArrayList<>();

        source.forEach((k, v) -> {
            columns.add(v.getValue(INTEGER_ZERO, GOODS.GOODS_NAME));
            param.setUnit(BYTE_ZERO);
            dayCharData(dayRows, v, getRefDateList(param), this::dayRule, field, this::dayDate, this::dayCompare);
            weekCharData(weekRows, v, getRefDateList1(param), this::weekRule, field);
            param.setUnit(BYTE_ONE);
            dayCharData(monthRows, v, getRefDateList(param), this::monthRule, field, this::monthDate, this::monthCompare);
            param.setUnit(BYTE_TWO);
            dayCharData(yearRows, v, getRefDateList(param), this::yearRule, field, this::yearDate, this::yearCompare);
        });
        return new HashMap<String, ChartData>(4) {{
            put(DAY_CHAR_DATA, ChartData.builder().columns(columns).rows(dayRows).build());
            put(WEEK_CHAR_DATA, ChartData.builder().columns(columns).rows(weekRows).build());
            put(MONTH_CHAR_DATA, ChartData.builder().columns(columns).rows(monthRows).build());
            put(YEAR_CHAR_DATA, ChartData.builder().columns(columns).rows(yearRows).build());
        }};
    }

    private TableData getTableData(RankingParam param, List<Map<String, Object>> rows, Function<LocalDate, String> func) {
        Set<String> goodsName = rows.stream().findAny().orElse(EMPTY_MAP).keySet()
            .stream().filter(e -> !STRING_DATE.equals(e)).collect(Collectors.toSet());
        List<String> refDate;
        if (BYTE_THREE.equals(param.getUnit())) {
            refDate = getRefDateList1(param).stream().map(Object::toString).collect(Collectors.toList());
        } else {
            refDate = getRefDateList(param).stream().map(func).collect(Collectors.toList());
        }
        return TableData.builder().refDate(refDate).goodsName(goodsName).arrayData(getTableArrayData(rows)).build();
    }

    private Object[][] getTableArrayData(final List<Map<String, Object>> rows) {

        Object[][] list = new Object[rows.size()][];

        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> tempMap = rows.get(i);
            Object dateValue = tempMap.get(STRING_DATE);
            tempMap.remove(STRING_DATE);
            list[i] = tempMap.values().toArray();
            tempMap.put(STRING_DATE, dateValue);
        }
        return swapHv(list);
    }


    /**
     * 二维数组行列转换
     * @param array
     * @return
     */
    private Object[][] swapHv(Object[][] array) {
        if (array.length <= 0) {
            return array;
        }
        Object[][] result = new Object[array[0].length][array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                result[j][i] = array[i][j];
            }
        }
        return result;
    }

    public Map<String, TableData> getAllTableData(RankingParam param, Map<String, ChartData> chartData) {
        Map<String, TableData> map = new HashMap<>(4);
        param.setUnit(BYTE_ZERO);
        map.put(DAY_TABLE_DATA, getTableData(param, chartData.get(DAY_CHAR_DATA).getRows(), this::dayDate));
        param.setUnit(BYTE_THREE);
        map.put(WEEK_TABLE_DATA, getTableData(param, chartData.get(WEEK_CHAR_DATA).getRows(), this::dayDate));
        param.setUnit(BYTE_ONE);
        map.put(MONTH_TABLE_DATA, getTableData(param, chartData.get(MONTH_CHAR_DATA).getRows(), this::monthDate));
        param.setUnit(BYTE_TWO);
        map.put(YEAR_TABLE_DATA, getTableData(param, chartData.get(YEAR_CHAR_DATA).getRows(), this::yearDate));
        return map;
    }

    /**
     * Gets goods group data.获取商品销售额/销售订单排行前10的分组数据
     *
     * @param <T>   the type parameter
     * @param param the param
     * @param field the field
     * @return the goods group data
     */
    private <T extends Number> Map<Integer, Result<Record4<Date, Integer, T, String>>> getGoodsGroupData(RankingParam param, Field<T> field) {
        // 获取销售额/销售订单排行前10的商品id
        Map<Integer, Result<Record1<Integer>>> ids = db()
            .select(min(GOODS_SUMMARY.GOODS_ID))
            .from(GOODS_SUMMARY)
            .where(GOODS_SUMMARY.REF_DATE.greaterThan(param.getStartTime()))
            .and(GOODS_SUMMARY.REF_DATE.le(param.getEndTime()))
            .and(GOODS_SUMMARY.TYPE.eq(BYTE_ONE))
            .groupBy(GOODS_SUMMARY.GOODS_ID)
            .orderBy(sum(field).desc())
            .limit(10)
            .fetchGroups(min(GOODS_SUMMARY.GOODS_ID));

        Set<Integer> goodsIds = ids.keySet();
        log.debug("销售额/销售订单排行前10的商品id为：{}", Util.toJson(goodsIds));

        Map<Integer, Result<Record4<Date, Integer, T, String>>> sales = db()
            .select(GOODS_SUMMARY.REF_DATE.sub(INTEGER_ONE), GOODS_SUMMARY.GOODS_ID, field, GOODS.GOODS_NAME)
            .from(GOODS_SUMMARY)
            .leftJoin(GOODS).on(GOODS_SUMMARY.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(GOODS_SUMMARY.REF_DATE.greaterThan(param.getStartTime()))
            .and(GOODS_SUMMARY.REF_DATE.le(param.getEndTime()))
            .and(GOODS_SUMMARY.TYPE.eq(BYTE_ONE))
            .and(GOODS_SUMMARY.GOODS_ID.in(goodsIds))
            .orderBy(GOODS_SUMMARY.REF_DATE.sub(INTEGER_ONE))
            .fetchGroups(GOODS_SUMMARY.GOODS_ID);
        return sales;
    }

    private List<LocalDate> getRefDateList(RankingParam param) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate start;
        LocalDate end;
        switch (param.getUnit()) {
            case 1:
                // 月
                start = param.getStartTime().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
                end = param.getEndTime().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
                for (LocalDate date = start;
                     date.isBefore(end.plusMonths(INTEGER_ONE));
                     date = date.plusMonths(INTEGER_ONE)) {
                    result.add(date);
                }
                return result;
            case 2:
                // 年
                start = param.getStartTime().toLocalDate().with(TemporalAdjusters.firstDayOfYear());
                end = param.getEndTime().toLocalDate().with(TemporalAdjusters.firstDayOfYear());
                for (LocalDate date = start;
                     date.isBefore(end.plusYears(INTEGER_ONE));
                     date = date.plusYears(INTEGER_ONE)) {
                    result.add(date);
                }
                return result;
            default:
                // 0天
                for (LocalDate date = param.getStartTime().toLocalDate();
                     date.isBefore(param.getEndTime().toLocalDate());
                     date = date.plusDays(INTEGER_ONE)) {
                    result.add(date);
                }
                return result;
        }
    }

    private List<Tuple2<LocalDate, LocalDate>> getRefDateList1(RankingParam param) {
        List<Tuple2<LocalDate, LocalDate>> result = new ArrayList<>();
        // 周
        for (LocalDate date = param.getStartTime().toLocalDate();
             date.isBefore(param.getEndTime().toLocalDate());
             date = date.plusDays(7)) {
            result.add(new Tuple2<>(date, date.plusDays(7).compareTo(param.getEndTime().toLocalDate()) > 0 ? param.getEndTime().toLocalDate() : date.plusDays(7)));
        }
        return result;
    }

    private <T extends Number> boolean dayRule(Record4<Date, Integer, T, String> t, LocalDate u) {
        return t.value1().toLocalDate().compareTo(u) == 0;
    }

    private <T extends Number> boolean weekRule(Record4<Date, Integer, T, String> t, Tuple2<LocalDate, LocalDate> u) {
        return t.value1().toLocalDate().compareTo(u.v1()) > 0 &&
            t.value1().toLocalDate().compareTo(u.v2()) < 0;
    }

    private <T extends Number> boolean monthRule(Record4<Date, Integer, T, String> t, LocalDate u) {
        return t.value1().toLocalDate().getYear() == u.getYear() &&
            t.value1().toLocalDate().getMonth() == u.getMonth();
    }

    private <T extends Number> boolean yearRule(Record4<Date, Integer, T, String> t, LocalDate u) {
        LocalDate tDate = t.value1().toLocalDate();
        return tDate.getYear() == u.getYear();
    }

    /**
     * 构造每天/月/年的图形数据
     * @param rows
     * @param results
     * @param showDate
     * @param rule
     * @param field
     * @param func
     * @param compare
     * @param <T>
     */
    private <T extends Number> void dayCharData(List<Map<String, Object>> rows,
                                                final Result<Record4<Date, Integer, T, String>> results,
                                                List<LocalDate> showDate,
                                                BiPredicate<Record4<Date, Integer, T, String>, LocalDate> rule,
                                                Field<T> field,
                                                Function<LocalDate, String> func,
                                                BiPredicate<String, LocalDate> compare) {
        String name = results.get(INTEGER_ZERO).getValue(GOODS.GOODS_NAME);
        AtomicBoolean flag = new AtomicBoolean(false);
        showDate.forEach(d -> {
            Optional<Map<String, Object>> optional = rows.stream().filter(m -> compare.test(m.get(STRING_DATE).toString(), d)).findFirst();
            Map<String, Object> map;
            if (optional.isPresent()) {
                map = optional.get();
            } else {
                map = new HashMap<>();
                flag.set(true);
            }
            List<Record4<Date, Integer, T, String>> temp = results.stream().filter(e -> rule.test(e, d)).collect(Collectors.toList());
            map.putIfAbsent(STRING_DATE, func.apply(d));
            Class<T> clazz = field.getType();
            if (clazz.equals(BigDecimal.class)) {
                map.put(name, temp.stream().map(e -> e.getValue(field)).map(Number::doubleValue).reduce(DOUBLE_ZERO, Double::sum));
            } else if (clazz.equals(Integer.class)) {
                map.put(name, temp.stream().map(e -> e.getValue(field)).map(Number::intValue).reduce(INTEGER_ZERO, Integer::sum));
            } else {
                map.put(name, STRING_ZERO);
            }
            if (flag.get()) {
                rows.add(map);
            }
            flag.set(false);
        });
    }

    private String dayDate(LocalDate date) {
        return date.toString();
    }

    private boolean dayCompare(String s, LocalDate date) {
        return s.equals(dayDate(date));
    }

    private String monthDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private boolean monthCompare(String s, LocalDate date) {
        return s.equals(monthDate(date));
    }

    private String yearDate(LocalDate date) {
        return String.valueOf(date.getYear());
    }

    private boolean yearCompare(String s, LocalDate date) {
        return s.equals(yearDate(date));
    }

    /**
     * 构造每周的图形数据
     * @param rows
     * @param results
     * @param showDate
     * @param rule
     * @param field
     * @param <T>
     */
    private <T extends Number> void weekCharData(List<Map<String, Object>> rows,
                                                 final Result<Record4<Date, Integer, T, String>> results,
                                                 List<Tuple2<LocalDate, LocalDate>> showDate,
                                                 BiPredicate<Record4<Date, Integer, T, String>, Tuple2<LocalDate, LocalDate>> rule,
                                                 Field<T> field) {
        String name = results.get(INTEGER_ZERO).getValue(GOODS.GOODS_NAME);
        AtomicBoolean flag = new AtomicBoolean(false);
        showDate.forEach(d -> {
            Optional<Map<String, Object>> optional = rows.stream().filter(m -> m.get(STRING_DATE).toString().equals(d.v1() + "~" + d.v2())).findFirst();
            Map<String, Object> map;
            if (optional.isPresent()) {
                map = optional.get();
            } else {
                map = new HashMap<>();
                flag.set(true);
            }
            List<Record4<Date, Integer, T, String>> temp = results.stream().filter(e -> rule.test(e, d)).collect(Collectors.toList());
            map.putIfAbsent(STRING_DATE, d.v1() + "~" + d.v2());
            Class<T> clazz = field.getType();
            if (clazz.equals(BigDecimal.class)) {
                map.put(name, temp.stream().map(e -> e.getValue(field)).map(Number::doubleValue).reduce(DOUBLE_ZERO, Double::sum));
            } else if (clazz.equals(Integer.class)) {
                map.put(name, temp.stream().map(e -> e.getValue(field)).map(Number::intValue).reduce(INTEGER_ZERO, Integer::sum));
            } else {
                map.put(name, STRING_ZERO);
            }
            if (flag.get()) {
                rows.add(map);
            }
            flag.set(false);
        });
    }

    /**
     * Rank export workbook.商品排行导出
     *
     * @param param the param
     * @return the workbook
     */
    public Workbook rankExport(RankingParam param) {
        List<GoodsEffectExportVo> list;
        if (BYTE_ZERO.equals(param.getFlag())) {
            // 销售额
            Map<Integer, Result<Record4<Date, Integer, BigDecimal, String>>> sales = getGoodsGroupData(param, GOODS_SUMMARY.GOODS_SALES);
            list = getExportVo(sales, param, GOODS_SUMMARY.GOODS_SALES);
        } else {
            // 销售订单
            Map<Integer, Result<Record4<Date, Integer, Integer, String>>> salesOrder = getGoodsGroupData(param, GOODS_SUMMARY.PAID_GOODS_NUMBER);
            list = getExportVo(salesOrder, param, GOODS_SUMMARY.PAID_GOODS_NUMBER);

        }
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(workbook);
        excelWriter.writeModelListWithDynamicColumn(list, GoodsEffectExportVo.class);
        return workbook;
    }

    private <T extends Number> List<GoodsEffectExportVo> getExportVo(Map<Integer, Result<Record4<Date, Integer, T, String>>> source,
                                                                     RankingParam param, Field<T> field) {
        if (MapUtils.isEmpty(source)) {
            return new ArrayList<>();
        }
        ChartData chartData;
        List<GoodsEffectExportVo> exportVos = new ArrayList<>();
        switch (param.getUnit()) {
            case 1:
                chartData = getChartData(source, param, field, param.getUnit(), this::monthRule, this::monthDate, this::monthCompare);
                break;
            case 2:
                chartData = getChartData(source, param, field, param.getUnit(), this::yearRule, this::yearDate, this::yearCompare);
                break;
            case 3:
                chartData = getChartData1(source, param, field);
                break;
            default:
                chartData = getChartData(source, param, field, param.getUnit(), this::dayRule, this::dayDate, this::dayCompare);
                break;
        }
        chartData.getRows().forEach(e -> {
            String date = e.get(STRING_DATE).toString();
            AtomicBoolean flag = new AtomicBoolean(false);
            e.forEach((k, v) -> {
                if (STRING_DATE.equals(k)) {
                    return;
                }
                GoodsEffectExportVo exportVo;
                Optional<GoodsEffectExportVo> optional = exportVos.stream().filter((t) -> filter(k, t)).findFirst();
                if (optional.isPresent()) {
                    exportVo = optional.get();
                } else {
                    exportVo = new GoodsEffectExportVo();
                    flag.set(true);
                    exportVo.setGoodsName(k);
                }
                LinkedHashMap<String, Object> map = exportVo.getDynamicValue();
                if (Objects.isNull(map)) {
                    map = new LinkedHashMap<>();
                }
                map.put(date, v);
                exportVo.setDynamicValue(map);
                if (flag.get()) {
                    exportVos.add(exportVo);
                }
            });
        });
        return exportVos;
    }

    private boolean filter(String source, GoodsEffectExportVo target) {
        return source.equals(target.getGoodsName());
    }
}
