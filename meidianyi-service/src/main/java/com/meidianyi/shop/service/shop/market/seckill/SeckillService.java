package com.meidianyi.shop.service.shop.market.seckill;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.SecKillDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.SecKillProductDefineRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleSeckill;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.seckill.*;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisDataVo;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisTotalVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagSrcConstant;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveOrderList;
import com.meidianyi.shop.service.pojo.shop.order.analysis.OrderActivityUserNum;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.market.seckill.SecKillProductParam;
import com.meidianyi.shop.service.pojo.wxapp.market.seckill.SeckillCheckVo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.TagService;
import jodd.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.SecKillDefine.SEC_KILL_DEFINE;
import static com.meidianyi.shop.db.shop.tables.SecKillList.SEC_KILL_LIST;
import static com.meidianyi.shop.db.shop.tables.SecKillProductDefine.SEC_KILL_PRODUCT_DEFINE;

/**
 * @author 王兵兵
 *
 * 2019年8月5日
 */
@Service
public class SeckillService extends ShopBaseService{

    @Autowired
    public SeckillListService seckillList;
    @Autowired
    private QrCodeService qrCode;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    protected DomainConfig domainConfig;
    @Autowired
    private TagService tagService;

    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;

    /**
     * 秒杀活动列表分页数据
     *
     */
    public PageResult<SeckillPageListQueryVo> getPageList(SeckillPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(SEC_KILL_DEFINE.SK_ID,SEC_KILL_DEFINE.NAME,SEC_KILL_DEFINE.START_TIME,SEC_KILL_DEFINE.END_TIME,SEC_KILL_DEFINE.FIRST,SEC_KILL_DEFINE.GOODS_ID,
            SEC_KILL_DEFINE.STATUS,SEC_KILL_DEFINE.SALE_NUM,SEC_KILL_DEFINE.LIMIT_AMOUNT,SEC_KILL_DEFINE.STOCK,SEC_KILL_DEFINE.BASE_SALE).
            from(SEC_KILL_DEFINE);
        select = buildOptions(select,param);
        select.orderBy(SEC_KILL_DEFINE.FIRST.desc(),SEC_KILL_DEFINE.CREATE_TIME.desc());
        PageResult<SeckillPageListQueryVo> res = getPageResult(select,param.getCurrentPage(),param.getPageRows(),SeckillPageListQueryVo.class);
        for(SeckillPageListQueryVo vo : res.dataList){
            vo.setCurrentState(Util.getActStatus(vo.getStatus(),vo.getStartTime(),vo.getEndTime()));
            vo.setGoods(vo.getGoodsId().split(",").length);
        }
        return res;
    }

