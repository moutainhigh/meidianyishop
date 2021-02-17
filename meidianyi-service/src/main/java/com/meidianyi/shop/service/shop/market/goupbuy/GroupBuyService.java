package com.meidianyi.shop.service.shop.market.goupbuy;


import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.*;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.*;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveOrderList;
import com.meidianyi.shop.service.pojo.shop.order.analysis.OrderActivityUserNum;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.market.groupbuy.*;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.TagService;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import jodd.util.StringUtil;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_DISABLE;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_NORMAL;
import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.*;

/**
 * @author 孔德成
 * @date 2019/7/18 15:55
 */

@Service
@Primary
public class GroupBuyService extends ShopBaseService {

    /**
     * 是否默认成团
     */
    public static final Byte IS_DEFAULT_Y = 1;
    /**
     * 团长优惠
     */
    public static final Byte GROUPER_CHEAP = 1;
    @Autowired
    private GroupBuyListService groupBuyListService;
    @Autowired
    private OrderReadService orderReadService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private ReturnOrderService returnOrderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpecProductService goodsSpecProductService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private QrCodeService qrCode;
    @Autowired
    private TagService tagService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;


    /**
     * 添加拼团活动
     *
     * @param groupBuy 拼团
     */
    public void addGroupBuy(GroupBuyParam groupBuy) {
        transaction(() -> {
            //分享配置转json
            groupBuy.setShareConfig(Util.toJson(groupBuy.getShare()));
            //订单总库存
            Integer stock = groupBuy.getProduct().stream().mapToInt(GroupBuyProductParam::getStock).sum();
            //拼团信息
            GroupBuyDefineRecord groupBuyDefineRecord = db().newRecord(GROUP_BUY_DEFINE, groupBuy);
            groupBuyDefineRecord.setStatus(ACTIVITY_STATUS_NORMAL);
            groupBuyDefineRecord.setStock(stock.shortValue());
            groupBuyDefineRecord.insert();
            //拼团商品规格价格信息
            groupBuy.getProduct().forEach((product) -> {
                GroupBuyProductDefineRecord productDefineRecord = db().newRecord(GROUP_BUY_PRODUCT_DEFINE, product);
                productDefineRecord.setActivityId(groupBuyDefineRecord.getId());
                productDefineRecord.insert();
            });
        });

        esDataUpdateMqService.addEsGoodsIndex(Util.splitValueToList(groupBuy.getGoodsId()), getShopId(), DBOperating.UPDATE);
    }

    /**
     * 根据id 获取活动record
     *
     * @param id
     * @return
     */
    public GroupBuyDefineRecord getGroupBuyRecord(Integer id) {
        return db().selectFrom(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.ID.eq(id)).fetchOne();
    }

    /**
     * 根据id 获取活动record
     *
     * @param id
     * @return
     */
    public Integer getGroupBuyLimitAmout(Integer id) {
        return db().select(GROUP_BUY_DEFINE.LIMIT_AMOUNT).from(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.ID.eq(id)).fetchOneInto(Integer.class);
    }

