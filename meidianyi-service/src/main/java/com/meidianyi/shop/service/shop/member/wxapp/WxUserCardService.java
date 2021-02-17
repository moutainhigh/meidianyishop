package com.meidianyi.shop.service.shop.member.wxapp;

import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardMaParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeVo;
import com.meidianyi.shop.service.pojo.shop.member.exception.CardActivateException;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardVo;
import com.meidianyi.shop.service.pojo.shop.member.ucard.CardUseListParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.DefaultCardParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ReceiveCardParam;
import com.meidianyi.shop.service.pojo.wxapp.user.UserCheckedGoodsParam;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardExchangeService;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardGiveAwaySerivce;
import com.meidianyi.shop.service.shop.member.UserCardService;
/**
 * @author 黄壮壮
 * 	小程序会员卡服务
 */
@Service
public class WxUserCardService extends ShopBaseService {
	@Autowired
	private WxAppCardReceiveSerive wxAppCardReceiveSerive;
	@Autowired
	private WxAppCardActivationService wxAppCardActivationService;
	@Autowired
	public WxCardGiveAwaySerivce giveAwaySvc;
	@Autowired 
	public WxCardExchangeService exchangeSvc;
	@Autowired 
	private UserCardService userCardService;
	
	private static final byte ONE = 1;
	private static final byte NEONE = -1;
	/**
	 * 通过领取码领取会员卡
	 * @throws MpException 
	 */
	public void receiveCard(ReceiveCardParam param) throws MpException {
		wxAppCardReceiveSerive.receiveCard(param);
	}
	/**
	 * 	会员卡激活
	 * @throws CardActivateException  激活失败
	 */
	public ActivateCardVo activationCard(ActivateCardParam param, String lang) throws CardActivateException {
		if(NumberUtils.BYTE_ONE.equals(param.getIsSetting())) {
			return wxAppCardActivationService.setActivationCard(param);
		}else {
			return wxAppCardActivationService.getActivationCard(param,lang);
		}
	}
	/**
	 * 	设置为默认会员卡
	 */
	public void setDefault(DefaultCardParam param) {
		userCardService.setDefault(param);
		
	}
	
	public UserCardMaParam getUseList(CardUseListParam param,String language) {
		Record userCardInfo = userCardService.userCardDao.getUserCardInfoBycardNo(param.getCardNo());
		if(userCardInfo==null) {
			//该卡不存在
			return null;
		}
		UserCardMaParam into = userCardInfo.into(UserCardMaParam.class);
		ChargeParam param2=new ChargeParam();
		param2.setCardNo(param.getCardNo());
		param2.setCurrentPage(param.getCurrentPage());
		param2.setPageRows(param.getPageRows());
		Byte showType = param.getShowType();
		if(showType.equals(ONE)) {
			logger().info("showType为1");
			PageResult<ChargeVo> chargeList = userCardService.cardDao.getChargeList(param2,language);
			into.setChargeList(chargeList);
		}
		if(showType.equals(NEONE)) {
			logger().info("showType为-1");
			PageResult<ChargeVo> consumeList = userCardService.cardDao.getConsumeList(param2,language);
			into.setChargeList(consumeList);
		}
		return into;
	}
	
	
	/**
	 * 删除已选活动商品
	 * @param param
	 */
	public void removeChoosedGoods(UserCheckedGoodsParam param) {
		
		
	}
	

}
