package com.meidianyi.shop.controller.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author lixinguo
 *
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test-user.properties")
public class AdminLoginControllerTest extends AdminBaseControllerTest {

	@Override
	protected Boolean needLogin() {
		return false;
	}
	
	@Test
	public void testLogin() {
		this.login();
	}

	@Test
	public void testLogout() {
		this.logout();
	}

}
