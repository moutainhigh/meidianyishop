package com.meidianyi.shop.service.wechat.util.http;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.http.RequestHttp;
import me.chanjar.weixin.common.util.http.apache.ApacheSimplePostRequestExecutor;
import me.chanjar.weixin.common.util.http.apache.Utf8ResponseHandler;

/**
 * @author lixinguo
 */
public class ApacheFormEncodedPostRequestExecutor extends ApacheSimplePostRequestExecutor {

	public ApacheFormEncodedPostRequestExecutor(RequestHttp<CloseableHttpClient, HttpHost> requestHttp) {
		super(requestHttp);
	}

	@Override
	public String execute(String uri, String postEntity) throws WxErrorException, IOException {
		HttpPost httpPost = new HttpPost(uri);
		if (requestHttp.getRequestHttpProxy() != null) {
			RequestConfig config = RequestConfig.custom().setProxy(requestHttp.getRequestHttpProxy()).build();
			httpPost.setConfig(config);
		}

		if (postEntity != null) {
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			StringEntity entity = new StringEntity(postEntity, Consts.UTF_8);
			httpPost.setEntity(entity);
		}

		try (CloseableHttpResponse response = requestHttp.getRequestHttpClient().execute(httpPost)) {
			String responseContent = Utf8ResponseHandler.INSTANCE.handleResponse(response);
			if (responseContent.isEmpty()) {
				throw new WxErrorException(WxError.builder().errorCode(9999).errorMsg("无响应内容").build());
			}

			if (responseContent.startsWith("<xml>")) {
				return responseContent;
			}

			WxError error = WxError.fromJson(responseContent);
			if (error.getErrorCode() != 0) {
				throw new WxErrorException(error);
			}
			return responseContent;
		} finally {
			httpPost.releaseConnection();
		}
	}
}
