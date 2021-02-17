package com.meidianyi.shop.service.shop.task.overview;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.*;
import com.meidianyi.shop.db.shop.tables.records.GoodsOverviewSummaryRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.commodity.ProductOverviewParam;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static com.meidianyi.shop.db.shop.tables.GoodsSummary.GOODS_SUMMARY;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.db.shop.tables.Cart.CART;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.Sort.SORT;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.CONDITION_THREE;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.*;

/**
 * The type Goods task service.
 *
 * @author liufei
 * @date 11 /18/19 商品统计定时任务统计信息
 */
@Service
public class GoodsStatisticTaskService extends ShopBaseService {
    /**
     * The constant BAK.
     */
    public static final GoodsBak BAK = GoodsBak.GOODS_BAK.as("BAK");
    /**
     * The constant LABEL.
     */
    public static final GoodsLabelCouple LABEL = GoodsLabelCouple.GOODS_LABEL_COUPLE.as("LABEL");
    /**
     * The constant USER_GR.
     */
    public static final UserGoodsRecord USER_GR = UserGoodsRecord.USER_GOODS_RECORD.as("USER_GR");
    /**
     * The constant ORDER_G.
     */
    public static final OrderGoods ORDER_G = OrderGoods.ORDER_GOODS.as("ORDER_G");
    /**
     * The constant ORDER_I.
     */
    public static final OrderInfo ORDER_I = OrderInfo.ORDER_INFO.as("ORDER_I");
    /**
     * The constant WX_SR.
     */
    public static final WxShoppingRecommend WX_SR = WxShoppingRecommend.WX_SHOPPING_RECOMMEND.as("WX_SR");
    /**
     * The constant COLLECTION.
     */
    public static final UserCollection COLLECTION = UserCollection.USER_COLLECTION.as("COLLECTION");

    /**
     * The constant SHARE.
     */
    public static final ShareRecord SHARE = ShareRecord.SHARE_RECORD.as("SHARE");

    /**
     * Condition builder condition.
     *
     * @param param the param
     * @return the condition
     */
    private Condition conditionBuilder(ProductOverviewParam param) {
        Condition extCondition = DSL.trueCondition();
        if (param.getBrandId() > 0) {
            // 品牌条件
            extCondition = extCondition.and(GOODS.BRAND_ID.eq(param.getBrandId()));
        }
        if (param.getSortId() > 0) {
            List<Integer> sortIds = new ArrayList<>();
            sortIds.add(param.getSortId());
            Byte hasChild = db().select(SORT.HAS_CHILD)
                .from(SORT)
                .where(SORT.SORT_ID.eq(param.getSortId()))
                .fetchOptionalInto(Byte.class).orElse(BYTE_ZERO);
            if (BYTE_ONE.equals(hasChild)){
                List<Integer> childIds = db().select(SORT.SORT_ID)
                    .from(SORT)
                    .where(SORT.PARENT_ID.eq(param.getSortId()))
                    .fetchInto(Integer.class);
                if (null!=childIds&&childIds.size()>0){
                    sortIds.addAll(childIds);
                }
            }
            // 商家分类条件
            extCondition = extCondition.and(GOODS.SORT_ID.in(sortIds));
        }
        if (param.getLabelId() > 0) {
            // 标签条件
            extCondition = extCondition.and(LABEL.LABEL_ID.eq(param.getLabelId()).and(LABEL.TYPE.eq(BYTE_ONE)));
        }
        return extCondition;
    }

    /**
     * 在架商品数
     *
     * @param param the param
     * @return the int
     */
    public int getSaleGoodsNumber(ProductOverviewParam param) {
        // 基本筛选条件 : 备份时间当天, 商品数量大于0, 在架状态 TODO 备份时间不确定
        Condition baseCondition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
            .and(GOODS.GOODS_NUMBER.greaterThan(INTEGER_ZERO))
            .and(GOODS.IS_ON_SALE.eq(BYTE_ONE))
            .and(GOODS.SALE_TIME.lessThan(param.getStartTime()));

        if (param.getLabelId() > 0) {
            return db().select(countDistinct(GOODS.GOODS_ID)).from(GOODS)
                .leftJoin(LABEL).on(GOODS.GOODS_ID.eq(LABEL.GTA_ID))
                .where(baseCondition).and(conditionBuilder(param))
                .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
        } else {
            return db().select(countDistinct(GOODS.GOODS_ID))
                .from(GOODS).where(baseCondition).and(conditionBuilder(param))
                .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
        }
    }

