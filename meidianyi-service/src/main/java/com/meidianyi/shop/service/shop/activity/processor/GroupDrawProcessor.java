package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupdraw.GroupDrawMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawReturn;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam.Goods;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.activity.dao.GroupDrawProcessorDao;
import com.meidianyi.shop.service.shop.market.groupdraw.GroupDrawService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_N;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_Y;

/**
 * 拼团抽奖
 * 
 * @author zhaojianqiang
 * @time 上午10:44:55
 */
@Service
@Slf4j
public class GroupDrawProcessor implements CreateOrderProcessor,GoodsDetailProcessor{
	@Autowired
	private GroupDrawService groupDrawService;
    @Autowired
	private GroupDrawProcessorDao groupDrawProcessorDao;

	@Override
	public Byte getPriority() {
		return GoodsConstant.ACTIVITY_GROUP_BUY_PRIORITY;
	}

	@Override
	public Byte getActivityType() {
		return BaseConstant.ACTIVITY_TYPE_GROUP_DRAW;
	}

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        if (!BaseConstant.ACTIVITY_TYPE_GROUP_DRAW.equals(param.getActivityType()) || param.getActivityId() == null) {
            return;
        }
        log.debug("小程序-商品详情-拼团抽奖信息获取开始");
        GroupDrawMpVo groupDrawMpVo = groupDrawProcessorDao.getGroupDrawInfoForDetail(param.getActivityId(), param.getUserId());
        if (BaseConstant.ACTIVITY_STATUS_NOT_HAS.equals(groupDrawMpVo.getActState())) {
            capsule.setActivity(groupDrawMpVo);
            log.debug("小程序-商品详情-拼团抽奖信息获取失败-拼团抽奖活动不存在[{}]-详情处理退出", param.getActivityId());
            return;
        }
        capsule.setActivity(groupDrawMpVo);
    }

    /*********** 下单 *****************/
	@Override
	public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
		log.info("拼团抽奖的判断processInitCheckedOrderCreate");
		// 拼团不使用优惠券和会员卡
		param.setMemberCardNo(StringUtils.EMPTY);
		param.setCouponSn(StringUtils.EMPTY);
		// 团长,团id
		Byte isGrouper = param.getGroupId() == null ? IS_GROUPER_Y : IS_GROUPER_N;
		log.debug("拼团订单");
		if (isGrouper.equals(IS_GROUPER_Y)) {
			log.info("是团长");
			param.setIsGrouper(IS_GROUPER_Y);
		} else {
			log.info("不是团长");
			param.setIsGrouper(IS_GROUPER_N);
		}
		log.info("开始校验");
		Goods goods = check(param);
		log.info("校验结束");
		GroupDrawRecord groupDraw = groupDrawService.getById(param.getActivityId());
		BigDecimal payMoney = groupDraw.getPayMoney();
		log.info("价格改为" + payMoney);
		goods.setProductPrice(payMoney);
		goods.setGoodsPrice(payMoney);
		groupDrawService.createInviteRecord(param);
		log.info("processInitCheckedOrderCreate结束");
	}

	private Goods check(OrderBeforeParam param) throws MpException {
		List<Goods> goodsList = param.getGoods();
		if (goodsList.size() > 1) {
			// 只能买一个商品
			throw new MpException(JsonResultCode.GROUP_ONLY_ONE, null);
		}
		Goods goods = goodsList.get(0);
		GroupDrawReturn result = groupDrawService.canCreateGroupOrder(param.getWxUserInfo().getUserId(),
				param.getActivityId(), goods.getGoodsId(), param.getGroupId(), true);
		JsonResultCode code = result.getCode();
		log.info("拼团抽奖的判断的code" + code);
		if (!code.equals(JsonResultCode.CODE_SUCCESS)) {
			//throw new MpException(code, null);
			throw MpException.initErrorResult(code, result.getUrl(), null);
		}
		return goods;
	}

	@Override
	public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
		// 拼团抽奖的判断
		log.info("拼团抽奖的判断processSaveOrderInfo");
		check(param);
		log.info("processSaveOrderInfo校验完");
		List<OrderGoodsBo> bos = param.getBos();
		log.info("bos大小"+bos.size());
		if (bos.size() > 1) {
			// 只能买一个商品
			throw new MpException(JsonResultCode.GROUP_ONLY_ONE, null);
		}
		OrderGoodsBo orderGoodsBo = bos.get(0);
		groupDrawService.generateGroupRecord(order, param.getGroupId(), (byte) -1,orderGoodsBo.getGoodsId());
		log.info("processSaveOrderInfo结束");
	}

	@Override
	public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
		log.info("拼团抽奖的判断processOrderEffective");
		check(param);
		log.info("processOrderEffective校验完");
		groupDrawService.updateGroupInfoByOrderSn(order.getOrderSn(), (byte) 0);
		log.info("processOrderEffective结束");
	}

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {

    }
}
