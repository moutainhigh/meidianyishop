package com.meidianyi.shop.dao.shop.distribution;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.Tables.USER_PROMOTION_LANGUAGE;

/**
 * @author panjing
 * @date 2020/7/20 15:22
 */
@Repository
public class UserPromotionLanguageDao extends ShopBaseDao {

    /**
     * 通过userId获取promotionLanguageId
     * @param userId
     * @return
     */
    public Integer getLanIdByUserId(Integer userId) {
        return db().select(USER_PROMOTION_LANGUAGE.LAN_ID).from(USER_PROMOTION_LANGUAGE)
            .where(USER_PROMOTION_LANGUAGE.USER_ID.eq(userId)).fetchAnyInto(Integer.class);
    }
}
