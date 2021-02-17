package com.meidianyi.shop.controller.admin;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.controller.BaseControllerTest;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopLoginParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopReq;

/**
 * 
 * @author lixinguo
 *
 */
public class AdminBaseControllerTest extends BaseControllerTest {

	@Value(value = "${admin.main.username}")
	protected String testUserName;

	@Value(value = "${admin.main.password}")
	protected String testPassword;

	@Value(value = "${admin.login.shop_id}")
	protected Integer testLoginShopId;

	protected static String token;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		if (needLogin()) {
			login();
		}
	}

	@Override
	@After
	public void tearDown() throws Exception {
		if (needLogin()) {
			logout();
		}
		super.tearDown();
	}

	/**
	 * 是否登录才能操作，可以在继承时改变这个状态
	 * 
	 * @return
	 */
	protected Boolean needLogin() {
		return true;
	}

	protected void login() {
		ShopLoginParam param = new ShopLoginParam();
		param.setUsername(testUserName);
		param.setPassword(testPassword);
		JsonResult result = this.post("/api/admin/login", param, null, null, null);
		this.expectSuccess(result);
		AdminTokenAuthInfo info = parseJsonResultContent(result, AdminTokenAuthInfo.class);

		token = info.getToken();
		ShopReq shopReq = new ShopReq();
		shopReq.setShopId(this.testLoginShopId);
		result = this.post("/api/admin/account/shop/switch", shopReq, token, null, null);
		this.expectSuccess(result);
	}

	protected void logout() {
		JsonResult result = this.get("/api/admin/logout", token, null, null);
		this.expectSuccess(result);
		token = null;
	}

	protected String getToken() {
		return token;
	}

	@Test
	public void test() {
		assertTrue(true);
	}

}
