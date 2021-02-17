package com.meidianyi.shop.auth;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.AuthConfig;
import com.meidianyi.shop.db.main.tables.records.SystemChildAccountRecord;
import com.meidianyi.shop.db.main.tables.records.SystemUserRecord;
import com.meidianyi.shop.db.main.tables.records.UserLoginRecordRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.saas.auth.SystemLoginParam;
import com.meidianyi.shop.service.pojo.saas.auth.SystemTokenAuthInfo;
import com.meidianyi.shop.service.saas.SaasApplication;

/**
 * 
 * @author 新国
 *
 */
@Component
public class SystemAuth {

	@Autowired
	protected AuthConfig authConfig;

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected SaasApplication saas;

	@Autowired
	protected JedisManager jedis;

	protected Logger log = LoggerFactory.getLogger(SystemAuth.class);

	protected static final String TOKEN = "V-Token";
	protected static final String AUTH_SECRET = "auth.secret";
	protected static final String AUTH_TIMEOUT = "auth.timeout";
	protected static final String TOKEN_PREFIX = "SYS@";

	/**
	 * 登出
	 */
	public boolean logout() {
		SystemTokenAuthInfo info = user();
		if (info == null) {
			return false;
		}
		this.deleteTokenInfo(info);
		return true;
	}

	protected String getToken() {
		return request.getHeader(TOKEN);
	}

	/**
	 * 是否有效system登录TOKEN
	 * 
	 * @param token
	 * @return
	 */
	public boolean isValidToken(String token) {
		return StringUtils.isNotEmpty(token) && StringUtils.startsWith(token, TOKEN_PREFIX);
	}

	/**
	 * 登录账户
	 * 
	 * @param param
	 * @return
	 */
	public SystemTokenAuthInfo login(SystemLoginParam param) {
		SystemChildAccountRecord subAccount = null;
		SystemUserRecord account = saas.sysUser.verify(param.username, param.password);
		if (account == null) {
			subAccount = saas.childAccount.verify(param.username, param.password);
			if (subAccount == null) {
				return null;
			}
		}

		SystemTokenAuthInfo info = new SystemTokenAuthInfo();
		info.setSystemUserId(account != null ? account.getSystemUserId() : 0);
		info.setUserName(account != null ? account.getUserName() : "");
		info.setSubAccountId(subAccount != null ? subAccount.getAccountId() : 0);
		info.setSubUserName(subAccount != null ? subAccount.getAccountName() : "");
		info.setSubLogin(subAccount != null);
		// 如果当前登录用户与正在登录的用户相同，则使用当前登录用户的Token
		SystemTokenAuthInfo user = user();
		if (user != null && user.getSystemUserId().equals(info.getSystemUserId())
				&& user.getSubAccountId().equals(info.getSubAccountId())) {
			info.setToken(user.getToken());
		}
		this.saveTokenInfo(info);
		insert(info);
		return info;
	}

	/**
	 * 保存登录信息
	 * 
	 * @param info
	 */
	protected void saveTokenInfo(SystemTokenAuthInfo info) {
		if (StringUtils.isBlank(info.getToken())) {
			String loginToken = TOKEN_PREFIX
					+ Util.md5(String.format("system_login_%d_%d_%s_%d", info.getSystemUserId(), info.getSubAccountId(),
							Util.randomId(), Calendar.getInstance().getTimeInMillis()));
			info.setToken(loginToken);
		}
		jedis.set(info.token, Util.toJson(info), authConfig.getTimeout());
	}

	/**
	 * 删除登录信息
	 * 
	 * @param info
	 */
	protected void deleteTokenInfo(SystemTokenAuthInfo info) {
		jedis.delete(info.token);
	}

	/**
	 * 得到当前登录用户
	 * 
	 * @return
	 */
	public SystemTokenAuthInfo user() {
		String token = getToken();
		if (this.isValidToken(token)) {
			String json = jedis.get(token);
			if (!StringUtils.isBlank(json)) {
				return Util.parseJson(json, SystemTokenAuthInfo.class);
			}
		}
		return null;
	}

	/**
	 * 重置token的存活时间
	 * 
	 * @param token
	 */
	public void reTokenTtl() {
		SystemTokenAuthInfo info = user();
		if (info != null) {
			this.saveTokenInfo(info);
		}
	}
	
	
	/**
	 * 登录时间表更新
	 * @param info
	 * @param shop
	 * @return 
	 * @return
	 */
	public int insert(SystemTokenAuthInfo info) {
		//saas.userLoginService.userLoginRecord(info, shop, Util.getCleintIp(request));
		UserLoginRecordRecord record = new UserLoginRecordRecord();
		record.setUserName(info.getUserName());
		record.setUserId(info.getSystemUserId());
		if (info.isSubLogin()) {
			record.setUserName(info.getSubUserName());
			record.setUserId(info.getSubAccountId());
			
		}
		record.setSysId(info.getSystemUserId());
		record.setShopName("");
		record.setShopId(0);
		record.setUserIp(Util.getCleintIp(request));
		record.setAccountType((byte)1);
		return saas.shop.insertUserLoginRecord(record);
	}
}
