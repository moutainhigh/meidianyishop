package com.meidianyi.shop.service.shop.market.firstspecial;

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
import com.meidianyi.shop.db.shop.tables.records.FirstSpecialGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.FirstSpecialProductRecord;
import com.meidianyi.shop.db.shop.tables.records.FirstSpecialRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.*;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.Record5;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectWhereStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.FirstSpecial.FIRST_SPECIAL;
import static com.meidianyi.shop.db.shop.tables.FirstSpecialGoods.FIRST_SPECIAL_GOODS;
import static com.meidianyi.shop.db.shop.tables.FirstSpecialProduct.FIRST_SPECIAL_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.sum;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 10:02
 **/
@Service
public class FirstSpecialService extends ShopBaseService {
    @Autowired
    private DomainConfig domainConfig;

    /**
     * 启用状态
     */
    public static final byte STATUS_NORMAL = 1;
    /**
     * 停用状态
     */
    public static final byte STATUS_DISABLED = 0;

    /**
     * 开启超购限制
     */
    public static final Byte LIMIT_FLAG_YES = 1;

    /**
     * 新建首单特惠活动
     *
     */
    public void addFirstSpecial(FirstSpecialAddParam param) {
        this.transaction(()->{
            FirstSpecialRecord record = db().newRecord(FIRST_SPECIAL);
            assign(param,record);
            if(param.getShareConfig() != null) {
                if (StringUtil.isNotEmpty(param.getShareConfig().getShareImg())) {
                    param.getShareConfig().setShareImg(RegexUtil.getUri(param.getShareConfig().getShareImg()));
                }
                record.setShareConfig(Util.toJson(param.getShareConfig()));
            }
            record.insert();
            Integer firstSpecialId = record.getId();
            for(FirstSpecialGoodsParam goods : param.getFirstSpecialGoodsParams()){
                FirstSpecialGoodsRecord goodsRecord = db().newRecord(FIRST_SPECIAL_GOODS);
                assign(goods,goodsRecord);
                goodsRecord.setFirstSpecialId(firstSpecialId);
                goodsRecord.insert();
                if (CollectionUtils.isNotEmpty(goods.getGoodsProductParams())) {
                    Integer goodsId = goodsRecord.getGoodsId();
                    for (FirstSpecialGoodsProductParam goodsProduct : goods.getGoodsProductParams()) {
                        FirstSpecialProductRecord productRecord = db().newRecord(FIRST_SPECIAL_PRODUCT);
                        assign(goodsProduct, productRecord);
                        productRecord.setFirstSpecialId(firstSpecialId);
                        productRecord.setGoodsId(goodsId);
                        productRecord.insert();
                    }
                }
            }
        });
    }

