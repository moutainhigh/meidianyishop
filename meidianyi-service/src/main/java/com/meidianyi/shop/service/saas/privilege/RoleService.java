package com.meidianyi.shop.service.saas.privilege;

import static com.meidianyi.shop.db.main.tables.SystemRole.SYSTEM_ROLE;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.main.tables.records.SystemRoleRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;

/**
 * 
 * @author 新国
 *
 */
@Service

public class RoleService extends MainBaseService {


	public SystemRoleRecord getRole(Integer roleId) {
		return db().selectFrom(SYSTEM_ROLE).where(SYSTEM_ROLE.ROLE_ID.eq(roleId)).fetchAny();
	}
}