    /**
     * Common builder int.
     * 被访问商品数, 商品UV数, 商品PV数 公共查询条件
     *
     * @param param the param
     * @param field the field
     * @return the int
     */
    private Map<Integer, Integer> commonBuilder(ProductOverviewParam param, Field<?> field) {
        // 基本筛选条件
        Condition baseCondition = USER_GR.CREATE_TIME.greaterOrEqual(param.getStartTime())
            .and(USER_GR.CREATE_TIME.lessThan(param.getEndTime()));
        SelectJoinStep<?> joinStep = db().select(min(USER_GR.GOODS_ID), cast(field, Integer.class)).from(USER_GR);
        return selectBuilder(param, joinStep, USER_GR.GOODS_ID, baseCondition, field);
    }

    /**
     * 被访问商品数
     *
     * @param param the param
     * @return the int
     */
    public int getGoodsNumByVisit(ProductOverviewParam param) {
        return commonBuilder(param, countDistinct(USER_GR.GOODS_ID)).size();
    }

    /**
     * 商品UV数
     *
     * @param param the param
     * @return the int
     */
    public int getGoodsUv(ProductOverviewParam param) {
        return getSingleGoodsUv(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的商品UV数
     *
     * @param param the param
     * @return the int
     */
    public Map<Integer, Integer> getSingleGoodsUv(ProductOverviewParam param) {
        return commonBuilder(param, countDistinct(USER_GR.USER_ID));
    }

    /**
     * 总商品PV数
     *
     * @param param the param
     * @return the goods pv
     */
    public int getGoodsPv(ProductOverviewParam param) {
        return getSingleGoodsPv(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的商品PV数
     *
     * @param param the param
     * @return the goods pv
     */
    public Map<Integer, Integer> getSingleGoodsPv(ProductOverviewParam param) {
        return commonBuilder(param, sum(USER_GR.COUNT));
    }

    /**
     * 总加购人数
     *
     * @param param the param
     * @return the int
     */
    public int addCartUserNum(ProductOverviewParam param) {
        logger().info("开始计算加购人数");
        Map<Integer,Integer> addCartUser = addSingleCartUserNum(param);
        return addCartUser.values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的加购人数
     *
     * @param param the param
     * @return the int
     */
    public Map<Integer, Integer> addSingleCartUserNum(ProductOverviewParam param) {
        return commonBuilder1(param, countDistinct(CART.USER_ID));
    }

    /**
     * 总加购件数
     *
     * @param param the param
     * @return the add cart goods number
     */
    public int getAddCartGoodsNumber(ProductOverviewParam param) {
        return getSingleAddCartGoodsNumber(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的加购件数
     *
     * @param param the param
     * @return the add cart goods number
     */
    public Map<Integer, Integer> getSingleAddCartGoodsNumber(ProductOverviewParam param) {
        return commonBuilder1(param, sum(CART.CART_NUMBER));
    }

    /**
     * Common builder 1 int.
     * 加购人数, 加购件数 公共查询条件
     *
     * @param param the param
     * @param field the field
     * @return the int
     */
    private Map<Integer, Integer> commonBuilder1(ProductOverviewParam param, Field<?> field) {
        // 基本筛选条件
        Condition baseCondition = CART.CREATE_TIME.greaterOrEqual(param.getStartTime())
            .and(CART.CREATE_TIME.lessThan(param.getEndTime()));
        SelectJoinStep<?> joinStep = db().select(min(CART.GOODS_ID), cast(field, Integer.class)).from(CART);
        return selectBuilder(param, joinStep, CART.GOODS_ID, baseCondition, field);
    }

    /**
     * Select builder int.
     *
     * @param param         the param
     * @param joinStep      the join step
     * @param field         the field
     * @param baseCondition the base condition
     * @param field1        the field 1
     * @return the int
     */
    private Map<Integer, Integer> selectBuilder(ProductOverviewParam param, SelectJoinStep<?> joinStep, Field<Integer> field, Condition baseCondition, Field<?> field1) {
        if (param.getBrandId() > 0 || param.getSortId() > 0) {
            joinStep = joinStep.leftJoin(GOODS).on(field.eq(GOODS.GOODS_ID));
        }
        if (param.getLabelId() > 0) {
            joinStep = joinStep.leftJoin(LABEL).on(LABEL.GTA_ID.eq(field));
        }
//        return joinStep.where(baseCondition).and(conditionBuilder(param)).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
        return joinStep.where(baseCondition).and(conditionBuilder(param)).groupBy(field).fetchMap(min(field), cast(field1, Integer.class));
    }

    /**
     * 总付款商品数
     *
     * @param param the param
     * @return the int
     */
    public int paidGoodsNum(ProductOverviewParam param) {
        return singlePaidGoodsNum(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的付款商品数
     *
     * @param param the param
     * @return the int
     */
    public Map<Integer, Integer> singlePaidGoodsNum(ProductOverviewParam param) {
        return commonBuilder2(param, sum(ORDER_G.GOODS_NUMBER), EXT_CONDITION);
    }

    /**
     * 总付款人数
     *
     * @param param the param
     * @return the int
     */
    public int paidUv(ProductOverviewParam param) {
        return singlePaidUv(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的付款人数
     *
     * @param param the param
     * @return the int
     */
    public Map<Integer, Integer> singlePaidUv(ProductOverviewParam param) {
        return commonBuilder2(param, count(ORDER_I.USER_ID), EXT_CONDITION);
    }

    /**
     * 总下单商品数
     *
     * @param param the param
     * @return the int
     */
    public int getPayOrderGoodsNum(ProductOverviewParam param) {
        return getSinglePayOrderGoodsNum(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 每件单一商品的下单商品数
     *
     * @param param the param
     * @return the int
     */
    public Map<Integer, Integer> getSinglePayOrderGoodsNum(ProductOverviewParam param) {
        return commonBuilder2(param, sum(ORDER_G.GOODS_NUMBER), TRUE_CONDITION);
    }

    /**
     * 动销商品数(统计时间内，销量不为0的商品数量)
     *
     * @param param the param
     * @return the int
     */
    public int getDySoldGoodsNum(ProductOverviewParam param) {
        return commonBuilder2(param, count(ORDER_G.GOODS_ID), EXT_CONDITION).size();
    }

    /**
     * The constant EXT_CONDITION.
     */
    private static final Condition EXT_CONDITION = ORDER_I.ORDER_STATUS.greaterOrEqual(CONDITION_THREE);
    /**
     * The constant TRUE_CONDITION.
     */
    public static final Condition TRUE_CONDITION = DSL.trueCondition();

    /**
     * Common builder 2 int.
     * 动销商品数, 下单商品数, 付款商品数 公共查询条件(排除货到付款非发货)
     *
     * @param param        the param
     * @param field        the field
     * @param extCondition the ext condition
     * @return the int
     */
    private Map<Integer, Integer> commonBuilder2(ProductOverviewParam param, Field<?> field, Condition extCondition) {
        Condition baseConditon = ORDER_I.CREATE_TIME.greaterOrEqual(param.getStartTime())
            .and(ORDER_I.CREATE_TIME.lessThan(param.getEndTime()))
            .and(ORDER_I.IS_COD.eq(BYTE_ZERO).or(ORDER_I.IS_COD.eq(BYTE_ONE).and(ORDER_I.SHIPPING_TIME.isNotNull())))
            .and(ORDER_I.ORDER_SN.notEqual(ORDER_I.MAIN_ORDER_SN))
            .and(extCondition);
        SelectJoinStep<?> joinStep = db().select(min(ORDER_G.GOODS_ID), cast(field, Integer.class)).from(ORDER_G)
            .leftJoin(ORDER_I).on(ORDER_G.ORDER_SN.eq(ORDER_I.ORDER_SN));
        return selectBuilder(param, joinStep, ORDER_G.GOODS_ID, baseConditon, field);
    }

    /**
     * The constant TYPE_LIST.
     */
    public static final List<Byte> TYPE_LIST = new ArrayList<Byte>() {{
        add(Byte.valueOf("1"));
        add(Byte.valueOf("7"));
        add(Byte.valueOf("30"));
    }};

    public static final List<Byte> TYPE_LIST_1 = new ArrayList<Byte>() {{
        add(Byte.valueOf("1"));
        add(Byte.valueOf("7"));
        add(Byte.valueOf("30"));
        add(Byte.valueOf("90"));
    }};

    /**
     * Insert overview.
     * 往表b2c_goods_overview_summary中插入统计数据(昨天, 前一星期, 前一个月)
     */
    public void insertOverview() {
        TYPE_LIST.forEach((e) -> db().executeInsert(createOverviewRecord(new ProductOverviewParam() {{
            setDynamicDate(e);
            setStartTime(Timestamp.valueOf(LocalDate.now().minusDays(e).atStartOfDay()));
            setEndTime(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }})));
    }

    /**
     * Create overview record goods overview summary record.
     *
     * @param param the param
     * @return the goods overview summary record
     */
    public GoodsOverviewSummaryRecord createOverviewRecord(ProductOverviewParam param) {
        return new GoodsOverviewSummaryRecord() {{
            setRefDate(DateUtils.yyyyMmDdDate(LocalDate.now()));
            setType(param.getDynamicDate());
            setOnShelfGoodsNum(getSaleGoodsNumber(param));
            setSoldGoodsNum(getDySoldGoodsNum(param));
            setVisitedGoodsNum(getGoodsNumByVisit(param));
            setGoodsUserVisit(getGoodsUv(param));
            setGoodsPageviews(getGoodsPv(param));
            setPurchaseNum(addCartUserNum(param));
            setPurchaseQuantity(getAddCartGoodsNumber(param));
            setPaidGoodsNum(paidGoodsNum(param));
            setOrderGoodsNum(getPayOrderGoodsNum(param));
        }};
    }

    /**
     * 所有商品的总推荐用户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int recommendUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(countDistinct(WX_SR.USER_ID)).from(WX_SR).where(WX_SR.CREATE_TIME.greaterOrEqual(startTime))
            .and(WX_SR.CREATE_TIME.lessOrEqual(endTime)).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * Single recommend user num.每件单一商品的推荐用户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> singleRecommendUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(min(WX_SR.GOODS_ID), countDistinct(WX_SR.USER_ID)).from(WX_SR).where(WX_SR.CREATE_TIME.greaterOrEqual(startTime))
            .and(WX_SR.CREATE_TIME.lessOrEqual(endTime))
            .groupBy(WX_SR.GOODS_ID)
            .fetchMap(min(WX_SR.GOODS_ID), countDistinct(WX_SR.USER_ID));
    }

    /**
     * 所有商品的总收藏人数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int collectUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(countDistinct(COLLECTION.USER_ID)).from(COLLECTION).where(COLLECTION.CREATE_TIME.greaterOrEqual(startTime))
            .and(COLLECTION.CREATE_TIME.lessOrEqual(endTime)).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * 每件单一商品的收藏人数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> singleCollectUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(min(COLLECTION.GOODS_ID), countDistinct(COLLECTION.USER_ID)).from(COLLECTION).where(COLLECTION.CREATE_TIME.greaterOrEqual(startTime))
            .and(COLLECTION.CREATE_TIME.lessOrEqual(endTime))
            .groupBy(COLLECTION.GOODS_ID)
            .fetchMap(min(COLLECTION.GOODS_ID), countDistinct(COLLECTION.USER_ID));
    }

    /**
     * The constant STATUS_CONDITION. 订单状态大于３的，排除货到付款非发货
     */
    public static final Condition STATUS_CONDITION = ORDER_I.ORDER_STATUS.ge(CONDITION_THREE).or(ORDER_I.ORDER_STATUS.eq(BYTE_ZERO).and(ORDER_I.BK_ORDER_PAID.greaterThan(BYTE_ZERO)))
        .and(ORDER_I.IS_COD.eq(BYTE_ZERO).or(ORDER_I.IS_COD.eq(BYTE_ONE).and(ORDER_I.SHIPPING_TIME.isNotNull())));

    /**
     * (新/老)成交客户数(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return \Illuminate\Database\Query\Builder
     */
    public Result<Record2<Integer, Integer>> customerTransactionNum(Timestamp startTime, Timestamp endTime) {
        // 限制时间范围内的成交客户
        Condition normalTime = ORDER_I.CREATE_TIME.ge(startTime).and(ORDER_I.CREATE_TIME.le(endTime));

        return db().select(ORDER_G.GOODS_ID, ORDER_I.USER_ID).from(ORDER_G)
            .leftJoin(ORDER_I).on(ORDER_G.ORDER_SN.eq(ORDER_I.ORDER_SN))
            .where(normalTime)
            .and(STATUS_CONDITION)
            .fetch();
    }

    /**
     * Customer his tran num set.历史成交客户集
     *
     * @param time the time
     * @return the set
     */
    public Set<Integer> customerHisTranNum(Timestamp time) {
        // 指定历史范围成交客户
        Condition conditionTime = ORDER_I.CREATE_TIME.lessThan(time);
        // 获取指定历史范围成交客户集
        return db().selectDistinct(ORDER_I.USER_ID).from(ORDER_I).where(conditionTime).and(STATUS_CONDITION).fetchSet(ORDER_I.USER_ID);
    }

    /**
     * Total customer tran num set.获取指定时间范围内总成交客户数（付款用户数）（可细分为新/老成交客户数）
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the set
     */
    public Set<Integer> totalCustomerTranNum(Timestamp startTime, Timestamp endTime) {
        return new HashSet<>(customerTransactionNum(startTime, endTime).getValues(ORDER_I.USER_ID));
    }

    /**
     * Total customer tran num set.获取指定时间范围内总成交人数（付款人数）（可细分为新/老成交客户数）
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the set
     */
    public int totalPeopleTranNum(Timestamp startTime, Timestamp endTime) {
        return customerTransactionNum(startTime, endTime).getValues(ORDER_I.USER_ID).size();
    }

    /**
     * Single goods customer tran num map.获取指定时间范围内每个单一商品的成交客户数（可细分为新/老成交客户数）
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, List<Integer>> singleGoodsCustomerTranNum(Timestamp startTime, Timestamp endTime) {
        return customerTransactionNum(startTime, endTime).intoGroups(ORDER_G.GOODS_ID, ORDER_I.USER_ID);
    }

    /**
     * New customer tran num int.全部商品的新成交客户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int newCustomerTranNum(Timestamp startTime, Timestamp endTime) {
        Set<Integer> result = totalCustomerTranNum(startTime, endTime);
        // 差集
        result.removeAll(customerHisTranNum(startTime));
        return result.size();
    }

    /**
     * Single new customer tran num.每件单一商品的新成交客户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> singleNewCustomerTranNum(Timestamp startTime, Timestamp endTime) {
        Map<Integer, List<Integer>> temp = singleGoodsCustomerTranNum(startTime, endTime);
        return new HashMap<Integer, Integer>(temp.size()) {{
            temp.forEach((k, v) -> {
                Set<Integer> r = new HashSet<>(v);
                r.removeAll(customerHisTranNum(startTime));
                put(k, r.size());
            });
        }};
    }

    /**
     * Old customer tran num int.全部商品的旧成交客户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int oldCustomerTranNum(Timestamp startTime, Timestamp endTime) {
        Set<Integer> result = totalCustomerTranNum(startTime, endTime);
        // 交集
        result.retainAll(customerHisTranNum(startTime));
        return result.size();
    }

    /**
     * Single old customer tran num.每件单一商品的旧成交客户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> singleOldCustomerTranNum(Timestamp startTime, Timestamp endTime) {
        Map<Integer, List<Integer>> temp = singleGoodsCustomerTranNum(startTime, endTime);
        return new HashMap<Integer, Integer>(temp.size()) {{
            temp.forEach((k, v) -> {
                Set<Integer> r = new HashSet<>(v);
                r.retainAll(customerHisTranNum(startTime));
                put(k, r.size());
            });
        }};
    }

    /**
     * Sales map.每件单一商品的商品的销售额
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, BigDecimal> singleGoodsSales(Timestamp startTime, Timestamp endTime) {
        Condition normalTime = ORDER_I.CREATE_TIME.ge(startTime).and(ORDER_I.CREATE_TIME.le(endTime));

        return db().select(min(ORDER_G.GOODS_ID), sum(ORDER_G.DISCOUNTED_GOODS_PRICE)).from(ORDER_G)
            .leftJoin(ORDER_I).on(ORDER_G.ORDER_SN.eq(ORDER_I.ORDER_SN))
            .where(normalTime)
            .and(STATUS_CONDITION)
            .groupBy(ORDER_G.GOODS_ID)
            .fetchMap(min(ORDER_G.GOODS_ID), sum(ORDER_G.DISCOUNTED_GOODS_PRICE));
    }

    /**
     * Single goods sales result.商品的总销售额
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the result
     */
    public BigDecimal goodsSales(Timestamp startTime, Timestamp endTime) {
        return singleGoodsSales(startTime, endTime).values().stream().reduce(BIGDECIMAL_ZERO, BigDecimal::add);
    }

    /**
     * Get single share goods pv map.获得每件单一商品的商品分享pv
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> getSingleShareGoodsPv(Timestamp startTime, Timestamp endTime) {
        return db().select(min(SHARE.ACTIVITY_ID), cast(sum(SHARE.COUNT), Integer.class)).from(SHARE)
            .where(SHARE.ACTIVITY_TYPE.eq(INTEGER_ZERO)).and(SHARE.CREATE_TIME.ge(startTime)).and(SHARE.CREATE_TIME.lessThan(endTime))
            .groupBy(SHARE.ACTIVITY_ID)
            .fetchMap(min(SHARE.ACTIVITY_ID), cast(sum(SHARE.COUNT), Integer.class));
    }

    /**
     * Get single share goods pv map.总商品分享pv
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public int getShareGoodsPv(Timestamp startTime, Timestamp endTime) {
        return getSingleShareGoodsPv(startTime, endTime).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * Get single share goods uv map.获得每件单一商品的商品分享uv
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> getSingleShareGoodsUv(Timestamp startTime, Timestamp endTime) {
        return db().select(min(SHARE.ACTIVITY_ID), count(SHARE.USER_ID)).from(SHARE)
            .where(SHARE.ACTIVITY_TYPE.eq(INTEGER_ZERO)).and(SHARE.CREATE_TIME.ge(startTime)).and(SHARE.CREATE_TIME.lessThan(endTime))
            .groupBy(SHARE.ACTIVITY_ID)
            .fetchMap(min(SHARE.ACTIVITY_ID), count(SHARE.USER_ID));
    }

    /**
     * Get single share goods uv map.获得每件单一商品的商品分享uv
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public int getShareGoodsUv(Timestamp startTime, Timestamp endTime) {
        return getSingleShareGoodsUv(startTime, endTime).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * Insert overview.
     * 往表 b2c_goods_summary 中插入统计数据(昨天, 前一星期, 前一个月)
     */
    public void insertGoodsSummary() {
        TYPE_LIST.forEach((e) -> {
            Date nowDate = DateUtils.yyyyMmDdDate(LocalDate.now());
            Timestamp startTimeStamp = Timestamp.valueOf(LocalDate.now().minusDays(e).atStartOfDay());
            Timestamp endTimeStamp = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            ProductOverviewParam param = new ProductOverviewParam() {{
                setDynamicDate(e);
                setStartTime(startTimeStamp);
                setEndTime(endTimeStamp);
            }};
            // 商品UV数
            getSingleGoodsUv(param).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.UV, v);
            });
            // 新成交客户数
            singleNewCustomerTranNum(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.NEW_USER_NUMBER, v);
            });
            // 旧成交客户数
            singleOldCustomerTranNum(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.OLD_USER_NUMBER, v);
            });
            // 商品PV数
            getSingleGoodsPv(param).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.PV, v);
            });
            // 加购人数
            addSingleCartUserNum(param).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.CART_UV, v);
            });
            // 付款人数
            singlePaidUv(param).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.PAID_UV, v);
            });
            // 付款商品数
            singlePaidGoodsNum(param).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.PAID_GOODS_NUMBER, v);
            });
            // 销售额
            singleGoodsSales(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.GOODS_SALES, v);
            });
            // 推荐人数
            singleRecommendUserNum(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.RECOMMEND_USER_NUM, v);
            });
            // 收藏人数
            singleCollectUserNum(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.COLLECT_USE_NUM, v);
            });
            // 商品分享pv
            getSingleShareGoodsPv(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.SHARE_PV, v);
            });
            // 商品分享uv
            getSingleShareGoodsUv(startTimeStamp, endTimeStamp).forEach((k, v) -> {
                createGoodsSummaryRecord(nowDate, e, k, GOODS_SUMMARY.SHARE_UV, v);
            });
        });
    }

    /**
     * Create overview record goods overview summary record.
     *
     * @param <T>        the type parameter
     * @param date       the date
     * @param type       the type
     * @param goodsId    the goods id
     * @param field      the field
     * @param fieldValue the field value
     * @return the goods overview summary record
     */
    public <T> void createGoodsSummaryRecord(Date date, Byte type, Integer goodsId, Field<T> field, T fieldValue) {
        db().insertInto(GOODS_SUMMARY, GOODS_SUMMARY.REF_DATE, GOODS_SUMMARY.TYPE, GOODS_SUMMARY.GOODS_ID, field)
            .values(date, type, goodsId, fieldValue)
            .onDuplicateKeyUpdate()
            .set(field, fieldValue)
            .execute();
    }

    /**
     * Order user money big decimal.成交金额(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public BigDecimal orderUserMoney(Timestamp startTime, Timestamp endTime) {
        // 商品订单成交金额+会员卡订单成交金额
        return goodsOrderTurnover(startTime, endTime).add(cardOrderTurnover(startTime, endTime)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * The constant ORDER_SN_CONDITION.
     */
    public static final Condition ORDER_SN_CONDITION = ORDER_I.ORDER_SN.eq(ORDER_I.MAIN_ORDER_SN).or(ORDER_I.MAIN_ORDER_SN.eq(StringUtils.EMPTY));

    /**
     * Goods order turnover big decimal.商品订单成交金额
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public BigDecimal goodsOrderTurnover(Timestamp startTime, Timestamp endTime) {
        return db().select(DSL.sum(ORDER_I.MONEY_PAID.add(ORDER_I.USE_ACCOUNT).add(ORDER_I.MEMBER_CARD_BALANCE)))
            .from(ORDER_I).where(STATUS_CONDITION)
            .and(ORDER_SN_CONDITION)
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime))
            .fetchOptionalInto(BigDecimal.class).orElse(BIGDECIMAL_ZERO);
    }

    /**
     * Card order turnover big decimal.（虚拟订单）成交金额
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public BigDecimal cardOrderTurnover(Timestamp startTime, Timestamp endTime) {
        return db().select(sum(VIRTUAL_ORDER.MONEY_PAID.add(VIRTUAL_ORDER.USE_ACCOUNT).add(VIRTUAL_ORDER.MEMBER_CARD_BALANCE)))
            .from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
            .and(VIRTUAL_ORDER.CREATE_TIME.ge(startTime)).and(VIRTUAL_ORDER.CREATE_TIME.lessThan(endTime))
            .fetchOptionalInto(BigDecimal.class).orElse(BIGDECIMAL_ZERO);
    }

    /**
     * Single goods clinch num map.每件商品的成交（订单状态大于3为成交）件数(排除货到付款非发货)
     *
     * @param param the param
     * @return the map
     */
    public Map<Integer, Integer> singleGoodsClinchNum(ProductOverviewParam param) {
        return commonBuilder2(param, count(ORDER_G.GOODS_NUMBER), EXT_CONDITION);
    }

    /**
     * All goods clinch num long.所有商品的总成交件数
     *
     * @param param the param
     * @return the long
     */
    public long allGoodsClinchNum(ProductOverviewParam param) {
        return singleGoodsClinchNum(param).values().stream().reduce(INTEGER_ZERO, Integer::sum);
    }

    /**
     * 加购人数
     * @param start 开始时间
     * @param end   结束时间
     * @return      人数
     */
    public Integer addCartUserNum(Timestamp start,Timestamp end){
        List<Integer> count = db().selectDistinct(CART.USER_ID)
            .from(CART)
            .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(CART.GOODS_ID))
            .where(CART.CREATE_TIME.ge(start))
            .and(CART.CREATE_TIME.lessThan(end))
            .fetchInto(Integer.class);
        Integer sum=0;
        if (null!=count){
            sum=count.size();
        }
        return sum;
    }
}
