package com.meidianyi.shop.service.shop.market.reduceprice;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.excel.bean.ClassList;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPriceBo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsProductVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialOrderExportVo;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialOrderGoodsExportVo;
import com.meidianyi.shop.service.pojo.shop.market.reduceprice.*;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisDataVo;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisTotalVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagSrcConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveOrderList;
import com.meidianyi.shop.service.pojo.shop.order.analysis.OrderActivityUserNum;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.shop.activity.dao.MemberCardProcessorDao;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.market.seckill.SeckillService;
import com.meidianyi.shop.service.shop.member.TagService;
import jodd.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.ReducePrice.REDUCE_PRICE;
import static com.meidianyi.shop.db.shop.tables.ReducePriceGoods.REDUCE_PRICE_GOODS;
import static com.meidianyi.shop.db.shop.tables.ReducePriceProduct.REDUCE_PRICE_PRODUCT;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.sum;

/**
 * @author: 王兵兵
 * @create: 2019-08-14 10:02
 **/
@Service
public class ReducePriceService extends ShopBaseService {

    public static final Byte PERIOD_ACTION_NORMAL = 0;
    public static final Byte PERIOD_ACTION_EVERY_DAY = 1;
    public static final Byte PERIOD_ACTION_EVERY_MONTH = 2;
    public static final Byte PERIOD_ACTION_EVERY_WEEK = 3;


    @Autowired
    GoodsService goodsService;
    @Autowired
    DomainConfig domainConfig;
    @Autowired
    private MemberCardProcessorDao memberCardProcessorDao;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;

    @Autowired
    private TagService tagService;
    /**
     * 新建限时降价活动
     */
    public void addReducePrice(ReducePriceAddParam param) {
        ReducePriceRecord record = db().newRecord(REDUCE_PRICE);
        assign(param, record);
        if (param.getShareConfig() != null) {
            if (StringUtil.isNotEmpty(param.getShareConfig().getShareImg())) {
                param.getShareConfig().setShareImg(RegexUtil.getUri(param.getShareConfig().getShareImg()));
            }
            record.setShareConfig(Util.toJson(param.getShareConfig()));
        }
        if(CollectionUtils.isNotEmpty(param.getActivityTagId())){
            record.setActivityTagId(Util.listToString(param.getActivityTagId()));
        }
        if (PERIOD_ACTION_NORMAL.equals(param.getPeriodAction())) {
            //不按周期重复
            record.setPointTime(null);
        }
        List<Integer> goodsIds = new ArrayList<>();
        this.transaction(() -> {
            record.insert();
            Integer reducePriceId = record.getId();
            for (ReducePriceGoodsAddParam goods : param.getReducePriceGoodsAddParams()) {
                ReducePriceGoodsRecord goodsRecord = db().newRecord(REDUCE_PRICE_GOODS);
                assign(goods, goodsRecord);
                goodsRecord.setReducePriceId(reducePriceId);
                goodsRecord.insert();
                Integer goodsId = goodsRecord.getGoodsId();
                if (CollectionUtils.isNotEmpty(goods.getReducePriceProduct())) {
                    for (ReducePriceGoodsProductAddParam goodsProduct : goods.getReducePriceProduct()) {
                        ReducePriceProductRecord productRecord = db().newRecord(REDUCE_PRICE_PRODUCT);
                        assign(goodsProduct, productRecord);
                        productRecord.setReducePriceId(reducePriceId);
                        productRecord.setGoodsId(goodsId);
                        productRecord.insert();
                    }
                } else {
                    List<GoodsProductVo> allProductListByGoodsId = goodsService.getAllProductListByGoodsId(goodsId);
                    GoodsProductVo defaultPrd = allProductListByGoodsId.get(0);
                    ReducePriceProductRecord productRecord = db().newRecord(REDUCE_PRICE_PRODUCT);
                    productRecord.setReducePriceId(reducePriceId);
                    productRecord.setGoodsId(goodsId);
                    productRecord.setPrdId(defaultPrd.getPrdId());
                    productRecord.setPrdPrice(goods.getGoodsPrice());
                    productRecord.insert();
                }

                goodsIds.add(goods.getGoodsId());
            }
        });

        //刷新goodsType
        saas.getShopApp(getShopId()).shopTaskService.reducePriceTaskService.monitorGoodsType();
        esDataUpdateMqService.addEsGoodsIndex(goodsIds, getShopId(), DBOperating.UPDATE);
    }

