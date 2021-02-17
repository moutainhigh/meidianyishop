package com.meidianyi.shop.service.shop.market.fullcut;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.excel.bean.ClassList;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.Goods;
import com.meidianyi.shop.db.shop.tables.OrderGoods;
import com.meidianyi.shop.db.shop.tables.OrderInfo;
import com.meidianyi.shop.db.shop.tables.records.MrkingStrategyConditionRecord;
import com.meidianyi.shop.db.shop.tables.records.MrkingStrategyRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPriceBo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.*;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisDataVo;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisTotalVo;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.market.fullcut.MrkingStrategyGoodsListParam;
import com.meidianyi.shop.service.pojo.wxapp.market.fullcut.MrkingStrategyGoodsListVo;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.member.GoodsCardCoupleService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.MrkingStrategy.MRKING_STRATEGY;
import static com.meidianyi.shop.db.shop.tables.MrkingStrategyCondition.MRKING_STRATEGY_CONDITION;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;

/**
 * @author: 王兵兵
 * @create: 2019-08-09 18:47
 **/
@Primary
@Service
public class MrkingStrategyService extends ShopBaseService {
    private static OrderGoods og = OrderGoods.ORDER_GOODS.as("og");
    private static OrderInfo oi = OrderInfo.ORDER_INFO.as("oi");
    private static Goods g = Goods.GOODS.as("g");

    /**
     * 启用状态
     */
    public static final byte STATUS_NORMAL = 1;
    /**
     * 停用状态
     */
    public static final byte STATUS_DISABLED = 0;

    //活动选定的商品范围
    /**
     * 全部商品
     */
    public final static Byte ACT_TYPE_ALL = 0;
    /**
     * 部分商品
     */
    public final static Byte ACT_TYPE_SECTION = 1;

    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsCardCoupleService goodsCardCoupleService;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private CartService cartService;
    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 新建满折满减活动
     */
    public void addMrkingStrategy(MrkingStrategyAddParam param) {
        this.transaction(() -> {
            MrkingStrategyRecord record = db().newRecord(MRKING_STRATEGY);
            assign(param, record);
            record.insert();
            Integer id = record.getId();
            for(MrkingStrategyCondition condition : param.getConditionAddParams()){
                MrkingStrategyConditionRecord conditionRecord = new MrkingStrategyConditionRecord();
                assign(condition,conditionRecord);
                conditionRecord.setStrategyId(id);
                db().executeInsert(conditionRecord);
            }
        });
    }

