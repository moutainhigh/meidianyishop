package com.meidianyi.shop.service.shop.market.increasepurchase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.*;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.PurchasePriceDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.PurchasePriceRuleRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPriceBo;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.increasepurchase.*;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagSrcConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase.PurchaseChangeGoodsParam;
import com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase.PurchaseChangeGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase.PurchaseGoodsListParam;
import com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase.PurchaseGoodsListVo;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.GoodsCardCoupleService;
import com.meidianyi.shop.service.shop.member.TagService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.dao.foundation.database.DslPlus.concatWs;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.PurchasePriceDefine.PURCHASE_PRICE_DEFINE;
import static com.meidianyi.shop.db.shop.tables.PurchasePriceRule.PURCHASE_PRICE_RULE;
import static com.meidianyi.shop.service.pojo.shop.market.form.FormConstant.MAPPER;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.*;
import static org.jooq.impl.DSL.*;

/**
 * @author liufei
 * @date 2019/8/14
 */
@Slf4j
@Service
public class IncreasePurchaseService extends ShopBaseService {
    private static OrderGoods og = OrderGoods.ORDER_GOODS.as("og");
    private static OrderInfo oi = OrderInfo.ORDER_INFO.as("oi");
    private static Goods g = Goods.GOODS.as("g");
    private static User u = User.USER.as("u");
    private static PurchasePriceDefine ppd = PURCHASE_PRICE_DEFINE.as("ppd");
    private static PurchasePriceRule ppr = PURCHASE_PRICE_RULE.as("ppr");

    @Autowired
    public QrCodeService qrCodeService;
    @Autowired
    private GoodsCardCoupleService goodsCardCoupleService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private CartService cartService;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TagService tagService;