    /**
     * 首单特惠活动列表分页查询
     *
     */
    public PageResult<FirstSpecialPageListQueryVo> getPageList(FirstSpecialPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(FIRST_SPECIAL.ID,FIRST_SPECIAL.NAME,FIRST_SPECIAL.FIRST,FIRST_SPECIAL.IS_FOREVER,FIRST_SPECIAL.START_TIME,FIRST_SPECIAL.END_TIME,FIRST_SPECIAL.STATUS).
            from(FIRST_SPECIAL);
        if(param.getState() > 0) {
            /** 状态过滤*/
            Timestamp now = DateUtils.getLocalDateTime();
            switch(param.getState()) {
                case (byte)1:
                    select.where(FIRST_SPECIAL.STATUS.eq(STATUS_NORMAL)).and(FIRST_SPECIAL.IS_FOREVER.eq(BaseConstant.ACTIVITY_IS_FOREVER).or(FIRST_SPECIAL.START_TIME.lt(now).and(FIRST_SPECIAL.END_TIME.gt(now))));
                    break;
                case (byte)2:
                    select.where(FIRST_SPECIAL.STATUS.eq(STATUS_NORMAL)).and(FIRST_SPECIAL.START_TIME.gt(now));
                    break;
                case (byte)3:
                    select.where(FIRST_SPECIAL.STATUS.eq(STATUS_NORMAL)).and(FIRST_SPECIAL.END_TIME.lt(now));
                    break;
                case (byte)4:
                    select.where(FIRST_SPECIAL.STATUS.eq(STATUS_DISABLED));
                    break;
                default:
            }
        }
        select.where(FIRST_SPECIAL.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(FIRST_SPECIAL.FIRST.desc(),FIRST_SPECIAL.CREATE_TIME.desc());
        PageResult<FirstSpecialPageListQueryVo> res = getPageResult(select,param.getCurrentPage(),param.getPageRows(),FirstSpecialPageListQueryVo.class);

        /**查询活动商品数量、订单付款数、付款用户数、付款总金额 */
        for(FirstSpecialPageListQueryVo vo : res.dataList){
            vo.setGoodsAmount(getFirstSpecialActGoodsAmount(vo.getId()));
            vo.setOrderAmount(getFirstSpecialActOrderAmount(vo.getId()));
            vo.setUserAmount(getFirstSpecialActUserAmount(vo.getId()));
            vo.setPaymentTotalAmount(getFirstSpecialPaymentTotalAmount(vo.getId()));
            vo.setCurrentState(Util.getActStatus(vo.getStatus(),vo.getStartTime(),vo.getEndTime(),vo.getIsForever()));
        }

        return res;
    }

    /**
     * 取单个首单特惠活动信息
     *
     */
    public FirstSpecialVo getFirstSpecialById(Integer id){
        FirstSpecialRecord record = db().select(FIRST_SPECIAL.ID,FIRST_SPECIAL.NAME,FIRST_SPECIAL.FIRST,FIRST_SPECIAL.IS_FOREVER,FIRST_SPECIAL.START_TIME,FIRST_SPECIAL.END_TIME,
            FIRST_SPECIAL.LIMIT_AMOUNT,FIRST_SPECIAL.LIMIT_FLAG,FIRST_SPECIAL.SHARE_CONFIG).
            from(FIRST_SPECIAL).where(FIRST_SPECIAL.ID.eq(id)).fetchOneInto(FirstSpecialRecord.class);
        FirstSpecialVo res = record.into(FirstSpecialVo.class);
        res.setShopShareConfig(Util.parseJson(record.getShareConfig(), PictorialShareConfigVo.class));
        if(res.getShopShareConfig() != null && StringUtil.isNotEmpty(res.getShopShareConfig().getShareImg())){
            res.getShopShareConfig().setShareImgFullUrl(domainConfig.imageUrl(res.getShopShareConfig().getShareImg()));
            res.getShopShareConfig().setShareImg(domainConfig.imageUrl(res.getShopShareConfig().getShareImg()));
        }
        res.setFirstSpecialGoods(getFirstSpecialGoodsVoList(id));

        return res;
    }

    /**
     * 更新首单特惠活动
     *
     */
    public void updateFirstSpecial(FirstSpecialUpdateParam param) {
        FirstSpecialRecord record = db().newRecord(FIRST_SPECIAL);
        assign(param, record);
        if (param.getShareConfig() != null) {
            if (StringUtil.isNotEmpty(param.getShareConfig().getShareImg())) {
                param.getShareConfig().setShareImg(RegexUtil.getUri(param.getShareConfig().getShareImg()));
            }
            record.setShareConfig(Util.toJson(param.getShareConfig()));
        }
        if (CollectionUtils.isNotEmpty(param.getFirstSpecialGoodsParams())) {
            List<FirstSpecialGoodsParam> newActGoods = param.getFirstSpecialGoodsParams().stream().filter(g -> g.getId() == null).collect(Collectors.toList());
            param.getFirstSpecialGoodsParams().removeAll(newActGoods);
            this.transaction(() -> {
                db().executeUpdate(record);
                db().deleteFrom(FIRST_SPECIAL_GOODS).where(FIRST_SPECIAL_GOODS.FIRST_SPECIAL_ID.eq(param.getId()).and(FIRST_SPECIAL_GOODS.ID.notIn(param.getFirstSpecialGoodsParams().stream().map(FirstSpecialGoodsParam::getId).collect(Collectors.toList())))).execute();
                if (CollectionUtils.isNotEmpty(param.getFirstSpecialGoodsParams())) {
                    for (FirstSpecialGoodsParam goods : param.getFirstSpecialGoodsParams()) {
                        FirstSpecialGoodsRecord goodsRecord = db().newRecord(FIRST_SPECIAL_GOODS);
                        assign(goods, goodsRecord);
                        db().executeUpdate(goodsRecord);
                        if (CollectionUtils.isNotEmpty(goods.getGoodsProductParams())) {
                            for (FirstSpecialGoodsProductParam goodsProduct : goods.getGoodsProductParams()) {
                                FirstSpecialProductRecord productRecord = db().newRecord(FIRST_SPECIAL_PRODUCT);
                                assign(goodsProduct, productRecord);
                                db().executeUpdate(productRecord);
                            }
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(newActGoods)) {
                    for (FirstSpecialGoodsParam goods : newActGoods) {
                        FirstSpecialGoodsRecord goodsRecord = db().newRecord(FIRST_SPECIAL_GOODS);
                        assign(goods, goodsRecord);
                        goodsRecord.setFirstSpecialId(param.getId());
                        goodsRecord.insert();
                        if (CollectionUtils.isNotEmpty(goods.getGoodsProductParams())) {
                            Integer goodsId = goodsRecord.getGoodsId();
                            for (FirstSpecialGoodsProductParam goodsProduct : goods.getGoodsProductParams()) {
                                FirstSpecialProductRecord productRecord = db().newRecord(FIRST_SPECIAL_PRODUCT);
                                assign(goodsProduct, productRecord);
                                productRecord.setFirstSpecialId(param.getId());
                                productRecord.setGoodsId(goodsId);
                                productRecord.insert();
                            }
                        }
                    }
                }

            });
        } else {
            db().executeUpdate(record);
        }


    }

    /**
     * 删除首单特惠活动
     *
     */
    public void delFirstSpecial(Integer id) {
        db().update(FIRST_SPECIAL).
            set(FIRST_SPECIAL.DEL_FLAG,DelFlag.DISABLE.getCode()).
            set(FIRST_SPECIAL.DEL_TIME, DateUtils.getLocalDateTime()).
            where(FIRST_SPECIAL.ID.eq(id)).
            execute();
    }

    /**
     * 首单特惠订单
     *
     */
    public PageResult<MarketOrderListVo> getFirstSpecialOrderList(MarketOrderListParam param) {
        return saas().getShopApp(getShopId()).readOrder.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL);
    }

    /**
     *  获取正在活动的首单特惠商品规格
     * @return
     */
    public List<Integer> getOnGoingPrdIds(){
        Timestamp nowDate =new Timestamp(System.currentTimeMillis());
        return db().select(FIRST_SPECIAL_PRODUCT.PRD_ID).from(FIRST_SPECIAL_PRODUCT).leftJoin(FIRST_SPECIAL).on(FIRST_SPECIAL.ID.ge(FIRST_SPECIAL_PRODUCT.FIRST_SPECIAL_ID))
                .where(FIRST_SPECIAL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(FIRST_SPECIAL.STATUS.eq(STATUS_NORMAL))
                .and(FIRST_SPECIAL.IS_FOREVER.eq(BaseConstant.ACTIVITY_IS_FOREVER)
                        .or(FIRST_SPECIAL.IS_FOREVER.eq(BaseConstant.ACTIVITY_NOT_FOREVER).and(FIRST_SPECIAL.START_TIME.lt(nowDate)).and(FIRST_SPECIAL.END_TIME.gt(nowDate))))
                .groupBy(FIRST_SPECIAL_PRODUCT.PRD_ID).fetch().getValues(FIRST_SPECIAL_PRODUCT.PRD_ID);

    }

    private int getFirstSpecialActGoodsAmount(int id){
        return db().selectCount().from(FIRST_SPECIAL_GOODS).where(FIRST_SPECIAL_GOODS.FIRST_SPECIAL_ID.eq(id)).fetchOneInto(Integer.class);
    }

    private int getFirstSpecialActOrderAmount(int id){
        return db().select(countDistinct(ORDER_GOODS.ORDER_SN)).from(ORDER_GOODS).where(ORDER_GOODS.ACTIVITY_ID.eq(id)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL)).fetchOneInto(Integer.class);
    }

    private int getFirstSpecialActUserAmount(int id){
        return db().select(countDistinct(ORDER_INFO.USER_ID)).from(ORDER_GOODS).leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN)).where(ORDER_GOODS.ACTIVITY_ID.eq(id)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL)).fetchOneInto(Integer.class);
    }

    private BigDecimal getFirstSpecialPaymentTotalAmount(int id){
        BigDecimal res =  db().select(sum(ORDER_GOODS.DISCOUNTED_GOODS_PRICE)).from(ORDER_GOODS).leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN)).where(ORDER_GOODS.ACTIVITY_ID.eq(id)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL)).and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_WAIT_DELIVERY)).fetchOneInto(BigDecimal.class);
        return res == null ? BigDecimal.ZERO : res;
    }

    private List<FirstSpecialGoodsVo> getFirstSpecialGoodsVoList(int id){
        List<FirstSpecialGoodsVo> res = db().select(FIRST_SPECIAL_GOODS.ID,FIRST_SPECIAL_GOODS.GOODS_ID,FIRST_SPECIAL_GOODS.DISCOUNT,FIRST_SPECIAL_GOODS.REDUCE_PRICE,FIRST_SPECIAL_GOODS.GOODS_PRICE).from(FIRST_SPECIAL_GOODS).where(FIRST_SPECIAL_GOODS.FIRST_SPECIAL_ID.eq(id)).fetchInto(FirstSpecialGoodsVo.class);
        if(!res.isEmpty()){
            for(FirstSpecialGoodsVo firstSpecialGoods : res){
                firstSpecialGoods.setGoodsView(saas().getShopApp(getShopId()).goods.getGoodsSmallVo(firstSpecialGoods.getGoodsId()));
                List<FirstSpecialProductVo> firstSpecialProduct = db().select(FIRST_SPECIAL_PRODUCT.ID,FIRST_SPECIAL_PRODUCT.PRD_ID,FIRST_SPECIAL_PRODUCT.PRD_PRICE,GOODS_SPEC_PRODUCT.PRD_DESC,GOODS_SPEC_PRODUCT.PRD_PRICE.as("originalPrice")).from(FIRST_SPECIAL_PRODUCT).innerJoin(GOODS_SPEC_PRODUCT).on(FIRST_SPECIAL_PRODUCT.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID)).where(FIRST_SPECIAL_PRODUCT.FIRST_SPECIAL_ID.eq(id)).and(FIRST_SPECIAL_PRODUCT.GOODS_ID.eq(firstSpecialGoods.getGoodsId())).fetchInto(FirstSpecialProductVo.class);
                firstSpecialGoods.setFirstSpecialProduct(firstSpecialProduct);
            }
        }
        return res;
    }

    /**
     * 获取首单特惠活动record
     * @param activityId 活动id
     * @return record信息
     */
    public FirstSpecialRecord getFirstSpecialRecord(Integer activityId) {
        return db().selectFrom(FIRST_SPECIAL).where(FIRST_SPECIAL.ID.eq(activityId))
            .fetchAny();
    }

    /**
     * 该商品正在进行的首单特惠信息
     * @param goodsId
     * @return
     */
    public FirstSpecialRecord getActInfoByGoodsId(Integer goodsId) {
        Timestamp now = DateUtils.getLocalDateTime();
        Optional<Record> res = db().select(FIRST_SPECIAL_GOODS.fields()).
            from(FIRST_SPECIAL_GOODS.leftJoin(FIRST_SPECIAL).on(FIRST_SPECIAL_GOODS.FIRST_SPECIAL_ID.eq(FIRST_SPECIAL.ID))).
            where(FIRST_SPECIAL_GOODS.GOODS_ID.eq(goodsId)).
            and(FIRST_SPECIAL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).
            and(FIRST_SPECIAL.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).
            and(FIRST_SPECIAL.IS_FOREVER.eq(BaseConstant.ACTIVITY_IS_FOREVER).or(FIRST_SPECIAL.IS_FOREVER.eq(BaseConstant.ACTIVITY_NOT_FOREVER).and(FIRST_SPECIAL.START_TIME.lt(now)).and(FIRST_SPECIAL.END_TIME.gt(now)))).
            orderBy(FIRST_SPECIAL.FIRST.desc(), FIRST_SPECIAL.ID.desc()).fetchOptional();
        if (res.isPresent()) {
            return res.get().into(FirstSpecialRecord.class);
        }
        return null;
    }

    /**
     *获得某个商品下的首单特惠规格商品
     * @param firstSpecialId
     * @param goodsId
     * @return
     */
    public List<FirstSpecialProductRecord> getProductListById(Integer firstSpecialId, Integer goodsId){
        return db().selectFrom(FIRST_SPECIAL_PRODUCT).where(FIRST_SPECIAL_PRODUCT.FIRST_SPECIAL_ID.eq(firstSpecialId)).and(FIRST_SPECIAL_PRODUCT.GOODS_ID.eq(goodsId)).fetch();
    }

    /**
     * 订单导出
     * @param param
     * @param lang
     * @return
     */
    public Workbook exportFirstSpecialOrderList(MarketOrderListParam param, String lang){
        List<MarketOrderListVo> list = saas.getShopApp(getShopId()).readOrder.marketOrderInfo.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL);

        List<FirstSpecialOrderExportVo> res = new ArrayList<>();
        list.forEach(order->{
            FirstSpecialOrderExportVo vo = new FirstSpecialOrderExportVo();
            vo.setOrderSn(order.getOrderSn());
            vo.setGoods(order.getGoods().stream().map((g)->{
                FirstSpecialOrderGoodsExportVo goods = new FirstSpecialOrderGoodsExportVo();
                goods.setGoodsName(g.getGoodsName());
                goods.setPrice(g.getGoodsPrice());
                return goods;
            }).collect(Collectors.toList()));
            vo.setCreateTime(order.getCreateTime());
            vo.setOrderUser(order.getUsername() + ";" + (StringUtil.isNotBlank(order.getUserMobile()) ? order.getUserMobile() : ""));
            vo.setMoneyPaid(order.getMoneyPaid());
            vo.setConsignee(order.getConsignee() + ";" + order.getMobile());
            vo.setOrderStatus(OrderConstant.getOrderStatusName(order.getOrderStatus(),lang));

            res.add(vo);
        });

        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        ClassList cList = new ClassList();
        cList.setUpClazz(FirstSpecialOrderExportVo.class);
        cList.setInnerClazz(FirstSpecialOrderGoodsExportVo.class);
        try {
            excelWriter.writeModelListByRegion(res, cList);
        } catch (Exception e) {
            logger().error("excel error",e);
        }

        return workbook;
    }

    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(FIRST_SPECIAL.ID, FIRST_SPECIAL.NAME.as(CalendarAction.ACTNAME), FIRST_SPECIAL.START_TIME,
				FIRST_SPECIAL.END_TIME,FIRST_SPECIAL.IS_FOREVER.as(CalendarAction.ISPERMANENT)).from(FIRST_SPECIAL).where(FIRST_SPECIAL.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record5<Integer, String, Timestamp, Timestamp, Byte>, Integer> select = db()
				.select(FIRST_SPECIAL.ID, FIRST_SPECIAL.NAME.as(CalendarAction.ACTNAME), FIRST_SPECIAL.START_TIME,
						FIRST_SPECIAL.END_TIME,FIRST_SPECIAL.IS_FOREVER.as(CalendarAction.ISPERMANENT))
				.from(FIRST_SPECIAL)
				.where(FIRST_SPECIAL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(FIRST_SPECIAL.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(FIRST_SPECIAL.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(FIRST_SPECIAL.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}
}
