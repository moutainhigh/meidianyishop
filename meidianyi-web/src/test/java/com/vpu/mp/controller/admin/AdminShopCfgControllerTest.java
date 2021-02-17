package com.meidianyi.shop.controller.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.config.SearchConfig;

/**
 * 
 * @author lixinguo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test-user.properties")
public class AdminShopCfgControllerTest extends AdminBaseControllerTest {

	@Test
	public void testGetSearchCfg() {
		JsonResult result = this.get("/api/get/searchcfg", token, null, null);
		this.expectSuccess(result);
	}

	@Test
	public void testUpdateSearchCfg() {
		SearchConfig cfg = new SearchConfig();
		cfg.setTitleAction(1);
		cfg.setIsOpenHistory(1);
		JsonResult result = this.post("/api/update/searchcfg", cfg, token, null, null);
		this.expectSuccess(result);
	}

}
