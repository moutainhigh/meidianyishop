package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.shop.tables.UserTotalFanli;
import com.meidianyi.shop.db.shop.tables.records.UserTotalFanliRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.UserTotalFanliVo;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTOR_LEVEL;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;
import static com.meidianyi.shop.db.shop.Tables.USER_TOTAL_FANLI;
/**
 * @author 黄壮壮
 * 	 用户返利服务
 */
@Service
public class UserTotalFanliService extends ShopBaseService {

    final static UserTotalFanli TABLE = USER_TOTAL_FANLI;

	public UserTotalFanliVo getUserRebate(Integer userId) {
		  UserTotalFanliVo res = db().select(USER_DETAIL.USERNAME,USER_DETAIL.USER_AVATAR,USER_TOTAL_FANLI.asterisk())
			.from(USER_DETAIL)
			.leftJoin(USER_TOTAL_FANLI).on(USER_DETAIL.USER_ID.eq(USER_TOTAL_FANLI.USER_ID))
			.leftJoin(DISTRIBUTOR_LEVEL).on(DSL.coerce(DISTRIBUTOR_LEVEL.LEVEL_ID,Integer.class).eq(USER_TOTAL_FANLI.USER_ID))
			.where(USER_DETAIL.USER_ID.eq(userId))
			.fetchAnyInto(UserTotalFanliVo.class);
		  return res;
	}
	
	/**
	 * 	累计获得佣金数
	 */
	public BigDecimal getTotalMoney(Integer userId) {
		UserTotalFanliVo userRebate = getUserRebate(userId);
		if(userRebate != null && userRebate.getTotalMoney() != null) {
			return userRebate.getTotalMoney();
		}else {
			return  BigDecimal.ZERO;
		}
	}

    /**
     *  @param userId
     * @param money
     * @param inviteCount
     */
    public void updateTotalRebate(Integer userId, BigDecimal money, int inviteCount) {
        UserTotalFanliRecord record = db().selectFrom(TABLE).where(TABLE.USER_ID.eq(userId).and(TABLE.USER_ID.eq(userId))).fetchOne();
        if(record == null) {
            UserTotalFanliRecord insert = db().newRecord(TABLE);
            insert.setUserId(userId);
            insert.setMobile("");
            insert.setSublayerNumber(inviteCount);
            insert.setTotalMoney(money);
            insert.setFinalMoney(money);
            insert.insert();
        }else {
            record.setSublayerNumber(inviteCount);
            record.setTotalMoney(BigDecimalUtil.add(record.getTotalMoney(), money));
            record.setFinalMoney(BigDecimalUtil.add(record.getFinalMoney(), money));
            record.update();
        }
    }

    public void blockedOrThawAmount(Integer userId, BigDecimal money) {
        db().update(TABLE).set(TABLE.TOTAL_MONEY, money).where(TABLE.USER_ID.eq(userId)).execute();
    }
}
