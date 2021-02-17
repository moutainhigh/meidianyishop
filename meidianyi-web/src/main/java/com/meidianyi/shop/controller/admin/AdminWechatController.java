package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.service.foundation.exception.MpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountRecord;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOfficeAccountListVo;
import com.meidianyi.shop.service.pojo.shop.auth.MenuAuthority;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.wechat.OpenPlatform;
import com.meidianyi.shop.service.wechat.WxPayment;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;

/**
 * 
 * @author 新国
 *
 */
@Controller
public class AdminWechatController extends AdminBaseController {

	@Autowired
	protected MenuAuthority authority;

	@Autowired
	protected OpenPlatform open;

	/**
	 * 公众号或小程序授权回调
	 * 
	 * @return
	 */
	@RequestMapping(value = "/wechat/notify/authorization/callback")
	public String authorizationCallback(@RequestParam("auth_code") String authorizationCode,
			@RequestParam(name = "sys_id", required = false) Integer sysId,
			@RequestParam(name = "shop_id", required = false) Integer shopId) {
		try {
			WxOpenQueryAuthResult queryAuthResult = open.getWxOpenComponentService().getQueryAuth(authorizationCode);
			String appId = queryAuthResult.getAuthorizationInfo().getAuthorizerAppid();
			if (sysId != null) {
				// 服务号授权
				//获取授权方的帐号基本信息
				WxOpenAuthorizerInfoResult authorizerInfo = open.getWxOpenComponentService().getAuthorizerInfo(appId);
				if (authorizerInfo.getAuthorizerInfo().getVerifyTypeInfo() == -1
						|| authorizerInfo.getAuthorizerInfo().getServiceTypeInfo() != 2) {
					// 请使用认证服务号授权
					return ("请使用认证服务号授权");
				}
				MpOfficeAccountListVo officeAccountByAppId = saas.shop.officeAccount
						.getOfficeAccountByAppId(authorizerInfo.getAuthorizationInfo().getAuthorizerAppid());
				if (officeAccountByAppId != null && (!officeAccountByAppId.getSysId().equals(sysId))) {
					// 公众号已经授权给其他账号！
					return ("公众号已经授权给其他账号！");
				}
				MpOfficialAccountRecord addMpOfficialAccountInfo = saas.shop.officeAccount.addMpOfficialAccountInfo(sysId, authorizerInfo);
				saas.shop.officeAccount.bindAllSamePrincipalOpenAppId(addMpOfficialAccountInfo.getPrincipalName());
				logger().info("公众号authorizerInfo", authorizerInfo);
				return "redirect:" + this.mainUrl("/admin/home/main/base_manger/authok");

			}
			if (shopId != null) {
				// 小程序授权
				MpAuthShopRecord mp = saas.shop.mp.getAuthShopByShopId(shopId);
				logger().debug("查询出的值1：" + mp);
				if (mp != null && !mp.getAppId().equals(appId)) {
					// 小程序上次授权与本次授权AppId不一致，请联系客服！
					logger().debug("appId" + appId + "小程序上次授权与本次授权AppId不一致，请联系客服！");
					return ("小程序上次授权与本次授权AppId不一致，请联系客服！");
				}
				mp = saas.shop.mp.getAuthShopByAppId(appId);
				if (mp != null && mp.getShopId().intValue() != shopId) {
					// 小程序已授权绑定其他账号，请联系客服！
					logger().debug("appId" + appId + "小程序已授权绑定其他账号，请联系客服！");
					return ("小程序已授权绑定其他账号，请联系客服！");
				}
				logger().debug("查询出的值2：" + mp);
				MpAuthShopRecord addMpAuthAccountInfo = saas.shop.mp.addMpAuthAccountInfo(appId, shopId);
				saas.shop.officeAccount.bindAllSamePrincipalOpenAppId(addMpAuthAccountInfo.getPrincipalName());
				//saas.shop.mp.bindAllSamePrincipalOpenAppId(addMpAuthAccountInfo);
				logger().info("小程序getQueryAuth", queryAuthResult);
				return "redirect:" + this.mainUrl("/admin/home/main/base_manger/authok");
			}
			return null;
		} catch (WxErrorException e) {
			logger().error("gotoPreAuthUrl", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 开放平台tick、公众号或小程序授权、更新授权、解除授权事件
	 * 
	 * @param requestBody
	 * @param timestamp
	 * @param nonce
	 * @param signature
	 * @param encType
	 * @param msgSignature
	 * @return
	 */
	@RequestMapping("/wechat/notify/component/event/callback")
	@ResponseBody
	public Object componentEventCb(@RequestBody(required = false) String requestBody,
			@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
			@RequestParam("signature") String signature,
			@RequestParam(name = "encrypt_type", required = false) String encType,
			@RequestParam(name = "msg_signature", required = false) String msgSignature) {
		return open.componetCallback(requestBody, timestamp, nonce, signature, encType, msgSignature);
	}

	/**
	 * 公众号或小程序消息处理
	 * 
	 * @param requestBody
	 * @param appId
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param openid
	 * @param encType
	 * @param msgSignature
	 * @return
	 */
	@RequestMapping("/wechat/notify/app/event/{appId}/callback")
	@ResponseBody
	public Object appEventCallback(@RequestBody(required = false) String requestBody,
			@PathVariable("appId") String appId, @RequestParam("signature") String signature,
			@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
			@RequestParam("openid") String openid, @RequestParam("encrypt_type") String encType,
			@RequestParam("msg_signature") String msgSignature) {
		return open.appEvent(requestBody, appId, signature, timestamp, nonce, openid, encType, msgSignature);
	}

	/**
	 * 小程序微信支付回调
	 * 
	 * @param xmlData
	 * @param shopId
	 * @return
	 */
	@RequestMapping("/wechat/notify/ma/payment/{shopId}")
	@ResponseBody
	public String maPaymentNotify(@RequestBody String xmlData, @PathVariable Integer shopId) {
		logger().debug("maPaymentNotify request: {}", xmlData);
		MpPaymentService mpPay = saas.getShopApp(shopId).pay.mpPay;
		WxPayment wxPayService = mpPay.getMpPay();
		WxPayOrderNotifyResult orderResult;
		try {
			orderResult = mpPay.getMpPay().parseOrderNotifyResult(xmlData);
			orderResult.checkResult(wxPayService, null, true);
			mpPay.onPayNotify(orderResult);
			return WxPayNotifyResponse.success("Ok");
		} catch (WxPayException | MpException e) {
			return WxPayNotifyResponse.fail(e.getMessage());
		}
    }

}
