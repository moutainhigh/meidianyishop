package com.meidianyi.shop.service.shop.task.wechat;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponWxVo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.FpRewardContent;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.FriendPromoteSelectVo;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.market.presale.PreSaleVo;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.store.service.order.StoreAppointmentRemindVo;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.shop.coupon.CouponGiveService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.market.friendpromote.FriendPromoteService;
import com.meidianyi.shop.service.shop.market.presale.PreSaleService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.trade.OrderPayService;
import com.meidianyi.shop.service.shop.store.service.ServiceOrderService;
import com.meidianyi.shop.service.shop.user.message.SubscribeMessageService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubscribeMessageConfig;
import com.meidianyi.shop.service.shop.user.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Result;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;

/**
 * 小程序公众号相关的定时任务
 *
 * @author zhaojianqiang
 * @time 下午2:14:03
 */
@Service
@Slf4j
public class MaMpScheduleTaskService extends ShopBaseService {
	private static final byte ZERO = 0;
	private static final byte NOTIFY_HELP_FROM_FRIENDS = 1;
	private static final byte NOTIFY_HELP_PROCESS = 2;
	private static final byte NOTIFY_HELP_FAIL = 3;
	private static final byte NOTIFY_HELP_SUCCESS = 4;
	private static final byte NOTIFY_AWARD_NO_GET = 5;

	@Autowired
	private CouponService coupon;
	@Autowired
	private ServiceOrderService serviceOrder;
	@Autowired
	private PreSaleService preSale;
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private OrderPayService orderPayService;
	@Autowired
	private FriendPromoteService friendPromoteService;
	@Autowired
	private SubscribeMessageService subscribeMessageService;
	@Autowired
	private CouponGiveService couponGiveService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private ScoreService scoreService;
	@Autowired
	public RecordAdminActionService record;