    /**
     * 根据id获取拼团规格商品详情
     *
     * @param activityId 活动id
     * @param productId  规格id
     * @return {@link GroupBuyProductDefineRecord}
     */
    public GroupBuyProductDefineRecord getGroupBuyProduct(Integer activityId, Integer productId) {
        return db().selectFrom(GROUP_BUY_PRODUCT_DEFINE).where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(activityId))
            .and(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID.eq(productId)).fetchOne();
    }

    /**
     * 根据id 获取拼团人数限制
     *
     * @param id
     * @return
     */
    public Integer getGroupBuyLimitAmountRecord(Integer id) {
        return db().select(GROUP_BUY_DEFINE.LIMIT_AMOUNT).from(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.ID.eq(id)).fetchOneInto(Integer.class);
    }

    /**
     * 删除
     *
     * @param id id
     */
    public int deleteGroupBuy(Integer id) {
        db().update(GROUP_BUY_DEFINE)
            .set(GROUP_BUY_DEFINE.DEL_FLAG, DelFlag.DISABLE.getCode())
            .where(GROUP_BUY_DEFINE.ID.eq(id))
            .execute();
        GroupBuyDefineRecord record = getGroupBuyRecord(id);
        esDataUpdateMqService.addEsGoodsIndex(Util.splitValueToList(record.getGoodsId()), getShopId(), DBOperating.UPDATE);
        return 1;
    }

    /**
     * 编辑测试
     *
     * @param param GroupBuyEditParam
     * @return int
     */
    public void updateGroupBuy(GroupBuyEditParam param) {
        transaction(() -> {
            //分享配置转json
            param.setShareConfig(Util.toJson(param.getShare()));
            //订单总库存
            Integer stock = param.getProduct().stream().mapToInt(GroupBuyProductParam::getStock).sum();
            //拼团信息
            GroupBuyDefineRecord groupBuyDefineRecord = db().newRecord(GROUP_BUY_DEFINE, param);
            groupBuyDefineRecord.setStock(stock.shortValue());
            groupBuyDefineRecord.update();
            //拼团商品规格价格信息
            db().delete(GROUP_BUY_PRODUCT_DEFINE).where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(groupBuyDefineRecord.getId())).execute();
            param.getProduct().forEach((product) -> {
                GroupBuyProductDefineRecord productDefineRecord = db().newRecord(GROUP_BUY_PRODUCT_DEFINE, product);
                productDefineRecord.setActivityId(groupBuyDefineRecord.getId());
                productDefineRecord.insert();
            });
        });

        GroupBuyDefineRecord record = getGroupBuyRecord(param.getId());
        esDataUpdateMqService.addEsGoodsIndex(Util.splitValueToList(record.getGoodsId()), getShopId(), DBOperating.UPDATE);

    }

    /**
     * 分享拼团链接
     *
     * @param id 活动Id
     * @return 二维码信息
     */
    public ShareQrCodeVo shareGroupBuy(Integer id) {
        String pathParam = "pageFrom=1&actId=" + id;
        String imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.GOODS_SEARCH, pathParam);
        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(QrCodeTypeEnum.GOODS_SEARCH.getPathUrl(pathParam));
        return vo;
    }

    /**
     * 停用或者启用
     *
     * @param id
     * @return
     */
    public int changeStatusActivity(Integer id, Byte status) {
        if (ACTIVITY_STATUS_DISABLE.equals(status)) {
            db().update(GROUP_BUY_DEFINE)
                .set(GROUP_BUY_DEFINE.STATUS, ACTIVITY_STATUS_DISABLE)
                .where(GROUP_BUY_DEFINE.ID.eq(id))
                .and(GROUP_BUY_DEFINE.STATUS.eq(ACTIVITY_STATUS_NORMAL))
                .execute();
        } else if (ACTIVITY_STATUS_NORMAL.equals(status)) {
            db().update(GROUP_BUY_DEFINE)
                .set(GROUP_BUY_DEFINE.STATUS, ACTIVITY_STATUS_NORMAL)
                .where(GROUP_BUY_DEFINE.ID.eq(id))
                .and(GROUP_BUY_DEFINE.STATUS.eq(ACTIVITY_STATUS_DISABLE))
                .execute();
        }
        GroupBuyDefineRecord record = getGroupBuyRecord(id);
        esDataUpdateMqService.addEsGoodsIndex(Util.splitValueToList(record.getGoodsId()), getShopId(), DBOperating.UPDATE);
        return 1;
    }


    /**
     * 查询拼团详情
     *
     * @param id
     * @return
     */
    public GroupBuyDetailVo detailGroupBuy(Integer id) {
        Record record = db().select().from(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.ID.eq(id)
            .and(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(GROUP_BUY_DEFINE.STATUS.eq(ACTIVITY_STATUS_NORMAL))
        ).fetchOne();
        if (record == null) {
            return null;
        }
        GroupBuyDetailVo groupBuy = record.into(GroupBuyDetailVo.class);
        //产品规格信息
        List<GroupBuyProductVo> buyProductVos = db()
                .select(GROUP_BUY_PRODUCT_DEFINE.ID,
                        GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID,
                        GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID,
                        GROUP_BUY_PRODUCT_DEFINE.GOODS_ID,
                        GOODS.GOODS_NAME,
                        GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE,
                        GROUP_BUY_PRODUCT_DEFINE.GROUPER_PRICE,
                        GROUP_BUY_PRODUCT_DEFINE.SALE_NUM,
                        GROUP_BUY_PRODUCT_DEFINE.STOCK,
                        GOODS_SPEC_PRODUCT.PRD_DESC,
                        GOODS_SPEC_PRODUCT.PRD_PRICE,
                        GOODS_SPEC_PRODUCT.PRD_NUMBER
                )
                .from(GROUP_BUY_PRODUCT_DEFINE)
                .leftJoin(GOODS_SPEC_PRODUCT).on(GOODS_SPEC_PRODUCT.PRD_ID.eq(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID))
                .leftJoin(GOODS).on(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.eq(GOODS.GOODS_ID))
                .where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(id)).fetch().into(GroupBuyProductVo.class);
        Map<Integer, List<GroupBuyProductVo>> goodsProductMap = buyProductVos.stream().collect(Collectors.groupingBy(GroupBuyProductVo::getGoodsId));
        List<Integer> collect = goodsProductMap.keySet().stream().collect(Collectors.toList());
        List<GoodsView> goodsViews = goodsService.selectGoodsViewList(collect);
        Map<Integer, GoodsView> goodsMap = goodsViews.stream().collect(Collectors.toMap(GoodsView::getGoodsId, (a) -> a));
        List<GroupBuyDetailVo.GroupBuyGoods> goodsList =new ArrayList<>();
        goodsProductMap.forEach((k,v)->{
            GoodsView goodsView = goodsMap.get(k);
            GroupBuyDetailVo.GroupBuyGoods groupBuyGoods =new GroupBuyDetailVo.GroupBuyGoods();
            groupBuyGoods.setGoodsId(k);
            groupBuyGoods.setGoodsImg(goodsView.getGoodsImg());
            groupBuyGoods.setGoodsName(goodsView.getGoodsName());
            groupBuyGoods.setGoodsNumber(goodsView.getGoodsNumber());
            groupBuyGoods.setShopPrice(goodsView.getShopPrice());
            groupBuyGoods.setUnit(goodsView.getUnit());
            groupBuyGoods.setProductList(v);
            goodsList.add(groupBuyGoods);
        });
        //优惠卷信息
        List<Integer> ids = Util.splitValueToList(groupBuy.getRewardCouponId());
        List<CouponView> couponViews = couponService.getCouponViewByIds(ids);
        groupBuy.setCouponViews(couponViews);
        if(groupBuy.getActivityTag().equals(BaseConstant.YES) && StringUtil.isNotBlank(groupBuy.getActivityTagId())){
            groupBuy.setTagList(tagService.getTagsById(Util.splitValueToList(groupBuy.getActivityTagId())));
        }
        groupBuy.setGoodsList(goodsList);
        groupBuy.setProductList(buyProductVos);
        groupBuy.setShare(Util.parseJson(groupBuy.getShareConfig(), GroupBuyShareConfigVo.class));
        groupBuy.setShareConfig(null);
        return groupBuy;
    }


    /**
     * 拼团订单列表
     *
     * @return
     */
    public PageResult<MarketOrderListVo> groupBuyOrderList(MarketOrderListParam param) {
        return groupBuyListService.groupBuyOrderList(param);
    }

    /**
     * 活动新增用户列表
     *
     * @param param
     */
    public PageResult<MemberInfoVo> groupBuyNewUserList(MarketSourceUserListParam param) {
        return groupBuyListService.groupBuyNewUserList(param);
    }

    /**
     * 参团明细列表
     *
     * @param param
     * @return
     */
    public PageResult<GroupBuyDetailListVo> detailGroupBuyList(GroupBuyDetailParam param) {
        return groupBuyListService.detailGroupBuyList(param);
    }

    /**
     * 查询团购列表
     *
     * @param param
     * @return
     */
    public Object getListGroupBuy(GroupBuyListParam param) {
        return groupBuyListService.getListGroupBuy(param);
    }

    /**
     * 拼团活动效果数据
     *
     * @param param
     * @return
     */
    public GroupBuyAnalysisVo groupBuyAnalysis(GroupBuyAnalysisParam param) {
        GroupBuyAnalysisVo analysisVo = new GroupBuyAnalysisVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = DateUtils.currentMonthFirstDay();
            endDate = DateUtils.getLocalDateTime();
        }
        //获取销售额等金额
        List<ActiveDiscountMoney> discountMoneyList = orderReadService.getActiveDiscountMoney(BaseConstant.ACTIVITY_TYPE_GROUP_BUY, param.getId(), startDate, endDate);
        //获取参与用户信息
        ActiveOrderList activeOrderList = orderReadService.getActiveOrderList(BaseConstant.ACTIVITY_TYPE_GROUP_BUY, param.getId(), startDate, endDate);

        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE, startDate);
            //活动实付金额
            ActiveDiscountMoney discountMoney = getDiscountMoneyByDate(discountMoneyList, startDate);
            if (discountMoney == null) {
                analysisVo.getGoodsPriceList().add(BigDecimal.ZERO);
                analysisVo.getMarketPriceList().add(BigDecimal.ZERO);
                analysisVo.getRatioList().add(BigDecimal.ZERO);
            } else {
                BigDecimal goodsPrice = Optional.ofNullable(discountMoney.getPaymentAmount()).orElse(BigDecimal.ZERO);
                BigDecimal marketPric = Optional.ofNullable(discountMoney.getDiscountAmount()).orElse(BigDecimal.ZERO);
                analysisVo.getGoodsPriceList().add(goodsPrice);
                analysisVo.getMarketPriceList().add(marketPric);
                analysisVo.setTotalPrice(analysisVo.getTotalPrice().add(goodsPrice));
                analysisVo.setTotalMarketPrice(analysisVo.getTotalMarketPrice().add(marketPric));
                analysisVo.getRatioList().add(goodsPrice.compareTo(BigDecimal.ZERO) > 0 ?
                    marketPric.divide(goodsPrice,4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            }
            //新用户数
            OrderActivityUserNum newUser = getUserNum(activeOrderList.getNewUserNum(), dateFormat);
            if (newUser == null) {
                analysisVo.getNewUserList().add(0);
            } else {
                analysisVo.getNewUserList().add(newUser.getNum());
            }
            //老用户数
            OrderActivityUserNum oldUser = getUserNum(activeOrderList.getOldUserNum(), dateFormat);
            if (oldUser == null) {
                analysisVo.getOldUserList().add(0);
            } else {
                analysisVo.getOldUserList().add(oldUser.getNum());
            }
            analysisVo.getDateList().add(dateFormat);
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }
        analysisVo.setTotalNewUser(activeOrderList.getNewUser());
        analysisVo.setTotalOldUser(activeOrderList.getOldUser());
        BigDecimal divide = analysisVo.getTotalPrice().compareTo(BigDecimal.ZERO)>0?analysisVo.getTotalMarketPrice().divide(analysisVo.getTotalPrice(), 4, RoundingMode.HALF_UP):BigDecimal.ZERO;
        analysisVo.setTotalRatio(divide);
        return analysisVo;
    }


    /**
     * 根据goodsIds获取拼团定义
     *
     * @param goodsIds 商品id
     * @param date     当前时间
     * @return List<GroupBuyProductDefineRecord>
     */
