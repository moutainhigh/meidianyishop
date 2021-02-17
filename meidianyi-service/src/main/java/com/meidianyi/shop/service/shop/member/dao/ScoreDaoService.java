package com.meidianyi.shop.service.shop.member.dao;


import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.UserScoreRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionName;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreSignParam;

import static org.jooq.impl.DSL.sum;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import static com.meidianyi.shop.db.shop.Tables.USER_SCORE;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_TAG;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.REFUND_SCORE_STATUS;

/**
* @author 黄壮壮
* @Date: 2019年10月12日
* @Description: 积分Dao层
*/
@Service
public class ScoreDaoService extends ShopBaseService {
	/** -积分有效的状态 列表*/
	final static List<Byte> AVAILABLE_SCORE_STATUS_LIST = new ArrayList<>(Arrays.asList(NO_USE_SCORE_STATUS, REFUND_SCORE_STATUS ));
	/**
	 * 连续签到天数
	 */
	public final static String  SIGN_DAY = "day";
	/**
	 * 连续签到获得积分数
	 */
	public final static String SIGN_SCORE = "score";
	/**
	 * 计算用户的所有累积分
	 * @param id
	 * @return
	 */
	public Integer calculateAccumulationScore(Integer userId) {
		 Integer accumulationScore = db().select(sum(USER_SCORE.SCORE))
									 	.from(USER_SCORE)
									 	.where(USER_SCORE.USER_ID.eq(userId))
									 	.and(USER_SCORE.STATUS.notIn(REFUND_SCORE_STATUS))
									 	.and(USER_SCORE.SCORE.greaterThan(0))
									 	.fetchAnyInto(Integer.class);
		 logger().info("计算用户累积积分为： "+accumulationScore);
		 return isNotNull(accumulationScore)?accumulationScore:NumberUtils.INTEGER_ZERO;
	}

	/**
	 * 计算用户的所有可使用的积分
	 */
	public Integer calculateAvailableScore(Integer userId) {

		/** 根据用户id,status,有效期 */
		Integer availableScore = db().select(DSL.sum(USER_SCORE.USABLE_SCORE))
									.from(USER_SCORE)
									.where(USER_SCORE.USER_ID.eq(userId))
									.and(USER_SCORE.STATUS.in(AVAILABLE_SCORE_STATUS_LIST))
									.and(USER_SCORE.EXPIRE_TIME.ge(DateUtils.getLocalDateTime()).or(USER_SCORE.EXPIRE_TIME.isNull()))
									.fetchOptionalInto(Integer.class)
									.orElse(NumberUtils.INTEGER_ZERO);
		logger().info("计算所有可用积分为： "+availableScore);
		return availableScore;
	}

	/**
	 * 计算从现在到指定时间的可用积分
	 * @param endTime 指定的时间
	 * @return Integer 积分
	 */
	public Integer calculateWillExpireSoonScore(Timestamp endTime,Integer userId) {
		Integer willExpireSoonScore = db().select(sum(USER_SCORE.USABLE_SCORE))
			.from(USER_SCORE)
			.where(USER_SCORE.USER_ID.eq(userId))
			.and(USER_SCORE.STATUS.in(AVAILABLE_SCORE_STATUS_LIST))
			.and(USER_SCORE.EXPIRE_TIME.between(DateUtils.getLocalDateTime(), endTime))
			.fetchOptionalInto(Integer.class)
			.orElse(NumberUtils.INTEGER_ZERO);

		logger().info("计算在指定时间 "+endTime+" 所有可用积分为： "+willExpireSoonScore);
		return willExpireSoonScore;
	}

	/**
	 * 获取一条用户可用的最早积分记录
	 */
	public UserScoreRecord getTheEarliestUsableUserScoreRecord(Integer userId) {
		return db().selectFrom(USER_SCORE)
			.where(USER_SCORE.USER_ID.eq(userId))
			.and(USER_SCORE.USABLE_SCORE.greaterThan(0)).and(USER_SCORE.STATUS.in(AVAILABLE_SCORE_STATUS_LIST))
			.and(USER_SCORE.EXPIRE_TIME.ge(DateUtils.getLocalDateTime()).or(USER_SCORE.EXPIRE_TIME.isNull()))
			.orderBy(USER_SCORE.CREATE_TIME)
			.fetchAny();
	}

