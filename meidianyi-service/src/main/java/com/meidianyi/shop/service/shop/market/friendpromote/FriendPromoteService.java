package com.meidianyi.shop.service.shop.market.friendpromote;

import static com.meidianyi.shop.db.shop.Tables.FRIEND_PROMOTE_ACTIVITY;
import static com.meidianyi.shop.db.shop.Tables.FRIEND_PROMOTE_DETAIL;
import static com.meidianyi.shop.db.shop.Tables.FRIEND_PROMOTE_LAUNCH;
import static com.meidianyi.shop.db.shop.Tables.FRIEND_PROMOTE_TIMES;
import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.Tables.MRKING_VOUCHER;
import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.PRIZE_RECORD;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.meidianyi.shop.service.pojo.shop.market.friendpromote.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.cj.util.StringUtils;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.FriendPromoteActivity;
import com.meidianyi.shop.db.shop.tables.FriendPromoteDetail;
import com.meidianyi.shop.db.shop.tables.FriendPromoteLaunch;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.records.FriendPromoteActivityRecord;
import com.meidianyi.shop.db.shop.tables.records.FriendPromoteLaunchRecord;
import com.meidianyi.shop.db.shop.tables.records.FriendPromoteTimesRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PrizeRecordRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueBo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.shop.coupon.CouponGiveService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.market.prize.PrizeRecordService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.task.wechat.MaMpScheduleTaskService;

/**
 * 好友助力
 *
 * @author liangchen
 * @date 2019年8月7日
 */
@Service
public class FriendPromoteService extends ShopBaseService {
    @Autowired private ImageService imageService;
    @Autowired private CouponGiveService couponGiveService;
    @Autowired private PrizeRecordService prizeRecordService;
    @Autowired private MaMpScheduleTaskService maMpScheduleTaskService;
    @Autowired private AtomicOperation atomicOperation;
    @Autowired private QrCodeService qrCode;
	private static FriendPromoteActivity fpa = FriendPromoteActivity.FRIEND_PROMOTE_ACTIVITY.as("fpa");
	private static FriendPromoteLaunch fpl = FriendPromoteLaunch.FRIEND_PROMOTE_LAUNCH.as("fpl");
	private static FriendPromoteDetail fpd = FriendPromoteDetail.FRIEND_PROMOTE_DETAIL.as("fpd");

	private static final Byte[] REWARD_TYPES = new Byte[] {(byte)0,(byte)1};
	private static final byte ZERO = 0;
	private static final byte ONE = 1;
	private static final byte TWO = 2;
	/**
	 * 好友助力活动列表
	 *
	 * @param param 活动名称等查询条件
	 * @return pageResult
	 */
	public PageResult<FriendPromoteListVo> friendPromoteList(FriendPromoteListParam param) {
		// 查询好友助力活动
		Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		SelectConditionStep<Record8<Integer, String,String, Timestamp, Timestamp, Byte, String, Byte>> sql = db()
				.select(fpa.ID, fpa.ACT_CODE,fpa.ACT_NAME, fpa.START_TIME, fpa.END_TIME, fpa.REWARD_TYPE, fpa.REWARD_CONTENT,
						fpa.IS_BLOCK)
				.from(fpa).where(fpa.DEL_FLAG.eq(FriendPromoteListParam.NOT_DELETE));
		//查询条件：活动名称
		if (!StringUtils.isNullOrEmpty(param.getActName())) {
			sql = sql.and(fpa.ACT_NAME.like(this.likeValue(param.getActName())));
		}
		//查询条件：开始时间
		if (param.getStartTime() != null) {
			sql = sql.and(fpa.START_TIME.greaterOrEqual(param.getStartTime()));
		}
		// 查询条件：结束时间
		if (param.getEndTime() != null) {
			sql = sql.and(fpa.END_TIME.lessOrEqual(param.getEndTime()));
		}
		// 查询条件：奖励类型
		if (!param.getRewardType().equals(FriendPromoteListParam.REWARD_TYPE_DEFAULT_VALUE)) {
			sql = sql.and(fpa.REWARD_TYPE.eq(param.getRewardType()));
		}
		// 活动状态0全部
		if (FriendPromoteListParam.ALL.equals(param.getActState())) {
		}
		// 活动状态1进行中
		if (FriendPromoteListParam.DOING.equals(param.getActState())) {
			sql = sql.and(fpa.IS_BLOCK.eq(ZERO)).and(fpa.START_TIME.lessOrEqual(nowTime))
					.and(fpa.END_TIME.greaterOrEqual(nowTime));
		}
		// 活动状态2未开始
		if (FriendPromoteListParam.TODO.equals(param.getActState())) {
			sql = sql.and(fpa.IS_BLOCK.eq(ZERO)).and(fpa.START_TIME.greaterOrEqual(nowTime));
		}
		// 活动状态3已结束
		if (FriendPromoteListParam.OUT_OF_DATE.equals(param.getActState())) {
			sql = sql.and(fpa.IS_BLOCK.eq(ZERO)).and(fpa.END_TIME.lessOrEqual(nowTime));
		}
		// 活动状态4已停用
		if (FriendPromoteListParam.STOPPED.equals(param.getActState())) {
			sql = sql.and(fpa.IS_BLOCK.eq((byte) 1));
		}
		sql.orderBy(fpa.ID.desc());
		// 整合分页信息
		PageResult<FriendPromoteListVo> pageResultVo = getPageResult(sql, param.getCurrentPage(), param.getPageRows(),
				FriendPromoteListVo.class);
		// 获取领取数量和活动状态
		for (FriendPromoteListVo vo : pageResultVo.getDataList()) {
		    vo.setFpRewardContent(Util.json2Object(vo.getRewardContent(),FpRewardContent.class,false));
			// 领取数量recNum
			int recNum = db().select(DSL.count(fpl.PROMOTE_STATUS).as("recNum")).from(fpl)
					.where(fpl.PROMOTE_ID.eq(vo.getId()))
					.and(fpl.PROMOTE_STATUS.eq( FriendPromoteListParam.RECEIVED)).fetchOptionalInto(Integer.class)
					.orElse(0);
			vo.setRecNum(recNum);
			// 活动状态actState
			if (1 == vo.getIsBlock()) {
				vo.setActState(FriendPromoteListParam.STOPPED);
			}
			else if ( nowTime.after(vo.getStartTime())&& nowTime.before(vo.getEndTime())) {
				vo.setActState(FriendPromoteListParam.DOING);
			}
			else if (nowTime.before(vo.getStartTime())) {
				vo.setActState(FriendPromoteListParam.TODO);
			}
			else if (nowTime.after(vo.getEndTime())) {
				vo.setActState(FriendPromoteListParam.OUT_OF_DATE);
			}
		}
		return pageResultVo;
	}

	/**
	 * 启用或停用活动
	 *
	 * @param param 活动id
	 */
	public void startOrBlock(FriendPromoteOptionParam param) {
		// 查询当前停用状态
		int isBlock = db().select(fpa.IS_BLOCK).from(fpa).where(fpa.ID.eq(param.getId()))
				.fetchOptionalInto(Integer.class).get();
		// 若未停用，则停用
		if (isBlock == FriendPromoteOptionParam.BLOCKED) {
			db().update(fpa).set(fpa.IS_BLOCK, FriendPromoteOptionParam.NOT_BLOCK)
					.where(fpa.ID.eq(param.getId())).execute();
		}
		// 若已停用，则启用
		if (isBlock == FriendPromoteOptionParam.NOT_BLOCK) {
			db().update(fpa).set(fpa.IS_BLOCK, FriendPromoteOptionParam.BLOCKED).where(fpa.ID.eq(param.getId()))
					.execute();
		}
	}

	/**
	 * 删除单个活动
	 *
	 * @param param 活动id
	 * @return void
	 */
	public void deleteAct(FriendPromoteOptionParam param) {
		// 修改del_flag
		db().update(fpa).set(fpa.DEL_FLAG,  FriendPromoteOptionParam.DELETED).where(fpa.ID.eq(param.getId()))
				.execute();
	}

	/**
	 * 查询领取明细
	 *
	 * @param param
	 * @return PageResult<FriendPromoteReceiveVo>
	 */
	public PageResult<FriendPromoteReceiveVo> receiveDetail(FriendPromoteReceiveParam param) {
		// 设置查询条件
		int rewardType = db().select(fpa.REWARD_TYPE).from(fpa).where(fpa.ID.eq(param.getPromoteId()))
				.fetchOptionalInto(Integer.class).get();
		SelectConditionStep<Record6<String, String, Integer, Byte, Timestamp, String>> sql;
		// 助力优惠券
		SelectConditionStep<Record6<String, String, Integer, Byte, Timestamp, String>> couponSql = db()
				.select(USER.USERNAME, USER.MOBILE, fpl.ID, fpl.PROMOTE_STATUS, fpl.SUCCESS_TIME.as("rec_time"),
						fpl.ORDER_SN)
				.from(USER, fpl).where(fpl.PROMOTE_ID.eq(param.getPromoteId())).and(USER.USER_ID.eq(fpl.USER_ID));
		// 助力商品
		SelectConditionStep<Record6<String, String, Integer, Byte, Timestamp, String>> goodSql = db().select(USER.USERNAME, USER.MOBILE,
            fpl.ID, fpl.PROMOTE_STATUS, ORDER_INFO.CREATE_TIME.as("rec_time"), fpl.ORDER_SN)
				.from(fpl).leftJoin(USER).on(USER.USER_ID.eq(fpl.USER_ID)).leftJoin(ORDER_INFO)
				.on(ORDER_INFO.ORDER_SN.eq(fpl.ORDER_SN)).where(fpl.PROMOTE_ID.eq(param.getPromoteId()));

		// 判断是商品还是优惠券
		sql = (rewardType == FriendPromoteReceiveParam.COUPON) ? couponSql : goodSql;
		// 查询条件
		if (!StringUtils.isNullOrEmpty(param.getUsername())) {
			sql.and(USER.USERNAME.like(this.likeValue(param.getUsername())));
		}
		if (!StringUtils.isNullOrEmpty(param.getMobile())) {
			sql.and(USER.MOBILE.like(this.likeValue(param.getMobile())));
		}
		if (param.getId() != null) {
			sql.and(fpl.ID.eq(param.getId()));
		}
		if (!StringUtils.isNullOrEmpty(param.getOrderSn())) {
			sql.and(ORDER_INFO.ORDER_SN.like(this.likeValue(param.getOrderSn())));
		}
		if (param.getPromoteStatus() != FriendPromoteReceiveParam.PROMOTE_STATUS_DEFAULT) {
			if (param.getPromoteStatus()==FriendPromoteReceiveParam.RECEIVED) {
				sql.and(fpl.PROMOTE_STATUS.equal((byte) param.getPromoteStatus()));
			}else {
				sql.and(fpl.PROMOTE_STATUS.notEqual(FriendPromoteReceiveParam.RECEIVED));
			}

		}
		// 整合分页信息
		PageResult<FriendPromoteReceiveVo> pageResult = getPageResult(sql, param.getCurrentPage(), param.getPageRows(),
				FriendPromoteReceiveVo.class);

		return pageResult;
	}