    /**
     * 秒杀活动列表分页查询(装修页弹窗选择)
     *
     */
    public PageResult<SeckillDecoratePageListVo> getPageListDialog(SeckillPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(SEC_KILL_PRODUCT_DEFINE.SK_ID, SEC_KILL_PRODUCT_DEFINE.GOODS_ID, DSL.min(SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE).as(SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE), DSL.sum(SEC_KILL_PRODUCT_DEFINE.STOCK).as(SEC_KILL_PRODUCT_DEFINE.STOCK), DSL.sum(SEC_KILL_PRODUCT_DEFINE.TOTAL_STOCK).as(SEC_KILL_PRODUCT_DEFINE.TOTAL_STOCK)).
            from(SEC_KILL_PRODUCT_DEFINE).leftJoin(SEC_KILL_DEFINE).on(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(SEC_KILL_DEFINE.SK_ID)).leftJoin(GOODS).on(SEC_KILL_PRODUCT_DEFINE.GOODS_ID.eq(GOODS.GOODS_ID));
        select = buildOptions(select,param);
        select.where(SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
        select.groupBy(SEC_KILL_PRODUCT_DEFINE.SK_ID,SEC_KILL_PRODUCT_DEFINE.GOODS_ID);

        PageResult<SeckillGoodsPriceBo> boList = getPageResult(select,param.getCurrentPage(),param.getPageRows(),SeckillGoodsPriceBo.class);

        PageResult<SeckillDecoratePageListVo> res = new PageResult<>();
        res.setPage(boList.getPage());
        List<SeckillDecoratePageListVo> dataList = new ArrayList<>();

        for(SeckillGoodsPriceBo bo : boList.dataList){
            SeckillDecoratePageListVo vo = new SeckillDecoratePageListVo();
            vo.setSecPrice(bo.getSecKillPrice());

            GoodsRecord goods = goodsService.getGoodsRecordById(bo.getGoodsId());
            vo.setGoodsId(bo.getGoodsId());
            vo.setGoodsImg(domainConfig.imageUrl(goods.getGoodsImg()));
            vo.setIsOnSale(goods.getIsOnSale());
            vo.setGoodsName(goods.getGoodsName());
            vo.setShopPrice(goods.getShopPrice());
            vo.setGoodsNumber(goods.getGoodsNumber());

            SecKillDefineRecord seckill = getSeckillActById(bo.getSkId());
            vo.setName(seckill.getName());
            vo.setStartTime(seckill.getStartTime());
            vo.setEndTime(seckill.getEndTime());
            vo.setSkId(bo.getSkId());
            vo.setStock(bo.getStock());
            vo.setBaseSale(seckill.getBaseSale());
            vo.setSaleNum(seckill.getSaleNum());
            vo.setStatus(seckill.getStatus());
            vo.setLimitAmount(seckill.getLimitAmount());
            vo.setTotalStock(bo.getTotalStock());

            dataList.add(vo);
        }

        res.setDataList(dataList);
        return res;
    }

    private int getTotalStock(int skId){
        return db().select(DSL.sum(SEC_KILL_PRODUCT_DEFINE.TOTAL_STOCK)).from(SEC_KILL_PRODUCT_DEFINE).where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(skId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    private SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends  Record> select,SeckillPageListQueryParam param){
        select.where(SEC_KILL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        if(param.getState() != null && param.getState().length > 0) {
            /** 状态过滤*/
            Condition stateCondition = DSL.noCondition();
            Timestamp now = DateUtils.getLocalDateTime();
            for(byte state : param.getState()){
                if(state == 1){
                    stateCondition = stateCondition.or((SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(SEC_KILL_DEFINE.START_TIME.lt(now)).and(SEC_KILL_DEFINE.END_TIME.gt(now)));
                }
                if(state == 2){
                    stateCondition = stateCondition.or((SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(SEC_KILL_DEFINE.START_TIME.gt(now)));
                }
                if(state == 3){
                    stateCondition = stateCondition.or((SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(SEC_KILL_DEFINE.END_TIME.lt(now)));
                }
                if(state == 4){
                    stateCondition = stateCondition.or(SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                }
            }
            select.where(stateCondition);
        }
        if(!StringUtils.isEmpty(param.getKeywords())){
            select.where(SEC_KILL_DEFINE.NAME.contains(param.getKeywords()).or(GOODS.GOODS_NAME.contains(param.getKeywords())));
        }

        return select;
    }

    /**
     * 取单个秒杀活动的最小的一个规格秒杀价
     *
     */
    private BigDecimal getMinProductSecPrice(int skId){
        return db().select(DSL.min(SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE)).from(SEC_KILL_PRODUCT_DEFINE).where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(skId)).fetchOneInto(BigDecimal.class);
    }

    /**
     * 新建秒杀活动
     *
     */
    public void addSeckill(SeckillAddParam param) {
        this.transaction(()->{
            SecKillDefineRecord record = db().newRecord(SEC_KILL_DEFINE);
            assign(param,record);
            if(param.getShareConfig() != null) {
                if (StringUtil.isNotEmpty(param.getShareConfig().getShareImg())) {
                    param.getShareConfig().setShareImg(RegexUtil.getUri(param.getShareConfig().getShareImg()));
                }
                record.setShareConfig(Util.toJson(param.getShareConfig()));
            }
            if(CollectionUtils.isNotEmpty(param.getActivityTagId())){
                record.setActivityTagId(Util.listToString(param.getActivityTagId()));
            }
            record.insert();
            Integer skId = record.getSkId();
            for(SeckillProductAddParam secKillProduct : param.getSecKillProduct()){
                SecKillProductDefineRecord productRecord = new SecKillProductDefineRecord();
                assign(secKillProduct,productRecord);
                productRecord.setSkId(skId);
                productRecord.setTotalStock(productRecord.getStock());
                db().executeInsert(productRecord);
            }
        });
        if(param.getStartTime().before(DateUtils.getLocalDateTime()) && param.getEndTime().after(DateUtils.getLocalDateTime())){
            //活动已生效
            saas.getShopApp(getShopId()).shopTaskService.seckillTaskService.monitorGoodsType();
        }

        esDataUpdateMqService.addEsGoodsIndex(Util.splitValueToList(param.getGoodsId()), getShopId(), DBOperating.UPDATE);

        /** 操作记录 */
        saas().getShopApp(getShopId()).record.insertRecord(Arrays.asList(new Integer[] { RecordContentTemplate.MARKET_SECKILL_ADD.code }), new String[] {param.getName()});
    }

    /**
     * 更新秒杀活动
     *
     */
    public void updateSeckill(SeckillUpdateParam param) {
        SecKillDefineRecord record = getSeckillActById(param.getSkId());
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
        List<Integer> oldGoodsIds = Util.splitValueToList(record.getGoodsId());

        if (CollectionUtils.isNotEmpty(param.getSecKillProduct())) {
            record.setGoodsId(Util.listToString(param.getSecKillProduct().stream().map(SeckillProductAddParam::getGoodsId).collect(Collectors.toList())));
            List<SeckillProductAddParam> newActGoods = param.getSecKillProduct().stream().filter(g -> g.getSkproId() == null).collect(Collectors.toList());
            param.getSecKillProduct().removeAll(newActGoods);
            Set<Integer> goodsIds = new HashSet<>();
            goodsIds.addAll(oldGoodsIds);
            transaction(() -> {

                db().deleteFrom(SEC_KILL_PRODUCT_DEFINE).where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(param.getSkId()).and(SEC_KILL_PRODUCT_DEFINE.SKPRO_ID.notIn(param.getSecKillProduct().stream().map(SeckillProductAddParam::getSkproId).collect(Collectors.toList())))).execute();

                int totalStock = 0;
                if (CollectionUtils.isNotEmpty(param.getSecKillProduct())) {


                    for (SeckillProductAddParam secKillProduct : param.getSecKillProduct()) {
                        if (secKillProduct.getProductId() != null && secKillProduct.getSecKillPrice() != null && secKillProduct.getStock() != null) {
                            SecKillProductDefineRecord secKillProductDefineRecord = new SecKillProductDefineRecord();
                            assign(secKillProduct, secKillProductDefineRecord);
                            db().executeUpdate(secKillProductDefineRecord);
                            totalStock += secKillProduct.getStock();
                            goodsIds.add(secKillProduct.getGoodsId());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(newActGoods)) {
                    for (SeckillProductAddParam secKillProduct : newActGoods) {
                        SecKillProductDefineRecord secKillProductDefineRecord = db().newRecord(SEC_KILL_PRODUCT_DEFINE);
                        assign(secKillProduct, secKillProductDefineRecord);
                        secKillProductDefineRecord.setSkId(param.getSkId());
                        secKillProductDefineRecord.setTotalStock(secKillProduct.getStock());
                        secKillProductDefineRecord.insert();
                        goodsIds.add(secKillProduct.getGoodsId());
                    }
                }
                if (totalStock > 0) {
                    record.setStock(totalStock);
                }
                db().executeUpdate(record);
            });
            //刷新goodsType
            saas.getShopApp(getShopId()).shopTaskService.seckillTaskService.monitorGoodsType();
            esDataUpdateMqService.addEsGoodsIndex(new ArrayList<>(goodsIds), getShopId(), DBOperating.UPDATE);
        } else {
            db().executeUpdate(record);
            //刷新goodsType
            saas.getShopApp(getShopId()).shopTaskService.seckillTaskService.monitorGoodsType();
            esDataUpdateMqService.addEsGoodsIndex(oldGoodsIds, getShopId(), DBOperating.UPDATE);
        }
    }

    /**
     * 删除秒杀活动
     *
     */
    public void delSeckill(Integer skId) {
        db().update(SEC_KILL_DEFINE).
            set(SEC_KILL_DEFINE.DEL_FLAG,DelFlag.DISABLE.getCode()).
            set(SEC_KILL_DEFINE.DEL_TIME, DateUtils.getLocalDateTime()).
            where(SEC_KILL_DEFINE.SK_ID.eq(skId)).
            execute();
        SecKillDefineRecord record = getSeckillActById(skId);
        esDataUpdateMqService.addEsGoodsIndex(Util.splitValueToList(record.getGoodsId()), getShopId(), DBOperating.UPDATE);
        //刷新goodsType
        saas.getShopApp(getShopId()).shopTaskService.seckillTaskService.monitorGoodsType();
    }

    /**
     * 取单个秒杀活动信息
     *
     */
    public SeckillVo getSeckillById(Integer skId){
        SecKillDefineRecord record = getSeckillActById(skId);
        SeckillVo res = record.into(SeckillVo.class);

        res.setGoods(this.getSecKillGoods(skId));
        res.setMemberCard(saas().getShopApp(getShopId()).member.card.getMemberCardByCardIds(Util.splitValueToList(record.getCardId())));
        if(StringUtil.isNotBlank(res.getShareConfig())){
            res.setShopShareConfig(Util.parseJson(res.getShareConfig(), PictorialShareConfigVo.class));
            if(res.getShopShareConfig() != null && StringUtil.isNotEmpty(res.getShopShareConfig().getShareImg())){
                res.getShopShareConfig().setShareImgFullUrl(domainConfig.imageUrl(res.getShopShareConfig().getShareImg()));
                res.getShopShareConfig().setShareImg(domainConfig.imageUrl(res.getShopShareConfig().getShareImg()));
            }
        }
        if(res.getActivityTag().equals(BaseConstant.YES) && StringUtil.isNotBlank(record.getActivityTagId())){
            res.setTagList(tagService.getTagsById(Util.splitValueToList(record.getActivityTagId())));
        }

        return res;
    }

    public List<SeckillVo.SeckillGoods> getSecKillGoods(Integer skId){
        List<SecKillProductVo> prdList = db().select(SEC_KILL_PRODUCT_DEFINE.SKPRO_ID, SEC_KILL_PRODUCT_DEFINE.PRODUCT_ID, SEC_KILL_PRODUCT_DEFINE.GOODS_ID, GOODS_SPEC_PRODUCT.PRD_DESC, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_NUMBER, SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE, SEC_KILL_PRODUCT_DEFINE.TOTAL_STOCK, SEC_KILL_PRODUCT_DEFINE.STOCK, SEC_KILL_PRODUCT_DEFINE.SALE_NUM).
            from(SEC_KILL_PRODUCT_DEFINE).innerJoin(GOODS_SPEC_PRODUCT).on(SEC_KILL_PRODUCT_DEFINE.PRODUCT_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID)).
            where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(skId)).fetch().into(SecKillProductVo.class);
        Map<Integer, List<SecKillProductVo>> goodsMap = prdList.stream().collect(Collectors.groupingBy(SecKillProductVo::getGoodsId));
        List<SeckillVo.SeckillGoods> res = new ArrayList<>();
        goodsMap.forEach((k,v)->{
            SeckillVo.SeckillGoods seckillGoods = new SeckillVo.SeckillGoods();
            GoodsView goodsView = goodsService.getGoodsView(k);
            seckillGoods.setGoodsId(k);
            seckillGoods.setGoodsImg(goodsView.getGoodsImg());
            seckillGoods.setGoodsName(goodsView.getGoodsName());
            seckillGoods.setGoodsNumber(goodsView.getGoodsNumber());
            seckillGoods.setShopPrice(goodsView.getShopPrice());
            seckillGoods.setUnit(goodsView.getUnit());
            seckillGoods.setSaleNum(v.stream().mapToInt(SecKillProductVo::getSaleNum).sum());
            seckillGoods.setSecKillProduct(v);
            res.add(seckillGoods);
        });
        return res;
    }

    /**
     * 获取商品集合内的秒杀价格
     * @param goodsIds 商品id集合
     * @param date 限制时间
     * @return key:商品id，value:活动价格
     */
    public Map<Integer, BigDecimal> getSecKillProductVo(List<Integer> goodsIds, Timestamp date){
        return db().select(SEC_KILL_PRODUCT_DEFINE.GOODS_ID, SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE)
            .from(SEC_KILL_PRODUCT_DEFINE)
            .innerJoin(SEC_KILL_DEFINE).on(SEC_KILL_DEFINE.SK_ID.eq(SEC_KILL_PRODUCT_DEFINE.SK_ID))
            .where(SEC_KILL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(SEC_KILL_DEFINE.END_TIME.gt(date))
            .and(SEC_KILL_DEFINE.START_TIME.le(date))
            .and(SEC_KILL_PRODUCT_DEFINE.GOODS_ID.in(goodsIds))
            .orderBy(SEC_KILL_DEFINE.FIRST.desc(),SEC_KILL_DEFINE.CREATE_TIME.desc(),SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE.asc())
            .fetch()
            .stream()
            .collect(
                Collectors
                    .toMap(x->x.get(SEC_KILL_PRODUCT_DEFINE.GOODS_ID),
                        y->y.get( SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE),(olValue,newValue)->olValue)
            );
    }

    /**
     * 活动新增用户
     * @param param
     */
    public PageResult<MemberInfoVo> getSeckillSourceUserList(MarketSourceUserListParam param) {
        MemberPageListParam pageListParam = new MemberPageListParam();
        pageListParam.setCurrentPage(param.getCurrentPage());
        pageListParam.setPageRows(param.getPageRows());
        pageListParam.setMobile(param.getMobile());
        pageListParam.setUsername(param.getUserName());
        pageListParam.setInviteUserName(param.getInviteUserName());

        return saas().getShopApp(getShopId()).member.getSourceActList(pageListParam, MemberService.INVITE_SOURCE_SECKILL, param.getActivityId());
    }

    /**
     * 秒杀订单
     *
     */
	public PageResult<MarketOrderListVo> getSeckillOrderList(MarketOrderListParam param) {
        return saas().getShopApp(getShopId()).readOrder.getMarketOrderList(param,BaseConstant.ACTIVITY_TYPE_SEC_KILL);
    }
    /**
     * 获取小程序码
     */
    public ShareQrCodeVo getMpQrCode(Integer skId) {
        String pathParam = "pageFrom=5&actId=" + skId;
        String imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.GOODS_SEARCH, pathParam);

        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(QrCodeTypeEnum.GOODS_SEARCH.getPathUrl(pathParam));
        return vo;
    }

    /**
     * 秒杀效果分析的echarts图表数据
     *
     *
     */
    public SeckillAnalysisDataVo getSeckillAnalysisData(SeckillAnalysisParam param){
        SeckillAnalysisDataVo analysisVo = new SeckillAnalysisDataVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = DateUtils.currentMonthFirstDay();
            endDate = DateUtils.getLocalDateTime();
        }
        //获取销售额等金额
        List<ActiveDiscountMoney> discountMoneyList = saas.getShopApp(getShopId()).readOrder.getActiveDiscountMoney(BaseConstant.ACTIVITY_TYPE_SEC_KILL, param.getSkId(), startDate, endDate);
        //获取参与用户信息
        ActiveOrderList activeOrderUserList = saas.getShopApp(getShopId()).readOrder.getActiveOrderList(BaseConstant.ACTIVITY_TYPE_SEC_KILL, param.getSkId(), startDate, endDate);

        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            //活动实付金额、付款订单数、付款商品件数
            ActiveDiscountMoney discountMoney = getDiscountMoneyByDate(discountMoneyList, startDate);
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
            OrderActivityUserNum newUser = getUserNum(activeOrderUserList.getNewUserNum(), startDate);
            if (newUser == null) {
                analysisVo.getNewUserNumber().add(0);
            } else {
                analysisVo.getNewUserNumber().add(newUser.getNum());
            }
            //老用户数
            OrderActivityUserNum oldUser = getUserNum(activeOrderUserList.getOldUserNum(), startDate);
            if (oldUser == null) {
                analysisVo.getOldUserNumber().add(0);
            } else {
                analysisVo.getOldUserNumber().add(oldUser.getNum());
            }
            analysisVo.getDateList().add(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE, startDate));
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }
        SeckillAnalysisTotalVo total = new SeckillAnalysisTotalVo();
        total.setTotalPayment(analysisVo.getPaymentAmount().stream().reduce(BigDecimal.ZERO,BigDecimal::add));
        total.setTotalDiscount(analysisVo.getDiscountAmount().stream().reduce(BigDecimal.ZERO,BigDecimal::add));
        total.setTotalCostEffectivenessRatio(total.getTotalPayment().compareTo(BigDecimal.ZERO) > 0 ? total.getTotalDiscount().divide(total.getTotalPayment(),3, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        total.setTotalPaidOrderNumber(analysisVo.getPaidOrderNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalPaidGoodsNumber(analysisVo.getPaidGoodsNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalOldUserNumber(analysisVo.getOldUserNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalNewUserNumber(analysisVo.getNewUserNumber().stream().mapToInt(Integer::intValue).sum());
        analysisVo.setTotal(total);
        return analysisVo;
    }

    public static ActiveDiscountMoney getDiscountMoneyByDate(List<ActiveDiscountMoney> discountMoneyList, Timestamp timestamp) {
        for (ActiveDiscountMoney data : discountMoneyList) {
            if (data.getCreateTime().equals(timestamp)) {
                return data;
            }
        }
        return null;
    }

    /**
     * 用户数
     * @param list
     * @param timestamp
     * @return
     */
    public static OrderActivityUserNum getUserNum(List<OrderActivityUserNum> list, Timestamp timestamp) {
        for (OrderActivityUserNum activityUserNum : list) {
            if (activityUserNum.getDate().equals(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE, timestamp))) {
                return activityUserNum;
            }
        }
        return null;
    }

    /**
     * 检查规格库存，更新秒杀规格库存以保证秒杀库存不大于规格库存
     * @param goodsIds
     */
    public void updateSeckillProcudtStock(List<Integer> goodsIds){
        List<SecKillProductDefineRecord> activeSeckillList = getSecKillWithMonitor(goodsIds);
        for(SecKillProductDefineRecord  seckillPrd : activeSeckillList){
            int prdNumber = goodsService.goodsSpecProductService.getPrdNumberByPrdId(seckillPrd.getProductId());
            if(prdNumber < seckillPrd.getStock()){
                db().update(SEC_KILL_PRODUCT_DEFINE).set(SEC_KILL_PRODUCT_DEFINE.STOCK,prdNumber).set(SEC_KILL_PRODUCT_DEFINE.TOTAL_STOCK,seckillPrd.getTotalStock()-(seckillPrd.getStock()-prdNumber)).execute();
            }
        }
    }

    /**
     * 当前有效的进行中秒杀
     * @return
     */
    private List<SecKillProductDefineRecord> getSecKillWithMonitor(List<Integer> goodsIds){
        return db().select(SEC_KILL_PRODUCT_DEFINE.fields()).from(SEC_KILL_PRODUCT_DEFINE).leftJoin(SEC_KILL_DEFINE).on(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(SEC_KILL_DEFINE.SK_ID)).where(SEC_KILL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(SEC_KILL_DEFINE.START_TIME.lt(DateUtils.getLocalDateTime())).and(SEC_KILL_DEFINE.END_TIME.gt(DateUtils.getLocalDateTime()))).and(SEC_KILL_PRODUCT_DEFINE.GOODS_ID.in(goodsIds)).fetchInto(SecKillProductDefineRecord.class);
    }

    /**
     * 校验该秒杀规格是否还有库存
     * @param skId
     * @param productId 规格ID
     * @return
     */
    private boolean checkSeckillProductStock(int skId,int productId){
        int seckillStock = db().select(SEC_KILL_PRODUCT_DEFINE.STOCK).from(SEC_KILL_PRODUCT_DEFINE).where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(skId).and(SEC_KILL_PRODUCT_DEFINE.PRODUCT_ID.eq(productId))).fetchOneInto(Integer.class);
        int prdNumber = db().select(GOODS_SPEC_PRODUCT.PRD_NUMBER).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(productId)).fetchOneInto(Integer.class);
        return seckillStock > 0 && prdNumber > 0;
    }

    /**
     * 判断秒杀规格的可用状态
     * @param param
     * @param userId
     * @return 0正常可用;1该活动不存在;2该活动已停用;3该活动未开始;4该活动已结束;5商品已抢光;6该用户已达到限购数量上限;
     *          7该秒杀为会员专属，该用户没有对应会员卡；8该规格无库存；9有待支付的秒杀订单
     */
    public SeckillCheckVo canApplySecKill(SecKillProductParam param, Integer userId) {
        SeckillCheckVo vo = new SeckillCheckVo();

        SecKillDefineRecord secKill = db().fetchAny(SEC_KILL_DEFINE,SEC_KILL_DEFINE.SK_ID.eq(param.getSkId()));
        int goodsNumber = saas.getShopApp(getShopId()).goods.getGoodsByProductId(param.getProductId()).into(GoodsRecord.class).getGoodsNumber();
        byte res = this.canApplySecKill(secKill,goodsNumber,userId,param.getGoodsId());

        if(res == 0){
            if(!this.checkSeckillProductStock(param.getSkId(),param.getProductId())){
                vo.setState(BaseConstant.ACTIVITY_STATUS_PRD_NOT_HAS_NUM);
            }
            String orderSn = seckillList.checkSeckillOrderWaitPay(param.getSkId(),userId);
            if(orderSn != null){
                vo.setState(BaseConstant.ACTIVITY_STATUS_HAS_ORDER_READY_TO_PAY);
                vo.setOrderSn(orderSn);
            }
            if(secKill.getLimitAmount() != null && secKill.getLimitAmount() > 0){
                int seckillGoodsNum = getUserSeckilledGoodsNumber(param.getSkId(),userId,param.getGoodsId());
                if((seckillGoodsNum + param.getGoodsNumber()) > secKill.getLimitAmount()){
                    vo.setState(BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT);
                    vo.setDiffNumber(secKill.getLimitAmount() - seckillGoodsNum);
                }
            }
        }else{
            vo.setState(res);
        }
        return vo;
    }

    /**
     * 判断秒杀活动的可用状态
     * @param secKill 秒杀基本信息
     * @param goodsNumber goods表的库存
     * @return 0正常;1该活动不存在;2该活动已停用;3该活动未开始;4该活动已结束;5商品已抢光;6该用户已达到限购数量上限;7该秒杀为会员专属，该用户没有对应会员卡
     */
    public Byte canApplySecKill(SecKillDefineRecord secKill,Integer goodsNumber,Integer userId,Integer goodsId) {
        if(secKill == null){
            return BaseConstant.ACTIVITY_STATUS_NOT_HAS;
        }
        if(BaseConstant.ACTIVITY_STATUS_DISABLE.equals(secKill.getStatus())){
            return BaseConstant.ACTIVITY_STATUS_STOP;
        }
        if(secKill.getStartTime().after(DateUtils.getLocalDateTime())){
            return BaseConstant.ACTIVITY_STATUS_NOT_START;
        }
        if(secKill.getEndTime().before(DateUtils.getLocalDateTime())){
            return BaseConstant.ACTIVITY_STATUS_END;
        }
        int minStock = goodsNumber < secKill.getStock() ? goodsNumber : secKill.getStock();
        if(minStock <= 0){
            return BaseConstant.ACTIVITY_STATUS_NOT_HAS_NUM;
        }
        if(secKill.getLimitAmount() > 0 && getUserSeckilledGoodsNumber(secKill.getSkId(),userId,goodsId) >= secKill.getLimitAmount()){
            return BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT;
        }
        if(StringUtil.isNotEmpty(secKill.getCardId()) && !userCardExclusiveSeckillIsValid(secKill.getCardId(),userId)){
            return BaseConstant.ACTIVITY_STATUS_NOT_HAS_MEMBER_CARD;
        }

        return BaseConstant.ACTIVITY_STATUS_CAN_USE;
    }

    /**
     * 已对该活动秒杀下单的数量
     * @param skId
     * @param userId
     * @return
     */
    private Integer getUserSeckilledGoodsNumber(Integer skId,Integer userId,Integer goodsId) {
        return db().select(DSL.sum(ORDER_INFO.GOODS_AMOUNT)).from(SEC_KILL_LIST).leftJoin(ORDER_INFO).on(SEC_KILL_LIST.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .where(SEC_KILL_LIST.SK_ID.eq(skId)
                .and(SEC_KILL_LIST.USER_ID.eq(userId))
                .and(SEC_KILL_LIST.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(SEC_KILL_LIST.GOODS_ID.eq(goodsId))).groupBy(ORDER_INFO.USER_ID).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 判断会员专享的秒杀活动该用户是否可参与
     * @param cardIds 会员卡ID字符串，逗号隔开
     * @param userId
     * @return true是可参与
     */
    private Boolean userCardExclusiveSeckillIsValid(String cardIds,Integer userId) {
        List<ValidUserCardBean> cards = saas.getShopApp(getShopId()).userCard.userCardDao.getValidCardList(userId);
        List<Integer> validCardIds = cards.stream().map(ValidUserCardBean::getCardId).collect(Collectors.toList());
        List<Integer> seckillCardIds = Util.splitValueToList(cardIds);
        validCardIds.retainAll(seckillCardIds);
        return (validCardIds != null && validCardIds.size() > 0);
    }

    /**
     * 取单个活动
     * @param actId
     * @return
     */
    public SecKillDefineRecord getSeckillActById(int actId){
        return db().selectFrom(SEC_KILL_DEFINE).where(SEC_KILL_DEFINE.SK_ID.eq(actId)).fetchOptionalInto(SecKillDefineRecord.class).orElse(null);
    }

    /**
     * 一个秒杀下的规格
     * @param skId
     * @return
     */
    private List<SecKillProductDefineRecord> getSeckillProductList(Integer skId){
        return db().selectFrom(SEC_KILL_PRODUCT_DEFINE).where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(skId)).fetchInto(SecKillProductDefineRecord.class);
    }

    /**
     * 小程序装修秒杀模块显示异步调用
     * @param moduleSecKill
     * @return
     */
    public ModuleSeckill getPageIndexSeckill(ModuleSeckill moduleSecKill){
        moduleSecKill.getSeckillGoods().forEach(seckillGoods->{
            SecKillDefineRecord seckill = getSeckillActById(seckillGoods.getActId());
            GoodsRecord goodsInfo = saas.getShopApp(getShopId()).goods.getGoodsRecordById(seckillGoods.getGoodsId());

            //set goods info
            seckillGoods.setGoodsId(seckillGoods.getGoodsId());
            seckillGoods.setGoodsName(goodsInfo.getGoodsName());
            seckillGoods.setGoodsImg(domainConfig.imageUrl(goodsInfo.getGoodsImg()));
            seckillGoods.setGoodsPrice(goodsInfo.getShopPrice());
            seckillGoods.setGoodsIsDelete(goodsInfo.getDelFlag());
            seckillGoods.setIsOnSale(goodsInfo.getIsOnSale());
            seckillGoods.setGoodsNumber(goodsInfo.getGoodsNumber());

            //set prd info
            seckillGoods.setIsPrd(goodsInfo.getIsDefaultProduct());
            seckillGoods.setMaxPrice(saas.getShopApp(getShopId()).goods.goodsSpecProductService.getMaxPrdPrice(goodsInfo.getGoodsId()));

            //set act info
            seckillGoods.setActBeginTime(seckill.getStartTime());
            seckillGoods.setActEndTime(seckill.getEndTime());
            seckillGoods.setActDelFlag(seckill.getDelFlag());
            seckillGoods.setActStatus(seckill.getStatus());
            seckillGoods.setSeckillSaleNum(seckill.getSaleNum());
            seckillGoods.setSeckillNum(seckill.getStock() > goodsInfo.getGoodsNumber() ? goodsInfo.getGoodsNumber() : seckill.getStock());
            seckillGoods.setBaseSale(seckill.getBaseSale() > 0 ? seckill.getBaseSale() : 0);
            seckillGoods.setSecPrice(getMinProductSecPrice(seckill.getSkId()));
            seckillGoods.setSecPriceInt(new BigDecimal(seckillGoods.getSecPrice().intValue()).compareTo(seckillGoods.getSecPrice()) == 0 ? (byte)1 : (byte)0);

            int unpaidSecPrdNum = 0;
            if(seckill.getDelFlag().equals(DelFlag.NORMAL_VALUE)){
                List<SecKillProductDefineRecord> seckillProducts = getSeckillProductList(seckill.getSkId());
                for(SecKillProductDefineRecord prd : seckillProducts){
                    unpaidSecPrdNum += seckillList.getUnpaidSeckillNumberByPrd(seckill.getSkId(),prd.getProductId());
                }
            }
            seckillGoods.setUnpaidGoodsNum(unpaidSecPrdNum);
            if(seckill.getStartTime().after(DateUtils.getLocalDateTime())){
                //未开始
                seckillGoods.setTimeState((byte)0);
                seckillGoods.setRemainingTime((seckill.getStartTime().getTime() - DateUtils.getLocalDateTime().getTime())/1000);
            }else if(seckill.getEndTime().after(DateUtils.getLocalDateTime())){
                //进行中
                seckillGoods.setTimeState((byte)1);
                seckillGoods.setRemainingTime((seckill.getEndTime().getTime() - DateUtils.getLocalDateTime().getTime())/1000);
            }else{
                //已结束
                seckillGoods.setTimeState((byte)2);
            }
        });
        return moduleSecKill;
    }

    /**
     * 更新秒杀库存和销量，减少number个库存，number可以是负数
     *
     * @param skId
     * @param productId
     * @param number
     */
    public void updateSeckillStock(int skId,int productId,int number){
        db().update(SEC_KILL_PRODUCT_DEFINE).set(SEC_KILL_PRODUCT_DEFINE.STOCK,SEC_KILL_PRODUCT_DEFINE.STOCK.sub(number)).set(SEC_KILL_PRODUCT_DEFINE.SALE_NUM,SEC_KILL_PRODUCT_DEFINE.SALE_NUM.add(number)).where(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(skId).and(SEC_KILL_PRODUCT_DEFINE.PRODUCT_ID.eq(productId))).execute();
        db().update(SEC_KILL_DEFINE).set(SEC_KILL_DEFINE.STOCK,SEC_KILL_DEFINE.STOCK.sub(number)).set(SEC_KILL_DEFINE.SALE_NUM,SEC_KILL_DEFINE.SALE_NUM.add(number)).where(SEC_KILL_DEFINE.SK_ID.eq(skId)).execute();
    }

    public Workbook exportSeckillOrderList(MarketOrderListParam param, String lang){
        SecKillDefineRecord secKillDefineRecord = db().fetchAny(SEC_KILL_DEFINE,SEC_KILL_DEFINE.SK_ID.eq(param.getActivityId()));
        List<MarketOrderListVo> list = saas.getShopApp(getShopId()).readOrder.marketOrderInfo.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_SEC_KILL);

        List<SeckillOrderExportVo> res = new ArrayList<>();
        list.forEach(order->{
            SeckillOrderExportVo vo = new SeckillOrderExportVo();
            vo.setActName(secKillDefineRecord.getName());
            vo.setOrderSn(order.getOrderSn());
            vo.setGoodsName(order.getGoods().get(0).getGoodsName());
            vo.setGoodsPrice(order.getGoods().get(0).getGoodsPrice());
            vo.setCreateTime(order.getCreateTime());
            vo.setUsername(order.getUsername() + ";" + (StringUtil.isNotBlank(order.getUserMobile()) ? order.getUserMobile() : ""));
            vo.setMoneyPaid(order.getMoneyPaid());
            vo.setConsignee(order.getConsignee() + ";" + order.getMobile());
            vo.setOrderStatus(OrderConstant.getOrderStatusName(order.getOrderStatus(),lang));

            res.add(vo);
        });

        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(res, SeckillOrderExportVo.class);
        return workbook;
    }

    /**
     * 从admin扫码活动码查看活动下的商品信息
     * @param activityId 活动id
     * @param baseCondition 过滤商品id基础条件
     * @return 可用商品id集合
     */
    public List<Integer> getSecKillCanUseGoodsIds(Integer activityId, Condition baseCondition) {
        Timestamp now = DateUtils.getLocalDateTime();
        SecKillDefineRecord record = getSeckillActById(activityId);
        if (record == null || record.getEndTime().compareTo(now) <= 0) {
            logger().debug("小程序-admin-seckill-扫码进小程序搜索列表页-活动已删除或停止");
            return new ArrayList<>();
        }

        List<Integer> goodsIds = db().selectDistinct(SEC_KILL_PRODUCT_DEFINE.GOODS_ID)
            .from(SEC_KILL_PRODUCT_DEFINE).innerJoin(Tables.GOODS).on(SEC_KILL_PRODUCT_DEFINE.GOODS_ID.eq(Tables.GOODS.GOODS_ID))
            .where(baseCondition.and(SEC_KILL_PRODUCT_DEFINE.SK_ID.eq(activityId)))
            .fetch(SEC_KILL_PRODUCT_DEFINE.GOODS_ID);

        Byte first = record.getFirst();
        // 未删除，停止，时间有效的活动
        Condition activityCondition = SEC_KILL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(SEC_KILL_DEFINE.END_TIME.gt(now));
        // 时间上有交集
        Condition timeCondition = SEC_KILL_DEFINE.START_TIME.le(record.getEndTime()).and(SEC_KILL_DEFINE.START_TIME.gt(record.getStartTime())).or(SEC_KILL_DEFINE.END_TIME.gt(record.getStartTime()).and(SEC_KILL_DEFINE.END_TIME.lt(record.getEndTime())));
        // 级别要高，或者级别相同但是创建的较晚
        Condition levelCondition = SEC_KILL_DEFINE.FIRST.gt(first).or(SEC_KILL_DEFINE.FIRST.eq(first).and(SEC_KILL_DEFINE.CREATE_TIME.gt(record.getCreateTime())));

        List<Integer> otherGoodsIds = db().selectDistinct(SEC_KILL_PRODUCT_DEFINE.GOODS_ID).from(SEC_KILL_DEFINE).innerJoin(SEC_KILL_PRODUCT_DEFINE).on(SEC_KILL_DEFINE.SK_ID.eq(SEC_KILL_PRODUCT_DEFINE.SK_ID))
            .where(activityCondition.and(timeCondition).and(levelCondition).and(SEC_KILL_PRODUCT_DEFINE.GOODS_ID.in(goodsIds)))
            .fetch(SEC_KILL_PRODUCT_DEFINE.GOODS_ID);

        goodsIds.removeAll(otherGoodsIds);
        return goodsIds;
    }


    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(SEC_KILL_DEFINE.SK_ID.as(CalendarAction.ID), SEC_KILL_DEFINE.NAME.as(CalendarAction.ACTNAME), SEC_KILL_DEFINE.START_TIME,
				SEC_KILL_DEFINE.END_TIME).from(SEC_KILL_DEFINE).where(SEC_KILL_DEFINE.SK_ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(SEC_KILL_DEFINE.SK_ID.as(CalendarAction.ID), SEC_KILL_DEFINE.NAME.as(CalendarAction.ACTNAME), SEC_KILL_DEFINE.START_TIME,
						SEC_KILL_DEFINE.END_TIME)
				.from(SEC_KILL_DEFINE)
				.where(SEC_KILL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(SEC_KILL_DEFINE.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(SEC_KILL_DEFINE.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(SEC_KILL_DEFINE.SK_ID.desc());
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
        SecKillDefineRecord secKillDefineRecord = getSeckillActById(actId);
        if(secKillDefineRecord.getActivityTag().equals(BaseConstant.YES) && StringUtil.isNotBlank(secKillDefineRecord.getActivityTagId())){
            tagService.userTagSvc.addActivityTag(userId,Util.stringToList(secKillDefineRecord.getActivityTagId()), TagSrcConstant.SECKILL,actId);
        }
    }
}
