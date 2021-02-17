package com.meidianyi.shop.service.shop.market.freeshipping;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.FreeShippingRecord;
import com.meidianyi.shop.db.shop.tables.records.FreeShippingRuleRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.freeshipping.FreeShippingConstant;
import com.meidianyi.shop.service.pojo.shop.market.freeshipping.FreeShippingRuleInfoVo;
import com.meidianyi.shop.service.pojo.shop.market.freeshipping.FreeShippingRuleVo;
import com.meidianyi.shop.service.pojo.shop.market.freeshipping.FreeShippingVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.GoodsSearchParam;
import com.meidianyi.shop.service.pojo.wxapp.market.freeshipping.FreeShipGoodsSearchVo;
import com.meidianyi.shop.service.pojo.wxapp.market.freeshipping.FreeShippingGoodsListParam;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import org.jooq.Condition;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.GOODS_AREA_TYPE_SECTION;

import java.util.ArrayList;
import java.util.List;

/**
 * 满包邮商品列表业务
 * @author 孔德成
 * @date 2019/12/12 16:00
 */
@Service
public class FreeShippingGoodsService extends ShopBaseService {
    private static final String MESSAGE = "messages";

    @Autowired
    private FreeShippingRuleService freeShipRuleService;
    @Autowired
    private FreeShippingService freeShipService;
    @Autowired
    private GoodsMpService goodsMpService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CartService cartService;
    /**
     * 满包邮商品列表
     * @param param
     * @return
     */
    public FreeShipGoodsSearchVo freeShipGoodsList(FreeShippingGoodsListParam param,String lang){
        param.initScene();
        /*
         * 活动查询
         */
        // 获取活动规则
        FreeShippingRecord freeShip = freeShipService.getFreeShippingByRuleId(param.getRuleId());
        if (freeShip==null){
            return null;
        }
        //包邮规则
        Result<FreeShippingRuleRecord> freeShippingRules = freeShipRuleService.getFreeShippingRule(freeShip.getId());
        FreeShippingRuleRecord freeShippingRule = freeShipRuleService.getRuleByRuleId(param.getRuleId());
        //包邮规则信息化
        FreeShippingRuleInfoVo ruleInfoVo = ruleToTextInfo(freeShip, freeShippingRule,lang);
        //查询参数
        GoodsSearchParam goodsSearchParam = handleSearchParam(param, freeShip);
        //查询商品
        PageResult<GoodsListMpBo> goodsListNormal = goodsMpService.getGoodsListNormal(goodsSearchParam);
        //添加营销活动
        goodsMpService.disposeGoodsList(goodsListNormal.dataList, param.getUserId());
        //是否显示划线价开关
        Byte delMarket = configService.shopCommonConfigService.getDelMarket();
        //是否显示购买按钮
        ShowCartConfig showCart = configService.shopCommonConfigService.getShowCart();
        showCart.setShowCart((byte) 1);
        FreeShipGoodsSearchVo vo =new FreeShipGoodsSearchVo();
        FreeShippingVo freeShippingVo = freeShip.into(FreeShippingVo.class);
        freeShippingVo.setRuleList(freeShippingRules.into(FreeShippingRuleVo.class));
        vo.setDelMarket(delMarket);
        vo.setShowCart(showCart);
        vo.setPageResult(goodsListNormal);
        vo.setFreeShipping(freeShippingVo);
        vo.setFreeShippingRule(ruleInfoVo);
        return vo;
    }

