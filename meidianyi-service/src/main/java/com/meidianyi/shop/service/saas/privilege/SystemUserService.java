package com.meidianyi.shop.service.saas.privilege;

import static com.meidianyi.shop.db.main.tables.SystemUser.SYSTEM_USER;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.SystemUserRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;

/**
 * 
 * @author 新国
 *
 */
@Service

public class SystemUserService extends MainBaseService {

	public SystemUserRecord verify(String username, String password) {
		SystemUserRecord user = db()
				.selectFrom(SYSTEM_USER)
				.where(SYSTEM_USER.USER_NAME.eq(username))
				.or(SYSTEM_USER.MOBILE.eq(username))
				.fetchAny();
		if (user != null) {
			String md5Pass = Util.md5(password);
			if (user.getPassword().equals(md5Pass)) {
				return user;
			}
		}
		return null;
	}
	
	public SystemUserRecord checkByIdAndNameOnMain(String username, Integer systemUserId) {
		SystemUserRecord user = db()
				.selectFrom(SYSTEM_USER)
				.where(SYSTEM_USER.USER_NAME.eq(username)).and(SYSTEM_USER.SYSTEM_USER_ID.eq(systemUserId))
				.fetchAny();
		if (user != null) {
			return user;
		}
		return null;
	}	

	public boolean checkNewPass(String oldPassword, Integer userId) {
		SystemUserRecord user = db().selectFrom(SYSTEM_USER)
				.where(SYSTEM_USER.SYSTEM_USER_ID.eq(userId))
				.and(SYSTEM_USER.PASSWORD.eq(Util.md5(oldPassword)))
				.fetchAny();
		return user != null;
	}

	public int updateNewPass(String newPassword, Integer userId) {
		return db().update(SYSTEM_USER).set(SYSTEM_USER.PASSWORD, Util.md5(newPassword))
				.where(SYSTEM_USER.SYSTEM_USER_ID.eq(userId)).execute();
	}

	public int updateLoginIp(String lastLoginIp, Integer userId) {
		return db().update(SYSTEM_USER).set(SYSTEM_USER.LAST_LOGIN_IP, lastLoginIp)
				.where(SYSTEM_USER.SYSTEM_USER_ID.eq(Integer.valueOf(userId))).execute();
	}
}
