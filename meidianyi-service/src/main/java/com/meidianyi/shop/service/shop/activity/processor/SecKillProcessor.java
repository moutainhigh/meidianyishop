package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.seckill.SeckillProductBo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.activity.dao.SecKillProcessorDao;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.SecKillProductDefine.SEC_KILL_PRODUCT_DEFINE;

/**
 * 秒杀
 * @author 李晓冰
 * @date 2019年11月01日
 * 秒杀
 */
@Service
@Slf4j
public class SecKillProcessor implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor,ActivityCartListStrategy ,CreateOrderProcessor{
    @Autowired
    SecKillProcessorDao secKillProcessorDao;
    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_SEC_KILL_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_SEC_KILL;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        List<GoodsListMpBo> availableCapsules = capsules.stream().filter(x -> BaseConstant.ACTIVITY_TYPE_SEC_KILL.equals(x.getActivityType())).collect(Collectors.toList());
        List<Integer> goodsIds = availableCapsules.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());
        Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> goodsSecKillListInfo = secKillProcessorDao.getGoodsSecKillListInfo(goodsIds, DateUtils.getLocalDateTime());

        availableCapsules.forEach(capsule->{
            if (goodsSecKillListInfo.get(capsule.getGoodsId()) == null) {
                return;
            }
            Record3<Integer, Integer, BigDecimal> record3 = goodsSecKillListInfo.get(capsule.getGoodsId()).get(0);

            capsule.setRealPrice(record3.get(SEC_KILL_PRODUCT_DEFINE.SEC_KILL_PRICE));
            GoodsActivityBaseMp activity = new GoodsActivityBaseMp();
            activity.setActivityId(record3.get(SEC_KILL_PRODUCT_DEFINE.SK_ID));
            activity.setActivityType(BaseConstant.ACTIVITY_TYPE_SEC_KILL);
            capsule.getGoodsActivities().add(activity);
            capsule.getProcessedTypes().add(BaseConstant.ACTIVITY_TYPE_SEC_KILL);
        });
    }

    /**
     * 商品详情页-秒杀
     * @param
     */
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        if (param.getActivityType() == null && capsule.getActivityAnnounceMpVo() == null) {
            // 探测是否需要进行活动预告
            capsule.setActivityAnnounceMpVo(secKillProcessorDao.getAnnounceInfo(capsule.getGoodsId(), DateUtils.getLocalDateTime()));
            return;
        }
        if(param.getActivityId() != null && param.getActivityType().equals(BaseConstant.ACTIVITY_TYPE_SEC_KILL)){
            //处理之前capsule中需要有商品的基本信息
            capsule.setActivity(secKillProcessorDao.getDetailSeckillInfo(param.getActivityId(),param.getUserId(),capsule));
        }
    }
    //*********************************购物车********************************

    /**
     * 购物车-秒杀
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        //秒杀商品
        List<Integer> secProductList = cartBo.getCartGoodsList().stream()
                .filter(goods -> BaseConstant.ACTIVITY_TYPE_SEC_KILL.equals(goods.getGoodsRecord().getGoodsType()))
                .map(WxAppCartGoods::getProductId).collect(Collectors.toList());
        //查询商品的秒杀活动,获取活动id
        Result<? extends Record> secKillInfoList = secKillProcessorDao.getSecKillInfoList(secProductList, cartBo.getDate());
        if (secKillInfoList!=null&&secKillInfoList.size()>0){
            Map<Integer, SeckillProductBo> seckillProductBoMap = secKillInfoList.intoMap(SEC_KILL_PRODUCT_DEFINE.PRODUCT_ID,SeckillProductBo.class);
            cartBo.getCartGoodsList().forEach(goods->{
                SeckillProductBo seckillPrd = seckillProductBoMap.get(goods.getProductId());
                if (seckillPrd!=null){
                    CartActivityInfo seckillProductInfo =new CartActivityInfo();
                    seckillProductInfo.setActivityType(BaseConstant.ACTIVITY_TYPE_SEC_KILL);
                    seckillProductInfo.setActivityId(seckillPrd.getSkId());
                    seckillProductInfo.setProductPrice(seckillPrd.getSecKillPrice());
                    goods.getCartActivityInfos().add(seckillProductInfo);
                }
            });
        }
    }

    /**
     * 订单生成前，重新计算秒杀价格
     * @param param
     */
    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) {
        secKillProcessorDao.setOrderPrdSeckillPrice(param);
        //秒杀不允许使用积分支付、优惠券、会员卡、货到付款
        if(param.getPaymentList() != null){
            param.getPaymentList().remove(OrderConstant.PAY_CODE_SCORE_PAY);
            param.getPaymentList().remove(OrderConstant.PAY_CODE_COD);
        }
        //禁用优惠券
        param.setCouponSn("");
        //禁用会员卡
        param.setMemberCardNo("");
    }

    /**
     * 秒杀下单后锁库存处理
     * 抛出异常：库存不足
     * @param order
     * @throws MpException
     */
    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        secKillProcessorDao.processSeckillStock(param,order);
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param,OrderInfoRecord order) throws MpException {
        if(BaseConstant.ACTIVITY_TYPE_SEC_KILL.equals(param.getActivityType()) && param.getActivityId() != null){
            secKillProcessorDao.seckillService.addActivityTag(param.getActivityId(),param.getWxUserInfo().getUserId());
        }
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {
        secKillProcessorDao.processReturn(returnOrderRecord,activityId,returnGoods);
    }

}
