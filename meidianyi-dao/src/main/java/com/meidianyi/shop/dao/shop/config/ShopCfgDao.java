package com.meidianyi.shop.dao.shop.config;

import static com.meidianyi.shop.db.shop.tables.ShopCfg.SHOP_CFG;

import org.springframework.stereotype.Repository;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;

/**
 *
 * @author lixinguo
 *
 */
@Repository
public class ShopCfgDao extends ShopBaseDao {

	/**
	 * 获取配置key对应value
	 *
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return db().select().from(SHOP_CFG).where(SHOP_CFG.K.eq(key)).fetchAny(SHOP_CFG.V);
	}

	/**
	 * 判断key是否存在 true:存在 false:不存在
	 *
	 * @param key
	 * @return
	 */
	public Boolean exists(String key) {
		return null != db().select().from(SHOP_CFG).where(SHOP_CFG.K.eq(key)).fetchAny();
	}

	/**
	 * 添加配置key对应value
	 *
	 * @param key
	 * @param value
	 * @param db
	 * @return
	 */
	public int add(String key, String value) {
		return db().insertInto(SHOP_CFG, SHOP_CFG.K, SHOP_CFG.V).values(key, value).execute();
	}

	/**
	 * 更新配置key对应value
	 *
	 * @param key
	 * @param value
	 * @param db
	 * @return
	 */
	public int update(String key, String value) {
		return db().update(SHOP_CFG).set(SHOP_CFG.V, value).where(SHOP_CFG.K.eq(key)).execute();
	}
}
