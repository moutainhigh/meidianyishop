package com.meidianyi.shop.service.shop.member.card;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.builder.UserCardRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;

/**
 * 	普通卡操作
 * @author 黄壮壮
 *
 */
@Service
public class NormalBaseCardOpt extends BaseCardOpt {

	public NormalBaseCardOpt() {
		super(CardConstant.MCARD_TP_NORMAL);
	}

	@Override
	protected String sendCard(Integer userId,Integer cardId,boolean isActivate) {
		logger().info("发放普通卡");
		//	获取卡
		MemberCardRecord card = cardService.getCardById(cardId);
		UserCardRecord uCard = UserCardRecordBuilder.create()
			.userId(userId)
			.cardId(cardId)
			.cardNo(cardService.generateCardNo(cardId))
			.freeLimit(card.getFreeshipLimit())
			.freeNum(card.getFreeshipNum())
			.createTime(DateUtils.getLocalDateTime())
			.expireTime(userCardService.calcCardExpireTime(card))
			.build();


		//	开卡送卡余额
		if(card.getSendMoney() != null) {
			uCard.setMoney(new BigDecimal(card.getSendMoney()));
		}

		//	设置激活
		if(isActivate || !CardUtil.isNeedActive(card.getActivation())) {

			uCard.setActivationTime(DateUtils.getLocalDateTime());
			//	开卡送优惠券
			cardService.sendCoupon(userId,cardId);
		}

		Integer result = userCardService.insertRow(uCard);
		logger().info(String.format("成功向ID为%d的用户，发送了%d张普通会员卡：%s", userId, result,card.getCardName()));

		//	开卡送积分
		userCardService.addUserCardScore(userId, card);
		//	赠送卡余额记录
		userCardService.addChargeMoney(card, uCard);
		//	同步打标签
		addAcitivityTag(userId,card);

		//	发送模板消息
		userCardService.sendScoreTemplateMsg(card);
		return uCard.getCardNo();
	}

	@Override
	public boolean canSendCard(Integer userId, Integer cardId) {
		//	用户目前是否拥有该类型会员卡
		UserCardRecord card = userCardDao.getUsableUserCard(userId, cardId);
		return card == null;
	}

}