//    public Map<Integer, List<Record2<Integer, BigDecimal>>> getGroupBuyProductByGoodsIds(List<Integer> goodsIds, Timestamp date) {
//        return db().select(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID, GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE)
//            .from(GROUP_BUY_PRODUCT_DEFINE)
//            .leftJoin(GROUP_BUY_DEFINE)
//            .on(GROUP_BUY_DEFINE.ID.eq(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID))
//            .where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
//            .and(GROUP_BUY_DEFINE.STATUS.eq(ACTIVITY_STATUS_NORMAL))
//            .and(GROUP_BUY_DEFINE.STOCK.notEqual((short) 0))
//            .and(GROUP_BUY_DEFINE.GOODS_ID.in(goodsIds))
//            .and(GROUP_BUY_DEFINE.START_TIME.lessThan(date))
//            .and(GROUP_BUY_DEFINE.END_TIME.greaterThan(date))
//            .orderBy(GROUP_BUY_DEFINE.LEVEL.desc(),GROUP_BUY_DEFINE.CREATE_TIME.desc(),GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE)
//            .fetch()
//            .stream()
//            .collect(Collectors.groupingBy(x -> x.get(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID)));
//    }
    /**
     * 根据goodsIds获取拼团定义
     *
     * @param goodsIds 商品id
     * @param date     当前时间
     * @return List<GroupBuyProductDefineRecord>
     */
    public Map<Integer, BigDecimal> getGroupBuyProductByGoodsIds(List<Integer> goodsIds, Timestamp date) {
        return db().select(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID, GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE)
            .from(GROUP_BUY_PRODUCT_DEFINE)
            .leftJoin(GROUP_BUY_DEFINE)
            .on(GROUP_BUY_DEFINE.ID.eq(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID))
            .where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(GROUP_BUY_DEFINE.STATUS.eq(ACTIVITY_STATUS_NORMAL))
            .and(GROUP_BUY_DEFINE.STOCK.notEqual((short) 0))
            .and(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.in(goodsIds))
            .and(GROUP_BUY_DEFINE.START_TIME.lessThan(date))
            .and(GROUP_BUY_DEFINE.END_TIME.greaterThan(date))
            .orderBy(GROUP_BUY_DEFINE.LEVEL.desc(),GROUP_BUY_DEFINE.CREATE_TIME.desc(),GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE)
            .fetch()
            .stream()
            .collect(
                Collectors
                    .toMap(x->x.get(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID),
                        y->y.get( GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE),(olValue,newValue)->olValue)
            );
    }

    private void outPutLog(Integer goodsId) {
        logger().error("拼团相关联的{}号商品不存在", goodsId);
    }

    /**
     * 活动实付金额
     *
     * @param discountMoneyList
     * @param timestamp
     * @return
     */
    private ActiveDiscountMoney getDiscountMoneyByDate(List<ActiveDiscountMoney> discountMoneyList, Timestamp timestamp) {
        for (ActiveDiscountMoney data : discountMoneyList) {
            if (data != null && timestamp.equals(data.getCreateTime())) {
                return data;
            }
        }
        return null;
    }

    /**
     * 活动用户数量
     *
     * @param list
     * @param dateFormat
     * @return
     */
    private OrderActivityUserNum getUserNum(List<OrderActivityUserNum> list, String dateFormat) {
        for (OrderActivityUserNum activityUserNum : list) {
            if (activityUserNum != null && dateFormat.equals(activityUserNum.getDate())) {
                return activityUserNum;
            }
        }
        return null;
    }

    /**
     * 拼团成功
     *
     * @param groupBuyId
     * @param groupId
     */
    public void groupBuySuccess(Integer groupBuyId, Integer groupId, String goodsName) {
        logger().info("拼团成功检查-开始");
        List<GroupBuyUserInfo> pinUserList = groupBuyListService.getGroupUserList(groupId);
        List<GroupBuyUserInfo> groupUserList = pinUserList.stream().filter(p -> p.getStatus().equals(STATUS_ONGOING)).collect(Collectors.toList());
        GroupBuyUserInfo first = pinUserList.stream().findFirst().get();
        Integer groupBuyLimitAmountRecord = getGroupBuyLimitAmountRecord(groupBuyId);
        //判断是否拼团成功
        if (groupBuyLimitAmountRecord <= groupUserList.size()) {
            logger().info("拼团成功,groupId:{}", groupId);
            Timestamp date = DateUtils.getLocalDateTime();
            List<String> orderSnList = groupUserList.stream().map(GroupBuyUserInfo::getOrderSn).collect(Collectors.toList());
            updateGroupSuccess(groupId, date, orderSnList);
            logger().info("修改订单状态");
            orderInfoService.batchChangeToWaitDeliver(orderSnList);
            List<Integer> userIds = groupUserList.stream().map(GroupBuyUserInfo::getUserId).collect(Collectors.toList());
            groupBuySuccessMessage(userIds,groupId, groupBuyId, first.getUsername(), goodsName);
        }
        logger().info("拼团成功检查-结束");
    }

    /**
     * 拼团成功 发送消息
     */
    private void groupBuySuccessMessage(List<Integer> userIds, Integer groupId, Integer groupBuyId, String grouperName, String goodsName) {
        logger().info("拼团成功 发送消息");
        String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
        if (officeAppId == null) {
            logger().info("店铺" + getShopId() + "没有关注公众号");
        }
        String page= "pages1/groupbuyinfo/groupbuyinfo?group_id="+groupId;
        String[][] data = new String[][]{{"您的拼团订单已经拼团成功", "#173177"},
            {goodsName, "#173177"},
            {grouperName, "#173177"},
            {userIds.size() + "", "#173177"},
            {"感觉您的惠顾，更多拼团请点击详情！", "#173177"}};
        RabbitMessageParam param = RabbitMessageParam.builder()
            .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.GROUP_SUCCESS).data(data).build())
            .page(page).shopId(getShopId()).userIdList(userIds).type(RabbitParamConstant.Type.SUCCESS_TEAM)
            .build();
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
            TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
        /**
         * 小程序
         */
        String groupBuyName = getGroupBuyNameBuyId(groupBuyId);
        String prizeName = "已成团";
        String[][] data1 = new String[][]{
            {groupBuyName},
            {prizeName},
            {Util.getdate("yyyy-MM-dd HH:mm:ss")}};
        MaSubscribeData build = MaSubscribeData.builder().data307(data1).build();
        RabbitMessageParam param1 = RabbitMessageParam.builder()
            .maTemplateData(MaTemplateData.builder().config(SubcribeTemplateCategory.INVITE_SUCCESS).data(build).build())
            .page(page).shopId(getShopId()).userIdList(userIds)
            .type(RabbitParamConstant.Type.INVITE_SUCCESS_GROUPBUY).build();
        saas.taskJobMainService.dispatchImmediately(param1, RabbitMessageParam.class.getName(), getShopId(),
            TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }
    /**
     * 组团失败发公众号
     */
    public void groupBuyFailedMessage(List<Integer> userIds, Integer groupId,  Integer groupBuyId, String grouperName, String goodsName) {
        logger().info("拼团失败 发送消息");
        String page = "pages1/pinintegration/pinintegration?groupId="+groupId;
        List<Integer> userIdList = new ArrayList<Integer>(userIds);
        String first="您好，您参加的拼团由于团已过期，拼团失败";
        String remake="您的退款会在1~3个工作日按原账户返还";
        String[][] data = new String[][] { { first, "#173177" }, { goodsName, "#173177" }, { "", "#173177" }, {remake, "#173177" } };
        RabbitMessageParam param = RabbitMessageParam.builder()
                .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.GROUP_FAIL).data(data).build())
                .page(page).shopId(getShopId()).userIdList(userIdList).type(RabbitParamConstant.Type.FAIL_TEAM)
                .build();
        logger().info("准备发组团瓜分积失败");
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
                TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 拼团成功
     *
     * @param groupId     团id
     * @param date        时间
     * @param orderSnList 订单号
     */
    private void updateGroupSuccess(Integer groupId, Timestamp date, List<String> orderSnList) {
        db().update(GROUP_BUY_LIST).set(GROUP_BUY_LIST.STATUS, STATUS_SUCCESS).set(GROUP_BUY_LIST.END_TIME, date)
            .where(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).and(GROUP_BUY_LIST.ORDER_SN.in(orderSnList)).execute();
    }
    /**
     * 拼团中
     * @param groupId     团id
     * @param date        时间
     * @param orderSn 订单号
     */
    public void updateGroupSuccess(Integer groupId, Timestamp date, String orderSn) {
        db().update(GROUP_BUY_LIST).set(GROUP_BUY_LIST.STATUS, STATUS_ONGOING).set(GROUP_BUY_LIST.END_TIME, date)
                .where(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).and(GROUP_BUY_LIST.ORDER_SN.eq(orderSn)).execute();
    }

    /**
     * 活动详情
     *
     * @param param 拼团id 用户id
     * @param lang 语音
     * @return
     */
    public GroupBuyInfoVo getGroupBuyInfo(GroupBuyInfoParam param, String lang) {
        GroupBuyInfoVo groupBuyInfoVo = new GroupBuyInfoVo();
        GroupBuyListRecord grouperInfo = groupBuyListService.getGrouperByGroupId(param.getGroupId());
        if (Objects.isNull(grouperInfo)){
            logger().debug("拼团不存在或已经删除,[groupId:{}]",param.getGroupId());
            GroupBuyStatusInfo statusInfo = new GroupBuyStatusInfo();
            statusInfo.setStatus(JsonResultCode.GROUP_BUY_GROUPID_DOES_NOT_EXIST.getCode());
            statusInfo.setMessage(Util.translateMessage(lang, JsonResultMessage.GROUP_BUY_GROUPID_DOES_NOT_EXIST, "messages"));
            groupBuyInfoVo.setStatusInfo(statusInfo);
            return groupBuyInfoVo;
        }
        Byte isGrouper;
        if (param.getUserId().equals(grouperInfo.getUserId())){
            isGrouper=IS_GROUPER_Y;
        }else {
            isGrouper =IS_GROUPER_N;
        }
        Timestamp date = DateUtils.getLocalDateTime();
        // 拼团状态
        ResultMessage resultMessage = groupBuyListService.canCreatePinGroupOrder(param.getUserId(), date, grouperInfo.getActivityId(), grouperInfo.getGroupId(), isGrouper);
        //拼团活动
        GroupBuyDefineInfo groupBuy = getGroupBuyRecord(grouperInfo.getActivityId()).into(GroupBuyDefineInfo.class);
        Result<Record3<Integer, BigDecimal, Short>> groupBuyProductRecord = db().select(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID, GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE, GROUP_BUY_PRODUCT_DEFINE.STOCK).from(GROUP_BUY_PRODUCT_DEFINE).where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(grouperInfo.getActivityId())).fetch();
        //商品
        Optional<GoodsRecord> goodsRecord = goodsService.getGoodsById(grouperInfo.getGoodsId());
        if (!goodsRecord.isPresent()){
            GroupBuyStatusInfo statusInfo = new GroupBuyStatusInfo();
            statusInfo.setStatus(JsonResultCode.GROUP_BUY_GROUPID_DOES_NOT_EXIST.getCode());
            statusInfo.setMessage(Util.translateMessage(lang, JsonResultMessage.GROUP_BUY_GROUPID_DOES_NOT_EXIST, "messages"));
            groupBuyInfoVo.setStatusInfo(statusInfo);
          return groupBuyInfoVo;
        }
        GroupBuyGoodsInfo goodsInfo = goodsRecord.get().into(GroupBuyGoodsInfo.class);
        //sku
        List<GoodsSpecProductRecord> prdInfos = goodsSpecProductService.getGoodsDetailPrds(grouperInfo.getGoodsId());
        List<GoodsPrdMpVo> prdMpVos = prdInfos.stream().map(GoodsPrdMpVo::new).collect(Collectors.toList());
        prdMpVos.forEach(prd -> {
            groupBuyProductRecord.forEach(groupPrd -> {
                //拼团规格价修改
                if (prd.getPrdId().equals(groupPrd.component1())) {
                    prd.setPrdLinePrice(prd.getPrdRealPrice());
                    prd.setPrdRealPrice(groupPrd.component2());
                    prd.setPrdNumber(groupPrd.component3().intValue());
                }
            });
        });
        //用户
        List<GroupBuyUserInfo> userList = groupBuyListService.getGroupUserList(param.getGroupId());
        boolean newUser = orderInfoService.isNewUser(param.getUserId());
        //是否需要绑定手机号
        Byte bindMobile = shopCommonConfigService.getBindMobile();
        Integer groupBuyStock = groupBuyProductRecord.stream().mapToInt(Record3<Integer, BigDecimal, Short>::component3).sum();
        BigDecimal maxPrice = groupBuyProductRecord.stream().map(Record3<Integer, BigDecimal, Short>::component2).distinct().max(BigDecimal::compareTo).get();
        BigDecimal minPrice = groupBuyProductRecord.stream().map(Record3<Integer, BigDecimal, Short>::component2).distinct().min(BigDecimal::compareTo).get();
        long dateDiff = date.getTime() - DateUtils.getLocalDateTime().getTime();
        long hour = 23 - (dateDiff / (60 * 60 * 1000));
        long min = 59 - (dateDiff % (60 * 60 * 1000)) / (60 * 1000);
        long s = 59 - ((dateDiff % (60 * 60 * 1000)) % (60 * 1000)) / 1000;

        goodsInfo.setGroupBuygoodsNum(groupBuyStock);
        goodsInfo.setMaxGroupBuyPrice(maxPrice);
        goodsInfo.setMinGroupBuyPrice(minPrice);
        GroupBuyStatusInfo statusInfo = new GroupBuyStatusInfo();
        statusInfo.setStatus(resultMessage.getJsonResultCode().getCode());
        statusInfo.setMessage(Util.translateMessage(lang, resultMessage.getJsonResultCode().getMessage(), "messages"));
        groupBuyInfoVo.setGoodsInfo(goodsInfo);
        groupBuyInfoVo.setPrdSpecsList(prdMpVos);
        groupBuyInfoVo.setStatusInfo(statusInfo);
        groupBuyInfoVo.setUserInfoList(userList);
        groupBuyInfoVo.setGroupBuyDefineInfo(groupBuy);
        groupBuyInfoVo.setHour(Math.toIntExact(hour));
        groupBuyInfoVo.setMinute(Math.toIntExact(min));
        groupBuyInfoVo.setSecond(Math.toIntExact(s));
        groupBuyInfoVo.setBindMobile(bindMobile);
        groupBuyInfoVo.setNewUser(newUser);
        return groupBuyInfoVo;
    }


    /**
     * 根据拼团id获取拼团活动详情
     *
     * @param activityId 拼团活动id
     * @return 拼团详情 null 表示该活动已删除
     */
    public GroupBuyDefineRecord getGroupBuyDefinedInfoBuyId(Integer activityId) {
        return db().selectFrom(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(GROUP_BUY_DEFINE.ID.eq(activityId)))
            .fetchAny();
    }

    /**
     * 根据拼团id获取拼团活动详情
     *
     * @param activityId 拼团活动id
     * @return 拼团详情 null 表示该活动已删除
     */
    public String getGroupBuyNameBuyId(Integer activityId) {
        return db().select(GROUP_BUY_DEFINE.NAME).from(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(GROUP_BUY_DEFINE.ID.eq(activityId)))
            .fetchAnyInto(String.class);
    }

    /**
     * 用户是否可以进行拼团下单-商品详情-拼团信息使用
     *
     * @param userId     用户id
     * @param date       品团比较时间（默认为当前时间）
     * @param activityId 拼团活动id
     * @return 0正常;1该活动不存在;2该活动已停用;3该活动未开始;4该活动已结束;5商品已抢光;6该用户已达到限购数量上限
     */
    protected Byte canCreatePinGroupOrder(Integer userId, Timestamp date, Integer activityId, GroupBuyDefineRecord groupBuyDefineRecord) {
        logger().debug("小程序-商品详情-拼团信息-是否可以参与活动判断");
        if (date == null) {
            date = DateUtils.getLocalDateTime();
        }

        if (groupBuyDefineRecord == null) {
            logger().debug("小程序-商品详情-拼团信息-活动不存在或已删除[activityId:{}]", activityId);
            return BaseConstant.ACTIVITY_STATUS_NOT_HAS;
        }

        if (BaseConstant.ACTIVITY_STATUS_DISABLE.equals(groupBuyDefineRecord.getStatus())) {
            logger().debug("小程序-商品详情-拼团信息-该活动未启用[activityId:{}]", activityId);
            return BaseConstant.ACTIVITY_STATUS_STOP;
        }

        if (groupBuyDefineRecord.getStartTime().compareTo(date) > 0) {
            logger().debug("活动未开始[activityId:{}]", activityId);
            return BaseConstant.ACTIVITY_STATUS_NOT_START;
        }

        if (groupBuyDefineRecord.getEndTime().compareTo(date) < 0) {
            logger().debug("活动已经结束[activityId:{}]", activityId);
            return BaseConstant.ACTIVITY_STATUS_END;
        }

        int openCount = groupBuyListService.getUserOpenGroupBuyActivityNum(userId, activityId);

        if (!groupBuyDefineRecord.getOpenLimit().equals(GroupBuyConstant.OPEN_LIMIT_N.shortValue()) && groupBuyDefineRecord.getOpenLimit() <= openCount) {
            logger().debug("该活动开团个数已经达到上限[activityId:{}]", activityId);
            return BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT;
        }

        return BaseConstant.ACTIVITY_STATUS_CAN_USE;
    }

    /**
     * 修改拼团库存和销量
     *
     * @param activityId
     * @param productId
     * @param goodsNumber 商品数量
     * @return
     */
    public boolean updateGroupBuyStock(Integer activityId, Integer productId, Integer goodsNumber) {
        //规格库存`
        int prdFlag = db().update(GROUP_BUY_PRODUCT_DEFINE)
            .set(GROUP_BUY_PRODUCT_DEFINE.STOCK, GROUP_BUY_PRODUCT_DEFINE.STOCK.minus(goodsNumber))
            .set(GROUP_BUY_PRODUCT_DEFINE.SALE_NUM, GROUP_BUY_PRODUCT_DEFINE.SALE_NUM.add(goodsNumber))
            .where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(activityId))
            .and(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID.eq(productId))
            .and(GROUP_BUY_PRODUCT_DEFINE.STOCK.ge(goodsNumber.shortValue())).execute();
        if (prdFlag == 1) {
            //总库存
            int tolFlag = db().update(GROUP_BUY_DEFINE)
                .set(GROUP_BUY_DEFINE.STOCK, GROUP_BUY_DEFINE.STOCK.minus(goodsNumber))
                .set(GROUP_BUY_DEFINE.SALE_NUM, GROUP_BUY_DEFINE.SALE_NUM.add(goodsNumber))
                .where(GROUP_BUY_DEFINE.ID.eq(activityId))
                .and(GROUP_BUY_DEFINE.STOCK.ge(goodsNumber.shortValue())).execute();
            if (tolFlag == 1) {
                return true;
            } else {
                //库存修改失败
                db().update(GROUP_BUY_PRODUCT_DEFINE)
                    .set(GROUP_BUY_PRODUCT_DEFINE.STOCK, GROUP_BUY_PRODUCT_DEFINE.STOCK.add(goodsNumber))
                    .set(GROUP_BUY_PRODUCT_DEFINE.SALE_NUM, GROUP_BUY_PRODUCT_DEFINE.SALE_NUM.minus(goodsNumber))
                    .where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(activityId))
                    .and(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID.eq(productId)).execute();
            }
        }
        return false;
    }


    /**
     * 从admin扫码活动码查看活动下的商品信息
     * @param activityId 活动id
     * @param baseCondition 过滤商品id基础条件
     * @return 可用商品id集合
     */
    public List<Integer> getGroupBuyCanUseGoodsIds(Integer activityId, Condition baseCondition) {
        Timestamp now = DateUtils.getLocalDateTime();
        GroupBuyDefineRecord record = getGroupBuyDefinedInfoBuyId(activityId);
        if (record == null || record.getEndTime().compareTo(now) <= 0) {
            logger().debug("小程序-admin-groupbuy-扫码进小程序搜索列表页-活动已删除或停止");
            return new ArrayList<>();
        }

        logger().debug("小程序-admin-groupbuy-扫码进小程序搜索列表页-搜索商品goodsType是拼团类型且在本拼团活动下的可用商品");
        List<Integer> goodsIds = db().selectDistinct(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID)
            .from(GROUP_BUY_PRODUCT_DEFINE).innerJoin(GOODS).on(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(baseCondition.and(GOODS.GOODS_TYPE.eq(BaseConstant.ACTIVITY_TYPE_GROUP_BUY)).and(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(activityId)))
            .fetch(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID);
        logger().debug("小程序-admin-groupbuy-扫码进小程序搜索列表页-goodsIds:{}", goodsIds);

        Integer level = record.getLevel();
        // 未删除，停止，时间有效的活动
        Condition activityCondition = GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(GROUP_BUY_DEFINE.END_TIME.gt(now));
        // 时间上有交集
        Condition timeCondition = GROUP_BUY_DEFINE.START_TIME.le(record.getEndTime()).and(GROUP_BUY_DEFINE.START_TIME.gt(record.getStartTime())).or(GROUP_BUY_DEFINE.END_TIME.gt(record.getStartTime()).and(GROUP_BUY_DEFINE.END_TIME.lt(record.getEndTime())));
        // 级别要高，或者级别相同但是创建的较晚
        Condition levelCondition = GROUP_BUY_DEFINE.LEVEL.gt(level).or(GROUP_BUY_DEFINE.LEVEL.eq(level).and(GROUP_BUY_DEFINE.CREATE_TIME.gt(record.getCreateTime())));

        List<Integer> otherGoodsIds = db().selectDistinct(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID).from(GROUP_BUY_DEFINE).innerJoin(GROUP_BUY_PRODUCT_DEFINE).on(GROUP_BUY_DEFINE.ID.eq(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID))
            .where(activityCondition.and(timeCondition).and(levelCondition).and(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.in(goodsIds)))
            .fetch(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID);

        goodsIds.removeAll(otherGoodsIds);
        return goodsIds;
    }

    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
	public MarketVo getActInfo(Integer id) {
		return db()
				.select(GROUP_BUY_DEFINE.ID, GROUP_BUY_DEFINE.NAME.as(CalendarAction.ACTNAME),
						GROUP_BUY_DEFINE.START_TIME, GROUP_BUY_DEFINE.END_TIME)
				.from(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.ID.eq(id)).fetchAnyInto(MarketVo.class);
	}

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(GROUP_BUY_DEFINE.ID, GROUP_BUY_DEFINE.NAME.as(CalendarAction.ACTNAME),
						GROUP_BUY_DEFINE.START_TIME, GROUP_BUY_DEFINE.END_TIME)
				.from(GROUP_BUY_DEFINE)
				.where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)
								.and(GROUP_BUY_DEFINE.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(GROUP_BUY_DEFINE.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}

    /**
     * 拼团说明
     * @param id
     * @return 拼团说明 or null
     */
    public String getActivityCopywriting(Integer id) {
        Record1<String> record1 = db().select(GROUP_BUY_DEFINE.ACTIVITY_COPYWRITING).from(GROUP_BUY_DEFINE).where(GROUP_BUY_DEFINE.ID.eq(id)).fetchAny();
        return record1.component1();
    }
}