    /**
     * 限时降价活动列表分页查询
     */
    public PageResult<ReducePricePageListQueryVo> getPageList(ReducePricePageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(REDUCE_PRICE.ID, REDUCE_PRICE.FIRST, REDUCE_PRICE.NAME, REDUCE_PRICE.START_TIME, REDUCE_PRICE.END_TIME, REDUCE_PRICE.STATUS).
            from(REDUCE_PRICE);
        if (param.getState() > 0) {
            /** 状态过滤*/
            Timestamp now = DateUtils.getLocalDateTime();
            switch (param.getState()) {
                case (byte) 1:
                    select.where(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(REDUCE_PRICE.START_TIME.lt(now)).and(REDUCE_PRICE.END_TIME.gt(now));
                    break;
                case (byte) 2:
                    select.where(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(REDUCE_PRICE.START_TIME.gt(now));
                    break;
                case (byte) 3:
                    select.where(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(REDUCE_PRICE.END_TIME.lt(now));
                    break;
                case (byte) 4:
                    select.where(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                    break;
                default:
            }
        }
        select.where(REDUCE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(REDUCE_PRICE.FIRST.desc(),REDUCE_PRICE.CREATE_TIME.desc());
        PageResult<ReducePricePageListQueryVo> res = getPageResult(select, param.getCurrentPage(), param.getPageRows(), ReducePricePageListQueryVo.class);

        /**查询活动商品数量、订单付款数、付款用户数、付款总金额 */
        for (ReducePricePageListQueryVo vo : res.dataList) {
            vo.setGoodsAmount(getReducePriceActGoodsAmount(vo.getId()));
            vo.setOrderAmount(getReducePriceActOrderAmount(vo.getId()));
            vo.setUserAmount(getReducePriceActUserAmount(vo.getId()));
            vo.setPaymentTotalAmount(getReducePricePaymentTotalAmount(vo.getId()));
            vo.setCurrentState(Util.getActStatus(vo.getStatus(), vo.getStartTime(), vo.getEndTime()));
        }

        return res;
    }

    /**
     * 取单个限时降价活动信息
     */
    public ReducePriceVo getReducePriceById(Integer id) {
        ReducePriceRecord record = getReducePriceRecord(id);
        ReducePriceVo res = record.into(ReducePriceVo.class);
        res.setShopShareConfig(Util.parseJson(record.getShareConfig(), PictorialShareConfigVo.class));
        if (StringUtil.isNotBlank(res.getShareConfig())) {
            res.setShopShareConfig(Util.parseJson(res.getShareConfig(), PictorialShareConfigVo.class));
            if (res.getShopShareConfig() != null && StringUtil.isNotEmpty(res.getShopShareConfig().getShareImg())) {
                res.getShopShareConfig().setShareImgFullUrl(domainConfig.imageUrl(res.getShopShareConfig().getShareImg()));
                res.getShopShareConfig().setShareImg(domainConfig.imageUrl(res.getShopShareConfig().getShareImg()));
            }
        }
        res.setReducePriceGoods(getReducePriceGoodsVoList(id));
        if(res.getActivityTag().equals(BaseConstant.YES) && StringUtil.isNotBlank(record.getActivityTagId())){
            res.setTagList(tagService.getTagsById(Util.splitValueToList(record.getActivityTagId())));
        }
        return res;
    }

    /**
     * 更新限时降价活动
     */
    public void updateReducePrice(ReducePriceUpdateParam param) {
        ReducePriceRecord record = db().newRecord(REDUCE_PRICE);
        assign(param, record);
        if (param.getShareConfig() != null) {
            if (StringUtil.isNotEmpty(param.getShareConfig().getShareImg())) {
                param.getShareConfig().setShareImg(RegexUtil.getUri(param.getShareConfig().getShareImg()));
            }
            record.setShareConfig(Util.toJson(param.getShareConfig()));
        }
        if (CollectionUtils.isNotEmpty(param.getActivityTagId())) {
            record.setActivityTagId(Util.listToString(param.getActivityTagId()));
        }
        if (PERIOD_ACTION_NORMAL.equals(param.getPeriodAction())) {
            //不按周期重复
            record.setPointTime(null);
        }
        if (CollectionUtils.isNotEmpty(param.getReducePriceGoodsAddParams())) {
            List<ReducePriceGoodsAddParam> newActGoods = param.getReducePriceGoodsAddParams().stream().filter(g -> g.getId() == null).collect(Collectors.toList());
            param.getReducePriceGoodsAddParams().removeAll(newActGoods);
            Set<Integer> goodsIds = new HashSet<>();
            goodsIds.addAll(getActGoodsIds(param.getId()));
            this.transaction(() -> {
                db().executeUpdate(record);
                db().deleteFrom(REDUCE_PRICE_GOODS).where(REDUCE_PRICE_GOODS.REDUCE_PRICE_ID.eq(param.getId()).and(REDUCE_PRICE_GOODS.ID.notIn(param.getReducePriceGoodsAddParams().stream().map(ReducePriceGoodsAddParam::getId).collect(Collectors.toList())))).execute();
                if (CollectionUtils.isNotEmpty((param.getReducePriceGoodsAddParams()))) {
                    //更新
                    for (ReducePriceGoodsAddParam goods : param.getReducePriceGoodsAddParams()) {
                        ReducePriceGoodsRecord goodsRecord = db().fetchAny(REDUCE_PRICE_GOODS, REDUCE_PRICE_GOODS.ID.eq(goods.getId()));
                        assign(goods, goodsRecord);
                        goodsRecord.update();
                        if (CollectionUtils.isNotEmpty(goods.getReducePriceProduct())) {
                            for (ReducePriceGoodsProductAddParam prd : goods.getReducePriceProduct()) {
                                ReducePriceProductRecord productRecord = db().fetchAny(REDUCE_PRICE_PRODUCT, REDUCE_PRICE_PRODUCT.ID.eq(prd.getId()));
                                assign(productRecord, productRecord);
                                productRecord.update();
                            }
                        }
                        goodsIds.add(goods.getGoodsId());
                    }
                }
                if (CollectionUtils.isNotEmpty(newActGoods)) {
                    for (ReducePriceGoodsAddParam goods : newActGoods) {
                        ReducePriceGoodsRecord goodsRecord = db().newRecord(REDUCE_PRICE_GOODS);
                        assign(goods, goodsRecord);
                        goodsRecord.setReducePriceId(param.getId());
                        goodsRecord.insert();
                        Integer goodsId = goodsRecord.getGoodsId();
                        if (CollectionUtils.isNotEmpty(goods.getReducePriceProduct())) {
                            for (ReducePriceGoodsProductAddParam goodsProduct : goods.getReducePriceProduct()) {
                                ReducePriceProductRecord productRecord = db().newRecord(REDUCE_PRICE_PRODUCT);
                                assign(goodsProduct, productRecord);
                                productRecord.setReducePriceId(param.getId());
                                productRecord.setGoodsId(goodsId);
                                productRecord.insert();
                            }
                        } else {
                            List<GoodsProductVo> allProductListByGoodsId = goodsService.getAllProductListByGoodsId(goodsId);
                            GoodsProductVo defaultPrd = allProductListByGoodsId.get(0);
                            ReducePriceProductRecord productRecord = db().newRecord(REDUCE_PRICE_PRODUCT);
                            productRecord.setReducePriceId(param.getId());
                            productRecord.setGoodsId(goodsId);
                            productRecord.setPrdId(defaultPrd.getPrdId());
                            productRecord.setPrdPrice(goods.getGoodsPrice());
                            productRecord.insert();
                        }

                        goodsIds.add(goods.getGoodsId());
                    }
                }
            });
            //刷新goodsType
            saas.getShopApp(getShopId()).shopTaskService.reducePriceTaskService.monitorGoodsType();
            esDataUpdateMqService.addEsGoodsIndex(new ArrayList<>(goodsIds), getShopId(), DBOperating.UPDATE);
        } else {
            db().executeUpdate(record);
            //刷新goodsType
            saas.getShopApp(getShopId()).shopTaskService.reducePriceTaskService.monitorGoodsType();
            esDataUpdateMqService.addEsGoodsIndex(getActGoodsIds(param.getId()), getShopId(), DBOperating.UPDATE);
        }


    }

    /**
     * 删除限时降价活动
     */
    public void delReducePrice(Integer id) {
        db().update(REDUCE_PRICE).
            set(REDUCE_PRICE.DEL_FLAG, DelFlag.DISABLE.getCode()).
            set(REDUCE_PRICE.DEL_TIME, DateUtils.getLocalDateTime()).
            where(REDUCE_PRICE.ID.eq(id)).
            execute();

        //刷新goodsType
        saas.getShopApp(getShopId()).shopTaskService.reducePriceTaskService.monitorGoodsType();
        esDataUpdateMqService.addEsGoodsIndex(getActGoodsIds(id), getShopId(), DBOperating.UPDATE);
    }

    /**
     * 限时降价订单
     */
    public PageResult<MarketOrderListVo> getReducePriceOrderList(MarketOrderListParam param) {
        return saas().getShopApp(getShopId()).readOrder.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE);
    }

    private int getReducePriceActGoodsAmount(int id) {
        return db().selectCount().from(REDUCE_PRICE_GOODS).where(REDUCE_PRICE_GOODS.REDUCE_PRICE_ID.eq(id)).fetchOneInto(Integer.class);
    }

    private int getReducePriceActOrderAmount(int id) {
        return db().select(countDistinct(ORDER_GOODS.ORDER_SN)).from(ORDER_GOODS).where(ORDER_GOODS.ACTIVITY_ID.eq(id)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)).fetchOneInto(Integer.class);
    }

    private int getReducePriceActUserAmount(int id) {
        return db().select(countDistinct(ORDER_INFO.USER_ID)).from(ORDER_GOODS).leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN)).where(ORDER_GOODS.ACTIVITY_ID.eq(id)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)).fetchOneInto(Integer.class);
    }

