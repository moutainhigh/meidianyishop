package com.meidianyi.shop.service.shop.recommend;

import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.meidianyi.shop.db.shop.tables.ShopCfg.SHOP_CFG;
/**
 * 好物圈的一些校验方法
 * @author zhaojianqiang
 *
 * 2019年11月18日 下午2:26:01
 */
@Slf4j
public class ShopMallBaseService extends ShopBaseService{
	private static final Byte ONE = 1;
	private static final String FORTYONE = "41";
	/**
	 * 校验
	 * 
	 * @param userId
	 * @return
	 */
	protected String check(Integer userId) {
		String cfg = get("wx_shopping_list_enbaled");
		if (StringUtils.isEmpty(cfg)) {
			log.info("wx_shopping_list_enbaled是空的");
			return null;
		} else {
            String disabledWxShoppingList = "0";
            if (cfg.equals(disabledWxShoppingList)) {
				log.info("wx_shopping_list_enbaled为0");
				return null;
			}
		}
		if (!hasShoppingListAuthority()) {
			log.info("没有微信购物单授权权限");
			return null;
		}
		UserRecord user = saas.getShopApp(getShopId()).user.getUserByUserId(userId);
		String openId = user.getWxOpenid();
		if (StringUtils.isEmpty(openId)) {
			log.info("userId为" + userId + "用户openid为空");
			return null;
		}
		return openId;
	}
	
	
	/**
	 * 校验
	 * 
	 * @param userId
	 * @return
	 */
	protected Boolean checkNoUserId() {
		String cfg = get("wx_shopping_list_enbaled");
		if (StringUtils.isEmpty(cfg)) {
			log.info("wx_shopping_list_enbaled是空的");
			return false;
		} else {
            String disabledWxShoppingList = "0";
            if (cfg.equals(disabledWxShoppingList)) {
				log.info("wx_shopping_list_enbaled为0");
				return false;
			}
		}
		if (!hasShoppingListAuthority()) {
			log.info("没有微信购物单授权权限");
			return false;
		}

		return true;
	}

	/**
	 * 是否有微信购物单授权权限
	 * 
	 * @return
	 */
	private boolean hasShoppingListAuthority() {
		MpAuthShopRecord mp = saas().shop.mp.getAuthShopByShopId(getShopId());
		if (null == mp) {
			log.info("MpAuthShop是空的");
			return false;
		}
		if (!mp.getIsAuthOk().equals(ONE)) {
			log.info("IsAuthOk不为1，为" + mp.getIsAuthOk());
			return false;
		}
		String funcInfo = mp.getFuncInfo();
		log.info("权限有" + funcInfo);
		String[] funcInfoList = funcInfo.split(",");
		for (String str : funcInfoList) {
			if (str.equals(FORTYONE)) {
				return true;
			}
		}
		log.info("没有41权限");
		return false;
	}
	private String get(String key) {
		return db().select().from(SHOP_CFG).where(SHOP_CFG.K.eq(key)).fetchAny(SHOP_CFG.V);
	}

}
