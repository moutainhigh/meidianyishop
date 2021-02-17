package com.meidianyi.shop.controller.wxapp;
import com.meidianyi.shop.service.pojo.shop.member.buy.CardOrdeerSnParam;
import com.meidianyi.shop.service.pojo.shop.member.account.*;
import com.meidianyi.shop.service.pojo.shop.member.card.RankCardToVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.account.CardReceiveVo;
import com.meidianyi.shop.service.pojo.shop.member.account.CardRenewCheckoutParam;
import com.meidianyi.shop.service.pojo.shop.member.account.CardRenewCheckoutVo;
import com.meidianyi.shop.service.pojo.shop.member.account.CardRenewParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardGetParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardJudgeVo;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardMaParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserIdAndCardIdParam;
import com.meidianyi.shop.service.pojo.shop.member.account.WxAppUserCardVo;
import com.meidianyi.shop.service.pojo.shop.member.buy.CardBuyClearingParam;
import com.meidianyi.shop.service.pojo.shop.member.buy.CardToPayParam;
import com.meidianyi.shop.service.pojo.shop.member.card.SearchCardParam;
import com.meidianyi.shop.service.pojo.shop.member.exception.CardActivateException;
import com.meidianyi.shop.service.pojo.shop.member.exception.UserCardNullException;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardVo;
import com.meidianyi.shop.service.pojo.shop.member.ucard.CardUseListParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.DefaultCardParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ReceiveCardParam;
import com.meidianyi.shop.service.pojo.wxapp.card.CardUpgradeVo;
import com.meidianyi.shop.service.pojo.wxapp.card.param.CardAddExchangeGoodsParam;
import com.meidianyi.shop.service.pojo.wxapp.card.param.CardExchaneGoodsJudgeParam;
import com.meidianyi.shop.service.pojo.wxapp.card.param.CardExchangeGoodsParam;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardCheckedGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardExchangeGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.user.UserCheckedGoodsParam;

/**
* @author 黄壮壮
* @Date: 2019年10月23日
* @Description: 微信小程序会员卡控制器
*/
@RestController
public class WxAppCardController extends WxAppBaseController {
	/**
	 * 获取用户的会员卡列表
	 * @return
	 */
	@PostMapping(value="/api/card/list")
	public JsonResult getUserCard(@RequestBody SearchCardParam param) {
		logger().info("wxapp request for card list of person.");
		PageResult<WxAppUserCardVo> cardList = shop().user.userCard.getAllCardsOfUser(param,getLang());
		return success(cardList);
	}
	
	/**
	 * 会员卡详情
	 */
	@PostMapping(value="/api/card/detail")
	public JsonResult getUserCardDetail(@RequestBody UserCardParam param) {
		logger().info("WxAppCardController: request for card detail");
		param.setUserId(wxAppAuth.user().getUserId());
		WxAppUserCardVo userCardDetail;
		try {
			userCardDetail = shop().user.userCard.getUserCardDetail(param,getLang());
		} catch (UserCardNullException e) {
			return fail(e.getErrorCode());
		}
		return success(userCardDetail);
	}

    /**
     * 获取登记卡的权益
     */
    @PostMapping(value="/api/card/interests/info")
    public JsonResult getInterests(@RequestBody CardInterestsParam param){
        param.setUserId(wxAppAuth.user().getUserId());
        return success(shop().userCard.getInterestsByGrade(param));
    }

	/**
	 * 领取会员卡判断
	 */
	@PostMapping(value="/api/card/judgement")
	public JsonResult userCardJudgement(@RequestBody UserIdAndCardIdParam param) {
		logger().info("判断是否有会员卡");
		WxAppSessionUser user = wxAppAuth.user();
		param.setUserId(user.getUserId());
		UserCardJudgeVo vo = shop().user.userCard.userCardJudgement(param,getLang());
		return success(vo);
	}
	/**
	 * 领取会员卡
	 */
	@PostMapping(value="/api/card/getCard")
	public JsonResult getCard(@RequestBody @Validated UserCardGetParam param) {
		UserIdAndCardIdParam para = new UserIdAndCardIdParam();
		logger().info("领取会员卡请求");
		WxAppSessionUser user = wxAppAuth.user();
		para.setUserId(user.getUserId());
		para.setGetType(param.getGetType());
		para.setCardId(param.getCardId()!=null?param.getCardId():param.getCardInfo().getCardId());
		try {
			CardReceiveVo vo = shop().user.userCard.getCard(para);
			if(vo!=null && !StringUtils.isBlank(vo.getCardNo())) {
				return success(vo);
			}else {
				return fail();
			}
		} catch (MpException e) {
			return fail(e.getErrorCode());
		}	
	}
	/**
	 * 用户通过领取码领取会员卡
	 */
	@PostMapping(value="/api/wxapp/card/code/receive")
	public JsonResult receiveCard(@RequestBody @Validated ReceiveCardParam param) {
		logger().info("用户通过领取码领取会员卡");
		param.setUserId(wxAppAuth.user().getUserId());
		try {
			shop().user.wxUserCardService.receiveCard(param);
		} catch (MpException e) {
			return fail(e.getErrorCode());
		} 
		return success();
	}
	
