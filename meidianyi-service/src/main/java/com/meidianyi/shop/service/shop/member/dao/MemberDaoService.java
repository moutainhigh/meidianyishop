package com.meidianyi.shop.service.shop.member.dao;

import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.STORE;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;
import static com.meidianyi.shop.db.shop.Tables.USER_IMPORT_DETAIL;
import static com.meidianyi.shop.db.shop.Tables.USER_LOGIN_RECORD;
import static com.meidianyi.shop.db.shop.Tables.USER_TAG;
import static com.meidianyi.shop.service.pojo.shop.member.MemberConstant.INVITE_USERNAME;
import static com.meidianyi.shop.service.pojo.shop.member.MemberConstant.INVITE_MOBILE;
import static com.meidianyi.shop.service.pojo.shop.member.MemberConstant.LOGIN_FORBID;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_DURING;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_FIX;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_FOREVER;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.UCARD_FG_USING;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectSeekStep3;
import org.jooq.SortField;
import org.jooq.Table;
import  org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.MemberBasicInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberParam;
import com.meidianyi.shop.service.pojo.shop.member.OrderRuleParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardDetailParam;
import com.meidianyi.shop.service.pojo.shop.member.userexp.UserExpCardVo;
import com.meidianyi.shop.service.pojo.shop.member.userexp.UserExpParam;
import com.meidianyi.shop.service.pojo.shop.member.userexp.UserExpVo;
import com.meidianyi.shop.service.shop.member.TagService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.user.cart.UserCartService;
import com.meidianyi.shop.service.shop.user.user.UserLoginRecordService;



/**
 * @author 黄壮壮
 * @Date: 2019年9月10日
 * @Description: 查询数据库的sql语句
 */
@Service
public class MemberDaoService extends ShopBaseService {
	@Autowired TagService tagService;
	@Autowired UserLoginRecordService userLoginRecordService;
	@Autowired UserCartService userCartService;
	@Autowired OrderInfoService orderInfoService;
	@Autowired UserCardService userCardService;
	private static final String USER_NAME = "userName";

