package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.Tables.USER_LOGIN_RECORD;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.main.tables.records.UserLoginRecordRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;

/**
 * 店铺登录信息的
 *
 * @author zhaojianqiang
 *
 *         2019年12月3日 下午4:15:41
 */
@Service
public class UserLoginService extends MainBaseService {


	/**
	 * 切换店铺时候记录登录信息
	 * @param info
	 * @param shop
	 * @param ip
	 */
	public void userLoginRecord(AdminTokenAuthInfo info, ShopRecord shop, String ip) {
		int userId = info.getSysId();
		if (info.isSubLogin()) {
			userId = info.getSubAccountId();
		}
		UserLoginRecordRecord res = db().selectFrom(USER_LOGIN_RECORD)
				.where(USER_LOGIN_RECORD.SYS_ID.eq(info.getSysId()).and(USER_LOGIN_RECORD.SHOP_ID.eq(shop.getShopId()))
						.and(USER_LOGIN_RECORD.USER_IP.eq(ip)).and(USER_LOGIN_RECORD.USER_ID.eq(userId))
						.and(USER_LOGIN_RECORD.ADD_TIME.gt(DateUtils.getLocalTimeDateBySelf("yyyy-MM-dd HH:00:00"))))
				.fetchAny();
		if(null==res) {
			//没有信息，插入新的
			UserLoginRecordRecord record = db().newRecord(USER_LOGIN_RECORD);
			record.setUserName(info.getUserName());
			record.setUserId(userId);
			record.setSysId(info.getSysId());
			record.setShopName(shop.getShopName());
			record.setShopId(shop.getShopId());
			record.setUserIp(ip);
			record.setCount((short)1);
			int insert = record.insert();
			logger().info("登录店铺插入"+shop.getShopId()+"是否成功"+insert);
		}else {
			//有信息
			res.setCount((short)(res.getCount()+1));
			int update = res.update();
			logger().info("登录店铺更新"+shop.getShopId()+"是否成功"+update);
		}
	}
}