    /**
     * 包邮规则信息化
     * @param freeShip
     * @param freeShipRule
     * @return
     */
    private FreeShippingRuleInfoVo ruleToTextInfo(FreeShippingRecord freeShip, FreeShippingRuleRecord freeShipRule,String lang) {
        FreeShippingRuleInfoVo ruleInfoVo = new FreeShippingRuleInfoVo();
        //活动名称
        ruleInfoVo.setActivityName(freeShip.getName());
        //活动时间 0:固定日期 1：永久有效
        if (FreeShippingConstant.FREE_SHIPPING_EXPIRE_FIXED.equals(freeShip.getExpireType())){
            ruleInfoVo.setExpire(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_EXPIRE_FIXED, MESSAGE,
                    new Object[]{freeShip.getStartTime().toString().substring(0,19),freeShip.getEndTime().toString().substring(0,19)}));
        }else {
            ruleInfoVo.setExpire(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_EXPIRE_NEVER, MESSAGE));
        }
        //活动商品 条件 1全部 0部分
        if (FreeShippingConstant.FREE_SHIPPING_GOODS_ALL.equals(freeShip.getType())){
            ruleInfoVo.setGoodsAreaInfo(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_GOODS_ALL, MESSAGE));
        }else {
            ruleInfoVo.setGoodsAreaInfo(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_GOODS_PART, MESSAGE));
        }
        //包邮规则 包邮条件 0满金额 1满件数
        if (freeShipRule.getConType().equals(FreeShippingConstant.FREE_SHIPPING_CONDITION_ACCOUNT.intValue())){
            ruleInfoVo.setRuleCondition(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_CONDITION_ACCOUNT, MESSAGE,
                    new Object[]{freeShipRule.getMoney().toString()}));
        }else if (freeShipRule.getConType().equals(FreeShippingConstant.FREE_SHIPPING_CONDITION_NUM.intValue())){
            ruleInfoVo.setRuleCondition(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_CONDITION_NUM, MESSAGE,
                    new Object[]{freeShipRule.getNum().toString()}));
        }else {
            ruleInfoVo.setRuleCondition(Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_CONDITION_ACCOUNT_OR_NUM, MESSAGE
            ,new Object[]{freeShipRule.getMoney().toString(),freeShipRule.getNum().toString()}));
        }
        //包邮区域
        List<Integer> regionIds = Util.splitValueToList(freeShipRule.getArea());
        List<String> regionNames = saas.region.getCityDistrictProvinceName(regionIds);
        ruleInfoVo.setAreaText(regionNames);
        //ruleText
        String ruleText =Util.translateMessage(lang, JsonResultMessage.FREE_SHIPPING_ACTIVITY_MESSAGE_RULE_TEXT, MESSAGE)+ruleInfoVo.getGoodsAreaInfo()+ruleInfoVo.getRuleCondition();
        ruleInfoVo.setRuleText(ruleText);
        return ruleInfoVo;
    }

    private GoodsSearchParam handleSearchParam(FreeShippingGoodsListParam param, FreeShippingRecord freeShip) {
        //活动商品范围
        List<Integer> goodsIds =null;
        List<Integer> catIds =null;
        List<Integer> sortIds =null;
        if (freeShip.getType().equals(GOODS_AREA_TYPE_SECTION)){
            if (!freeShip.getRecommendCatId().trim().isEmpty()){
                catIds = new ArrayList<>(Util.splitValueToList(freeShip.getRecommendCatId()));
            }
            if (!freeShip.getRecommendSortId().trim().isEmpty()){
                sortIds = new ArrayList<>(Util.splitValueToList(freeShip.getRecommendSortId()));
            }
            if (!freeShip.getRecommendGoodsId().trim().isEmpty()){
                goodsIds = new ArrayList<>(Util.splitValueToList(freeShip.getRecommendGoodsId()));
            }
        }
        //售罄是否显示
        Boolean soldOutGoods = GoodsConstant.SOLD_OUT_GOODS_SHOW.equals(configService.shopCommonConfigService.getSoldOutGoods());
        //获取售罄商品展示设置
        GoodsSearchParam goodsSearchParam  =new GoodsSearchParam();
        goodsSearchParam.setCurrentPage(param.getCurrentPage());
        goodsSearchParam.setPageRows(param.getPageRows());
        goodsSearchParam.setCatIds(catIds);
        goodsSearchParam.setGoodsIds(goodsIds);
        goodsSearchParam.setSortIds(sortIds);
        goodsSearchParam.setGoodsAreaType(freeShip.getType());
        goodsSearchParam.setGoodsName(param.getSearchText());
        goodsSearchParam.setUserId(param.getUserId());
        goodsSearchParam.setShowSoldOut(soldOutGoods);
        return goodsSearchParam;
    }

    /**
     * 获取购物车满包邮商品
     * @param userId
     * @param ruleId
     * @return
     */
    public WxAppCartBo getCartGoodsList(Integer userId, Integer ruleId) {
        FreeShippingRecord freeShip = freeShipService.getFreeShippingByRuleId(ruleId);
        if (freeShip!=null){
            //活动商品范围
            FreeShippingGoodsListParam param =new FreeShippingGoodsListParam();
            //查询参数
            GoodsSearchParam goodsSearchParam = handleSearchParam(param, freeShip);
            //查询条件拼接
            Condition condition = goodsMpService.handleSearchCondition(goodsSearchParam);
            List<Integer> goodsIds = goodsMpService.getGoodsIdsByCondition(condition);
            return  cartService.getCartList(userId, goodsIds,null,null);
        }
        return  cartService.getCartList(userId, new ArrayList<>(),null,null);
    }
}
