package com.meidianyi.shop.service.shop.member.dao;

import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserAccount.USER_ACCOUNT;

import java.util.List;

import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountPageInfo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountPageListParam;

/**
 * @author 黄壮壮
 * @Date: 2019年11月18日
 * @Description:
 */
@Service
public class UserAccountDao extends ShopBaseService {
	/**
	 * 分页查询会员用户余额详情
	 */
	public PageResult<AccountPageInfo> getPageListOfAccountDetails(AccountPageListParam param) {

		SelectJoinStep<? extends Record> select = db()
				.select(USER.USERNAME, USER.MOBILE, USER_ACCOUNT.asterisk())
				.from(USER_ACCOUNT.join(USER).on(USER.USER_ID.eq(USER_ACCOUNT.USER_ID)));

		buildOptions(select, param);

		select.orderBy(USER_ACCOUNT.CREATE_TIME.desc());
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(), AccountPageInfo.class);
	}

	/**
	 * 分页查询用户余额明细-积分明细时其他查询条件
	 */
	private void buildOptions(SelectJoinStep<? extends Record> select, AccountPageListParam param) {
		logger().info("正在构建查询条件");

		/** 会员id与昵称,优先处理id */
		if (isNotNull(param.getUserId())) {
			select.where(USER_ACCOUNT.USER_ID.eq(param.getUserId()));
		} else if (isNotBlank(param.getUserName())) {

			String likeValue = this.likeValue(param.getUserName());
			List<Integer> ids = db().select(USER.USER_ID).from(USER).where(USER.USERNAME.like(likeValue)).fetch()
					.into(Integer.class);
			logger().info("昵称查询转id列表" + ids);
			select.where(USER_ACCOUNT.USER_ID.in(ids));
		}

		/** 订单号 */
		if (isNotBlank(param.getOrderSn())) {
			select.where(USER_ACCOUNT.ORDER_SN.like(likeValue(param.getOrderSn())));
		}

		/** 开始时间 */
		if (isNotNull(param.getStartTime())) {
			select.where(USER_ACCOUNT.CREATE_TIME.ge(param.getStartTime()));
		}
		/** 结束时间 */
		if (isNotNull(param.getEndTime())) {
			select.where(USER_ACCOUNT.CREATE_TIME.le(param.getEndTime()));
		}
	}

	private boolean isNotBlank(String val) {
		return !org.apache.commons.lang3.StringUtils.isBlank(val);
	}
	private boolean isNotNull(Object obj) {
		return obj != null;
	}
}
