package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.service.shop.user.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.meidianyi.shop.auth.AdminAuth;
import com.meidianyi.shop.controller.BaseController;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.shop.ShopApplication;

/**
 * 
 * @author 新国
 *
 */
public class AdminBaseController extends BaseController {
	
	@Autowired
	protected AdminAuth adminAuth;
	
	
	/**
	 * 得到当前登录店铺
	 * @return
	 */
	protected ShopApplication shop() {
		AdminTokenAuthInfo user = adminAuth.user();
		Assert.isTrue(user!=null && user.isShopLogin(),"shop is null");
		return saas.getShopApp(user.getLoginShopId());
	}
	
	/**
	 * 得到登录店铺ID
	 * @return
	 */
	protected Integer shopId() {
		AdminTokenAuthInfo user = adminAuth.user();
		Assert.isTrue(user!=null && user.isShopLogin(),"shop is null");
		return user.getLoginShopId();
	}
	
	/**
	 * 日志
	 * @return
	 */
	protected Logger logger() {
		return LoggerFactory.getLogger(this.getClass());
	}
}