    /**
     * 分页查询加价购活动信息
     *
     * @param param 查询条件
     * @return 分页数据
     */
    public PageResult<PurchaseShowVo> selectByPage(PurchaseShowParam param) {
        //默认查询结果不包含已删除信息
        Condition categoryConditon = ppd.DEL_FLAG.eq(FLAG_ZERO);
        switch (param.getCategory()) {
            // 所有0
            case PURCHASE_ALL:
                break;
            // 已停用4
            case PURCHASE_TERMINATED:
                categoryConditon = categoryConditon.and(ppd.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                break;
            // 已过期3
            case PURCHASE_EXPIRED:
                categoryConditon = categoryConditon.and(ppd.END_TIME.lessThan(Timestamp.valueOf(LocalDateTime.now()))).and(ppd.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                break;
            // 未开始2
            case PURCHASE_PREPARE:
                categoryConditon = categoryConditon.and(ppd.START_TIME.greaterThan(Timestamp.valueOf(LocalDateTime.now()))).and(ppd.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                break;
            // 默认进行中1
            default:
                categoryConditon = categoryConditon.and(ppd.START_TIME.lessThan(Timestamp.valueOf(LocalDateTime.now()))).and(ppd.END_TIME.greaterThan(Timestamp.valueOf(LocalDateTime.now()))).and(ppd.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));;
                break;
        }
        Table<Record9<Integer, String, Short, Short, Timestamp, Timestamp, Byte, Byte, Timestamp>> conditionStep = db().
            select(ppd.ID, ppd.NAME, ppd.LEVEL, ppd.MAX_CHANGE_PURCHASE, ppd.START_TIME, ppd.END_TIME, ppd.STATUS, ppd.DEL_FLAG,ppd.CREATE_TIME).from(ppd).where(categoryConditon).asTable("ppd");

        Condition selectConditon = ppd.DEL_FLAG.eq(FLAG_ZERO);
        if (StringUtils.isNotEmpty(param.getName())) {
            selectConditon = selectConditon.and(ppd.NAME.like(this.likeValue(param.getName())));
        }
        if (param.getStartTime() != null) {
            selectConditon = selectConditon.and(ppd.END_TIME.ge(param.getStartTime()));
        }
        if (param.getEndTime() != null) {
            selectConditon = selectConditon.and(ppd.START_TIME.le(param.getEndTime()));
        }
        if (param.getFullPriceUp() != null&&!BigDecimal.ZERO.equals(param.getFullPriceUp())) {
            selectConditon = selectConditon.and(ppr.FULL_PRICE.lessOrEqual(param.getFullPriceUp()));
        }
        if (param.getFullPriceDown() != null&&!BigDecimal.ZERO.equals(param.getFullPriceDown())) {
            selectConditon = selectConditon.and(ppr.FULL_PRICE.greaterOrEqual(param.getFullPriceDown()));
        }
        if (param.getPurchasePriceUp() != null&&!BigDecimal.ZERO.equals(param.getPurchasePriceUp())) {
            selectConditon = selectConditon.and(ppr.PURCHASE_PRICE.lessOrEqual(param.getPurchasePriceUp()));
        }
        if (param.getPurchasePriceDown() != null&&!BigDecimal.ZERO.equals(param.getPurchasePriceDown())) {
            selectConditon = selectConditon.and(ppr.PURCHASE_PRICE.greaterOrEqual(param.getPurchasePriceDown()));
        }

        SelectConditionStep<Record8<Integer, String, Short, Short, Timestamp, Timestamp, Byte, Timestamp>> resultStep = db().
            selectDistinct(ppd.ID, ppd.NAME, ppd.LEVEL, ppd.MAX_CHANGE_PURCHASE, ppd.START_TIME, ppd.END_TIME, ppd.STATUS,ppd.CREATE_TIME).from(conditionStep).leftJoin(ppr).on(ppd.ID.eq(ppr.PURCHASE_PRICE_ID)).where(selectConditon);
        PageResult<PurchaseShowVo> pageResult = this.getPageResult(resultStep.orderBy(ppd.LEVEL.desc(),ppd.CREATE_TIME.desc()), param.getCurrentPage(), param.getPageRows(), PurchaseShowVo.class);
        for (PurchaseShowVo vo : pageResult.getDataList()) {
            Integer purchaseId = vo.getId();
            vo.setResaleQuantity(getResaleQuantity(purchaseId));
            vo.setPurchaseInfo(getPurchaseDetailInfo(purchaseId));
            vo.setCategory(Util.getActStatus(vo.getStatus(),vo.getStartTime(),vo.getEndTime()));
            vo.setTimestamp(DateUtils.getLocalDateTime());
        }
        return pageResult;
    }

    private List<String> getPurchaseDetailInfo(Integer purchasePriceId) {
        List<String> list = db().select(concatWs(CONCAT_WS_SEPARATOR, ppr.FULL_PRICE, ppr.PURCHASE_PRICE)).from(ppr).where(ppr.PURCHASE_PRICE_ID.eq(purchasePriceId)).orderBy(ppr.ID).fetch(concatWs(CONCAT_WS_SEPARATOR, ppr.FULL_PRICE, ppr.PURCHASE_PRICE));
        log.debug("获取加价购活动详细规则 [{}]", list);
        return list;
    }


    /**
     * 计算已换购数量
     */
    private Integer getResaleQuantity(Integer purchasePriceId) {
        Integer defaultValue = 0;
        return db().select(sum(og.GOODS_NUMBER)).from(og).leftJoin(oi).on(og.ORDER_SN.eq(oi.ORDER_SN))
            .where(og.PURCHASE_ID.eq(purchasePriceId))
            .and(og.ACTIVITY_RULE.greaterThan(0))
            .and(oi.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY))
            .fetchOptionalInto(Integer.class).orElse(defaultValue);
    }

    /**
     * 添加加价购活动
     *
     * @param param 加价购活动详情参数
     */
    public void addIncreasePurchase(AddPurchaseParam param) {
        PurchasePriceDefineRecord defineRecord = new PurchasePriceDefineRecord();
        PurchasePriceRuleRecord ruleRecord = new PurchasePriceRuleRecord();
        FieldsUtil.assignNotNull(param, defineRecord);
        if(CollectionUtils.isNotEmpty(param.getActivityTagId())){
            defineRecord.setActivityTagId(Util.listToString(param.getActivityTagId()));
        }
        defineRecord.setStatus(BaseConstant.ACTIVITY_STATUS_NORMAL);
        db().transaction(configuration -> {
            DSLContext db = DSL.using(configuration);
            db.executeInsert(defineRecord);
            Integer pruchaseId = db.lastID().intValue();
            log.debug("获取刚刚插入PurchasePriceDefine表后改表自增主键自动生成的值：{}", pruchaseId);
            for (PurchaseRule rule : param.getRules()) {
                FieldsUtil.assignNotNull(rule, ruleRecord);
                ruleRecord.setPurchasePriceId(pruchaseId);
                db.executeInsert(ruleRecord);
                ruleRecord.reset();
            }
        });
    }

    /**
     * 更新加价购活动
     * TODO FieldsUtil.assignNotNull不支持继承父类的的属性赋值
     *
     * @param param 加价购活动详情参数
     */
    public void updateIncreasePurchase(UpdatePurchaseParam param) {
        Integer count = db().selectCount().from(ppd).where(ppd.ID.eq(param.getId())).fetchOptionalInto(Integer.class).orElseThrow(() -> new RuntimeException("Information doesn't exist!"));
        if (count == 0) {
            throw new RuntimeException("Information doesn't exist!");
        }
        PurchasePriceDefineRecord defineRecord = new PurchasePriceDefineRecord();
        PurchasePriceRuleRecord ruleRecord = new PurchasePriceRuleRecord();
        FieldsUtil.assignNotNull(param, defineRecord);
        db().transaction(configuration -> {
            DSLContext db = DSL.using(configuration);
            db.executeUpdate(defineRecord);
            for (PurchaseRule rule : param.getRules()) {
                FieldsUtil.assignNotNull(rule, ruleRecord);
                ruleRecord.setPurchasePriceId(param.getId());
                db.executeUpdate(ruleRecord);
                ruleRecord.reset();
            }
        });
    }

    /**
     * 获取加价购活动详细信息
     *
     * @param param 加价购活动id
     */
    @SuppressWarnings("unchecked")
    public PurchaseDetailVo getPurchaseDetail(PurchaseDetailParam param) {
        PurchaseDetailVo vo = db().select(ppd.ID, ppd.NAME, ppd.LEVEL, ppd.MAX_CHANGE_PURCHASE, ppd.START_TIME, ppd.END_TIME, ppd.GOODS_ID,ppd.REDEMPTION_FREIGHT,ppd.ACTIVITY_TAG,ppd.ACTIVITY_TAG_ID).from(ppd).where(ppd.ID.eq(param.getPurchaseId())).fetchOptionalInto(PurchaseDetailVo.class).orElseThrow(() -> new RuntimeException("Information doesn't exist!"));
        vo.setPurchaseInfo(getPurchaseDetailInfo(param.getPurchaseId()));
        String goodsId = vo.getGoodsId();
        //主商品详情
        Integer[] goodsIdArray = stringArray2Int(goodsId.split(","));
        vo.setMainGoods(db().selectFrom(g).where(g.GOODS_ID.in(goodsIdArray)).fetchInto(GoodsInfo.class));
        vo.getMainGoods().forEach(g->{
            if(StringUtil.isNotBlank(g.getGoodsImg())){
                g.setGoodsImg(domainConfig.imageUrl(g.getGoodsImg()));
            }
        });
        //换购商品详情
        List<String> redemptionGoods = db().select(ppr.PRODUCT_ID).from(ppr).where(ppr.PURCHASE_PRICE_ID.eq(param.getPurchaseId())).orderBy(ppr.ID).fetchInto(String.class);
        List<ProductSmallInfoVo>[] redemptionresult = new List[redemptionGoods.size()];
        int integer = 0;
        for (String s : redemptionGoods) {
            Integer[] array = stringArray2Int(s.split(","));
            List<ProductSmallInfoVo> redemptionGoodsList =
                db().select(g.GOODS_ID,g.GOODS_IMG,g.GOODS_NAME,g.SHOP_PRICE,GOODS_SPEC_PRODUCT.PRD_ID,GOODS_SPEC_PRODUCT.PRD_NUMBER,GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS_SPEC_PRODUCT.PRD_DESC)
                .from(GOODS_SPEC_PRODUCT).innerJoin(g).on(g.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
                .where(GOODS_SPEC_PRODUCT.PRD_ID.in(array)).fetchInto(ProductSmallInfoVo.class);
            redemptionGoodsList.forEach(g->{
                if(StringUtil.isNotBlank(g.getGoodsImg())){
                    g.setGoodsImg(domainConfig.imageUrl(g.getGoodsImg()));
                }
            });
            redemptionresult[integer++] = redemptionGoodsList;
        }
        vo.setRedemptionGoods(redemptionresult);
        if(vo.getActivityTag().equals(FLAG_ONE) && StringUtil.isNotBlank(vo.getActivityTagId())){
            vo.setTagList(tagService.getTagsById(Util.splitValueToList(vo.getActivityTagId())));
        }
        return vo;
    }

    private Integer[] stringArray2Int(String[] source) {
        Integer[] target = new Integer[source.length];
        int i = 0;
        for (String s : source) {
            target[i++] = Integer.valueOf(s);
        }
        return target;
    }

    /**
     * 停用/启用/删除加价购活动
     *
     * @param param 活动id和被修改的状态值
     */
    public void changeTheStatus(PurchaseStatusParam param) {
        switch (param.getStatus()) {
            case CONDITION_ZERO:
                //启用
                db().update(ppd).set(ppd.STATUS, BaseConstant.ACTIVITY_STATUS_NORMAL).where(ppd.ID.eq(param.getId())).execute();
                break;
            case CONDITION_ONE:
                //停用
                db().update(ppd).set(ppd.STATUS, BaseConstant.ACTIVITY_STATUS_DISABLE).where(ppd.ID.eq(param.getId())).execute();
                break;
            case CONDITION_TWO:
                //删除，更新删除标志和删除时间
                db().update(ppd).set(ppd.DEL_FLAG, FLAG_ONE).set(ppd.DEL_TIME,Timestamp.valueOf(LocalDateTime.now())).where(ppd.ID.eq(param.getId())).execute();
                break;
            default:
                break;
        }
    }

    /**
     * 分享,获取小程序二维码
     *
     * @param param 表单id
     * @return 图片路径
     */
    public ShareQrCodeVo share(PurchaseStatusParam param) {
        String pathParam = IDENTITY_ID + param.getId();
        String imageUrl = qrCodeService.getMpQrCode(QrCodeTypeEnum.RAISE_PRICE_BUY_MAIN_GOODS, pathParam);
        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(QrCodeTypeEnum.SECKILL_GOODS_ITEM_INFO.getPathUrl(pathParam));
        return vo;

    }

    /**
     * 查看换购订单列表
     *
     * @param param 加价购活动id和筛选条件
     * @return 分页数据
     */
    public PageResult<RedemptionOrderVo> getRedemptionOrderList(MarketOrderListParam param) {
        //默认排序方式---下单时间
        PageResult<RedemptionOrderVo> vo = this.getPageResult(buildRedemptionListOption(param).groupBy(og.ORDER_SN).orderBy(oi.CREATE_TIME), param.getCurrentPage(), param.getPageRows(), RedemptionOrderVo.class);

        log.debug("进行商品展示信息的组合");
        preExportList(vo.getDataList());
        return vo;
    }

    /**
     * Build redemption list option select condition step.
     * 构建换购订单列表查询条件
     * activity_type:营销活动类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品 6限时降价 7加价购'
     *
     * @param param the param
     * @return the select condition step
     */
    private SelectConditionStep<Record11<String, String, String, String, String, String, String, Timestamp, String, String, String>> buildRedemptionListOption(MarketOrderListParam param) {
        SelectConditionStep<Record11<String, String, String, String, String, String, String, Timestamp, String, String, String>> conditionStep = db().select(min(oi.ORDER_SN).as("orderSn"), groupConcat(og.GOODS_ID, GROUPCONCAT_SEPARATOR).as("concatId"), groupConcat(og.GOODS_NAME, GROUPCONCAT_SEPARATOR).as("concatName"), groupConcat(og.GOODS_NUMBER, GROUPCONCAT_SEPARATOR).as("concatNumber"), groupConcat(og.PURCHASE_ID, GROUPCONCAT_SEPARATOR).as("activityIds"), groupConcat(og.ACTIVITY_RULE, GROUPCONCAT_SEPARATOR).as("activityRules"), groupConcat(g.GOODS_IMG, GROUPCONCAT_SEPARATOR).as("concatImg"), min(oi.CREATE_TIME).as("createTime"), min(oi.CONSIGNEE).as("consignee"), min(oi.MOBILE).as("mobile"), min(oi.ORDER_STATUS_NAME).as("orderStatusName")).from(og).leftJoin(g).on(og.GOODS_ID.eq(g.GOODS_ID)).leftJoin(oi).on(og.ORDER_SN.eq(oi.ORDER_SN)).where(og.PURCHASE_ID.eq(param.getActivityId()));

        if (StringUtils.isNotBlank(param.getGoodsName())) {
            conditionStep = conditionStep.and(og.GOODS_NAME.like(likeValue(param.getGoodsName())));
        }
        if (StringUtils.isNotBlank(param.getOrderSn())) {
            conditionStep = conditionStep.and(og.ORDER_SN.like(likeValue(param.getOrderSn())));
        }
        if (StringUtils.isNotBlank(param.getConsignee())) {
            conditionStep = conditionStep.and(oi.CONSIGNEE.like(likeValue(param.getConsignee())));
        }
        if (StringUtils.isNotBlank(param.getMobile())) {
            conditionStep = conditionStep.and(oi.MOBILE.like(likeValue(param.getMobile())));
        }
        if(param.getOrderStatus() != null && param.getOrderStatus().length != 0){
            conditionStep = conditionStep.and(oi.ORDER_STATUS.in(param.getOrderStatus()));
        }
        if (param.getProvinceCode() != null) {
            conditionStep = conditionStep.and(oi.PROVINCE_CODE.eq(param.getProvinceCode()));
        }
        if (param.getCityCode() != null) {
            conditionStep = conditionStep.and(oi.CITY_CODE.eq(param.getCityCode()));
        }
        if (param.getDistrictCode() != null) {
            conditionStep = conditionStep.and(oi.DISTRICT_CODE.eq(param.getDistrictCode()));
        }
        return conditionStep;
    }

    /**
     * 换购订单列表导出
     *
     * @param param 筛选条件和导出起始页
     * @return Workbook
     */
    public Workbook exportOrderList(MarketOrderListParam param) {
        List<RedemptionOrderVo> list = buildRedemptionListOption(param).groupBy(og.ORDER_SN).orderBy(oi.CREATE_TIME).fetchInto(RedemptionOrderVo.class);
        preExportList(list);
        return this.export(list, RedemptionOrderVo.class);
    }

    /**
     * Pre export.
     * 导出换购订单列表前数据处理
     *
     * @param list the list
     */
    private void preExportList(List<RedemptionOrderVo> list) {
        for (RedemptionOrderVo redemption : list) {
            String[] concatIds = redemption.getConcatId().split(GROUPCONCAT_SEPARATOR);
            String[] concatNames = redemption.getConcatName().split(GROUPCONCAT_SEPARATOR);
            String[] concatNumbers = redemption.getConcatNumber().split(GROUPCONCAT_SEPARATOR);
            String[] activityRules = redemption.getActivityRules().split(GROUPCONCAT_SEPARATOR);
            String[] concatImgs = redemption.getConcatImg().split(GROUPCONCAT_SEPARATOR);
            List<RedemptionGoodsInfo> mainGoods = new ArrayList<>();
            List<RedemptionGoodsInfo> redempGoods = new ArrayList<>();
            for (int i = 0; i < concatIds.length; i++) {
                log.debug("activityRule字段不为默认值0，说明该商品在本次订单中属于加购商品（非主商品）");
                if (Integer.valueOf(activityRules[i]) > 0) {
                    redempGoods.add(new RedemptionGoodsInfo(Integer.valueOf(concatIds[i]), concatNames[i], concatImgs[i], Integer.valueOf(concatNumbers[i])));
                } else {
                    mainGoods.add(new RedemptionGoodsInfo(Integer.valueOf(concatIds[i]), concatNames[i], concatImgs[i], Integer.valueOf(concatNumbers[i])));
                }
            }
            redemption.setMainGoods(mainGoods);
            redemption.setRedemptionGoods(redempGoods);
            try {
                redemption.setMainGoodsString(MAPPER.writeValueAsString(mainGoods));
                redemption.setRedemptionGoodsString(MAPPER.writeValueAsString(redempGoods));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查看换购明细
     *
     * @param param 加价购活动id和筛选条件
     * @return 分页数据
     */
    public PageResult<RedemptionDetailVo> getRedemptionDetail(RedemptionDetailParam param) {
        if (param.getRedemptionNum() != null && param.getRedemptionNum() != 0) {
            return getRedemptionDetailSpec(param);
        }
        PageResult<RedemptionDetailVo> vo = this.getPageResult(buildDetailSelectStep(param), param.getCurrentPage(), param.getPageRows(), RedemptionDetailVo.class);
        preExportDetail(vo.getDataList());
        return vo;
    }

    /**
     * 查看换购明细
     * 含有换购数量筛选条件时采用此方法
     *
     * @param param 加价购活动id和筛选条件
     * @return 分页数据
     */
    private PageResult<RedemptionDetailVo> getRedemptionDetailSpec(RedemptionDetailParam param) {
        //由于筛选条件中可能含有换购数量，所以只能现找出换购信息，然后在挨个查询主商品信息
        SelectSeekStep1<Record7<Integer, String, String, String, Timestamp, BigDecimal, BigDecimal>, Timestamp> conditionStep = db()
            .select(min(oi.USER_ID).as("userId")
                , min(u.USERNAME).as("username")
                , min(u.MOBILE).as("mobile")
                , min(oi.ORDER_SN).as("orderSn")
                , min(oi.CREATE_TIME).as("createTime")
                , sum(og.GOODS_PRICE).as("redemptionTotalMoney")
                , sum(og.GOODS_NUMBER).as("redemptionNum"))
            .from(og).leftJoin(oi).on(og.ORDER_SN.eq(oi.ORDER_SN)).leftJoin(u).on(oi.USER_ID.eq(u.USER_ID))
            .where(og.ACTIVITY_RULE.greaterThan(0))
            .and(buildRedemptionDetailOption(param))
            .groupBy(og.ORDER_SN).having(sum(og.GOODS_NUMBER).eq(BigDecimal.valueOf(param.getRedemptionNum())))
            .orderBy(oi.CREATE_TIME);

        //获取分页数据
        PageResult<RedemptionDetailVo> vo = this.getPageResult(conditionStep, param.getCurrentPage(), param.getPageRows(), RedemptionDetailVo.class);
        //循环查询主商品信息
        for (RedemptionDetailVo redemption : vo.getDataList()) {
            String orderSn = redemption.getOrderSn();
            //当ACTIVITY_RULE字段值为0时，表明该订单商品为加价购活动中的主商品，统计主商品总金额
            BigDecimal mainGoodsTotalMoney = db().select(sum(og.GOODS_PRICE))
                .from(og).leftJoin(oi).on(og.ORDER_SN.eq(oi.ORDER_SN)).leftJoin(u).on(oi.USER_ID.eq(u.USER_ID))
                .where(og.ACTIVITY_RULE.eq(0))
                .and(og.ORDER_SN.eq(orderSn))
                .and(buildRedemptionDetailOption(param))
                .fetchOne().value1();
            redemption.setMainGoodsTotalMoney(mainGoodsTotalMoney);
        }
        return vo;
    }

    /**
     * 构建换购明细查询条件
     */
    private Condition buildRedemptionDetailOption(RedemptionDetailParam param) {
        Condition baseCondition = and(og.PURCHASE_ID.eq(param.getActivityId()));
        if (StringUtils.isNotEmpty(param.getNickName())) {
            baseCondition = baseCondition.and(u.USERNAME.like(this.likeValue(param.getNickName())));
        }
        if (StringUtils.isNotEmpty(param.getPhoneNumber())) {
            baseCondition = baseCondition.and(u.MOBILE.like(this.likeValue(param.getPhoneNumber())));
        }
        return baseCondition;
    }

    /**
     * 构建分页查询ConditionStep
     */
    private SelectSeekStep1<Record8<Integer, String, String, String, Timestamp, String, String, String>, Timestamp> buildDetailSelectStep(RedemptionDetailParam param) {
        return db()
            .select(min(oi.USER_ID).as("userId")
                , min(u.USERNAME).as("username")
                , min(u.MOBILE).as("mobile")
                , min(oi.ORDER_SN).as("orderSn")
                , min(oi.CREATE_TIME).as("createTime")
                , groupConcat(og.DISCOUNTED_TOTAL_PRICE, GROUPCONCAT_SEPARATOR).as("concatPrices")
                , groupConcat(og.GOODS_NUMBER, GROUPCONCAT_SEPARATOR).as("concatNumbers")
                , groupConcat(og.ACTIVITY_RULE, GROUPCONCAT_SEPARATOR).as("activityRules"))
            .from(og).leftJoin(oi).on(og.ORDER_SN.eq(oi.ORDER_SN)).leftJoin(u).on(oi.USER_ID.eq(u.USER_ID))
            .where(buildRedemptionDetailOption(param))
            .groupBy(og.ORDER_SN).orderBy(oi.CREATE_TIME);
    }

    /**
     * 换购明细导出
     *
     * @param param 筛选条件和导出起始页
     * @return Workbook
     */
    public Workbook exportOrderDetail(RedemptionDetailParam param) {
        List<RedemptionDetailVo> list = buildDetailSelectStep(param).fetchInto(RedemptionDetailVo.class);
            preExportDetail(list);
        return this.export(list, RedemptionDetailVo.class);
    }

    /**
     * Pre export.
     * 导出换购明细前数据处理
     *
     * @param list the list
     */
    private void preExportDetail(List<RedemptionDetailVo> list) {
        for (RedemptionDetailVo redemption : list) {
            String[] concatPrices = redemption.getConcatPrices().split(GROUPCONCAT_SEPARATOR);
            String[] concatNumbers = redemption.getConcatNumbers().split(GROUPCONCAT_SEPARATOR);
            String[] activityRules = redemption.getActivityRules().split(GROUPCONCAT_SEPARATOR);
            BigDecimal mainMoney = new BigDecimal(0);
            BigDecimal redempMoney = new BigDecimal(0);
            Integer redempNum = 0;
            for (int i = 0; i < activityRules.length; i++) {
                if (Integer.valueOf(activityRules[i]) > 0) {
                    redempMoney = redempMoney.add(new BigDecimal(concatPrices[i]));
                    redempNum += Integer.valueOf(concatNumbers[i]);
                } else {
                    mainMoney = mainMoney.add(new BigDecimal(concatPrices[i]));
                }
            }
            redemption.setMainGoodsTotalMoney(mainMoney);
            redemption.setRedemptionNum(redempNum);
            redemption.setRedemptionTotalMoney(redempMoney);
        }
    }

    /**
     * Update priority.更新活动优先级
     */
    public void updatePriority(UpdatePriorityParam param){
        if (db().fetchExists(db().selectFrom(ppd).where(ppd.ID.eq(param.getId())))){
            db().update(ppd).set(ppd.LEVEL,param.getPriority()).where(ppd.ID.eq(param.getId())).execute();
            return;
        }
        throw new NullPointerException("Activity Not Exist !");
    }

    /**
     * 小程序端活动页数据
     * @param param
     * @param userId
     * @return
     */
    public PurchaseGoodsListVo getWxAppGoodsList(PurchaseGoodsListParam param, Integer userId){
        PurchaseGoodsListVo vo = new PurchaseGoodsListVo();
        PurchasePriceDefineRecord purchasePriceDefineRecord = db().selectFrom(ppd).where(ppd.ID.eq(param.getPurchasePriceId())).fetchAny();
        if(purchasePriceDefineRecord == null || purchasePriceDefineRecord.getDelFlag().equals(DelFlag.DISABLE_VALUE)){
            vo.setState((byte)1);
            return vo;
        }else if(purchasePriceDefineRecord.getStartTime().after(DateUtils.getLocalDateTime())){
            vo.setState((byte)2);
            return vo;
        }else if(purchasePriceDefineRecord.getEndTime().before(DateUtils.getLocalDateTime())){
            vo.setState((byte)3);
            return vo;
        }
        vo.setState((byte)0);

        //换购规则
        List<PurchasePriceRuleRecord> ruleRecords = getRules(param.getPurchasePriceId());
        List<PurchaseGoodsListVo.Rule> rules = new ArrayList<>();
        ruleRecords.forEach(r->{
            PurchaseGoodsListVo.Rule rule = new PurchaseGoodsListVo.Rule();
            rule.setFullPrice(r.getFullPrice());
            rule.setPurchasePrice(r.getPurchasePrice());
            rules.add(rule);
        });
        vo.setRules(rules);

        //用户的购物车
        WxAppCartBo cartBo = cartService.getCartList(userId,null, BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE,param.getPurchasePriceId());
        BigDecimal totalPrice = cartBo.getCartGoodsList().stream().map(
            wxAppCartGoods -> wxAppCartGoods.getPrdPrice().multiply(BigDecimal.valueOf(wxAppCartGoods.getCartNumber()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setMainPrice(totalPrice);
        vo.setChangeDoc(getChangeGoodsDoc(totalPrice,ruleRecords));

        //过滤掉用户不能买的专属商品
        List<Integer> inGoodsIds = Util.splitValueToList(purchasePriceDefineRecord.getGoodsId());
        List<Integer> userExclusiveGoodsIds = goodsCardCoupleService.getGoodsUserNotExclusive(userId);
        inGoodsIds.removeAll(userExclusiveGoodsIds);

        //商品列表
        PageResult<PurchaseGoodsListVo.Goods> goodsPageResult = getGoods(inGoodsIds,param.getSearch(),param.getCurrentPage(),param.getPageRows());
        goodsPageResult.getDataList().forEach(goods->{
            GoodsPriceBo goodsPriceBo = saas.getShopApp(getShopId()).reducePrice.parseGoodsPrice(goods.getGoodsId(),userId);
            goods.setGoodsPriceAction(goodsPriceBo.getGoodsPriceAction());
            goods.setGoodsPrice(goodsPriceBo.getGoodsPrice());
            goods.setMaxPrice(goodsPriceBo.getMaxPrice());
            goods.setMarketPrice(goodsPriceBo.getMaxPrice());
            goods.setLimitAmount(goodsPriceBo.getLimitAmount());
            if(StringUtil.isNotEmpty(goods.getGoodsImg())){
                goods.setGoodsImg(domainConfig.imageUrl(goods.getGoodsImg()));
            }
            if(goods.getIsDefaultProduct() == 1){
                goods.setPrdId(goodsService.goodsSpecProductService.getDefaultPrdId(goods.getGoodsId()));
            }

            goods.setCartGoodsNumber(cartBo.getCartGoodsList().stream().filter(cartGoods->cartGoods.getGoodsId().equals(goods.getGoodsId())).mapToInt(WxAppCartGoods::getCartNumber).sum());
        });
        vo.setGoods(goodsPageResult);

        return vo;
    }

    /**
     * 查出goods列表
     * @param inGoodsIds
     * @param search
     * @param currentPage
     * @param pageRows
     * @return
     */
    private PageResult<PurchaseGoodsListVo.Goods> getGoods(List<Integer> inGoodsIds,String search,Integer currentPage,Integer pageRows){
        Byte soldOutGoods = shopCommonConfigService.getSoldOutGoods();
        SelectWhereStep<? extends Record> select = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.SHOP_PRICE, GOODS.MARKET_PRICE, GOODS.CAT_ID, GOODS.GOODS_TYPE, GOODS.SORT_ID, GOODS.IS_CARD_EXCLUSIVE, GOODS.IS_DEFAULT_PRODUCT, GOODS.GOODS_NUMBER).from(GOODS);
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
        return getPageResult(select,currentPage,pageRows,PurchaseGoodsListVo.Goods.class);
    }

    /**
     * 根据活动ID取换购规则
     * @param purchasePriceId
     * @return
     */
    private List<PurchasePriceRuleRecord> getRules(int purchasePriceId){
        return db().selectFrom(ppr).where(ppr.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(ppr.PURCHASE_PRICE_ID.eq(purchasePriceId)).fetch();
    }

    /**
     *
     * @param totalPrice
     * @param ruleRecords
     * @return
     */
    private PurchaseGoodsListVo.ChangeDoc getChangeGoodsDoc(BigDecimal totalPrice,List<PurchasePriceRuleRecord> ruleRecords){
        PurchaseGoodsListVo.ChangeDoc doc = new PurchaseGoodsListVo.ChangeDoc();
        if(totalPrice.compareTo(BigDecimal.ZERO) <= 0){
            doc.setState((byte)0);
            return doc;
        }
        ruleRecords = ruleRecords.stream().sorted(Comparator.comparing(PurchasePriceRuleRecord::getFullPrice)).collect(Collectors.toList());
        for(PurchasePriceRuleRecord rule:ruleRecords){
            if(totalPrice.compareTo(rule.getFullPrice()) >= 0){
                doc.setState((byte)2);
                return doc;
            }
        }
        BigDecimal diffPrice = ruleRecords.get(0).getFullPrice().subtract(totalPrice).setScale(2,BigDecimal.ROUND_HALF_UP);
        doc.setState((byte)1);
        doc.setDiffPrice(diffPrice);
        return doc;
    }

    /***
     * 当前已换购的商品
     * @param param
     * @param userId
     * @return
     */
    public PurchaseChangeGoodsVo changePurchaseProductList(PurchaseChangeGoodsParam param,Integer userId){
        PurchaseChangeGoodsVo vo = new PurchaseChangeGoodsVo();

        WxAppCartBo cartBo = cartService.getCartList(userId,null, BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS,param.getPurchasePriceId());
        WxAppCartBo cartBoMainGoods = cartService.getCartList(userId,null, BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE,param.getPurchasePriceId());
        BigDecimal cartTotalPrice = cartBoMainGoods.getCartGoodsList().stream().map(
            wxAppCartGoods -> wxAppCartGoods.getPrdPrice().multiply(BigDecimal.valueOf(wxAppCartGoods.getCartNumber()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Integer> cartPrdIds = new ArrayList<>();
        Integer cartTotalGoodsNumber = 0;
        if(CollectionUtils.isNotEmpty(cartBo.getCartGoodsList())){
            for(WxAppCartGoods g:cartBo.getCartGoodsList()){
                cartPrdIds.add(g.getProductId());
                cartTotalGoodsNumber += g.getCartNumber();
            }
        }

        PurchasePriceDefineRecord purchasePriceDefineRecord = db().fetchAny(ppd,ppd.ID.eq(param.getPurchasePriceId()));

        //用户不能买的专属商品
        List<Integer> userExclusiveGoodsIds = goodsCardCoupleService.getGoodsUserNotExclusive(userId);
        //换购规则
        List<PurchasePriceRuleRecord> ruleRecords = getRules(param.getPurchasePriceId());
        //输出商品列表
        List<PurchaseChangeGoodsVo.Goods> list = new ArrayList<>();
        for(PurchasePriceRuleRecord rule : ruleRecords){
            Map<Integer, GoodsSpecProductRecord> prds = goodsService.goodsSpecProductService.goodsSpecProductByIds(Util.splitValueToList(rule.getProductId()));
            for(GoodsSpecProductRecord productRecord : prds.values()){
                if(!userExclusiveGoodsIds.contains(productRecord.getGoodsId())){
                    PurchaseChangeGoodsVo.Goods goods = new PurchaseChangeGoodsVo.Goods();
                    GoodsRecord goodsView = goodsService.getGoodsRecordById(productRecord.getGoodsId());
                    goods.setGoodsId(goodsView.getGoodsId());
                    goods.setGoodsName(goodsView.getGoodsName());
                    if(StringUtil.isNotBlank(goodsView.getGoodsImg())){
                        goods.setGoodsImg(domainConfig.imageUrl(goodsView.getGoodsImg()));
                    }
                    goods.setShopPrice(goodsView.getShopPrice());
                    goods.setMarketPrice(goodsView.getMarketPrice());
                    goods.setIsDelete(goodsView.getDelFlag());
                    goods.setIsOnSale(goodsView.getIsOnSale());
                    goods.setPrdId(productRecord.getPrdId());
                    goods.setPrdDesc(productRecord.getPrdDesc());
                    if(StringUtil.isNotBlank(productRecord.getPrdImg())){
                        goods.setPrdImg(domainConfig.imageUrl(productRecord.getPrdImg()));
                    }
                    goods.setPrdNumber(productRecord.getPrdNumber());

                    goods.setPrdPrice(rule.getPurchasePrice());
                    goods.setPurchaseRuleId(rule.getId());
                    log.info("加价购调试-cartPrdIds-"+Util.toJson(cartPrdIds));
                    log.info("加价购调试-productRecord-"+Util.toJson(productRecord.getPrdId()));
                    log.info("加价购调试-cartBo-"+Util.toJson(cartBo));
                    if(cartPrdIds.contains(productRecord.getPrdId())){
                        goods.setIsChecked((byte)1);
                    }
                    if(rule.getFullPrice().compareTo(cartTotalPrice) > 0){
                        goods.setTip((byte)0);
                        goods.setTipMoney(rule.getFullPrice());
                    }else{
                        goods.setTip((byte)1);
                    }

                    list.add(goods);
                }
            }
        }
        vo.setList(list);
        vo.setMaxChangePurchase(purchasePriceDefineRecord.getMaxChangePurchase());
        vo.setAlreadyChangeNum(cartTotalGoodsNumber);
        return vo;
    }

    public Result<PurchasePriceRuleRecord> getRules(List<Integer> ruleIds) {
        return db().selectFrom(ppr).where(ppr.ID.in(ruleIds)).fetch();
    }

    /**
     * 获取加价购规则
     * @param ruleId
     * @return
     */
    public PurchasePriceRuleRecord getRuleByRuleId(Integer ruleId){
        return db().selectFrom(ppr).where(ppr.ID.eq(ruleId)).fetchAny();
    }

    /**
     * 换购商品运费策略
     * @param actId
     * @return
     */
    public boolean isFreeShip(Integer actId) {
        //换购商品运费策略，0免运费，1使用原商品运费模板
        Byte isFreeShip = db().select(ppd.REDEMPTION_FREIGHT).from(ppd).where(ppd.ID.eq(actId)).fetchOneInto(Byte.class);
        return NumberUtils.BYTE_ZERO.equals(isFreeShip);
    }

    /**
     * 给下单用户打标签
     * @param actId
     * @param userId
     */
    public void addActivityTag(Integer actId,Integer userId){
        PurchasePriceDefineRecord purchasePriceDefineRecord = db().fetchAny(ppd,ppd.ID.eq(actId));
        if(purchasePriceDefineRecord.getActivityTag().equals(FLAG_ONE) && StringUtil.isNotBlank(purchasePriceDefineRecord.getActivityTagId())){
            tagService.userTagSvc.addActivityTag(userId,Util.stringToList(purchasePriceDefineRecord.getActivityTagId()), TagSrcConstant.PURCHASE_PRICE,actId);
        }
    }
    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(ppd.ID, ppd.NAME.as(CalendarAction.ACTNAME), ppd.START_TIME,
				ppd.END_TIME).from(ppd).where(ppd.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(ppd.ID, ppd.NAME.as(CalendarAction.ACTNAME), ppd.START_TIME,
						ppd.END_TIME)
				.from(ppd)
				.where(ppd.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(ppd.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(ppd.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(ppd.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}

}
