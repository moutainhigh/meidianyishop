package com.meidianyi.shop.service.shop.market.groupdraw;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawRecord;
import com.meidianyi.shop.db.shop.tables.records.JoinDrawListRecord;
import com.meidianyi.shop.db.shop.tables.records.JoinGroupListRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.coupon.MpGetCouponParam;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam.ReturnGoods;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.coupon.CouponMpService;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.action.ReturnService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.util.Util.currentTimeStamp;
import static com.meidianyi.shop.db.shop.tables.GroupDraw.GROUP_DRAW;
import static com.meidianyi.shop.db.shop.tables.JoinDrawList.JOIN_DRAW_LIST;
import static com.meidianyi.shop.db.shop.tables.JoinGroupList.JOIN_GROUP_LIST;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_WAIT_DELIVERY;

/**
 * 拼团抽奖
 *
 * @author 郑保乐
 */
@Service
public class GroupDrawUserService extends ShopBaseService {
	@Autowired
	private GroupDrawService drawService;

	/** 拼团中 **/
	private static final byte GROUP_ONGOING = 0;
	/** 已成团 **/
	private static final byte GROUPED = 1;
	/** 未成团 **/
	private static final byte NOT_GROUPED = 2;
	/** 已开奖 **/
	private static final byte DREW = 1;
	/** 开奖失败 **/
	private static final byte DRAW_FAIL = 2;
	/** 已中奖 **/
	private static final byte WIN_DRAW = 1;
	@Autowired
	private CouponMpService couponMpService;
	@Autowired
	private ReturnService returnService;
	@Autowired
	private AtomicOperation optOperation;
	@Autowired
	private OrderGoodsService orderGoodsService;
	@Autowired
	private OrderInfoService orderInfoService;
    @Autowired
    private OrderReadService orderRead;
	private static final byte ZERO = 0;
	private static final byte ONE = 1;

