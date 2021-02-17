package com.meidianyi.shop.auth;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meidianyi.shop.dao.foundation.database.DatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.saas.SaasApplication;

/****
 **
 ** @author 新国
 **
 **/
@Component
public class WxAppAuthInterceptor extends HandlerInterceptorAdapter {

	private static final String URL_LOGIN = "/api/wxapp/login";
	private static final String LOCALE_GET = "/api/wxapp/locale/get";
	private static final String WXSCORE_GET="/api/wxapp/score/scoreDocument";
	@Autowired
	protected WxAppAuth wxAppAuth;

	@Autowired
	protected SaasApplication saas;

	@Autowired
	protected DatabaseManager databaseManager;

	/**
	 * 账号登录例外URL
	 */
	protected String[] accountLoginExcept = { URL_LOGIN ,LOCALE_GET,WXSCORE_GET};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI();
		String language = request.getParameter(WxAppAuth.TOKEN);

		// 如果为账户登录例外URL，直接通过
		if (match(this.accountLoginExcept, path)) {
			return true;
		}

		WxAppSessionUser user = wxAppAuth.user();
		if (user == null) {
			return true;
//			errorResponse(request, response, URL_LOGIN,
//					(new JsonResult()).fail(language, JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED));
		}
		databaseManager.switchShopDb(wxAppAuth.shopId());
		return true;
	}

	protected void errorResponse(HttpServletRequest request, HttpServletResponse response, String path,
			JsonResult result) throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(Util.toJson(result));
		writer.close();
		response.flushBuffer();
	}

	public boolean match(String[] regexps, String path) {
		for (String regexp : regexps) {
			if (match(regexp, path)) {
				return true;
			}
		}
		return false;
	}

	public boolean match(String regexp, String path) {
		char asterisk = '*';
		if (regexp.charAt(regexp.length() - 1) == asterisk) {
			regexp = regexp.substring(0, regexp.length() - 1);
			return path.startsWith(regexp);
		} else {
			return regexp.equals(path);
		}
	}
}
