package com.meidianyi.shop.service.shop.member.wxapp;

import java.util.List;

import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.CardReceiveCodeRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.builder.CardReceiveCodeRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.exception.CardReceiveFailException;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ReceiveCardParam;
import com.meidianyi.shop.service.shop.member.CardReceiveCodeService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.UserCardService;
/**
 * @author 黄壮壮
 * 	领取会员卡服务
 */
@Service
public class WxAppCardReceiveSerive extends ShopBaseService {
	@Autowired
	private MemberCardService memberCardServie;
	@Autowired
	private CardReceiveCodeService cardReceiveCodeService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private JedisManager jedis;
	/**
	 * 	领取会员卡通过码
	 * @throws MpException
	 */
	public void receiveCard(ReceiveCardParam param) throws MpException {
		MemberCardRecord mCard = memberCardServie.getCardById(param.getCardId());

		if(!CardUtil.isValidCard(mCard)) {
			logger().info("无效卡");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_INVALID);
		}

		if(!CardUtil.isNeedReceiveCode(mCard.getIsPay())) {
			logger().info("领取该卡不需要领取码");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_NOCODE);
		}

		CardReceiveCodeRecord record = null;
		if(CardUtil.isReceiveByCode(mCard.getReceiveAction())) {
			record = checkReceiveCardByCode(param);
		}else {
			record = checkReceiveCardByPwd(param);
		}

		String cacheKey = String.format("receive_card_code:%s%s_%s",getShopId(),param.getCardId(),param.getCode());
		String cacheVal = jedis.get(cacheKey);
		if(!StringUtils.isBlank(cacheVal)) {
			logger().info("正在生成卡，请勿提交");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_GENERATE);
		}
		jedis.set(cacheKey, "1", 2);
		String cardNo = null;

		List<String> cardNoList = userCardService.addUserCard(param.getUserId(), param.getCardId());
		if(cardNoList!=null && cardNoList.size()>0) {
			cardNo = cardNoList.get(0);
		}


		if(StringUtils.isBlank(cardNo)) {
			logger().info("您已经有此会员卡");
			jedis.delete(cacheKey);
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_ALREADYHAS);
		}

		logger().info("领取码已使用，更新领取码");
		cardReceiveCodeService.updateRow(
					CardReceiveCodeRecordBuilder
					.create()
					.id(record.getId())
					.userId(param.getUserId())
					.receiveTime(DateUtils.getLocalDateTime())
					.build()
				);

		// 开卡送优惠券
		memberCardServie.sendCoupon(param.getUserId(), param.getCardId());

	}

	/**
	 * 领取会员卡通过卡号加密码
	 */
	private CardReceiveCodeRecord checkReceiveCardByPwd(ReceiveCardParam param) throws CardReceiveFailException{
		if(StringUtils.isBlank(param.getCardNo())
				|| StringUtils.isBlank(param.getCardPwd())) {
			logger().info("请填写卡号或密码");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_PWD);
		}
		CardReceiveCodeRecord record = cardReceiveCodeService.getUserHasCode(param.getUserId(),param.getCardNo(),param.getCardPwd());
		if(record != null) {
			logger().info("您已经领取会员卡，是否前往查看");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_GOTOLOOK);
		}
		CardReceiveCodeRecord cardCodeInfo = cardReceiveCodeService.getCardPwd(param.getCardId(),param.getCardNo(),param.getCardPwd());
		if(cardCodeInfo == null) {
			logger().info("请输入有效的卡号或密码");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_VALIDPWD);
		}
		return cardCodeInfo;
	}

	/**
	 * 领取会员卡通过领取码
	 * @throws CardReceiveFailException
	 */
	private CardReceiveCodeRecord checkReceiveCardByCode(ReceiveCardParam param) throws CardReceiveFailException {
		logger().info("通过领取码领取会员卡");
		CardReceiveCodeRecord record = cardReceiveCodeService.getUserHasCode(param.getUserId(), param.getCode());
		if(record != null) {
			logger().info("您已经领取会员卡，是否前往查看");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_GOTOLOOK);
		}

		CardReceiveCodeRecord cardCodeInfo = cardReceiveCodeService.getCardCode(param.getCardId(), param.getCode());

		if(cardCodeInfo == null) {
			logger().info("没有检测到用户输入的领取码,或该领取码已经被使用");
			throw new CardReceiveFailException(JsonResultCode.CODE_CARD_RECEIVE_VALIDCODE);
		}
		return cardCodeInfo;
	}
}
