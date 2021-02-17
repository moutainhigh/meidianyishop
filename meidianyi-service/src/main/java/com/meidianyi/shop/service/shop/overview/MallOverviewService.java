package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.shop.tables.*;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.WxShoppingListConfig;
import com.meidianyi.shop.service.pojo.shop.member.card.ActiveAuditParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.*;
import com.meidianyi.shop.service.saas.shop.ShopAccountService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.WxShoppingListConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.distribution.DistributorCheckService;
import com.meidianyi.shop.service.shop.goods.GoodsCommentService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.member.CardVerifyService;
import com.meidianyi.shop.service.shop.member.dao.CardDaoService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.meidianyi.shop.db.shop.tables.DeliverFeeTemplate.DELIVER_FEE_TEMPLATE;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.RecommendGoods.RECOMMEND_GOODS;
import static com.meidianyi.shop.db.shop.tables.Sort.SORT;
import static com.meidianyi.shop.db.shop.tables.UserSummaryTrend.USER_SUMMARY_TREND;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static com.meidianyi.shop.db.shop.tables.XcxCustomerPage.XCX_CUSTOMER_PAGE;
import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.DEFAULT_SCALE;
import static com.meidianyi.shop.db.main.tables.ShopAccount.SHOP_ACCOUNT;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.BYTE_THREE;
import static com.meidianyi.shop.service.shop.order.store.StoreOrderService.HUNDRED;
import static com.meidianyi.shop.service.shop.store.store.StoreWxService.BYTE_TWO;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.jooq.impl.DSL.*;

/**
 * @author liufei
 * date 2019/7/15
 * 商城概览service
 */
@Service
@Slf4j
public class MallOverviewService extends ShopBaseService {
    @Autowired
    public WxShoppingListConfigService shoppingListConfigService;

    @Autowired
    public ShopCommonConfigService shopCommonConfigService;

    @Autowired
    public CouponService couponService;

    @Autowired
    public DistributorCheckService distributorCheckService;

    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    public ReturnOrderService returnOrderService;

    @Autowired
    public GoodsCommentService goodsCommentService;

    @Autowired
    public GoodsService goodsService;

    @Autowired
    public CardVerifyService cardVerifyService;

    @Autowired
    public CardDaoService cardDaoService;

    @Autowired
    public ShopAccountService shopAccountService;

