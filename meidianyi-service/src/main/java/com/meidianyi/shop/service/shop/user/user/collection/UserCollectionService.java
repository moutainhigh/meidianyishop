package com.meidianyi.shop.service.shop.user.user.collection;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.UserCollection.USER_COLLECTION;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;

/**
 * 商品收藏
 * 
 * @author zhaojianqiang
 *
 *         2019年10月12日 下午2:23:40
 */
@Service
public class UserCollectionService extends ShopBaseService {

	/**
	 * 获取用户收藏数量
	 * @param userId
	 * @return
	 */
	public Integer getUserCollectNumber(Integer userId) {
		return db().selectCount().from(USER_COLLECTION).leftJoin(GOODS).on(USER_COLLECTION.GOODS_ID.eq(GOODS.GOODS_ID))
				.where(GOODS.DEL_FLAG.eq((byte) 0).and(USER_COLLECTION.USER_ID.eq(userId))).fetchOne()
				.into(Integer.class);
	}

}