	/**
	 * 卡券到期提醒
	 * @param
	 * @return
	 */
	public String expiringCouponNotify() {
		// 查找shopId对应的公众号
		String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
		if (officeAppId == null) {
			logger().info("店铺" + getShopId() + "没有关注公众号");
			return null;
		}
		List<CouponWxVo> list = coupon.getExpiringCouponList();
		String page = "pages/coupon/coupon";
		String keyword1 = "尊敬的用户，您的优惠券";
		String keyword11 = "即将到期";
		for (CouponWxVo couponWxVo : list) {
			String couponName = couponWxVo.getActName();
			List<Integer> userIdList = new ArrayList<Integer>();
			UserRecord wxUserInfo = checkMp(couponWxVo.getWxUnionId(), officeAppId);
			if(null==wxUserInfo) {
				continue;
			}
			userIdList.add(wxUserInfo.getUserId());
			logger().info("userIdList" + userIdList);
			String[][] data = new String[][] { { keyword1 + couponName + keyword11, "#173177" }, { "", "#173177" },
					{ couponName, "#173177" },
					{ DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, couponWxVo.getEndTime()), "#173177" },
					{ "", "#173177" } };
			RabbitMessageParam param = RabbitMessageParam.builder()
					.mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.COUPON_EXPIRE).data(data).build())
					.page(page).shopId(getShopId()).userIdList(userIdList).type(RabbitParamConstant.Type.EXPIRED_MEMBER)
					.build();
			logger().info("准备发");
			saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
					TaskJobEnum.SEND_MESSAGE.getExecutionType());
		}
		return null;

	}
	/**
	 * 校验MpOfficialAccountUserRecord
	 * @param wxUnionId
	 * @param officeAppId
	 * @return
	 */
    public UserRecord checkMp(String wxUnionId, String officeAppId) {

		if (StringUtils.isEmpty(wxUnionId)) {
			logger().info("用户" + wxUnionId + "没有关注公众号");
			return null;
		}
		UserRecord user = userService.getUserByUnionId(wxUnionId);
		if (user == null) {
			logger().info("表中没有数据" + wxUnionId);
			return null;

		}
		return user;

    }

	//预约服务提前一小时提醒
	public String sendAppointmentRemind() {
		String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
		if (officeAppId == null) {
			logger().info("店铺" + getShopId() + "没有关注公众号");
			return null;
		}
		List<StoreAppointmentRemindVo> serviceOrderList = serviceOrder.getDealServiceOrder();
		logger().info("serviceOrderList大小"+serviceOrderList.size());
		String first = "您好，您的预约即将到期";
		String remake = "感谢您的使用";
		for (StoreAppointmentRemindVo serviceOrderRecord : serviceOrderList) {
			List<Integer> userIdList = new ArrayList<Integer>();
			UserRecord wxUserInfo = checkMp(serviceOrderRecord.getWxUnionId(), officeAppId);
			if(null==wxUserInfo) {
				continue;
			}
			userIdList.add(wxUserInfo.getUserId());
			String page = "pages/appointinfo/appointinfo?order_sn=" + serviceOrderRecord.getOrderSn();
			String orderSn = serviceOrderRecord.getOrderSn();
			String serviceDate = serviceOrderRecord.getServiceDate();
			String servicePeriod = serviceOrderRecord.getServicePeriod();
			String[][] data = new String[][] { { first, "#173177" }, { orderSn, "#173177" },
					{ serviceDate + " " + servicePeriod.substring(0, 5), "#173177" }, { remake, "#173177" } };
			RabbitMessageParam param = RabbitMessageParam.builder()
					.mpTemplateData(
							MpTemplateData.builder().config(MpTemplateConfig.APPOINTMENT_REMINDER).data(data).build())
					.page(page).shopId(getShopId()).userIdList(userIdList).type(RabbitParamConstant.Type.BOOKING_EXPIRED)
					.build();
			logger().info("预约服务提前一小时提醒准备发");
			saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
					TaskJobEnum.SEND_MESSAGE.getExecutionType());
		}
		return null;
	}

    //商家自定义模板消息 定时发送，持续发送
	public String  sendTemplateMessage() {
		// TODO Auto-generated method stub
		String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
		if (officeAppId == null) {
			logger().info("店铺" + getShopId() + "没有关注公众号");
			return null;
		}
		// 尾款未支付前24小时提醒
		logger().info(getShopId()+"开始：尾款未支付前24小时提醒");
		toSendPreSaleMessage(24, officeAppId, ZERO);
		logger().info(getShopId()+"结束：尾款未支付前24小时提醒");

        // 尾款未支付前2小时提醒
		logger().info(getShopId()+"开始：尾款未支付前2小时提醒");
		toSendPreSaleMessage(2, officeAppId, ZERO);
		logger().info(getShopId()+"结束：尾款未支付前2小时提醒");

        // 尾款支付时间开始发送通知
		logger().info(getShopId()+"开始：尾款未支付前0小时提醒");
		toSendPreSaleMessage(0, officeAppId, NOTIFY_HELP_FROM_FRIENDS);
		logger().info(getShopId()+"结束：尾款未支付前0小时提醒");

        return officeAppId;

    }
	/**
	 * 尾款未支付前N小时提醒
	 * @param hours
	 * @param officeAppId
	 */
	private void toSendPreSaleMessage(Integer hours, String officeAppId, Byte type) {
		List<PreSaleVo> preSaleList = preSale.getPreSaleListByHour(hours, type);
		for (PreSaleVo preSaleVo : preSaleList) {
			Result<OrderInfoRecord> orderList = orderInfoService.getNoPayOrderByIdentityId(preSaleVo.getId());
			for (OrderInfoRecord orderInfoRecord : orderList) {
				UserRecord user = userService.getUserByUserId(orderInfoRecord.getUserId());
				List<Integer> userIdList = new ArrayList<Integer>();
				UserRecord wxUserInfo = checkMp(user.getWxUnionId(), officeAppId);
				if (null == wxUserInfo) {
					continue;
				}
				userIdList.add(wxUserInfo.getUserId());
				String first = "";
				String remake = "";
				if (type.equals(ZERO)) {
					first = "尾款支付提醒";
					remake = "您有待付尾款的订单";
				}
				if (type.equals(NOTIFY_HELP_FROM_FRIENDS)) {
					first = "未支付订单提醒";
					remake = "您有未支付的订单";
				}
				String goodsNameForPay = orderPayService.getGoodsNameForPay(orderInfoRecord.getOrderSn());
				// 发送
				String page = "pages/orderinfo/orderinfo?order_sn=" + orderInfoRecord.getOrderSn();
				String orderSn = orderInfoRecord.getOrderSn();
				String keyword2 = String.valueOf(orderInfoRecord.getMoneyPaid());
				String[][] data = new String[][] { { first, "#173177" }, { orderSn, "#173177" },
						{ keyword2, "#173177" }, { remake + goodsNameForPay, "#173177" } };
				RabbitMessageParam param = RabbitMessageParam.builder()
						.mpTemplateData(
								MpTemplateData.builder().config(MpTemplateConfig.PAYMENT_REMINDER).data(data).build())
						.page(page).shopId(getShopId()).userIdList(userIdList)
						.type(RabbitParamConstant.Type.ORDER_NO_PAY).build();
				logger().info("尾款未支付前" + hours + "小时提醒");
				saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
						TaskJobEnum.SEND_MESSAGE.getExecutionType());
			}
		}

	}

    /**
	 * 发送模板消息和助力失败改变状态
	 * @return
	 */
	public String friendPromoteCommand() {
		String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
		if (officeAppId == null) {
			logger().info("店铺" + getShopId() + "没有关注公众号");
			return null;
		}
		promoteSendMessage(officeAppId);
		return officeAppId;

    }

	//发送模板消息和助力失败改变状态
	private void promoteSendMessage(String officeAppId) {
		//活动结束前一小时
		logger().info("开始活动结束前一小时");
		List<FriendPromoteSelectVo> hoursList = friendPromoteService.getLaunchListByHour(1);
		for (FriendPromoteSelectVo item : hoursList) {
			sendMessage(NOTIFY_HELP_PROCESS, item, officeAppId);
		}
		logger().info("结束活动结束前一小时");
		//助力失败
		logger().info("开始助力失败");
		List<FriendPromoteSelectVo> promoteFailedList = friendPromoteService.getPromoteFailedList(-24);
		for (FriendPromoteSelectVo item : promoteFailedList) {
			int upPromoteInfo = friendPromoteService.upPromoteInfo(NOTIFY_HELP_SUCCESS, item.getId());
			if(upPromoteInfo==1) {
				sendMessage(NOTIFY_HELP_FAIL, item, officeAppId);
			}

            //发放失败奖励
			if (item.getFailedSendType().equals(NOTIFY_HELP_FROM_FRIENDS)) {
				logger().info("发放失败奖励");
				MrkingVoucherRecord infoById = couponGiveService.getInfoById(item.getFailedSendContent());
				if(infoById==null) {
					logger().info("优惠券"+item.getFailedSendContent()+"不存在，找管理员");
				}else {
					CouponGiveQueueParam param=new CouponGiveQueueParam(getShopId(), Arrays.asList(item.getUserId()), item.getId(), new String[] {item.getFailedSendContent().toString()}, ZERO, (byte)17);
                    couponGiveService.handlerCouponGive(param);
				}
			}

            if (item.getFailedSendType().equals(NOTIFY_HELP_PROCESS)) {
//				UserScoreVo data=new UserScoreVo();
//				//data.setRemark("好友助力失败奖励积分");
//				data.setRemarkCode(RemarkTemplate.FRIENDS_HELP_FAIL.code);
//				data.setIdentityId(String.valueOf(item.getPromoteId()));
//				data.setScore(item.getFailedSendContent());
//				data.setUserId(item.getUserId());
//				Integer addUserScore = scoreService.addUserScore(data,"0", FOUR, ONE);
				ScoreParam param=new ScoreParam();
				param.setRemarkCode(RemarkTemplate.FRIENDS_HELP_FAIL.code);
				param.setOrderSn(String.valueOf(item.getPromoteId()));
				param.setScore(item.getFailedSendContent());
				param.setUserId(item.getUserId());
				try {
					scoreService.updateMemberScore(param, 0, RecordTradeEnum.TYPE_SCORE_POWER.val(), NOTIFY_HELP_FROM_FRIENDS);
				} catch (MpException e) {
					logger().info("创建用户积分记录失败");
					continue;
				}
//				if(addUserScore==1) {
					UserRecord user = userService.getUserByUserId(item.getUserId());
					record.insertRecord(Arrays.asList(new Integer[] { RecordContentTemplate.MEMBER_INTEGRALT.code }),
							new String[] { String.valueOf(item.getUserId()), user.getUsername(),
									"+"+String.valueOf(item.getFailedSendContent()) });
//				}
			}

        }
		logger().info("结束助力失败");
		logger().info("开始助力失效前一小时");
		//助力失效前一小时
		List<FriendPromoteSelectVo> promoteWaitReceiveList = friendPromoteService.getPromoteWaitReceiveList(1);
		for (FriendPromoteSelectVo item : promoteWaitReceiveList) {
			sendMessage(NOTIFY_AWARD_NO_GET, item, officeAppId);
		}
		logger().info("结束助力失效前一小时");

        logger().info("开始助力失效修改状态");
		List<FriendPromoteSelectVo> promoteWaitReceiveList2 = friendPromoteService.getPromoteWaitReceiveList(0);
		for (FriendPromoteSelectVo item : promoteWaitReceiveList2) {
			friendPromoteService.upPromoteInfo(NOTIFY_HELP_FAIL, item.getId());
		}
		logger().info("结束助力失效修改状态");
	}

    //发送好友助力中奖结果
	private String sendPromoteDrawMessage(FriendPromoteSelectVo vo,String officeAppId) {
		String json = vo.getRewardContent();
		FpRewardContent rewardContent = Util.json2Object(json,FpRewardContent.class, false);
		String goodsName=null;
		if(vo.getRewardType().equals(NOTIFY_HELP_PROCESS)) {
			Integer rewardIds = rewardContent.getRewardIds();
			if(rewardIds!=null) {
				MrkingVoucherRecord coupon = couponGiveService.getInfoById(rewardIds);
				//TODO sendPromoteDrawMessage
                String value = coupon.getActCode().equals(CouponConstant.ACT_CODE_VOUCHER) ? "元" : "折";
				goodsName=String.valueOf(coupon.getDenomination())+value+"优惠券";
			}
		}else {
		    Integer goodsId = db().select(GOODS_SPEC_PRODUCT.GOODS_ID).from(GOODS_SPEC_PRODUCT)
                .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(rewardContent.getGoodsIds()))
                .fetchOne().into(Integer.class);
			Optional<GoodsRecord> goodsById = goodsService.getGoodsById(goodsId);
			if(goodsById.get()!=null) {
				goodsName=goodsById.get().getGoodsName();
			}
		}
		String page="pages1/promoteinfo/promoteinfo?actCode="+vo.getActCode()+"&launch_id="+String.valueOf(vo.getId());

        List<Integer> userIdList = new ArrayList<Integer>();
		UserRecord user = userService.getUserByUserId(vo.getUserId());
		UserRecord userRecord = checkMp(user.getWxUnionId(), officeAppId);
		if (null == userRecord) {
			logger().info("店铺："+getShopId()+"用户userId："+vo.getUserId()+"活动Id："+vo.getId()+"用户没有关注公众号");
			return null;
		}
		userIdList.add(user.getUserId());
		String title="恭喜您中奖了";
		String remark="将尽快为您发货";
		String[][] data = new String[][] { { title, "#173177" }, {goodsName, "#173177" },{"恭喜中奖", "#173177" },{ remark, "#173177" },
			{ DateUtils.getLocalDateFormat(), "#173177" }};
		RabbitMessageParam param = RabbitMessageParam.builder()
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.ACTIVITY_CONFIG).data(data).build())
				.page(page).shopId(getShopId()).userIdList(userIdList)
				.type(RabbitParamConstant.Type.LOTTERY_TEAM).build();
		saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
		return remark;
	}

    public String sendMessage(Byte type,FriendPromoteSelectVo vo,String officeAppId) {
		logger().info("sendMessage");
		String title = "";
		String content = "";
		if(type.equals(NOTIFY_HELP_SUCCESS)) {
			logger().info("type为4");
			sendPromoteDrawMessage(vo, officeAppId);
		}else {
			switch (type) {
			case NOTIFY_HELP_FROM_FRIENDS:
				title = "好友助力通知";
				content = "好友帮您助力啦！快去看看吧！";
				break;
			case NOTIFY_HELP_PROCESS:
				title = "助力进度通知";
				content = "活动即将结束，赶快邀请好友帮忙助力吧！";
				break;
			case NOTIFY_HELP_FAIL:
				title = "助力失败通知";
				content = "很遗憾，本次活动助力失败，来看看其他活动吧~";
				break;
			case NOTIFY_AWARD_NO_GET:
				title = "奖励未领取通知";
				content = "您有奖励即将失效，快来领取吧！";
				break;
			default:
				break;
			}
			if(type.equals(NOTIFY_HELP_FROM_FRIENDS)) {
                if (!processHelpFromFriends(vo, officeAppId, title, content)) {
                    return null;
                }
            }else {
				logger().info("type不为1，为"+type);
				sendPromoteResultMessage(type, vo, officeAppId, title, content);
			}

		}
		return content;
	}

    /**
     * 处理好友助力通知
     * @param vo
     * @param officeAppId
     * @param title
     * @param content
     * @return
     */
    private boolean processHelpFromFriends(FriendPromoteSelectVo vo, String officeAppId, String title, String content) {
        logger().info("type为1");
        logger().info("店铺："+getShopId()+"用户userId："+vo.getUserId()+"活动Id："+vo.getId()+"状态是1,发送小程序");
        MpAuthShopRecord authShop = saas.shop.mp.getAuthShopByShopId(getShopId());
        if (null == authShop) {
            logger().info("店铺："+getShopId()+"没有绑定小程序");
            return false;
        }
        Integer tid = SubscribeMessageConfig.getTid(SubcribeTemplateCategory.INVITE_SUCCESS);
        if(tid==null) {
            logger().info("SubcribeTemplateCategory中没有定义相关的类型");
            return false;
        }
        boolean canUse = subscribeMessageService.getCanUse(vo.getUserId(), tid);
        String page = "pages1/promoteinfo/promoteinfo?actCode=" + vo.getActCode() + "&launch_id=" + vo.getId();
        String name2 = "已有好友帮忙助力";
        String date = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, vo.getSuccessTime());
        if(!canUse) {
            logger().info("店铺："+getShopId()+"用户userId："+vo.getUserId()+"活动Id："+vo.getId()+"不能发送小程序，转发送公众号");
            //ACTIVITY_CONFIG
            List<Integer> userIdList = new ArrayList<Integer>();
            UserRecord user = userService.getUserByUserId(vo.getUserId());
            UserRecord wxUserInfo = checkMp(user.getWxUnionId(), officeAppId);
            if (null == wxUserInfo) {
                logger().info("店铺："+getShopId()+"用户userId："+vo.getUserId()+"活动Id："+vo.getId()+"用户没有关注公众号");
                return false;
            }
            userIdList.add(user.getUserId());
            String[][] data = new String[][] { { title, "#173177" }, {name2, "#173177" },
                { vo.getActName(), "#173177" }, { content, "#173177" }, { date, "#173177" },{"","#173177"}};
                //发的公众号
                RabbitMessageParam param = RabbitMessageParam.builder()
                        .mpTemplateData(
                                MpTemplateData.builder().config(MpTemplateConfig.ACTIVITY_CONFIG).data(data).build())
                        .page(page).shopId(getShopId()).userIdList(userIdList)
                        .type(RabbitParamConstant.Type.LOTTERY_TEAM).build();
                saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
            return false;
        }
        logger().info("店铺："+getShopId()+"用户userId："+vo.getUserId()+"活动Id："+vo.getId()+"发送小程序");
        String[][] data = new String[][] { { vo.getActName() }, { name2 }, { date } };
        List<Integer> userIdList = new ArrayList<Integer>();
        userIdList.add(vo.getUserId());
        MaSubscribeData buildData = MaSubscribeData.builder().data307(data).build();
        //发的小程序
        RabbitMessageParam param = RabbitMessageParam.builder()
                .maTemplateData(
                        MaTemplateData.builder().config(SubcribeTemplateCategory.INVITE_SUCCESS).data(buildData).build())
                .page(page).shopId(getShopId())
                .userIdList(userIdList)
                .type(RabbitParamConstant.Type.INVITE_SUCCESS_FRIEND_PROMOTE).build();
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
        return true;
    }

    public String sendPromoteResultMessage(Byte type,FriendPromoteSelectVo vo,String officeAppId,String title,String content) {
    	logger().info("传入参数：type：{}，title：{},content:{}",type,title,content);
		FriendPromoteSelectVo userLaunchInfo = friendPromoteService.getUserLaunchInfo(vo.getId());
		if(userLaunchInfo!=null) {
			String page="pages1/promoteinfo/promoteinfo?actCode="+userLaunchInfo.getActCode()+"&launch_id="+String.valueOf(vo.getId());
			List<Integer> userIdList = new ArrayList<Integer>();
			UserRecord user = userService.getUserByUserId(vo.getUserId());
			UserRecord wxUserInfo = checkMp(user.getWxUnionId(), officeAppId);
			if (null == wxUserInfo) {
				logger().info("店铺："+getShopId()+"用户userId："+vo.getUserId()+"活动Id："+vo.getId()+"用户没有关注公众号");
				return null;
			}
			userIdList.add(vo.getUserId());
			String remake="点击查看详情";
			String[][] data = new String[][] { { "", "#173177" }, {title, "#173177" },
				{ vo.getActName(), "#173177" }, { content, "#173177" }, { DateUtils.getLocalDateFormat(), "#173177" },{remake,"#173177"}};
				//发的公众号
			RabbitMessageParam param = RabbitMessageParam.builder()
					.mpTemplateData(
							MpTemplateData.builder().config(MpTemplateConfig.ACTIVITY_CONFIG).data(data).build())
					.page(page).shopId(getShopId()).userIdList(userIdList)
					.type(RabbitParamConstant.Type.LOTTERY_TEAM).build();
			saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
		}
		return content;
	}
}