    /**
     * 满折满减活动列表分页查询
     *
     */
    public PageResult<MrkingStrategyPageListQueryVo> getPageList(MrkingStrategyPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(MRKING_STRATEGY.ID,MRKING_STRATEGY.ACT_NAME,MRKING_STRATEGY.TYPE,MRKING_STRATEGY.START_TIME,MRKING_STRATEGY.END_TIME, MRKING_STRATEGY.STATUS,MRKING_STRATEGY.STRATEGY_PRIORITY).
            from(MRKING_STRATEGY);
        if(param.getState() > 0) {
            /** 状态过滤*/
            Timestamp now = DateUtils.getLocalDateTime();
            switch(param.getState()) {
                case (byte)1:
                    select.where(MRKING_STRATEGY.STATUS.eq(STATUS_NORMAL)).and(MRKING_STRATEGY.START_TIME.lt(now)).and(MRKING_STRATEGY.END_TIME.gt(now));
                    break;
                case (byte)2:
                    select.where(MRKING_STRATEGY.STATUS.eq(STATUS_NORMAL)).and(MRKING_STRATEGY.START_TIME.gt(now));
                    break;
                case (byte)3:
                    select.where(MRKING_STRATEGY.STATUS.eq(STATUS_NORMAL)).and(MRKING_STRATEGY.END_TIME.lt(now));
                    break;
                case (byte)4:
                    select.where(MRKING_STRATEGY.STATUS.eq(STATUS_DISABLED));
                    break;
                default:
            }
        }
        select.where(MRKING_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(MRKING_STRATEGY.STRATEGY_PRIORITY.desc(),MRKING_STRATEGY.CREATE_TIME.desc());
        PageResult<MrkingStrategyPageListQueryVo> res =  getPageResult(select,param.getCurrentPage(),param.getPageRows(),MrkingStrategyPageListQueryVo.class);
        if(res.dataList != null){
            for(MrkingStrategyPageListQueryVo mrkingStrategy : res.dataList){
                mrkingStrategy.setCondition(this.getMrkingStrategyCondition(mrkingStrategy.getId()));
                mrkingStrategy.setCurrentState(Util.getActStatus(mrkingStrategy.getStatus(),mrkingStrategy.getStartTime(),mrkingStrategy.getEndTime()));
            }
        }

        return res;
    }

    /**
     * 取某满折满减活动下的优惠规则
     *
     */
    public List<MrkingStrategyCondition> getMrkingStrategyCondition(Integer strategyId){
        return db().select(MRKING_STRATEGY_CONDITION.FULL_MONEY,MRKING_STRATEGY_CONDITION.REDUCE_MONEY,MRKING_STRATEGY_CONDITION.AMOUNT,MRKING_STRATEGY_CONDITION.DISCOUNT).
            from(MRKING_STRATEGY_CONDITION).
            where(MRKING_STRATEGY_CONDITION.STRATEGY_ID.eq(strategyId)).
            fetchInto(MrkingStrategyCondition.class);
    }

    /**
     * 取单个满折满减活动信息
     *
     */
    public MrkingStrategyVo getMrkingStrategyById(Integer id){
        MrkingStrategyRecord record = db().selectFrom(MRKING_STRATEGY).where(MRKING_STRATEGY.ID.eq(id)).fetchAny();
        MrkingStrategyVo res = record.into(MrkingStrategyVo.class);

        if(StringUtil.isNotEmpty(record.getRecommendGoodsId())){
            res.setRecommendGoodsIds(Util.splitValueToList(record.getRecommendGoodsId()));
            res.setRecommendGoods(saas().getShopApp(getShopId()).goods.selectGoodsViewList(Util.stringToList(record.getRecommendGoodsId())));
        }
        if(StringUtil.isNotEmpty(record.getCardId())){
            res.setCardIds(Util.splitValueToList(record.getCardId()));
            res.setMemberCards(saas().getShopApp(getShopId()).member.card.getMemberCardByCardIds(Util.splitValueToList(record.getCardId())));
        }
        if(StringUtil.isNotEmpty(record.getRecommendCatId())){
            res.setRecommendCatIds(Util.splitValueToList(record.getRecommendCatId()));
            res.setRecommendCat(saas.sysCate.getList(res.getRecommendCatIds()));
        }
        if(StringUtil.isNotEmpty(record.getRecommendBrandId())){
            res.setRecommendBrandIds(Util.splitValueToList(record.getRecommendBrandId()));
            res.setRecommendBrand(saas.getShopApp(getShopId()).goods.goodsBrand.getGoodsBrandVoById(res.getRecommendBrandIds()));
        }
        if(StringUtil.isNotEmpty(record.getRecommendSortId())){
            res.setRecommendSortIds(Util.splitValueToList(record.getRecommendSortId()));
            res.setRecommendSort(saas.getShopApp(getShopId()).goods.goodsSort.getListByIds(res.getRecommendSortIds()));
        }

        res.setCondition(this.getMrkingStrategyCondition(record.getId()));

        return res;
    }

    /**
     * 更新满折满减活动
     *
     */
    public void updateMrkingStrategy(MrkingStrategyUpdateParam param) {
        MrkingStrategyRecord record = new MrkingStrategyRecord();
        assign(param,record);
        db().executeUpdate(record);
    }

    /**
     * 删除满折满减活动
     *
     */
    public void delMrkingStrategy(Integer id) {
        db().update(MRKING_STRATEGY).
            set(MRKING_STRATEGY.DEL_FLAG,DelFlag.DISABLE.getCode()).
            where(MRKING_STRATEGY.ID.eq(id)).
            execute();
    }

    public MrkingStrategyGoodsListVo getWxAppGoodsList(MrkingStrategyGoodsListParam param,Integer userId){
        MrkingStrategyGoodsListVo vo = new MrkingStrategyGoodsListVo();

        //校验活动
        MrkingStrategyRecord mrkingStrategyAct = db().selectFrom(MRKING_STRATEGY).where(MRKING_STRATEGY.ID.eq(param.getStrategyId())).fetchAny();
        if(mrkingStrategyAct == null || mrkingStrategyAct.getDelFlag().equals(DelFlag.DISABLE_VALUE)){
            vo.setState((byte)1);
            return vo;
        }else if(mrkingStrategyAct.getStartTime().after(DateUtils.getLocalDateTime())){
            vo.setState((byte)2);
            return vo;
        }else if(mrkingStrategyAct.getEndTime().before(DateUtils.getLocalDateTime())){
            vo.setState((byte)3);
            return vo;
        }

        //Conditions
        List<MrkingStrategyCondition> conditions = getMrkingStrategyCondition(param.getStrategyId());
        vo.setCondition(conditions);
        vo.setType(mrkingStrategyAct.getType());

        //根据购物车里的商品计算底边条的提醒文案
        WxAppCartBo cartBo = cartService.getCartList(userId,null, BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION,param.getStrategyId());
        vo.setFullPriceDoc(getStrategyGoodsDoc(cartBo,mrkingStrategyAct.getType(),conditions));
        vo.setTotalPrice(cartBo.getCartGoodsList().stream().map(
            wxAppCartGoods -> wxAppCartGoods.getPrdPrice().multiply(BigDecimal.valueOf(wxAppCartGoods.getCartNumber()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add));

        if(StringUtil.isNotEmpty(mrkingStrategyAct.getCardId())){
            //设置了持有会员卡才可以参与活动
            List<Integer> cardIds = Util.splitValueToList(mrkingStrategyAct.getCardId());
            List<ValidUserCardBean> cards = saas.getShopApp(getShopId()).userCard.userCardDao.getValidCardList(userId);
            List<Integer> validCardIds = cards.stream().map(ValidUserCardBean::getCardId).collect(Collectors.toList());
            validCardIds.retainAll(cardIds);

            if(validCardIds == null || validCardIds.size() == 0){
                vo.setState((byte)4);
                vo.setCardList(memberCardService.getMemberCardByCardIds(cardIds));
            }
        }

        PageResult<MrkingStrategyGoodsListVo.Goods> goodsPageResult;
        //过滤掉其中用户不能买的会员专享商品
        if (mrkingStrategyAct.getActType().equals(ACT_TYPE_SECTION)) {
            List<Integer> inGoodsIds = getMrkingStrategyGoodsIds(mrkingStrategyAct);
            List<Integer> userExclusiveGoodsIds = goodsCardCoupleService.getGoodsUserNotExclusive(userId);
            inGoodsIds.removeAll(userExclusiveGoodsIds);
            goodsPageResult = getGoods(inGoodsIds,Collections.emptyList(),param.getSearch(),param.getCurrentPage(),param.getPageRows());
        }else {
            List<Integer> notInGoodsIds = goodsCardCoupleService.getGoodsUserNotExclusive(userId);
            goodsPageResult = getGoods(Collections.emptyList(),notInGoodsIds,param.getSearch(),param.getCurrentPage(),param.getPageRows());
        }

        goodsPageResult.getDataList().forEach(goods -> {
            if(StringUtil.isNotEmpty(goods.getGoodsImg())){
                goods.setGoodsImg(domainConfig.imageUrl(goods.getGoodsImg()));
            }
            if(goods.getIsDefaultProduct() == 1){
                goods.setPrdId(goodsService.goodsSpecProductService.getDefaultPrdId(goods.getGoodsId()));
            }

            //处理限时降价、首单特惠、会员等级价对商品价格的覆盖
            GoodsPriceBo goodsPriceBo = saas.getShopApp(getShopId()).reducePrice.parseGoodsPrice(goods.getGoodsId(),userId);
            goods.setGoodsPriceAction(goodsPriceBo.getGoodsPriceAction());
            goods.setGoodsPrice(goodsPriceBo.getGoodsPrice());
            goods.setMaxPrice(goodsPriceBo.getMaxPrice());
            goods.setMarketPrice(goodsPriceBo.getMaxPrice());

            goods.setCartGoodsNumber(cartBo.getCartGoodsList().stream().filter(cartGoods->cartGoods.getGoodsId().equals(goods.getGoodsId())).mapToInt(WxAppCartGoods::getCartNumber).sum());
        });
        vo.setGoods(goodsPageResult);

        return vo;
    }

    /**
     * 活动包含的所有商品ID（指定部分商品类型的活动）
     * @param record
     * @return
     */
    private List<Integer> getMrkingStrategyGoodsIds(MrkingStrategyRecord record){
        List<Integer> res = new ArrayList<>();

        if(StringUtil.isNotBlank(record.getRecommendGoodsId())){
            List<Integer> goodsIds = Util.splitValueToList(record.getRecommendGoodsId());
            res.removeAll(goodsIds);
            res.addAll(goodsIds);
        }

        if(StringUtil.isNotBlank(record.getRecommendCatId()) || StringUtil.isNotBlank(record.getRecommendSortId()) || StringUtil.isNotBlank(record.getRecommendBrandId())){
            List<Integer> goodsIds = goodsService.getOnShelfGoodsIdList(Util.splitValueToList(record.getRecommendCatId()),Util.splitValueToList(record.getRecommendSortId()),Util.splitValueToList(record.getRecommendBrandId()));
            res.removeAll(goodsIds);
            res.addAll(goodsIds);
        }


        return res;
    }

    /**
     * 查出goods列表
     * @param inGoodsIds
     * @param search
     * @param currentPage
     * @param pageRows
     * @return
     */
    private PageResult<MrkingStrategyGoodsListVo.Goods> getGoods(List<Integer> inGoodsIds,List<Integer> notInGoodsIds,String search,Integer currentPage,Integer pageRows){
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
        if(CollectionUtils.isNotEmpty(notInGoodsIds)){
            select.where(GOODS.GOODS_ID.notIn(notInGoodsIds));
        }
        return getPageResult(select,currentPage,pageRows,MrkingStrategyGoodsListVo.Goods.class);
    }

    /**
     * 满折满减主商品文案
     * @param cartBo
     * @param strategyType  类型,1每满减 2满减 3满折 4仅第X件打折
     *       @param               conditions
     * @return
     */
    private MrkingStrategyGoodsListVo.FullPriceDoc getStrategyGoodsDoc(WxAppCartBo cartBo,Byte strategyType,List<MrkingStrategyCondition> conditions){
        MrkingStrategyGoodsListVo.FullPriceDoc doc = new MrkingStrategyGoodsListVo.FullPriceDoc();
        BigDecimal totalPrice = cartBo.getCartGoodsList().stream().map(
            wxAppCartGoods -> wxAppCartGoods.getPrdPrice().multiply(BigDecimal.valueOf(wxAppCartGoods.getCartNumber()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(totalPrice.compareTo(BigDecimal.ZERO) <= 0){
            doc.setDocType((byte)0);
            return doc;
        }

        switch (strategyType){
            case 1:
                //每满减，只有一条condition
                if (processStrategyType1(cartBo, conditions, doc, totalPrice)) {
                    return doc;
                }
                break;

            case 2:
                //满减，可以有多条condition
                if (processStrategyType2(cartBo, conditions, doc, totalPrice)) {
                    return doc;
                }
                break;

            case 3:
                //满折，可以有多条condition
                if (processStrategyType3(cartBo, conditions, doc, totalPrice)) {
                    return doc;
                }
                break;
            case 4:
                //第amount件，打discount折，只有一条condition
                return processStrategyType4(cartBo, conditions, doc);
            default:

        }

        return doc;
    }

    /**
     * 第amount件，打discount折，只有一条condition
     * @param cartBo
     * @param conditions
     * @param doc
     * @return
     */
    private MrkingStrategyGoodsListVo.FullPriceDoc processStrategyType4(WxAppCartBo cartBo, List<MrkingStrategyCondition> conditions, MrkingStrategyGoodsListVo.FullPriceDoc doc) {
        MrkingStrategyCondition c = conditions.get(0);
        BigDecimal reduceMoney = BigDecimal.ZERO;
        for (WxAppCartGoods g : cartBo.getCartGoodsList()){
            if(g.getCartNumber() >= c.getAmount()){
                reduceMoney = reduceMoney.add(g.getGoodsPrice().multiply(BigDecimal.ONE.subtract(c.getDiscount().divide(BigDecimal.valueOf(10))))).setScale(2,BigDecimal.ROUND_HALF_UP);
            }
        }
        if(reduceMoney.compareTo(BigDecimal.ZERO) <= 0){
            doc.setDocType((byte)0);
            return doc;
        }else {
            doc.setDocType((byte)1);
            doc.setReduceMoney(reduceMoney);
            return doc;
        }
    }

    /**
     * 满折，可以有多条condition
     * @param cartBo
     * @param conditions
     * @param doc
     * @param totalPrice
     * @return
     */
    private boolean processStrategyType3(WxAppCartBo cartBo, List<MrkingStrategyCondition> conditions, MrkingStrategyGoodsListVo.FullPriceDoc doc, BigDecimal totalPrice) {
        if(conditions.get(0).getFullMoney() != null && conditions.get(0).getFullMoney().compareTo(BigDecimal.ZERO) > 0) {
            //满金额类型
            conditions = conditions.stream().sorted(Comparator.comparing(MrkingStrategyCondition::getFullMoney).reversed()).collect(Collectors.toList());
            BigDecimal discount = null;
            for(MrkingStrategyCondition c:conditions){
                if(totalPrice.compareTo(c.getFullMoney()) >= 0){
                    discount = c.getDiscount();
                    break;
                }
            }
            if(discount != null && discount.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal reduceMoney = totalPrice.multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(10)))).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)1);
                doc.setReduceMoney(reduceMoney);
                return true;
            }else {
                BigDecimal diffPrice = conditions.get(conditions.size() - 1).getFullMoney().subtract(totalPrice).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)3);
                doc.setDiffPrice(diffPrice);
                doc.setDiscount(conditions.get(conditions.size() - 1).getDiscount());
                return true;
            }
        }else if(conditions.get(0).getAmount() != null && conditions.get(0).getAmount() > 0){
            //满件数类型
            int totalGoodsNum = cartBo.getCartGoodsList().stream().mapToInt(WxAppCartGoods::getCartNumber).sum();
            conditions = conditions.stream().sorted(Comparator.comparing(MrkingStrategyCondition::getAmount).reversed()).collect(Collectors.toList());
            BigDecimal discount = null;
            for(MrkingStrategyCondition c:conditions){
                if(totalGoodsNum >= c.getAmount()){
                    discount = c.getDiscount();
                    break;
                }
            }
            if(discount != null && discount.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal reduceMoney = totalPrice.multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(10)))).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)1);
                doc.setReduceMoney(reduceMoney);
                return true;
            }else {
                int diffNumber = conditions.get(conditions.size() - 1).getAmount() - totalGoodsNum;
                doc.setDocType((byte)5);
                doc.setDiscount(conditions.get(conditions.size() - 1).getDiscount());
                doc.setDiffNumber(diffNumber);
                return true;
            }
        }
        return false;
    }

    private boolean processStrategyType2(WxAppCartBo cartBo, List<MrkingStrategyCondition> conditions, MrkingStrategyGoodsListVo.FullPriceDoc doc, BigDecimal totalPrice) {
        //满减，可以有多条condition
        if(conditions.get(0).getFullMoney() != null && conditions.get(0).getFullMoney().compareTo(BigDecimal.ZERO) > 0){
            //满金额类型
            conditions = conditions.stream().sorted(Comparator.comparing(MrkingStrategyCondition::getFullMoney).reversed()).collect(Collectors.toList());
            BigDecimal reduceMoney = null;
            for(MrkingStrategyCondition c:conditions){
                if(totalPrice.compareTo(c.getFullMoney()) >= 0){
                    reduceMoney = c.getReduceMoney();
                    break;
                }
            }
            if(reduceMoney != null && reduceMoney.compareTo(BigDecimal.ZERO) > 0){
                doc.setDocType((byte)1);
                doc.setReduceMoney(reduceMoney);
                return true;
            }else {
                BigDecimal diffPrice = conditions.get(conditions.size() - 1).getFullMoney().subtract(totalPrice).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)2);
                doc.setReduceMoney(conditions.get(conditions.size() - 1).getReduceMoney());
                doc.setDiffPrice(diffPrice);
                return true;
            }

        }else if(conditions.get(0).getAmount() != null && conditions.get(0).getAmount() > 0){
            //满件数类型
            int totalGoodsNum = cartBo.getCartGoodsList().stream().mapToInt(WxAppCartGoods::getCartNumber).sum();
            conditions = conditions.stream().sorted(Comparator.comparing(MrkingStrategyCondition::getAmount).reversed()).collect(Collectors.toList());
            BigDecimal reduceMoney = null;
            for(MrkingStrategyCondition c:conditions){
                if(totalGoodsNum >= c.getAmount()){
                    reduceMoney = c.getReduceMoney();
                    break;
                }
            }
            if(reduceMoney != null && reduceMoney.compareTo(BigDecimal.ZERO) > 0){
                doc.setDocType((byte)1);
                doc.setReduceMoney(reduceMoney);
                return true;
            }else {
                int diffNumber = conditions.get(conditions.size() - 1).getAmount() - totalGoodsNum;
                doc.setDocType((byte)4);
                doc.setReduceMoney(conditions.get(conditions.size() - 1).getReduceMoney());
                doc.setDiffNumber(diffNumber);
                return true;
            }
        }
        return false;
    }

    /**
     * 每满减，只有一条condition
     * @param cartBo
     * @param conditions
     * @param doc
     * @param totalPrice
     * @return true
     */
    private boolean processStrategyType1(WxAppCartBo cartBo, List<MrkingStrategyCondition> conditions, MrkingStrategyGoodsListVo.FullPriceDoc doc, BigDecimal totalPrice) {
        MrkingStrategyCondition condition = conditions.get(0);
        if(condition.getFullMoney() != null && condition.getFullMoney().compareTo(BigDecimal.ZERO) > 0){
            //满金额类型
            if(totalPrice.compareTo(condition.getFullMoney()) >= 0){
                BigDecimal reduceMoney = (totalPrice.divide(condition.getFullMoney(),0,BigDecimal.ROUND_DOWN)).multiply(condition.getReduceMoney()).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)1);
                doc.setReduceMoney(reduceMoney);
                return true;
            }else{
                BigDecimal diffPrice = condition.getFullMoney().subtract(totalPrice).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)2);
                doc.setReduceMoney(condition.getReduceMoney());
                doc.setDiffPrice(diffPrice);
                return true;
            }
        }else if(condition.getAmount() != null && condition.getAmount() > 0){
            //满件数类型
            int totalGoodsNum = cartBo.getCartGoodsList().stream().mapToInt(WxAppCartGoods::getCartNumber).sum();
            if(totalGoodsNum >= condition.getAmount()){
                BigDecimal reduceMoney = condition.getReduceMoney().multiply(BigDecimal.valueOf(totalGoodsNum/condition.getAmount())).setScale(2,BigDecimal.ROUND_HALF_UP);
                doc.setDocType((byte)1);
                doc.setReduceMoney(reduceMoney);
                return true;
            }else{
                int diffNumber = condition.getAmount() - totalGoodsNum;
                doc.setDocType((byte)4);
                doc.setDiffNumber(diffNumber);
                doc.setReduceMoney(condition.getReduceMoney());
                return true;
            }
        }
        return false;
    }

    public List<WxAppCartGoods> getWxAppCheckedGoodsList(MrkingStrategyGoodsListParam param,Integer userId){
        WxAppCartBo cartBo = cartService.getCartList(userId,null, BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION,param.getStrategyId());
        return cartBo.getCartGoodsList();
    }


    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
        return db().select(MRKING_STRATEGY.ID, MRKING_STRATEGY.ACT_NAME, MRKING_STRATEGY.START_TIME,
            MRKING_STRATEGY.END_TIME).from(MRKING_STRATEGY).where(MRKING_STRATEGY.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
    public PageResult<MarketVo> getListNoEnd(MarketParam param) {
        SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
            .select(MRKING_STRATEGY.ID, MRKING_STRATEGY.ACT_NAME, MRKING_STRATEGY.START_TIME,
                MRKING_STRATEGY.END_TIME)
            .from(MRKING_STRATEGY)
            .where(MRKING_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(MRKING_STRATEGY.STATUS
                .eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(MRKING_STRATEGY.END_TIME.gt(DateUtils.getSqlTimestamp()))))
            .orderBy(MRKING_STRATEGY.ID.desc());
        PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
            MarketVo.class);
        return pageResult;
    }

    /**
     * 活动订单
     */
    public PageResult<MrkingStrategyOrderVo> getMrkingStrategyOrderList(MrkingStrategyOrderParam param, String lang) {
        //默认排序方式---下单时间
        PageResult<MrkingStrategyOrderVo> vo = this.getPageResult(buildRedemptionListOption(param), param.getCurrentPage(), param.getPageRows(), MrkingStrategyOrderVo.class);
        /** 填充商品行 */
        for (MrkingStrategyOrderVo order : vo.dataList) {
            List<MarketOrderGoodsListVo> goods = orderGoods.getMarketOrderGoodsByOrderSn(order.getOrderSn());
            goods.forEach(g -> {
                if (StringUtil.isNotBlank(g.getGoodsImg())) {
                    g.setGoodsImg(domainConfig.imageUrl(g.getGoodsImg()));
                }
                g.setPerDiscount(BigDecimalUtil.multiply(g.getPerDiscount(), new BigDecimal(g.getGoodsNumber())));
                g.setDiscountedGoodsPrice(BigDecimalUtil.multiply(g.getDiscountedGoodsPrice(), new BigDecimal(g.getGoodsNumber())));
            });
            order.setGoods(goods);
            order.setOrderStatusName(OrderConstant.getOrderStatusName(order.getOrderStatus(), lang));
        }

        return vo;
    }

    /**
     * Build redemption list option select condition step.
     * 构建满折满减订单列表查询条件
     * activity_type:营销活动类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品 6限时降价 7加价购'
     *
     * @param param the param
     * @return the select condition step
     */
    private SelectConditionStep<? extends Record> buildRedemptionListOption(MrkingStrategyOrderParam param) {
        SelectConditionStep<Record5<String, Byte, Integer, String, String>> conditionStep = db().select(oi.ORDER_SN, oi.ORDER_STATUS, oi.USER_ID, USER.USERNAME, USER.MOBILE).from(oi).leftJoin(USER).on(oi.USER_ID.eq(USER.USER_ID)).leftJoin(og).on(og.ORDER_SN.eq(oi.ORDER_SN)).where(og.STRA_ID.eq(param.getActivityId()));


        if (StringUtils.isNotBlank(param.getGoodsName())) {
            conditionStep = conditionStep.and(og.GOODS_NAME.like(likeValue(param.getGoodsName())));
        }
        if (StringUtils.isNotBlank(param.getOrderSn())) {
            conditionStep = conditionStep.and(og.ORDER_SN.like(likeValue(param.getOrderSn())));
        }

        if (StringUtils.isNotBlank(param.getUserInfo())) {
            conditionStep = conditionStep.and(USER.MOBILE.contains(param.getUserInfo()).or(USER.USERNAME.contains(param.getUserInfo())));
        }
        if (param.getOrderStatus() != null && param.getOrderStatus().length != 0) {
            conditionStep = conditionStep.and(oi.ORDER_STATUS.in(param.getOrderStatus()));
        }
        conditionStep.groupBy(oi.ORDER_SN, oi.ORDER_STATUS, oi.USER_ID, USER.USERNAME, USER.MOBILE);

        return conditionStep;
    }

    public Workbook exportOrderList(MrkingStrategyOrderParam param, String lang) {
        SelectConditionStep<? extends Record> select = buildRedemptionListOption(param);
        List<MrkingStrategyOrderVo> list = select.fetchInto(MrkingStrategyOrderVo.class);
        /** 填充商品行 */
        for (MrkingStrategyOrderVo order : list) {
            List<MarketOrderGoodsListVo> goods = orderGoods.getMarketOrderGoodsByOrderSn(order.getOrderSn());
            goods.forEach(g -> {
                if (StringUtil.isNotBlank(g.getGoodsImg())) {
                    g.setGoodsImg(domainConfig.imageUrl(g.getGoodsImg()));
                }
                g.setPerDiscount(BigDecimalUtil.multiply(g.getPerDiscount(), new BigDecimal(g.getGoodsNumber())));
                g.setDiscountedGoodsPrice(BigDecimalUtil.multiply(g.getDiscountedGoodsPrice(), new BigDecimal(g.getGoodsNumber())));
            });
            order.setGoods(goods);
        }

        List<MrkingStrategyOrderExportVo> res = new ArrayList<>();
        list.forEach(order -> {
            MrkingStrategyOrderExportVo vo = new MrkingStrategyOrderExportVo();
            vo.setOrderSn(order.getOrderSn());
            vo.setGoods(order.getGoods().stream().map((g) -> {
                MrkingStrategyOrderGoodsExportVo goods = new MrkingStrategyOrderGoodsExportVo();
                goods.setGoodsName(g.getGoodsName() + (StringUtil.isNotBlank(g.getGoodsAttr()) ? (":" + g.getGoodsAttr()) : ""));
                goods.setGoodsPrice(g.getGoodsPrice());
                goods.setPerDiscount(BigDecimalUtil.multiply(g.getPerDiscount(), BigDecimal.valueOf(g.getGoodsNumber())));
                goods.setDiscountedGoodsPrice(BigDecimalUtil.multiply(g.getDiscountedGoodsPrice(), BigDecimal.valueOf(g.getGoodsNumber())));
                goods.setGoodsNumber(g.getGoodsNumber());
                return goods;
            }).collect(Collectors.toList()));
            vo.setUsername(order.getUsername() + ";" + (StringUtil.isNotBlank(order.getMobile()) ? order.getMobile() : ""));
            vo.setOrderStatus(OrderConstant.getOrderStatusName(order.getOrderStatus(), lang));

            res.add(vo);
        });

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        ClassList cList = new ClassList();
        cList.setUpClazz(MrkingStrategyOrderExportVo.class);
        cList.setInnerClazz(MrkingStrategyOrderGoodsExportVo.class);
        try {
            excelWriter.writeModelListByRegion(res, cList);
        } catch (Exception e) {
            logger().error("excel error", e);
        }

        return workbook;
    }

    /**
     * 满折满减效果分析的图表数据
     */
    public SeckillAnalysisDataVo getAnalysisData(MrkingStrategyAnalysisParam param) {
        SeckillAnalysisDataVo analysisVo = new SeckillAnalysisDataVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = DateUtils.currentMonthFirstDay();
            param.setStartTime(startDate);
            endDate = DateUtils.getLocalDateTime();
            param.setEndTime(endDate);
        }

        Map<Date, List<MrkingStrategyOrderAnalysisBo>> orderGoodsMap = getFullCutOrderDiscountMoney(param);

        //老用户的ID，为了给老用户总数去重
        Set<Integer> oldUserIds = new HashSet<>();

        //填充
        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            Date k = new Date(startDate.getTime());
            List<MrkingStrategyOrderAnalysisBo> v = orderGoodsMap.get(k);
            if (v != null) {
                /**活动实付金额 */
                BigDecimal totalPayment = BigDecimal.ZERO;
                /**活动优惠金额 */
                BigDecimal totalDiscount = BigDecimal.ZERO;
                /**费效比  */
                BigDecimal totalCostEffectivenessRatio = BigDecimal.ZERO;
                /**付款订单数 */
                Integer totalPaidOrderNumber = 0;
                /**付款商品件数 */
                Integer totalPaidGoodsNumber = 0;
                /**老成交用户数 */
                Integer totalOldUserNumber = 0;
                /**新成交用户数 */
                Integer totalNewUserNumber = 0;
                for (MrkingStrategyOrderAnalysisBo o : v) {
                    totalPayment = BigDecimalUtil.add(totalPayment, o.getDiscountedTotalPrice());
                    totalDiscount = BigDecimalUtil.add(totalDiscount, o.getPerDiscount());
                    totalPaidGoodsNumber += o.getGoodsNumber();
                }
                Map<Integer, List<MrkingStrategyOrderAnalysisBo>> orderListMap = v.stream().collect(Collectors.groupingBy(MrkingStrategyOrderAnalysisBo::getUserId));
                for (Map.Entry<Integer, List<MrkingStrategyOrderAnalysisBo>> entry : orderListMap.entrySet()) {
                    if (orderInfoService.isNewUser(entry.getKey(), entry.getValue().get(0).getCreateTime())) {
                        totalNewUserNumber++;
                    } else {
                        totalOldUserNumber++;
                        oldUserIds.add(entry.getKey());
                    }
                }

                totalPaidOrderNumber = v.stream().collect(Collectors.groupingBy(MrkingStrategyOrderAnalysisBo::getOrderSn)).size();
                totalCostEffectivenessRatio = BigDecimalUtil.divide(totalDiscount, totalPayment);

                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaymentAmount().add(totalPayment);
                analysisVo.getDiscountAmount().add(totalDiscount);
                analysisVo.getCostEffectivenessRatio().add(totalCostEffectivenessRatio);
                analysisVo.getPaidOrderNumber().add(totalPaidOrderNumber);
                analysisVo.getPaidGoodsNumber().add(totalPaidGoodsNumber);
                analysisVo.getNewUserNumber().add(totalNewUserNumber);
                analysisVo.getOldUserNumber().add(totalOldUserNumber);
            } else {
                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaymentAmount().add(BigDecimal.ZERO);
                analysisVo.getDiscountAmount().add(BigDecimal.ZERO);
                analysisVo.getCostEffectivenessRatio().add(BigDecimal.ZERO);
                analysisVo.getPaidOrderNumber().add(0);
                analysisVo.getPaidGoodsNumber().add(0);
                analysisVo.getNewUserNumber().add(0);
                analysisVo.getOldUserNumber().add(0);
            }
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }


        SeckillAnalysisTotalVo total = new SeckillAnalysisTotalVo();
        total.setTotalPayment(analysisVo.getPaymentAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        total.setTotalDiscount(analysisVo.getDiscountAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        total.setTotalCostEffectivenessRatio(total.getTotalPayment().compareTo(BigDecimal.ZERO) > 0 ? total.getTotalDiscount().divide(total.getTotalPayment(), 3, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        total.setTotalPaidOrderNumber(analysisVo.getPaidOrderNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalPaidGoodsNumber(analysisVo.getPaidGoodsNumber().stream().mapToInt(Integer::intValue).sum());
        total.setTotalOldUserNumber(oldUserIds.size());
        total.setTotalNewUserNumber(analysisVo.getNewUserNumber().stream().mapToInt(Integer::intValue).sum());
        analysisVo.setTotal(total);
        return analysisVo;
    }

    /**
     * 活动效果要分析的订单数据
     *
     * @param param
     * @return
     */
    private Map<Date, List<MrkingStrategyOrderAnalysisBo>> getFullCutOrderDiscountMoney(MrkingStrategyAnalysisParam param) {
        List<MrkingStrategyOrderAnalysisBo> list = db().select(DSL.date(ORDER_INFO.CREATE_TIME).as("date"), ORDER_INFO.CREATE_TIME, ORDER_INFO.ORDER_SN, ORDER_INFO.USER_ID, ORDER_GOODS.PER_DISCOUNT, ORDER_GOODS.DISCOUNTED_TOTAL_PRICE, ORDER_GOODS.GOODS_NUMBER)
            .from(ORDER_INFO).leftJoin(ORDER_GOODS).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
            .where(ORDER_GOODS.STRA_ID.eq(param.getId()))
            .and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(param.getStartTime(), param.getEndTime()))
            .fetchInto(MrkingStrategyOrderAnalysisBo.class);
        Map<Date, List<MrkingStrategyOrderAnalysisBo>> map = list.stream().collect(Collectors.groupingBy(MrkingStrategyOrderAnalysisBo::getDate));
        return map;
    }

}
