package com.meidianyi.shop.service.shop.member;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.shop.user.UserGoodsRecordDao;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.records.DistributionWithdrawRecord;
import com.meidianyi.shop.db.shop.tables.records.TagRecord;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserImportDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.db.shop.tables.records.UserTagRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.area.AreaCityVo;
import com.meidianyi.shop.service.pojo.shop.area.AreaDistrictVo;
import com.meidianyi.shop.service.pojo.shop.area.AreaProvinceVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorListVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.member.CommonMemberPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.member.CommonMemberPageListQueryVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberBasicInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberDetailsVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberEducationEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberIndustryEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberRecordExportVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberTransactionStatisticsVo;
import com.meidianyi.shop.service.pojo.shop.member.MememberLoginStatusParam;
import com.meidianyi.shop.service.pojo.shop.member.SourceNameEnum;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.AvailableMemberCardVo;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardDetailParam;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardDetailVo;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.order.UserCenterNumBean;
import com.meidianyi.shop.service.pojo.shop.member.order.UserOrderBean;
import com.meidianyi.shop.service.pojo.shop.member.report.MemberGoodsBrowseReportParam;
import com.meidianyi.shop.service.pojo.shop.member.report.MemberGoodsBrowseReportVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.UserTagParam;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.UserBindParam;
import com.meidianyi.shop.service.saas.area.AreaSelectService;
import com.meidianyi.shop.service.shop.card.CardFreeShipService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.distribution.DistributorListService;
import com.meidianyi.shop.service.shop.distribution.DistributorWithdrawService;
import com.meidianyi.shop.service.shop.member.dao.MemberDaoService;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.InsertValuesStep2;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectField;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.Table;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.CHANNEL;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.ORDER_VERIFIER;
import static com.meidianyi.shop.db.shop.Tables.TAG;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.db.shop.Tables.USER_IMPORT_DETAIL;
import static com.meidianyi.shop.db.shop.Tables.USER_LOGIN_RECORD;
import static com.meidianyi.shop.db.shop.Tables.USER_TAG;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.date;


/**
 *
 * @author 黄壮壮 2019-07-08 16:22
 */
@Service
public class MemberService extends ShopBaseService {

	public static final String INVITE_SOURCE_GROUPBUY = "groupbuy";
	public static final String INVITE_SOURCE_BARGAIN = "bargain";
	public static final String INVITE_SOURCE_INTEGRAL = "integral";
	public static final String INVITE_SOURCE_SECKILL = "seckill";
	public static final String INVITE_SOURCE_LOTTERY = "lottery";
	public static final String INVITE_SOURCE_GOODS = "goods";
	public static final String INVITE_SOURCE_MEMBERCARD = "membercard";
	public static final String INVITE_SOURCE_SCANQRCODE = "scanqrcode";
	public static final String INVITE_SOURCE_CHANNEL = "channel";
	public static final String INVITE_SOURCE_PROMOTE="promote";
	public static final String ZERO = "0";
	public static final String NEG_ONE = "-1";
	public static final Integer WEEK = 7;
	public static final Integer MONTH = 30;
	public static final Integer YEAR = 365;
	public static final Byte YES_DISTRIBUTOR = 1;

	@Autowired
	private UserGoodsRecordDao userGoodsRecordDao;
	@Autowired
	public AccountService account;
	@Autowired
	public StoreService store;
	@Autowired
	public ScoreService score;
	@Autowired
	public MemberCardService card;
	@Autowired
	public OrderInfoService order;
	@Autowired
	public ReturnOrderService returnOrderSvc;
	@Autowired
	public AddressService address;
	@Autowired
	public DistributorListService distributorListService;
	@Autowired
	public DistributorWithdrawService distributorWithdrawService;
	@Autowired
	public MemberDaoService memberDao;
	@Autowired
	public AreaSelectService area;
	@Autowired
	public UserCardService userCardService;
	@Autowired
	public PatientService patientService;
	@Autowired
	public UserCardDaoService userCardDao;
	@Autowired
	public UserImportService userImportService;
	@Autowired
	public UserExportService userExpSvc;
	@Autowired
	public RecordAdminActionService recordAdminActionSvc;
	@Autowired
	public CardFreeShipService freeShipSvc;
	@Autowired
	public CouponService couponSvc;

	/**
	 * 导出会员
	 */
	public Workbook exportUser(MemberPageListParam param,String lang) {

		List<UserRecord> userList = getExportUserList(param);
		List<MemberRecordExportVo> resList = new ArrayList<>();

		for(UserRecord user: userList) {
			MemberRecordExportVo vo = new MemberRecordExportVo();
			FieldsUtil.assignNotNull(user, vo);
			//TODO
			UserCenterNumBean userCenterOrder = order.getUserCenterNum(user.getUserId(), 0, new Integer[] {6,8,10}, new Integer[] {});
			UserCenterNumBean userCenterReturnOrder = order.getUserCenterNum(user.getUserId(), 4, new Integer[] {7,9}, new Integer[] {5});

			// 邀请人姓名
			vo.setInviteUserName(userCardDao.getUserName(user.getInviteId()));

			resList.add(vo);
		}
		 Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
	     ExcelWriter excelWriter = new ExcelWriter(lang,workbook);

	     excelWriter.writeModelList(resList,MemberRecordExportVo.class);
	     return workbook;
	}



