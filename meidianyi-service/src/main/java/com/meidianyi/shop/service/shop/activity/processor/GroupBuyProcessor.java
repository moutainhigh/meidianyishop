package com.meidianyi.shop.service.shop.activity.processor;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyListRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyProductDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupOrderVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagSrcConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy.GroupBuyMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy.GroupBuyPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GroupBuyListMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.activity.dao.GroupBuyProcessorDao;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyListService;
import com.meidianyi.shop.service.shop.member.TagService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GROUP_BUY_LIST;
import static com.meidianyi.shop.db.shop.Tables.GROUP_BUY_PRODUCT_DEFINE;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_CHEAP_Y;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_N;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_Y;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_ONGOING;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_WAIT_PAY;

/**
 * 商品列表,下单
 *
 * @author 李晓冰
 * @date 2019年10月29日
 */
@Slf4j
@Service
public class GroupBuyProcessor extends ShopBaseService implements Processor, GoodsDetailProcessor, ActivityGoodsListProcessor, CreateOrderProcessor {

    @Autowired
    GroupBuyProcessorDao groupBuyProcessorDao;
    @Autowired
    GroupBuyListService groupBuyListService;
    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private TagService tagService;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_GROUP_BUY_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_GROUP_BUY;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> bos, Integer userId) {
        List<GoodsListMpBo> availableBos = bos.stream().filter(x -> BaseConstant.ACTIVITY_TYPE_GROUP_BUY.equals(x.getActivityType())).collect(Collectors.toList());
        List<Integer> goodsIds = availableBos.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());
        Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> goodsGroupBuyListInfo = groupBuyProcessorDao.getGoodsGroupBuyListInfo(goodsIds, DateUtils.getLocalDateTime());

        availableBos.forEach(bo -> {
            if (goodsGroupBuyListInfo.get(bo.getGoodsId()) == null) {
                return;
            }
            Record3<Integer, Integer, BigDecimal> record3 = goodsGroupBuyListInfo.get(bo.getGoodsId()).get(0);
            bo.setRealPrice(record3.get(GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE));
            GroupBuyListMpVo activity = new GroupBuyListMpVo();

            activity.setActivityId(record3.get(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID));
            activity.setActivityType(BaseConstant.ACTIVITY_TYPE_GROUP_BUY);
            activity.setDiscountPrice(bo.getShopPrice().subtract(bo.getRealPrice()));
            bo.getGoodsActivities().add(activity);
            bo.getProcessedTypes().add(BaseConstant.ACTIVITY_TYPE_GROUP_BUY);
        });
    }

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {

        if (param.getActivityType() == null && capsule.getActivityAnnounceMpVo() == null) {
            // 探测是否需要进行活动预告
            capsule.setActivityAnnounceMpVo(groupBuyProcessorDao.getAnnounceInfo(capsule.getGoodsId(), DateUtils.getLocalDateTime()));
            return;
        }
        if (param.getActivityId() == null || !BaseConstant.ACTIVITY_TYPE_GROUP_BUY.equals(param.getActivityType())) {
            return;
        }
        log.debug("小程序-商品详情-拼团信息获取开始");
        GroupBuyMpVo groupBuyInfo = groupBuyProcessorDao.getGroupBuyInfo(param.getUserId(), param.getActivityId());

        if (BaseConstant.ACTIVITY_STATUS_NOT_HAS.equals(groupBuyInfo.getActState())) {
            capsule.setActivity(groupBuyInfo);
            log.debug("小程序-商品详情-拼团信息获取失败-拼团活动不存在[{}]-详情处理退出", param.getActivityId());
            return;
        }
        if (param.getUserId() != null && orderInfoService.isNewUser(param.getUserId(), true)) {
            groupBuyInfo.setIsNewUser(true);
        } else {
            groupBuyInfo.setIsNewUser(false);
        }


        Map<Integer, GoodsPrdMpVo> prdMap = capsule.getProducts().stream().collect(Collectors.toMap(GoodsPrdMpVo::getPrdId, x -> x));

        log.debug("小程序-商品详情-拼团规格信息获取开始");
        List<GroupBuyPrdMpVo> groupBuyPrdInfos = groupBuyProcessorDao.getGroupBuyPrdInfo(param.getActivityId(), prdMap.keySet());
        groupBuyInfo.setGroupBuyPrdMpVos(groupBuyPrdInfos);

        List<GroupBuyPrdMpVo> newGroupPrds = new ArrayList<>();
        int stock = 0;
        for (int i = 0; i < groupBuyPrdInfos.size(); i++) {
            // 商品拼团规格
            GroupBuyPrdMpVo vo = groupBuyPrdInfos.get(i);
            //商品原规格
            GoodsPrdMpVo goodsPrdMpVo = prdMap.get(vo.getProductId());

            if (goodsPrdMpVo == null) {
                continue;
            }

            // 设置拼团规格对应的原价，便于前端使用
            vo.setPrdPrice(goodsPrdMpVo.getPrdRealPrice());
            // 处理商品数量不足情况
            if (goodsPrdMpVo.getPrdNumber() < vo.getStock()) {
                vo.setStock(goodsPrdMpVo.getPrdNumber());
            }
            stock += vo.getStock();
            newGroupPrds.add(vo);
        }

        // 重新设置有效规格
        groupBuyInfo.setGroupBuyPrdMpVos(newGroupPrds);
        groupBuyInfo.setStock(stock);

        if (stock == 0 && BaseConstant.needToConsiderNotHasNum(groupBuyInfo.getActState())) {
            log.debug("小程序-商品详情-拼团商品数量已用完");
            groupBuyInfo.setActState(BaseConstant.ACTIVITY_STATUS_NOT_HAS_NUM);
        }
        if (newGroupPrds.size() == 0) {
            log.debug("小程序-商品详情-拼团-商品规格信息和活动规格信息无交集");
            groupBuyInfo.setActState(BaseConstant.ACTIVITY_STATUS_NO_PRD_TO_USE);
        }

        capsule.setActivity(groupBuyInfo);
    }


    //*********** 下单 *****************

    /**
     * 下单 初始化参数 校验
     *
     * @param param OrderBeforeParam
     */
    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        //不允许使用货到付款
        if (param.getPaymentList() != null) {
            param.getPaymentList().remove(OrderConstant.PAY_CODE_COD);
        }
        //拼团不使用优惠券和会员卡
        param.setMemberCardNo(StringUtils.EMPTY);
        param.setCouponSn(StringUtils.EMPTY);
        //不允许使用积分支付和货到付款
        if (param.getPaymentList() != null) {
            param.getPaymentList().remove(OrderConstant.PAY_CODE_SCORE_PAY);
            param.getPaymentList().remove(OrderConstant.PAY_CODE_COD);
        }
        //团长,团id
        Byte isGrouper = param.getGroupId() == null ? IS_GROUPER_Y : IS_GROUPER_N;
        log.debug("拼团订单");
        if (isGrouper.equals(IS_GROUPER_Y)) {
            param.setIsGrouper(IS_GROUPER_Y);
        } else {
            param.setIsGrouper(IS_GROUPER_N);
        }
        log.info("团长-IsGrouper:{}-groupId:{}", param.getIsGrouper(), param.getGroupId());
        //校验活动
        ResultMessage resultMessage = groupBuyListService.canCreatePinGroupOrder(param.getWxUserInfo().getUserId(), param.getDate(), param.getActivityId(), param.getGroupId(), isGrouper);
        if (!resultMessage.getFlag()) {
            throw new MpException(resultMessage.getJsonResultCode(), null, resultMessage.getMessages().toArray(new String[0]));
        }
        GroupBuyDefineRecord groupBuyRecord = groupBuyProcessorDao.getGroupBuyRecord(param.getActivityId());
        if (groupBuyRecord.getShippingType().equals(OrderConstant.YES)) {
            param.setIsFreeShippingAct(OrderConstant.YES);
        }
        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            //拼团规格库存校验
            GroupBuyProductDefineRecord groupBuyProduct = groupBuyProcessorDao.getGroupBuyProduct(param.getActivityId(), goods.getProductId());
            if (goods.getGoodsNumber() > groupBuyProduct.getStock()) {
                throw new MpException(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_JOIN_LIMIT_MAX);
            }
            //团员价格
            goods.setProductPrice(groupBuyProduct.getGroupPrice());
            goods.setGoodsPriceAction(param.getActivityType());
            // 团长优惠价
            if (groupBuyRecord.getIsGrouperCheap().equals(IS_GROUPER_CHEAP_Y) && isGrouper.equals(IS_GROUPER_Y)) {
                // (拼团价-团长价)*数量
                goods.setGrouperTotalReduce(groupBuyProduct.getGroupPrice().subtract(groupBuyProduct.getGrouperPrice()).multiply(BigDecimal.valueOf(goods.getGoodsNumber())));
                //拼团价-团长价
                goods.setGrouperGoodsReduce(groupBuyProduct.getGroupPrice().subtract(groupBuyProduct.getGrouperPrice()));
            }
            //商品展示价格(团员价格)
            goods.setGoodsPrice(groupBuyProduct.getGroupPrice());
        }
        if(groupBuyRecord.getActivityTag().equals(BaseConstant.YES) && StringUtil.isNotBlank(groupBuyRecord.getActivityTagId())){
            tagService.userTagSvc.addActivityTag(param.getWxUserInfo().getUserId(), Util.stringToList(groupBuyRecord.getActivityTagId()), TagSrcConstant.GROUPBUY,groupBuyRecord.getId());
        }
    }

    /**
     * 保存订单
     *
     * @param param OrderBeforeParam
     * @param order OrderInfoRecord
     * @throws MpException
     */
    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            GroupBuyListRecord groupBuyProductList = db().newRecord(GROUP_BUY_LIST);
            groupBuyProductList.setActivityId(param.getActivityId());
            groupBuyProductList.setGoodsId(goods.getGoodsId());
            groupBuyProductList.setGroupId(param.getGroupId() == null ? 0 : param.getGroupId());
            groupBuyProductList.setOrderSn(order.getOrderSn());
            groupBuyProductList.setUserId(param.getWxUserInfo().getUserId());
            groupBuyProductList.setIsGrouper(param.getIsGrouper());
            groupBuyProductList.setStartTime(param.getDate());
            if (order.getOrderStatus() >= OrderConstant.ORDER_WAIT_DELIVERY) {
                groupBuyProductList.setStatus(STATUS_ONGOING);
            } else {
                groupBuyProductList.setStatus(STATUS_WAIT_PAY);
            }
            int save = groupBuyProductList.insert();
            if (save != 1) {
                throw new MpException(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_JOIN_LIMIT_MAX);
            }
            groupBuyProductList.refresh();
            log.debug("开团成功,团长useri:d{},团groupId:{}", groupBuyProductList.getUserId(), groupBuyProductList.getId());
            if (groupBuyProductList.getIsGrouper().equals(IS_GROUPER_Y)) {
                groupBuyProductList.setGroupId(groupBuyProductList.getId());
                groupBuyProductList.update();
            }
        }
    }

    /**
     * 修改状态
     *
     * @param param
     * @param order
     * @throws MpException
     */
    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        log.info("拼团订单--(拼团中)已付款");
        List<OrderGoodsBo> orderGoodsBos = orderGoodsService.getByOrderId(order.getOrderId()).into(OrderGoodsBo.class);
        ArrayList<String> goodsTypes = Lists.newArrayList(OrderInfoService.orderTypeToArray(order.getGoodsType()));
        if (goodsTypes.contains(String.valueOf(BaseConstant.ACTIVITY_TYPE_GROUP_BUY))) {
            GroupOrderVo byOrder = groupBuyListService.getByOrder(order.getOrderSn());
            if (byOrder.getStatus().equals(STATUS_WAIT_PAY)){
                log.info("修改拼团这状态");
                groupBuyProcessorDao.updateGroupSuccess(byOrder.getGroupId(), DateUtils.getLocalDateTime(),byOrder.getOrderSn());
            }
            groupBuyProcessorDao.groupBuySuccess(order.getActivityId(), byOrder.getGroupId(), orderGoodsBos.get(0).getGoodsName());
        }
    }

    /**
     * 修改库存
     *
     * @param param
     * @param order
     * @throws MpException
     */
    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            boolean b = groupBuyProcessorDao.updateGroupBuyStock(param.getActivityId(), goods.getProductId(), goods.getGoodsNumber());
            if (!b) {
                log.error("拼团改库存失败,商品名称:{}",goods.getGoodsInfo().getGoodsName());
                throw new MpException(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_STOCK_LIMIT);
            }
        }
    }

    /**
     * 退款
     *
     * @param returnOrderRecord
     * @param activityId  活动id
     * @param returnGoods 退款商品
     */
    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {
        log.info("拼团退款-退库存");
        for (OrderReturnGoodsVo returnGoodsVo : returnGoods) {
            boolean b = groupBuyProcessorDao.updateGroupBuyStock(activityId, returnGoodsVo.getProductId(), -returnGoodsVo.getGoodsNumber());
            if (!b) {
                log.error("拼团退款,退库存-失败");
                throw new MpException(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_INVENTORY_FAILED);
            }
        }
    }
}
