package com.meidianyi.shop.service.shop.member.card;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.builder.UserCardRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.dao.CardFullDetail;

import static com.meidianyi.shop.service.pojo.shop.member.card.base.UserCardConstant.SOURCE_GIVE_WAY;
import static com.meidianyi.shop.service.pojo.shop.member.card.base.UserCardConstant.SOURCE_NORMAL;
/**
 * 	限次卡操作
 * @author 黄壮壮
 *
 */
@Service
public class LimitBaseCardOpt extends BaseCardOpt {

	public LimitBaseCardOpt() {
		super(CardConstant.MCARD_TP_LIMIT);
	}


	@Override
	protected String sendCard(Integer userId,Integer cardId,boolean isActivate) {
		logger().info("发送限次会员卡");
		MemberCardRecord card = cardService.getCardById(cardId);
		return getUserCardRecord(userId,card,null,false,isActivate);
	}

	/**
	 * 	领取转赠的限次卡
	 * @param userId 领取的用户ID
	 * @param cardNo 转赠的卡号
	 * @return 新卡号
	 */
	public String handleSendGiveAwayCard(Integer userId,Integer cardId,String cardNo) {
		logger().info("领取转赠的限次卡");
		if(canSendCard(userId,cardId)) {
			CardFullDetail cardDetail = cardService.getCardDetailByNo(cardNo);
			UserCardRecord userCardRecord = cardDetail.getUserCard();
			MemberCardRecord memberCardRecord = cardDetail.getMemberCard();
			return getUserCardRecord(userId,memberCardRecord,userCardRecord,true,false);
		}
		return null;
	}

	/**
	 * 	获取用户卡记录
	 * @param userId
	 * @param mCard
	 * @param userCardRecord
	 * @param isGiveWay 是否为转赠
	 * @isActivate 是否直接激活
	 */
	private String getUserCardRecord(Integer userId,MemberCardRecord mCard,UserCardRecord uCard,boolean isGiveWay,boolean isActivate) {
		UserCardRecord newCard = UserCardRecordBuilder.create()
				.userId(userId)
				.cardId(mCard.getId())
				.cardNo(cardService.generateCardNo(mCard.getId()))
				.createTime(DateUtils.getLocalDateTime())
				.expireTime(userCardService.calcCardExpireTime(mCard))
				.build();

		if(isGiveWay) {
			//	转赠获取上一个用户卡的权益值
			newCard.setCardSource(SOURCE_GIVE_WAY);
			//	剩余门店兑换次数
			if(uCard.getSurplus()!=null) {
				newCard.setSurplus(uCard.getSurplus());
			}
			//	剩余适用商品兑换次数
			if(uCard.getExchangSurplus()!=null) {
				newCard.setExchangSurplus(uCard.getExchangSurplus());
			}

			if(CardUtil.isCardGiveAwayForeverTimes(mCard.getMostGiveAway())) {
				//	无限转赠
				newCard.setGiveAwaySurplus(Integer.MAX_VALUE);
			}else {
				//	剩余转赠次数
				newCard.setGiveAwaySurplus(uCard.getGiveAwaySurplus()-1);
			}
		}else {
			newCard.setCardSource(SOURCE_NORMAL);
			//	门店兑换次数
			if(CardUtil.canUseInStore(mCard.getStoreUseSwitch())) {
				newCard.setSurplus(mCard.getCount());
			}
			//	适用商品兑换次数
			if(CardUtil.canExchangGoods(mCard.getIsExchang())) {
				newCard.setExchangSurplus(mCard.getExchangCount());
			}
			//	转赠次数
			if(CardUtil.isCardGiveWway(mCard.getCardGiveAway())) {
				newCard.setGiveAwaySurplus(mCard.getMostGiveAway());
			}
		}
		//	激活设置
		if(isActivate || !CardUtil.isNeedActive(mCard.getActivation())) {
			newCard.setActivationTime(DateUtils.getLocalDateTime());
		}

		Integer result = userCardService.insertRow(newCard);
		if(result>0) {
			logger().info(String.format("成功向ID为%d的用户，发送了%d张限次会员卡：%s", userId, result,mCard.getCardName()));
			userCardService.addChargeMoney(mCard,newCard);
			addAcitivityTag(userId,mCard);
			return newCard.getCardNo();
		}else{
			logger().info("领取限次卡失败");
			return null;
		}

	}


	@Override
	public boolean canSendCard(Integer userId, Integer cardId) {
		logger().info("检测是否能够发放该先此限次卡");
		//	获取要发送卡
		MemberCardRecord card = cardService.getCardById(cardId);
		if(card == null || CardUtil.isCardDeleted(card.getDelFlag())) {
			logger().info("该卡不存在或已经删除");
			return false;
		}
		//	该用户已经领取该限次卡的张数
		int personCardNum = userCardDao.getNumHasSendUser(userId, card.getId());
		//	该卡已经发放的总次数
		int totalSendCard = userCardDao.getHasSend(card.getId());

		//	校验是否满足限次卡的领取条件
		boolean personGetFlag = true;
		boolean sendTotalCardFlag = true;

		if(card.getLimit()>0 && personCardNum>=card.getLimit()) {
			logger().info("个人领取次数达到上限");
			personGetFlag = false;
		}
		if(card.getStock()>0 && totalSendCard >= card.getStock()) {
			logger().info("卡发放达到上限");
			sendTotalCardFlag = false;
		}

		return personGetFlag && sendTotalCardFlag;
	}


	/**
	 * 	判断这张用户卡是否可以继续转赠
	 * @param userCardRecord
	 * @param memberCardRecord
	 * @param rootControl 是否统一控制开放转赠权限 true 是，false 否
	 * @return true 用户卡可以继续转赠  |  false 用户卡不可以继续转赠
	 */
	public boolean canGiveAway(UserCardRecord userCard,MemberCardRecord memberCard,boolean rootControl) {
		Byte cardSource = userCard.getCardSource();
		//	卡是否允许转赠
		if(rootControl || CardUtil.isCardGiveWway(memberCard.getCardGiveAway())) {
			//	正常的卡
			if(CardUtil.isCardSourceNormal(cardSource)) {
				return true;
			}
			//	通过转赠获取的卡
			if(CardUtil.isCardSourceGiveWay(cardSource) && CardUtil.isCardGiveContinue(memberCard.getCardGiveContinue())) {
				//	检查是否为无限转赠或者是否还有有转赠次数
				if(CardUtil.isCardGiveAwayForeverTimes(memberCard.getMostGiveAway()) || userCard.getGiveAwaySurplus()>0) {
					return true;
				}
			}
		}
		return false;
	}
}
