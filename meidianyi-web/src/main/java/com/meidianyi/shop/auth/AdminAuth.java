package com.meidianyi.shop.auth;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.AuthConfig;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.main.tables.records.UserLoginRecordRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopLoginParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 *
 * @author 新国
 *
 */
@Component
public class AdminAuth {

	@Autowired
	protected AuthConfig authConfig;

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected SaasApplication saas ;

	@Autowired
	protected JedisManager jedis;

	protected Logger log = LoggerFactory.getLogger(AdminAuth.class);

	protected static final String TOKEN = "V-Token";
	protected static final String AUTH_SECRET = "auth.secret";
	protected static final String AUTH_TIMEOUT = "auth.timeout";
	protected static final String TOKEN_PREFIX = "ADM@";

	/**
	 * 登出
	 */
	public boolean logout() {
		AdminTokenAuthInfo info = user();
		if(info == null) {
			return false;
		}
		this.deleteTokenInfo(info);
		return true;
	}

	protected String getToken() {
		return request != null ? request.getHeader(TOKEN) : null;
	}

	/**
	 * 是否有效system登录TOKEN
	 *
	 * @param  token
	 * @return
	 */
	public boolean isValidToken(String token) {
		return StringUtils.isNotEmpty(token) && StringUtils.startsWith(token, TOKEN_PREFIX);
	}

	/**
	 * 登录账户
	 *
	 * @param  param
	 * @return
	 */
	public AdminTokenAuthInfo login(ShopLoginParam param) {
		ShopAccountRecord account = saas.shop.account.getAccountInfo(param.username);
		if (account == null) {
			return null;
		}
		ShopChildAccountRecord subAccount = null;
		if (param.isSubLogin) {
			subAccount = saas.shop.subAccount.verify(account.getSysId(), param.subUsername, param.password);
			if (subAccount == null) {
				return null;
			}
		} else {
			if (!account.getPassword().equals(Util.md5(param.password))) {
				return null;
			}
		}

		AdminTokenAuthInfo info = new AdminTokenAuthInfo();
		info.setSysId(account.getSysId());
		info.setUserName(account.getUserName());
		info.setMobile(account.getMobile());
		info.setSubAccountId(subAccount != null ? subAccount.getAccountId() : 0);
		info.setSubUserName(subAccount != null ? subAccount.getAccountName() : "");
		info.setSubLogin(subAccount != null);
		info.setLoginShopId(0);
		info.setAccountName(subAccount != null ? subAccount.getAccountName() : account.getAccountName());

		// 如果当前登录用户与正在登录的用户相同，则使用当前登录用户的Token
		AdminTokenAuthInfo user = user();
		if(user!=null && user.getSysId().equals(info.getSysId()) && user.getSubAccountId().equals(info.getSubAccountId())) {
			info.setToken(user.getToken());
		}

		this.saveTokenInfo(info);
		return info;
	}

	/**
	 * 保存登录信息
	 *
	 * @param info
	 */
	public void saveTokenInfo(AdminTokenAuthInfo info) {
		if (StringUtils.isBlank(info.getToken())) {
			String loginToken = TOKEN_PREFIX
					+ Util.md5(String.format("admin_login_%d_%d_%s_%d", info.getSysId(), info.getSubAccountId(),
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
	public void deleteTokenInfo(AdminTokenAuthInfo info) {
		jedis.delete(info.token);
	}

	/**
	 * 切换店铺
	 *
	 * @param  shopId
	 * @return
	 */
	public boolean switchShopLogin(Integer shopId) {
		AdminTokenAuthInfo info = user();
		if (info == null) {
			return false;
		}
		if (info.getLoginShopId().equals(shopId)) {
			return true;
		}
		ShopRecord shop = saas.shop.getShopById(shopId);
		if (shop == null) {
			return false;
		}
		if (!shop.getSysId().equals(info.getSysId())) {
			return false;
		}
		if (saas.shop.checkExpire(shopId)) {
			return false;
		}
		Integer roleId = 0;
		if (info.isSubLogin()) {
			roleId = saas.shop.getShopAccessRoleId(info.getSysId(), shopId, info.getSubAccountId());
			if (roleId == -1) {
				return false;
			}
		}
		info.setLoginShopId(shopId);
		info.setShopLogin(true);
		info.setCurrency(shop.getCurrency());
		info.setShopLanguage(shop.getShopLanguage());
		this.saveTokenInfo(info);
		insert(info, shop);
		return true;
	}

	/**
	 * 得到当前登录用户
	 *
	 * @return
	 */
	public AdminTokenAuthInfo user() {
		String token = getToken();
		if (this.isValidToken(token)) {
			String json = jedis.get(token);
			if (!StringUtils.isBlank(json)) {
				return Util.parseJson(json, AdminTokenAuthInfo.class);
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
		AdminTokenAuthInfo info = user();
		if (null != info) {
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
	public int insert(AdminTokenAuthInfo info, ShopRecord shop) {
		UserLoginRecordRecord record = new UserLoginRecordRecord();
		record.setUserName(info.getUserName());
		record.setUserId(info.getSysId());
		if (info.isSubLogin()) {
			record.setUserName(info.getSubUserName());
			record.setUserId(info.getSubAccountId());

		}
		record.setSysId(info.getSysId());
		record.setShopName(shop.getShopName());
		record.setShopId(shop.getShopId());
		record.setUserIp(Util.getCleintIp(request));
		record.setAccountType((byte)0);
		return saas.shop.insertUserLoginRecord(record);
	}

	/**
	 * 更新accountName
	 * @param accountName
	 */
	public void updateAccountName(String accountName) {
		AdminTokenAuthInfo user = user();
		user.setAccountName(accountName);
		jedis.set(getToken(), Util.toJson(user));
	}
}