	/**
	 * 获取拼团抽奖活动
	 */
	private GroupDrawRecord getGroupDraw(Integer groupDrawId) {
		return db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(groupDrawId)).fetchOneInto(GROUP_DRAW);
	}

	/**
	 * 处理拼团抽奖
	 */
	public void dealOpenGroupDraw() {
		logger().info("处理拼团抽奖");
		List<GroupDrawRecord> goodsGroupDrawList = getOpenGroupDrawList();
		goodsGroupDrawList.forEach(goodsGroupDraw -> {
			Integer goodsGroupDrawId = goodsGroupDraw.getId();
			Short minJoinNum = goodsGroupDraw.getMinJoinNum();
			logger().info("团："+goodsGroupDrawId+"最小参与人数："+minJoinNum);
			// 抽奖记录置为已开奖
			updateGroupDrawStatus(goodsGroupDraw.getId());
			// 活动商品
			List<Integer> goodsIds = Util.stringToList(goodsGroupDraw.getGoodsId());
			goodsIds.forEach(goodsId -> {
				int joinUserNum = getJoinUserNumByGoodsId(goodsGroupDrawId, goodsId, null);
				logger().info("joinUserNum:{}",joinUserNum);
				if (joinUserNum > 0) {
					// 已成团该商品参与用户数
					logger().info("已成团该商品参与用户数");
					joinUserNum = getJoinUserNumByGoodsId(goodsGroupDrawId, goodsId, GROUPED);
					logger().info("参与人数："+joinUserNum);
					if (minJoinNum <= joinUserNum) {
						// 人数达到开奖条件 开奖
						logger().info("人数达到开奖条件 开奖");
						startDraw(goodsGroupDrawId, goodsId);
					} else {
						// 不满足开奖条件，更新开奖状态
						updateGroupDrawStatus(goodsGroupDrawId, goodsId);
					}
					// 拼团失败更新开奖状态
					logger().info("拼团失败更新开奖状态");
					updateOnGoingGroupDrawStatus(goodsGroupDrawId, goodsId);
					// 获得参与抽奖用户
					logger().info("获得参与抽奖用户");
					List<JoinGroupListRecord> groupUserList = getGroupUserListByGoodsId(goodsGroupDrawId, goodsId,
							null);
					// 订单号
					List<String> orderSns = groupUserList.stream().map(JoinGroupListRecord::getOrderSn)
							.collect(Collectors.toList());
					// 更新订单状态为 "待发货"
					logger().info("更新订单状态为 待发货");
					updateOrderWaitDelivery(orderSns);
					// TODO 同步订单状态到 CRM
					List<Integer> couponUserIds = new LinkedList<>();
					String couponIds = goodsGroupDraw.getRewardCouponId();
					groupUserList.forEach(groupUser -> {
						if (WIN_DRAW == groupUser.getIsWinDraw()) {
							// 更新库存
							logger().info("更新库存");
							updateProductNumber(groupUser.getOrderSn());
						} else {
							refundMoney(groupUser.getOrderSn());
							if (!couponIds.isEmpty()) {
								couponUserIds.add(groupUser.getUserId());
							}
							sendDrawResultMessage(groupUser.getUserId(), groupUser.getGroupDrawId(),
									groupUser.getGroupId(), ZERO);
						}
					});
					// 批量送券
					giveVoucher(couponIds, couponUserIds);
				}
			});
		});
	}

    private void refundMoney(String orderSn) {
        logger().info("订单号" + orderSn + "未中奖退款");
        RefundParam param = new RefundParam();
        param.setReturnType(OrderConstant.RT_ONLY_MONEY);
        param.setAction((byte) OrderServiceCode.RETURN.ordinal());
        param.setOrderSn(orderSn);
        OrderInfoRecord orderInfo = orderInfoService.getOrderByOrderSn(orderSn);
        if (null == orderInfo) {
            return;
        }
        param.setOrderId(orderInfo.getOrderId());
        param.setIsMp(OrderConstant.IS_MP_AUTO);
        param.setReturnMoney(orderInfoService.getOrderFinalAmount(orderInfo.into(OrderListInfoVo.class), false));
        param.setShippingFee(orderInfo.getShippingFee() == null ? BigDecimal.ZERO : orderInfo.getShippingFee());
        Result<OrderGoodsRecord> orderGoods = orderRead.orderGoods.getByOrderId(orderInfo.getOrderId());
        ArrayList<ReturnGoods> returnGoods = new ArrayList<>(orderGoods.size());
        for (OrderGoodsRecord orderGoodsRecord: orderGoods) {
            RefundParam.ReturnGoods oneReturnGoods = new RefundParam.ReturnGoods();
            oneReturnGoods.setRecId(orderGoodsRecord.getRecId());
            oneReturnGoods.setReturnNumber(orderGoodsRecord.getGoodsNumber());
            returnGoods.add(oneReturnGoods);
        }
        param.setReturnGoods(returnGoods);
        ExecuteResult execute = returnService.execute(param);
        if(execute != null && !execute.isSuccess()) {
            logger().error("订单号:{},errorCode:{},errorParam:{},退款失败", orderSn, execute.getErrorCode(), execute.getErrorParam());
        }
    }

	/**
	 * 更新库存
	 */
	private void updateProductNumber(String orderSn) {
		logger().info("订单号" + orderSn + "更新库存");
		OrderInfoRecord orderInfo = db().selectFrom(ORDER_INFO).where(ORDER_INFO.ORDER_SN.eq(orderSn)).fetchAny();
		if (null == orderInfo) {
			return;
		}
		List<OrderGoodsBo> goodsBo = orderGoodsService.getByOrderId(orderInfo.getOrderId()).into(OrderGoodsBo.class);
		try {
			optOperation.updateStockandSales(orderInfo, goodsBo, false);
		} catch (MpException e) {
			logger().info("订单号" + orderSn + "更新库存失败");
			logger().info(e.getMessage(), e);
		}
	}

	/**
	 * 获取订单信息
	 */
	private OrderInfoRecord getOrderInfo(String orderSn) {
		return db().selectFrom(ORDER_INFO).where(ORDER_INFO.ORDER_SN.eq(orderSn)).fetchOneInto(ORDER_INFO);
	}

	/**
	 * 获取订单中的商品
	 */
	private List<OrderGoodsRecord> getOrderGoods(String orderSn) {
		return db().selectFrom(ORDER_GOODS).where(ORDER_GOODS.ORDER_SN.eq(orderSn)).fetch()
				.into(OrderGoodsRecord.class);
	}

	/**
	 * 更新订单状态为待发货
	 */
	private void updateOrderWaitDelivery(List<String> orderSns) {
		db().update(ORDER_INFO).set(ORDER_INFO.ORDER_STATUS, ORDER_WAIT_DELIVERY)
				.where(ORDER_INFO.ORDER_SN.in(orderSns)).execute();
	}

	/**
	 * 拼团失败更新状态
	 */
	private void updateOnGoingGroupDrawStatus(Integer goodsGroupDrawId, Integer goodsId) {
		db().update(JOIN_GROUP_LIST).set(JOIN_GROUP_LIST.STATUS, NOT_GROUPED)
				.set(JOIN_GROUP_LIST.END_TIME, currentTimeStamp())
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(goodsGroupDrawId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId))
						.and(JOIN_GROUP_LIST.STATUS.eq(GROUP_ONGOING)))
				.execute();
	}

	/**
	 * 不满足开奖条件，更新状态
	 */
	private void updateGroupDrawStatus(Integer goodsGroupDrawId, Integer goodsId) {
		logger().info("不满足开奖条件，更新开奖状态");
		db().update(JOIN_GROUP_LIST).set(JOIN_GROUP_LIST.DRAW_STATUS, DRAW_FAIL)
				.set(JOIN_GROUP_LIST.DRAW_TIME, currentTimeStamp())
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(goodsGroupDrawId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId)))
				.execute();
	}

	/**
	 * 获取某个商品的参与用户数
	 */
	private Integer getJoinUserNumByGoodsId(Integer groupDrawId, Integer goodsId, Byte status) {
		logger().info("入参：groupDrawId：{}，goodsId：{}，status：{}",groupDrawId,goodsId,status);
		SelectConditionStep<Record1<Integer>> select = db().selectCount().from(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId)));
		if (null != status) {
			select.and(JOIN_GROUP_LIST.STATUS.eq(status));
		} else {
			select.and(JOIN_GROUP_LIST.STATUS.ge(ZERO));
		}
		return (Integer) select.fetchOne().get(0);
	}

	/**
	 * 更新开奖状态
	 */
	private void updateGroupDrawStatus(Integer id) {
		int execute = db().update(GROUP_DRAW).set(GROUP_DRAW.IS_DRAW, (byte) 1)
				.where(GROUP_DRAW.ID.eq(id)).execute();
		logger().info("更新：{}，结果：{}", id, execute);
	}

	/**
	 * 获取待开奖活动
	 */
	private List<GroupDrawRecord> getOpenGroupDrawList() {
		return db().selectFrom(GROUP_DRAW)
				.where(GROUP_DRAW.END_TIME.le(currentTimeStamp()).and(GROUP_DRAW.IS_DRAW.eq(ZERO)))
				.fetchInto(GroupDrawRecord.class);
	}

	/**
	 * 开奖
	 */
	private void startDraw(Integer groupDrawId, Integer goodsId) {
		logger().info("开奖");
		// 该商品的已成团
		List<Integer> groupIds = getGroupListByGoodsId(groupDrawId, goodsId, GROUPED).stream()
				.map(JoinGroupListRecord::getGroupId).collect(Collectors.toList());
		// 抽奖记录
		List<Integer> drawIds = getUserByGroupIds(groupDrawId, goodsId, groupIds);
		// 开奖
		int winIndex = randomItemFrom(drawIds);
		Integer winDrawId = drawIds.get(winIndex);
		// 状态更新
		updateDraw(winDrawId);
		JoinDrawListRecord winDrawRecord = getDrawRecordById(winDrawId);
		Integer winDrawGroupId = winDrawRecord.getGroupId();
		Integer winDrawUserId = winDrawRecord.getUserId();
		updateDrawStatus(groupDrawId, winDrawGroupId, winDrawUserId);
		updateGroupInfoByGoodsId(groupDrawId, goodsId);
		// 发送模板消息
		logger().info("拼团成功的用户：" + winDrawUserId + "发送中奖信息");
		sendDrawResultMessage(winDrawUserId, groupDrawId, winDrawGroupId, ONE);

	}

	/**
	 * 发送中奖结果
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param groupId
	 * @param isWinDraw
	 */
	public void sendDrawResultMessage(Integer userId, Integer groupDrawId, Integer groupId, Byte isWinDraw) {
		logger().info("发送中奖结果" + isWinDraw);
		GroupDrawRecord groupDrawInfo = getGroupDraw(groupDrawId);
		JoinGroupListRecord userJoinGroup = drawService.getUserJoinGroupInfo(userId, groupDrawId, groupId, true);
		Integer goodsId = userJoinGroup.getGoodsId();
		GoodsRecord good = drawService.getGood(goodsId);
		String goodsName = good.getGoodsName();
		String page = "pages1/pinlotteryinfo/pinlotteryinfo?group_id=" + userJoinGroup.getGroupId() + "&group_draw_id="
				+ userJoinGroup.getGroupDrawId() + "&goods_id=" + goodsId;
		String msg = Objects.equals(isWinDraw, ONE) ? "恭喜您中奖了" : "很遗憾您未中奖";
		String marketName = groupDrawInfo.getName();
		String[][] data = new String[][] { { marketName }, { Util.getdate("yyyy-MM-dd HH:mm:ss") }, { msg } };
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(userId);
		String[][] mpData = new String[][] { { marketName, "#173177" }, { "系统通知", "#173177" },
				{ Util.getdate("yyyy-MM-dd HH:mm:ss"), "#173177" }, { msg, "#173177" }, { "", "#173177" } };
		MaSubscribeData buildData = MaSubscribeData.builder().data307(data).build();
		RabbitMessageParam param = RabbitMessageParam.builder()
				.maTemplateData(
						MaTemplateData.builder().config(SubcribeTemplateCategory.DRAW_RESULT).data(buildData).build())
				.mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.PUSHMSG).data(mpData).build())
				.page(page).shopId(getShopId()).userIdList(arrayList)
				.type(RabbitParamConstant.Type.INVITE_SUCCESS_GROUPBUY).build();
		saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
				TaskJobEnum.SEND_MESSAGE.getExecutionType());

	}

	/**
	 * 更新团开奖状态
	 */
	private void updateGroupInfoByGoodsId(Integer groupDrawId, Integer goodsId) {
		db().update(JOIN_GROUP_LIST).set(JOIN_GROUP_LIST.DRAW_STATUS, DREW)
				.set(JOIN_GROUP_LIST.DRAW_TIME, DateUtils.getSqlTimestamp())
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId))).execute();
	}

	/**
	 * 更新团中奖状态
	 */
	private void updateDrawStatus(Integer groupDrawId, Integer winDrawGroupId, Integer winDrawUserId) {
		db().update(JOIN_GROUP_LIST).set(JOIN_GROUP_LIST.IS_WIN_DRAW, WIN_DRAW)
				.set(JOIN_GROUP_LIST.DRAW_STATUS, WIN_DRAW)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(
						JOIN_GROUP_LIST.GROUP_ID.eq(winDrawGroupId).and(JOIN_GROUP_LIST.USER_ID.eq(winDrawUserId))))
				.execute();
	}

	/**
	 * 获取抽奖记录
	 */
	private JoinDrawListRecord getDrawRecordById(Integer winDrawId) {
		return db().selectFrom(JOIN_DRAW_LIST).where(JOIN_DRAW_LIST.ID.eq(winDrawId))
				.fetchOneInto(JoinDrawListRecord.class);
	}

	/**
	 * 更新中奖状态
	 */
	private void updateDraw(Integer winDrawId) {
		db().update(JOIN_DRAW_LIST).set(JOIN_DRAW_LIST.IS_WIN_DRAW, WIN_DRAW).where(JOIN_DRAW_LIST.ID.eq(winDrawId))
				.execute();
	}

	/**
	 * 获取list中的随机项
	 */
	private int randomItemFrom(List<Integer> items) {
		int size = items.size();
		return new Random().nextInt(size);
	}

	/**
	 * 获取团内用户的id
	 */
	private List<Integer> getUserByGroupIds(Integer groupDrawId, Integer goodsId, List<Integer> groupIds) {
		return db().selectFrom(JOIN_DRAW_LIST)
				.where(JOIN_DRAW_LIST.GROUP_DRAW_ID.eq(groupDrawId)
						.and(JOIN_DRAW_LIST.GOODS_ID.eq(goodsId).and(JOIN_DRAW_LIST.GROUP_ID.in(groupIds))))
				.fetch().into(JoinDrawListRecord.class).stream().map(JoinDrawListRecord::getId)
				.collect(Collectors.toList());
	}

	/**
	 * 获取某个商品的参团记录
	 */
	private List<JoinGroupListRecord> getGroupListByGoodsId(Integer groupDrawId, Integer goodsId, Byte groupStatus) {
		SelectConditionStep<JoinGroupListRecord> select = db().selectFrom(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId)));
		if (null != groupStatus) {
			select.and(JOIN_GROUP_LIST.STATUS.eq(groupStatus));
		} else {
			select.and(JOIN_GROUP_LIST.STATUS.ge(GROUP_ONGOING));
		}
		return select.fetchInto(JoinGroupListRecord.class);
	}

	/**
	 * 通过商品获得团列表
	 *
	 * @param groupDrawId
	 * @param goodsId
	 * @param isWinDraw
	 * @return
	 */
	public List<JoinGroupListRecord> getGroupUserListByGoodsId(Integer groupDrawId, Integer goodsId, Byte isWinDraw) {
		SelectConditionStep<JoinGroupListRecord> select = db().selectFrom(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId)).and(JOIN_GROUP_LIST.STATUS.ge(ZERO)));
		if (null != isWinDraw) {
			select.and(JOIN_GROUP_LIST.IS_WIN_DRAW.eq(isWinDraw));
		}
		return select.fetchInto(JoinGroupListRecord.class);
	}

	/**
	 * 未中奖送券
	 */
	private void giveVoucher(String coupOnIds, List<Integer> userIds) {
		logger().info("未中奖送券");
		logger().info("优惠券id：{}，用户数量：{},列表：{}",coupOnIds,userIds.size(),userIds.toArray());
		String[] split = coupOnIds.split(",");
		List<String> list = new ArrayList<String>();
		for (String string : split) {
			Byte couponGetStatus = couponMpService.couponGetStatus(new MpGetCouponParam(Integer.valueOf(string), null));
			if (Objects.equals(couponGetStatus, ZERO)) {
				list.add(string);
			} else {
				logger().info("优惠券" + string + "状态：" + couponGetStatus);
			}
		}
		String[] array = list.toArray(new String[0]);
		CouponGiveQueueParam newParam = new CouponGiveQueueParam(getShopId(),userIds, 0, array, BaseConstant.ACCESS_MODE_ISSUE,
				BaseConstant.GET_SOURCE_ACT);
		saas.taskJobMainService.dispatchImmediately(newParam, CouponGiveQueueParam.class.getName(), getShopId(),
				TaskJobsConstant.TaskJobEnum.GIVE_COUPON.getExecutionType());
	}

	/**
	 * 获得该活动的成团人数
	 * @param groupDrawId
	 * @return
	 */
	public Integer getSuccessGroupUserNum(Integer groupDrawId) {
		return db().selectCount().from(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId))
				.and(JOIN_GROUP_LIST.STATUS.ge(ONE)).fetchOptionalInto(Integer.class).orElse(0);
	}

	/**
	 * 获得该活动的开团数
	 * @param groupDrawId
	 * @return
	 */
	public Integer getOpenGroupNumberById(Integer groupDrawId) {
		return db().selectCount().from(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId))
				.and(JOIN_GROUP_LIST.STATUS.ge(ZERO)).and(JOIN_GROUP_LIST.IS_GROUPER.eq(ONE)).fetchOptionalInto(Integer.class).orElse(0);
	}

	/**
	 * 获得中奖用户数
	 * @param groupDrawId
	 * @return
	 */
	public Integer getDrawUserNumById(Integer groupDrawId) {
		return db().selectCount().from(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId))
				.and(JOIN_GROUP_LIST.IS_WIN_DRAW.ge(ONE)).fetchOptionalInto(Integer.class).orElse(0);
	}
}
