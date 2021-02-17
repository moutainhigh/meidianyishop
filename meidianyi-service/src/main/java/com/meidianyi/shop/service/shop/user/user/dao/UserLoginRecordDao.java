package com.meidianyi.shop.service.shop.user.user.dao;

import static com.meidianyi.shop.db.shop.Tables.USER_LOGIN_RECORD;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;

/**
 * @author 黄壮壮
 * @Date: 2019年11月22日
 * @Description:
 */
@Service
public class UserLoginRecordDao extends ShopBaseService {
	/**
	 * 获取从该时间开始登录的用户Id
	 */
	public List<Integer> getUserIdFromLoginStartTime(Timestamp time) {
		logger().info("登录开始时间："+time);
		return db().select(USER_LOGIN_RECORD.USER_ID).from(USER_LOGIN_RECORD)
				.where(USER_LOGIN_RECORD.CREATE_TIME.ge(time)).groupBy(USER_LOGIN_RECORD.USER_ID)
				.fetchInto(Integer.class);
	}
	
	/**
	 * 获取从该时间之前登录的用户Id
	 */
	public List<Integer> getUserIdUtilToLoginEndTime(Timestamp time) {
		logger().info("登录结束时间");
		return db().select(USER_LOGIN_RECORD.USER_ID).from(USER_LOGIN_RECORD)
				.where(USER_LOGIN_RECORD.CREATE_TIME.le(time)).groupBy(USER_LOGIN_RECORD.USER_ID)
				.fetchInto(Integer.class);
	}

}
