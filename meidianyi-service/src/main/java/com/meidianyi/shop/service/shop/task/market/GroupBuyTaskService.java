package com.meidianyi.shop.service.shop.task.market;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyListRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.bo.GroupBuyListScheduleBo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyDetailVo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyProductVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import jodd.util.StringUtil;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GroupBuyDefine.GROUP_BUY_DEFINE;
import static com.meidianyi.shop.db.shop.tables.GroupBuyList.GROUP_BUY_LIST;
import static com.meidianyi.shop.db.shop.tables.GroupBuyProductDefine.GROUP_BUY_PRODUCT_DEFINE;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_Y;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_DEFAULT_SUCCESS;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_FAILED;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_ONGOING;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_SUCCESS;
import static java.util.stream.Collectors.toList;

/**
 * 拼团的定时任务
 * @author: 王兵兵
 * @create: 2019-12-05 10:34
 **/
@Service
public class GroupBuyTaskService  extends ShopBaseService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;
    @Autowired
    private OrderGoodsService orderGoodsService;

    /**
     * 监控goodsType
     */
    public void monitorGoodsType(){
        //目前Goods表里还是拼团类型的商品
        List<Integer> pastGroupBuyGoodsIdList = getPastGroupBuyGoodsId();
        //在活动有效期内的拼团记录的ID列表
        List<Integer> currentGroupBuyGoodsIdList = getCurrentGroupBuyGoodsIdList();

        //求差集
        List<Integer> changeToNormalGoodsIds = Util.diffList(pastGroupBuyGoodsIdList,currentGroupBuyGoodsIdList);
        List<Integer> changeToActGoodsIds = Util.diffList(currentGroupBuyGoodsIdList,pastGroupBuyGoodsIdList);

        if(changeToNormalGoodsIds != null && changeToNormalGoodsIds.size() > 0){
            //活动已失效，将goodsType改回去
            goodsService.changeToNormalType(changeToNormalGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToNormalGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }

        if(changeToActGoodsIds != null && changeToActGoodsIds.size() > 0){
            //有新的活动生效，商品goodsType标记活动类型
            changeToGroupBuyType(changeToActGoodsIds);
            //刷新拼团库存
            updateGroupBuyProcudtStock(changeToActGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToActGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }

    }

    /**
     * 监控并处理拼团订单
     */
    public void monitorOrder(){
        //所有需要处理的团(状态是拼团中，但已超过24小时有效期)
        List<GroupBuyListScheduleBo> groups = getClosedGroup();
        groups.forEach(group -> {
            //下属的所有拼团中记录
            List<GroupBuyListRecord> listRecords = getGroupRecordsByGroupId(group.getGroupId());
            List<String> orderSnList = listRecords.stream().map(GroupBuyListRecord::getOrderSn).collect(toList());

            //先更改订单状态
            orderInfoService.batchChangeToWaitDeliver(orderSnList);

            if(GroupBuyService.IS_DEFAULT_Y.equals(group.getIsDefault())){
                //设置了默认成团，将生成虚拟的参团记录，并将已参团用户的订单置为待发货状态
                transaction(()->{
                    //randUserNum 需要随机生成的参团记录数
                    int randUserNum = group.getLimitAmount() - listRecords.size();
                    if(randUserNum > 0){
                        //生成虚拟的参团记录
                        insertRandGroupBuyList(group,randUserNum);
                    }
                    //更改参团状态
                    updateGroupBuyListStatus(group.getGroupId(),STATUS_DEFAULT_SUCCESS);
                });

                // TODO 同步订单状态到CRM
                // TODO 向用户发送拼团成功消息（小程序 || 公众号）
            }else{
                //未设置默认成团，拼团失败，将已参团用户的订单发起退款
                transaction(()->{
                    //更改参团状态
                    updateGroupBuyListStatus(group.getGroupId(),STATUS_FAILED);
                    //退款
                    refund(orderSnList);
                });
                if(StringUtil.isNotEmpty(group.getRewardCouponId())){
                    //拼团失败发放优惠券
                    CouponGiveQueueParam newParam = new CouponGiveQueueParam(
                        getShopId(), listRecords.stream().map(GroupBuyListRecord::getUserId).collect(toList()), group.getActivityId(), group.getRewardCouponId().split(","), BaseConstant.ACCESS_MODE_ISSUE, BaseConstant.GET_SOURCE_ACT);
                    //队列异步发放
                    saas.taskJobMainService.dispatchImmediately(newParam, CouponGiveQueueParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.GIVE_COUPON.getExecutionType());
                }

            }
        });

    }



    /**
     * 当前所有goodsType还是拼团的商品ID（某些商品可能已经过期，需要更新回普通商品）
     * @return
     */
    private List<Integer> getPastGroupBuyGoodsId(){
        return db().select(GOODS.GOODS_ID).from(GOODS).where(GOODS.GOODS_TYPE.eq(BaseConstant.ACTIVITY_TYPE_GROUP_BUY)).fetchInto(Integer.class);
    }

    /**
     * 当前有效、有库存的拼团活动下属的goodsId列表
     * @return
     */
    private List<Integer> getCurrentGroupBuyGoodsIdList(){
        List<Integer> res =  db().selectDistinct(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID)
                .from(GROUP_BUY_PRODUCT_DEFINE)
                .leftJoin(GROUP_BUY_DEFINE).on(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(GROUP_BUY_DEFINE.ID))
                .leftJoin(GOODS).on(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(
                GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
                .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(GROUP_BUY_DEFINE.START_TIME.lt(DateUtils.getLocalDateTime()))
                .and(GROUP_BUY_DEFINE.END_TIME.gt(DateUtils.getLocalDateTime()))
                .and(GROUP_BUY_DEFINE.STOCK.gt((short)0))
                .and(GOODS.GOODS_NUMBER.gt(0))
            ).fetchInto(Integer.class);
        return res;
    }

    /**
     * 批量将活动商品改成拼团商品
     * @param goodsIds
     */
    private void changeToGroupBuyType(List<Integer> goodsIds){
        //比拼团优先级高的活动，不覆盖goodsType是这些的商品（3、5、10）
        List<Byte> highPriorityAct = Stream.of(BaseConstant.ACTIVITY_TYPE_BARGAIN,BaseConstant.ACTIVITY_TYPE_SEC_KILL,BaseConstant.ACTIVITY_TYPE_PRE_SALE).collect(toList());
        db().update(GOODS).set(GOODS.GOODS_TYPE, BaseConstant.ACTIVITY_TYPE_GROUP_BUY).where(GOODS.GOODS_TYPE.notIn(highPriorityAct).and(GOODS.GOODS_ID.in(goodsIds))).execute();
    }

    /**
     * 检查规格库存，更新拼团规格库存以保证拼团库存不大于规格库存
     * @param goodsIds
     */
    private void updateGroupBuyProcudtStock(List<Integer> goodsIds){
        List<GroupBuyDetailVo> activeGroupBuyList = getGroupBuyWithMonitor(goodsIds);
        for(GroupBuyDetailVo  groupBuy : activeGroupBuyList){
            for(GroupBuyProductVo groupBuyProduct : groupBuy.getProductList()){
                int prdNumber = goodsService.goodsSpecProductService.getPrdNumberByPrdId(groupBuyProduct.getProductId());
                if(prdNumber < groupBuyProduct.getStock()){
                    db().update(GROUP_BUY_PRODUCT_DEFINE).set(GROUP_BUY_PRODUCT_DEFINE.STOCK,(short)prdNumber).execute();
                }
            }
        }
    }

    /**
     * 当前有效的进行中拼团活动
     * @return
     */
    private List<GroupBuyDetailVo> getGroupBuyWithMonitor(List<Integer> goodsIds){
        List<GroupBuyDetailVo> res = db().select(GROUP_BUY_DEFINE.STOCK,GROUP_BUY_DEFINE.GOODS_ID,GROUP_BUY_DEFINE.ID).from(GROUP_BUY_DEFINE).where(
            GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
            .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(GROUP_BUY_DEFINE.START_TIME.lt(DateUtils.getLocalDateTime()))
            .and(GROUP_BUY_DEFINE.END_TIME.gt(DateUtils.getLocalDateTime()))
            .and(GROUP_BUY_DEFINE.GOODS_ID.in(goodsIds))
        ).fetchInto(GroupBuyDetailVo.class);
        for(GroupBuyDetailVo groupBuy : res){
            List<GroupBuyProductVo> groupBuyProduct = db().select(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID,GROUP_BUY_PRODUCT_DEFINE.STOCK,GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID).from(GROUP_BUY_PRODUCT_DEFINE).where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(groupBuy.getId())).fetchInto(GroupBuyProductVo.class);
            groupBuy.setProductList(groupBuyProduct);
        }
        return res;
    }

    /**
     * 已经结束的团（距开团时间已超过24小时）
     * @return
     */
    private List<GroupBuyListScheduleBo> getClosedGroup(){
        return db().select(GROUP_BUY_DEFINE.GOODS_ID,GROUP_BUY_DEFINE.NAME,GROUP_BUY_DEFINE.IS_DEFAULT,GROUP_BUY_DEFINE.LIMIT_AMOUNT,GROUP_BUY_DEFINE.REWARD_COUPON_ID,GROUP_BUY_LIST.ACTIVITY_ID,GROUP_BUY_LIST.GROUP_ID,GROUP_BUY_LIST.ORDER_SN,GROUP_BUY_LIST.START_TIME)
            .from(GROUP_BUY_LIST.leftJoin(GROUP_BUY_DEFINE).on(GROUP_BUY_LIST.ACTIVITY_ID.eq(GROUP_BUY_DEFINE.ID)))
            .where(
                GROUP_BUY_LIST.STATUS.eq(STATUS_ONGOING)
                .and(GROUP_BUY_LIST.IS_GROUPER.eq(IS_GROUPER_Y))
                .and(GROUP_BUY_LIST.START_TIME.lt(DateUtils.getDalyedDateTime(-3600*24)))
            ).fetchInto(GroupBuyListScheduleBo.class);
    }

    /**
     * 一个团里的正在拼团
     * @param groupId
     * @return
     */
    private List<GroupBuyListRecord> getGroupRecordsByGroupId(int groupId){
        return db().select().from(GROUP_BUY_LIST).where(
            GROUP_BUY_LIST.GROUP_ID.eq(groupId)
            .and(GROUP_BUY_LIST.STATUS.eq(STATUS_ONGOING))
        ).fetchInto(GroupBuyListRecord.class);
    }

    /**
     * 更新一个团下所有的参团状态
     * @param groupId
     * @param status
     */
    private void updateGroupBuyListStatus(int groupId,byte status){
        db().update(GROUP_BUY_LIST).set(GROUP_BUY_LIST.STATUS,status).set(GROUP_BUY_LIST.END_TIME, DateUtils.getLocalDateTime()).where(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).execute();
    }

    /**
     * 插入randNum个伪造的参团记录
     * @param group
     * @param randNum
     */
    private void insertRandGroupBuyList(GroupBuyListScheduleBo group,int randNum){
        GroupBuyListRecord record = db().newRecord(GROUP_BUY_LIST);
        record.setActivityId(group.getActivityId());
        record.setGoodsId(group.getGoodsId());
        record.setGroupId(group.getGroupId());
        //userId为0时代表该记录为伪造参团记录
        record.setUserId(0);
        record.setStatus(STATUS_SUCCESS);
        record.setOrderSn("");
        record.setStartTime(group.getStartTime());
        record.setEndTime(DateUtils.getLocalDateTime());
        List<GroupBuyListRecord> list = new ArrayList<>();
        for(int i = 0;i < randNum;i++){
            list.add(record);
        }
        db().batchInsert(list).execute();
    }

    /**
     * 对多个拼团失败订单自动退款
     * @param orderSnList
     */
    private void refund(List<String> orderSnList){
        orderSnList.forEach(orderSn->{
            OrderInfoRecord orderInfo = orderInfoService.getOrderByOrderSn(orderSn);
            Result<OrderGoodsRecord> oGoods = orderGoodsService.getByOrderId(orderInfo.getOrderId());

            //组装退款param
            RefundParam param = new RefundParam();
            param.setAction((byte)OrderServiceCode.RETURN.ordinal());//1是退款
            param.setIsMp(OrderConstant.IS_MP_AUTO);
            param.setReturnSourceType(OrderConstant.RS_AUTO_GROUP_BUY);
            param.setOrderSn(orderSn);
            param.setOrderId(orderInfo.getOrderId());
            param.setReturnType(OrderConstant.RT_ONLY_MONEY);
            param.setReturnMoney(orderInfo.getMoneyPaid().add(orderInfo.getScoreDiscount()).add(orderInfo.getUseAccount()).add(orderInfo.getMemberCardBalance()).subtract(orderInfo.getShippingFee()));
            param.setShippingFee(orderInfo.getShippingFee());

            List<RefundParam.ReturnGoods> returnGoodsList = new ArrayList<>();
            oGoods.forEach(orderGoods->{
                RefundParam.ReturnGoods returnGoods = new RefundParam.ReturnGoods();
                returnGoods.setRecId(orderGoods.getRecId());
                returnGoods.setReturnNumber(orderGoods.getGoodsNumber());

                returnGoodsList.add(returnGoods);
            });

            param.setReturnGoods(returnGoodsList);

            ExecuteResult executeResult = saas.getShopApp(getShopId()).orderActionFactory.orderOperate(param);
            if(executeResult == null || !executeResult.isSuccess()){
                //throw new BusinessException(executeResult.getErrorCode());
                //退款失败
                logger().error("拼团自动退款失败：" + orderSn);
                //TODO log记录或其他处理
            }
        });
    }
}