	/**
	 * 获取会员列表的基本信息
	 */
	public PageResult<MemberInfoVo> getMemberList(MemberPageListParam param) {
		User aliasUser = USER.as("aliasUser");

		SelectOnConditionStep<? extends Record> select = db()
				.selectDistinct(USER.USER_ID, USER.USERNAME.as(USER_NAME), aliasUser.USERNAME.as(INVITE_USERNAME), USER.MOBILE,
						USER.ACCOUNT, USER.SCORE, USER.SOURCE, USER.SCENE,USER.CREATE_TIME, USER.DEL_FLAG, USER_DETAIL.REAL_NAME)
				.from(USER)
				.leftJoin(aliasUser).on(aliasUser.USER_ID.eq(USER.INVITE_ID))
				.leftJoin(USER_DETAIL).on(USER_DETAIL.USER_ID.eq(USER.USER_ID));

		buildOptionsForTable(param,select);
		SortField<? extends Object> order= USER.USER_ID.desc();
		if(param.getOrderRule()!=null) {
			OrderRuleParam orderRule = param.getOrderRule();
			switch (orderRule.getRule()) {
				case OrderRuleParam.SCORE_RULE:
					// 积分排序
					order = orderRule.isDesc() ? USER.SCORE.desc() : USER.SCORE.asc();
					break;
				case OrderRuleParam.REGIST_TIME_RULE:
					// 注册时间
					order = orderRule.isDesc() ? USER.CREATE_TIME.desc(): USER.CREATE_TIME.asc();
					break;
				default:
					break;
			}
		}

		select.where(buildOptions(param)).orderBy(order);
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(), MemberInfoVo.class);
	}

	/** 获取会员列表的基本信息 */
	public List<UserRecord> getExportUserList(MemberPageListParam param) {
		SelectJoinStep<Record> select = db().select(USER.asterisk()).from(USER);
		buildOptionsForTable(param,select);

		select.where(buildOptions(param))
			  .orderBy(USER.USER_ID.desc());

		PageResult<UserRecord> memberList = getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				UserRecord.class);
		return memberList.dataList;
	}

	/**
	 * 	获取用户的导出会员 第二版
	 * @param param
	 * @return
	 */
	public List<UserExpVo> getExportAllUserList(MemberPageListParam param) {
		SelectJoinStep<Record> select = db().select(USER.asterisk()).from(USER);
		buildOptionsForTable(param,select);
		UserExpParam expParam = param.getUserExpParam();
		return select.where(buildOptions(param))
			  .orderBy(USER.USER_ID.desc())
			  .limit(expParam.getStartNum(), expParam.getEndNum())
			  .fetchInto(UserExpVo.class);
	}



	/**
	 * 通过活动新增用户
	 */
	public PageResult<MemberInfoVo> getSourceActList(MemberPageListParam param, String source, int actId) {
		User aliasUser = USER.as("aliasUser");
		SelectSeekStep1<? extends Record, Timestamp> select = db()
				.select(USER.USER_ID, USER.USERNAME.as(USER_NAME), USER.MOBILE, USER.CREATE_TIME, USER.INVITE_ID,
						aliasUser.USERNAME.as(INVITE_USERNAME))
				.from(USER)
				.leftJoin(aliasUser)
				.on(USER.INVITE_ID.eq(aliasUser.USER_ID))
				.where(USER.INVITE_SOURCE.eq(source))
				.and(USER.INVITE_ACT_ID.eq(actId))
				.and(buildOptions(param))
				.orderBy(USER.CREATE_TIME.desc());

		return this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), MemberInfoVo.class);
	}



	/**
	 * 动态连表
	 */
	private void buildOptionsForTable(MemberPageListParam param,SelectJoinStep<? extends Record> select) {
		if(param.getCardId() != null) {
			select.leftJoin(USER_CARD).on(USER.USER_ID.eq(USER_CARD.USER_ID));
		}
		if(param.getTagName()!=null && param.getTagName().size()>0) {
			/** -标签处理 */
			select.leftJoin(USER_TAG).on(USER.USER_ID.eq(USER_TAG.USER_ID));
		}
	}


	/**
	 * 获取会员用户的详细信息
	 */
	public MemberBasicInfoVo getMemberInfo(Integer userId) {
		User a = USER.as("a");
		User b = USER.as("b");
		Field<?> inviteName = db().select(b.USERNAME).from(b).where(b.USER_ID.eq(a.INVITE_ID)).asField(INVITE_USERNAME);
		Field<?>  inviteMobile = db().select(b.MOBILE).from(b).where(b.USER_ID.eq(a.INVITE_ID)).asField(INVITE_MOBILE);
		return db().select(a.USER_ID,a.USERNAME, a.WX_UNION_ID, a.CREATE_TIME, a.MOBILE, a.WX_OPENID,a.SCORE,a.ACCOUNT,
				a.INVITE_ID, a.SOURCE,a.SCENE, a.UNIT_PRICE, inviteName,inviteMobile, USER_DETAIL.REAL_NAME, USER_DETAIL.EDUCATION,USER_DETAIL.INDUSTRY_INFO,
				USER_DETAIL.PROVINCE_CODE, a.IS_DISTRIBUTOR, USER_DETAIL.CITY_CODE, USER_DETAIL.DISTRICT_CODE,
				USER_DETAIL.BIRTHDAY_DAY, USER_DETAIL.BIRTHDAY_MONTH, USER_DETAIL.BIRTHDAY_YEAR, USER_DETAIL.SEX,
				USER_DETAIL.MARITAL_STATUS, USER_DETAIL.MONTHLY_INCOME, USER_DETAIL.CID,USER_DETAIL.USER_AVATAR)
				.from(a.leftJoin(USER_DETAIL).on(a.USER_ID.eq(USER_DETAIL.USER_ID)))
				.where(a.USER_ID.eq(userId)).fetchAnyInto(MemberBasicInfoVo.class);
	}

	/**
	 * 查询会员最近浏览时间
	 */
	public Record2<Timestamp, Timestamp> getRecentBrowseTime(Integer userId) {
		return db().select(USER_LOGIN_RECORD.CREATE_TIME, USER_LOGIN_RECORD.UPDATE_TIME).from(USER_LOGIN_RECORD)
				.where(USER_LOGIN_RECORD.USER_ID.eq(userId)).orderBy(USER_LOGIN_RECORD.ID.desc()).limit(1).fetchOne();
	}

	/** 获取门店名称 */
	public Record1<String> getStoreName(String source) {
		return db().select(STORE.STORE_NAME).from(STORE).where(STORE.STORE_ID.eq(Integer.parseInt(source)))
				.fetchAny();
	}

	/**
	 * 获取一张会员卡信息,此会员卡为等级会员，限次会员卡再到普通会员卡
	 * @param inData 工作日，休息，或 无限制
	 */
	public Record getOneMemberCard(Integer userId) {
		List<Integer> inDate = userCardService.useInDate();
		SelectSeekStep3<Record, Byte, Byte, String> sql = getMemberCardSql(inDate,userId);
		return sql.limit(1).fetchAny();
	}

	/**
	 * 获取用户其中的一张卡，改卡的顺序为等级卡》限次卡》普通卡
	 * @param userIds
	 * @return List<UserExpCardVo> 用户卡信息列表 || null
	 */
	public Map<Integer, UserExpCardVo> getUserOneCard(List<Integer> userIds) {
		if(userIds == null || userIds.size()==0) {
			return null;
		}
		List<Integer> inDate = userCardService.useInDate();
		Map<Integer, UserExpCardVo>  res = getMemberCardSql(inDate,userIds);
		return res;
	}


	/**
	 * 获取用户的所有可用会员卡
	 */
	public Result<Record> getAllAvailableMemberCard(Integer userId) {
		List<Integer> data = Arrays.asList(new Integer[]{0,1,2});
		SelectSeekStep3<Record, Byte, Byte, String> sql = getMemberCardSql(data,userId);
		return sql.fetch();
	}

	/**
	 * 获取用户会员卡sql
	 */
	private SelectSeekStep3<Record, Byte, Byte, String> getMemberCardSql(List<Integer> inData, Integer userId) {
		return  db()
		.select(USER_CARD.asterisk(), MEMBER_CARD.ID,MEMBER_CARD.CARD_NAME, MEMBER_CARD.CARD_TYPE, MEMBER_CARD.DISCOUNT,
				MEMBER_CARD.BG_TYPE, MEMBER_CARD.BG_COLOR, MEMBER_CARD.BG_IMG, MEMBER_CARD.BUY_SCORE,
				MEMBER_CARD.EXPIRE_TYPE, MEMBER_CARD.START_TIME, MEMBER_CARD.END_TIME,
				MEMBER_CARD.RECEIVE_DAY, MEMBER_CARD.DATE_TYPE, MEMBER_CARD.STORE_LIST,
				MEMBER_CARD.ACTIVATION)
		.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
		.where(USER_CARD.USER_ID.eq(userId)).and(USER_CARD.FLAG.eq(UCARD_FG_USING))
		.and(MEMBER_CARD.USE_TIME.in(inData).or(MEMBER_CARD.USE_TIME.isNull()))
		.and((MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FIX).and(MEMBER_CARD.START_TIME.le(DateUtils.getLocalDateTime())))
				.or(MEMBER_CARD.EXPIRE_TYPE.in(MCARD_ET_DURING, MCARD_ET_FOREVER)))
		.orderBy(USER_CARD.IS_DEFAULT.desc(),MEMBER_CARD.CARD_TYPE.desc(), MEMBER_CARD.GRADE.desc());
	}

	private Map<Integer, UserExpCardVo> getMemberCardSql(List<Integer> inData, List<Integer> userIds) {
		// 防止mysql5.7groupby对之前的order by不生效
		int count = db().fetchCount(USER_CARD);
		Table<?> nested = db()
						.select(USER_CARD.USER_ID,USER_CARD.CARD_ID,MEMBER_CARD.CARD_NAME)
						.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
						.where(USER_CARD.USER_ID.in(userIds)
								.and(USER_CARD.FLAG.eq(UCARD_FG_USING))
								.and(MEMBER_CARD.USE_TIME.in(inData).or(MEMBER_CARD.USE_TIME.isNull()))
								.and((MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FIX).and(MEMBER_CARD.START_TIME.le(DateUtils.getLocalDateTime())))
										.or(MEMBER_CARD.EXPIRE_TYPE.in(MCARD_ET_DURING, MCARD_ET_FOREVER))))
						.orderBy(USER_CARD.IS_DEFAULT.desc(),MEMBER_CARD.CARD_TYPE.desc(), MEMBER_CARD.GRADE.desc())
						.limit(count)
						.asTable("nested");

		Map<Integer, UserExpCardVo> res = db().select(nested.field("user_id"),DSL.max(nested.field("card_id")).as("card_id"),
					DSL.max(nested.field("card_name")).as("card_name"))
					.from(nested)
					.groupBy(nested.field("user_id"))
					.fetchMap(nested.field("user_id").cast(Integer.class), UserExpCardVo.class);
		return res;
	}


	/**
	 * 更新用户的邀请人id
	 */
	public void updateMemberInviteId(Integer userId,Integer invitedId) {
		db().update(USER).set(USER.INVITE_ID, invitedId).where(USER.USER_ID.eq(userId)).execute();
	}

	/**
	 * 更新用户信息
	 */
	public void updateMemberInfoSql(MemberParam param) {
		logger().info("正在更新用户信息");
		UserDetailRecord record = new UserDetailRecord();
		buildMemberInfoOptions(param, record);
		db().update(USER_DETAIL).set(record).where(USER_DETAIL.USER_ID.eq(param.getUserId())).execute();
	}

	/**
	 * 构建更新字段
	 */
	private void buildMemberInfoOptions(MemberParam param, UserDetailRecord record) {
		/** -生日-年 */
		if(param.getBirthdayYear() != null) {
			record.setBirthdayYear(param.getBirthdayYear());
		}
		/** -生日-月 */
		if(param.getBirthdayMonth() != null) {
			record.setBirthdayMonth(param.getBirthdayMonth());
		}
		/** -生日-天 */
		if(param.getBirthdayDay() != null) {
			record.setBirthdayDay(param.getBirthdayDay());
		}
		/** -婚姻状况*/
		if(param.getMaritalStatus() != null) {
			record.setMaritalStatus(param.getMaritalStatus());
		}
		/** -真实姓名 */
		if(!StringUtils.isBlank(param.getRealName())) {
			record.setRealName(param.getRealName());
		}
		/** -月收入 */
		if(param.getMonthlyIncome() != null) {
			record.setMonthlyIncome(param.getMonthlyIncome());
		}
		/** -身份证  */
		if(!StringUtils.isBlank(param.getCid())) {
			record.setCid(param.getCid());
		}
		/** -受教育程度 */
		if(param.getEducation()!=null) {
			record.setEducation(param.getEducation());
		}

		/** -行业 */
		if(param.getIndustory()!= null) {
			record.setIndustryInfo(param.getIndustory());
		}
		/** -性别 */
		if(!StringUtils.isBlank(param.getSex())) {
			record.setSex(param.getSex());
		}

		if(param.getProvinceCode()!=null) {
			record.setProvinceCode(param.getProvinceCode());
		}

		if(param.getCityCode()!=null) {
			record.setCityCode(param.getCityCode());
		}

		if(param.getDistrictCode()!=null) {
			record.setDistrictCode(param.getDistrictCode());
		}

	}

	/**
	 *  会员持有会员卡详情
	 */
	public Result<Record> getAllUserCardDetailSql(UserCardDetailParam param) {
		return db().select(USER_CARD.asterisk(),MEMBER_CARD.CARD_NAME,MEMBER_CARD.CARD_TYPE,USER.USERNAME)
					.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
					.leftJoin(USER).on(USER_CARD.USER_ID.eq(USER.USER_ID))
					.where(DSL.noCondition())
					.and(buildOptionsForUserCard(param))
					.orderBy(USER_CARD.CREATE_TIME.desc())
					.fetch();
	}

	/**
	 * 多条件查询构建条件
	 */
	private Condition buildOptions(MemberPageListParam param) {
		Condition condition = DSL.noCondition();
		if (isNotNull(param)) {
			condition = condition
				.and(getUserIdCondition(param.getUserId()))
				.and(getMobileCondition(param.getMobile()))
				.and(getUserNameCondition(param.getUsername()))
				.and(getSourceCondition(param.getSource(),param.getType(),param.getChannelId()))
				.and(getInviteUserCondition(param.getInviteUserName()))
				.and(getUserCardCondition(param.getCardId()))
				.and(getTagNameCondition(param.getTagName()))
				.and(getIsDistributorCondition(param.getIsDistributor()))
				.and(getUnitPriceLowCondition(param.getUnitPriceLow()))
				.and(getUnitPriceHightCondition(param.getUnitPriceHight()))
				.and(getBuyCountLowCondition(param.getBuyCountLow()))
				.and(getBuyCountHightCondition(param.getBuyCountHight()))
				.and(getGoodsIdCondition(param.getGoodsId()))
				.and(getHasMobileCondition(param.getHasMobile()))
				.and(getHasScoreCondition(param.getHasScore()))
				.and(getHasBalance(param.getHasBalance()))
				.and(getHasCardCondition(param.getHasCard()))
				.and(getIsForbidLoginCondition(param.getHasDelete()))
				.and(getRealNameCondition(param.getRealName()))
				.and(getHasImportCondition(param.getHasImport()))
				.and(getRegistStartTimeCondition(DateUtils.convertToTimestamp(param.getCreateTime())))
				.and(getRegistEndTimeCondition(DateUtils.convertToTimestamp(param.getEndTime())))
				.and(getLoginStartTimeCondition(DateUtils.convertToTimestamp(param.getLoginStartTime())))
				.and(getLoginEndTimeCondition(DateUtils.convertToTimestamp(param.getLoginEndTime())))
				.and(getCartStartTimeCondition(DateUtils.convertToTimestamp(param.getCartStartTime())))
				.and(getCartEndTimeCondition(DateUtils.convertToTimestamp(param.getCartEndTime())))
				.and(getBuyStartTimeCondition(DateUtils.convertToTimestamp(param.getBuyStartTime())))
				.and(getBuyEndTimeCondition(DateUtils.convertToTimestamp(param.getBuyEndTime())));
		}
		return condition;
	}




	/**
	 * 构建查询会员持有会员卡详情的参数
	 */
	private Condition buildOptionsForUserCard(UserCardDetailParam param) {
		Condition condition = DSL.noCondition();
		if(isNotNull(param)) {
			condition = condition
				.and(getUserIdCondition(param.getUserId()))
				.and(getMobileCondition(param.getMobile()))
				.and(getUserNameCondition(param.getUsername()))
				.and(getReceiveCardStartTimeCondition(DateUtils.convertToTimestamp(param.getCreateTimeFirst())))
				.and(getReceiveCardEndTimeCondition(DateUtils.convertToTimestamp(param.getCreateTimeSecond())))
				.and(getCardIdCondition(param.getCardId()))
				.and(getCardTypeCondition(param.getCardType()));


			/** - 卡状态 */
			if (param.getStatusValue() != null && !NumberUtils.BYTE_MINUS_ONE.equals(param.getStatusValue())) {
				/** - 状态为过期 */
				if (CardConstant.UCARD_FG_EXPIRED.equals(param.getStatusValue())) {
					condition = condition.and(USER_CARD.EXPIRE_TIME.le(DateUtils.getLocalDateTime()));

				} else if (CardConstant.UCARD_FG_USING.equals(param.getStatusValue())) {
					condition = condition.and(USER_CARD.EXPIRE_TIME.ge(DateUtils.getLocalDateTime()).or(USER_CARD.EXPIRE_TIME.isNull()))
										.and(USER_CARD.FLAG.eq(param.getStatusValue()));
				}else if(CardConstant.UCARD_FG_STOP.equals(param.getStatusValue())) {
					condition = condition.and(USER_CARD.FLAG.eq(param.getStatusValue()));

				}else {
					//	转赠中或已转赠
					condition = condition.and(USER_CARD.FLAG.eq(param.getStatusValue()));
				}
			}
		}
		return condition;
	}

	/**
	 * 会员id条件
	 */
	private Condition getUserIdCondition(Integer userId) {
		Condition condition = DSL.noCondition();
		return isNotNull(userId)?condition.and(USER.USER_ID.eq(userId)):condition;
	}

	/**
	 * 手机号条件
	 */
	private Condition getMobileCondition(String mobile) {
		Condition condition = DSL.noCondition();
		if(isNotBlank(mobile)) {
			condition = condition.and(USER.MOBILE.like(likeValue(mobile)));
		}
		return condition;
	}

	/**
	 * 昵称条件
	 */
	private Condition getUserNameCondition(String userName) {
		Condition condition = DSL.noCondition();
		if(isNotBlank(userName)) {
			condition = condition.and(USER.USERNAME.like(likeValue(userName)));
		}
		return condition;
	}

	/**
	 * 来源条件
	 * @Param source 来源信息
	 * @Param type 类型  0 微信，1 门店，2渠道页
	 * @Param channelId 渠道页
	 *
	 */
	private Condition getSourceCondition(Integer source,Integer type,Integer channelId) {
		Condition condition = DSL.noCondition();

		if(isNotNull(source) && source != 0) {
			if(source<0) {
				// 微信来源
				condition = condition.and(USER.SCENE.eq(source));
			}else if(source>0) {
				// 门店
				condition = condition.and(USER.SOURCE.eq(source));
			}else if(type!=null && NumberUtils.INTEGER_TWO.equals(type)) {
				condition = condition.and(USER.INVITE_SOURCE.eq("channel"))
								.and(USER.INVITE_ACT_ID.eq(channelId));
			}
		}
		return condition;
	}





	/**
	 * 邀请人条件
	 */
	private Condition getInviteUserCondition(String name) {
		Condition condition = DSL.noCondition();
		if(isNotBlank(name)) {
			condition = condition.and(USER.INVITE_ID.in(getUserIdByName(name)));
		}
		return condition;
	}

	private List<Integer> getUserIdByName(String name){
		return getUserByName(name).getValues(USER.USER_ID,Integer.class);
	}

	private Result<UserRecord> getUserByName(String name) {
		String val = likeValue(name);
		return db().selectFrom(USER).where(USER.USERNAME.like(val)).fetch();
	}

	/**
	 * 会员卡条件
	 */
	private Condition getUserCardCondition(Integer cardId) {
		Condition condition = DSL.noCondition();
		if(isNotNull(cardId) && cardId != 0) {
			condition = condition
			.and(USER_CARD.CARD_ID.eq(cardId))
			.and(USER_CARD.FLAG.eq(UCARD_FG_USING));
		}
		return condition;
	}

	/**
	 * 注册开始时间查询条件
	 */
	private Condition getRegistStartTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		return isNotNull(time)?condition.and(USER.CREATE_TIME.ge(time)):condition;
	}

	/**
	 * 注册结束时间查询条件
	 */
	private Condition getRegistEndTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		return isNotNull(time)?condition.and(USER.CREATE_TIME.le(time)):condition;
	}

	/**
	 * 标签查询条件
	 */
	private Condition getTagNameCondition(List<Integer> tagIdList) {
		Condition condition = DSL.noCondition();
		if(tagIdList != null && tagIdList.size()>0) {
			condition = condition.and(USER_TAG.TAG_ID.in(tagIdList));
		}
		return condition;
	}

	/**
	 * 	是否是分销员条件
	 */
	private Condition getIsDistributorCondition(Byte isDistributor) {
		Condition condition = DSL.noCondition();
		if(isDistributor != null && !NumberUtils.BYTE_MINUS_ONE.equals(isDistributor)) {
			condition = condition.and(USER.IS_DISTRIBUTOR.eq(isDistributor));
		}
		return condition;
	}

	/**
	 * 指定时间内有登录 - 开始时间条件
	 */
	private Condition getLoginStartTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			List<Integer> userIdList = userLoginRecordService.getUserIdFromLoginStartTime(time);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 指定时间内有登录 - 结束时间条件
	 */
	private Condition getLoginEndTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			List<Integer> userIdList = userLoginRecordService.getUserIdUtilToLoginEndTime(time);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}


	/**
	 * 指定时间内有加购行为 - 开始时间条件
	 */
	private Condition getCartStartTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			List<Integer> userIdList = userCartService.getUserIdFromCartStartTime(time);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 指定时间内有加购行为 - 结束时间条件
	 */
	private Condition getCartEndTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			List<Integer> userIdList = userCartService.getUserIdUtilToCartEndTime(time);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 指定时间内有交易记录 - 开始时间条件
	 */
	private Condition getBuyStartTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			List<Integer> userIdList = orderInfoService.getUserIdFromBuyStartTime(time);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}


	/**
	 * 指定时间内有交易记录 - 结束时间条件
	 */
	private Condition getBuyEndTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			List<Integer> userIdList = orderInfoService.getUserIdUtilToBuyEndTime(time);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}


	/**
	 * 客单价最低查询条件
	 */
	private Condition getUnitPriceLowCondition(BigDecimal price) {
		Condition condition = DSL.noCondition();
		return isNotNull(price)?condition.and(USER.UNIT_PRICE.ge(price)):condition;
	}

	/**
	 * 客单价最高查询条件
	 */
	private Condition getUnitPriceHightCondition(BigDecimal price) {
		Condition condition = DSL.noCondition();
		return isNotNull(price)?condition.and(USER.UNIT_PRICE.le(price)):condition;
	}

	/**
	 * 累计购买次数 - 最低次数条件
	 */
	private Condition getBuyCountLowCondition(Integer cnt) {
		Condition condition = DSL.noCondition();
		if(isNotNull(cnt)) {
			List<Integer> userIdList = orderInfoService.getUserIdGreateThanBuyCountLow(cnt);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 累计购买次数 - 最高次数条件
	 */
	private Condition getBuyCountHightCondition(Integer cnt) {
		Condition condition = DSL.noCondition();
		if(isNotNull(cnt)) {
			List<Integer> userIdList = orderInfoService.getUserIdLessThanBuyCountHight(cnt);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 购买指定商品条件
	 */
	private Condition getGoodsIdCondition(List<Integer> goodsIdList) {
		Condition condition = DSL.noCondition();
		if (isNotNull(goodsIdList) && goodsIdList.size()>0) {
			List<Integer> userIdList = orderInfoService.getUserIdHasBuyTheGoods(goodsIdList);
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 是否有手机号条件
	 */
	private Condition getHasMobileCondition(boolean hasMobile) {
		Condition condition = DSL.noCondition();
		if(hasMobile) {
			condition = condition.and(USER.MOBILE.isNotNull())
						.and(USER.MOBILE.length().gt(0));
		}
		return condition;
	}

	/**
	 * 是否有可用积分
	 */
	private Condition getHasScoreCondition(boolean hasScore) {
		Condition condition = DSL.noCondition();
		if(hasScore) {
			condition = condition.and(USER.SCORE.gt(NumberUtils.INTEGER_ZERO));
		}
		return condition;
	}

	/**
	 * 是否有可用余额
	 */
	private Condition getHasBalance(boolean hasBalance) {
		Condition condition = DSL.noCondition();
		if(hasBalance) {
			condition = condition.and(USER.ACCOUNT.gt(BigDecimal.ZERO));
		}
		return condition;
	}

	/**
	 * 是否持有会员卡
	 */
	private Condition getHasCardCondition(boolean hasCard) {
		Condition condition = DSL.noCondition();
		if(hasCard) {
			List<Integer> userIdList = userCardService.getUserIdThatHasValidCard();
			condition = condition.and(USER.USER_ID.in(userIdList));
		}
		return condition;
	}

	/**
	 * 是否禁止登录条件
	 */
	private Condition getIsForbidLoginCondition(boolean isLogin) {
		Condition condition = DSL.noCondition();
		if(isLogin) {
			condition = condition.and(USER.DEL_FLAG.eq(LOGIN_FORBID));
		}
		return condition;
	}

	/**
	 * 真实姓名条件
	 */
	private Condition getRealNameCondition(String name) {
		Condition condition = DSL.noCondition();
		if(isNotBlank(name)) {
			String val = likeValue(name);
			condition = condition.and(USER_DETAIL.REAL_NAME.like(val));
		}
		return condition;
	}

	/**
	 * 是否为导入会员
	 */
	private Condition getHasImportCondition(boolean hasImport) {
		Condition condition = DSL.noCondition();
		if(hasImport) {
			SelectConditionStep<Record1<Integer>> subSelect = db().select(USER_IMPORT_DETAIL.ID).from(USER_IMPORT_DETAIL).where(USER_IMPORT_DETAIL.MOBILE.eq(USER.MOBILE));
			condition = condition.andExists(subSelect);
		}
		return condition;
	}

	/**
	 * 会员卡领取开始时间条件
	 */
	private Condition getReceiveCardStartTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			condition = condition.and(USER_CARD.CREATE_TIME.ge(time));
		}
		return condition;
	}

	/**
	 * 会员卡领取结束时间条件
	 */
	private Condition getReceiveCardEndTimeCondition(Timestamp time) {
		Condition condition = DSL.noCondition();
		if(isNotNull(time)) {
			condition = condition.and(USER_CARD.CREATE_TIME.le(time));
		}
		return condition;
	}

	/**
	 * 会员卡ID条件
	 */
	private Condition getCardIdCondition(Integer cardId) {
		Condition condition = DSL.noCondition();
		return isNotNull(cardId)?condition.and(MEMBER_CARD.ID.eq(cardId)):condition;
	}

	/**
	 * 会员卡类型条件
	 */
	private Condition getCardTypeCondition(Byte cardType) {
		Condition condition = DSL.noCondition();
		return isNotNull(cardType)?condition.and(MEMBER_CARD.CARD_TYPE.eq(cardType)):condition;
	}

	private boolean isNotNull(Object obj) {
		return obj!=null;
	}

	private boolean isNotBlank(String val) {
		return !StringUtils.isBlank(val);
	}

	public void updateUserDetail(UserDetailRecord record) {
		db().executeUpdate(record, USER_DETAIL.USER_ID.eq(record.getUserId()));
	}

	/**
	 * 	获取用户总数
	 */
	public int getNumOfUser(MemberPageListParam param) {
		return db().fetchCount(USER, buildOptions(param));
	}
}