	/**
	 * 发起明细
	 *
	 * @param param
	 * @return PageResult<FriendPromoteLaunchVo>
	 */
	public PageResult<FriendPromoteLaunchVo> launchDetail(FriendPromoteLaunchParam param) {
		//设置查询条件
		SelectHavingStep<? extends Record> sql = db().select(fpl.ID,USER.USER_ID,
                        USER.USERNAME, USER.MOBILE, DSL.countDistinct(fpd.USER_ID).as("join_num"),
						DSL.count(fpd.USER_ID).as("promote_times"), DSL.sum(fpd.PROMOTE_VALUE).as("promote_value"),
						fpl.PROMOTE_STATUS)
				.from(fpl).leftJoin(USER).on(fpl.USER_ID.eq(USER.USER_ID)).leftJoin(fpd).on(fpl.ID.eq(fpd.LAUNCH_ID))
				.where(fpl.PROMOTE_ID.eq(param.getPromoteId())).groupBy(fpl.ID,USER.USER_ID,USER.USERNAME, USER.MOBILE,fpl.PROMOTE_STATUS);
		// 查询条件
		if (!StringUtils.isNullOrEmpty(param.getUsername())) {
			sql.having(USER.USERNAME.like(this.likeValue(param.getUsername())));
		}
		if (!StringUtils.isNullOrEmpty(param.getMobile())) {
			sql.having(USER.MOBILE.like(this.likeValue(param.getMobile())));
		}
		if (param.getId() != null) {
			sql.having(fpl.ID.eq(param.getId()));
		}
		if ( ! FriendPromoteReceiveParam.PROMOTE_STATUS_DEFAULT.equals(param.getPromoteStatus())) {
			if (FriendPromoteReceiveParam.RECEIVED.equals(param.getPromoteStatus())) {
				sql.having(fpl.PROMOTE_STATUS.equal( param.getPromoteStatus()));
			}else {
				sql.having(fpl.PROMOTE_STATUS.notEqual( FriendPromoteReceiveParam.RECEIVED));
			}
		}
		// 整合分页信息
		PageResult<FriendPromoteLaunchVo> pageResult = getPageResult(sql, param.getCurrentPage(), param.getPageRows(),
				FriendPromoteLaunchVo.class);

		return pageResult;
	}

	/**
	 * 参与明细
	 *
	 * @param param
	 * @return
	 */
	public PageResult<FriendPromoteParticipateVo> participateDetail(FriendPromoteParticipateParam param) {
		User a = USER.as("a");
		User b = USER.as("b");
		SelectHavingStep<? extends Record> sql = db().select(fpd.LAUNCH_ID,
                        a.USER_ID.as("join_user_id"),a.USERNAME, a.MOBILE, a.INVITE_SOURCE,
                        b.USER_ID.as("launch_user_id"),b.USERNAME.as("launch_username"), DSL.count(fpd.USER_ID).as("promote_times"),
						DSL.sum(fpd.PROMOTE_VALUE).as("promote_value"))
				.from(fpd)
				.leftJoin(a).on(fpd.USER_ID.eq(a.USER_ID))
				.leftJoin(fpl).on(fpd.LAUNCH_ID.eq(fpl.ID))
				.leftJoin(b).on(fpl.USER_ID.eq(b.USER_ID))
				.where(fpl.PROMOTE_ID.eq(param.getPromoteId()))
				.groupBy(fpd.LAUNCH_ID,a.USER_ID.as("join_user_id"),a.USERNAME, a.MOBILE, a.INVITE_SOURCE,
                    b.USER_ID.as("launch_user_id"),b.USERNAME.as("launch_username"));
		// 查询条件
		if (!StringUtils.isNullOrEmpty(param.getUsername())) {
			sql.having(a.USERNAME.like(this.likeValue(param.getUsername())));
		}
		if (!StringUtils.isNullOrEmpty(param.getMobile())) {
			sql.having(a.MOBILE.like(this.likeValue(param.getMobile())));
		}
		if (param.getLaunchId() != null) {
			sql.having(fpd.LAUNCH_ID.eq(param.getLaunchId()));
		}
		if (!StringUtils.isNullOrEmpty(param.getInviteSource())) {
			if (MemberService.INVITE_SOURCE_PROMOTE.equalsIgnoreCase(param.getInviteSource())) {
				sql.having(a.INVITE_SOURCE.eq(MemberService.INVITE_SOURCE_PROMOTE));
			}else {
				sql.having(a.INVITE_SOURCE.notEqual(MemberService.INVITE_SOURCE_PROMOTE).or(a.INVITE_SOURCE.isNull()));
			}
		}

		// 整合分页信息
		PageResult<FriendPromoteParticipateVo> pageResult = getPageResult(sql, param.getCurrentPage(), param.getPageRows(),
				FriendPromoteParticipateVo.class);

		return pageResult;

	}

	/**
	 * 添加好友助力活动
	 *
	 * @param param 活动信息
	 */
	public void addActivity(FriendPromoteAddParam param) {
		FriendPromoteActivityRecord record = new FriendPromoteActivityRecord();
		String rewardContent = Util.toJson(param.getFpRewardContent());
		String activityCopywriting = Util.toJson(param.getActCopywriting());
		record.setActivityCopywriting(activityCopywriting);
		record.setRewardContent(rewardContent);
		record.setShopId(getShopId());
		record.setActCode(getActCode());
		FieldsUtil.assignNotNull(param, record);
		db().executeInsert(record);
	}

	/**
	 * 生成助力活动编号
	 * @return 活动编号
	 */
	public static String getActCode() {
		System.currentTimeMillis();
		String sn = "FP" + System.currentTimeMillis();
		return sn;
	}

	/**
	 * 查询单个好友助力活动
	 *
	 * @param param 活动id
	 * @return 活动详情
	 */
	public FriendPromoteSelectVo selectOne(FriendPromoteSelectParam param) {

		FriendPromoteSelectVo vo = db().select().from(fpa).where(fpa.ID.eq(param.getId()))
				.fetchOneInto(FriendPromoteSelectVo.class);
        if (vo!=null&&vo.getActivityCopywriting()!=null){
            PromoteActCopywriting copywriting = Util.json2Object(vo.getActivityCopywriting(), PromoteActCopywriting.class,false);
            vo.setActCopywriting(copywriting);
        }
		return vo;
	}

	/**
	 * 修改好友助力活动信息
	 *
	 * @param param 活动id
	 */
	public void updateActivity(FriendPromoteUpdateParam param) {
		FriendPromoteActivityRecord record = new FriendPromoteActivityRecord();
        String rewardContent = Util.toJson(param.getFpRewardContent());
        String activityCopywriting = Util.toJson(param.getActCopywriting());
        record.setActivityCopywriting(activityCopywriting);
        record.setRewardContent(rewardContent);
		FieldsUtil.assignNotNull(param, record);
		db().executeUpdate(record);

	}

	/**
	 * 助力活动信息
	 * @param promoteId
	 * @param actCode
	 * @return
	 */
	public FriendPromoteActivityRecord promoteInfo(Integer promoteId,String actCode) {
		SelectWhereStep<FriendPromoteActivityRecord> selectFrom = db().selectFrom(fpa);
		if(promoteId>0) {
			selectFrom.where(fpa.ID.eq(promoteId));
		}if(!org.springframework.util.StringUtils.isEmpty(actCode)) {
			selectFrom.where(fpa.ACT_CODE.eq(actCode));
		}
		return selectFrom.fetchAny();
	}

	/**
	 * 获取结束前后
	 * @param hours
	 * @return
	 */
	public List<FriendPromoteSelectVo> getLaunchListByHour(Integer hours) {
		Timestamp timeStampPlus = DateUtils.getTimeStampPlus(hours, ChronoUnit.HOURS);
		String date = DateUtils.dateFormat("yyyy-MM-dd HH:mm", timeStampPlus);
		Result<Record> fetch = db()
				.select(fpl.asterisk(),fpa.ACT_CODE,fpa.ACT_NAME,fpa.REWARD_CONTENT,fpa.REWARD_TYPE).from(fpl,
						fpa)
				.where(fpl.PROMOTE_ID.eq(fpa.ID).and(fpa.DEL_FLAG.eq(ZERO)).and(fpl.DEL_FLAG.eq(ZERO)).and(fpl.PROMOTE_STATUS.eq(ZERO))
						.and(dateFormat(fpa.END_TIME, DateUtils.DATE_MYSQL_DAY).eq(date)))
				.fetch();
		List<FriendPromoteSelectVo> into = new ArrayList<FriendPromoteSelectVo>();
		if (fetch != null) {
			into = fetch.into(FriendPromoteSelectVo.class);
		}
		return into;
	}

	/**
	 * 获取助力失败列表
	 * @param hours
	 * @return
	 */
	public List<FriendPromoteSelectVo> getPromoteFailedList(Integer hours) {
		Timestamp timeStampPlus = DateUtils.getTimeStampPlus(hours, ChronoUnit.HOURS);
		String date = DateUtils.dateFormat("yyyy-MM-dd HH:mm", timeStampPlus);
		Result<Record> fetch = db().select(fpl.asterisk(),fpa.ACT_CODE,fpa.ACT_NAME,fpa.REWARD_CONTENT,fpa.REWARD_TYPE,fpa.FAILED_SEND_TYPE,fpa.FAILED_SEND_CONTENT).from(fpl,
						fpa)
				.where(fpl.PROMOTE_ID.eq(fpa.ID).and(
						fpa.DEL_FLAG.eq(ZERO)).and(fpl.DEL_FLAG.eq(ZERO)).and(fpl.PROMOTE_STATUS.eq(ZERO))
								.and(dateFormat(fpl.LAUNCH_TIME, DateUtils.DATE_MYSQL_DAY).eq(date).or(fpa.END_TIME.le(DateUtils.getLocalDateTime()))))
				.fetch();
		List<FriendPromoteSelectVo> into = new ArrayList<FriendPromoteSelectVo>();
		if (fetch != null) {
			into = fetch.into(FriendPromoteSelectVo.class);
		}
		return into;
	}

	/**
	 * 修改助力状态
	 * @param status
	 * @param id
	 * @return
	 */
	public int upPromoteInfo(Byte status,Integer id) {
		return db().update(fpl).set(fpl.PROMOTE_STATUS,status ).where(fpl.ID.eq(id)).execute();
	}

	/**
	 * 助力失效前一小时
	 * @param hours
	 * @return
	 */
	public List<FriendPromoteSelectVo> getPromoteWaitReceiveList(Integer hours) {
		logger().info("运行助力失效前一小时的sql");
		Timestamp timeStampPlus = DateUtils.getTimeStampPlus(hours, ChronoUnit.HOURS);
		String date = DateUtils.dateFormat("yyyy-MM-dd HH:mm", timeStampPlus);
		SelectConditionStep<Record> where = db()
				.select(fpl.asterisk(), fpa.ACT_CODE, fpa.ACT_NAME, fpa.REWARD_CONTENT, fpa.REWARD_TYPE).from(fpl, fpa)
				.where(fpl.PROMOTE_ID.eq(fpa.ID).and(fpa.REWARD_TYPE.in(REWARD_TYPES)).and(fpa.DEL_FLAG.eq(ZERO))
						.and(fpl.DEL_FLAG.eq(ZERO)).and(fpl.PROMOTE_STATUS.eq(ONE)));
		Field<String> left = DSL.left(DSL.dateAdd(fpl.SUCCESS_TIME.cast(Date.class),
				DSL.when(fpa.REWARD_DURATION_UNIT.eq(ZERO), fpa.REWARD_DURATION)
						.when(fpa.REWARD_DURATION_UNIT.eq(ONE), fpa.REWARD_DURATION.multiply(24))
						.when(fpa.REWARD_DURATION_UNIT.eq(TWO), fpa.REWARD_DURATION.multiply(7).multiply(24)).otherwise(1),
				DatePart.HOUR).cast(String.class), 16);
		where.and(left.eq(date));
		Result<Record> fetch = where.fetch();
		List<FriendPromoteSelectVo> into = new ArrayList<FriendPromoteSelectVo>();
		if (fetch != null) {
			into = fetch.into(FriendPromoteSelectVo.class);
		}
		return into;
	}

