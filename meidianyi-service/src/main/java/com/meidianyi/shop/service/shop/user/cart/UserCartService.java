package com.meidianyi.shop.service.shop.user.cart;

import com.meidianyi.shop.common.pojo.shop.table.UserCartRecordDo;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.shop.user.cart.dao.UserCartDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/10/15 10:35
 */
@Service
public class UserCartService extends ShopBaseService {
	@Autowired UserCartDao userCartDao;
	/**
	 * 在时间之后有加购行为的用户id列表
	 */
	public List<Integer> getUserIdFromCartStartTime(Timestamp time) {
		return userCartDao.getUserIdFromCartStartTime(time);
	}

	/**
	 * 在时间之前有加购行为的用户Id列表
	 */
	public List<Integer> getUserIdUtilToCartEndTime(Timestamp time) {
		return userCartDao.getUserIdUtilToCartEndTime(time);
	}

	public Integer save(UserCartRecordDo param){
		return userCartDao.save(param);
	}

	public Integer addRecord(Integer goodsId, Integer prdId, Integer userId, Integer num, boolean inCartFlag){
		UserCartRecordDo param =new UserCartRecordDo();
		param.setGoodsId(goodsId);
		param.setPrdId(prdId);
		param.setUserId(userId);
		param.setNum(num.shortValue());
		param.setDelFlag((short) (inCartFlag?1:0));
		return save(param);
	}
}