	public List<UserRecord> getExportUserList(MemberPageListParam param) {
		return memberDao.getExportUserList(param);
	}


	/**
	 * 会员列表分页查询
	 *
	 * @param param
	 * @return
	 */
	public PageResult<MemberInfoVo> getPageList(MemberPageListParam param, String language) {

		/** 获取会员列表的基本信息 */
		PageResult<MemberInfoVo> memberList = getMemberList(param);
		logger().info("获取会员列表成功");
		for (MemberInfoVo member : memberList.dataList) {
			Integer userId = member.getUserId();

			/** 只需要一张会员卡的信息即可 */
			Record recordInfo = memberDao.getOneMemberCard(userId);

			if(recordInfo != null) {
				String cardName = recordInfo.get(MEMBER_CARD.CARD_NAME);
				logger().info(cardName);
				member.setCardName(cardName);
			}
			/** 处理来源信息 */
			String sourceName = getSourceName(language, member);

			logger().info(sourceName);
			member.setSourceName(sourceName);
			member.setPatientNum(patientService.countPatientByUser(userId));
		}
		logger().info("获取会员成功");

		return memberList;
	}



	/**
	 * 获取会员列表的基本信息
	 */
	private PageResult<MemberInfoVo> getMemberList(MemberPageListParam param) {
		PageResult<MemberInfoVo> memberList = memberDao.getMemberList(param);
		logger().info("积分检查");
		for(MemberInfoVo vo: memberList.dataList) {
			// 积分缓存
			Integer currentScore = vo.getScore();
			// 实际积分
			Integer actualScore = score.getTotalAvailableScoreById(vo.getUserId());
			if(!currentScore.equals(actualScore)) {
				// 缓存积分失效需要更新
				vo.setScore(actualScore);
				score.updateUserScore(vo.getUserId(), actualScore);
			}
		}
		return memberList;
	}

	/**
	 * 获取用户来源
	 */
	public String getSourceName(String language, MemberInfoVo member) {
		logger().info("正在获取用户来源信息");
		// 微信后台相关来源
		String sourceName = SourceNameEnum.getI18NameByCode(member.getScene(), language);
		final String symbol = "; ";
		if(INVITE_SOURCE_CHANNEL.equals(member.getInviteSource())) {
			// 渠道页
			StringBuilder tmp = new StringBuilder();
			String channelName = db().select(CHANNEL.CHANNEL_NAME).from(CHANNEL)
					.where(CHANNEL.ID.eq(member.getInviteActId())).fetchOne().into(String.class);
			if(!StringUtils.isBlank(sourceName)) {
				tmp.append(sourceName);
				tmp.append(symbol);
			}
			tmp.append(Util.translateMessage(language, "member.channal.page", "member"));
			tmp.append(channelName);
			sourceName =  tmp.toString();
		}

		if(member.getSource()!=null && member.getSource()>0) {
			// 门店
			StringBuilder tmp = new StringBuilder();
			if(!StringUtils.isBlank(sourceName)) {
				tmp.append(sourceName);
				tmp.append(symbol);
			}
			tmp.append(store.getStoreName(new Integer(member.getSource())));
			sourceName = tmp.toString();
		}

		return sourceName;
	}



    /**
     * 通用会员列表弹窗分页查询
     */
    public PageResult<CommonMemberPageListQueryVo> getCommonPageList(CommonMemberPageListQueryParam param) {
        SelectJoinStep<? extends Record> select = db().select(USER.USER_ID, USER.USERNAME, USER.MOBILE).from(USER);
        select = this.buildCommonPageListQueryOptions(select, param);
        select.where(USER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(USER.CREATE_TIME);
        return this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
            CommonMemberPageListQueryVo.class);
    }

