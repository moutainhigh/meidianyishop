package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meidianyi.shop.controller.BaseController;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;

/**
 * @author zhaojianqiang
 */
public class HelpBaseController extends BaseController {

	private static final String RSHOPID = "shop_id";

	private static final String RUSERID = "user_id";

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 检查传来的shopId和userId
	 */
	public void checkId() {
		if (StringUtils.isNotEmpty(input(RSHOPID))) {
			ShopRecord sRecord = saas.shop.getShopById(Integer.parseInt(input(RSHOPID)));
			if (sRecord == null) {
				log.info("店铺不存在");
				throw new IllegalArgumentException("Invalid shop_id");
			}
		}

		if (StringUtils.isNotEmpty(input(RUSERID))) {
			UserRecord record = saas.getShopApp(Integer.parseInt(input(RSHOPID))).user
					.getUserByUserId(Integer.parseInt(input(RUSERID)));
			if (record == null) {
				log.info("用户不存在");
				throw new IllegalArgumentException("Invalid user_id");
			}
		}
	}

}
