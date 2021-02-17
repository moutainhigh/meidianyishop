package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.GradePrdRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.member.bo.UserCardGradePriceBo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.GoodsActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.OrderCartProductBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade.GradeCardMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade.GradePrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade.GradeReduceMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade.GradeReducePrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.reduce.ReducePriceMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.reduce.ReducePricePrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.activity.dao.MemberCardProcessorDao;
import com.meidianyi.shop.service.shop.member.UserCardService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record2;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_MEMBER_GRADE;
import static com.meidianyi.shop.db.shop.Tables.GRADE_PRD;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;

/**
 * 等级价格
 *
 * @author 李晓冰
 * @date 2019年10月31日
 */
@Service
@Slf4j
public class GradeCardProcessor implements Processor, ActivityGoodsListProcessor, GoodsDetailProcessor, ActivityCartListStrategy, CreateOrderProcessor {

    @Autowired
    MemberCardProcessorDao memberCardProcessorDao;
    @Autowired
    private UserCardService userCardService;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_MEMBER_GRADE_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return ACTIVITY_TYPE_MEMBER_GRADE;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        List<GoodsListMpBo> availableCapsules = capsules.stream().filter(x -> !GoodsConstant.isGoodsTypeIn13510(x.getActivityType()) && !x.getProcessedTypes().contains(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL))
            .collect(Collectors.toList());
        List<Integer> goodsIds = availableCapsules.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());

        Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> goodsGradeInfos = memberCardProcessorDao.getGoodsGradeCardForListInfo(userId, goodsIds);

        availableCapsules.forEach(capsule -> {
            Integer goodsId = capsule.getGoodsId();
            if (goodsGradeInfos.get(goodsId) == null) {
                return;
            }
            Record3<Integer, Integer, BigDecimal> record3 = goodsGradeInfos.get(goodsId).get(0);

            // 已被限时降价处理（被限时降价处理则不可能是会员专享）
            if (capsule.getProcessedTypes().contains(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)) {
                // 会员价比限时降价价格低则将限时降价的处理信息删除
                if (record3.get(GRADE_PRD.GRADE_PRICE).compareTo(capsule.getRealPrice()) < 0) {
                    capsule.getProcessedTypes().remove(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE);
                    List<GoodsActivityBaseMp> activities = capsule.getGoodsActivities().stream().filter(x -> !BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(x.getActivityType())).collect(Collectors.toList());
                    capsule.setGoodsActivities(activities);
                } else {// 没有限时降价的价格低,则按照直接返回不加入会员价信息
                    return;
                }
            }

            // 不存在限时降价，或者会员价格比限时降价低则加入会员价活动信息
            capsule.setRealPrice(record3.get(GRADE_PRD.GRADE_PRICE));
            // 如果商品是会员专享的话则价格显示会员价的价格，但是提示信息显示会员专享，就不会执行下面if内语句
            if (!capsule.getProcessedTypes().contains(BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE)) {
                GoodsActivityBaseMp activity = new GoodsActivityBaseMp();
                activity.setActivityType(ACTIVITY_TYPE_MEMBER_GRADE);
                capsule.getGoodsActivities().add(activity);
            }
            capsule.getProcessedTypes().add(ACTIVITY_TYPE_MEMBER_GRADE);
        });
    }

    /*****************商品详情处理******************/
    /**
     * 不考虑拼砍秒预售首单
     *
     * @param capsule 商品详情对象{@link com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo}
     * @param param
     */
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {

        // 被其它活动处理过，但不是限时降价
        if (capsule.getActivity() != null && !BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(capsule.getActivity().getActivityType())) {
            return;
        }
        // 获取用户等级卡
        Record2<Integer, String> userGradeCard = memberCardProcessorDao.getUserGradeCard(param.getUserId());
        // 没有等级卡，直接退出
        if (userGradeCard == null) {
            return;
        }

        List<GradePrdMpVo> gradePrdMpVos = getGradePrdMpVos(capsule, param, userGradeCard);


        // 用户所拥有的等级卡在本商品上没有对应的等级会员价
        if (gradePrdMpVos == null || gradePrdMpVos.size() == 0) {
            return;
        }
        // 等级卡对应的规格数据和商品的规格数据应该一直一致，除非商品修改部分有bug,此处不用过滤有效规格

        // 没有限时降价
        if (capsule.getActivity() == null) {
            GradeCardMpVo vo = new GradeCardMpVo();
            vo.setActState(BaseConstant.ACTIVITY_STATUS_CAN_USE);
            vo.setActivityType(BaseConstant.ACTIVITY_TYPE_MEMBER_GRADE);
            vo.setGradePrdMpVos(gradePrdMpVos);
            capsule.setActivity(vo);
        } else {
            // 限时降价和会员价共存
            ReducePriceMpVo reduceMpVo = (ReducePriceMpVo) capsule.getActivity();
            Map<Integer, ReducePricePrdMpVo> reducePrdMap = reduceMpVo.getReducePricePrdMpVos().stream().collect(Collectors.toMap(ReducePricePrdMpVo::getProductId, Function.identity()));
            List<GradeReducePrdMpVo> gradeReducePrdMpVos = new ArrayList<>(reducePrdMap.size());

            gradePrdMpVos.forEach(gradePrd -> {
                GradeReducePrdMpVo prdMpVo = new GradeReducePrdMpVo();
                prdMpVo.setProductId(gradePrd.getProductId());
                prdMpVo.setPrdPrice(gradePrd.getPrdPrice());
                // reducePricePrdMpVo可能为null,创建限时降价后商品的规格信息可能又被修改
                // 如果为null，则价格按照会员价设置
                ReducePricePrdMpVo reducePricePrdMpVo = reducePrdMap.get(gradePrd.getProductId());
                if (reducePricePrdMpVo != null && reducePricePrdMpVo.getReducePrice().compareTo(gradePrd.getGradePrice()) <= 0) {
                    prdMpVo.setIsGradePrice(false);
                    prdMpVo.setActivityPrice(reducePricePrdMpVo.getReducePrice());
                } else {
                    prdMpVo.setIsGradePrice(true);
                    prdMpVo.setActivityPrice(gradePrd.getGradePrice());
                }
                prdMpVo.setReducePrice(reducePricePrdMpVo == null ? BigDecimal.valueOf(Double.MAX_VALUE) : reducePricePrdMpVo.getReducePrice());
                gradeReducePrdMpVos.add(prdMpVo);
            });

            GradeReduceMpVo gradeReduceMpVo = new GradeReduceMpVo();
            if (capsule.getActivity() != null) {
                gradeReduceMpVo.setActivityId(capsule.getActivity().getActivityId());
            }
            gradeReduceMpVo.setActivityType(BaseConstant.ACTIVITY_TYPE_GRADE_REDUCE_PRICE);
            gradeReduceMpVo.setActState(reduceMpVo.getActState());
            gradeReduceMpVo.setNextStartTimestamp(reduceMpVo.getNextStartTimestamp());
            gradeReduceMpVo.setCurrentEndTimestamp(reduceMpVo.getCurrentEndTimestamp());
            gradeReduceMpVo.setIsLimit(reduceMpVo.getIsLimit());
            gradeReduceMpVo.setLimitAmount(reduceMpVo.getLimitAmount());
            gradeReduceMpVo.setLimitFlag(reduceMpVo.getLimitFlag());
            gradeReduceMpVo.setGradeReducePrdVos(gradeReducePrdMpVos);
            capsule.setActivity(gradeReduceMpVo);
        }
    }

    /**
     * 处理等级
     *
     * @param capsule
     * @param param
     * @param userGradeCard
     * @return
     */
    private List<GradePrdMpVo> getGradePrdMpVos(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param, Record2<Integer, String> userGradeCard) {
        // 用户等级
        String grade = userGradeCard.get(MEMBER_CARD.GRADE);

        // 处理商品对应的等级价
        List<GradePrdMpVo> gradePrdMpVos = null;
        Map<Integer, GoodsPrdMpVo> prdMap = capsule.getProducts().stream().collect(Collectors.toMap(GoodsPrdMpVo::getPrdId, Function.identity()));
        List<GoodsDetailMpBo.GradePrd> gradeCards = capsule.getGradeCardPrice();
        if (!capsule.getIsDisposedByEs()) {
            log.debug("小程序-会员价格查询");
            List<GradePrdRecord> goodsGradeGradePrice = memberCardProcessorDao.getGoodsGradeGradePrice(param.getUserId(), param.getGoodsId());
            gradePrdMpVos = goodsGradeGradePrice.stream().map(x -> {
                GradePrdMpVo prd = new GradePrdMpVo();
                prd.setProductId(x.getPrdId());
                prd.setGradePrice(x.getGradePrice());
                prd.setPrdPrice(prdMap.get(x.getPrdId()).getPrdRealPrice());
                return prd;
            }).collect(Collectors.toList());
        } else {
            // 过滤符合用户等级的规格，es获取的是所有等级的价格，而不是针对用户等级的
            gradePrdMpVos = gradeCards.stream().map(x -> {
                GradePrdMpVo prd = new GradePrdMpVo();
                prd.setGradePrice(x.getGradePrice());
                prd.setProductId(x.getPrdId());
                prd.setPrdPrice(prdMap.get(x.getPrdId()).getPrdRealPrice());
                return prd;
            }).collect(Collectors.toList());
        }
        return gradePrdMpVos;
    }

    //*****************购物车处理************************

    /**
     * 会员等级价
     *
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        log.info("购物车-会员价计算-开始");
        String grade = userCardService.getUserGrade(cartBo.getUserId());
        if (grade.equals(CardConstant.LOWEST_GRADE)) {
            return;
        }
        List<UserCardGradePriceBo> userCartGradePrice = userCardService.getUserCartGradePrice(grade, cartBo.getProductIdList());
        if (userCartGradePrice != null && userCartGradePrice.size() > 0) {
            Map<Integer, List<UserCardGradePriceBo>> gradePriceMap = userCartGradePrice.stream().collect(Collectors.groupingBy(UserCardGradePriceBo::getPrdId));
            cartBo.getCartGoodsList().stream().filter(goods -> goods.getBuyStatus().equals(BaseConstant.YES) && goods.getPriceStatus().equals(BaseConstant.NO)).forEach(goods -> {
                // 会员等级
                if (gradePriceMap.containsKey(goods.getProductId())) {
                    UserCardGradePriceBo gradePriceBo = gradePriceMap.get(goods.getProductId()).get(0);
                    log.info("购物车-会员价-商品{}-商品原价{},会员价价格{}", goods.getGoodsName(), goods.getPrdPrice(), gradePriceBo.getGradePrice());
                    if (gradePriceBo.getGradePrice().compareTo(goods.getPrdPrice()) < 0) {
                        CartActivityInfo gradePriceInfo = new CartActivityInfo();
                        gradePriceInfo.setActivityType(ACTIVITY_TYPE_MEMBER_GRADE);
                        gradePriceInfo.setProductPrice(gradePriceBo.getGradePrice());
                        log.info("购物车-会员价-修改价格{}", gradePriceBo.getGradePrice());
                        goods.getCartActivityInfos().add(gradePriceInfo);
                        goods.setPrdPrice(gradePriceBo.getGradePrice());
                        goods.setPriceActivityType(ACTIVITY_TYPE_MEMBER_GRADE);
                    }
                }
            });
        }
        log.info("购物车-会员价计算-结束");
    }

    public void doOrderOperation(OrderCartProductBo productBo) {
        log.info("会员价计算start");
        String grade = userCardService.getUserGrade(productBo.getUserId());
        if (grade.equals(CardConstant.LOWEST_GRADE)) {
            return;
        }
        List<UserCardGradePriceBo> userCartGradePrice = userCardService.getUserCartGradePrice(grade, productBo.getProductIds());
        productBo.getAll().forEach(goods -> {
            // 会员等级
            userCartGradePrice.forEach(gradePrice -> {
                if (goods.getProductId().equals(gradePrice.getPrdId())) {
                    log.info("规格：{},会员价 ：{}", gradePrice.getPrdId(), gradePrice.getGradePrice());
                    GoodsActivityInfo goodsActivityInfo = new GoodsActivityInfo();
                    goodsActivityInfo.setActivityType(ACTIVITY_TYPE_MEMBER_GRADE);
                    goodsActivityInfo.setMemberPrice(gradePrice.getGradePrice());
                    goods.getActivityInfo().put(ACTIVITY_TYPE_MEMBER_GRADE, goodsActivityInfo);
                }
            });
        });
        log.info("会员价计算end");
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        doOrderOperation(param.createOrderCartProductBo());
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }


    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {

    }
}
