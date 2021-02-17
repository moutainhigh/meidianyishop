package com.meidianyi.shop.controller.system;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.controller.BaseControllerTest;
import com.meidianyi.shop.service.pojo.saas.auth.SystemLoginParam;
import com.meidianyi.shop.service.pojo.saas.auth.SystemTokenAuthInfo;

/**
 * 
 * @author lixinguo
 *
 */
public class SystemBaseControllerTest extends BaseControllerTest{

	@Value(value = "${system.main.username}")
	protected String testUserName;

	@Value(value = "${system.main.password}")
	protected String testPassword;

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
		SystemLoginParam param = new SystemLoginParam();
		param.setUsername(testUserName);
		param.setPassword(testPassword);
		JsonResult result = this.post("/api/system/login", param, null, null, null);
		this.expectSuccess(result);
		SystemTokenAuthInfo info = parseJsonResultContent(result, SystemTokenAuthInfo.class);
		token = info.getToken();
	}


	protected void logout() {
		JsonResult result = this.get("/api/system/logout", token, null, null);
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
