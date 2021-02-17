package com.meidianyi.shop.service.shop.activity.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.records.GiftProductRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.ProductVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.RuleVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.gift.GoodsGiftMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.gift.GoodsGiftPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.gift.OrderGiftProductVo;
import com.meidianyi.shop.service.shop.config.GiftConfigService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.market.gift.GiftService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.Record6;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Gift.GIFT;
import static com.meidianyi.shop.db.shop.tables.GiftProduct.GIFT_PRODUCT;

/**
 * 赠品processor
 * @author 王帅
 */
@Service
public class GiftProcessorDao extends GiftService {

    @Autowired
    private GiftConfigService giftConfig;

    @Autowired
    private OrderReadService orderRead;

    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    private MemberService member;

    @Autowired
    private UserCardService userCard;

    @Autowired
    private GoodsMpService goodsMpService;

    @Autowired
    private ImageService imageService;

    public final static List<Byte> ORDER_ACT_FILTER;
    static {
        //TODO 好友助力送商品，送礼多人送时过滤
        ORDER_ACT_FILTER = Arrays.asList(
            BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT,
            BaseConstant.ACTIVITY_TYPE_PAY_AWARD,
            BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER
        );
    }


    /**
     * 获取商品的赠品信息
     * @param goodsId 商品ID
     * @return 赠品信息
     */
    public List<GoodsGiftMpVo> getGoodsGiftInfoForDetail(Integer goodsId,Timestamp now){
        // 查询赠品数量大于零的规格
        Condition condition = GIFT.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(GIFT.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(GIFT.START_TIME.le(now)).and(GIFT.END_TIME.ge(now)).and(GIFT_PRODUCT.PRODUCT_NUMBER.gt(0)).and(GIFT.GOODS_ID.isNull().or(DslPlus.findInSet(goodsId,GIFT.GOODS_ID)));

        Result<Record6<Integer, String, String, Integer, Integer, Integer>> giftResults = db().
            select(GIFT.ID, GIFT.RULE, GIFT.EXPLAIN, GIFT_PRODUCT.ID, GIFT_PRODUCT.PRODUCT_ID, GIFT_PRODUCT.PRODUCT_NUMBER)
            .from(GIFT).innerJoin(GIFT_PRODUCT).on(GIFT.ID.eq(GIFT_PRODUCT.GIFT_ID))
            .where(condition).orderBy(GIFT.LEVEL.desc(), GIFT.CREATE_TIME.desc()).fetch();

        if (giftResults == null || giftResults.size() == 0) {
            return null;
        }

        // 获取对应有效规格的信息
        List<Integer> giftPrdIds = giftResults.stream().map(item -> item.get(GIFT_PRODUCT.PRODUCT_ID)).collect(Collectors.toList());
        List<GoodsGiftPrdMpVo> prdInfos = goodsMpService.getGoodsDetailGiftPrdsInfoDao(giftPrdIds);
        Map<Integer, GoodsGiftPrdMpVo> prdInfoMap = prdInfos.stream().collect(Collectors.toMap(GoodsGiftPrdMpVo::getProductId, Function.identity()));

        Map<Integer,GoodsGiftMpVo> returnMap = new HashMap<>(prdInfoMap.size());

        for (Record6<Integer, String, String, Integer, Integer, Integer> giftResult : giftResults) {

            GoodsGiftPrdMpVo goodsGiftPrdMpVo = prdInfoMap.get(giftResult.get(GIFT_PRODUCT.PRODUCT_ID));
            // 对应的商品规格信息已不存在
            if (goodsGiftPrdMpVo == null) {
                continue;
            }
            goodsGiftPrdMpVo.setId(giftResult.get(GIFT_PRODUCT.ID));

            GoodsGiftMpVo goodsGiftMpVo = returnMap.get(giftResult.get(GIFT.ID));
            if (goodsGiftMpVo == null) {
                goodsGiftMpVo =new GoodsGiftMpVo();
                goodsGiftMpVo.setId(giftResult.get(GIFT.ID));
                goodsGiftMpVo.setExplain(giftResult.get(GIFT.EXPLAIN));
                String rule = giftResult.get(GIFT.RULE);
                goodsGiftMpVo.setIsFullPrice(rule!=null&&(rule.contains("full_price") || rule.contains("full_number")));
                goodsGiftMpVo.setGoodsGiftPrdMpVos(new ArrayList<>());
                returnMap.put(giftResult.get(GIFT.ID),goodsGiftMpVo);
            }
            goodsGiftPrdMpVo.setPrdImg(imageService.getImgFullUrl(goodsGiftPrdMpVo.getPrdImg()));
            goodsGiftMpVo.getGoodsGiftPrdMpVos().add(goodsGiftPrdMpVo);
        }

        return new ArrayList<>(returnMap.values());
    }


    /**
     * 下单获取赠品
     * @param userId
     * @param goodsBo
     */
    public void getGifts(Integer userId, List<OrderGoodsBo> goodsBo, List<Byte> orderType){
        logger().info("下单获取赠品start");
        for(Byte type : orderType) {
            if (ORDER_ACT_FILTER.contains(type)) {
                logger().info("该活动不可参与赠品活动，type:{}", type);
                return;
            }
        }
        //googsBo转map,聚合相同规格(k->prdId;v->数量)
        Map<Integer, Integer> goodsMapCount = goodsBo.stream().collect(Collectors.toMap(OrderGoodsBo::getProductId, OrderGoodsBo::getGoodsNumber, (ov, nv) -> ov + nv));
        //商品未参与赠品记录
        Set<Integer> noJoinRecord = goodsBo.stream().map(OrderGoodsBo::getGoodsId).collect(Collectors.toSet());
        //0：赠送满足赠品条件的所有赠品;1：只赠送其中优先级最高的活动赠品
        Byte cfg = giftConfig.getCfg();
        //所有进行中的活动
        List<GiftVo> activitys = getActiveActivity();
        activitys.forEach(activity->{
            if(CONDITION_PRIORITY.equals(cfg) && noJoinRecord.size() == 0){
                //只送最高优先级：如果当前未参与商品为0则
                return;
            }
            final int[] number = {0};
            final BigDecimal[] price = {BigDecimal.ZERO};
            //当前活动参与商品
            Set<Integer> joinRecord = Sets.newHashSet();
            //转化
            transformVo(activity);
            goodsBo.forEach(goods->{
                boolean canJoin = goods.getIsGift() == OrderConstant.NO && (CollectionUtils.isEmpty(activity.getGoodsIds()) || activity.getGoodsIds().contains(goods.getGoodsId()));
                if(canJoin){
                    number[0] = number[0] + goods.getGoodsNumber();
                    price[0] = price[0].add(goods.getDiscountedTotalPrice());
                    joinRecord.add(goods.getGoodsId());
                }
            });
            if(number[0] > 0){
                List<OrderGoodsBo> orderGoodsBos = packageAndCheckGift(userId, activity, price[0], number[0], goodsMapCount, orderType, noJoinRecord);
                if(CollectionUtils.isNotEmpty(orderGoodsBos)){
                    goodsBo.addAll(orderGoodsBos);
                    noJoinRecord.removeAll(joinRecord);
                }
            }
        });
        logger().info("下单获取赠品end");
    }

    /**
     * 获取所有进行中的活动
     */
    public List<GiftVo> getActiveActivity(){
        Timestamp now = DateUtils.getSqlTimestamp();
        return db().select(TABLE.ID, TABLE.NAME, TABLE.START_TIME, TABLE.END_TIME, TABLE.LEVEL, TABLE.STATUS, TABLE.GOODS_ID, TABLE.RULE, TABLE.EXPLAIN)
            .from(TABLE)
            .where(TABLE.DEL_FLAG.eq(DelFlag.NORMAL.getCode())
                .and(TABLE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(TABLE.START_TIME.le(now))
                .and(TABLE.END_TIME.gt(now)))
            .orderBy(TABLE.LEVEL.desc())
            .fetchInto(GiftVo.class);
    }

    /**
     * 获取当前符合当前活动规则的赠品
     * @param userId
     * @param giftVo
     * @param price
     * @param number
     * @param goodsMapCount
     * @param orderType
     * @param noJoinRecord
     * @return
     */
    public List<OrderGoodsBo> packageAndCheckGift(Integer userId, GiftVo giftVo, BigDecimal price, int number, Map<Integer, Integer> goodsMapCount, List<Byte> orderType, Set<Integer> noJoinRecord) {
        RuleVo rules = giftVo.getRules();
        if(rules.getFullPrice() != null && !orderType.contains(BaseConstant.ACTIVITY_TYPE_EXCHANG_ORDER) && rules.getFullPrice().compareTo(price) <= 0){
            logger().info("赠品：满金额满足,活动id:{}", giftVo.getId());
            return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
        }
        if(rules.getFullNumber() != null && rules.getFullNumber() <= number){
            logger().info("赠品：满件数满足,活动id:{}", giftVo.getId());
            return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
        }
        if(CollectionUtils.isNotEmpty(rules.getCardId())){
            logger().info("赠品：会员卡满足,活动id:{}", giftVo.getId());
            List<ValidUserCardBean> validCardList = userCard.userCardDao.getValidCardList(userId, new Byte[]{CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE}, UserCardDaoService.CARD_ONLINE);
            List<Integer> cardIds = validCardList.stream().map(ValidUserCardBean::getCardId).collect(Collectors.toList());
            cardIds.retainAll(rules.getCardId());
            if(CollectionUtils.isNotEmpty(cardIds)){
                logger().info("赠品：会员卡满足,活动id:{}", giftVo.getId());
                return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
            }
        }
        if(CollectionUtils.isNotEmpty(rules.getTagId())){
            List<TagVo> tagVos = member.getTagForMember(userId);
            Collection<Integer> tagIds = tagVos.stream().map(TagVo::getTagId).collect(Collectors.toList());
            tagIds.retainAll(rules.getTagId());
            if(CollectionUtils.isNotEmpty(tagIds)){
                logger().info("赠品：会员标签满足,活动id:{}", giftVo.getId());
                return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
            }
        }
        if(rules.getPayStartTime() != null && rules.getPayEndTime() !=null){
            Timestamp now = DateUtils.getSqlTimestamp();
            if(now.after(rules.getPayStartTime()) && now.before(rules.getPayEndTime())){
                logger().info("赠品：付款时间满足,活动id:{}", giftVo.getId());
                return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
            }
        }
        if(rules.getPayTop() !=null){
            Integer giftOrderCount = orderRead.getGiftOrderCount(giftVo.getId(), false);
            if(giftOrderCount < rules.getPayTop()){
                logger().info("赠品：付款排名满足,活动id:{}", giftVo.getId());
                return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
            }
        }
        if(rules.getMaxPayNum() != null && rules.getMinPayNum() != null){
            //仅在店铺内购买活动商品达到指定次数的用户可获得赠品
            Integer giftOrderCount = orderInfo.getGoodsBuyNum(userId, giftVo.getGoodsIds());
            if(giftOrderCount >= rules.getMinPayNum() && giftOrderCount <= rules.getMaxPayNum()){
                logger().info("赠品：已购买指定商品次数满足,活动id:{}", giftVo.getId());
                return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
            }
        }
        if(rules.getUserAction() !=null){
            boolean newUser = orderInfo.isNewUser(userId);
            if(rules.getUserAction().equals(newUser ? OrderConstant.USER_ACTION_NEW : OrderConstant.USER_ACTION_OLD)){
                logger().info("赠品：新老用户满足,活动id:{}", giftVo.getId());
                return packageGift(giftVo.getId(), noJoinRecord, goodsMapCount);
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 打包赠品
     * @param id
     * @param noJoinRecord
     * @param goodsMapCount
     */
    private List<OrderGoodsBo> packageGift(Integer id, Set<Integer> noJoinRecord, Map<Integer, Integer> goodsMapCount) {
        List<OrderGiftProductVo> giftProducts = getOrderGiftProducts(id);
        List<OrderGoodsBo> bos = Lists.newArrayList();
        giftProducts.forEach(gPrd->{
            if(gPrd.getProductNumber() <= 0 && gPrd.getPrdNumber() - (goodsMapCount.get(gPrd.getProductId() == null ? 0 : gPrd.getProductId())) - 1 < 0){
                //规格数量校验失败
                return;
            }
            if(goodsMapCount.get(gPrd.getProductId())!= null){
                //赠品规格与商品规格数量相加->校验数量
                goodsMapCount.put(gPrd.getProductId(), goodsMapCount.get(gPrd.getProductId()) + 1);
            }
            bos.add(gPrd.toOrderGoodsBo());
        });
        return bos;
    }

    /**
     * 赠品库存
     * @param param k->giftId, v->(k->prdId, v->数量（>0下单，<0退款）)
     */
    public void updateStockAndSales(Map<Integer, Map<Integer, Integer>> param) throws MpException {
        List<ProductVo> productVos = getGiftProduct(param.keySet().toArray(new Integer[0]));
        List<GiftProductRecord> result = Lists.newArrayList();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : param.entrySet()) {
            for (Map.Entry<Integer, Integer> entry1 : entry.getValue().entrySet()) {
                GiftProductRecord giftProductRecord = db().newRecord(SUB_TABLE);
                ListIterator<ProductVo> iterator = productVos.listIterator();
                while (iterator.hasNext()){
                    ProductVo crt = iterator.next();
                    if(crt.getGiftId().equals(entry.getKey()) && crt.getProductId().equals(entry1.getKey())){
                        if(entry1.getValue() > 0 && crt.getProductNumber() < entry1.getValue()){
                            logger().error("下单时赠品已经送完，请重新下单");
                            throw new MpException(JsonResultCode.CODE_ORDER_GIFT_GOODS_ZERO);
                        }
                        giftProductRecord.setId(crt.getId());
                        giftProductRecord.setProductNumber(crt.getProductNumber() - entry1.getValue());
                        iterator.remove();
                        break;
                    }
                }
                if(giftProductRecord.getProductNumber() == null){
                    if(entry1.getValue() > 0){
                        logger().error("下单时赠品已经送完，请重新下单");
                        throw new MpException(JsonResultCode.CODE_ORDER_GIFT_GOODS_ZERO);
                    }
                }else{
                    logger().info("{}时更新赠品库存,giftId={},productId={}",entry1.getValue() > 0 ? "下单" : "退款", entry.getKey(), entry1.getKey());
                    result.add(giftProductRecord);
                }
            }
        }
        db().batchUpdate(result).execute();
    }

    /**
     * 支付接口校验赠品
     * @param giftId 赠品活动id
     * @param productId 赠品规格id
     * @param number 商品数量
     */
    public boolean toPayCheck(Integer giftId, Integer productId, Integer number){
        GiftVo info = getGiftDetail(giftId);
        if(info == null || info.getEndTime().before(DateUtils.getSqlTimestamp()) || BaseConstant.ACTIVITY_STATUS_DISABLE.equals(info.getStatus()) || !checkStock(info, productId, number)) {
            return false;
        }
        return true;
    }

    /**
     * 赠品校验库存
     * @param info
     * @param productId
     * @param number
     * @return
     */
    private boolean checkStock(GiftVo info, Integer productId,  Integer number){
        for (ProductVo productVo : info.getGifts()) {
            if(productVo.getProductId().equals(productId) && productVo.getProductNumber() >= number){
                return true;
            }
        }
        return false;
    }

    /**
     * 赠品活动
     * @param goodsId 商品id
     * @return
     */
    public Result<Record6<Integer, String, String, Integer, Integer, Integer>> getGiftsActivity(Integer goodsId, Timestamp nowTime) {
        Condition condition = GIFT.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(GIFT.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(GIFT.START_TIME.le(nowTime))
                .and(GIFT.END_TIME.ge(nowTime))
                .and(GIFT_PRODUCT.PRODUCT_NUMBER.gt(0))
                .and(GIFT.GOODS_ID.isNull().or(DslPlus.findInSet(goodsId,GIFT.GOODS_ID)));
        return db().select(GIFT.ID, GIFT.RULE, GIFT.EXPLAIN, GIFT_PRODUCT.ID, GIFT_PRODUCT.PRODUCT_ID, GIFT_PRODUCT.PRODUCT_NUMBER)
                .from(GIFT).innerJoin(GIFT_PRODUCT).on(GIFT.ID.eq(GIFT_PRODUCT.GIFT_ID))
                .where(condition).orderBy(GIFT.LEVEL.desc(), GIFT.CREATE_TIME.desc()).fetch();
    }
}
