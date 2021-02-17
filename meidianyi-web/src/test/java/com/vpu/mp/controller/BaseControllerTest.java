package com.meidianyi.shop.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;

/**
 * 
 * @author lixinguo
 *
 */
public class BaseControllerTest {

	/**
	 * 请求基础URL
	 */
	@Value(value = "${test.base_url}")
	protected String baseUrl;

	/**
	 * 请求模板客户端
	 */
	private static RestTemplate restTemplate = new RestTemplate();

	/**
	 * Get请求
	 * 
	 * @param uri
	 * @param enName
	 * @param lang
	 * @return
	 */
	protected JsonResult get(String uri, String token, String enName, String lang) {
		return this.request(HttpMethod.GET, uri, null, token, enName, lang);
	}

	/**
	 * post请求
	 * 
	 * @param uri
	 * @param jsonObject
	 * @param token
	 * @param enName
	 * @param lang
	 * @return
	 */
	protected JsonResult post(String uri, Object jsonObject, String token, String enName, String lang) {
		return this.request(HttpMethod.POST, uri, Util.toJson(jsonObject), token, enName, lang);
	}

	/**
	 * post请求
	 * 
	 * @param uri
	 * @param jsonObject
	 * @param token
	 * @param enName
	 * @param lang
	 * @return
	 */
	protected JsonResult post(String uri, String json, String token, String enName, String lang) {
		return this.request(HttpMethod.POST, uri, json, token, enName, lang);
	}

	/**
	 * 请求
	 * 
	 * @param method
	 * @param uri
	 * @param jsonObject
	 * @param token
	 * @param enName
	 * @param lang
	 * @return
	 */
	protected JsonResult request(HttpMethod method, String uri, String json, String token, String enName,
			String lang) {
		String url = baseUrl + uri;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
		if (token != null) {
			headers.add("V-Token", token);
		}
		if (lang != null) {
			headers.add("V-Lang", lang);
		}
		if (enName == null) {
			headers.add("V-EnName", enName);
		}
		HttpEntity<String> entity = null;
		if (HttpMethod.POST.equals(method)) {
			entity = new HttpEntity<String>(json, headers);
		} else if (HttpMethod.GET.equals(method)) {
			entity = new HttpEntity<String>(headers);
		} else {
			fail("Not yet implemented methods");
		}
		logger().debug("requesst: " + url);
		if (HttpMethod.POST.equals(method)) {
			logger().debug("body   : "+ json);
		}
		
		ResponseEntity<JsonResult> response = restTemplate.exchange(url, method, entity, JsonResult.class);
		JsonResult result = response.getBody();
		logger().debug("result : "+ Util.toJson(result));
		return result;
	}

	/**
	 * 期望结果为success
	 * 
	 * @param result
	 */
	protected void expectSuccess(JsonResult result) {
		assertEquals(result.getError(), JsonResultCode.CODE_SUCCESS.getCode());
	}

	/**
	 * 期望结果为失败
	 * 
	 * @param result
	 */
	protected void expectFail(JsonResult result) {
		assertNotEquals(result.getError(), JsonResultCode.CODE_SUCCESS.getCode());
	}

	/**
	 * 期望结果为指定的code
	 * 
	 * @param result
	 * @param code
	 */
	protected void expectCode(JsonResult result, JsonResultCode code) {
		assertEquals(result.getError(), code.getCode());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 * @param <T>
	 * @param result
	 * @param cls
	 * @return
	 */
	protected <T> T parseJsonResultContent(JsonResult result, Class<T> cls) {
		return Util.parseJson(Util.toJson(result.getContent()), cls);
	}

	protected Logger logger() {
		return LoggerFactory.getLogger(getClass());
	}
}
