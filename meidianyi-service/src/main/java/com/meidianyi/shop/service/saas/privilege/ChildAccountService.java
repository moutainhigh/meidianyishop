package com.meidianyi.shop.service.saas.privilege;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.SystemChildAccountRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;

import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.meidianyi.shop.db.main.tables.SystemChildAccount.SYSTEM_CHILD_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.SystemRole.SYSTEM_ROLE;

/**
 * 
 * @author 新国
 *
 */
@Service

public class ChildAccountService extends MainBaseService {

	public SystemChildAccountRecord verify(String username, String password) {
		SystemChildAccountRecord user = db().selectFrom(SYSTEM_CHILD_ACCOUNT)
				.where(SYSTEM_CHILD_ACCOUNT.ACCOUNT_NAME.eq(username)).or(SYSTEM_CHILD_ACCOUNT.MOBILE.eq(username))
				.fetchAny();
		if (user != null) {
			if (user.getAccountPwd().equals(Util.md5(password))) {
				return user;
			}
		}

		// db().selectFrom(SYSTEM_CHILD_ACCOUNT).limit(0, 20).fetch();
		return null;
	}

	public SystemChildAccountRecord checkByIdAndNameOnChild(Integer accountId, String username, Integer sysId) {
		SystemChildAccountRecord user = db().selectFrom(SYSTEM_CHILD_ACCOUNT)
				.where(SYSTEM_CHILD_ACCOUNT.ACCOUNT_NAME.eq(username))
				.and(SYSTEM_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId)).and(SYSTEM_CHILD_ACCOUNT.SYSTEM_USER_ID.eq(sysId))
				.fetchAny();
		if (user != null) {
			return user;
		}
		return null;
	}

	public SystemChildAccountRecord getUserFromAccountName(String username) {
		return db().selectFrom(SYSTEM_CHILD_ACCOUNT)
				.where(SYSTEM_CHILD_ACCOUNT.ACCOUNT_NAME.eq(username))
				.fetchAny();
	}
	public SystemChildAccountRecord getUserFromAccountId(Integer accountId) {
		return db().selectFrom(SYSTEM_CHILD_ACCOUNT)
				.where(SYSTEM_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId))
				.fetchAny();
	}

	public SystemChildAccountRecord getUserFromMobile(String mobile) {
		return db().selectFrom(SYSTEM_CHILD_ACCOUNT)
				.where(SYSTEM_CHILD_ACCOUNT.MOBILE.eq(mobile))
				.fetchAny();
	}

	public Result<Record> getPageList(Integer pageNo, Integer pageRows) {
		return db().select().from(SYSTEM_CHILD_ACCOUNT)
				.join(SYSTEM_ROLE)
				.on(SYSTEM_CHILD_ACCOUNT.ROLE_ID.eq(SYSTEM_ROLE.ROLE_ID))
				.limit(pageNo * pageRows, pageRows).fetch();

	}

    public List<SystemChildAccountRecord> getByAccountIds(List<Integer> accountIds) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return Collections.emptyList();
        }
        return db().selectFrom(SYSTEM_CHILD_ACCOUNT).where(SYSTEM_CHILD_ACCOUNT.ACCOUNT_ID.in(accountIds)).fetch();
    }
}