	public FriendPromoteSelectVo getUserLaunchInfo(Integer launchId) {
		Record fetchAny = db()
				.select(fpl.asterisk(), fpa.ACT_CODE, fpa.ACT_NAME, fpa.REWARD_CONTENT, fpa.REWARD_TYPE,
						fpa.FAILED_SEND_TYPE, fpa.FAILED_SEND_CONTENT)
				.from(fpl, fpa).where(fpl.PROMOTE_ID.eq(fpa.ID).and(fpl.ID.eq(launchId))).fetchAny();
		FriendPromoteSelectVo vo = null;
		if (fetchAny != null) {
			vo = fetchAny.into(FriendPromoteSelectVo.class);
		}
		return vo;
	}

    /**
     * 小程序-好友助力
     */
    public PromoteInfo promoteInfo(PromoteParam param){
        //助力活动展示信息
        PromoteInfo promoteInfo = getPromoteInfo(param.getActCode());
        //发起的好友助力
        FriendPromoteLaunchRecord launchInfo;
        if (param.getLaunchId()!=null&&param.getLaunchId()>0){
            launchInfo = getLaunchInfo(param.getLaunchId(),null,null);
        }
        else{
            launchInfo = getLaunchInfo(null,param.getUserId(),promoteInfo.getId());
        }
        //发起用户id
        Integer launchUserId = launchInfo!=null?launchInfo.getUserId():0;
        //发起or助力标识:1发起页面，2助力页面
        Integer launchFlag = (!param.getUserId().equals(launchUserId) && launchUserId>0)?2:1;
        //设置助力进度：-1未发起，0助力中，1助力完成待领取，2助力完成已领取，
        // 3助力未领取失效，4助力未完成失败，5取消订单未领取
        promoteInfo.setPromoteStatus(launchInfo!=null?launchInfo.getPromoteStatus():-1);
        //设置已被助力总次数
        promoteInfo.setHasPromoteTimes(0);
        if (launchInfo!=null){
            promoteInfo.setHasPromoteTimes(getHasPromoteTimes(launchInfo.getId(),null,null,null));
        }

        // 处理发起页 和 助力页
        processLaunchAndHelpPage(param, promoteInfo, launchInfo, launchFlag);

        //活动倒计时
        promoteDownTime(promoteInfo,launchInfo);
        //奖励倒计时
        if (promoteInfo.getPromoteStatus()==1){
            Long duration = promoteDurationSec(promoteInfo.getRewardDurationUnit(),promoteInfo.getRewardDuration());
            //成功时间 倒计时的起点
            Long successTime = getSuccessTime(launchInfo.getId()).getTime();
            //奖励结束时间
            Long endTime = successTime+duration*1000;
            Long nowTime = DateUtils.getLocalDateTime().getTime();
            Long spurTime = endTime-nowTime>0?(endTime/1000-nowTime/1000):0;
            promoteInfo.setRewardSpurTime(spurTime);
        }

        //已助力总值
        if (promoteInfo.getPromoteStatus()>=0){
            promoteInfo.setHasPromoteValue(hasPromoteValue(launchInfo.getId()));
            promoteInfo.setLaunchId(launchInfo.getId());
        }else {
            promoteInfo.setHasPromoteValue(0);
            promoteInfo.setHasPromoteTimes(0);
            promoteInfo.setLaunchId(null);
        }

        //更多的助力活动
        promoteInfo.setPromoteActList(promoteActList(promoteInfo.getId()));
        promoteInfo.setLaunchFlag(launchFlag);
        //todo 生成助力图片
        //设置奖品记录id 下单用
        if (launchInfo!=null){
            promoteInfo.setRewardRecordId(getRewardRecordId(param.getUserId(),promoteInfo.getId(),launchInfo.getId()));
        }
        if (promoteInfo.getRewardRecordId()!=null&&promoteInfo.getRewardRecordId()!=0){

            String orderSn = getRewardOrderSn(promoteInfo.getRewardRecordId());
            if (orderSn!=null&&!"".equals(orderSn)&&orderSn.length()>0){
                logger().info("奖品已领取，开始同步活动状态和订单编号:{}",orderSn);
                if (launchInfo.getId()!=null){
                    //更新状态
                    upPromoteInfo(TWO,launchInfo.getId());
                    //更新订单号
                    upOrderSn(orderSn,launchInfo.getId());
                    promoteInfo.setPromoteStatus((byte)2);
                    //重新设置订单
                    promoteInfo.setOrderSn(orderSn);
                }
                logger().info("同步完成活动状态和订单编号:{}",orderSn);
            }
        }
        return promoteInfo;
    }

    private void processLaunchAndHelpPage(PromoteParam param, PromoteInfo promoteInfo, FriendPromoteLaunchRecord launchInfo, Integer launchFlag) {
        //如果是发起页面
        if(launchFlag==1){
            //是否可以再次发起好友助力
            CanLaunch canLaunch = canLaunch(promoteInfo,launchInfo,param.getUserId());
            promoteInfo.setCanLaunch(canLaunch.getCode());
            //助力完成或者失效装修页进入后
//            if (param.getLaunchId()==null&&promoteInfo.getCanLaunch()==1){
//                promoteInfo.setPromoteStatus((byte)-1);
//            }
            //好友助力榜
            if(launchInfo!=null&&promoteInfo.getPromoteStatus()>=0){
                promoteInfo.setPromoteDetailList(friendPromoteDetail(launchInfo.getId()));
            }
            //设置订单
            promoteInfo.setOrderSn(launchInfo==null?null:launchInfo.getOrderSn());
            //助力完成订单操作标识：0不可下单，1立即下单，2查看订单详情
            Byte orderFlag = 0;
            //奖励类型不为优惠券
            if (promoteInfo.getRewardType()!=2){
                //助力进度为完成待领取
                if (promoteInfo.getPromoteStatus()==1){
//                    OrderInfoRecord orderInfo = getOrder(launchInfo.getOrderSn());
//                    if (orderInfo==null||orderInfo.getOrderStatus()== OrderConstant.ORDER_CANCELLED||orderInfo.getOrderStatus()==OrderConstant.ORDER_CLOSED||DelFlag.DISABLE_VALUE.equals(orderInfo.getDelFlag())){
                        orderFlag = 1;
//                    }else {
//                        orderFlag = 2;
//                    }
                }else if (promoteInfo.getPromoteStatus()==2){
                    orderFlag = 2;
                }
            }
            promoteInfo.setOrderFlag(orderFlag);
        }

        //否则为助力界面
        else {
            //是否可以继续助力
            CanPromote canPromote = canPromote(promoteInfo,promoteInfo.getHasPromoteTimes(),param.getUserId(),param.getLaunchId());
            promoteInfo.setCanPromote(canPromote);
            //是否可以分享获取助力次数
            Byte canShareTimes = canShareTimes(promoteInfo.getShareCreateTimes(),param.getUserId(),param.getLaunchId());
            promoteInfo.setCanShare(canShareTimes>0?(byte)1:(byte)0);
        }
    }

    /**
     * 修改助力状态
     * @param orderSn 订单号
     * @param id    发起id
     * @return
     */
    public int upOrderSn(String orderSn,Integer id) {
        return db().update(fpl).set(fpl.ORDER_SN,orderSn ).where(fpl.ID.eq(id)).execute();
    }
    /**
     * 得到奖品订单
     * @param id 奖品记录id
     * @return 订单
     */
    public String getRewardOrderSn(Integer id){
        String orderSn = db().select(PRIZE_RECORD.ORDER_SN)
            .from(PRIZE_RECORD)
            .where(PRIZE_RECORD.ID.eq(id))
            .fetchOneInto(String.class);
        return orderSn;
    }

    /**
     * 得到指定actCode活动的信息
     * @param actCode 唯一活动码
     * @return 活动详情
     */
    public FriendPromoteActivityRecord getInfo(String actCode){
        if (actCode!=null&&!actCode.isEmpty()){
            FriendPromoteActivityRecord record = db().select().from(FRIEND_PROMOTE_ACTIVITY)
                .where(FRIEND_PROMOTE_ACTIVITY.ACT_CODE.eq(actCode))
                .fetchOne().into(FriendPromoteActivityRecord.class);
            return record;
        }
        else {
            return null;
        }
    }

    /**
     * 小程序-得到助力信息
     * @param actCode 唯一活动码
     * @return 助力信息
     */
    public PromoteInfo getPromoteInfo(String actCode){
        //展示内容promoteInfo
        PromoteInfo promoteInfo = new PromoteInfo();
        //得到当前活动部分信息
        FriendPromoteActivityRecord record = getInfo(actCode);
        //设置奖励内容
        FpRewardContent rewardContent = Util.json2Object(record.getRewardContent(),FpRewardContent.class,false);
        promoteInfo.setRewardContent(rewardContent);
        //设置活动id
        promoteInfo.setId(record.getId());
        //设置助力次数
        promoteInfo.setHasLaunchNum(launchTotalTimes(record.getId()));
        //设置活动库存
        promoteInfo.setMarketStore(rewardContent.getMarketStore());
        //设置活动状态
        promoteInfo.setActStatus(getActStatus(actCode));
        //设置是否停用
        promoteInfo.setIsBlock(record.getIsBlock());
        //设置是否删除
        promoteInfo.setDelFlag(record.getDelFlag());
        //设置奖励类型
        promoteInfo.setRewardType(record.getRewardType());
        //设置发起限制次数
        promoteInfo.setLaunchLimitTimes(record.getLaunchLimitTimes());
        //设置发起次数限制时长
        promoteInfo.setLaunchLimitDuration(record.getLaunchLimitDuration());
        //设置发起次数限制时长单位
        promoteInfo.setLaunchLimitUnit(record.getLaunchLimitUnit());
        //设置所需助力次数
        promoteInfo.setPromoteTimes(record.getPromoteTimes());
        //设置单个用户每天最多可帮忙助力次数
        promoteInfo.setPromoteTimesPerDay(record.getPromoteTimesPerDay());
        //设置分享可获得助力次数
        promoteInfo.setShareCreateTimes(record.getShareCreateTimes());
        //设置结束时间
        promoteInfo.setEndTime(record.getEndTime());
        //设置所需助力总值
        promoteInfo.setPromoteAmount(record.getPromoteAmount());
        //设置助力类型 0平均 1随机
        promoteInfo.setPromoteType(record.getPromoteType());
        //设置分享图片相关
        promoteInfo.setActivityShareType(record.getActivityShareType());
        promoteInfo.setCustomShareWord(record.getCustomShareWord());
        promoteInfo.setShareImgType(record.getShareImgType());
        promoteInfo.setCustomImgPath(record.getCustomImgPath());
        //设置奖励倒计时
        promoteInfo.setRewardDuration(record.getRewardDuration());
        promoteInfo.setRewardDurationUnit(record.getRewardDurationUnit());
        //设置授权相关
        promoteInfo.setPromoteCondition(record.getPromoteCondition());
        //设置活动说明相关
        if (record.getActivityCopywriting()!=null){
            promoteInfo.setActCopywriting(Util.json2Object(record.getActivityCopywriting(), PromoteActCopywriting.class,false));
        }

        // 设置商品信息和库存
        setPromotGoodsAndStore(promoteInfo, record, rewardContent);

        //销毁奖励内容
        promoteInfo.setRewardContent(null);
        return promoteInfo;
    }

