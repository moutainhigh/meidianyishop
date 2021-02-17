package com.meidianyi.shop.service.shop.member.card;


import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.builder.UserCardRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;

/**
 * 	等级卡操作
 * @author 黄壮壮
 *
 */
@Service
public class GradeBaseCardOpt extends BaseCardOpt {
	public GradeBaseCardOpt() {
		super(CardConstant.MCARD_TP_GRADE);
	}

	@Override
	protected String sendCard(Integer userId, Integer cardId, boolean isActivate) {
		//	是否拥有会员卡
		if(userCardService.isHasAvailableGradeCard(userId)) {
			//	设置卡等级
			logger().info("更换等级卡卡号");
			MemberCardRecord oldCard = userCardService.getUserGradeCard(userId);
			MemberCardRecord newCard = cardService.getCardById(cardId);
			if(oldCard != null && newCard != null) {
				// 检测是否为同一张卡
				if(!oldCard.getId().equals(cardId)) {
					userCardService.changeUserGradeCard(userId, oldCard, newCard, "",isActivate);
					return userCardService.getCardNoByUserAndCardId(userId,cardId);
				}
			}
			return null;

		}else {
			//	直接发卡
			logger().info("直接发卡");
			MemberCardRecord card = cardService.getCardById(cardId);

			UserCardRecord newCard = UserCardRecordBuilder.create()
				.userId(userId)
				.cardId(cardId)
				.cardNo(userCardService.getRandomCardNo(cardId))
				.createTime(DateUtils.getLocalDateTime())
				.expireTime(userCardService.calcCardExpireTime(card))
				.build();
			if(isActivate || !CardUtil.isNeedActive(card.getActivation())) {
				newCard.setActivationTime(DateUtils.getLocalDateTime());
			}

			Integer result = userCardService.insertRow(newCard);
			logger().info(String.format("成功向ID为%d的用户，发送了%d张等级会员卡：%s", userId, result,card.getCardName()));

			//	开卡赠送积分
			userCardService.addUserCardScore(userId,card);

			return newCard.getCardNo();
		}
	}

	@Override
	public boolean canSendCard(Integer userId, Integer cardId) {
		// 等待产品规划发放等级卡业务->目前admin后台权限是放开的

		return true;
	}
}
