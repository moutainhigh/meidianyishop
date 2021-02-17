package com.meidianyi.shop.service.shop.member.card;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.MemberBasicInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.card.dao.CardFullDetail;
import com.meidianyi.shop.service.pojo.shop.member.card.show.CardUseCondition;
import com.meidianyi.shop.service.shop.card.msg.CardMsgNoticeService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.member.tag.UserTagService;

/**
 *	 卡操作抽象类
 * @author 黄壮壮
 *
 */
@Service
public abstract class BaseCardOpt extends ShopBaseService{
	@Autowired protected UserTagService userTagSvc;
	@Autowired protected MemberCardService cardService;
	@Autowired protected UserCardService userCardService;
	@Autowired protected UserCardDaoService userCardDao;
	@Autowired protected MemberService memberSvc;
	@Autowired protected CardMsgNoticeService cardMsgSvc;
	/**	卡类型	*/
	private Byte type;

	public BaseCardOpt(Byte type) {
		this.type = type;
	}

	public Byte getType() {
		return this.type;
	}
	public void setType(Byte type) {
		this.type = type;
	}

	/**
	 * 	发卡
	 * @param userId 用户Id
	 * @param cardId 卡Id
	 * @param isActivate 是否直接激活 true: 是，false不是
	 * @return 卡号 null为发卡失败
	 */
	public final String handleSendCard(Integer userId,Integer cardId,boolean isActivate) {

		if(canSendCard(userId,cardId)) {
			String cardNo = sendCard(userId,cardId,isActivate);
			if(!StringUtils.isBlank(cardNo)) {
				//	订阅消息
				sendMessage(cardNo);
			}
			return cardNo;
		}
		return null;
	}


	/**
	 * 	发卡
	 * @param userId 用户Id
	 * @param cardId 卡Id
	 * @param isActivate 是否直接激活 true: 是，false不是
	 * @return 卡号
	 */
	protected abstract String sendCard(Integer userId,Integer cardId,boolean isActivate);

	/**
	 * 	是否可以发放此卡给用户
	 * @param userId
	 * @param cardId
	 * @return true 可以 false 不可以
	 */
	public abstract boolean canSendCard(Integer userId,Integer cardId);

	/**
	 * 	同步用户打标签
	 * @param userId
	 * @param mCard
	 */
	protected void addAcitivityTag(Integer userId,MemberCardRecord mCard) {
		logger().info("同步用户打标签");

		List<Integer> tagIdList = cardService.cardDetailSvc.getCardTag(mCard).getCardTagId();
		if(tagIdList!=null && tagIdList.size()>0) {
			userTagSvc.addActivityTag(userId, tagIdList, UserTagService.SRC_CARD, mCard.getId());
		}
	}

	/**
	 * 发卡成功模板消息
	 */
	protected void sendMessage(String cardNo) {
		CardFullDetail cardDetail = cardService.getCardDetailByNo(cardNo);
		MemberCardRecord memberCard = cardDetail.getMemberCard();
		UserCardRecord userCard = cardDetail.getUserCard();
		MemberBasicInfoVo user = memberSvc.getMemberInfo(userCard.getUserId());
		String expireTime = null;
		if(CardUtil.isCardTimeForever(memberCard.getExpireType())) {
			expireTime="永久有效";
		}else {
			expireTime = String.valueOf(userCard.getExpireTime().toLocalDateTime().toLocalDate());
		}
		Byte cardType = memberCard.getCardType();
		String cardTypeText="";
		if(CardUtil.isLimitCard(cardType)) {
			cardTypeText = "限次卡";
		}else if(CardUtil.isNormalCard(cardType)){
			cardTypeText = "普通卡";
		}else if(CardUtil.isGradeCard(cardType)){
			cardTypeText = "等级卡";
		}
		cardMsgSvc.sendCardMsgNotice(cardNo, memberCard, user, expireTime, cardTypeText);
	}


	/**
	 * 	获取当前会员卡是否可以使用
	 */
	public CardUseCondition judegeCardUsable(String cardNo) {
		CardFullDetail cardDetail = cardService.getCardDetailByNo(cardNo);
		return judegeCardUsable(cardDetail.getUserCard(),cardDetail.getMemberCard());
	}

	/**
	 * 	获取目前会员卡是否可以使用
	 */
	public CardUseCondition judegeCardUsable(UserCardRecord userCard,MemberCardRecord memberCard) {
		logger().info("获取当前会员卡是否可以使用");
		CardUseCondition res = new CardUseCondition();
		if(CardUtil.isCardFixTime(memberCard.getExpireType())) {
			if(memberCard.getStartTime().after(DateUtils.getLocalDateTime())) {
				//	本卡还未到开始使用时间
				res.setUsable(Boolean.FALSE);
				res.setErrorCode(JsonResultCode.MSG_CARD_BEFORE_START_TIME);
				return res;
			}
		}

		if(CardUtil.isNeedActive(memberCard.getActivation())) {
			if(null == userCard.getActivationTime()) {
				//	本卡尚未激活
				res.setUsable(Boolean.FALSE);
				res.setErrorCode(JsonResultCode.MSG_CARD_NOT_ACTIVE);
				return res;
			}
		}

		if(null != userCard.getExpireTime()) {
			if(userCard.getExpireTime().before(DateUtils.getLocalDateTime())) {
				//	本卡已过期
				res.setUsable(Boolean.FALSE);
				res.setErrorCode(JsonResultCode.MSG_CARD_ALREADY_EXPIRED);
			}
		}

		res.setErrorCode(JsonResultCode.CODE_SUCCESS);
		res.setUsable(Boolean.TRUE);
		return res;
	}

}
