package com.meidianyi.shop.service.shop.market.gift;

import static com.meidianyi.shop.common.foundation.util.Util.listToString;
import static com.meidianyi.shop.common.foundation.util.Util.numberToString;
import static com.meidianyi.shop.common.foundation.util.Util.stringToList;
import static com.meidianyi.shop.common.foundation.util.Util.underLineStyleGson;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.MemberCard.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.Tag.TAG;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.select;
import static org.springframework.util.StringUtils.isEmpty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Record7;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectWhereStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.Gift;
import com.meidianyi.shop.db.shop.tables.GiftProduct;
import com.meidianyi.shop.db.shop.tables.GoodsSpecProduct;
import com.meidianyi.shop.db.shop.tables.records.GiftProductRecord;
import com.meidianyi.shop.db.shop.tables.records.GiftRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPriceBo;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftDetailListParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftDetailListVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftListParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftListVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.LevelParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.ProductVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.RuleJson;
import com.meidianyi.shop.service.pojo.shop.market.gift.RuleParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.RuleVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.UserAction;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.market.gift.GiftGoodsListParam;
import com.meidianyi.shop.service.pojo.wxapp.market.gift.GiftGoodsListVo;
import com.meidianyi.shop.service.pojo.wxapp.market.gift.ShowCartConfigVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.gift.OrderGiftProductVo;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.member.GoodsCardCoupleService;
import com.meidianyi.shop.service.shop.user.cart.CartService;

import jodd.util.StringUtil;

/**
 * 赠品
 *
 * @author 郑保乐
 */
@Service
@Primary
public class GiftService extends ShopBaseService {
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    protected ImageService imageService;
    @Autowired
    private GoodsCardCoupleService goodsCardCoupleService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private CartService cartService;

    public static final Gift TABLE = Gift.GIFT;
    public static final GiftProduct SUB_TABLE = GiftProduct.GIFT_PRODUCT;
    public static final GoodsSpecProduct PRODUCT = GoodsSpecProduct.GOODS_SPEC_PRODUCT;

    private static final int MAX_RULE_SIZE = 3;

    /**赠送满足赠品条件的所有赠品*/
    protected static final Byte CONDITION_ALL = 0;

    /**只赠送其中优先级最高的活动赠品*/
    public static final Byte CONDITION_PRIORITY = 1;

    /**
     * 添加赠品活动
     */
    public void addGift(GiftParam param) {
        validateRules(param);
        db().transaction(configuration -> {
            DSLContext db = DSL.using(configuration);
            // 保存活动表
            int giftId = insertGift(db, param);
            // 保存赠品规格表
            param.setId(giftId);
            insertProduct(db, param);
        });
    }

    /**
     * 保存赠品规格
     */
    private int insertGift(DSLContext db, GiftParam param) {
        transformParam(param);
        GiftRecord giftRecord = db.newRecord(TABLE, param);
        giftRecord.insert();
        return giftRecord.getId();
    }

    /**
     * 格式转换
     */
    private void transformParam(GiftParam param) {
        RuleParam rules = param.getRules();
        RuleJson json = getRuleJson(rules);
        String ruleJson = underLineStyleGson().toJson(json);
        String goodsId = listToString(param.getGoodsIds());
        if (isEmpty(goodsId)) {
            goodsId = null;
        }
        param.setGoodsId(goodsId);
        param.setRule(ruleJson);
    }

    /**
     * 赠品规则 json 转换
     */
    private RuleJson getRuleJson(RuleParam param) {
        RuleJson ruleJson = new RuleJson();
        ruleJson.setFullNumber(numberToString(param.getFullNumber()));
        ruleJson.setCardId(listToString(param.getCardId()));
        ruleJson.setFullPrice(numberToString(param.getFullPrice()));
        ruleJson.setMaxPayNum(numberToString(param.getMaxPayNum()));
        ruleJson.setMinPayNum(numberToString(param.getMinPayNum()));
        ruleJson.setPayTop(numberToString(param.getPayTop()));
        ruleJson.setUserAction(numberToString(param.getUserAction()));
        ruleJson.setTagId(listToString(param.getTagId()));
        ruleJson.setPayEndTime(param.getPayEndTime());
        ruleJson.setPayStartTime(param.getPayStartTime());
        return ruleJson;
    }

