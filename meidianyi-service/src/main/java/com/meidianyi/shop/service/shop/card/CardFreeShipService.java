package com.meidianyi.shop.service.shop.card;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkMessage;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
/**
 *	卡的包邮服务
 *	@author 黄壮壮
 *
 */
@Service
public class CardFreeShipService extends ShopBaseService{
	private static final String REMARK_I18N = "remark";
	@Autowired
	private MemberCardService mCardSvc;
	@Autowired
	private UserCardService uCardSvc;
	@Autowired
	private OrderInfoService orderInfoSvc;
	/**
	 *	返回会员卡的包邮配置
	 * @param card MemberCardRecord
	 * @return CardFreeship 包邮信息 
	 */
	public CardFreeship getFreeshipData(MemberCardRecord card,String lang) {
		logger().info("获取会员卡的包邮配置信息");
		
		Byte type = card.getFreeshipLimit();
		Integer num = 0;
		String desc = "";
		if(type != null && type>=CardFreeship.shipType.SHIP_IN_EFFECTTIME.getType()) {
			//	周期内包邮次数
			num = card.getFreeshipNum();
			desc = Util.translateMessage(lang, RemarkMessage.FREESHIP_TOTAL_NUM, REMARK_I18N, card.getFreeshipNum());
		}else if(type != null && type.equals(CardFreeship.shipType.SHIP_VIP.getType())){
			//	无限制包邮
			desc = CardFreeship.getFreeShipDesc(lang).get(0);
		}
		return CardFreeship.builder()
					.type(type)
					.num(num)
					.desc(desc)
					.build();
	}
	
	/**
	 * 	返回用户卡的包邮快照信息
	 * @param card UserCardParam
	 * @param lang 语言类型
	 * @return CardFreeship 包邮信息 不会为null
	 */
	public CardFreeship getFreeshipData(UserCardParam card,String lang) {
		logger().info("获取用户卡的包邮信息");
		if(card.getFreeLimit()==null) {
			return new CardFreeship();
		}
		String desc=null;
		// 剩余的包邮次数
		Integer remainNum = card.getFreeNum();
		List<String> freeShipDescs = CardFreeship.getFreeShipDesc(lang);
		if(NumberUtils.BYTE_ZERO.equals(card.getFreeLimit())) {
			desc = freeShipDescs.get(0);
		}else if(card.getFreeLimit()>0) {
			// 使用包邮次数，以便计算剩余的包邮次数
			Integer hasFree = orderInfoSvc.getCardFreeShipSum(card.getCardNo(),card.getFreeLimit());
			
			// 剩余包邮次数多少
			remainNum -= hasFree;
			String remainStr = Util.translateMessage(lang, RemarkMessage.FREESHIP_NUM, REMARK_I18N, remainNum);
			desc = freeShipDescs.get(card.getFreeLimit())+remainStr;
			logger().info(desc); 
		}
		
		return CardFreeship.builder()
						   .type(card.getFreeLimit())
						   .num(card.getFreeNum())
						   .remainNum(remainNum)
						   .desc(desc)
						   .build();
	}

	/**
	 * 	返回用户卡的包邮快照信息
	 * @param cardNo 卡号
	 * @param lang 语言
	 * @return CardFreeship 包邮信息 不会为null
	 */
	public CardFreeship getFreeshipData(String cardNo,String lang) {
		UserCardParam card = uCardSvc.getCard(cardNo);
		return getFreeshipData(card,lang);
	}

}