	/**
	 * 	会员卡激活
	 */
	@PostMapping(value="/api/wxapp/activation/card")
	public JsonResult activationCard(@RequestBody @Validated ActivateCardParam param) {
		logger().info("获取会员卡激活信息"+param);
		param.setUserId(this.wxAppAuth.user().getUserId());
		try {
			ActivateCardVo vo = shop().user.wxUserCardService.activationCard(param,getLang());
			if(NumberUtils.BYTE_ONE.equals(param.getIsSetting())) {
				return result(vo.getMsg(),null);
			}else if(vo != null){
				return this.success(vo);
			}
			return fail();
		} catch (CardActivateException e) {
			return fail(e.getErrorCode());
		}
	}
	
	/**
	 * 	设置默认会员卡
	 */
	@PostMapping(value="/api/wxapp/card/default")
	public JsonResult setDefault(@RequestBody @Validated DefaultCardParam param) {
		logger().info("设置默认会员卡");
		param.setUserId(wxAppAuth.user().getUserId());
		shop().user.wxUserCardService.setDefault(param);
		return success();
	}
	
	
	/**
	 * 	会员卡使用记录
	 */
	@PostMapping(value="/api/wxapp/card/use")
	public JsonResult cardConsume(@RequestBody @Validated CardUseListParam param) {
		logger().info("会员卡使用记录");
		UserCardMaParam useList = shop().user.wxUserCardService.getUseList(param,getLang());
		if(useList==null) {
			return fail(JsonResultCode.CODE_CARD_NO);
		}
		return success(useList);
	}
	
	/**
	 * 会员卡升降级记录
	 */
	@PostMapping(value="/api/wxapp/card/upgrade")
	public JsonResult getGradeList(@RequestBody SearchCardParam param) {
		param.setUserId(wxAppAuth.user().getUserId());
		PageResult<CardUpgradeVo> gradeList = shop().userCard.getGradeList(param);
		return success(gradeList);
	}
	/**
	 * 	删除已过期的会员卡
	 */
	@PostMapping(value="/api/wxapp/card/del")
	public JsonResult delUserCard(@RequestBody DefaultCardParam param) {
		logger().info("删除会员卡");
		param.setUserId(wxAppAuth.user().getUserId());
		int res = shop().userCard.delUserCard(param);
		if(res != 0) {
			return success();
		}
		return fail();
	}
    /**
     * 	会员卡续费-详情页
     */
    @PostMapping(value="/api/wxapp/card/renew")
    public JsonResult cardRenew(@RequestBody CardRenewParam param) {
        UserCardParam res = shop().userCard.cardRenew(param);
        return success(res);
    }
    /**
     * 	会员卡续费-支付完成
     */
    @PostMapping(value="/api/wxapp/card/renew/checkout")
    public JsonResult cardRenewCheckout(@RequestBody CardRenewCheckoutParam param) throws MpException {
        param.setClientIp(RequestUtil.getIp(request));
        CardRenewCheckoutVo res = shop().userCard.renewCardCheckout(param);
        return success(res);
    }
	/**
	 * 购买会员卡结算
	 * @return
	 */
	@PostMapping("/api/wxapp/card/buy/clearing")
	public JsonResult toBuyCardClearing(@RequestBody @Validated CardBuyClearingParam param){
		WxAppSessionUser user = wxAppAuth.user();
		param.setUserId(user.getUserId());
		return success(shop().userCard.toBuyCardClearing(param));
	}

	/**
	 * 购买会员卡支付
	 * @return
	 */
	@PostMapping("/api/wxapp/card/buy/pay")
	public JsonResult toBuyCard(@RequestBody @Validated CardToPayParam payParam) throws MpException {
		WxAppSessionUser user = wxAppAuth.user();
		payParam.setUser(user);
		payParam.setClientIp(RequestUtil.getIp(request));
    	return success(shop().userCard.buyCardCreateOrder(payParam));
	}