    private void setPromotGoodsAndStore(PromoteInfo promoteInfo, FriendPromoteActivityRecord record, FpRewardContent rewardContent) {
        //判断奖励类型-为赠送商品或商品折扣时
        if(record.getRewardType()==ZERO||record.getRewardType()==ONE){
            GoodsInfo goodsInfo = getGoodsInfo(rewardContent.getGoodsIds());
            if (goodsInfo==null){
                goodsInfo = new GoodsInfo();
            }
            goodsInfo.setMarketPrice(record.getRewardType()==ONE?rewardContent.getMarketPrice(): BigDecimal.ZERO);
            //设置商品信息
            promoteInfo.setGoodsInfo(goodsInfo);
            //检查活动库存是否发完
            promoteInfo.setMarketStore(promoteInfo.getMarketStore()>promoteInfo.getHasLaunchNum()?promoteInfo.getMarketStore()-promoteInfo.getHasLaunchNum():0);
            //商品库存与活动库存比较 重新设置活动库存
            if (goodsInfo.getGoodsStore()!=null){
                promoteInfo.setMarketStore(goodsInfo.getGoodsStore()>promoteInfo.getMarketStore()?promoteInfo.getMarketStore():goodsInfo.getGoodsStore());
            }
        }
        //判断奖励类型-为赠送优惠券时
        else if (record.getRewardType()==TWO){
            CouponInfo couponInfo = getCouponById(rewardContent.getRewardIds());
            //设置优惠券信息
            promoteInfo.setCouponInfo(couponInfo);
            //检查活动库存是否发完
            promoteInfo.setMarketStore(promoteInfo.getMarketStore()>promoteInfo.getHasLaunchNum()?promoteInfo.getMarketStore()-promoteInfo.getHasLaunchNum():0);
        }
    }

    /**
     * 得到当前活动助力次数
     * @param id 当前助力活动id
     * @return  活动助力次数
     */
    public Integer launchTotalTimes(Integer id){
        List<Byte> promoteStatus = new ArrayList<>();
        promoteStatus.add(ONE);
        promoteStatus.add(TWO);
        Integer hasLaunchNum = db().select(DSL.count(FRIEND_PROMOTE_LAUNCH.ID))
            .from(FRIEND_PROMOTE_LAUNCH)
            .where(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(id))
            .and(FRIEND_PROMOTE_LAUNCH.PROMOTE_STATUS.in(promoteStatus))
            .fetchOneInto(Integer.class);
        return hasLaunchNum;
    }

    /**
     * 根据规格id返回商品信息
     * @param prdId 规格id
     * @return 商品信息
     */
    public GoodsInfo getGoodsInfo(Integer prdId){
        GoodsInfo goodsInfo = db().select(GOODS.GOODS_ID,GOODS_SPEC_PRODUCT.PRD_ID,GOODS_SPEC_PRODUCT.PRD_DESC,GOODS.GOODS_NAME,
            GOODS_SPEC_PRODUCT.PRD_IMG.as("goods_img"),GOODS_SPEC_PRODUCT.PRD_PRICE.as("goods_price"),
            GOODS_SPEC_PRODUCT.PRD_NUMBER.as("goods_store"),GOODS.UPDATE_TIME)
            .from(GOODS)
            .leftJoin(GOODS_SPEC_PRODUCT)
            .on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
            .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
            .fetchOneInto(GoodsInfo.class);
        String goodsImage = db().select(GOODS.GOODS_IMG)
            .from(GOODS)
            .leftJoin(GOODS_SPEC_PRODUCT)
            .on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
            .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
            .fetchOneInto(String.class);
        if (null!=goodsInfo){
            if(StringUtils.isNullOrEmpty(goodsInfo.getGoodsImg())){
                goodsInfo.setGoodsImg(goodsImage);
            }
            //图片地址添加域名
            String goodsImg = imageService.getImgFullUrl(goodsInfo.getGoodsImg());
            goodsInfo.setGoodsImg(goodsImg);
        }
        return goodsInfo;
    }

    /**
     * 根据id返回优惠券信息
     * @param couponId 优惠券id
     * @return 优惠券信息
     */
    public CouponInfo getCouponById(Integer couponId){
        CouponInfo couponInfo = db().select(MRKING_VOUCHER.ID.as("coupon_id"),MRKING_VOUCHER.ACT_NAME,
            MRKING_VOUCHER.ACT_CODE,MRKING_VOUCHER.ALIAS_CODE,MRKING_VOUCHER.DENOMINATION,
            MRKING_VOUCHER.USE_CONSUME_RESTRICT,MRKING_VOUCHER.LEAST_CONSUME)
            .from(MRKING_VOUCHER)
            .where(MRKING_VOUCHER.ID.eq(couponId))
            .fetchOneInto(CouponInfo.class);
        return couponInfo;
    }

    /**
     * 得到助力活动状态
     * @param actCode 唯一活动码
     * @return 活动状态：0未开始，1进行中，2已结束
     */
    public Byte getActStatus(String actCode){
        Byte actStatus = 0;
        Timestamp startTime = db().select(FRIEND_PROMOTE_ACTIVITY.START_TIME)
            .from(FRIEND_PROMOTE_ACTIVITY)
            .where(FRIEND_PROMOTE_ACTIVITY.ACT_CODE.eq(actCode))
            .fetchOneInto(Timestamp.class);
        Timestamp endTime = db().select(FRIEND_PROMOTE_ACTIVITY.END_TIME)
            .from(FRIEND_PROMOTE_ACTIVITY)
            .where(FRIEND_PROMOTE_ACTIVITY.ACT_CODE.eq(actCode))
            .fetchOneInto(Timestamp.class);
        Timestamp nowTime = Util.currentTimeStamp();
        if (startTime.before(nowTime)&&endTime.after(nowTime)){
            actStatus = 1;
        }
        else if (endTime.before(nowTime)){
            actStatus = 2;
        }
        return actStatus;
    }

    /**
     * 得到发起助力信息
     * @param launchId 发起id
     * @param userId 用户id
     * @param promoteId 活动id
     * @return 助力信息
     */
    public FriendPromoteLaunchRecord getLaunchInfo(Integer launchId,Integer userId,Integer promoteId){
        FriendPromoteLaunchRecord record;
        if (launchId!=null){
            record = db().select()
                .from(FRIEND_PROMOTE_LAUNCH)
                .where(FRIEND_PROMOTE_LAUNCH.ID.eq(launchId))
                .fetchOneInto(FriendPromoteLaunchRecord.class);
        }
        else {
            record =db().select()
                .from(FRIEND_PROMOTE_LAUNCH)
                .where(FRIEND_PROMOTE_LAUNCH.USER_ID.eq(userId))
                .and(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(promoteId))
                .orderBy(FRIEND_PROMOTE_LAUNCH.ID.desc())
                .limit(1)
                .fetchOneInto(FriendPromoteLaunchRecord.class);
        }
        return record;
    }

