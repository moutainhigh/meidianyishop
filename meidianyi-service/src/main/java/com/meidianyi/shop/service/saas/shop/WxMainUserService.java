package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.User.USER;
import static com.meidianyi.shop.db.main.tables.UserDetail.USER_DETAIL;

import java.util.Map;

import org.jooq.SortField;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.main.tables.UserDetail;
import com.meidianyi.shop.db.main.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.main.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.wxapp.account.UserDetailMainVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserMainVo;
/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月14日 上午10:31:02
 */
@Service
public class WxMainUserService extends MainBaseService {
	public static final Byte SYCUPDATE = 1;
	public static final Byte SYCINSERT = 0;

	/**
	 * 同步到主库
	 * 
	 * @param sendRecord
	 * @param type
	 */
	public int[] syncMainUser(Integer shopId, Integer userId,com.meidianyi.shop.db.shop.tables.records.UserRecord infoRecord) {
		UserMainVo info = infoRecord.into(UserMainVo.class);
		logger().info("User同步开始到主库,shopId:"+shopId+" userId:"+userId);
		UserRecord record = db().selectFrom(USER).where(USER.SHOP_ID.eq(shopId).and(USER.USER_ID.eq(userId))).fetchAny();
		int[] success =new int[2];
		if (record != null) {
			// 更新
			logger().info("更新");
			info.setId(record.getId());
			info.setShopId(shopId);
			UserRecord newRecord = db().newRecord(USER, info);
			int executeUpdate = newRecord.update();
			logger().info("更新User，结果" + executeUpdate);
			success[0]=executeUpdate;
		} else {
			// 插入
			logger().info("插入");
			info.setShopId(shopId);
			UserRecord newRecord = db().newRecord(USER, info);
			int insert = newRecord.insert();
			logger().info("插入User，结果" + insert);
			success[1]=insert;
		}
		return success;

	}

	/**
	 * 同步到主库
	 * 
	 * @param sendRecord
	 * @param type
	 */
	public int[] syncMainUserDetail(Integer shopId, Integer userId,com.meidianyi.shop.db.shop.tables.records.UserDetailRecord infoRecord) {
		UserDetailMainVo info=infoRecord.into(UserDetailMainVo.class);
		logger().info("UserDetail同步开始到主库,shopId:"+shopId+" userId:"+userId);
		int[] success =new int[2];
		UserDetailRecord record = db().selectFrom(USER_DETAIL).where(USER_DETAIL.SHOP_ID.eq(shopId).and(USER_DETAIL.USER_ID.eq(userId)))
				.fetchAny();
		if (record != null) {
			// 更新
			logger().info("更新");
			info.setId(record.getId());
			info.setShopId(shopId);
			UserDetailRecord newRecord = db().newRecord(USER_DETAIL, info);
			int executeUpdate = newRecord.update();
			logger().info("更新UserDetail，结果" + executeUpdate);
			success[0]=executeUpdate;
		} else {
			// 插入
			logger().info("插入");
			info.setShopId(shopId);
			UserDetailRecord newRecord = db().newRecord(USER_DETAIL, info);
			int insert = newRecord.insert();
			logger().info("插入UserDetail，结果" + insert);
			success[1]=insert;
		}
		return success;
	}
	
	/**
	 * Map类型更新User
	 * @param map
	 * @param shopId
	 * @param userId
	 */
	public void updateMainUser(Map<?, ?> map,Integer shopId, Integer userId) {
		db().update(USER).set(map).where(USER.SHOP_ID.eq(shopId).and(USER.USER_ID.eq(userId))).execute();
	}
	
	/**
	 * Map类型更新UserDeatil
	 * @param map
	 * @param shopId
	 * @param userId
	 */
	public void updateMainUserDetail(Map<?, ?> map,Integer shopId, Integer userId) {
		db().update(USER).set(map).where(USER.SHOP_ID.eq(shopId).and(USER.USER_ID.eq(userId))).execute();
	}
}