    private BigDecimal getReducePricePaymentTotalAmount(int id) {
        BigDecimal res = db().select(sum(ORDER_GOODS.DISCOUNTED_GOODS_PRICE)).from(ORDER_GOODS).leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN)).where(ORDER_GOODS.ACTIVITY_ID.eq(id)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)).and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_WAIT_DELIVERY)).fetchOneInto(BigDecimal.class);
        return res == null ? BigDecimal.ZERO : res;
    }

    private List<ReducePriceGoodsVo> getReducePriceGoodsVoList(int id) {
        List<ReducePriceGoodsVo> res = db().select(REDUCE_PRICE_GOODS.ID, REDUCE_PRICE_GOODS.GOODS_ID, REDUCE_PRICE_GOODS.DISCOUNT, REDUCE_PRICE_GOODS.REDUCE_PRICE, REDUCE_PRICE_GOODS.GOODS_PRICE).from(REDUCE_PRICE_GOODS).where(REDUCE_PRICE_GOODS.REDUCE_PRICE_ID.eq(id)).fetchInto(ReducePriceGoodsVo.class);
        if (!res.isEmpty()) {
            for (ReducePriceGoodsVo reducePriceGoods : res) {
                reducePriceGoods.setGoodsView(saas().getShopApp(getShopId()).goods.getGoodsView(reducePriceGoods.getGoodsId()));
                List<ReducePriceProductVo> reducePriceProduct = db().select(REDUCE_PRICE_PRODUCT.ID, REDUCE_PRICE_PRODUCT.PRD_ID.as("productId"), REDUCE_PRICE_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_DESC, GOODS_SPEC_PRODUCT.PRD_PRICE.as("originalPrice")).from(REDUCE_PRICE_PRODUCT).innerJoin(GOODS_SPEC_PRODUCT).on(REDUCE_PRICE_PRODUCT.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID)).where(REDUCE_PRICE_PRODUCT.REDUCE_PRICE_ID.eq(id)).and(REDUCE_PRICE_PRODUCT.GOODS_ID.eq(reducePriceGoods.getGoodsId())).fetchInto(ReducePriceProductVo.class);
                reducePriceGoods.setReducePriceProduct(reducePriceProduct);
            }
        }
        return res;
    }

    public Map<Integer, BigDecimal> getShowPriceByGoodsIds(List<Integer> goodsIds, Timestamp date) {
        Map<Integer, Result<Record5<Integer, Byte, String, String, Integer>>> recordMap = db()
            .select(
                REDUCE_PRICE.ID,
                REDUCE_PRICE.PERIOD_ACTION,
                REDUCE_PRICE.EXTEND_TIME,
                REDUCE_PRICE.POINT_TIME,
                REDUCE_PRICE_GOODS.GOODS_ID)
            .from(REDUCE_PRICE_GOODS)
            .leftJoin(REDUCE_PRICE).on(REDUCE_PRICE.ID.eq(REDUCE_PRICE_GOODS.REDUCE_PRICE_ID))
            .where(REDUCE_PRICE_GOODS.GOODS_ID.in(goodsIds))
            .and(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(REDUCE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(REDUCE_PRICE.START_TIME.lessThan(date))
            .and(REDUCE_PRICE.END_TIME.greaterThan(date))
            .orderBy(REDUCE_PRICE.FIRST.desc(),REDUCE_PRICE.CREATE_TIME.desc())
            .fetch()
            .intoGroups(REDUCE_PRICE_GOODS.GOODS_ID);
        Map<Integer, Integer> goodsIdAndReducePriceIdMap = recordMap.entrySet()
            .stream()
            .filter(x -> isActivityGoingOn(x.getValue().get(0)))
            .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().get(0).get(REDUCE_PRICE.ID)));
        Condition condition = DSL.noCondition();
        for(Map.Entry<Integer,Integer> entry:goodsIdAndReducePriceIdMap.entrySet()){
            condition = condition.or(REDUCE_PRICE_PRODUCT.GOODS_ID.eq(entry.getKey()).and(REDUCE_PRICE_PRODUCT.REDUCE_PRICE_ID.eq(entry.getValue())));
        }

        Map<Integer, BigDecimal> resultPriceMap = new HashMap<>(goodsIdAndReducePriceIdMap.size());
        Map<Integer, Result<Record2<Integer, BigDecimal>>> beatPrice = db()
            .select(REDUCE_PRICE_PRODUCT.GOODS_ID, REDUCE_PRICE_PRODUCT.PRD_PRICE)
            .from(REDUCE_PRICE_PRODUCT)
            .where(condition)
            .fetchGroups(REDUCE_PRICE_PRODUCT.GOODS_ID);
        beatPrice.forEach((k, v) -> {
            if (v.size() > 1) {
                resultPriceMap.put(k, v.sortAsc(REDUCE_PRICE_PRODUCT.PRD_PRICE).get(0).get(REDUCE_PRICE_PRODUCT.PRD_PRICE));
            } else {
                resultPriceMap.put(k, v.get(0).get(REDUCE_PRICE_PRODUCT.PRD_PRICE));
            }
        });
        return resultPriceMap;

    }

    /**
     * 根据商品ID和当前时间获取第一个正在进行的限时降价
     * @param goodsId
     * @param date
     * @return
     */
    public ReducePriceRecord getOnGoingReducePrice(Integer goodsId, Timestamp date){
        Integer reducePriceId = db().select(REDUCE_PRICE.ID)
            .from(REDUCE_PRICE_GOODS)
            .leftJoin(REDUCE_PRICE).on(REDUCE_PRICE.ID.eq(REDUCE_PRICE_GOODS.REDUCE_PRICE_ID))
            .where(REDUCE_PRICE_GOODS.GOODS_ID.eq(goodsId))
            .and(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(REDUCE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(REDUCE_PRICE.START_TIME.lessThan(date))
            .and(REDUCE_PRICE.END_TIME.greaterThan(date))
            .orderBy(REDUCE_PRICE.FIRST.desc(), REDUCE_PRICE.CREATE_TIME.desc())
            .fetchAny(REDUCE_PRICE.ID);
        if (reducePriceId == null) {
            return null;
        }
        Optional<ReducePriceRecord> reducePriceRecordOptional =
            db().selectFrom(REDUCE_PRICE).where(REDUCE_PRICE.ID.eq(reducePriceId)).fetchOptional();
        if (!reducePriceRecordOptional.isPresent() || !isActivityGoingOn(reducePriceRecordOptional.get())) {
            return null;
        }else{
            return reducePriceRecordOptional.get();
        }
    }

    /**
     * 当前的规格的限时降价价格
     * @param prdIds
     * @return
     */
    public Map<Integer,Record10<Integer, Integer, BigDecimal, String, String, Byte, Byte, Timestamp, Integer, Byte>> getProductReducePrice(List<Integer> prdIds){
        Timestamp date = DateUtils.getLocalDateTime();
        Result<Record10<Integer, Integer, BigDecimal, String, String, Byte, Byte, Timestamp, Integer, Byte>> list =
            db().select(REDUCE_PRICE_PRODUCT.PRD_ID,REDUCE_PRICE_PRODUCT.REDUCE_PRICE_ID,REDUCE_PRICE_PRODUCT.PRD_PRICE,
                REDUCE_PRICE.POINT_TIME,REDUCE_PRICE.EXTEND_TIME,REDUCE_PRICE.PERIOD_ACTION,REDUCE_PRICE.FIRST,REDUCE_PRICE.CREATE_TIME,REDUCE_PRICE.LIMIT_AMOUNT,REDUCE_PRICE.LIMIT_FLAG)
                .from(REDUCE_PRICE_PRODUCT)
                .leftJoin(REDUCE_PRICE).on(REDUCE_PRICE.ID.eq(REDUCE_PRICE_PRODUCT.REDUCE_PRICE_ID))
                .where(REDUCE_PRICE_PRODUCT.PRD_ID.in(prdIds))
                .and(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(REDUCE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(REDUCE_PRICE.START_TIME.lessThan(date))
                .and(REDUCE_PRICE.END_TIME.greaterThan(date))
                .fetch();
        if (list == null) {
            return null;
        }

        Map<Integer,Record10<Integer, Integer, BigDecimal, String, String, Byte, Byte, Timestamp, Integer, Byte>> retMap = new HashMap<>(prdIds.size());
        list.forEach(r->{
            if(isActivityGoingOn(r.get(REDUCE_PRICE.POINT_TIME),r.get(REDUCE_PRICE.EXTEND_TIME),r.get(REDUCE_PRICE.PERIOD_ACTION))){
                Record10<Integer, Integer, BigDecimal, String, String, Byte, Byte, Timestamp, Integer, Byte> oldRecord = retMap.get(r.get(REDUCE_PRICE_PRODUCT.PRD_ID));
                if(oldRecord == null){
                    retMap.put(r.get(REDUCE_PRICE_PRODUCT.PRD_ID),r);
                }else{
                    boolean isReducePrdOk = oldRecord.get(REDUCE_PRICE.FIRST) < r.get(REDUCE_PRICE.FIRST) || (oldRecord.get(REDUCE_PRICE.FIRST).equals(r.get(REDUCE_PRICE.FIRST)) && r.get(REDUCE_PRICE.CREATE_TIME).after(oldRecord.get(REDUCE_PRICE.CREATE_TIME)));
                    if(isReducePrdOk){
                        retMap.put(r.get(REDUCE_PRICE_PRODUCT.PRD_ID),r);
                    }
                }
            }


        });
        return retMap;
    }


    /**
     * 计算当前正在进行的活动的预计结束时间
     *
     * @param endLocalTime 周期性活动指定的每次活动结束时刻
     * @param periodAction 活动的时间类型 null 表示活动仅指定了开始结束日期，未指定周期日
     * @param endDate      活动设定的失效时间
     * @return 结束的时间
     */
    public Timestamp getCurrentActivityEndDate(LocalTime endLocalTime, Byte periodAction, Timestamp endDate) {
        LocalDate nowDate = LocalDate.now();
        Timestamp currentActivityEndDate = null;
        if (PERIOD_ACTION_NORMAL.equals(periodAction)) {
            currentActivityEndDate = endDate;
        } else {
            currentActivityEndDate = DateUtils.convertToTimestamp(nowDate, endLocalTime);
        }
        return currentActivityEndDate;
    }

    /**
     * 获取活动下一次的开始时间
     * ps 调用此方法的活动都处于有效（当前时间早于活动结束时间）未进行阶段
     *
     * @param extendTime     指定的日
     * @param startLocalTime 活动的在指定日的开始时刻
     * @param periodAction   活动的时间类型
     * @param startDate      活动设定的开始时间
     * @param endDate        活动设定的失效时间
     * @return 下次活动的开始时间
     */
    public Timestamp getNextActivityStartDate(List<Integer> extendTime, LocalTime startLocalTime, LocalTime endLocalTime, Byte periodAction, Timestamp startDate, Timestamp endDate) {
        Timestamp nextStartTimestamp = null;

        Timestamp nowTimestamp = DateUtils.getLocalDateTime();
        LocalDate comparedDate = LocalDate.now();
        LocalTime comparedTime = LocalTime.now();

        // 活动未开始
        if (nowTimestamp.compareTo(startDate) < 0) {
            comparedDate = startDate.toLocalDateTime().toLocalDate();
            comparedTime = startDate.toLocalDateTime().toLocalTime();
        }

        // 按照每天指定时间段内开始活动
        if (PERIOD_ACTION_EVERY_DAY.equals(periodAction)) {

            // 当前时刻如果小于活动开始时刻则表明今天的活动尚未开启，否则活动已经结束需等待第二天开始
            int offsetDay = comparedTime.isBefore(startLocalTime) ? 0 : 1;
            comparedDate = comparedDate.plusDays(offsetDay);
            nextStartTimestamp = DateUtils.convertToTimestamp(comparedDate, startLocalTime);

        } else if (PERIOD_ACTION_EVERY_MONTH.equals(periodAction)) {

            //当前日大于目标日或日相同但是当前时刻大于等于目标时刻则本月活动已结束
            boolean isCurDayExpired = comparedDate.getDayOfMonth() > extendTime.get(0) || (comparedDate.getDayOfMonth() == extendTime.get(0) && comparedTime.compareTo(endLocalTime) >= 0);
            if (isCurDayExpired) {
                comparedDate = comparedDate.plusMonths(1);
            }
            LocalDate nextDate = LocalDate.of(comparedDate.getYear(), comparedDate.getMonth(), extendTime.get(0));
            nextStartTimestamp = DateUtils.convertToTimestamp(nextDate, startLocalTime);

        } else if (PERIOD_ACTION_EVERY_WEEK.equals(periodAction)) {
            // 当前时间是一周的第几天
            int nowDayOfWeek = comparedDate.getDayOfWeek().getValue();

            // 判断是否是今天有活动但是还没到要执行的时间
            if (extendTime.contains(nowDayOfWeek) && comparedTime.isBefore(startLocalTime)) {
                nextStartTimestamp = DateUtils.convertToTimestamp(comparedDate, startLocalTime);
            } else {
                // 非当天的最近一次活动是一周内的第几天
                Optional<Integer> optional = extendTime.stream().filter(x -> x > nowDayOfWeek).findFirst();
                int nextActivityDayOfWeek = optional.orElseGet(() -> extendTime.get(0));
                // (nextActivityDayOfWeek -nowDayOfWeek+7)%7 计算下次活动时间到今天的相差的天数
                int offsetDays = (nextActivityDayOfWeek + 7 - nowDayOfWeek) % 7;

                comparedDate = comparedDate.plusDays(offsetDays);
                nextStartTimestamp = DateUtils.convertToTimestamp(comparedDate, startLocalTime);
            }
        } else {
            // 仅指定了开始日期和结束日期的时候nextStartDate即为startDate
            nextStartTimestamp = startDate;
        }
        // 已经超过最后截止日期
        if (nextStartTimestamp.compareTo(endDate) > 0) {
            nextStartTimestamp = null;
        }
        return nextStartTimestamp;
    }


    /**
     * 判断当前活动是否还有效
     *
     * @param reducePriceRecord 限时减价活动
     * @return 有效：true
     */
    private boolean isActivityGoingOn(ReducePriceRecord reducePriceRecord) {
        return isActivityGoingOn(reducePriceRecord.getPointTime(), reducePriceRecord.getExtendTime(), reducePriceRecord.getPeriodAction());
    }

    private boolean isActivityGoingOn(Record5<Integer, Byte, String, String, Integer> reducePriceRecord) {
        return isActivityGoingOn(reducePriceRecord.get(REDUCE_PRICE.POINT_TIME)
            , reducePriceRecord.get(REDUCE_PRICE.EXTEND_TIME)
            , reducePriceRecord.get(REDUCE_PRICE.PERIOD_ACTION));
    }

    /**
     * 判断处于开始和结束时间之间的活动是否处于正在进行中
     *
     * @param pointTimeStr 指定的开始结束时间点
     * @param extendTime   指定的日期
     * @param periodAction 周期类型
     * @return true正在进行中，false 未进行中
     */
    public boolean isActivityGoingOn(String pointTimeStr, String extendTime, Byte periodAction) {
        LocalTime startLocalTime;
        LocalTime endLocalTime;
        LocalTime nowLocalTime = LocalTime.now();
        List<Integer> dayArray = new ArrayList<>(7);
        if (StringUtils.isNotBlank(pointTimeStr)) {
            String[] pointTime = pointTimeStr.split("@");
            String[] startArray = pointTime[0].split(":");
            String[] endArray = pointTime[1].split(":");
            startLocalTime = LocalTime.of(Integer.parseInt(startArray[0]), Integer.parseInt(startArray[1]));
            endLocalTime = LocalTime.of(Integer.parseInt(endArray[0]), Integer.parseInt(endArray[1]));
            if (nowLocalTime.isAfter(endLocalTime) || nowLocalTime.isBefore(startLocalTime)) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(extendTime)) {
            dayArray = Arrays.stream(extendTime.split("@"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        }
        if (PERIOD_ACTION_EVERY_DAY.equals(periodAction) || PERIOD_ACTION_NORMAL.equals(periodAction)) {
            return true;
        } else if (PERIOD_ACTION_EVERY_WEEK.equals(periodAction)) {
            Integer dayOfWeek = DateUtils.getLocalDate().getDayOfWeek().getValue();
            return dayArray.contains(dayOfWeek);
        } else if (PERIOD_ACTION_EVERY_MONTH.equals(periodAction)) {
            Integer dayOfMonth = DateUtils.getLocalDate().getDayOfMonth();
            return dayArray.contains(dayOfMonth);
        }
        return false;
    }

    /**
     * 获取限时降价的商品
     * @param reducePriceId
     * @param goodsId
     * @return
     */
    private List<ReducePriceProductRecord> getReducePriceProductRecordByGoodsId(Integer reducePriceId, Integer goodsId) {
        return db().selectFrom(REDUCE_PRICE_PRODUCT)
            .where(REDUCE_PRICE_PRODUCT.REDUCE_PRICE_ID.eq(reducePriceId))
            .and(REDUCE_PRICE_PRODUCT.GOODS_ID.eq(goodsId))
            .orderBy(REDUCE_PRICE_PRODUCT.PRD_PRICE.asc())
            .fetch();
    }

    private Optional<ReducePriceProductRecord> getReducePriceProductRecordByGoodsIds(List<Integer> reducePriceId, Integer goodsId) {
        return db().selectFrom(REDUCE_PRICE_PRODUCT)
            .where(REDUCE_PRICE_PRODUCT.REDUCE_PRICE_ID.in(reducePriceId))
            .and(REDUCE_PRICE_PRODUCT.GOODS_ID.eq(goodsId))
            .fetchOptional();
    }

    private <T> Collector<T, ?, List<T>> toSortedList(Comparator<? super T> c) {
        return Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new), l -> {
                l.sort(c);
                return l;
            });
    }

    /**
     * 获取限时降价record信息
     * @param activityId 活动id
     * @return record信息 或 null
     */
    public ReducePriceRecord getReducePriceRecord(Integer activityId){
        return db().selectFrom(REDUCE_PRICE).where(REDUCE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(REDUCE_PRICE.ID.eq(activityId)))
            .fetchAny();
    }

    /**
     * 获取限时降价record信息
     * @param activityId 活动id
     * @return record信息 或 null
     */
    public ReducePriceRecord getReducePriceRecordCanDel(Integer activityId){
        return db().selectFrom(REDUCE_PRICE).where(REDUCE_PRICE.ID.eq(activityId))
            .fetchAny();
    }

    /**
     * 考虑限时降价、首单特惠、等级会员价三种情况下，得出的商品价格
     * 首单特惠最高优先级，限时降价与等级会员之间价取低价
     * @param goodsId
     * @param userId
     * @return
     */
    public GoodsPriceBo parseGoodsPrice(Integer goodsId,Integer userId){
        GoodsPriceBo res = new GoodsPriceBo();

        //TODO 分销改价

        //处理首单特惠
        if(saas.getShopApp(getShopId()).readOrder.orderInfo.isNewUser(userId)){
            FirstSpecialRecord firstSpecialRecord = saas.getShopApp(getShopId()).firstSpecial.getActInfoByGoodsId(goodsId);
            if(null != firstSpecialRecord){
                List<FirstSpecialProductRecord> firstSpecialProductRecordList = saas.getShopApp(getShopId()).firstSpecial.getProductListById(firstSpecialRecord.getId(),goodsId);
                List<BigDecimal> prdPriceList = firstSpecialProductRecordList.stream().map(FirstSpecialProductRecord::getPrdPrice).sorted().collect(Collectors.toList());

                res.setGoodsPrice(prdPriceList.get(0));
                res.setMaxPrice(prdPriceList.get(prdPriceList.size() - 1));
                res.setLimitAmount(firstSpecialRecord.getLimitAmount());
                res.setGoodsPriceAction((byte) 3);
                return res;
            }
        }

        GoodsRecord goodsInfo = goodsService.getGoodsRecordById(goodsId);
        res.setMarketPrice(goodsInfo.getMarketPrice());
        res.setIsCardExclusive(goodsInfo.getIsCardExclusive());

        //处理限时降价
        //当前生效的活动
        ReducePriceRecord reducePriceRecord = getOnGoingReducePrice(goodsId, DateUtils.getLocalDateTime());
        if (reducePriceRecord != null) {
            List<ReducePriceProductRecord> reducePriceProductRecords = getReducePriceProductRecordByGoodsId(reducePriceRecord.getId(), goodsId);
            if (CollectionUtils.isNotEmpty(reducePriceProductRecords)) {
                List<BigDecimal> prdPriceList = reducePriceProductRecords.stream().map(ReducePriceProductRecord::getPrdPrice).sorted().collect(Collectors.toList());

                res.setGoodsPrice(prdPriceList.get(0));
                res.setMaxPrice(prdPriceList.get(prdPriceList.size() - 1));
                res.setLimitAmount(reducePriceRecord.getLimitAmount());
                res.setGoodsPriceAction((byte) 2);
            }
        }



        //处理会员等级
        String userCardGrade = saas.getShopApp(getShopId()).userCard.userCardDao.getUserCardGrade(userId);
        if(StringUtil.isNotEmpty(userCardGrade)){
            List<GradePrdRecord> gradePrdRecords = memberCardProcessorDao.getGoodsGradePrdListByGrade(goodsId,userCardGrade);
            if(CollectionUtils.isNotEmpty(gradePrdRecords)){
                List<BigDecimal> prdPriceList = gradePrdRecords.stream().map(GradePrdRecord::getGradePrice).sorted().collect(Collectors.toList());

                //有生效的限时降价价格，需要比较价格，取低的一个
                if (res.getGoodsPrice() != null && res.getGoodsPrice().compareTo(BigDecimal.ZERO) > 0) {
                    if (prdPriceList.get(0).compareTo(res.getGoodsPrice()) < 0) {
                        res.setGoodsPrice(prdPriceList.get(0));
                        res.setMaxPrice(prdPriceList.get(prdPriceList.size() - 1));
                        res.setGoodsPriceAction((byte) 1);
                    }
                } else {
                    res.setGoodsPrice(prdPriceList.get(0));
                    res.setMaxPrice(prdPriceList.get(prdPriceList.size() - 1));
                    res.setGoodsPriceAction((byte) 1);
                }
            }
        }

        if(res.getGoodsPriceAction() != null && res.getGoodsPriceAction() > 0){
            return res;
        }else{
            //没有可参与的活动，取商品原价
            List<GoodsSpecProductRecord> goodsSpecProductRecords = goodsService.goodsSpecProductService.getGoodsDetailPrds(goodsId);
            List<BigDecimal> prdPriceList = goodsSpecProductRecords.stream().map(GoodsSpecProductRecord::getPrdPrice).sorted().collect(Collectors.toList());

            res.setGoodsPrice(prdPriceList.get(0));
            res.setMaxPrice(prdPriceList.get(prdPriceList.size() - 1));
            res.setGoodsPriceAction((byte)0);

            return res;
        }
    }

    /**
     * 当前时间（今天）的活动时间段
     * @param reducePriceId
     * @return ret[0]开始时间 ret[1]结束时间
     */
    public Timestamp[] getCurrentPeriodTime(int reducePriceId){
        ReducePriceRecord reducePriceRecord = getReducePriceRecord(reducePriceId);
        Byte periodAction = reducePriceRecord.getPeriodAction();
        if (!periodAction.equals(PERIOD_ACTION_NORMAL)) {
            Timestamp[] ret = new Timestamp[2];
            String[] periodString = reducePriceRecord.getPointTime().split("@");
            ret[0] = DateUtils.convertToTimestamp(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE) + " " + periodString[0] + ":00");
            ret[1] = DateUtils.convertToTimestamp(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE) + " " + periodString[1] + ":00");
            return ret;
        }
        return null;
    }


    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(REDUCE_PRICE.ID, REDUCE_PRICE.NAME.as(CalendarAction.ACTNAME), REDUCE_PRICE.START_TIME,
				REDUCE_PRICE.END_TIME).from(REDUCE_PRICE).where(REDUCE_PRICE.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(REDUCE_PRICE.ID, REDUCE_PRICE.NAME.as(CalendarAction.ACTNAME), REDUCE_PRICE.START_TIME,
						REDUCE_PRICE.END_TIME)
				.from(REDUCE_PRICE)
				.where(REDUCE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(REDUCE_PRICE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)
								.and(REDUCE_PRICE.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(REDUCE_PRICE.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}

    /**
     * 给下单用户打标签
     * @param actId
     * @param userId
     */
    public void addActivityTag(Integer actId,Integer userId){
        ReducePriceRecord reducePriceRecord = getReducePriceRecord(actId);
        if(reducePriceRecord.getActivityTag().equals(BaseConstant.YES) && StringUtil.isNotBlank(reducePriceRecord.getActivityTagId())){
            tagService.userTagSvc.addActivityTag(userId,Util.stringToList(reducePriceRecord.getActivityTagId()), TagSrcConstant.REDUCE_PRICE,actId);
        }
    }

    private List<Integer> getActGoodsIds(Integer actId) {
        return db().select(REDUCE_PRICE_GOODS.GOODS_ID).from(REDUCE_PRICE_GOODS).where(REDUCE_PRICE_GOODS.REDUCE_PRICE_ID.eq(actId)).fetchInto(Integer.class);
    }

    public Workbook exportReducePriceOrderList(MarketOrderListParam param, String lang) {
        List<MarketOrderListVo> list = saas.getShopApp(getShopId()).readOrder.marketOrderInfo.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE);

        List<FirstSpecialOrderExportVo> res = new ArrayList<>();
        list.forEach(order -> {
            FirstSpecialOrderExportVo vo = new FirstSpecialOrderExportVo();
            vo.setOrderSn(order.getOrderSn());
            vo.setGoods(order.getGoods().stream().map((g) -> {
                FirstSpecialOrderGoodsExportVo goods = new FirstSpecialOrderGoodsExportVo();
                goods.setGoodsName(g.getGoodsName());
                goods.setPrice(g.getGoodsPrice());
                return goods;
            }).collect(Collectors.toList()));
            vo.setCreateTime(order.getCreateTime());
            vo.setOrderUser(order.getUsername() + ";" + (StringUtil.isNotBlank(order.getUserMobile()) ? order.getUserMobile() : ""));
            vo.setMoneyPaid(order.getMoneyPaid());
            vo.setConsignee(order.getConsignee() + ";" + order.getMobile());
            vo.setOrderStatus(OrderConstant.getOrderStatusName(order.getOrderStatus(), lang));

            res.add(vo);
        });

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        ClassList cList = new ClassList();
        cList.setUpClazz(FirstSpecialOrderExportVo.class);
        cList.setInnerClazz(FirstSpecialOrderGoodsExportVo.class);
        try {
            excelWriter.writeModelListByRegion(res, cList);
        } catch (Exception e) {
            logger().error("excel error", e);
        }

        return workbook;
    }

    /**
     * 限时降价效果分析的echarts图表数据
     */
    public SeckillAnalysisDataVo getReduceAnalysisData(ReducePriceAnalysisParam param) {
        SeckillAnalysisDataVo analysisVo = new SeckillAnalysisDataVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = DateUtils.currentMonthFirstDay();
            endDate = DateUtils.getLocalDateTime();
        }
        //获取销售额等金额
        List<ActiveDiscountMoney> discountMoneyList = saas.getShopApp(getShopId()).readOrder.getActiveDiscountMoney(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE, param.getId(), startDate, endDate);
        //获取参与用户信息
        ActiveOrderList activeOrderUserList = saas.getShopApp(getShopId()).readOrder.getActiveOrderList(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE, param.getId(), startDate, endDate);

        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            //活动实付金额、付款订单数、付款商品件数
            ActiveDiscountMoney discountMoney = SeckillService.getDiscountMoneyByDate(discountMoneyList, startDate);
            if (discountMoney == null) {
                analysisVo.getPaymentAmount().add(BigDecimal.ZERO);
                analysisVo.getDiscountAmount().add(BigDecimal.ZERO);
                analysisVo.getCostEffectivenessRatio().add(BigDecimal.ZERO);
                analysisVo.getPaidOrderNumber().add(0);
                analysisVo.getPaidGoodsNumber().add(0);
            } else {
                BigDecimal goodsPrice = Optional.ofNullable(discountMoney.getPaymentAmount()).orElse(BigDecimal.ZERO);
                BigDecimal marketPric = Optional.ofNullable(discountMoney.getDiscountAmount()).orElse(BigDecimal.ZERO);
                analysisVo.getPaymentAmount().add(Optional.ofNullable(discountMoney.getPaymentAmount()).orElse(BigDecimal.ZERO));
                analysisVo.getDiscountAmount().add(Optional.ofNullable(discountMoney.getDiscountAmount()).orElse(BigDecimal.ZERO));
                analysisVo.getCostEffectivenessRatio().add(goodsPrice.compareTo(BigDecimal.ZERO) > 0 ?
                    marketPric.divide(goodsPrice, BigDecimal.ROUND_FLOOR) : BigDecimal.ZERO);
                analysisVo.getPaidOrderNumber().add(discountMoney.getPaidOrderNumber());
                analysisVo.getPaidGoodsNumber().add(discountMoney.getPaidGoodsNumber());
            }

            //新用户数
            OrderActivityUserNum newUser = SeckillService.getUserNum(activeOrderUserList.getNewUserNum(), startDate);
            if (newUser == null) {
                analysisVo.getNewUserNumber().add(0);
            } else {
                analysisVo.getNewUserNumber().add(newUser.getNum());
            }
            //老用户数
            OrderActivityUserNum oldUser = SeckillService.getUserNum(activeOrderUserList.getOldUserNum(), startDate);
            if (oldUser == null) {
                analysisVo.getOldUserNumber().add(0);
            } else {
                analysisVo.getOldUserNumber().add(oldUser.getNum());
            }
            analysisVo.getDateList().add(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE, startDate));
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }
        SeckillAnalysisTotalVo total = new SeckillAnalysisTotalVo();
        total.setTotalPayment(analysisVo.getPaymentAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        total.setTotalDiscount(analysisVo.getDiscountAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        total.setTotalCostEffectivenessRatio(total.getTotalPayment().compareTo(BigDecimal.ZERO) > 0 ? total.getTotalDiscount().divide(total.getTotalPayment(), 3, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        total.setTotalPaidOrderNumber(analysisVo.getPaidOrderNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalPaidGoodsNumber(analysisVo.getPaidGoodsNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalOldUserNumber(analysisVo.getOldUserNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalNewUserNumber(analysisVo.getNewUserNumber().stream().mapToInt(Integer::intValue).sum());
        analysisVo.setTotal(total);
        return analysisVo;
    }

}
