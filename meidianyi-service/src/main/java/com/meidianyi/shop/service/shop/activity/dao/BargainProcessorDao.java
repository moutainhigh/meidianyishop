package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.BargainGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.BargainRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.bargain.BargainMpVo;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainGoodsPriceBo;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainRecordInfo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.market.bargain.BargainRecordService;
import com.meidianyi.shop.service.shop.market.bargain.BargainService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.BARGAIN_GOODS;
import static com.meidianyi.shop.db.shop.Tables.BARGAIN_RECORD;
import static com.meidianyi.shop.db.shop.Tables.BARGAIN_USER_LIST;
import static com.meidianyi.shop.db.shop.tables.Bargain.BARGAIN;

/**
 * @author 李晓冰
 * @date 2019年11月01日
 */
@Service
public class BargainProcessorDao extends ShopBaseService {

    @Autowired
    private BargainService bargainService;
    @Autowired
    private OrderInfoService orderInfo;

    /**
     * 获取商品集合内的砍价信息
     *
     * @param goodsIds 商品id集合
     * @param date     日期
     * @return List<BargainRecord>
     */
    public Map<Integer, BargainGoodsPriceBo> getGoodsBargainListInfo(List<Integer> goodsIds, Timestamp date) {
        Map<Integer, List<BargainGoodsPriceBo>> integerListMap = db().select(BARGAIN_GOODS.GOODS_ID, BARGAIN_GOODS.EXPECTATION_PRICE, BARGAIN_GOODS.FLOOR_PRICE, BARGAIN.BARGAIN_TYPE, BARGAIN.ID)
            .from(BARGAIN_GOODS).leftJoin(BARGAIN).on(BARGAIN.ID.eq(BARGAIN_GOODS.BARGAIN_ID))
            .where(BARGAIN.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(BARGAIN.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(BARGAIN_GOODS.GOODS_ID.in(goodsIds)).and(BARGAIN.START_TIME.lt(date)).and(BARGAIN.END_TIME.gt(date)).and(BARGAIN.STOCK.gt(0))
            .orderBy(BARGAIN.FIRST.desc(), BARGAIN.CREATE_TIME.desc())
            .fetchGroups(BARGAIN_GOODS.GOODS_ID, BargainGoodsPriceBo.class);
        Map<Integer,BargainGoodsPriceBo> retMap = new HashMap<>(integerListMap.size());

        for (Map.Entry<Integer, List<BargainGoodsPriceBo>> entry : integerListMap.entrySet()) {
            retMap.put(entry.getKey(),entry.getValue().get(0));
        }
        return retMap;
    }

    /**
     * 小程序-商品详情-获取砍价详情
     * @param activityId
     * @return
     */
    public BargainMpVo getBargainInfo(Integer userId,Integer activityId,Integer goodsId) {
        BargainMpVo vo = new BargainMpVo();
        vo.setActivityId(activityId);
        vo.setActivityType(BaseConstant.ACTIVITY_TYPE_BARGAIN);

        Timestamp now = DateUtils.getLocalDateTime();

        BargainRecord bargainRecord = db().selectFrom(BARGAIN).where(BARGAIN.ID.eq(activityId).and(BARGAIN.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))).fetchAny();
        BargainGoodsRecord bargainGoods = db().fetchAny(BARGAIN_GOODS,BARGAIN_GOODS.BARGAIN_ID.eq(activityId).and(BARGAIN_GOODS.GOODS_ID.eq(goodsId)));

        Byte aByte = canApplyBargain(userId, now, bargainRecord,goodsId);
        if(BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT.equals(aByte)){
            //该用户已经发起过对这个活动的砍价
            int recordId = db().select(BARGAIN_RECORD.ID).from(BARGAIN_RECORD).where(BARGAIN_RECORD.BARGAIN_ID.eq(activityId)).and(BARGAIN_RECORD.USER_ID.eq(userId).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))).fetchOptionalInto(Integer.class).orElse(0);
            vo.setRecordId(recordId);
        }
        vo.setActState(aByte);

        // 活动不存在
        if (BaseConstant.ACTIVITY_STATUS_NOT_HAS.equals(vo.getActState())) {
            return vo;
        }
        // 活动未开始
        if (BaseConstant.ACTIVITY_STATUS_NOT_START.equals(aByte)) {
            vo.setStartTime((bargainRecord.getStartTime().getTime() - now.getTime())/1000);
        }
        vo.setEndTime((bargainRecord.getEndTime().getTime() - now.getTime())/1000);

