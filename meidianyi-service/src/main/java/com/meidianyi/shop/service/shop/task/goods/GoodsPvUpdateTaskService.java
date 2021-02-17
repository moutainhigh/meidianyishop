package com.meidianyi.shop.service.shop.task.goods;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

import static com.meidianyi.shop.db.shop.tables.UserGoodsRecord.USER_GOODS_RECORD;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;

/**
 * @author: 王兵兵
 * @create: 2020-03-19 19:27
 **/
@Service
public class GoodsPvUpdateTaskService extends ShopBaseService {
    /**
     * 7天访问量
     */
    private static final int SAVE_DAYS = 7;

    /**
     * 统计截至今天0点之前SAVE_DAYS天的商品访问量
     */
    public void updateGoodsPv(){
        Timestamp today = Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_BEGIN, DateUtils.getLocalDateTime()));
        Timestamp daysAgo = DateUtils.getTimeStampPlus(today,-SAVE_DAYS, ChronoUnit.DAYS);
        Result<Record2<Integer, BigDecimal>> res = db().select(USER_GOODS_RECORD.GOODS_ID, DSL.sum(USER_GOODS_RECORD.COUNT)).from(USER_GOODS_RECORD).
            where(USER_GOODS_RECORD.CREATE_TIME.ge(daysAgo)).
            and(USER_GOODS_RECORD.CREATE_TIME.le(today)).
            groupBy(USER_GOODS_RECORD.GOODS_ID).
            fetch();
        res.forEach(g->{
            db().update(GOODS).set(GOODS.PV,g.value2().intValue()).where(GOODS.GOODS_ID.eq(g.value1())).execute();
        });
    }
}
