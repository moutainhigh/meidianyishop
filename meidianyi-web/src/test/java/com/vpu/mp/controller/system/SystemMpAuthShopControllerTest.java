package com.meidianyi.shop.controller.system;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpDeployQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionListParam;

/**
 * 
 * @author lixinguo
 *
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test-user.properties")
public class SystemMpAuthShopControllerTest extends SystemBaseControllerTest {

	@Test
	public void testList() {
		MpVersionListParam param = new MpVersionListParam();
		param.setCurrentPage(1);
		param.setPageRows(10);
		JsonResult result = this.post("/api/system/mp/version/list", param, token,null,null);
		this.expectSuccess(result);
	}

	@Test
	public void testSynMpList() {
		JsonResult result = this.get("/api/system/mp/version/syn", token,null,null);
		this.expectSuccess(result);
	}

	@Test
	public void testSetVersion() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchPublish() {
		fail("Not yet implemented");
	}

	@Test
	public void testMpPublishAction() {
		
		MpDeployQueryParam param = new MpDeployQueryParam();
		
		param.setAct(MpDeployQueryParam.ACT_MODIFY_DOMAIN);
		param.setAppId("wx3a6cbd7a7735b683");
		JsonResult result = this.post("/api/system/mp/publish", param, token,null,null);
		this.expectSuccess(result);
		
		// TODO:其他等待 小程序准备成功
		
	}

	@Test
	public void testGetMp() {
		fail("Not yet implemented");
	}

}