	private boolean isNotNull(Object obj) {
		return obj!=null;
	}

	/**
	 * 获取所有签到积分
	 */
	public PageResult<? extends Record> getAllSignList(ScoreSignParam param) {
		Condition condition = buildSignPageCondition(param);
		// 定义查询的数据属性
		Field<?>[] fields = USER_SCORE.fields();
		List<Field<?>> f = new ArrayList<>(Arrays.asList(fields));
		f.add(USER.USERNAME);
		f.add(USER.MOBILE);
		Field<?>[] myFields = f.toArray(new Field<?>[0]);
		Record myRecord = db().newRecord(myFields);

		SelectSeekStep1<Record, Timestamp> select = db().select(myFields)
			.from(USER_SCORE.innerJoin(USER).on(USER_SCORE.USER_ID.eq(USER.USER_ID)).leftJoin(USER_TAG).on(USER_SCORE.USER_ID.eq(USER_TAG.USER_ID)))
			.where(USER_SCORE.DESC.eq(VersionName.SUB_3_SIGN_SCORE).and(condition))
			.groupBy(myFields)
			.orderBy(USER_SCORE.CREATE_TIME.desc());

		PageResult<? extends Record> pageResult = getPageResult(select, param.getCurrentPage(), param.getPageRows(), myRecord.getClass());
		return pageResult;
	}

	/**
	 * 构建会员签到列表查询条件
	 */
	private Condition buildSignPageCondition(ScoreSignParam param) {
		Condition condition = DSL.noCondition();
		if(!StringUtils.isBlank(param.getSearch())) {
			condition = condition.and(USER.USERNAME.like(likeValue(param.getSearch()))
									.or(USER.MOBILE.like(likeValue(param.getSearch()))));

		}
		if(null != param.getStartTime()) {
			condition = condition.and(USER_SCORE.CREATE_TIME.greaterOrEqual(param.getStartTime()));
		}

		if(null != param.getEndTime()) {
			condition = condition.and(USER_SCORE.CREATE_TIME.lessOrEqual(param.getEndTime()));
		}

		if(param.getTagIds()!=null && param.getTagIds().size()>0) {
			condition = condition.and(USER_TAG.TAG_ID.in(param.getTagIds()));
		}
		return condition;
	}

	/**
	 * 获取连续签到天数
	 * @return Map day=连续签到天数; score=连续签到获得的积分
	 */
	public Map<String,Integer> checkDays(Integer userId,Timestamp time,Integer score) {
		logger().info("获取连续签到的天数");
		Map<String,Integer> map = new HashMap<>();
		map.put(SIGN_DAY, 0);
		map.put(SIGN_SCORE,0);
		if(time == null) {
			return map;
		}

		int day = 1;

		LocalDateTime refTime = time.toLocalDateTime();
		do{
			map.put(SIGN_DAY, day);
			map.put(SIGN_SCORE,score);

			LocalDateTime pastTime = refTime.minusDays(day);
			LocalDateTime pastStartTime = pastTime.withHour(0).withMinute(0).withSecond(0);
			LocalDateTime pastEndTime = pastTime.withHour(23).withMinute(59).withSecond(59);

			UserScoreRecord record = db().selectFrom(USER_SCORE)
				.where(USER_SCORE.USER_ID.eq(userId))
				.and(USER_SCORE.DESC.eq(VersionName.SUB_3_SIGN_SCORE))
				.and(USER_SCORE.CREATE_TIME.greaterOrEqual(Timestamp.valueOf(pastStartTime)))
				.and(USER_SCORE.CREATE_TIME.lessOrEqual(Timestamp.valueOf(pastEndTime)))
				.fetchOne();

			if(record == null) {
				break;
			}else {
				day += 1;
				score += record.getUsableScore();
			}
		}while(true);

		return map;
	}

}
