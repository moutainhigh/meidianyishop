package com.meidianyi.shop.controller.admin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopLoginParam;

/**
 * 
 * @author 新国
 *
 */
@RestController
@RequestMapping("/api")
public class AdminLoginController extends AdminBaseController {

	@PostMapping(value = "/admin/login")
	public JsonResult login(@RequestBody @Valid ShopLoginParam param) {
		AdminTokenAuthInfo info = adminAuth.login(param);

		if (info != null) {
			return this.success(info);
		} else {
			return this.fail(JsonResultCode.CODE_ACCOUNT_OR_PASSWORD_INCRRECT);
		}
	}


	@GetMapping(value = "/admin/logout")
	public JsonResult logout() {
		adminAuth.logout();
		return success(JsonResultCode.CODE_SUCCESS);
	}
	
	/**
	 * 判断用户是否在线，首页右上角是否显示用户信息用
	 * @return
	 */
	@GetMapping(value = "/admin/login/isLogin")
	public JsonResult checkToken() {
		AdminTokenAuthInfo user = adminAuth.user();
		if(user!=null) {
			return success();
		}
		return fail();
	}
	
}