    /**
     * 通过订单查找卡号
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/card/order/getcardno")
	public JsonResult getCardNo(@RequestBody @Validated CardOrdeerSnParam param){
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        return success(shop().userCard.getCardNoByOrderSn(param));
    }

	/**
	 * 	领取赠送的会员卡
	 */
	@PostMapping("/api/wxapp/card/getgiveawaycard")
	public JsonResult getGiveAwayCard(@RequestBody DefaultCardParam param) {
		logger().info("领取赠送的会员卡");
		param.setUserId(wxAppAuth.user().getUserId());
		try {
			shop().user.wxUserCardService.giveAwaySvc.getGiveAwayCard(param);
			return success();
		} catch (MpException e) {
			return fail(e.getErrorCode());
		}
	}
	
	
	/**
	 * 	取消限次卡转赠
	 */
	@PostMapping("/api/wxapp/card/giveAway/quit")
	public JsonResult quitLimitCardGiveAway(@RequestBody DefaultCardParam param) {
		logger().info("取消限次卡转赠");
		param.setUserId(wxAppAuth.user().getUserId());
		shop().user.wxUserCardService.giveAwaySvc.quitLimitCardGiveAway(param);
		return success();
	}
	
	/**
	 * 	转赠限次卡
	 */
	@PostMapping("/api/wxapp/card/giveAway/record")
	public JsonResult limitCardGiveAway(@RequestBody DefaultCardParam param) {
		logger().info("转赠限次卡");
		param.setUserId(wxAppAuth.user().getUserId());
		shop().user.wxUserCardService.giveAwaySvc.addLimitCardGiveAwayRecord(param);
		return success();
	}
	
	
	/**
	 * 	限次卡兑换商品列表
	 */
	@PostMapping("/api/wxapp/card/change/goodslist")
	public JsonResult changeGoodsList(@RequestBody @Validated CardExchangeGoodsParam param) {
		logger().info("兑换商品列表");
		param.setUserId(wxAppAuth.user().getUserId());
		CardExchangeGoodsVo vo = shop().user.wxUserCardService.exchangeSvc.changeGoodsList(param);
		return success(vo);
	}

	/**
	 * 限次卡是否兑换商品
	 * @throws MpException
	 */
	@PostMapping("/api/wxapp/card/exchange/judge")
	public JsonResult changeGoodsList(@RequestBody @Validated CardExchaneGoodsJudgeParam param) throws MpException {
		logger().info("限次卡是否兑换商品判断");
		param.setUserId(wxAppAuth.user().getUserId());
		boolean res = shop().user.wxUserCardService.exchangeSvc.judgeCardGoods(param);
		return res?success():fail();
	}
	
	/**
	 * 限次卡是否能够兑换单个商品校验
	 * @throws MpException
	 */
	@PostMapping("/api/card/exchange/tobuy/judge")
	public JsonResult judgeCardExchangeTobuy(@RequestBody @Validated CardExchaneGoodsJudgeParam param) throws MpException {
		logger().info("限次卡是否兑换商品判断");
		param.setUserId(wxAppAuth.user().getUserId());
		boolean res = shop().user.wxUserCardService.exchangeSvc.judgeCardGoods(param);
		return res?success():fail();
	}
	
	/**
	 * 兑换商品加购
	 * @throws MpException
	 */
	@PostMapping("/api/wxapp/card/change/add")
	public JsonResult addExchangeGoods(@RequestBody CardAddExchangeGoodsParam param) throws MpException {
		logger().info("限次卡兑换商品加购");
		param.setUserId(wxAppAuth.user().getUserId());
		shop().user.wxUserCardService.exchangeSvc.addExchangeGoods(param);
		return success();
	}
	
	/**
	 * 已选商品列表
	 */
	@PostMapping("/api/wxapp/card/change/checkedlist")
	public JsonResult changeCheckedGoodsList(@RequestBody UserCheckedGoodsParam param) {
		param.setUserId(wxAppAuth.user().getUserId());
		CardCheckedGoodsVo vo = shop().user.wxUserCardService.exchangeSvc.changeCheckedGoodsList(param);
		return success(vo);
	}
	
	/**
	 * 兑换商品删除
	 */
	@PostMapping("/api/wxapp/card/change/remove")
	public JsonResult removeChoosedGoods(@RequestBody UserCheckedGoodsParam param) {
		logger().info("兑换商品删除");
		shop().user.wxUserCardService.exchangeSvc.removeChoosedGoods(param);
		return success();
	}
}