    /**
     * 保存赠品规格
     */
    private void insertProduct(DSLContext db, GiftParam param) {
        List<GiftProductRecord> giftList = getProductsOf(db, param);
        db.batchInsert(giftList).execute();
    }

    /**
     * 校验赠品规则参数
     * 判断提交的规则条数是否超过限制
     */
    private void validateRules(GiftParam param) {
        RuleParam ruleParam = param.getRules();
        List<List<Supplier<?>>> rules = ruleParam.getRules();
        int counteri = 0;
        for (List<Supplier<?>> suppliers : rules) {
            int size = suppliers.size();
            int counterj = 0;
            for (Supplier<?> supplier : suppliers) {
                if (null != supplier.get()) {
                    counterj++;
                }
            }
            if (size == counterj && 0 != size) {
                counteri++;
            }
            if (MAX_RULE_SIZE < counteri) {
                throw new IllegalArgumentException("Rules reached number limit: " + MAX_RULE_SIZE);
            }
        }
    }

    /**
     * 删除活动
     */
    public void deleteGift(Integer id) {

        db().update(TABLE).set(TABLE.STATUS, (byte) 0).set(TABLE.DEL_FLAG, (byte) 1).set(TABLE.DEL_TIME,
            Util.currentTimeStamp()).where(TABLE.ID.eq(id)).execute();
    }

    /**
     * 停用活动
     */
    public void disableGift(Integer id) {
        db().update(TABLE).set(TABLE.STATUS, (byte) 0).where(TABLE.ID.eq(id)).execute();
    }

    /**
     * 启用活动
     */
    public void enableGift(Integer id) {
        db().update(TABLE).set(TABLE.STATUS, (byte) 1).where(TABLE.ID.eq(id)).execute();
    }

    /**
     * 编辑活动 - 查询明细
     */
    public GiftVo getGiftDetail(Integer giftId) {
        GiftVo giftVo = db().selectFrom(TABLE).where(TABLE.ID.eq(giftId)).fetchOneInto(GiftVo.class);
        transformVo(giftVo);
        List<ProductVo> productVos = getGiftProduct(giftId);
        productVos.forEach(vo -> vo.setOfferNumber(getGiftOrderedNumber(vo.getProductId(), giftId)));
        giftVo.setGifts(productVos);
        return giftVo;
    }