    /**
     * 得到助力次数
     * @param launchId 助力发起id
     * @return 助力次数
     */
    public Integer getHasPromoteTimes(Integer launchId,Integer promoteId,Integer userId,Timestamp currentTime){
        SelectJoinStep<Record1<Integer>> select = db().select(DSL.count(FRIEND_PROMOTE_DETAIL.ID))
            .from(FRIEND_PROMOTE_DETAIL);
        if (launchId!=null){
            select.where(FRIEND_PROMOTE_DETAIL.LAUNCH_ID.eq(launchId));
        }
        if (promoteId!=null){
            select.where(FRIEND_PROMOTE_DETAIL.PROMOTE_ID.eq(promoteId));
        }
        if (userId!=null){
            select.where(FRIEND_PROMOTE_DETAIL.USER_ID.eq(userId));
        }
        if (currentTime!=null){
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            Timestamp startTime = new Timestamp(start.getTime().getTime());
            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.MILLISECOND, 999);
            Timestamp endTime = new Timestamp(end.getTime().getTime());
            select.where(FRIEND_PROMOTE_DETAIL.CREATE_TIME.between(startTime,endTime));
        }
        Integer hasPromoteTimes = select.fetchOptionalInto(Integer.class)
                .orElse(NumberUtils.INTEGER_ZERO);
        return hasPromoteTimes;
    }

    /**
     * 是否可以发起好友助力活动
     * @param promoteInfo 主力活动信息
     * @param launchInfo 该活动的发起信息
     * @param userId 当前用户id
     * @return 是否可发起及文字提示
     */
    public CanLaunch canLaunch(PromoteInfo promoteInfo,FriendPromoteLaunchRecord launchInfo,Integer userId){
        CanLaunch canLaunch = new CanLaunch();
        canLaunch.setCode(NumberUtils.BYTE_ZERO);
        //检查是否停用或者删除
        if(promoteInfo.getIsBlock().equals(NumberUtils.BYTE_ONE)||promoteInfo.getDelFlag().equals(NumberUtils.BYTE_ONE)){
            canLaunch.setMsg(1);
            return canLaunch;
        }
        //检查活动库存
        if (promoteInfo.getMarketStore()<=0){
            canLaunch.setMsg(2);
            return canLaunch;
        }
        //检查商品库存
        if (promoteInfo.getRewardType()!=2){
            if (promoteInfo.getGoodsInfo().getGoodsStore()<=0){
                canLaunch.setMsg(3);
                return canLaunch;
            }
        }
        //检查有效期
        if (promoteInfo.getActStatus()==0){
            canLaunch.setMsg(4);
            return canLaunch;
        }
        if (promoteInfo.getActStatus()==2){
            canLaunch.setMsg(5);
            return canLaunch;
        }
        //检查当前发起状态
        if(promoteInfo.getPromoteStatus()==0||promoteInfo.getPromoteStatus()==1){
            canLaunch.setMsg(6);
            return canLaunch;
        }
        //检查可发起次数
        if(promoteInfo.getPromoteStatus()!=0&&promoteInfo.getPromoteStatus()!=1){
            //从未发起
            if (promoteInfo.getPromoteStatus()==-1){
                canLaunch.setCode(NumberUtils.BYTE_ONE);
            }else {
                //发起次数不限
                if (promoteInfo.getLaunchLimitTimes()==0){
                    canLaunch.setCode(NumberUtils.BYTE_ONE);
                }else {
                    launchInfo = db().select()
                        .from(FRIEND_PROMOTE_LAUNCH)
                        .where(FRIEND_PROMOTE_LAUNCH.USER_ID.eq(userId))
                        .and(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(promoteInfo.getId()))
                        .orderBy(FRIEND_PROMOTE_LAUNCH.ID.asc())
                        .limit(1)
                        .fetchOneInto(FriendPromoteLaunchRecord.class);
                    //已发起助力次数
                    Integer launchTimes = promoteLaunchTimes(promoteInfo.getId(),userId,promoteInfo.getLaunchLimitDuration(),promoteInfo.getLaunchLimitUnit(),launchInfo.getLaunchTime());
                    logger().info("当前发起ID为{}，ID为{}的用户已经发起了{}次",launchInfo.getId(),userId,launchTimes);
                    logger().info("当前活动发起限制次数为{}次",promoteInfo.getLaunchLimitTimes());
                    if (launchTimes<promoteInfo.getLaunchLimitTimes().intValue()){
                        canLaunch.setCode(NumberUtils.BYTE_ONE);
                    }
                }
            }
        }
        return canLaunch;
    }

    /**
     * 某时间段内最多可发好友助力活动的次数
     * @param promoteId 活动id
     * @param userId 用户id
     * @param duration 限制时间数
     * @param unit 时间单位
     * @param launchTime 发起时间
     * @return 次数
     */
    public Integer promoteLaunchTimes(Integer promoteId,Integer userId,Integer duration,Byte unit,Timestamp launchTime){
        Duration timeDuration;
        switch (unit){
            //天
            case 0:
                timeDuration = getDurationDay(launchTime,duration);
                break;
            //周
            case 1:
                timeDuration = getDurationDay(launchTime,duration*7);
                break;
            //月
            case 2:
                timeDuration = getDurationDay(launchTime,duration*30);
                break;
            //年
            case 3:
                timeDuration = getDurationDay(launchTime,duration*365);
                break;
            default:
                return 0;
        }
        Integer launchTimes = db().select(DSL.count(FRIEND_PROMOTE_LAUNCH.ID))
            .from(FRIEND_PROMOTE_LAUNCH)
            .where(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(promoteId))
            .and(FRIEND_PROMOTE_LAUNCH.USER_ID.eq(userId))
            .and(FRIEND_PROMOTE_LAUNCH.LAUNCH_TIME.between(timeDuration.getStartTime(),timeDuration.getEndTime()))
            .and(FRIEND_PROMOTE_LAUNCH.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchOptionalInto(Integer.class)
            .orElse(0);
        return launchTimes;
    }

    /**
     * 任意一天和从此时间开始起多少天后的开始和结束时间
     * @param day 开始时间
     * @param limitDuration 持续时长
     * @return 起止时间
     */
    public Duration getDurationDay(Timestamp day,Integer limitDuration){
        Duration duration = new Duration();
        duration.setStartTime(day);
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        c.add(Calendar.DATE,limitDuration);
        Timestamp endTime = new Timestamp(c.getTimeInMillis());
        duration.setEndTime(endTime);
        return duration;
    }

    /**
     * 好友助力明细列表
     * @param launchId 发起id
     * @return 助力详情
     */
    public List<PromoteDetail> friendPromoteDetail(Integer launchId){
        List<PromoteDetail> detailList = db().select(FRIEND_PROMOTE_DETAIL.PROMOTE_VALUE,
            USER.USERNAME,USER.USER_ID,USER_DETAIL.USER_AVATAR)
            .from(FRIEND_PROMOTE_DETAIL)
            .leftJoin(USER).on(FRIEND_PROMOTE_DETAIL.USER_ID.eq(USER.USER_ID))
            .leftJoin(USER_DETAIL).on(FRIEND_PROMOTE_DETAIL.USER_ID.eq(USER_DETAIL.USER_ID))
            .where(FRIEND_PROMOTE_DETAIL.LAUNCH_ID.eq(launchId))
            .orderBy(FRIEND_PROMOTE_DETAIL.ID.desc())
            .limit(20)
            .fetchInto(PromoteDetail.class);
        for(PromoteDetail item:detailList){
            item.setUsername((item.getUsername()!=null&&!item.getUsername().isEmpty())?item.getUsername():item.getUserId().toString());
            item.setUserAvatar((item.getUserAvatar()!=null&&!item.getUserAvatar().isEmpty())?item.getUserAvatar():"/image/admin/head_icon.png");
        }
        return detailList;
    }

    /**
     * 得到订单信息
     * @param orderSn 订单编号
     * @return 订单信息
     */
    public OrderInfoRecord getOrder(String orderSn){
        OrderInfoRecord orderInfo = db().select()
            .from(ORDER_INFO)
            .where(ORDER_INFO.ORDER_SN.eq(orderSn))
            .fetchOneInto(OrderInfoRecord.class);
        return orderInfo;
    }

    /**
     * 校验是否可助力
     * @param promoteInfo 助力详情
     * @param hasPromoteTimes 已经助力次数
     * @param userId 用户id
     * @param launchId 发起id
     * @return 是否可助力信息
     */
    public CanPromote canPromote(PromoteInfo promoteInfo,Integer hasPromoteTimes,Integer userId,Integer launchId){
        logger().info("*********");
        logger().info("*promoteInfo*:"+promoteInfo);
        logger().info("*promoteInfoId*:"+promoteInfo.getId());
        logger().info("*********");
        CanPromote canPromote = new CanPromote();
        //是否发起
        if(promoteInfo.getPromoteStatus()==-1){
            canPromote.setCode((byte)0);
            canPromote.setMsg((byte)0);
            return canPromote;
        }
        //是否已完成助力
        if (hasPromoteTimes>=promoteInfo.getPromoteTimes()||promoteInfo.getPromoteStatus()==1||promoteInfo.getPromoteStatus()==2||promoteInfo.getPromoteStatus()==3){
            canPromote.setCode((byte)0);
            canPromote.setMsg((byte)1);
            return canPromote;
        }
        //判断当天助力次数限制
        if(promoteInfo.getPromoteTimesPerDay()>0){
            Integer usedPromoteTimesCurrentDay = getHasPromoteTimes(null,promoteInfo.getId(),userId, DateUtils.getLocalDateTime());
            Integer launchIdTime = getHasPromoteTimes(launchId,promoteInfo.getId(),userId, DateUtils.getLocalDateTime());
            logger().info("*********");
            logger().info("*判断当天助力次数限制*");
            logger().info("*********");
            logger().info("用户单天助力次数限制："+promoteInfo.getPromoteTimesPerDay());
            logger().info("用户对当前配置活动助力次数："+usedPromoteTimesCurrentDay);
            logger().info("用户对当前发起活动助力次数："+launchIdTime);
            if (usedPromoteTimesCurrentDay>=promoteInfo.getPromoteTimesPerDay()){
                canPromote.setCode((byte)0);
                canPromote.setMsg((byte)2);
                return canPromote;
            }
        }
        //获取所有可助力的次数
        Integer promoteTimesInfo = 0;
        FriendPromoteTimesRecord timesRecord = promoteTimesInfo(userId,launchId);
        if (timesRecord!=null){
            promoteTimesInfo = promoteTimesInfo(userId,launchId).getOwnPromoteTimes();
        }
        Integer ownPromoteTimes = promoteTimesInfo+1;
        //判断所有的助力次数限制
        Integer usedPromoteTimes = getHasPromoteTimes(launchId,null,userId,null);
        logger().info("*********");
        logger().info("*判断所有的助力次数限制*");
        logger().info("*********");
        logger().info("用户对当前发起活动已经助力次数："+usedPromoteTimes);
        logger().info("免费机会+分享授权机会："+ownPromoteTimes);
        if (usedPromoteTimes>=ownPromoteTimes){
            canPromote.setCode((byte)0);
            canPromote.setMsg((byte)3);
            return canPromote;
        }
        //活动是否失效
        if (promoteInfo.getPromoteTimes()==0&&promoteInfo.getActStatus()==2){
            canPromote.setCode((byte)0);
            canPromote.setMsg((byte)3);
            return canPromote;
        }
        canPromote.setCode((byte)1);
        return canPromote;
    }

    /**
     * 助力次数详情
     * @param userId 用户id
     * @param launchId 发起id
     * @return 次数详情
     */
    public FriendPromoteTimesRecord promoteTimesInfo(Integer userId,Integer launchId){
        FriendPromoteTimesRecord record = db().select()
            .from(FRIEND_PROMOTE_TIMES)
            .where(FRIEND_PROMOTE_TIMES.USER_ID.eq(userId))
            .and(FRIEND_PROMOTE_TIMES.LAUNCH_ID.eq(launchId))
            .fetchOneInto(FriendPromoteTimesRecord.class);
        return record;
    }

    /**
     * 获得用户可分享次数
     * @param shareCreateTimes 分享可获得助力次数
     * @param userId 用户id
     * @param launchId 发起id
     * @return 可分享次数
     */
    public Byte canShareTimes(Byte shareCreateTimes,Integer userId,Integer launchId){
        if(shareCreateTimes==0){
            return 0;
        }
        Integer hasShareTimes = promoteTimesInfo(userId,launchId)!=null?promoteTimesInfo(userId,launchId).getShareTimes():0;
        hasShareTimes = (hasShareTimes!=null&&hasShareTimes>0)?hasShareTimes:0;
        Integer canShareTimes =shareCreateTimes-hasShareTimes;
        canShareTimes = canShareTimes>0?canShareTimes:0;
        return canShareTimes.byteValue();
    }

    /**
     * 倒计时
     * @param promoteInfo 活动信息
     * @param launchInfo 发起信息
     */
    public void promoteDownTime(PromoteInfo promoteInfo,FriendPromoteLaunchRecord launchInfo){
        long secEndTime = promoteInfo.getEndTime().getTime();
        //如果助力进度是完成待领取
        if (promoteInfo.getPromoteStatus()==1){
            Long sec = promoteDurationSec(promoteInfo.getLaunchLimitUnit(),promoteInfo.getLaunchLimitDuration());
            Long surplusSecond = launchInfo.getCreateTime().getTime()+sec*1000- DateUtils.getLocalDateTime().getTime();
            promoteInfo.setSurplusSecond(surplusSecond>0?surplusSecond/1000:0);
        }
        //活动进行中 助力未开始
        else if (promoteInfo.getPromoteStatus()==0&&promoteInfo.getActStatus()==1){
            long secDeadTime = 24*60*60*1000;
            long secLaunchTime = launchInfo.getLaunchTime().getTime();
            if (secEndTime<secDeadTime+secLaunchTime){
                promoteInfo.setSurplusSecond((secEndTime- DateUtils.getLocalDateTime().getTime())/1000);
            }else {
                promoteInfo.setSurplusSecond((secDeadTime+secLaunchTime- DateUtils.getLocalDateTime().getTime())/1000);
            }
        }
        else {
            promoteInfo.setSurplusSecond(promoteInfo.getActStatus()==1?(secEndTime- DateUtils.getLocalDateTime().getTime())/1000:0);
        }
    }

    /**
     * 计算奖励有效期有多少秒
     * @param unit 时间单位
     * @param duration 持续时长
     * @return second
     */
    public Long promoteDurationSec(Byte unit,Integer duration){
        Integer sec ;
        switch (unit){
            //小时
            case 0:
                sec=duration*3600;
                break;
            //天
            case 1:
                sec=duration*24*3600;
                break;
            //周
            case 2:
                sec=duration*7*24*3600;
                break;
            default:
                sec = 0;
                break;
        }
        return sec.longValue();
    }

    /**
     * 得到助力总值
     * @param launchId 发起id
     * @return 助力总值
     */
    public Integer hasPromoteValue(Integer launchId){
        Integer promoteValue = db().select(DSL.sum(FRIEND_PROMOTE_DETAIL.PROMOTE_VALUE))
            .from(FRIEND_PROMOTE_DETAIL)
            .where(FRIEND_PROMOTE_DETAIL.LAUNCH_ID.eq(launchId))
            .fetchOptionalInto(Integer.class)
            .orElse(0);
        return promoteValue;
    }
    public List<PromoteActList> promoteActList(Integer id){
        List<PromoteActList> promoteActList = db().select()
            .from(FRIEND_PROMOTE_ACTIVITY)
            .where(FRIEND_PROMOTE_ACTIVITY.IS_BLOCK.eq((byte)0))
            .and(FRIEND_PROMOTE_ACTIVITY.DEL_FLAG.eq((byte)0))
            .and(FRIEND_PROMOTE_ACTIVITY.END_TIME.greaterThan(DateUtils.getSqlTimestamp()))
            .and(FRIEND_PROMOTE_ACTIVITY.ID.notEqual(id))
            .orderBy(FRIEND_PROMOTE_ACTIVITY.ID.desc())
            .limit(10)
            .fetchInto(PromoteActList.class);
        if (promoteActList==null){
            return null;
        }
        for (PromoteActList item:promoteActList){
            Integer receiveNum = db().select(DSL.count(FRIEND_PROMOTE_LAUNCH.ID))
                .from(FRIEND_PROMOTE_LAUNCH)
                .where(FRIEND_PROMOTE_LAUNCH.PROMOTE_STATUS.eq((byte)1).or(FRIEND_PROMOTE_LAUNCH.PROMOTE_STATUS.eq((byte)2)))
                .and(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(item.getId()))
                .fetchOneInto(Integer.class);
            //活动状态
            item.setActStatus(getActStatus(item.getActCode()));
            //活动奖励
            item.setFpRewardContent(Util.json2Object(item.getRewardContent(),FpRewardContent.class,false));
            //奖励内容
            if (item.getRewardType()==TWO){
                CouponInfo couponInfo = getCouponById(item.getFpRewardContent().getRewardIds());
                couponInfo.setMarketStore(item.getFpRewardContent().getMarketStore()>receiveNum?item.getFpRewardContent().getMarketStore()-receiveNum:0);
                item.setCouponInfo(couponInfo);
            }else{
                GoodsInfo goodsInfo = getGoodsInfo(item.getFpRewardContent().getGoodsIds());
                goodsInfo.setMarketPrice(item.getRewardType()==ONE?item.getFpRewardContent().getMarketPrice():BigDecimal.ZERO);
                goodsInfo.setMarketStore(item.getFpRewardContent().getMarketStore());
                logger().info("当前活动id："+item.getId());
                logger().info("其他活动商品信息："+goodsInfo);
                //设置库存
                goodsInfo.setMarketStore(goodsInfo.getGoodsStore()>goodsInfo.getMarketStore()?goodsInfo.getMarketStore():goodsInfo.getGoodsStore());
                goodsInfo.setMarketStore(goodsInfo.getMarketStore()>receiveNum?goodsInfo.getMarketStore()-receiveNum:0);
                item.setGoodsInfo(goodsInfo);
            }
        }
        return promoteActList;
    }

    /**
     * 小程序-发起好友助力
     * @param param 用户id 活动码
     * @return 发起信息
     */
    public LaunchVo friendPromoteLaunch(PromoteParam param){
        LaunchVo launchVo = new LaunchVo();
        PromoteInfo  promoteInfo = getPromoteInfo(param.getActCode());
        //最新一次的发起的好友助力
        FriendPromoteLaunchRecord launchInfo = getLaunchInfo(null,param.getUserId(),promoteInfo.getId());
        //助力进度：-1未发起，0助力中，1助力完成待领取，2助力完成已领取,3助力未领取失效，4助力未完成失败，5取消订单未领取
        promoteInfo.setPromoteStatus(launchInfo!=null?launchInfo.getPromoteStatus():-1);
        //是否可以再次发起好友助力
        CanLaunch canLaunch = canLaunch(promoteInfo,launchInfo,param.getUserId());
        promoteInfo.setCanLaunch(canLaunch.getCode());
        if (canLaunch.getCode().equals(NumberUtils.BYTE_ZERO)){
            //返回失败信息
            launchVo.setMsg(canLaunch.getMsg());
            return launchVo;
        }
        //发起入库
        Integer effectRows = promoteLaunch(param.getUserId(),promoteInfo.getId());
        if (effectRows==0){
            launchVo.setMsg(7);
            return launchVo;
        }
        Integer launchId = db().lastID().intValue();
        launchVo.setMsg(0);
        launchVo.setActCode(param.getActCode());
        launchVo.setLaunchUserId(param.getUserId());
        launchVo.setLaunchId(launchId);
        return launchVo;
    }

    /**
     * 发起助力活动入库
     * @param userId 用户id
     * @param promoteId 助力活动id
     * @return 受影响的行数
     */
    public Integer promoteLaunch(Integer userId,Integer promoteId){
        Integer effectRows = db().insertInto(FRIEND_PROMOTE_LAUNCH,FRIEND_PROMOTE_LAUNCH.USER_ID,FRIEND_PROMOTE_LAUNCH.PROMOTE_ID,FRIEND_PROMOTE_LAUNCH.LAUNCH_TIME)
            .values(userId,promoteId, DateUtils.getSqlTimestamp())
            .execute();
        return effectRows;
    }

    /**
     * 用户参与好友助力
     */
    public PromoteVo friendPromote(PromoteParam param){
        PromoteVo promoteVo = new PromoteVo();
        PromoteInfo promoteInfo = new PromoteInfo();
        //得到活动信息
        FriendPromoteActivityRecord record = getInfo(param.getActCode());
        //校验必需参数
        if(param.getLaunchId()==null||record==null){
            //返回参数错误
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        //设置活动id
        promoteInfo.setId(record.getId());
        //活动状态：0未开始，1进行中，2已结束
        promoteInfo.setActStatus(getActStatus(param.getActCode()));
        //需要被助力申请信息
        FriendPromoteLaunchRecord launchInfo = getLaunchInfo(param.getLaunchId(),null,null);
        if (launchInfo==null){
            //返回参数错误
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        Integer launchUserId =launchInfo.getUserId();
        //设置助力进度：-1未发起，0助力中，1助力完成待领取，2助力完成已领取，3助力未领取失效，4助力未完成失败
        promoteInfo.setPromoteStatus(launchInfo!=null?launchInfo.getPromoteStatus():-1);
        //设置已被助力总次数
        promoteInfo.setHasPromoteTimes(0);
        promoteInfo.setHasPromoteTimes(getHasPromoteTimes(launchInfo.getId(),null,null,null));
        //是否可以继续助力
        promoteInfo.setPromoteTimes(record.getPromoteTimes());
        promoteInfo.setPromoteTimesPerDay(record.getPromoteTimesPerDay());
        CanPromote canPromote = canPromote(promoteInfo,promoteInfo.getHasPromoteTimes(),param.getUserId(),param.getLaunchId());
        if (canPromote!=null&&canPromote.getCode()==0){
            //返回canPromote.getMsg()
            promoteVo.setCantPromote(canPromote.getMsg());
            return promoteVo;
        }
        //助力总值
        Integer hasPromoteValue = hasPromoteValue(param.getLaunchId());
        promoteInfo.setHasPromoteValue(hasPromoteValue);
        //获取每次助力值
        promoteInfo.setPromoteAmount(record.getPromoteAmount());
        promoteInfo.setPromoteTimes(record.getPromoteTimes());
        promoteInfo.setPromoteType(record.getPromoteType());
        Integer promoteValue = perPromoteValue(promoteInfo,promoteInfo.getHasPromoteTimes(),hasPromoteValue);
        //助力入库
        promoteInfo.setId(record.getId());
        promoteInfo.setRewardType(record.getRewardType());
        promoteInfo.setRewardContent(Util.json2Object(record.getRewardContent(),FpRewardContent.class,false));
        promoteInfo.setRewardDuration(record.getRewardDuration());
        promoteInfo.setRewardDurationUnit(record.getRewardDurationUnit());
        friendPromote(promoteInfo,launchUserId,param.getUserId(),param.getLaunchId(),promoteValue);
        //发送消息
        FriendPromoteSelectVo messageVo = new FriendPromoteSelectVo();
        messageVo.setId(launchInfo.getId());
        messageVo.setUserId(launchUserId);
        messageVo.setActCode(record.getActCode());
        messageVo.setActName(record.getActName());
        messageVo.setSuccessTime(DateUtils.getSqlTimestamp());
        messageVo.setRewardType(record.getRewardType());
        messageVo.setRewardContent(record.getRewardContent());
        String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
        //首位助力者发送消息通知
        if (promoteInfo.getHasPromoteTimes()==0){
            maMpScheduleTaskService.sendMessage((byte)1,messageVo,officeAppId);
        }
        //助力完成
        if (promoteInfo.getHasPromoteTimes()+1>=promoteInfo.getPromoteTimes()||promoteInfo.getHasPromoteValue()+promoteValue>=promoteInfo.getPromoteAmount().intValue()){
            maMpScheduleTaskService.sendMessage((byte)4,messageVo,officeAppId);
        }
        promoteInfo.setHasPromoteTimes(getHasPromoteTimes(launchInfo.getId(),null,null,null));
        CanPromote isCanPromote = canPromote(promoteInfo,promoteInfo.getHasPromoteTimes(),param.getUserId(),param.getLaunchId());
        Byte canShareTimes = canShareTimes(record.getShareCreateTimes(),param.getUserId(),param.getLaunchId());
        promoteInfo.setCanShare(canShareTimes>0?(byte)1:(byte)0);

        promoteVo.setPromoteValue(promoteValue);
        promoteVo.setCanPromote(isCanPromote==null?0:isCanPromote.getCode());
        promoteVo.setCanShare(promoteInfo.getCanShare());
        return promoteVo;
    }

    /**
     * 获取每次助力值
     * @param promoteInfo 助力信息
     * @param hasPromoteTimes 已助力次数
     * @param hasPromoteValue 已获得的助力值
     */
    public Integer perPromoteValue(PromoteInfo promoteInfo,Integer hasPromoteTimes,Integer hasPromoteValue){
        //最后一次助力时
        if ((promoteInfo.getPromoteTimes()-hasPromoteTimes)<=1){
            return promoteInfo.getPromoteAmount().intValue()-hasPromoteValue;
        }
        Double av = Math.ceil(promoteInfo.getPromoteAmount().doubleValue()/promoteInfo.getPromoteTimes().doubleValue());
        if (promoteInfo.getPromoteType()==1){
            Double d = av/10;
            Random random = new Random();
            Integer randResult = new Double(Math.ceil(av+(random.nextDouble()*(d*2)-d))).intValue();
            return randResult;
        }
        else {
            Integer avResult =av.intValue();
            return avResult;
        }
    }

    /**
     * 助力入库
     * @param promoteInfo 活动信息
     * @param userId 帮忙助力用户id
     * @param launchId 发起id
     * @param promoteValue 助力值
     * @return
     */
    public void friendPromote(PromoteInfo promoteInfo, Integer launchUserId,Integer userId, Integer launchId, Integer promoteValue){
        logger().info("开始助力，助力用户为："+userId);
        logger().info("发起活动为："+launchId);
        logger().info("助力值为："+promoteValue);
//        AtomicReference<Integer> rewardRecordId = new AtomicReference<>();
            this.transaction(()->{
                db().insertInto(FRIEND_PROMOTE_DETAIL,FRIEND_PROMOTE_DETAIL.LAUNCH_ID,FRIEND_PROMOTE_DETAIL.USER_ID,FRIEND_PROMOTE_DETAIL.PROMOTE_ID,FRIEND_PROMOTE_DETAIL.PROMOTE_VALUE)
                    .values(launchId,userId,promoteInfo.getId(),promoteValue)
                    .execute();
//                Integer lastId = db().lastID().intValue();
//                rewardRecordId.set(lastId);
                if (promoteInfo.getHasPromoteTimes()+1>=promoteInfo.getPromoteTimes()||promoteInfo.getHasPromoteValue()+promoteValue>=promoteInfo.getPromoteAmount().intValue()){
                    //助力状态
                    Byte promoteStatus;
                    //发优惠券
                    if (promoteInfo.getRewardType()==2){
                        CouponInfo couponInfo = getCouponById(promoteInfo.getRewardContent().getRewardIds());
                        CouponGiveQueueParam param = new CouponGiveQueueParam();
                        List<Integer> userList = new ArrayList<>();
                        userList.add(launchUserId);
                        logger().info("用户编号为："+launchUserId);
                        logger().info("用户集合为："+userList);
                        param.setUserIds(userList);
                        param.setCouponArray(new String[]{couponInfo.getCouponId().toString()});
                        param.setAccessMode((byte)0);
                        param.setGetSource((byte)16);
                        param.setActId(promoteInfo.getId());
                        param.setLimitNumType(BaseConstant.NO);
                        CouponGiveQueueBo couponGiveQueueBo = couponGiveService.handlerCouponGive(param);
                        if (couponGiveQueueBo.getSuccessSize()==0){
                            logger().info("优惠券发放失败");
                            throw new BusinessException(JsonResultCode.FRIEND_PROMOTE_FAIL);
                        }
                        promoteStatus=2;
                    }else {
                        promoteStatus=1;
                    }
                    //更新助力状态
                    Integer successRows = upPromoteInfo(promoteStatus,launchId);
                    if (successRows==null||successRows==0){
                        logger().info("助力状态更新失败");
                        throw new BusinessException(JsonResultCode.FRIEND_PROMOTE_FAIL);
                    }
                    //更新成功时间
                    updateSuccessTime(launchId);

                    //赠品入库
                    if(promoteInfo.getRewardType()==0||promoteInfo.getRewardType()==1){
                        //得到商品规格信息
                        GoodsSpecProductRecord prdRecord = getPrdInfo(promoteInfo.getRewardContent().getGoodsIds());
                        if (prdRecord!=null&&prdRecord.getPrdNumber()>0){
                            //计算奖励过期时间
                            Long durationSec = promoteDurationSec(promoteInfo.getRewardDurationUnit(),promoteInfo.getRewardDuration());
                            Long endSec = DateUtils.getLocalDateTime().getTime()+durationSec*1000;
                            Timestamp expiredTime = new Timestamp(endSec);
                            //奖励入库
                            PrizeRecordRecord  prizeRecordRecord = prizeRecordService.savePrize(launchUserId,promoteInfo.getId(),launchId,(byte)1,promoteInfo.getRewardContent().getGoodsIds(),null,expiredTime);
                            if (prizeRecordRecord==null){
                                logger().info("商品发放失败");
                                throw new BusinessException(JsonResultCode.FRIEND_PROMOTE_FAIL);
                            }
                            //得到goodsId
                            Integer goodsId = db().select(GOODS_SPEC_PRODUCT.GOODS_ID)
                                .from(GOODS_SPEC_PRODUCT)
                                .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(promoteInfo.getRewardContent().getGoodsIds()))
                                .fetchOneInto(Integer.class);
                            //更新库存
                            atomicOperation.updateStockAndSalesByLock(goodsId,promoteInfo.getRewardContent().getGoodsIds(),1,false);
                        }else {
                            logger().info("商品库存不足");
                            throw new BusinessException(JsonResultCode.FRIEND_PROMOTE_FAIL);
                        }
                    }
                }
//                return lastId;
            });
        //返回插入成功数据行的id
//        return rewardRecordId;
    }

    /**
     * 得当指定规格商品信息
     * @param prdId 规格id
     * @return 商品信息
     */
    public GoodsSpecProductRecord getPrdInfo(Integer prdId){
        GoodsSpecProductRecord record = db().select()
            .from(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
            .fetchOneInto(GoodsSpecProductRecord.class);
        return record;
    }

    /**
     * 发放成功减库存
     * @param prdId 规格id
     * @param prdNumber 库存改变量
     * @return 成功数量
     */
    public Integer updatePrdNum(Integer prdId,Integer prdNumber){
        GoodsSpecProductRecord prdRecord = getPrdInfo(prdId);
        if (prdRecord!=null&&prdRecord.getPrdNumber()>0){
            Integer newNumber = prdRecord.getPrdNumber()-prdNumber;
            return db().update(GOODS_SPEC_PRODUCT)
                .set(GOODS_SPEC_PRODUCT.PRD_NUMBER,newNumber)
                .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
                .execute();
        }
        return 0;
    }

    /**
     * 更新商品库存
     * @param prdId 规格id
     * @param prdNumber 变化数量
     * @return 成功条数
     */
    public Integer updateGoodsNum(Integer prdId,Integer prdNumber){
        GoodsRecord goodsRecord = db().select()
            .from(GOODS)
            .leftJoin(GOODS_SPEC_PRODUCT).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
            .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
            .fetchOneInto(GoodsRecord.class);
        if (goodsRecord!=null){
            Integer goodsNumber = goodsRecord.getGoodsNumber()-prdNumber;
            Integer goodsSaleNumber = goodsRecord.getGoodsSaleNum()+prdNumber;
            return db().update(GOODS)
                .set(GOODS.GOODS_NUMBER,goodsNumber)
                .set(GOODS.GOODS_SALE_NUM,goodsSaleNumber)
                .where(GOODS.GOODS_ID.eq(goodsRecord.getGoodsId()))
                .execute();
        }
        return 0;
    }

    /**
     * 用户分享获得助力次数
     * @param param 用户id 发起id
     * @return flag
     */
    public AddPromoteTimesVo addPromoteTimes(PromoteShareOrAuthParam param){
        PromoteParam promoteParam = new PromoteParam();
        promoteParam.setUserId(param.getUserId());
        promoteParam.setLaunchId(param.getLaunchId());
        AddPromoteTimesVo vo = new AddPromoteTimesVo();
        //分享可得助力次数
        Integer shareCreateTimes = addShareTimesInfo(param.getLaunchId());
        Byte canShareTimes = canShareTimes(shareCreateTimes.byteValue(),param.getUserId(),param.getLaunchId());
        if (canShareTimes<=0){
            // 分享获取助力次数已用完
            vo.setFlag(0);
            vo.setMsgCode(0);
            return vo;
        }
        Integer addNum = 1;
        Integer shareTimes = 1 ;
        if (param.getType()==1){
            shareTimes = 0;
        }


        Integer affectRows = addUserPromoteTimes(promoteParam,addNum,shareTimes,param.getType());
        if (affectRows == 0){
            vo.setFlag(0);
            return vo;
        }else {
            vo.setFlag(1);
            return vo;
        }
    }

    /**
     * 增加助力次数入库
     * @param param 用户id 发起id
     * @param addNum 增加次数
     * @param shareTimes 分享次数
     * @return 成功条数
     */
    public Integer addUserPromoteTimes(PromoteParam param,Integer addNum,Integer shareTimes,Integer type){
        //次数详情
        FriendPromoteTimesRecord promoteTimesInfo = promoteTimesInfo(param);
        Integer affectRows = 0;
        Byte isAuth = type == 0 ?(byte) 0:(byte)1;
        if (promoteTimesInfo!=null){
            shareTimes = shareTimes + promoteTimesInfo.getShareTimes();
            addNum = addNum + promoteTimesInfo.getOwnPromoteTimes();
            //分享
            if (isAuth==(byte)0){
                affectRows = db().update(FRIEND_PROMOTE_TIMES)
                    .set(FRIEND_PROMOTE_TIMES.SHARE_TIMES,shareTimes)
                    .set(FRIEND_PROMOTE_TIMES.OWN_PROMOTE_TIMES,addNum)
                    .where(FRIEND_PROMOTE_TIMES.USER_ID.eq(param.getUserId()))
                    .and(FRIEND_PROMOTE_TIMES.LAUNCH_ID.eq(param.getLaunchId()))
                    .execute();
            }
            //授权
            if (isAuth==(byte)1){
                affectRows = db().update(FRIEND_PROMOTE_TIMES)
                    .set(FRIEND_PROMOTE_TIMES.SHARE_TIMES,shareTimes)
                    .set(FRIEND_PROMOTE_TIMES.OWN_PROMOTE_TIMES,addNum)
                    .set(FRIEND_PROMOTE_TIMES.IS_AUTH,isAuth)
                    .where(FRIEND_PROMOTE_TIMES.USER_ID.eq(param.getUserId()))
                    .and(FRIEND_PROMOTE_TIMES.LAUNCH_ID.eq(param.getLaunchId()))
                    .execute();
            }
        }else {
            affectRows = db().insertInto(FRIEND_PROMOTE_TIMES,FRIEND_PROMOTE_TIMES.SHARE_TIMES,FRIEND_PROMOTE_TIMES.OWN_PROMOTE_TIMES,
                FRIEND_PROMOTE_TIMES.USER_ID,FRIEND_PROMOTE_TIMES.LAUNCH_ID,FRIEND_PROMOTE_TIMES.IS_AUTH)
                .values(shareTimes,addNum,param.getUserId(),param.getLaunchId(),isAuth)
                .execute();
        }
        return affectRows;
    }

    /**
     * 获得助力次数详情
     * @param param 用户id 发起id
     */
    public FriendPromoteTimesRecord promoteTimesInfo(PromoteParam param){
        FriendPromoteTimesRecord promoteTimesInfo = db().select()
            .from(FRIEND_PROMOTE_TIMES)
            .where(FRIEND_PROMOTE_TIMES.USER_ID.eq(param.getUserId()))
            .and(FRIEND_PROMOTE_TIMES.LAUNCH_ID.eq(param.getLaunchId()))
            .fetchOneInto(FriendPromoteTimesRecord.class);
        return promoteTimesInfo;
    }

    /**
     * 获取分享可获得助力次数
     * @param launchId 发起id
     * @return 分享可获得助力次数
     */
    public Integer addShareTimesInfo(Integer launchId){
        Integer shareCreateTimes = db().select(FRIEND_PROMOTE_ACTIVITY.SHARE_CREATE_TIMES)
            .from(FRIEND_PROMOTE_LAUNCH)
            .leftJoin(FRIEND_PROMOTE_ACTIVITY).on(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(FRIEND_PROMOTE_ACTIVITY.ID))
            .where(FRIEND_PROMOTE_LAUNCH.ID.eq(launchId))
            .fetchOptionalInto(Integer.class)
            .orElse(1);
        return shareCreateTimes;
    }

    /**
     * 好友助力明细
     * @param param 发起id
     * @return 助力用户详情
     */
    public List<PromoteDetail> friendPromoteDetailList(PromoteParam param){
        List<PromoteDetail> detailList = friendPromoteDetail(param.getLaunchId());
        return detailList;
    }

    /**
     * 更新助力成功时间
     * @param launchId 发起id
     */
    public void updateSuccessTime(Integer launchId){
        db().update(FRIEND_PROMOTE_LAUNCH)
            .set(FRIEND_PROMOTE_LAUNCH.SUCCESS_TIME, DateUtils.getSqlTimestamp())
            .where(FRIEND_PROMOTE_LAUNCH.ID.eq(launchId))
            .execute();
    }

    /**
     * 得到助力成功时间
     * @param launchId 发起id
     * @return 成功时间
     */
    public Timestamp getSuccessTime(Integer launchId){
        Timestamp successTime = db().select(FRIEND_PROMOTE_LAUNCH.SUCCESS_TIME)
            .from(FRIEND_PROMOTE_LAUNCH)
            .where(FRIEND_PROMOTE_LAUNCH.ID.eq(launchId))
            .fetchOneInto(Timestamp.class);
        return successTime;
    }

    /**
     * 得到奖品记录id
     * @param userId 用户id
     * @param actId 活动id
     * @param recordId 发起id
     * @return 记录id
     */
    public Integer getRewardRecordId(Integer userId,Integer actId,Integer recordId){
        Integer id = db().select(PRIZE_RECORD.ID)
            .from(PRIZE_RECORD)
            .where(PRIZE_RECORD.USER_ID.eq(userId))
            .and(PRIZE_RECORD.ACTIVITY_ID.eq(actId))
            .and(PRIZE_RECORD.RECORD_ID.eq(recordId))
            .and(PRIZE_RECORD.ACTIVITY_TYPE.eq((byte)1))
            .and(PRIZE_RECORD.EXPIRED_TIME.greaterThan(DateUtils.getSqlTimestamp()))
            .fetchOptionalInto(Integer.class)
            .orElse(0);
        return id;
    }

    public ActEffectDataVo getEffectData(FriendPromoteSelectParam param){
        Timestamp startTime;
        Timestamp endTime;
        if (param.getStartTime()!=null&&param.getEndTime()!=null){
            startTime = param.getStartTime();
            endTime = param.getEndTime();
        }
        else {
            //当前活动信息
            FriendPromoteActivityRecord record = db().select().from(FRIEND_PROMOTE_ACTIVITY)
                .where(FRIEND_PROMOTE_ACTIVITY.ID.eq(param.getId()))
                .fetchOneInto(FriendPromoteActivityRecord.class);
            //设置筛选起止时间
            Timestamp todayTime = Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_END, DateUtils.getLocalDateTime()));
            startTime = record.getStartTime();
            endTime = record.getEndTime().before(todayTime)?record.getEndTime():todayTime;
        }
        ActEffectDataVo vo = new ActEffectDataVo();
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        List<ActEffectData> dataList = new ArrayList<>();
        while (startTime.before(endTime)){
            Timestamp tempEnd = Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_END,startTime));
            Integer launch = getLaunchCount(param, startTime, tempEnd);
            Integer promote = getPromoteCount(param, startTime, tempEnd);
            Integer success = getSuccessCount(param, startTime, tempEnd);
            Integer newUser = getActivityNewUserCount(param, startTime, tempEnd);
            String date = startTime.toString().substring(0,10);
            ActEffectData actEffectData = new ActEffectData();
            actEffectData.setDate(date);
            actEffectData.setLaunch(launch);
            actEffectData.setPromote(promote);
            actEffectData.setSuccess(success);
            actEffectData.setNewUser(newUser);
            dataList.add(actEffectData);
            //时间+1
            Long longTime = startTime.getTime()+(long)1000*3600*24;
            startTime = new Timestamp(longTime);
        }

        //处理dataList
        processTotalNumber(vo, dataList);

        return vo;
    }

    private void processTotalNumber(ActEffectDataVo vo, List<ActEffectData> dataList) {
        Integer launchTotal = 0;
        Integer promoteTotal = 0;
        Integer successTotal = 0;
        Integer newUserTotal = 0;
        for (ActEffectData item:dataList){
            List<String> tempDate = new ArrayList<>();
            if (vo.getDate()!=null){
                tempDate=vo.getDate();
            }
            tempDate.add(item.getDate());
            vo.setDate(tempDate);

            List<Integer> tempLaunch = new ArrayList<>();
            if (vo.getLaunch()!=null){
                tempLaunch = vo.getLaunch();
            }
            tempLaunch.add(item.getLaunch());
            vo.setLaunch(tempLaunch);
            launchTotal += item.getLaunch();

            List<Integer> tempPromote = new ArrayList<>();
            if (vo.getPromote()!=null){
                tempPromote = vo.getPromote();
            }
            tempPromote.add(item.getPromote());
            vo.setPromote(tempPromote);
            promoteTotal += item.getPromote();

            List<Integer> tempSuccess = new ArrayList<>();
            if (vo.getSuccess()!=null){
                tempSuccess = vo.getSuccess();
            }
            tempSuccess.add(item.getSuccess());
            vo.setSuccess(tempSuccess);
            successTotal += item.getSuccess();

            List<Integer> tempNewUser = new ArrayList<>();
            if (vo.getNewUser()!=null){
                tempNewUser = vo.getNewUser();
            }
            tempNewUser.add(item.getNewUser());
            vo.setNewUser(tempNewUser);
            newUserTotal += item.getNewUser();
        }
        vo.setLaunchTotal(launchTotal);
        vo.setPromoteTotal(promoteTotal);
        vo.setSuccessTotal(successTotal);
        vo.setNewUserTotal(newUserTotal);
    }

    private Integer getActivityNewUserCount(FriendPromoteSelectParam param, Timestamp startTime, Timestamp tempEnd) {
        return db().select(DSL.count(USER.USER_ID).as("new_user"))
                    .from(USER)
                    .where(USER.INVITE_ACT_ID.eq(param.getId()))
                    .and(USER.INVITE_SOURCE.eq(MemberService.INVITE_SOURCE_PROMOTE))
                    .and(USER.CREATE_TIME.greaterOrEqual(startTime))
                    .and(USER.CREATE_TIME.lessOrEqual(tempEnd))
                    .fetchOptionalInto(Integer.class)
                    .orElse(0);
    }

    private Integer getSuccessCount(FriendPromoteSelectParam param, Timestamp startTime, Timestamp tempEnd) {
        return db().select(DSL.count(FRIEND_PROMOTE_LAUNCH.ID).as("success"))
                    .from(FRIEND_PROMOTE_LAUNCH)
                    .where(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(param.getId()))
                    .and(FRIEND_PROMOTE_LAUNCH.LAUNCH_TIME.greaterOrEqual(startTime))
                    .and(FRIEND_PROMOTE_LAUNCH.LAUNCH_TIME.lessOrEqual(tempEnd))
                    .and(FRIEND_PROMOTE_LAUNCH.PROMOTE_STATUS.eq((byte)1).or(FRIEND_PROMOTE_LAUNCH.PROMOTE_STATUS.eq((byte)2)))
                    .fetchOptionalInto(Integer.class)
                    .orElse(0);
    }

    private Integer getPromoteCount(FriendPromoteSelectParam param, Timestamp startTime, Timestamp tempEnd) {
        return db().select(DSL.count(FRIEND_PROMOTE_DETAIL.ID).as("promote"))
                    .from(FRIEND_PROMOTE_DETAIL)
                    .where(FRIEND_PROMOTE_DETAIL.PROMOTE_ID.eq(param.getId()))
                    .and(FRIEND_PROMOTE_DETAIL.CREATE_TIME.greaterOrEqual(startTime))
                    .and(FRIEND_PROMOTE_DETAIL.CREATE_TIME.lessOrEqual(tempEnd))
                    .fetchOptionalInto(Integer.class)
                    .orElse(0);
    }

    private Integer getLaunchCount(FriendPromoteSelectParam param, Timestamp startTime, Timestamp tempEnd) {
        return db().select(DSL.count(FRIEND_PROMOTE_LAUNCH.ID).as("launch"))
                    .from(FRIEND_PROMOTE_LAUNCH)
                    .where(FRIEND_PROMOTE_LAUNCH.PROMOTE_ID.eq(param.getId()))
                    .and(FRIEND_PROMOTE_LAUNCH.LAUNCH_TIME.greaterOrEqual(startTime))
                    .and(FRIEND_PROMOTE_LAUNCH.LAUNCH_TIME.lessOrEqual(tempEnd))
                    .fetchOptionalInto(Integer.class)
                    .orElse(0);
    }

    /**
     * 分享
     * @param param 活动id
     * @retuen 二维码信息
     */
    public ShareQrCodeVo getQrCode(FriendPromoteSelectParam param){
        String actCode = db().select(FRIEND_PROMOTE_ACTIVITY.ACT_CODE)
            .from(FRIEND_PROMOTE_ACTIVITY)
            .where(FRIEND_PROMOTE_ACTIVITY.ID.eq(param.getId()))
            .fetchOneInto(String.class);
        String pathParam = "actCode="+actCode;
        String imgUrl = qrCode.getMpQrCode(QrCodeTypeEnum.FRIEND_HELP_SHARE,pathParam);
        ShareQrCodeVo share = new ShareQrCodeVo();
        share.setImageUrl(imgUrl);
        share.setPagePath(QrCodeTypeEnum.FRIEND_HELP_SHARE.getPathUrl(pathParam));
        return share;
    }
    /**
     * 发起明细导出
     * @param param 查询信息
     * @param lang 语言
     */
    public Workbook launchExport(FriendPromoteLaunchParam param,String lang){
        List<FriendPromoteLaunchVo> vo = new ArrayList<>();
        PageResult<FriendPromoteLaunchVo> pageResult = launchDetail(param);
        for (FriendPromoteLaunchVo item : pageResult.getDataList()){
            FriendPromoteLaunchVo tempVo = new FriendPromoteLaunchVo();
            FieldsUtil.assignNotNull(item,tempVo);
            boolean success = item.getPromoteStatus() != null && (item.getPromoteStatus() == (byte) 1 || item.getPromoteStatus() == (byte) 2);
            if (success){
                tempVo.setIsSuccess("是");
            }else {
                tempVo.setIsSuccess("否");
            }
            vo.add(tempVo);
        }
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(vo,FriendPromoteLaunchVo.class);
        return workbook;
    }
    /**
     * 参与明细导出
     * @param param 查询信息
     * @param lang 语言
     */
    public Workbook joinExport(FriendPromoteParticipateParam param,String lang){
        List<FriendPromoteParticipateVo> vo = new ArrayList<>();
        PageResult<FriendPromoteParticipateVo> pageResult = participateDetail(param);
        for (FriendPromoteParticipateVo item : pageResult.getDataList()){
            FriendPromoteParticipateVo tempVo = new FriendPromoteParticipateVo();
            FieldsUtil.assignNotNull(item,tempVo);
            if (tempVo.getInviteSource()!=null&&tempVo.getInviteSource().equals(MemberService.INVITE_SOURCE_PROMOTE)){
                tempVo.setIsNew("是");
            }else {
                tempVo.setIsNew("否");
            }
            vo.add(tempVo);
        }
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(vo,FriendPromoteParticipateVo.class);
        return workbook;
    }
    /**
     * 参与明细导出
     * @param param 查询信息
     * @param lang 语言
     */
    public Workbook receiveExport(FriendPromoteReceiveParam param,String lang){
        List<FriendPromoteReceiveVo> vo = new ArrayList<>();
        PageResult<FriendPromoteReceiveVo> pageResult = receiveDetail(param);
        for (FriendPromoteReceiveVo item : pageResult.getDataList()){
            FriendPromoteReceiveVo tempVo = new FriendPromoteReceiveVo();
            FieldsUtil.assignNotNull(item,tempVo);
            if (tempVo.getPromoteStatus()!=null&&tempVo.getPromoteStatus()==(byte)2){
                tempVo.setIsReceive("是");
            }else {
                tempVo.setIsReceive("否");
            }
            vo.add(tempVo);
        }

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(vo,FriendPromoteReceiveVo.class);
        return workbook;
    }
    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(FRIEND_PROMOTE_ACTIVITY.ID, FRIEND_PROMOTE_ACTIVITY.ACT_NAME.as(CalendarAction.ACTNAME), FRIEND_PROMOTE_ACTIVITY.START_TIME,
				FRIEND_PROMOTE_ACTIVITY.END_TIME).from(FRIEND_PROMOTE_ACTIVITY).where(FRIEND_PROMOTE_ACTIVITY.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(FRIEND_PROMOTE_ACTIVITY.ID, FRIEND_PROMOTE_ACTIVITY.ACT_NAME.as(CalendarAction.ACTNAME), FRIEND_PROMOTE_ACTIVITY.START_TIME,
						FRIEND_PROMOTE_ACTIVITY.END_TIME)
				.from(FRIEND_PROMOTE_ACTIVITY)
				.where(FRIEND_PROMOTE_ACTIVITY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(FRIEND_PROMOTE_ACTIVITY.IS_BLOCK
						.eq(ZERO).and(FRIEND_PROMOTE_ACTIVITY.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(FRIEND_PROMOTE_ACTIVITY.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}

    /**
     * 小程序-根据actCode获得当前活动的活动说明
     * @param actCode actCode
     * @return {{@link PromoteActCopywriting}}
     */
	public PromoteActCopywriting getActCopywriting(String actCode){
	    String activityCopywriting = db().select(FRIEND_PROMOTE_ACTIVITY.ACTIVITY_COPYWRITING)
            .from(FRIEND_PROMOTE_ACTIVITY)
            .where(FRIEND_PROMOTE_ACTIVITY.ACT_CODE.eq(actCode))
            .fetchOptionalInto(String.class)
            .orElse(null);
	    PromoteActCopywriting actCopywriting = new PromoteActCopywriting();
	    if (activityCopywriting!=null){
	        actCopywriting = Util.json2Object(activityCopywriting, PromoteActCopywriting.class,false);
        }
	    return actCopywriting;
    }
}
