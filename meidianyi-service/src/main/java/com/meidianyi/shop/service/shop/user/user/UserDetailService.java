package com.meidianyi.shop.service.shop.user.user;

import org.springframework.stereotype.Service;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;

import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

/**
 * @author lixinguo
 */
@Service
public class UserDetailService extends ShopBaseService {
	
	public UserDetailRecord getUserDetailByUserId(Integer userId) {
		return db().fetchAny(USER_DETAIL,USER_DETAIL.USER_ID.eq(userId));
	}
	
	public int updateRow(UserDetailRecord userDetailRecord) {
		return db().update(USER_DETAIL).set(userDetailRecord).where(USER_DETAIL.USER_ID.eq(userDetailRecord.getUserId())).execute();
		//TODO 更新主库的
	}
}