    /**
     * 获取活动赠品
     */
    private List<ProductVo> getGiftProduct(Boolean isEffective,Integer... giftId) {
        SelectWhereStep<? extends Record> select = (SelectWhereStep<? extends Record>) db().select(SUB_TABLE.ID,SUB_TABLE.GIFT_ID,SUB_TABLE.PRODUCT_ID,SUB_TABLE.PRODUCT_NUMBER,
                PRODUCT.PRD_IMG,PRODUCT.PRD_PRICE,PRODUCT.PRD_DESC,GOODS.GOODS_NAME,GOODS.GOODS_IMG)
            .select(PRODUCT.PRD_PRICE, PRODUCT.PRD_IMG, PRODUCT.PRD_NUMBER, PRODUCT.PRD_DESC)
            .select(GOODS.GOODS_NAME, GOODS.GOODS_IMG)
            .from(SUB_TABLE)
            .leftJoin(PRODUCT).on(PRODUCT.PRD_ID.eq(SUB_TABLE.PRODUCT_ID))
            .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(PRODUCT.GOODS_ID))
            .where(SUB_TABLE.GIFT_ID.in(giftId));
        if(isEffective){
            select.where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(PRODUCT.PRD_NUMBER.gt(0));
        }
        return select.fetchInto(ProductVo.class);
    }

    /**
     * 获取活动赠品
     */
    protected List<ProductVo> getGiftProduct(Integer... giftId) {
        return getGiftProduct(false,giftId);
    }

    /**
     * 获取活动赠品(下单)
     */
    protected List<OrderGiftProductVo> getOrderGiftProducts(Integer giftId) {
        return db().select(SUB_TABLE.GIFT_ID,SUB_TABLE.PRODUCT_ID,SUB_TABLE.PRODUCT_NUMBER,
            PRODUCT.PRD_IMG,PRODUCT.PRD_PRICE,PRODUCT.PRD_DESC,GOODS.GOODS_NAME,GOODS.GOODS_IMG)
            .select(PRODUCT.PRD_PRICE, PRODUCT.PRD_IMG, PRODUCT.PRD_NUMBER, PRODUCT.PRD_DESC, PRODUCT.PRD_SN)
            .select(GOODS.GOODS_ID,GOODS.GOODS_SN, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.DELIVER_TEMPLATE_ID, GOODS.SHOP_PRICE, GOODS.GOODS_WEIGHT, GOODS.CAT_ID, GOODS.SORT_ID)
            .from(SUB_TABLE)
            .leftJoin(PRODUCT).on(PRODUCT.PRD_ID.eq(SUB_TABLE.PRODUCT_ID))
            .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(PRODUCT.GOODS_ID))
            .where(SUB_TABLE.GIFT_ID.eq(giftId).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(SUB_TABLE.PRODUCT_NUMBER.gt(0)))
            .fetchInto(OrderGiftProductVo.class);
    }

   /**
     * 获取商品规格
     */
    public ProductVo getProductDetail(Integer giftId, Integer productId) {
        ProductVo vo = db()
            .select(PRODUCT.PRD_ID.as("productId"), PRODUCT.PRD_PRICE, PRODUCT.PRD_IMG, PRODUCT.PRD_NUMBER,
                PRODUCT.PRD_DESC)
            .select(GOODS.GOODS_NAME, GOODS.GOODS_IMG)
            .from(PRODUCT)
            .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(PRODUCT.GOODS_ID))
            .where(PRODUCT.PRD_ID.eq(productId))
            .fetchOneInto(ProductVo.class);
        if (vo!=null){
            vo.setOfferNumber(getGiftOrderedNumber(productId, giftId));
        }
        if(StringUtils.isNotEmpty(vo.getPrdImg())){
            vo.setPrdImg(domainConfig.imageUrl(vo.getPrdImg()));
        }else if(StringUtils.isNotEmpty(vo.getGoodsImg())){
            vo.setGoodsImg(domainConfig.imageUrl(vo.getGoodsImg()));
        }
        return vo;
    }

    /**
     * 出参格式转换
     */
    public void transformVo(GiftVo giftVo) {
        giftVo.setGoodsIds(stringToList(giftVo.getGoodsId()));
        String rule = giftVo.getRule();
        RuleJson ruleJson = underLineStyleGson().fromJson(rule, RuleJson.class);
        RuleVo ruleVo = getRuleVo(ruleJson);
        giftVo.setRules(ruleVo);
    }

    /**
     * 赠品规则 json 转 vo
     */
    private RuleVo getRuleVo(RuleJson ruleJson) {
        RuleVo ruleVo = new RuleVo();
        ruleVo.setCardId(stringToListNullable(ruleJson.getCardId()));
        ruleVo.setTagId(stringToListNullable(ruleJson.getTagId()));
        ruleVo.setFullNumber(stringToInt(ruleJson.getFullNumber()));
        ruleVo.setFullPrice(ruleJson.getFullPrice() == null ? null : new BigDecimal(ruleJson.getFullPrice()));
        ruleVo.setMaxPayNum(stringToInt(ruleJson.getMaxPayNum()));
        ruleVo.setMinPayNum(stringToInt(ruleJson.getMinPayNum()));
        ruleVo.setPayStartTime(ruleJson.getPayStartTime());
        ruleVo.setPayEndTime(ruleJson.getPayEndTime());
        ruleVo.setPayTop(stringToInt(ruleJson.getPayTop()));
        ruleVo.setUserAction(stringToByte(ruleJson.getUserAction()));
        return ruleVo;
    }

    /**
     * 字符串转 List（null 参数则返回 null，而非空 List）
     */
    private List<Integer> stringToListNullable(String stringValue) {
        if (null == stringValue) {
            return null;
        }
        return stringToList(stringValue);
    }

    /**
     * 字符串转 Byte
     */
    private Byte stringToByte(String value) {
        if (null == value) {
            return null;
        }
        return Byte.valueOf(value);
    }

    /**
     * 字符串转 Double
     */
    private Double stringToDouble(String value) {
        if (null == value) {
            return null;
        }
        return Double.valueOf(value);
    }

    /**
     * 字符串转 Integer
     */
    private Integer stringToInt(String value) {
        if (null == value) {
            return null;
        }
        return Integer.valueOf(value);
    }

    /**
     * 编辑活动 - 更新
     */
    public void updateGift(GiftParam param) {
        Integer giftId = param.getId();
        Assert.notNull(giftId, "Missing parameter id");
        validateRules(param);
        transformParam(param);
        db().transaction(configuration -> {
            DSLContext db = DSL.using(configuration);
            updateGift(db, param);
            deleteProduct(db, giftId);
            insertProduct(db, param);
        });
    }

    /**
     * 更新活动
     */
    private void updateGift(DSLContext db, GiftParam param) {
        GiftRecord giftRecord = db.newRecord(TABLE, param);
        giftRecord.update();
    }

    /**
     * 删除旧的赠品规格
     */
    private void deleteProduct(DSLContext db, Integer giftId) {
        db.delete(SUB_TABLE).where(SUB_TABLE.GIFT_ID.eq(giftId)).execute();
    }

    /**
     * 获取规格 record
     */
    private List<GiftProductRecord> getProductsOf(DSLContext db, GiftParam param) {
        return param.getGifts().stream().map(gift -> {
            GiftProductRecord productRecord = db.newRecord(SUB_TABLE, gift);
            productRecord.setGiftId(param.getId());
            return productRecord;
        }).collect(Collectors.toList());
    }

    /**
     * 赠品活动列表
     */
    /**
     *
     * @param param GiftListParam
     * @return GiftListVo
     */
    public PageResult<GiftListVo> getPageList(GiftListParam param) {
        SelectConditionStep<?> query = getPageListQuery();
        buildOptions(query, param);
        query.orderBy(TABLE.LEVEL.desc(),TABLE.CREATE_TIME.desc());
        PageResult<GiftListVo> page = getPageResult(query, param, GiftListVo.class);
        page.dataList.forEach(vo->{
            vo.setCurrentState(Util.getActStatus(vo.getStatus(),vo.getStartTime(),vo.getEndTime()));
        });
        return page;
    }

    /**
     * 列表查询
     */
    private SelectConditionStep<?> getPageListQuery() {
        Table<Record2<Integer, String>> giftTimes = db()
                .select(ORDER_GOODS.GIFT_ID, ORDER_GOODS.ORDER_SN)
                .from(ORDER_GOODS)
                .where(ORDER_GOODS.IS_GIFT.eq(BaseConstant.YES.intValue()))
                .groupBy(ORDER_GOODS.GIFT_ID, ORDER_GOODS.ORDER_SN).asTable("t");
        Table<Record2<Integer, Integer>> giftTimes2 = db().select(giftTimes.field(ORDER_GOODS.GIFT_ID), DSL.count().as("giftTimes")).from(giftTimes).groupBy(giftTimes.field(ORDER_GOODS.GIFT_ID)).asTable("t2");
        SelectConditionStep<? extends Record7<Integer, String, Timestamp, Timestamp, Short, Byte, ?>> query = db()
                .select(TABLE.ID, TABLE.NAME, TABLE.START_TIME, TABLE.END_TIME, TABLE.LEVEL, TABLE.STATUS, giftTimes2.field("giftTimes"))
                .from(TABLE)
                .leftJoin(giftTimes2).on(giftTimes2.field(ORDER_GOODS.GIFT_ID).eq(TABLE.ID))
                .where(TABLE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        query.groupBy(TABLE.ID, TABLE.NAME, TABLE.START_TIME, TABLE.END_TIME,
                TABLE.LEVEL, TABLE.STATUS,giftTimes2.field("giftTimes"));
        return query;
    }

    /**
     * 查询条件
     */
    private void buildOptions(SelectConditionStep<?> query, GiftListParam param) {
        Byte status = param.getStatus();
        String name = param.getName();
        if (null != status && !status.equals((byte)0)) {
            addStatusCondition(query, status);
        }
        if (isNotEmpty(name)) {
            query.and(TABLE.NAME.like(likeValue(name)));
        }

    }

    /**
     * 状态转换
     */
    private void addStatusCondition(SelectConditionStep<?> query, Byte status) {
        switch (status) {
            case BaseConstant.NAVBAR_TYPE_ONGOING:
                query.and(TABLE.START_TIME.le(Util.currentTimeStamp()))
                        .and(TABLE.END_TIME.gt(Util.currentTimeStamp()));
                query.and(TABLE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                break;
            case BaseConstant.NAVBAR_TYPE_NOT_STARTED:
                query.and(TABLE.START_TIME.gt(Util.currentTimeStamp()));
                query.and(TABLE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                break;
            case BaseConstant.NAVBAR_TYPE_FINISHED:
                query.and(TABLE.END_TIME.lt(Util.currentTimeStamp()));
                query.and(TABLE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                break;
            case BaseConstant.NAVBAR_TYPE_DISABLED:
                query.and(TABLE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                break;
            default:
        }
    }

    /**
     * 修改活动优先级
     */
    public void updateLevel(LevelParam param) {
        db().update(TABLE).set(TABLE.LEVEL, param.getLevel()).where(TABLE.ID.eq(param.getId())).execute();
    }

    /**
     * 赠送明细列表查询
     */
    public PageResult<GiftDetailListVo> getGiftDetailPageList(GiftDetailListParam param) {
        SelectConditionStep<?> query = db().select(ORDER_GOODS.ORDER_SN, USER.USER_ID, USER.USERNAME, USER.MOBILE,
            DSL.sum(ORDER_GOODS.GOODS_NUMBER).as("giftAmount"), ORDER_GOODS.CREATE_TIME).from(ORDER_GOODS)
            .leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
            .leftJoin(USER).on(USER.USER_ID.eq(ORDER_INFO.USER_ID))
            .where(ORDER_GOODS.IS_GIFT.eq(1));
        buildDetailOptions(query, param);
        query.groupBy(ORDER_GOODS.ORDER_SN, ORDER_GOODS.CREATE_TIME, USER.USER_ID, USER.USERNAME, USER.MOBILE)
            .orderBy(ORDER_GOODS.CREATE_TIME.desc());
        return getPageResult(query, param, GiftDetailListVo.class);
    }

    /**
     * 赠品明细查询条件
     */
    private void buildDetailOptions(SelectConditionStep<?> query, GiftDetailListParam param) {
        Timestamp startTime = param.getStartTime();
        Timestamp endTime = param.getEndTime();
        String username = param.getUsername();
        String mobile = param.getMobile();
        Integer giftId = param.getGiftId();
        if (null != startTime) {
            query.and(ORDER_GOODS.CREATE_TIME.ge(startTime));
        }
        if (null != endTime) {
            query.and(ORDER_GOODS.CREATE_TIME.le(endTime));
        }
        if (isNotEmpty(username)) {
            query.and(USER.USERNAME.like(format("%s%%", username)));
        }
        if (isNotEmpty(mobile)) {
            query.and(USER.MOBILE.like(format("%s%%", mobile)));
        }
        query.and(ORDER_GOODS.GIFT_ID.eq(giftId));
    }

    /**
     * 获取会员卡列表
     */
    public List<UserAction> getMemberCardList() {
        return db().select(MEMBER_CARD.ID, MEMBER_CARD.CARD_NAME.as("name")).from(MEMBER_CARD)
            .where(MEMBER_CARD.DEL_FLAG.eq((byte) 0)).fetchInto(UserAction.class);
    }

    /**
     * 获取用户标签列表
     */
    public List<UserAction> getUserTagList() {
        return db().select(TAG.TAG_ID.as("id"), TAG.TAG_NAME.as("name")).from(TAG)
            .fetchInto(UserAction.class);
    }

    /**
     * 获取赠品的已下单未发货单量（已确定将发出的赠品数量）
     */
    private Integer getGiftOrderedNumber(Integer productId, Integer giftId) {
        return db().select(countDistinct(ORDER_INFO.ORDER_SN))
            .from(ORDER_INFO)
            .where(ORDER_INFO.ORDER_STATUS.ge(OrderConstant.ORDER_WAIT_DELIVERY)
            .or(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_PAY)
                    .and(ORDER_INFO.IS_LOCK.ge(OrderConstant.ORDER_WAIT_DELIVERY))))
                .and(ORDER_INFO.ORDER_SN
                    .in(select(ORDER_GOODS.ORDER_SN).from(ORDER_GOODS)
                        .where(ORDER_GOODS.IS_GIFT.eq(1)
                            .and(ORDER_GOODS.PRODUCT_ID.eq(productId)
                                .and(ORDER_GOODS.GIFT_ID.eq(giftId))
                            )
                        )
                    )
            ).fetchOneInto(Integer.class);
    }

    /**
     * 订单-购物车 商品转换
     * @param orderGoods 订单商品
     * @param giftVo 赠品活动
     * @param userId
     */
    public WxAppCartGoods getOrderGoodsToCartGoods(OrderGoodsBo orderGoods, GiftVo giftVo, Integer userId) {
        WxAppCartGoods cartGoods =new WxAppCartGoods();
        cartGoods.setPrdPrice(BigDecimal.ZERO);
        cartGoods.setCartNumber(1);
        cartGoods.setType(cartGoods.getType());
        cartGoods.setExtendId(giftVo.getId());
        cartGoods.setStoreId(0);
        cartGoods.setUserId(userId);
        cartGoods.setGoodsId(orderGoods.getGoodsId());
        cartGoods.setGoodsSn(orderGoods.getGoodsSn());
        cartGoods.setGoodsName(orderGoods.getGoodsName());
        cartGoods.setGoodsImg(getImgFullUrlUtil(orderGoods.getGoodsImg()));
        cartGoods.setPrdDesc("");
        cartGoods.setProductId(orderGoods.getProductId());
        cartGoods.setActivityType(BaseConstant.ACTIVITY_TYPE_GIFT);
        cartGoods.setActivityId(giftVo.getId());
        return cartGoods;
    }

    /**
     * 将相对路劲修改为全路径
     *
     * @param relativePath 相对路径
     * @return null或全路径
     */
    private String getImgFullUrlUtil(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return null;
        } else {
            return imageService.imageUrl(relativePath);
        }
    }

    /**
     * 小程序端活动页数据
     * @param param
     * @param userId
     * @return
     */
    public GiftGoodsListVo getWxAppGoodsList(GiftGoodsListParam param, Integer userId){
        GiftGoodsListVo vo = new GiftGoodsListVo();
        GiftRecord giftRecord = db().fetchAny(TABLE,TABLE.ID.eq(param.getGiftId()));
        if(giftRecord == null || giftRecord.getDelFlag().equals(DelFlag.DISABLE_VALUE)){
            vo.setState((byte)1);
            return vo;
        }else if(giftRecord.getStartTime().after(DateUtils.getLocalDateTime())){
            vo.setState((byte)2);
            return vo;
        }else if(giftRecord.getEndTime().before(DateUtils.getLocalDateTime())){
            vo.setState((byte)3);
            return vo;
        }

        List<Integer> goodsIds = Util.splitValueToList(giftRecord.getGoodsId());
        List<Integer> userExclusiveGoodsIds = goodsCardCoupleService.getGoodsUserNotExclusive(userId);
        goodsIds.removeAll(userExclusiveGoodsIds);

        //主商品
        PageResult<GiftGoodsListVo.Goods> goodsPageResult = getGoods(goodsIds,param.getSearch(),param.getCurrentPage(),param.getPageRows());
        goodsPageResult.getDataList().forEach(goods -> {
            if(StringUtil.isNotEmpty(goods.getGoodsImg())){
                goods.setGoodsImg(domainConfig.imageUrl(goods.getGoodsImg()));
            }
            if(goods.getIsDefaultProduct() == 1){
                goods.setPrdId(saas.getShopApp(getShopId()).goods.goodsSpecProductService.getDefaultPrdId(goods.getGoodsId()));
            }

            //处理限时降价、首单特惠、会员等级价对商品价格的覆盖
            GoodsPriceBo goodsPriceBo = saas.getShopApp(getShopId()).reducePrice.parseGoodsPrice(goods.getGoodsId(),userId);
            goods.setGoodsPriceAction(goodsPriceBo.getGoodsPriceAction());
            goods.setGoodsPrice(goodsPriceBo.getGoodsPrice());
            goods.setMaxPrice(goodsPriceBo.getMaxPrice());
            goods.setMarketPrice(goodsPriceBo.getGoodsPriceAction().equals(Byte.valueOf((byte)0)) ? goodsPriceBo.getMarketPrice()  : goodsPriceBo.getMaxPrice());
        });
        vo.setGoods(goodsPageResult);

        //赠品
        List<ProductVo> giftProductList = getGiftProduct(true,param.getGiftId());
        giftProductList.forEach(p->{
            if(StringUtil.isNotEmpty(p.getGoodsImg())){
                p.setGoodsImg(domainConfig.imageUrl(p.getGoodsImg()));
            }
            if(StringUtil.isNotEmpty(p.getPrdImg())){
                p.setPrdImg(domainConfig.imageUrl(p.getPrdImg()));
            }
        });
        vo.setGiftProductList(giftProductList);

        RuleJson ruleJson = underLineStyleGson().fromJson(giftRecord.getRule(), RuleJson.class);
        RuleVo rule = getRuleVo(ruleJson);
        vo.setRule(rule);

        WxAppCartBo cartBo = cartService.getCartList(userId,goodsIds,null,null);
        vo.setCheckedGoodsPrice(cartBo.getCartGoodsList().stream().map(
            wxAppCartGoods -> wxAppCartGoods.getPrdPrice().multiply(BigDecimal.valueOf(wxAppCartGoods.getCartNumber()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setCartGoodsNumber(cartBo.getTotalGoodsNum());

        //检查满金额和满件数条件
        if(rule.getFullPrice() != null && rule.getFullPrice().compareTo(BigDecimal.ZERO) > 0){
            if(cartBo.getTotalPrice().compareTo(rule.getFullPrice()) < 0){
                vo.setState((byte)4);
            }else{
                vo.setState((byte)0);
            }
        }else if(rule.getFullNumber() != null && rule.getFullNumber() > 0){
            if(cartBo.getTotalGoodsNum() < rule.getFullNumber()){
                vo.setState((byte)5);
            }else {
                vo.setState((byte)0);
            }
        }
        vo.setDelMarket(shopCommonConfigService.getDelMarket());
        vo.setShowCart(new ShowCartConfigVo(shopCommonConfigService.getShowCart()));
        return vo;
    }

    /**
     * 小程序端活动页 已加入购物车的主商品
     * @param param
     * @param userId
     * @return
     */
    public List<WxAppCartGoods> giftCheckedGoodsList(GiftGoodsListParam param, Integer userId){
        GiftRecord giftRecord = db().fetchAny(TABLE,TABLE.ID.eq(param.getGiftId()));
        List<Integer> goodsIds = Util.splitValueToList(giftRecord.getGoodsId());
        List<Integer> userExclusiveGoodsIds = goodsCardCoupleService.getGoodsUserNotExclusive(userId);
        goodsIds.removeAll(userExclusiveGoodsIds);
        WxAppCartBo cartBo = cartService.getCartList(userId,goodsIds,null,null);
        return cartBo.getCartGoodsList();
    }

    /**
     * 查出goods列表
     * @param inGoodsIds
     * @param search
     * @param currentPage
     * @param pageRows
     * @return
     */
    private PageResult<GiftGoodsListVo.Goods> getGoods(List<Integer> inGoodsIds,String search,Integer currentPage,Integer pageRows){
        Byte soldOutGoods = shopCommonConfigService.getSoldOutGoods();
        SelectWhereStep<? extends Record> select = db().select(GOODS.GOODS_ID,GOODS.GOODS_NAME,GOODS.GOODS_IMG,GOODS.SHOP_PRICE,GOODS.MARKET_PRICE,GOODS.CAT_ID,GOODS.GOODS_TYPE,GOODS.SORT_ID,GOODS.IS_CARD_EXCLUSIVE,GOODS.IS_DEFAULT_PRODUCT,GOODS.GOODS_SALE_NUM,GOODS.COMMENT_NUM).from(GOODS);
        select.where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        select.where(GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE));
        if(!NumberUtils.BYTE_ONE.equals(soldOutGoods)){
            select.where(GOODS.GOODS_NUMBER.gt(0));
        }
        if(StringUtil.isNotEmpty(search)){
            select.where(GOODS.GOODS_NAME.contains(search));
        }
        if(CollectionUtils.isNotEmpty(inGoodsIds)){
            select.where(GOODS.GOODS_ID.in(inGoodsIds));
        }
        return getPageResult(select,currentPage,pageRows,GiftGoodsListVo.Goods.class);
    }

    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(TABLE.ID, TABLE.NAME.as(CalendarAction.ACTNAME), TABLE.START_TIME,
				TABLE.END_TIME).from(TABLE).where(TABLE.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(TABLE.ID, TABLE.NAME.as(CalendarAction.ACTNAME), TABLE.START_TIME,
						TABLE.END_TIME)
				.from(TABLE)
				.where(TABLE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(TABLE.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(TABLE.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(TABLE.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}
}
