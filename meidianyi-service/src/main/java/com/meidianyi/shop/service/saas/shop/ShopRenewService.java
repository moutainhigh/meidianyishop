package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.Shop.SHOP;
import static com.meidianyi.shop.db.main.tables.ShopAccount.SHOP_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.ShopRenew.SHOP_RENEW;
import static com.meidianyi.shop.db.main.tables.SystemChildAccount.SYSTEM_CHILD_ACCOUNT;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.tools.Convert;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.ShopAccount;
import com.meidianyi.shop.db.main.tables.ShopRenew;
import com.meidianyi.shop.db.main.tables.records.ShopRenewRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.auth.SystemTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopRenewListParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopRenewReq;
import com.meidianyi.shop.service.pojo.shop.auth.ShopRenewVo;

/**
 *
 * @author 新国
 *
 */
@Service

public class ShopRenewService extends MainBaseService {

	public void insertRenewDate() {

	}

	public Integer getShopNumber(Integer sysId) {
		return (Integer) db().select(DSL.count(SHOP_RENEW.SYS_ID)).from(SHOP_RENEW).where(SHOP_RENEW.SYS_ID.eq(sysId))
				.fetchAny(0);
	}

	public Result<Record> getRenewList(Integer sysId) {
		return db().select().from(SHOP_RENEW).where(SHOP_RENEW.SYS_ID.eq(sysId)).orderBy(SHOP_RENEW.EXPIRE_TIME.desc())
				.fetch();
	}

	public PageResult<ShopRenewVo> getShopRenewList(ShopRenewListParam sParam) {
		ShopRenew a = SHOP_RENEW.as("a");
		ShopAccount b = SHOP_ACCOUNT.as("b");
		SelectWhereStep<? extends Record> select = db().select(a.ID,a.SHOP_ID,a.SYS_ID,a.MOBILE,a.RENEW_MONEY,a.RENEW_DATE,a.EXPIRE_TIME,
				a.OPERATOR,a.RENEW_DESC,a.RENEW_TYPE,a.RENEW_DURATION,a.SEND_TYPE,a.SEND_CONTENT,b.ACCOUNT_NAME).from(a, b);
		select.where(a.SHOP_ID.eq(sParam.getShopId()).and(a.SYS_ID.eq(b.SYS_ID)));
		select.orderBy(a.ID.desc());

		PageResult<ShopRenewVo> pageResult = this.getPageResult(select, sParam.getCurrentPage(), sParam.getPageRows(),
				ShopRenewVo.class);
		for(ShopRenewVo sRenewVo:pageResult.dataList) {
			if(sRenewVo.getOperator().equals(0)) {
				sRenewVo.setOperatorName("system");
			} else {
				Record1<String> record1 = db().select(SYSTEM_CHILD_ACCOUNT.ACCOUNT_NAME).from(SYSTEM_CHILD_ACCOUNT)
						.where(SYSTEM_CHILD_ACCOUNT.ACCOUNT_ID.eq(sRenewVo.getOperator())).fetchAny();
				sRenewVo.setOperatorName(record1.getValue(SYSTEM_CHILD_ACCOUNT.ACCOUNT_NAME));
			}

		}
		return pageResult;
	}

	public BigDecimal getRenewTotal(Integer sysId) {
		Object total = db().select(DSL.sum(SHOP_RENEW.RENEW_MONEY)).from(SHOP_RENEW).where(SHOP_RENEW.SYS_ID.eq(sysId))
				.fetchAny(0);

		return total == null ? new BigDecimal("0") : Convert.convert(total, BigDecimal.class);
	}

	public BigDecimal getShopRenewTotal(Integer shopId) {
		Object total = db().select(DSL.sum(SHOP_RENEW.RENEW_MONEY)).from(SHOP_RENEW)
				.where(SHOP_RENEW.SHOP_ID.eq(shopId)).fetchAny(0);

		return total == null ? new BigDecimal(0) : Convert.convert(total, BigDecimal.class);
	}

	public Timestamp getShopRenewExpireTime(Integer shopId) {
		return  db().selectFrom(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchAny(SHOP.EXPIRE_TIME);
//		return db().select().from(SHOP_RENEW).where(SHOP_RENEW.SHOP_ID.eq(shopId))
//				.orderBy(SHOP_RENEW.EXPIRE_TIME.desc()).fetchAny(SHOP_RENEW.EXPIRE_TIME);
	}

	public int insertShopRenew(ShopRenewReq sReq,SystemTokenAuthInfo info) {
		ShopRenewRecord sRecord=db().newRecord(SHOP_RENEW,sReq);
		// 续费类型：1续费，2试用，3赠送，4退款
        byte renewTypeRefund = (byte) 4;
        if(sReq.getRenewType().equals(renewTypeRefund)){
			logger().info("退款{}",sReq.getRenewMoney());
			//改为负数
			sRecord.setRenewMoney(sReq.getRenewMoney().negate());
		}
		sRecord.setRenewDuration(sReq.getYear()+","+sReq.getMonth());
		sRecord.setSendContent(sReq.getSendYear()+","+sReq.getSendMonth());
		sRecord.setRenewDate(DateUtils.getSqlTimestamp());
		if(info.isSubLogin()) {
			//子账户登录
			sRecord.setOperator(info.getSubAccountId());
		}else {
			sRecord.setOperator(0);
		}
		Integer shopId = sReq.getShopId();
		 this.transaction(()->{
			 int execute = db().update(SHOP).set(SHOP.EXPIRE_TIME,sReq.getExpireTime()).where(SHOP.SHOP_ID.eq(shopId)).execute();
			 logger().info("店铺{}续费插入主库：{}",shopId,execute);
			 int executeInsert = db().executeInsert(sRecord);
			 logger().info("店铺{}续费插入从库：{}",shopId,executeInsert);
		 });
		return 1;
	}
}
