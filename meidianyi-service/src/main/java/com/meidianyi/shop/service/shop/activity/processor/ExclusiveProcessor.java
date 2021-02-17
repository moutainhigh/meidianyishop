package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.MemberCardDetailMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import com.meidianyi.shop.service.shop.activity.dao.MemberCardProcessorDao;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant.ACTIVITY_STATUS_INVALID;
import static com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant.GOODS_STATUS_EXCLUSIVE;

/**
 *  会员专享
 * @author 李晓冰
 * @date 2019年10月31日
 */
@Service
@Slf4j
public class ExclusiveProcessor implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor,ActivityCartListStrategy,CreateOrderProcessor {
    @Autowired
    MemberCardProcessorDao memberCardProcessorDao;
    @Autowired
    UserCardService userCardService;
    @Autowired
    CartService cartService;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_CARD_EXCLUSIVE_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {

        List<Integer> goodsIdParams = new ArrayList<>();
        List<Integer> sortIdParams = new ArrayList<>();
        List<Integer> catIdParams = new ArrayList<>();
        List<Integer> brandIdParams = new ArrayList<>();
        capsules.forEach(capsule->{
            goodsIdParams.add(capsule.getGoodsId());
            sortIdParams.add(capsule.getSortId());
            catIdParams.add(capsule.getCatId());
            brandIdParams.add(capsule.getBrandId());
        });

        // 获取专享会员对应信息
        Map<Byte, List<Integer>> exclusiveInfo = memberCardProcessorDao.getExclusiveInfo(goodsIdParams,sortIdParams, catIdParams,brandIdParams);
        Set<Integer> goodsIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_GOODS));
        Set<Integer> catIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_PLAT));
        Set<Integer> sortIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_STORE));
        Set<Integer> brandIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_BRAND));

        capsules.forEach(capsule->{
            if (GoodsConstant.isGoodsTypeIn13510(capsule.getActivityType())) {
                return;
            }
            Integer goodsId = capsule.getGoodsId();
            Integer catId = capsule.getCatId();
            Integer sortId = capsule.getSortId();
            Integer brandId = capsule.getBrandId();

            if (goodsIds.contains(goodsId) || catIds.contains(catId) || sortIds.contains(sortId) || brandIds.contains(brandId)) {
                GoodsActivityBaseMp activity = new GoodsActivityBaseMp();
                activity.setActivityType(BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE);
                capsule.getGoodsActivities().add(activity);
                capsule.getProcessedTypes().add(BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE);
            }
        });

    }
    /*****************商品详情处理******************/

    /**
     * 商品是否是会员专享
     * @param capsule 商品
     * @return true是 false否
     */
    private boolean isExclusive(GoodsDetailMpBo capsule){
        Map<Byte, List<Integer>> exclusiveInfo =
            memberCardProcessorDao.getExclusiveInfo(Collections.singletonList(capsule.getGoodsId()),Collections.singletonList(capsule.getSortId()),
            Collections.singletonList(capsule.getCatId()),Collections.singletonList(capsule.getBrandId()));

        Set<Integer> goodsIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_GOODS));
        Set<Integer> catIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_PLAT));
        Set<Integer> sortIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_STORE));
        Set<Integer> brandIds = new HashSet<>(exclusiveInfo.get(CardConstant.COUPLE_TP_BRAND));
        if (goodsIds.contains(capsule.getGoodsId()) || catIds.contains(capsule.getCatId()) || sortIds.contains(capsule.getSortId()) || brandIds.contains(capsule.getBrandId())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理用户是否可以购买该商品
     * @param capsule  商品详情对象{@link com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo}
     * @param param
     */
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        //不是会员专享
        if (!isExclusive(capsule)) {
            capsule.setUserCanBuy(true);
            capsule.setMemberCards(new ArrayList<>());
            capsule.setIsExclusive(GoodsConstant.NOT_CARD_EXCLUSIVE);
            return;
        }
        capsule.setUserCanBuy(false);
        // 获取商品所有专享卡（包含普通卡和等级卡）
        log.debug("商品详情-会员专享卡查询");
        List<MemberCardRecord> exclusiveInfo = memberCardProcessorDao.getExclusiveInfo(param.getGoodsId(), param.getCatId(), param.getSortId(), param.getBrandId());
        Map<Byte, List<MemberCardRecord>> map = exclusiveInfo.stream().collect(Collectors.groupingBy(MemberCardRecord::getCardType));
        log.debug("商品详情-商品有效的专享卡ids:{}",exclusiveInfo.stream().map(x->x.get(MEMBER_CARD.ID)).collect(Collectors.toList()));
        // 普通卡
        List<MemberCardRecord> normalCards = map.getOrDefault(CardConstant.MCARD_TP_NORMAL, new ArrayList<>(0));
        // 等级卡
        List<MemberCardRecord> gradeCards = map.getOrDefault(CardConstant.MCARD_TP_GRADE, new ArrayList<>(0));

        // 获取当前用户拥有的所有会员卡信息
        List<UserCardRecord> userAllCard = memberCardProcessorDao.getUserAllCard(param.getUserId());
        log.debug("商品详情-用户拥有的会员卡ids:{}",userAllCard.stream().map(UserCardRecord::getCardId).collect(Collectors.toList()));
        // 获取用户等级
        Record2<Integer, String> userGrade = memberCardProcessorDao.getUserGradeCard(param.getUserId());
        // 最后的lambda是为了防止主键重复
        Map<Integer, Record> userAllCardMap = userAllCard.stream().collect(Collectors.toMap(x -> x.get(USER_CARD.CARD_ID), Function.identity(),(x1,x2)->x1));
        log.debug("商品详情-用户拥有的会员卡转map后ids:{}",userAllCardMap.keySet());
        Timestamp now = DateUtils.getLocalDateTime();

        List<MemberCardDetailMpVo> cardsLis = new ArrayList<>();
        // 判断用户和专享普通卡的状态关系
        normalCards.forEach(normalCard->{
            MemberCardDetailMpVo card =new MemberCardDetailMpVo(normalCard);
            Record record = userAllCardMap.get(normalCard.getId());
            if (record == null) {
                // 用户待领取
                card.setStatus(CardConstant.USER_CARD_STATUS_NOT_HAS);
            } else {
                // 是否需要激活
                Byte isNeedActive = normalCard.getActivation();
                // 激活时间
                Timestamp activeTime = record.get(USER_CARD.ACTIVATION_TIME);
                // 卡有效时间，为null表示永久型
                Timestamp expireTime = record.get(USER_CARD.EXPIRE_TIME);

                // 已激活或不需要激活
                boolean activatedOrNotNeed = (CardConstant.MCARD_ACT_NO.equals(isNeedActive) || (CardConstant.MCARD_ACT_YES.equals(isNeedActive) && activeTime != null));
                if (activatedOrNotNeed) {
                    if (expireTime == null || expireTime.compareTo(now) > 0) {
                        // 未过期可使用
                        card.setStatus(CardConstant.USER_CARD_STATUS_HAS);
                        capsule.setUserCanBuy(true);
                    } else {
                        // 已过期
                        card.setStatus(CardConstant.USER_CARD_STATUS_OUT_OF_EXPIRE);
                    }
                } else {
                    // 需要激活
                    card.setStatus(CardConstant.USER_CARD_STATUS_NEED_ACTVATION);
                }
            }
            cardsLis.add(card);
        });

        capsule.setMemberCards(cardsLis);
        capsule.setIsExclusive(GoodsConstant.CARD_EXCLUSIVE);
        if (gradeCards.size() == 0) {
            log.debug("商品详情-会员专享商品不存在等级卡");
            return;
        }
        if (userGrade == null) {
            log.debug("商品详情-会员专享商品用户不存在等级卡");
            capsule.setUserCanBuy(false);
            return;
        }
        MemberCardRecord minGradeCard = gradeCards.get(0);
        if (userGrade.get(MEMBER_CARD.GRADE).compareTo(minGradeCard.getGrade()) > 0) {
            capsule.setUserCanBuy(true);
        }
    }

    //**********************购物车********************************
    /**
     * 专享
     * @param cartBo 购物车业务数据
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        //会员卡绑定商品
        Set<Integer> userCardExclusive = userCardService.getUserCardExclusiveGoodsIds(cartBo.getUserId(), cartBo.getCartGoodsList());
        log.info("会员绑定的商品[userCardExclusive:{"+Util.toJson(userCardExclusive)+"}]");
        cartBo.getCartGoodsList().stream().filter(goods -> GoodsConstant.CARD_EXCLUSIVE.equals(goods.getGoodsRecord().getIsCardExclusive())).forEach(goods -> {
            // 会员专享商品
            if (!userCardExclusive.contains(goods.getGoodsId())) {
                //没有资格0
                log.info("购物车-会员没有资格的商品:[getGoodsName:"+goods.getGoodsName()+",getGoodsId"+goods.getGoodsId()+"]");
                log.info("购物车-专享商品-不可购买");
                CartActivityInfo exclusiveGrade = new CartActivityInfo();
                exclusiveGrade.setActivityType(BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE);
                exclusiveGrade.setStatus(ACTIVITY_STATUS_INVALID);
                goods.getCartActivityInfos().add(exclusiveGrade);
                goods.setGoodsStatus(GOODS_STATUS_EXCLUSIVE);
                goods.setIsChecked(CartConstant.CART_NO_CHECKED);
                goods.setBuyStatus(BaseConstant.NO);
                cartService.switchCheckedByProductId(cartBo.getUserId(), goods.getProductId(), CartConstant.CART_NO_CHECKED);
            } else {
                log.info("会员专享商品:[getGoodsName:"+goods.getGoodsName()+",getGoodsId"+goods.getGoodsId()+"]");
                CartActivityInfo exclusiveGrade = new CartActivityInfo();
                exclusiveGrade.setActivityType(BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE);
                goods.getCartActivityInfos().add(exclusiveGrade);
                goods.setGoodsStatus(GOODS_STATUS_EXCLUSIVE);
            }
        });
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        //会员可用会员卡
        List<OrderMemberVo> userCards = userCardService.userCardDao.getOrderMembers(param.getWxUserInfo().getUserId(), new Byte[]{CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE}, OrderConstant.MEMBER_CARD_ONLINE);
        //会员等级
        String lv = CardConstant.LOWEST_GRADE;
        //会员可用会员卡中等级最高的卡
        for (OrderMemberVo cardVo: userCards) {
            if(CardUtil.isGradeCard(cardVo.getInfo().getCardType())) {
                lv = cardVo.getInfo().getGrade();
                break;
            }
        }
        for (OrderBeforeParam.Goods one: param.getGoods()) {
            // 获取商品所有专享卡（包含普通卡和等级卡）
            List<MemberCardRecord> exclusiveCards = memberCardProcessorDao.getExclusiveInfo(one.getGoodsInfo().getGoodsId(), one.getGoodsInfo().getCatId(), one.getGoodsInfo().getSortId(), one.getGoodsInfo().getBrandId());
            if(CollectionUtils.isNotEmpty(exclusiveCards)) {
                //flag
                boolean flag = false;
                for (MemberCardRecord exclusiveCard: exclusiveCards) {
                    if(!CardUtil.isGradeCard(exclusiveCard.getCardType())) {
                        for(OrderMemberVo userCard : userCards) {
                            if(exclusiveCard.getId().equals(userCard.getInfo().getCardId())) {
                                flag = true;
                                one.setIsCardExclusive(OrderConstant.YES);
                                break;
                            }
                        }
                        if(flag) {
                            break;
                        }
                    } else if(lv.compareTo(exclusiveCard.getGrade()) >= 0){
                        flag = true;
                        one.setIsCardExclusive(OrderConstant.YES);
                        break;
                    }

                }
                if(!flag) {
                    log.info("专享商品不可购买,{}" ,one.getGoodsInfo().getGoodsName());
                    throw new MpException(JsonResultCode.CODE_ORDER_EXCLUSIVE_GOODS_NO_BUY, "专享商品不可购买", one.getGoodsInfo().getGoodsName());
                }

            }
        }
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //无
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //无
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //无
    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {

    }
}