        // 设置砍价展示价格
        vo.setBargainPrice(GoodsConstant.BARGAIN_TYPE_FIXED.equals(bargainRecord.getBargainType())?bargainGoods.getExpectationPrice():bargainGoods.getFloorPrice());
        vo.setBargainType(bargainRecord.getBargainType());
        // 砍价库存，处理器里面还需要判断商品数量是否足够
        vo.setStock(bargainGoods.getStock());
        // 砍价活动表 免运费为1 不免运费为0,正好和前端相反
        vo.setFreeShip((byte) (bargainRecord.getFreeFreight()==1?0:1));
        int bargainJoinNum = getBargainJoinNum(activityId);
        if(bargainRecord.getInitialSales() != null && bargainRecord.getInitialSales() > 0 && saas.shop.account.getAccountInfoForId(getSysId()).getBaseSale() == 1){
            bargainJoinNum += bargainRecord.getInitialSales();
        }
        vo.setBargainJoinNum(bargainJoinNum);
        return vo;
    }

    /**
     * 查询某一砍价活动的参与人数
     * @param activityId 活动id
     * @return 参与的人数
     */
    private Integer getBargainJoinNum(Integer activityId){
        return db().fetchCount(BARGAIN_RECORD.innerJoin(BARGAIN_USER_LIST).on(BARGAIN_RECORD.ID.eq(BARGAIN_USER_LIST.RECORD_ID)), BARGAIN_RECORD.BARGAIN_ID.eq(activityId));
    }

    /**
     * 判断用户是否可以发起砍价
     * @param userId 用户id
     * @param date 时间
     * @param bargainRecord 砍价详情
     * @return
     */
    public Byte canApplyBargain(Integer userId, Timestamp date, BargainRecord bargainRecord,Integer goodsId) {
        logger().debug("小程序-商品详情-砍价信息-是否可以发起砍价判断");
        if (date == null) {
            date = DateUtils.getLocalDateTime();
        }

        if (bargainRecord == null) {
            logger().debug("小程序-商品详情-砍价信息-活动不存在或已删除");
            return  BaseConstant.ACTIVITY_STATUS_NOT_HAS;
        }

        if (BaseConstant.ACTIVITY_STATUS_DISABLE.equals(bargainRecord.getStatus())) {
            logger().debug("小程序-商品详情-砍价信息-该活动未启用[activityId:{}]",bargainRecord.getId());
            return BaseConstant.ACTIVITY_STATUS_STOP;
        }

        if (bargainRecord.getStartTime().compareTo(date) > 0) {
            logger().debug("活动未开始[activityId:{}]",bargainRecord.getId());
            return BaseConstant.ACTIVITY_STATUS_NOT_START;
        }

        if (bargainRecord.getEndTime().compareTo(date) < 0) {
            logger().debug("活动已经结束[activityId:{}]", bargainRecord.getId());
            return BaseConstant.ACTIVITY_STATUS_END;
        }

        // 一个用户同活动下同一个商品处于正在砍价中 只能有一个
        int bargainCount = db().fetchCount(BARGAIN_RECORD, BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(BARGAIN_RECORD.BARGAIN_ID.eq(bargainRecord.getId()))
            .and(BARGAIN_RECORD.USER_ID.eq(userId)).and(BARGAIN_RECORD.GOODS_ID.eq(goodsId)));
        if (bargainCount > 0) {
            logger().debug("用户存在正在砍价[activityId:{}]", bargainRecord.getId());
            return BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT;
        }


        return BaseConstant.ACTIVITY_STATUS_CAN_USE;
    }


    //砍价下单

    /**
     * 下单前
     * 处理砍价订单的价格
     * @param param
     */
    public void setOrderPrdBargainPrice(OrderBeforeParam param) throws MpException {
        logger().info("砍价下单校验调试param:{}",param);
        BargainRecordInfo bargainRecordInfo = bargainService.bargainRecord.getRecordInfo(param.getRecordId());
        if(!bargainRecordInfo.getStatus().equals(BargainRecordService.STATUS_SUCCESS)){
            //状态不对
            throw new MpException(JsonResultCode.BARGAIN_NOT_YET_SUCCESSFUL);
        }else if(bargainRecordInfo.getIsOrdered().equals(BargainRecordService.IS_ORDERED_Y)){
            //已下单
            OrderInfoRecord order = orderInfo.getOrderByOrderSn(bargainRecordInfo.getOrderSn());
            if(order.getOrderAmount().equals(bargainRecordInfo.getFloorPrice()) || order.getOrderStatus() > OrderConstant.ORDER_CLOSED){
                throw new MpException(JsonResultCode.BARGAIN_RECORD_ORDERED);
            }
        }

        //是否免运费
        param.setIsFreeShippingAct(bargainRecordInfo.getFreeFreight());

        //临时记录
        param.setBargainRecordInfo(bargainRecordInfo);

        for(OrderBeforeParam.Goods prd : param.getGoods()) {
            //砍价价格
            BigDecimal price = bargainRecordInfo.getGoodsPrice().subtract(bargainRecordInfo.getBargainMoney());
            prd.setProductPrice(price);
            prd.setGoodsPrice(price);
        }
    }

    /**
     * 砍价下单
     * @param orderParam
     * @param newOrder 新订单
     */
    public void processSaveOrderInfo(OrderBeforeParam orderParam, OrderInfoRecord newOrder) throws MpException {
        if(orderParam.getBargainRecordInfo().getIsOrdered().equals(BargainRecordService.IS_ORDERED_Y) && StringUtil.isNotEmpty(orderParam.getBargainRecordInfo().getOrderSn())){
            //关闭砍价记录之前绑定的其他订单
            OrderOperateQueryParam param = new OrderOperateQueryParam();
            param.setAction((byte) OrderServiceCode.CLOSE.ordinal());
            param.setIsMp(OrderConstant.IS_MP_Y);
            param.setOrderSn(orderParam.getBargainRecordInfo().getOrderSn());
            param.setOrderId(orderInfo.getOrderIdBySn(orderParam.getBargainRecordInfo().getOrderSn()));
            //关闭订单
            ExecuteResult executeResult = saas.getShopApp(getShopId()).orderActionFactory.orderOperate(param);
            if(!executeResult.isSuccess()){
                throw new MpException(JsonResultCode.CODE_FAIL);
            }
        }

        //绑定新订单
        db().update(BARGAIN_RECORD).set(BARGAIN_RECORD.IS_ORDERED,BargainRecordService.IS_ORDERED_Y).set(BARGAIN_RECORD.ORDER_SN,newOrder.getOrderSn()).where(BARGAIN_RECORD.ID.eq(orderParam.getBargainRecordInfo().getId())).execute();
    }

    public void processReturn(Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {
        returnGoods.forEach(g->{
            if(g.getIsGift().equals(OrderConstant.IS_GIFT_N)){
                bargainService.updateBargainStock(activityId,g.getGoodsId(),- 1);
            }
        });
    }

}
