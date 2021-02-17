package com.meidianyi.shop.service.shop.member.card;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.card.GradeConditionJson;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.member.UserCardService;

/**
 *	 普通用户领卡操作
 * @author 黄壮壮
 *
 */
@Service
public class BaseCardUserOpt extends BaseCardOpt {
	@Autowired
	private MemberCardService mCardSvc;
	@Autowired
	private UserCardService uCardSvc;
	@Autowired
	private ScoreService scoreSvc;
	
	private BaseCardOpt decorate;
	
	
	public BaseCardUserOpt() {
		super(null);
	}

	
	public void setDecorate(BaseCardOpt baseCardOpt) {
		this.setType(baseCardOpt.getType());
		this.decorate = baseCardOpt;
	}
	
	@Override
	protected String sendCard(Integer userId, Integer cardId, boolean isActivate) {
		
	
		
		return decorate.sendCard(userId, cardId, isActivate);
	}

	@Override
	public boolean canSendCard(Integer userId, Integer cardId) {
		MemberCardRecord card = mCardSvc.getCardById(cardId);
		
		//	等级卡
		if(CardUtil.isGradeCard(card.getCardType())) {
			return canSendGradeCard(userId,cardId);
		}
		
		//	添加额外附加条件
		
		//	需要购买
		if(CardUtil.isNeedToBuy(card.getIsPay())) {
			return false;
		}
		//	需要领取码
		if(CardUtil.isNeedReceiveCode(card.getIsPay())) {
			return false;
		}
		
		return decorate.canSendCard(userId, cardId);
	}

	/**
	 * 	是否可以发放等级卡
	 * @param userId
	 * @param cardId
	 * @return true是，false否
	 */
	private boolean canSendGradeCard(Integer userId, Integer cardId) {
		//	用户领取等级卡需要满足一定的条件
		MemberCardRecord mCard = mCardSvc.getCardById(cardId);
		if(mCard==null) {
			return false;
		}
			
		MemberCardRecord uCard = uCardSvc.getUserGradeCard(userId);
		if(uCard != null) {
			// 要领取的等级卡只有大于用户目前的等级才能领取
			String oldGrade = uCard.getGrade();
			String newGrade = mCard.getGrade();
			if(newGrade.compareToIgnoreCase(oldGrade)<1) {
				return false;
			}
		}
		// 积分或者余额是否满足升级条件
		Integer userTotalScore = scoreSvc.getAccumulationScore(userId);
		BigDecimal amount = uCardSvc.getUserTotalSpendAmount(userId);
		
		GradeConditionJson gradeCondition = uCardSvc.getGradeCondition(userTotalScore, amount, mCard);
		if(gradeCondition.getGradeScore()!=null && gradeCondition.getGradeScore().intValue() <= userTotalScore) {
			return true;
		}
		if(gradeCondition.getGradeMoney()!=null && BigDecimalUtil.compareTo(gradeCondition.getGradeMoney(), amount) <= 0) {
			return true;
		}
		return false;
		
	}
	

}
