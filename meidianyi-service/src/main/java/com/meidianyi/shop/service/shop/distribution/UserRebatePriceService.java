package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.UserRebatePrice;
import com.meidianyi.shop.db.shop.tables.records.UserRebatePriceRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

import org.jooq.Result;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.tables.UserRebatePrice.USER_REBATE_PRICE;


/**
 * 用户分销价格表
 * @author 王帅
 */
@Service
public class UserRebatePriceService extends ShopBaseService {

    final static private UserRebatePrice TABLE = USER_REBATE_PRICE;

    public Result<UserRebatePriceRecord> getUserRebatePrice(Integer userId, Integer[] prdIds) {
        return db().selectFrom(TABLE).where(TABLE.USER_ID.eq(userId).and(TABLE.PRODUCT_ID.in(prdIds)).and(TABLE.EXPIRE_TIME.gt(DateUtils.getSqlTimestamp()))).fetch();
    }
}
