package com.meidianyi.shop.service.shop.user.user;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.db.shop.tables.records.UserLoginRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.account.UserLoginRecordVo;
import com.meidianyi.shop.service.shop.user.user.dao.UserLoginRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.USER_LOGIN_RECORD;

/**
 *
 * @author zhaojianqiang
 *
 *         2019年10月10日 下午3:32:40
 */
@Service
public class UserLoginRecordService extends ShopBaseService {
	@Autowired UserLoginRecordDao userLoginRecordDao;

	public void userLoginRecord(Integer userId, UserLoginRecordVo vo) {
		logger().info("记录小程序登录");
		UserLoginRecordRecord res = db().selectFrom(USER_LOGIN_RECORD).where(USER_LOGIN_RECORD.USER_ID.eq(userId))
				.and(USER_LOGIN_RECORD.CREATE_TIME.gt(DateUtils.getLocalTimeDateBySelf("yyyy-MM-dd HH:00:00"))).fetchAny();
		// 有记录更新登陆次数，没有记录加记录 一小时一条数据
		UserLoginRecordRecord record2=db().newRecord(USER_LOGIN_RECORD);
		FieldsUtil.assignNotNull(vo, record2);
		if (res == null) {
			record2.setCount(1);
			int insert = record2.insert();
			logger().info("插入小程序登录"+insert);
		} else {
			res.setCount(res.getCount() + 1);
			if (vo.getLat() != null && (!vo.getLat().equals(res.getLat()))) {
				res.setLat(vo.getLat());
				res.setLng(vo.getLng());
			}
			int update = res.update();
			logger().info("更新小程序登录"+update);
		}
	}


	/**
	 * 获取用户登录记录的地址信息
	 * @param userId  用户id
	 * @return district
	 */
	public Integer getUserLoginRecordDistrictCode(Integer userId){

		return db().select(USER_LOGIN_RECORD.DISTRICT_CODE).from(USER_LOGIN_RECORD)
				.where(USER_LOGIN_RECORD.USER_ID.eq(userId))
				.and(USER_LOGIN_RECORD.DISTRICT_CODE.notEqual("").or(USER_LOGIN_RECORD.DISTRICT_CODE.isNotNull()))
				.orderBy(USER_LOGIN_RECORD.CREATE_TIME.desc()).fetchOneInto(Integer.class);
	}

	/**
	 * 获取从该时间开始登录的用户Id
	 */
	public List<Integer> getUserIdFromLoginStartTime(Timestamp time) {
		return userLoginRecordDao.getUserIdFromLoginStartTime(time);
	}

	/**
	 * 获取从该时间之前登录的用户Id
	 */
	public List<Integer> getUserIdUtilToLoginEndTime(Timestamp time) {
		return userLoginRecordDao.getUserIdUtilToLoginEndTime(time);
	}

    /**
     * 用户最近的一次登录记录
     * @param userId
     * @return
     */
	public UserLoginRecordRecord getUserLoginRecord(Integer userId){
	    return db().selectFrom(USER_LOGIN_RECORD).where(USER_LOGIN_RECORD.USER_ID.eq(userId)).orderBy(USER_LOGIN_RECORD.CREATE_TIME.desc()).fetchAny();
    }
}