    public static final List<Byte> RECENT_DATE = new ArrayList<Byte>() {{
        add(Byte.valueOf("0"));
        add(Byte.valueOf("1"));
        add(Byte.valueOf("7"));
        add(Byte.valueOf("30"));
        add(Byte.valueOf("90"));
    }};
    /**
     * 获取数据展示数据，
     * @param param 1：当天数据；2表示昨天: 7：近一周数据；30：近一个月数据；90：近三个月数据
     */
    public DataDemonstrationVo dataDemonstration(DataDemonstrationParam param){
        byte screenTime = param.getScreeningTime();
        if (!RECENT_DATE.contains(screenTime)) {
            screenTime = 0;
        }
        return getDataDemonstration(screenTime);
    }
    private DataDemonstrationVo getDataDemonstration(byte screeningTime){
        DataDemonstrationVo vo = new DataDemonstrationVo();
        // 0当天数据实时统计返回
        if (screeningTime == 0) {
            Condition orderInfoTime = ORDER_INFO.CREATE_TIME.ge(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            Condition virtualTime = VIRTUAL_ORDER.CREATE_TIME.ge(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            Condition userLoginRecordTime = UserLoginRecord.USER_LOGIN_RECORD.CREATE_TIME.ge(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            Condition payOrderCon = ORDER_INFO.ORDER_STATUS.ge((byte) 3);

            vo.setUserVisitNum(db().fetchCount(UserLoginRecord.USER_LOGIN_RECORD, userLoginRecordTime));
            vo.setPaidOrderNum(db().fetchCount(ORDER_INFO, orderInfoTime.and(payOrderCon)));
            vo.setOrderUserNum(db().select(countDistinct(ORDER_INFO.USER_ID))
                .from(ORDER_INFO).where(orderInfoTime).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO));
            vo.setOrderNum(db().fetchCount(ORDER_INFO, orderInfoTime));
            // 总支付金额（商品订单(MONEY_PAID+USE_ACCOUNT+MEMBER_CARD_BALANCE)+会员卡订单）
            BigDecimal virtual = db().select(sum(VIRTUAL_ORDER.MONEY_PAID.add(VIRTUAL_ORDER.USE_ACCOUNT).add(VIRTUAL_ORDER.MEMBER_CARD_BALANCE)))
                .from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
                .and(virtualTime)
                .fetchOptionalInto(BigDecimal.class).orElse(BIGDECIMAL_ZERO);
            BigDecimal goodsPaid = db().select(sum(ORDER_INFO.MONEY_PAID.add(ORDER_INFO.USE_ACCOUNT).add(ORDER_INFO.MEMBER_CARD_BALANCE)))
                .from(ORDER_INFO).where(orderInfoTime.and(payOrderCon))
                .fetchOptionalInto(BigDecimal.class).orElse(BigDecimal.ZERO);

            vo.setTotalPaidSum(virtual.add(goodsPaid).setScale(2, RoundingMode.HALF_UP));

            vo.setPaidUserNum(db().select(countDistinct(ORDER_INFO.USER_ID))
                .from(ORDER_INFO).where(orderInfoTime.and(payOrderCon))
                .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO));
            vo.setOrderPeopleNum(db().select(count(ORDER_INFO.USER_ID))
                .from(ORDER_INFO).where(orderInfoTime).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO));
            vo.setPayPeopleNum(db().select(count(ORDER_INFO.USER_ID))
                .from(ORDER_INFO).where(orderInfoTime.and(payOrderCon))
                .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO));
        } else {
            // 历史数据从统计表中获取返回
            vo = db().select(USER_SUMMARY_TREND.LOGIN_DATA.as("userVisitNum")
                , USER_SUMMARY_TREND.ORDER_NUM.as("orderNum")
                , USER_SUMMARY_TREND.ORDER_USER_NUM.as("orderUserNum")
                , USER_SUMMARY_TREND.PAY_ORDER_NUM.as("paidOrderNum")
                , USER_SUMMARY_TREND.TOTAL_PAID_MONEY.as("totalPaidSum")
                , USER_SUMMARY_TREND.ORDER_USER_DATA.as("paidUserNum")
                , USER_SUMMARY_TREND.ORDER_PEOPLE_NUM.as("orderPeopleNum")
                , USER_SUMMARY_TREND.PAY_PEOPLE_NUM.as("payPeopleNum")
            ).from(USER_SUMMARY_TREND).where(USER_SUMMARY_TREND.REF_DATE.eq(java.sql.Date.valueOf(LocalDate.now())))
                .and(USER_SUMMARY_TREND.TYPE.eq(screeningTime)).fetchOptionalInto(DataDemonstrationVo.class).orElse(vo);
        }
        BigDecimal orderUserNum = BigDecimalUtil.valueOf(vo.getOrderUserNum());
        BigDecimal userVisitNum = BigDecimalUtil.valueOf(vo.getUserVisitNum());
        BigDecimal paidUserNum = BigDecimalUtil.valueOf(vo.getPaidUserNum());
        vo.setUv2order(specialDivide(orderUserNum, userVisitNum));
        vo.setUv2paid(specialDivide(paidUserNum, userVisitNum));
        vo.setOrder2paid(specialDivide(paidUserNum, orderUserNum));
        return vo;
    }

    private BigDecimal specialDivide(BigDecimal left, BigDecimal right) {
        if (left == null || left.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (right == null || right.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return left.divide(right, 6, RoundingMode.HALF_UP).multiply(HUNDRED).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 代办事项
     * @return
     */
    public ToDoItemVo toDoItem(ToDoItemParam param){
        ToDoItemVo toDoItemVo = new ToDoItemVo();
        ShopAccountRecord oldRecord = shopAccountService.getAccountInfoForId(getSysId());
        if (!StringUtils.isEmpty(param.getBacklog())&&!"".equals(param.getBacklog())){
            oldRecord.setBacklog(param.getBacklog());
        }
        ShopAccountRecord newRecord = db().newRecord(SHOP_ACCOUNT,oldRecord);
        shopAccountService.updateAccountInfo(newRecord);
        Condition orderStatus = ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY);
        Condition deliverType0 = ORDER_INFO.DELIVER_TYPE.in(OrderConstant.DELIVER_TYPE_COURIER,OrderConstant.CITY_EXPRESS_SERVICE);
        Condition deliverType1 = ORDER_INFO.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_COURIER);
        Condition refundStatus = ReturnOrder.RETURN_ORDER.REFUND_STATUS.in(OrderConstant.REFUND_STATUS_AUDITING,OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
        toDoItemVo.setPendingOrder(db().fetchCount(ORDER_INFO, orderStatus.and(deliverType1)));
        toDoItemVo.setToBeDelivered(db().fetchCount(ORDER_INFO, orderStatus.and(deliverType0)));
        toDoItemVo.setRefunds(db().fetchCount(ReturnOrder.RETURN_ORDER,refundStatus));
        GoodsSpecProduct gsp = GoodsSpecProduct.GOODS_SPEC_PRODUCT.as("gsp");
        Goods g = Goods.GOODS.as("g");
        toDoItemVo.setSoldOutGoods(db().select(count(gsp.PRD_ID))
                .from(gsp)
                .leftJoin(g)
                .on(gsp.GOODS_ID.eq(g.GOODS_ID))
                .where(g.DEL_FLAG.eq((byte)0))
                .and(gsp.PRD_NUMBER.eq(0))
            .and(g.SOURCE.eq((byte) 0)).fetchOneInto(Integer.class));
        toDoItemVo.setProductEvaluationPr(db().fetchCount(CommentGoods.COMMENT_GOODS,CommentGoods.COMMENT_GOODS.DEL_FLAG.eq((byte)0)
                .and(CommentGoods.COMMENT_GOODS.FLAG.eq((byte)0))));
        toDoItemVo.setDistributorPr(db().fetchCount(DistributorApply.DISTRIBUTOR_APPLY,DistributorApply.DISTRIBUTOR_APPLY.STATUS.eq((byte)0)));
        toDoItemVo.setMembershipCardPr(db().fetchCount(CardExamine.CARD_EXAMINE,CardExamine.CARD_EXAMINE.STATUS.eq((byte)1)));
        toDoItemVo.setDistributionWithdrawalPr(db().fetchCount(DistributionWithdraw.DISTRIBUTION_WITHDRAW,DistributionWithdraw.DISTRIBUTION_WITHDRAW.STATUS.eq((byte)1)));
        toDoItemVo.setServiceEvaluationPr(db().fetchCount(CommentService.COMMENT_SERVICE,CommentService.COMMENT_SERVICE.FLAG.eq((byte)0)));
        ShopAccountRecord shopAccountRecord = shopAccountService.getAccountInfoForId(getSysId());
        toDoItemVo.setBacklog(StringUtils.isEmpty(shopAccountRecord.getBacklog())?null:shopAccountRecord.getBacklog());
        return toDoItemVo;
    }

    /**
     * 店铺助手
     *
     * @param param the param
     * @return the shop assistant vo
     */
    public ShopAssistantVo shopAssistant(ShopAssistantParam param) {
        return ShopAssistantVo.builder()
            .dataShop(shopNav())
            .dataGoods(goodsNav(param))
            .dataOrder(orderNav(param))
            .dataMarket(marketNav(param))
            .build();
    }

    /**
     * 店铺相关统计信息
     */
    private AssiDataShop shopNav() {
        WxShoppingListConfig shoppingListConfig = shoppingListConfigService.getShoppingListConfig();
        Map<String, String> comm = new HashMap<String, String>(INTEGER_ONE) {{
            put("ShoppingRecommend", shoppingListConfig.getWxShoppingRecommend());
        }};
        return AssiDataShop.builder()
            //  店铺首页 0：已完成店铺首页装修，否未装修店铺首页
            .homePageConf(Metadata.builder().type(BYTE_ONE).value(shopPageConfig()).build())
            //  好物圈 0: 未开启 "1"开启
            .shopRecommendConf(Metadata.builder()
                .type(BYTE_ONE)
                .value(Integer.valueOf(shoppingListConfig.getEnabeldWxShoppingList()))
                .content(comm).build())
            //  客服 0: 已开启客服，否未开启
            .customServiceConf(Metadata.builder().type(BYTE_ONE).value(isOpenCommon()).build())
            .build().ruleHandler();
    }


    /**
     * 是否开启客服
     * @return
     */
    private int isOpenCommon() {
        return shopCommonConfigService.getCustomService() + shopCommonConfigService.getReturnService() > 0 ? BYTE_ZERO : BYTE_ONE;
    }

    /**
     * 商品相关统计信息
     */
    private AssiDataGoods goodsNav(ShopAssistantParam param) {
        return AssiDataGoods.builder()
            // 运费模板设置
            .shipTemplateConf(Metadata.builder()
                .type(BYTE_ONE)
                .value(db().fetchCount(DELIVER_FEE_TEMPLATE)).build())
            // 商品添加
            .goodsConf(Metadata.builder()
                .type(BYTE_ONE)
                .value(db().fetchCount(Goods.GOODS, Goods.GOODS.DEL_FLAG.eq(BYTE_ZERO))).build())
            // 商品库存偏小
            .goodsStoreConf(Metadata.builder()
                .value(goodsService.smallCommodityInventory(param.getStoreSizeNum())).build())
            //  滞销商品
            .goodsUnsalableConf(Metadata.builder().value(goodsService.unsalableGoods()).build())
            //  商品评价审核逾期
            .goodsComment(Metadata.builder().value(goodsCommentService.reviewOverdue(param.getCommentOver())).build())
            //  推荐商品
            .goodsRecommend(Metadata.builder()
                .type(BYTE_THREE)
                .value(db().fetchCount(RECOMMEND_GOODS, RECOMMEND_GOODS.DEL_FLAG.eq(BYTE_ZERO))).build())
            // 商家分类
            .shopSort(Metadata.builder()
                .type(BYTE_THREE)
                .value(db().fetchCount(SORT)).build())
            .build().ruleHandler().setType();
    }

    /**
     * 订单相关统计信息（未完成状态为提醒，已完成状态为任务）
     */
    private AssiDataOrder orderNav(ShopAssistantParam param) {
        return AssiDataOrder.builder()
            //  发货逾期
            .deliver(Metadata.builder().value(orderInfo.overdueDelivery(param.getDeliverOver())).build())
            //  退款申请逾期
            .refund(Metadata.builder().value(returnOrderService.refundOverdue(param.getRefundOver())).build())
            // 提醒发货
            .remind(Metadata.builder().value(orderInfo.remindOverdueOrder()).build())
            .build().ruleHandler().setType();
    }

    /**
     * 营销相关统计信息
     */
    private AssiDataMarket marketNav(ShopAssistantParam param) {
        Map<String, String> memberContent = buildMemberVo(param.getExamineOver());
        int memberValue;
        if (MapUtils.isEmpty(memberContent)) {
            memberValue = INTEGER_ZERO;
        } else {
            memberValue = cardVerifyService.getUndealUserNum(Integer.valueOf(memberContent.get("card_id")));
        }
        Map<Integer, String> coupon = couponService.getSmallInventoryCoupon(param.getCouponSizeNum());

        return AssiDataMarket.builder()
            //  分销审核超时
            .examine(Metadata.builder()
                .type(BYTE_TWO)
                .value(distributorCheckService.distributionReviewTimeout(param.getApplyOver())).build())
            // 会员卡激活审核超时
            .member(Metadata.builder()
                .type(BYTE_TWO)
                .value(memberValue).content(memberContent).build())
            //  优惠券库存不足
            .voucher(Metadata.builder()
                .type(BYTE_TWO)
                .value(coupon.size()).content(coupon).build())
            .build().ruleHandler();
    }

    private Map<String, String> buildMemberVo(int examineOver) {
        CardExamineRecord cardExamineRecord = cardVerifyService.getLastRecordCanNull(new ActiveAuditParam() {{
            setExamineOver(Timestamp.valueOf(LocalDateTime.now().minusDays(examineOver)));
        }});

        if (Objects.isNull(cardExamineRecord) || cardExamineRecord.size() == 0) {
            return null;
        }
        Integer cardId = cardExamineRecord.getCardId();
        return new HashMap<String, String>(3) {{
            put("card_id", String.valueOf(cardId));
            put("card_name", cardDaoService.getCardById(cardId).getCardName());
//            put("card_num", cardVerifyService.getUndealUserNum(cardId).toString());
        }};
    }

    /**
     * 店铺首页 0：已完成店铺首页装修，否未装修店铺首页
     */
    private Byte shopPageConfig() {
        String shopPage = db().select(DSL.concat(XCX_CUSTOMER_PAGE.PAGE_CONTENT, XCX_CUSTOMER_PAGE.PAGE_PUBLISH_CONTENT)).from(XCX_CUSTOMER_PAGE)
            .where(XCX_CUSTOMER_PAGE.PAGE_TYPE.eq(BYTE_ONE))
            .and(XCX_CUSTOMER_PAGE.SHOP_ID.eq(getShopId()))
            .fetchOneInto(String.class);
        return StringUtils.isNoneBlank(shopPage) ? BYTE_ZERO : BYTE_ONE;
    }
}
