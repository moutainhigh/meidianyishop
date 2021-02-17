package com.meidianyi.shop.dao.shop.distribution;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.Tables.PROMOTION_LANGUAGE;

/**
 * @author panjing
 * @date 2020/7/20 15:15
 */
@Repository
public class PromotionLanguageDao extends ShopBaseDao {

    /**
     * 获取分销推广语
     * @param id
     * @return
     */
    public String getPromotionLanguage(Integer id) {
        return db().select(PROMOTION_LANGUAGE.PROMOTION_LANGUAGE_).from(PROMOTION_LANGUAGE)
            .where(PROMOTION_LANGUAGE.ID.eq(id)).and(PROMOTION_LANGUAGE.DEL_FLAG.eq((byte)0)).fetchAnyInto(String.class);
    }
}
