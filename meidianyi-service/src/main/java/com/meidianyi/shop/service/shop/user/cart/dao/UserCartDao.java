package com.meidianyi.shop.service.shop.user.cart.dao;

import com.meidianyi.shop.common.pojo.shop.table.UserCartRecordDo;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.USER_CART_RECORD;

/**
* @author 黄壮壮
*/
@Service
public class UserCartDao extends ShopBaseService {
	/**
	 * 在时间之后有加购行为的用户id列表
	 */
	public List<Integer> getUserIdFromCartStartTime(Timestamp time) {
		return db().select(USER_CART_RECORD.USER_ID).from(USER_CART_RECORD).where(USER_CART_RECORD.CREATE_TIME.ge(time))
				.groupBy(USER_CART_RECORD.USER_ID).fetchInto(Integer.class);
	}

	/**
	 * 在时间之前有加购行为的用户Id列表
	 */
	public List<Integer> getUserIdUtilToCartEndTime(Timestamp time) {
		 return db().select(USER_CART_RECORD.USER_ID).from(USER_CART_RECORD).where(USER_CART_RECORD.CREATE_TIME.le(time))
				.groupBy(USER_CART_RECORD.USER_ID).fetchInto(Integer.class);
	}

	/**
	 *
	 * @param param
	 * @return
	 */
	public Integer save(UserCartRecordDo param){
		return db().newRecord(USER_CART_RECORD,param).insert();
	}

}
