package com.meidianyi.shop.controller.wxapp;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.auth.WxAppAuth;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuditStateVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawSumDetailVo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountNumberVo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountPageListVo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountWithdrawVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserAccoountInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserAccountSetParam;
import com.meidianyi.shop.service.pojo.wxapp.account.UserAccountSetVo;
import com.meidianyi.shop.service.pojo.wxapp.account.WxAppAccountParam;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.shop.ShopApplication;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年10月9日 下午3:36:59
 */
@RestController
public class WxAppAccountController extends WxAppBaseController {

	/**
	 *更新用户昵称，头像
	 * 
	 * @throws WxErrorException
	 */
	@PostMapping("/api/wxapp/account/updateUser")
	public JsonResult updateUser(@RequestBody WxAppAccountParam param) throws WxErrorException {
		logger().info("更新用户昵称，头像");
		Integer shopId = wxAppAuth.shopId();
		ShopApplication shopApp = saas.getShopApp(shopId);
		param.setUserId(wxAppAuth.user().getUserId());
		boolean updateUser = shopApp.user.updateUser(param, WxAppAuth.TOKEN_PREFIX);
		if(updateUser) {
			return success();
		}
		return fail();
	}
	
	/**
	 * 新版个人中心
	 */
	@PostMapping("/api/wxapp/account/usercenter")
	public JsonResult getNewUserAccoountInfo() {
		logger().info("新版个人中心");
		Integer shopId = wxAppAuth.shopId();
		ShopApplication shopApp = saas.getShopApp(shopId);
		logger().info("接受"+wxAppAuth.user().toString());
		List<Map<String, Object>> moduleData = shopApp.user.parseCenterModule(wxAppAuth.user().getUserId());
		if(moduleData==null) {
			logger().info("用户不存在");
			return fail();
		}
		logger().info("个人中心准备返回"+moduleData);
		UserAccoountInfoVo vo=new UserAccoountInfoVo();
		vo.setModuleData(moduleData);
		vo.setOtherData(new String[0]);
		return success(vo);
	}
	
	
	/**
	 * 账号设置
	 * @return
	 */
	@PostMapping("/api/wxapp/account/setting")
	public JsonResult accountSetting(@RequestBody UserAccountSetParam param) {
		Integer shopId = wxAppAuth.shopId();
		ShopApplication shopApp = saas.getShopApp(shopId);
		JsonResultCode code = shopApp.user.accountSetting(param, wxAppAuth.user());
		UserAccountSetVo data = shopApp.user.accountSetting(wxAppAuth.user().getUserId(), param.getIsSetting());
		if(data!=null) {
			return success(data);
		}
		if(code!=JsonResultCode.CODE_SUCCESS) {
			return success(code);
		}
		return fail(code);
		
	}
	
	/**
	 * 	获取用户的余额和提现金额
	 */
	@PostMapping("/api/wxapp/user/account/withdraw")
	public JsonResult getUserAccountWithdraw() {
		WxAppSessionUser user = wxAppAuth.user();
		AccountWithdrawVo vo = shop().member.account.getUserAccountWithdraw(user.getUserId());
		return success(vo);
	}
	
	/**
	 * 获取用户余额-积分信息
	 */
	@PostMapping("/api/wxapp/user/number")
	public JsonResult getUserAccountNumber() {
		AccountNumberVo vo = shop().member.account.getUserAccountNumber(wxAppAuth.user().getUserId());
		if(vo==null) {
			return fail();
		}
		return success(vo);
	}
	
	/**
	 * 获取用户余额明细
	 */
	@PostMapping("/api/wxapp/account/list")
	public JsonResult getUserAccountList(@RequestBody AccountPageListParam param) {
		param.setUserId(wxAppAuth.user().getUserId());
		PageResult<AccountPageListVo> res = shop().member.account.getPageListOfAccountDetails(param,getLang());
		return success(res);
	}
	
	/**
	 * 提现记录
	 */
	@PostMapping("/api/wxapp/distributor/withdraw/list")
	public JsonResult withdrawList(@RequestBody DistributorWithdrawListParam param) {
		param.setUserId(wxAppAuth.user().getUserId());
		DistributorWithdrawSumDetailVo vo = shop().withdraw.withdrawList(param);
		if(vo == null ) {
			return fail();
		}else {
			return success(vo);
		}
	}
	
	/**
	 * 获取用户手机号的解密
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/wxdecrypt")
	public JsonResult wxDecryptData(@RequestBody WxAppAccountParam param) {
		logger().info("获取用户手机号的解密");
		Integer shopId = wxAppAuth.shopId();
		ShopApplication shopApp = saas.getShopApp(shopId);
		param.setUserId(wxAppAuth.user().getUserId());
		WxMaPhoneNumberInfo updateUser = shopApp.user.wxDecryptData(param, WxAppAuth.TOKEN_PREFIX);
		if(updateUser!=null) {
			return success(updateUser.getPhoneNumber());
		}
		return fail();
		
	}
	
	/**
	 * 获取小程序审核状态
	 * @return
	 */
	@PostMapping("/api/wxapp/auditState")
	public JsonResult getAppAduitInfo() {
		logger().info("获取小程序审核状态");
		Integer shopId = wxAppAuth.shopId();
		Boolean authOk = saas.shop.mp.isAuthOk(shopId);
		if (!authOk) {
			return fail(JsonResultCode.WX_MA_SHOP_HAS_NO_AP);
		}
		MpAuditStateVo appAuditInfo = saas.shop.mp.getAppAuditInfo(shopId);
		return success(appAuditInfo.getAuditState());
	}
	
	/**
	 * 获取用户待激活信息
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/user/waitactivate")
	public JsonResult waitActivateUser() {
		logger().info("获取用户待激活信息");
		return success(shop().member.userImportService.getInfo(getLang()));
	}
	
	
	/**
	 * 用户待激活
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/user/toactivate")
	public JsonResult toActivateUser() {
		logger().info("用户待激活");
		JsonResultCode activateUser = shop().member.userImportService.toActivateUser(wxAppAuth.user().getUserId());
		if(activateUser.equals(JsonResultCode.CODE_SUCCESS)) {
			return success();
		}
		return fail(activateUser);
	}
}