    /**
     * 通用会员选择弹窗的指定规则过滤条件构造
     *
     * @return
     */
    private SelectJoinStep<? extends Record> buildCommonPageListQueryOptions(SelectJoinStep<? extends Record> select,
                                                                             CommonMemberPageListQueryParam param) {
        if (param == null) {
            return select;
        }
        if (null != param.getUserId() && param.getUserId() > 0) {
            select.where(USER.USER_ID.eq(param.getUserId()));
        }
        if (!StringUtils.isEmpty(param.getMobile())) {
            select.where(USER.MOBILE.contains(param.getMobile()));
        }
        if (!StringUtils.isEmpty(param.getUsername())) {
            select.where(USER.USERNAME.contains(param.getUsername()));
        }

        /**
         * 过滤已经是该门店核销员的用户，用于为该门店添加核销员
         */
        if (null != param.getStoreId() && param.getStoreId() > 0) {
            Table<Record1<Integer>> a = DSL.select(ORDER_VERIFIER.USER_ID)
                .from(ORDER_VERIFIER)
                .where(ORDER_VERIFIER.STORE_ID.eq(param.getStoreId())
                    .and(ORDER_VERIFIER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).asTable("a");
            select.leftJoin(a)
                .on(USER.USER_ID.eq(a.field(0, Integer.class)))
                .where(a.field(0).isNull()).and(USER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        }
        return select;
    }

	/**
	 *  获取用户信息
	 */
	public UserRecord getUserRecordById(Integer userId) {
		UserRecord user = db().selectFrom(USER).where(USER.USER_ID.eq(userId)).fetchAny();
		// 积分缓存
		Integer currentScore = user.getScore();
		// 实际积分
		Integer actualScore = score.getTotalAvailableScoreById(userId);
		if(!currentScore.equals(actualScore)) {
			// 缓存积分失效需要更新
			logger().info("缓存积分失效需要更新");
			user.setScore(actualScore);
			score.updateUserScore(userId, actualScore);
		}

		return user;
	}

	public <T> T getUserFieldById(Integer userId, SelectField<T> field) {
		return db().select(field).from(USER).where(USER.USER_ID.eq(userId)).fetchOne().component1();
	}

	/**
	 * 通过活动新增用户
	 */
	public PageResult<MemberInfoVo> getSourceActList(MemberPageListParam param, String source, int actId) {
		return memberDao.getSourceActList(param,source,actId);
	}

	/**
	 * 分裂营销活动拉新用户数据分析
	 */
	public Map<Date, Integer> getMarketSourceUserAnalysis(MarketAnalysisParam param) {
		Map<Date, Integer> map;
		@SuppressWarnings("unchecked")
		SelectWhereStep<? extends Record> select = (SelectWhereStep<? extends Record>) db()
				.select(date(USER.CREATE_TIME).as("date"), count().as("number")).from(USER)
				.where(USER.INVITE_ACT_ID.eq(param.getActId()));
		if (!StringUtils.isEmpty(param.getInviteSource())) {
			select.where(USER.INVITE_SOURCE.eq(param.getInviteSource()));
		}
		map = select.where(USER.CREATE_TIME.between(param.getStartTime(), param.getEndTime()))
				.groupBy(date(USER.CREATE_TIME)).fetch()
				.intoMap(date(USER.CREATE_TIME).as("date"), count().as("number"));
		return map;
	}

	/**
	 * 	批量设置用户的登录状态 ： 禁止登录-恢复登录
	 */
	public void changeLoginStatus(MememberLoginStatusParam param) {
		logger().info("设置用户登录权限");
		final List<Integer> ids;
		if(param.getUserIdList() == null || param.getUserIdList().size()==0) {
			return;
		}
		boolean allUserFlag = MememberLoginStatusParam.ALL_USER_ID.equals(param.getUserIdList().get(0));
		if(allUserFlag && param.getSearchCnt() != null) {
			ids = getAllUserIdBySeachCond(param.getSearchCnt());
		}else {
			ids = param.getUserIdList();
		}

		transaction(()->{
			int num = updateUserLoginPermission(param, ids);
			if(num > 0) {
				addLoginOptAdminRec(param, ids, num);
			}
		});
	}


	/**
	 * 	更新用户登录权限
	 * @return num 更新权限的用户数
	 */
	private int updateUserLoginPermission(MememberLoginStatusParam param, final List<Integer> ids) {
		logger().info("更新用户登录权限");
		UpdateSetMoreStep<UserRecord> setSql = db().update(USER).set(USER.DEL_FLAG,(byte)param.getPermission().ordinal());
		int num = 0;
		if(MememberLoginStatusParam.ALL_USER_ID.equals(ids.get(0))) {
			logger().info("更新系统中筛选出来的用户的状态");
			num = setSql.where(USER.USER_ID.in(ids))
						.and(USER.DEL_FLAG.notEqual((byte)param.getPermission().ordinal()))
						.execute();
		}else {
			logger().info("更新指定用户登录状态");
			num = setSql.where(USER.USER_ID.in(ids)).execute();
		}
		return num;
	}


	/**
	 * 	记录admin管理员操作用户登录权限的记录
	 */
	private void addLoginOptAdminRec(MememberLoginStatusParam param, final List<Integer> ids, int num) {
		logger().info("记录admin的改变用户登录权限的日志");
		boolean flag = MememberLoginStatusParam.LoginPermission.on.equals(param.getPermission());
		if(num == 1) {
			MemberBasicInfoVo user = getMemberInfo(ids.get(0));
			if(flag) {
				logger().info("允许用户"+user.getUsername()+"登录");
				recordAdminActionSvc.insertRecord(Collections.singletonList(RecordContentTemplate.MEMBER_LOGIN_ON.code),
							String.valueOf(user.getUserId()),user.getUsername());
			}else {
				logger().info("禁止用户"+user.getUsername()+"登录");
				recordAdminActionSvc.insertRecord(Collections.singletonList(RecordContentTemplate.MEMBER_LOGIN_OFF.code),
						String.valueOf(user.getUserId()),user.getUsername());
			}
		}else {
			Integer code;
			if(flag) {
				logger().info("批量允许"+num+"名用户登录");
				code = RecordContentTemplate.MEMBER_BATCH_LOGIN_ON.code;
			}else {
				logger().info("批量禁止"+num+"名用户登录");
				code = RecordContentTemplate.MEMBER_BATCH_LOGIN_OFF.code;
			}
			recordAdminActionSvc.insertRecord(Collections.singletonList(code), String.valueOf(num));
		}
	}


	/**
	 * 	通过筛选条件获得用户Id列表
	 * @param searchParam
	 * @return List<Integer>用户ID列表
	 */
	private List<Integer> getAllUserIdBySeachCond(MemberPageListParam searchParam) {
		logger().info("处理筛选出全部用户IDs");
		PageResult<MemberInfoVo> memberList = this.memberDao.getMemberList(searchParam);
		return memberList.dataList.stream().map(r->r.getUserId()).collect(Collectors.toList());
	}

	/**
	 * 为会员用户打标签
	 */
	public void setTagForMember(UserTagParam param) {

		/** 在事务中 */
		this.transaction(() -> {
			/** 构建insert sql语句 */
			InsertValuesStep2<UserTagRecord, Integer, Integer> insert = db().insertInto(USER_TAG)
					.columns(USER_TAG.USER_ID, USER_TAG.TAG_ID);
			/** 将值放入insert 语句 */
			param.getUserIdList().forEach(userId -> {
				param.getTagIdList().forEach(tagId -> insert.values(userId, tagId));
			});

			/** 删除原来的标签 */
			int before = db().delete(USER_TAG).where(USER_TAG.USER_ID.in(param.getUserIdList())).execute();
			int after = insert.execute();
			logger().info("删除 " + before + " 条记录。添加 " + after + " 条记录。");
		});
	}

	public boolean addUserTag(Integer tagId,Integer userId) {
		TagRecord tagRecord = db().selectFrom(TAG).where(TAG.TAG_ID.eq(tagId)).fetchAny();
		if(tagRecord==null) {
			logger().info("userId："+userId+"添加tag"+tagId+"不存在");
			return false;
		}
		UserTagRecord record2 = db().selectFrom(USER_TAG).where(USER_TAG.TAG_ID.eq(tagId).and(USER_TAG.USER_ID.eq(userId))).fetchAny();
		if(record2!=null) {
			logger().info("userId："+userId+"添加tag"+tagId+"已经存在，不用重复添加");
			return true;
		}
		UserTagRecord record = db().newRecord(USER_TAG);
		record.setTagId(tagId);
		record.setUserId(userId);
		int insert = record.insert();
		if(insert>0) {
			return true;
		}
		return false;
	}
	/**
	 * 查询会员所持有标签列表
	 */
	public List<TagVo> getTagForMember(Integer userId) {
		 return db().select(TAG.TAG_NAME,TAG.TAG_ID).from(USER_TAG.innerJoin(TAG).on(USER_TAG.TAG_ID.eq(TAG.TAG_ID)))
			.where(USER_TAG.USER_ID.eq(userId))
			.fetchInto(TagVo.class);
	}

	/** 根据用户id获取用户详情
	 * @throws MpException */
	public MemberDetailsVo getMemberInfoById(Integer userId,String language) {
		MemberDetailsVo vo = new MemberDetailsVo();
		MemberTransactionStatisticsVo transStatistic = new MemberTransactionStatisticsVo();

		/** 用户基本信息 */
		MemberBasicInfoVo memberBasicInfoVo = dealWithUserBasicInfo(userId, transStatistic,language);

		/** -查询不到用户 */
		if(memberBasicInfoVo == null) {
			return vo;
		}
		/** 分销统计 */
		dealWithDistributorsInfo(userId, transStatistic, memberBasicInfoVo);

		vo.setMemberBasicInfo(memberBasicInfoVo);
		vo.setTransStatistic(transStatistic);
		return vo;
	}


	/**
	 * 处理会员用户的底本信息
	 *
	 * @throws MpException
	 */
	private MemberBasicInfoVo dealWithUserBasicInfo(Integer userId, MemberTransactionStatisticsVo transStatistic,String language){
		/** 会员用户基本信息 */
		logger().info("正在处理会员基本信息");

		MemberBasicInfoVo memberBasicInfoVo = getMemberInfo(userId);

		if(memberBasicInfoVo == null) {
			return memberBasicInfoVo;
		}
		logger().info("生日: " + memberBasicInfoVo.getBirthdayYear());

		/** 处理省市区 */
		changeProvinceCodeToName(memberBasicInfoVo);
		/** 最近浏览时间 */
		Record2<Timestamp, Timestamp> loginTime = memberDao.getRecentBrowseTime(userId);

		/** 最近浏览时间如果updateTime 为null，则设置为createTime */
		if(loginTime != null) {
			if (loginTime.get(USER_LOGIN_RECORD.UPDATE_TIME) != null) {
                memberBasicInfoVo.setUpdateTime(loginTime.get(USER_LOGIN_RECORD.UPDATE_TIME));
            } else {
                memberBasicInfoVo.setUpdateTime(loginTime.get(USER_LOGIN_RECORD.CREATE_TIME));
            }
        }
        /** 累计积分 */
        Integer totalScore = score.getAccumulationScore(userId);
        memberBasicInfoVo.setTotalScore(totalScore);

        /** 订单相关信息 */
        getOrderStatistics(userId, transStatistic);
        memberBasicInfoVo.setTotalConsumpAmount(transStatistic.getAllTransactionStatistics().getTotalMoneyPaid());
        /** 客单价 */
        memberBasicInfoVo.setUnitPrice(transStatistic.getAllTransactionStatistics().getUnitPrice());

        /** 优惠券 */
        memberBasicInfoVo.setCanUseCouponNum(couponSvc.getCanUseCouponNum(userId));

        /** 详细地址 */
        List<String> addressList = address.getUserAddressById(userId);
        memberBasicInfoVo.setAddressList(addressList);

        /** 受教育程度 */
        Byte eduCode = memberBasicInfoVo.getEducation();
		if(eduCode != null) {
			memberBasicInfoVo.setEducationStr(MemberEducationEnum.valueOf(eduCode).getName());
			logger().info("受教育程度" + MemberEducationEnum.valueOf(eduCode).getName());
		}
		/** 来源 */

		MemberInfoVo memberInfoVo = new MemberInfoVo();
		FieldsUtil.assignNotNull(memberBasicInfoVo, memberInfoVo);
		String sourceName = getSourceName(language,memberInfoVo);
		memberBasicInfoVo.setSourceName(sourceName);


		/** 行业 */
		if(memberBasicInfoVo.getIndustryInfo() != null) {
			int industryCode = Integer.parseInt(memberBasicInfoVo.getIndustryInfo());
			memberBasicInfoVo.setIndustryId(industryCode);
			String name = MemberIndustryEnum.getNameByCode(industryCode);
			memberBasicInfoVo.setIndustryInfo(name);
		}
		memberBasicInfoVo.setUserId(userId);

		/** 邀请人分销分组名称 */
		memberBasicInfoVo.setInviteGroupName(distributorListService.getGroupName(userId));
        List<PatientOneParam> patientList = patientService.listPatientByUserId(userId);
        memberBasicInfoVo.setPatientList(patientList);
		return memberBasicInfoVo;
	}

	/**
	 * 将用户的住址省市区code转化为name
	 * @param memberBasicInfoVo
	 */
	public void changeProvinceCodeToName(MemberBasicInfoVo memberBasicInfoVo) {
		List<AreaProvinceVo> allArea = area.getAllArea();
		/** 省代码 */
		Integer provinceCode = memberBasicInfoVo.getProvinceCode();
		/** 市代码 */
		Integer cityCode = memberBasicInfoVo.getCityCode();
		/** 区代码 */
		Integer districtCode = memberBasicInfoVo.getDistrictCode();

		if(provinceCode != null) {
			for(AreaProvinceVo province: allArea) {

				if(provinceCode.equals(province.getProvinceId())){
					/** 设置省名称 */
					String provinceName = province.getProvinceName();
					memberBasicInfoVo.setProvinceName(provinceName);
					List<AreaCityVo> allCity = province.getAreaCity();
					if(cityCode == null || allCity.size()<1) {
						return;
					}
					/** 设置城市名称 */
					for(AreaCityVo city: allCity) {
						if(cityCode.equals(city.getCityId())) {
							String cityName = city.getCityName();
							memberBasicInfoVo.setCityName(cityName);

							/** 获取区|县 */
							List<AreaDistrictVo> areaDistrict = city.getAreaDistrict();
							if(districtCode == null || areaDistrict.size()<1) {
								return;
							}
							for(AreaDistrictVo district: areaDistrict) {
								if(districtCode.equals(district.getDistrictId())){
									/** 设置区 | 县 */
									String districtName = district.getDistrictName();
									memberBasicInfoVo.setDistictName(districtName);
									return;
								}
							}

						}
					}


				}
			}
		}

	}

	/**
	 * 获得门店信息
	 * @param source
	 * @return
	 */
	public String getStoreName(String source) {
		Record storeName = memberDao.getStoreName(source);
		if(storeName != null) {
			return storeName.into(String.class);
		}
		return null;
	}




	/**
	 * 获取会员用户的信息
	 * @param userId
	 * @return 用户信息，不会为null
	 */
	public MemberBasicInfoVo getMemberInfo(Integer userId) {
		return memberDao.getMemberInfo(userId);
	}

	/**
	 * 	获取用户积分
	 */
	public Integer getUserScore(Integer userId) {
		MemberBasicInfoVo member = getMemberInfo(userId);
		if(member != null && member.getScore() != null) {
			return member.getScore();
		}else {
			return NumberUtils.INTEGER_ZERO;
		}

	}

	/**
	 * 	获取用户余额
	 */
	public BigDecimal getUserAccount(Integer userId) {
		MemberBasicInfoVo member = getMemberInfo(userId);
		if(member != null && member.getAccount() != null) {
			return member.getAccount();
		}else {
			return BigDecimal.ZERO;
		}
	}
	/**
	 * 	获取用户微信openid
	 */
	public String getUserWxOpenId(Integer userId) {
		MemberBasicInfoVo member = getMemberInfo(userId);
		return member.getWxOpenid();
	}
	/**
	 * 获取分销信息
	 *
	 * @param userId
	 * @param transStatistic
	 * @param memberBasicInfoVo
	 */
	private void dealWithDistributorsInfo(Integer userId, MemberTransactionStatisticsVo transStatistic,
			MemberBasicInfoVo memberBasicInfoVo) {

		if(memberBasicInfoVo == null) {
			return;
		}
		logger().info("正在获取分销统计信息");
		/** 分销统计 */
		/** 判断是不是分销员 */

		if (YES_DISTRIBUTOR.equals(memberBasicInfoVo.getIsDistributor())) {

			/** 用户的分销信息 */
			DistributorListVo distributor = getDistributor(userId, memberBasicInfoVo);
			if (distributor != null) {

                /** 返利商品总金额(元) */
                transStatistic.getDistributionStatistics().setTotalCanFanliMoney(distributor.getTotalCanFanliMoney());

                /** 获返利佣金总额(元) */
                transStatistic.getDistributionStatistics().setRebateMoney(BigDecimalUtil.add(distributor.getTotalFanliMoney(), distributor.getWaitFanliMoney()));


                /** 分销员分组 */
                transStatistic.getDistributionStatistics().setGroupName(distributor.getGroupName());

                /** 分销员等级 */
                transStatistic.getDistributionStatistics().setLevelName(distributor.getLevelName());

                /** 下级用户数 */
                transStatistic.getDistributionStatistics().setSublayerNumber(distributor.getSublayerNumber());

                /** 获返利订单数量 */
                transStatistic.getDistributionStatistics().setRebateOrderNum(distributorListService.getRebateOrderNum(userId));
            }
			/**-获取分销提现 */
			DistributionWithdrawRecord distributionWithdraw = distributorWithdrawService.getWithdrawByUserId(userId);
			if(distributionWithdraw != null) {
                /** -已提现佣金总额(元) */
                transStatistic.getDistributionStatistics().setWithdrawCash(distributionWithdraw.getWithdrawCash());
            }

		}
	}

	/**
	 * 获取对应Id的分销员信息
	 *
	 * @param userId
	 * @param memberBasicInfoVo
	 * @return
	 */
	private DistributorListVo getDistributor(Integer userId, MemberBasicInfoVo memberBasicInfoVo) {
		/** 通过调用DistributorListService 服务的分页信息获取该分销员的信息 */
		DistributorListParam param = new DistributorListParam();
		param.setUsername(memberBasicInfoVo.getUsername());
		PageResult<DistributorListVo> pageList = distributorListService.getPageList(param);
		/** 找到userId相同的数据 */
		for (DistributorListVo distributor : pageList.dataList) {
			if (distributor.getUserId().equals(userId)) {
				return distributor;
			}
		}
		return null;
	}

    /**
     * 获取用户详情关于订单的信息
     */
    private void getOrderStatistics(Integer userId, MemberTransactionStatisticsVo transStatistic) {

        UserOrderBean userOrder = order.getUserOrderStatistics(userId);
        Tuple2<BigDecimal, Integer> returnOrderTuple = returnOrderSvc.getUserReturnOrderStatistics(userId);
        //实物订单统计（普通订单）
        transStatistic.getPhysicalTransactionStatistics().setLastOrderTime(userOrder.getLastOrderTime());
        transStatistic.getPhysicalTransactionStatistics().setOrderNum(userOrder.getOrderNum());
        transStatistic.getPhysicalTransactionStatistics().setTotalMoneyPaid(userOrder.getTotalMoneyPaid());
        transStatistic.getPhysicalTransactionStatistics().setUnitPrice(userOrder.getUnitPrice());
        transStatistic.getPhysicalTransactionStatistics().setReturnOrderMoney(returnOrderTuple.v1);
        transStatistic.getPhysicalTransactionStatistics().setReturnOrderNum(returnOrderTuple.v2);


        // 会员卡购买订单
        UserOrderBean memberCardOrder = saas().getShopApp(getShopId()).memberCardOrder.getUserOrderStatistics(userId);
        // 优惠券礼包订单
        UserOrderBean couponPackOrder = saas().getShopApp(getShopId()).couponPackOrder.getUserOrderStatistics(userId);
        // 会员卡续费订单
        UserOrderBean cardRenew = saas().getShopApp(getShopId()).userCard.getRenewOrderStatistics(userId);
        // 会员卡充值订单
        UserOrderBean cardCharge = saas().getShopApp(getShopId()).userCard.getChargeOrderStatistics(userId);
        //会员卡和优惠券礼包的退款
        Tuple2<BigDecimal, Integer> returnVirtualOrder = saas().getShopApp(getShopId()).memberCardOrder.getUserReturnOrderStatistics(userId);
        //增值交易统计
        transStatistic.getAppreciationTransactionStatistics().setMemberCardPurchaseOrderNum(memberCardOrder.getOrderNum());
        transStatistic.getAppreciationTransactionStatistics().setCouponPackPurchaseOrderNum(couponPackOrder.getOrderNum());
        transStatistic.getAppreciationTransactionStatistics().setMemberCardRenewOrderNum(cardRenew.getOrderNum());
        transStatistic.getAppreciationTransactionStatistics().setMemberCardChargeOrderNum(cardCharge.getOrderNum());


        // 门店服务订单
        UserOrderBean serviceOrder = saas().getShopApp(getShopId()).store.serviceOrder.getUserOrderStatistics(userId);
        // 退款服务订单
        Tuple2<BigDecimal, Integer> returnServiceOrder = saas().getShopApp(getShopId()).store.serviceOrder.getUserReturnOrderStatistics(userId);
        //门店服务预约交易统计
        transStatistic.getStoreServiceOrderTransactionStatistics().setLastOrderTime(serviceOrder.getLastOrderTime());
        transStatistic.getStoreServiceOrderTransactionStatistics().setOrderNum(serviceOrder.getOrderNum());
        transStatistic.getStoreServiceOrderTransactionStatistics().setTotalMoneyPaid(serviceOrder.getTotalMoneyPaid());
        transStatistic.getStoreServiceOrderTransactionStatistics().setUnitPrice(serviceOrder.getUnitPrice());
        transStatistic.getStoreServiceOrderTransactionStatistics().setReturnOrderMoney(returnServiceOrder.v1);
        transStatistic.getStoreServiceOrderTransactionStatistics().setReturnOrderNum(returnServiceOrder.v2);


        // 门店买单订单
        UserOrderBean storeOrder = saas().getShopApp(getShopId()).store.reservation.storeOrderService.getUserOrderStatistics(userId);
        // 退款买单订单
        Tuple2<BigDecimal, Integer> returnStoreOrder = saas().getShopApp(getShopId()).store.reservation.storeOrderService.getUserReturnOrderStatistics(userId);
        //门店买单订单交易统计
        transStatistic.getStoreOrderTransactionStatistics().setLastOrderTime(storeOrder.getLastOrderTime());
        transStatistic.getStoreOrderTransactionStatistics().setOrderNum(storeOrder.getOrderNum());
        transStatistic.getStoreOrderTransactionStatistics().setTotalMoneyPaid(storeOrder.getTotalMoneyPaid());
        transStatistic.getStoreOrderTransactionStatistics().setUnitPrice(storeOrder.getUnitPrice());


        //all 求和
        transStatistic.getAllTransactionStatistics().setLastOrderTime(getMaxTimestamp(
            transStatistic.getPhysicalTransactionStatistics().getLastOrderTime(),
            transStatistic.getStoreServiceOrderTransactionStatistics().getLastOrderTime(),
            transStatistic.getStoreOrderTransactionStatistics().getLastOrderTime()
        ));
        transStatistic.getAllTransactionStatistics().setTotalMoneyPaid(
            BigDecimalUtil.addOrSubtrac(
                BigDecimalUtil.BigDecimalPlus.create(userOrder.getTotalMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(memberCardOrder.getTotalMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(couponPackOrder.getTotalMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(cardRenew.getTotalMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(cardCharge.getTotalMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(serviceOrder.getTotalMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(storeOrder.getTotalMoneyPaid(), BigDecimalUtil.Operator.add))
        );
        transStatistic.getAllTransactionStatistics().setOrderNum(
            userOrder.getOrderNum() + memberCardOrder.getOrderNum() + couponPackOrder.getOrderNum() + cardRenew.getOrderNum() + cardCharge.getOrderNum() + serviceOrder.getOrderNum() + storeOrder.getOrderNum()
        );
        transStatistic.getAllTransactionStatistics().setUnitPrice(
            transStatistic.getAllTransactionStatistics().getOrderNum() > 0 ?
                BigDecimalUtil.divide(transStatistic.getAllTransactionStatistics().getTotalMoneyPaid(), new BigDecimal(transStatistic.getAllTransactionStatistics().getOrderNum())) :
                BigDecimal.ZERO
        );
        transStatistic.getAllTransactionStatistics().setReturnOrderMoney(
            BigDecimalUtil.addOrSubtrac(
                BigDecimalUtil.BigDecimalPlus.create(returnOrderTuple.v1, BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(returnVirtualOrder.v1, BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(returnServiceOrder.v1, BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(returnStoreOrder.v1, BigDecimalUtil.Operator.add))
        );
        transStatistic.getAllTransactionStatistics().setReturnOrderNum(
            returnOrderTuple.v2 + returnVirtualOrder.v2 + returnServiceOrder.v2 + returnStoreOrder.v2
        );
    }

    private static Timestamp getMaxTimestamp(Timestamp... timestamps) {
        Timestamp r = null;
        for (Timestamp t : timestamps) {
            boolean isMaxT = t != null && (r == null || t.after(r));
            if (isMaxT) {
                r = t;
            }
        }
        return r;
    }


    /**
     * 	更新会员的信息
     *
     * @param param
     * @throws MpException
     */
    public void updateMemberInfo(MemberParam param) throws MpException {

		/** 更新user_detail */
		memberDao.updateMemberInfoSql(param);


		/** 更新用户邀请人*/
        if (param.getInviteId() != null && param.getUserId()!=null) {
        	if(param.getUserId().equals(param.getInviteId())) {
        		throw new MpException(JsonResultCode.USER_INVITED_MSG);
        	}

        	//  不能互为邀请人检查
            int bind = saas.getShopApp(getShopId()).mpDistribution.isBind(param.getInviteId());
            if(bind == param.getUserId()){
                throw new MpException(JsonResultCode.USER_INVITED_NOT_EACH_OTHER);
            }

            /** 更新user表*/
            memberDao.updateMemberInviteId(param.getUserId(),param.getInviteId());
            logger().info("设置分销处理");
            //  分销邀请人处理
            UserBindParam param2 = new UserBindParam();
        	param2.setUserId(param.getUserId());
        	param2.setInviteId(param.getInviteId());
            saas.getShopApp(getShopId()).mpDistribution.userBind(param2);
		}

	}


	/**
	 * 获取用户的所有可用会员卡
	 * @param userId
	 */
	public List<AvailableMemberCardVo> getAllAvailableMemberCard(Integer userId) {
		Result<Record> allAvailableMemberCard = memberDao.getAllAvailableMemberCard(userId);
		List<AvailableMemberCardVo> cardList = new ArrayList<>();
		allAvailableMemberCard.stream()
							  .forEach(
									  record->{
										  Timestamp expireTime = record.get(USER_CARD.EXPIRE_TIME);
										  if(expireTime==null || !CardUtil.isCardExpired(expireTime)) {
											  cardList.add(new AvailableMemberCardVo(record.get(MEMBER_CARD.ID),record.get(MEMBER_CARD.CARD_TYPE),record.get(MEMBER_CARD.CARD_NAME)));
										  }
										  }
                              );
        return cardList;
    }


    /**
     * 处理会员获取会员卡详情
     * @param param
     * @param param
	 * @return
	 */
	public List<UserCardDetailVo> getAllUserCardDetail(UserCardDetailParam param) {
		logger().info("处理会员的会员卡列表详情");
		Result<Record> allUserCardDetail = memberDao.getAllUserCardDetailSql(param);
		List<UserCardDetailVo> res = allUserCardDetail.into(UserCardDetailVo.class);
		Map<String, UserCardParam> cardMap = allUserCardDetail.intoMap(USER_CARD.CARD_NO, UserCardParam.class);
		 // 处理包邮信息
		 String cardNo = null;
		 for(UserCardDetailVo vo: res) {
			 UserCardParam card = cardMap.get(vo.getCardNo());
			 CardFreeship freeshipData = freeShipSvc.getFreeshipData(card, null);
			 vo.setFreeShip(freeshipData);
         }
        return res;
    }

    /**
     * 获得用户手机号
     * @param mobile
	 * @return
	 */
	public UserImportDetailRecord getUserByMobile(String mobile) {

		return db().selectFrom(USER_IMPORT_DETAIL)
				.where(USER_IMPORT_DETAIL.MOBILE.eq(mobile)
						.and(USER_IMPORT_DETAIL.ERROR_MSG.isNull().or(USER_IMPORT_DETAIL.ERROR_MSG.eq(""))).and(USER_IMPORT_DETAIL.USER_ACTION.eq(YES_DISTRIBUTOR)))
				.orderBy(USER_IMPORT_DETAIL.ID.desc()).fetchAny();
	}

	public void updateUserDetail(UserDetailRecord record) {
		memberDao.updateUserDetail(record);
	}


    public Result<UserRecord> getUserRecordByIds(List<Integer> collect) {
        return db().selectFrom(User.USER).where(User.USER.USER_ID.in(collect))
            .fetchInto(User.USER);
    }

	public PageResult<MemberGoodsBrowseReportVo> userGoodsRecordReport(MemberGoodsBrowseReportParam param) {
		 return userGoodsRecordDao.userGoodsBrowseReport(param);
	}
}
