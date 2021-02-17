package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.bargain.BargainMpVo;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainGoodsPriceBo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.activity.dao.BargainProcessorDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2019年11月01日
 */
@Slf4j
@Service
public class BargainProcessor implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor,CreateOrderProcessor{
    @Autowired
    BargainProcessorDao bargainProcessorDao;
    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_BARGAIN_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_BARGAIN;
    }

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        if (param.getActivityId() == null || !BaseConstant.ACTIVITY_TYPE_BARGAIN.equals(param.getActivityType())) {
            return;
        }

        BargainMpVo bargainInfo = bargainProcessorDao.getBargainInfo(param.getUserId(), param.getActivityId(),param.getGoodsId());

        if (BaseConstant.ACTIVITY_STATUS_NOT_HAS.equals(bargainInfo.getActState())) {
            capsule.setActivity(bargainInfo);
            log.debug("小程序-商品详情-拼团信息获取失败-砍价活动不存在[{}]-详情处理退出",param.getActivityId());
            return;
        }
        // 设置最终库存
        int stock = capsule.getGoodsNumber()<bargainInfo.getStock()?capsule.getGoodsNumber():bargainInfo.getStock();
        bargainInfo.setStock(stock);

        if (stock == 0 && BaseConstant.needToConsiderNotHasNum(bargainInfo.getActState())) {
            log.debug("小程序-商品详情-砍价商品数量已用完");
            bargainInfo.setActState(BaseConstant.ACTIVITY_STATUS_NOT_HAS_NUM);
        }
        capsule.setActivity(bargainInfo);
    }

    /*****装修商品列表*****/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        List<GoodsListMpBo> availableCapsules = capsules.stream().filter(x -> BaseConstant.ACTIVITY_TYPE_BARGAIN.equals(x.getActivityType())).collect(Collectors.toList());
        List<Integer> goodsIds = availableCapsules.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());
        Map<Integer, BargainGoodsPriceBo> goodsBargainInfo = bargainProcessorDao.getGoodsBargainListInfo(goodsIds, DateUtils.getLocalDateTime());

        availableCapsules.forEach(capsule->{
            if (goodsBargainInfo.get(capsule.getGoodsId()) == null) {
                return;
            }
            BargainGoodsPriceBo bargain = goodsBargainInfo.get(capsule.getGoodsId());
            capsule.setRealPrice(GoodsConstant.BARGAIN_TYPE_FIXED.equals(bargain.getBargainType())?bargain.getExpectationPrice():bargain.getFloorPrice());
            GoodsActivityBaseMp activity = new GoodsActivityBaseMp();

            activity.setActivityId(bargain.getId());
            activity.setActivityType(BaseConstant.ACTIVITY_TYPE_BARGAIN);
            capsule.getGoodsActivities().add(activity);
            capsule.getProcessedTypes().add(BaseConstant.ACTIVITY_TYPE_BARGAIN);
        });
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        bargainProcessorDao.setOrderPrdBargainPrice(param);
        //砍价不允许使用积分支付和货到付款
        if(param.getPaymentList() != null){
            param.getPaymentList().remove(OrderConstant.PAY_CODE_SCORE_PAY);
            param.getPaymentList().remove(OrderConstant.PAY_CODE_COD);
        }
        //禁用优惠券
        param.setCouponSn("");
        //禁用会员卡
        param.setMemberCardNo("");
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        bargainProcessorDao.processSaveOrderInfo(param,order);
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {
        bargainProcessorDao.processReturn(activityId,returnGoods);
    }
}
