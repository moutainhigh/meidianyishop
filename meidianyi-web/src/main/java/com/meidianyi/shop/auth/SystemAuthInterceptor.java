package com.meidianyi.shop.auth;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.language.LanguageManager;
import com.meidianyi.shop.service.pojo.saas.auth.SystemTokenAuthInfo;
import com.meidianyi.shop.service.saas.SaasApplication;

/****
 ** 
 ** @author 新国
 **
 **/
@Component
public class SystemAuthInterceptor extends HandlerInterceptorAdapter {

	private static final String URL_LOGIN = "/api/system/login";

	@Autowired
	protected SystemAuth systemAuth;

	@Autowired
	protected SaasApplication saas;

	/**
	 * 账号登录例外URL
	 */
	protected String[] accountLoginExcept = { "/api/system/login" };

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI();
		String language = request.getParameter("lang");

		// 切换语言
		LanguageManager.switchLanguage(language);

		// 如果为账户登录例外URL，直接通过
		if (match(this.accountLoginExcept, path)) {
			return true;
		}

		SystemTokenAuthInfo user = systemAuth.user();
		if (user == null) {
			errorResponse(request, response, URL_LOGIN, (new JsonResult()).fail(language, JsonResultCode.CODE_ACCOUNT_SYTEM_LOGIN_EXPIRED));
			return false;
		} else {
			//重置token时间
			systemAuth.reTokenTtl();
			// 账号和店铺都登录，判断路径权限
			if(user.isSubLogin()) {
				// TODO: 加入子账号权限设置
//				SystemChildAccountRecord subAccount = saas.childAccount.getUserFromAccountId(user.subAccountId);
//				if (!saas.menu.isRoleAccess(subAccount.getRoleId(), path)) {
//					errorResponse(request, response, URL_NO_AUTH,
//							JsonResult.fail(language, JsonResultCode.CODE_ROLE__NO_AUTH));
//					return false;
//				}
			}
		}
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
