package com.meidianyi.shop.service.shop.market.integration;

import static com.meidianyi.shop.db.shop.tables.GroupIntegrationDefine.GROUP_INTEGRATION_DEFINE;
import static com.meidianyi.shop.db.shop.tables.GroupIntegrationList.GROUP_INTEGRATION_LIST;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DatePart;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.GroupIntegrationDefine;
import com.meidianyi.shop.db.shop.tables.GroupIntegrationList;
import com.meidianyi.shop.db.shop.tables.records.GroupIntegrationDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupIntegrationListRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupInteGetEndVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefineEnums;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefineEnums.IsGroupper;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationInfoPojo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListDetailParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListEnums;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListEnums.IsGrouper;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListEnums.IsNew;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListEnums.Status;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListParticipationVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListPojo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationMaVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationSuccessParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationSuccessVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupperInfoPojo;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;

/**
 * @author huangronggang
 * @date 2019年8月6日
 */
@Service
public class GroupIntegrationListService extends ShopBaseService {

	public static final byte TRADE_TYPE=10;
	public static final byte TRADE_FLOW=1;

	public static final byte ZERO=0;
	public static final String REMARK = "积分瓜分";

	@Autowired private GroupIntegrationCalculatorService calculatorHandler;

	/**
	 * 参团明细
	 * @param param
	 * @return
	 */
	public PageResult<GroupIntegrationListParticipationVo> getDetailPageList(GroupIntegrationListDetailParam param){
		SelectOnConditionStep<?> select = db().select(GROUP_INTEGRATION_LIST.ID,GROUP_INTEGRATION_LIST.USER_ID,USER.USERNAME,USER.MOBILE,GROUP_INTEGRATION_LIST.IS_NEW,GROUP_INTEGRATION_LIST.START_TIME,GROUP_INTEGRATION_LIST.GROUP_ID,GROUP_INTEGRATION_LIST.INVITE_NUM,GROUP_INTEGRATION_LIST.INTEGRATION,GROUP_INTEGRATION_LIST.IS_GROUPER,GROUP_INTEGRATION_LIST.CAN_INTEGRATION)
			.from(GROUP_INTEGRATION_LIST)
			.leftJoin(USER)
			.on(GROUP_INTEGRATION_LIST.USER_ID.eq(USER.USER_ID))
			.leftJoin(GROUP_INTEGRATION_DEFINE)
			.on(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(GROUP_INTEGRATION_DEFINE.ID));
		SelectConditionStep<?> step = buildOptionDetail(select,param);
		return getPageResult(step, param.getCurrentPage(), param.getPageRows(), GroupIntegrationListParticipationVo.class);

	}
	/**
	 * 成团明细
	 * @param param
	 * @return
	 */
	public PageResult<GroupIntegrationSuccessVo> getSuccessPageList(GroupIntegrationSuccessParam param){
		GroupIntegrationDefineRecord defineRecord =saas().getShopApp(getShopId()).groupIntegration.selectDefineById(param.getActId());
		if(null==defineRecord) {
			return null;
		}
		SelectJoinStep<?> selectFrom = db().select(GROUP_INTEGRATION_LIST.GROUP_ID,DSL.count(GROUP_INTEGRATION_LIST.ID).as("participantNum"),DSL.sum(GROUP_INTEGRATION_LIST.INTEGRATION).as("useIntegration"))
			.from(GROUP_INTEGRATION_LIST);
		SelectConditionStep<?> select = buildOptionSuccess(selectFrom,param);
		PageResult<GroupIntegrationSuccessVo> result = getPageResult(select,param.getCurrentPage(),param.getPageRows(), GroupIntegrationSuccessVo.class);
		List<GroupIntegrationSuccessVo> list = result.dataList;
		if(list == null ||list.isEmpty()) {
			return result;
		}
		for(GroupIntegrationSuccessVo vo :list) {
			GroupperInfoPojo grouperInfo = getGrouperInfo(param.getActId(),vo.getGroupId());
			if(grouperInfo == null) {
				continue;
			}
			vo.setGrouperName(grouperInfo.getUsername());
			vo.setMobile(grouperInfo.getMobile());
			vo.setStartTime(grouperInfo.getStartTime());
			vo.setEndTime(grouperInfo.getEndTime());
			vo.setActId(grouperInfo.getInteActivityId());
			vo.setStatus(grouperInfo.getStatus());
			vo.setLimitAmount(defineRecord.getLimitAmount());
		}
		return result;
	}

	/**
	 * 查某一个团长信息
	 * @param actId
	 * @param groupId
	 * @return
	 */
	public GroupperInfoPojo getGrouperInfo(Integer actId,Integer groupId) {
		GroupperInfoPojo pojo = db().select().from(GROUP_INTEGRATION_LIST)
			.leftJoin(USER).on(GROUP_INTEGRATION_LIST.USER_ID.eq(USER.USER_ID))
			.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(actId))
			.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
			.and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(IsGrouper.TRUE.value()))
			.fetchOneInto(GroupperInfoPojo.class);

		return pojo;
	}

	/**
	 * 获取某一个活动 所有团 的团长
	 * @param id
	 * @return
	 */
	public List<GroupIntegrationListRecord> getOnGoingGrouperInfo(Integer id) {
		List<GroupIntegrationListRecord> fetch = db().selectFrom(GROUP_INTEGRATION_LIST)
			.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(id))
			.and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(GroupIntegrationListEnums.IsGrouper.TRUE.value()))
			.and(GROUP_INTEGRATION_LIST.STATUS.eq(GroupIntegrationListEnums.Status.UNDERWAY.value()))
			.fetch();
		return fetch;
	}

	/**
	 * 记录拼团结果
	 * @param actId
	 * @param groupId
	 * @param status 取值见 GroupIntegrationListEnums.Status
	 * @return
	 */
	public int setIntegrationListResult(Integer actId,Integer groupId, Byte status) {
		return db().update(GROUP_INTEGRATION_LIST)
				.set(GROUP_INTEGRATION_LIST.END_TIME,Timestamp.valueOf(LocalDateTime.now()))
				.set(GROUP_INTEGRATION_LIST.STATUS,status)
				.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(actId))
				.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
				.execute();
	}
	/**
	 * 更新某一个团员参团获取的积分
	 * @param id 该团员对应记录的ID
	 * @param integration
	 * @return
	 */
	public int updateIntegration(Integer id,Integer integration) {
		return db().update(GROUP_INTEGRATION_LIST)
					.set(GROUP_INTEGRATION_LIST.INTEGRATION,integration)
					.where(GROUP_INTEGRATION_LIST.ID.eq(id))
					.execute();
	}
	/** 更新参加某一个活动的某一个团全部成员获取的积分*/
	public int batchUpdateIntegeration(Integer actId,Integer groupId,Integer integration) {
		return db().update(GROUP_INTEGRATION_LIST)
			.set(GROUP_INTEGRATION_LIST.INTEGRATION,integration)
			.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(actId)
			.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
			.and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(IsGroupper.NO.value())))
			.execute();
	}
	/**
	 * 更新团长获取的积分
	 * @param actId
	 * @param groupId
	 * @param integration
	 * @return
	 */
	public int updateGroupperIntegration(Integer actId,Integer groupId,Integer integration) {
		return db().update(GROUP_INTEGRATION_LIST)
				.set(GROUP_INTEGRATION_LIST.INTEGRATION,integration)
				.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(actId))
				.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
				.and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(IsGroupper.YES.value()))
				.execute();
	}
	/**
	 * 邀请的的人数
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public int getInviteNum(Integer groupId,Integer userId,Integer pinInteId) {
		return db().select(DSL.count())
			.from(GROUP_INTEGRATION_LIST)
			.where(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
			.and(GROUP_INTEGRATION_LIST.INVITE_USER.eq(userId))
			.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(pinInteId))
			.fetchOneInto(Integer.class);
	}
	/**
	 * 邀请的新用户人数
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public int getInviteNewNum(Integer groupId,Integer userId,Integer pinInteId) {
		return db().select(DSL.count())
			.from(GROUP_INTEGRATION_LIST)
			.where(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
			.and(GROUP_INTEGRATION_LIST.INVITE_USER.eq(userId))
			.and(GROUP_INTEGRATION_LIST.IS_NEW.eq(IsNew.YES.value()))
			.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(pinInteId))
			.fetchOneInto(Integer.class);
	}
	/**
	 * 获取参加某活动 某一个团的全部成员参与信息
	 * @param actId 拼团活动ID
	 * @param groupId 团ID
	 * @return
	 */
	public List<GroupIntegrationListParticipationVo> getGroupIntegrationListParticipation(Integer actId,Integer groupId){
		List<GroupIntegrationListParticipationVo> fetch = db().select(GROUP_INTEGRATION_LIST.ID,GROUP_INTEGRATION_LIST.USER_ID,USER.USERNAME,USER.MOBILE,GROUP_INTEGRATION_LIST.IS_NEW,GROUP_INTEGRATION_LIST.START_TIME,GROUP_INTEGRATION_LIST.GROUP_ID,GROUP_INTEGRATION_LIST.INVITE_NUM,GROUP_INTEGRATION_LIST.INTEGRATION,GROUP_INTEGRATION_LIST.IS_GROUPER,GROUP_INTEGRATION_LIST.CAN_INTEGRATION)
			.from(GROUP_INTEGRATION_LIST)
			.leftJoin(USER)
			.on(GROUP_INTEGRATION_LIST.USER_ID.eq(USER.USER_ID))
			.leftJoin(GROUP_INTEGRATION_DEFINE)
			.on(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(GROUP_INTEGRATION_DEFINE.ID))
			.where(GROUP_INTEGRATION_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
			.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))
			.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(actId))
			.orderBy(GROUP_INTEGRATION_LIST.INTEGRATION)
			.fetchInto(GroupIntegrationListParticipationVo.class);
		return fetch;
	}
	/**
	 * 给参加某活动的某一个团进行结算  废了
	 * @param groupId
	 * @param actId
	 * TODO 待指定线程池
	 */
	@Deprecated
	public void asyncSuccessGroupIntegration(Integer groupId,Integer actId) {
//		活动内容
		GroupIntegrationDefineRecord defineRecord = saas().getShopApp(getShopId()).groupIntegration.selectDefineById(actId);
//		参与情况
		List<GroupIntegrationListParticipationVo> listParticipation = getGroupIntegrationListParticipation(actId, groupId);
		if(listParticipation == null||listParticipation.size()==0) {
			return ;
		}
		if(isFail(defineRecord,listParticipation)) {
			this.setIntegrationListResult(actId, groupId,GroupIntegrationListEnums.Status.FAIL.value());
//			TODO 通知用户拼团失败
		}else {
			calculatorHandler.handle(defineRecord, listParticipation);
			this.setIntegrationListResult(actId, groupId, GroupIntegrationListEnums.Status.SUCCESS.value());
			if(defineRecord.getIsDayDivide().equals(GroupIntegrationDefineEnums.IsDayDivide.NO.value()) && defineRecord.getInteTotal() > 0) {
				saas().getShopApp(getShopId()).groupIntegration.refreshRemainInte(actId);
			}
			listParticipation = this.getGroupIntegrationListParticipation(actId, groupId);
			listParticipation.forEach(item->{
				ScoreParam scoreParam = new ScoreParam();
				scoreParam.setOrderSn("");
				scoreParam.setRemarkCode(RemarkTemplate.DIVIDE_SCORE.code);
				scoreParam.setScore(item.getIntegration());
				scoreParam.setScoreDis(0);
				//scoreService.updateMemberScore(scoreParam, null, item.getUserId(),TRADE_TYPE, TRADE_FLOW);
			});
//			TODO 通知用户拼团成功
		}
		defineRecord = saas().getShopApp(getShopId()).groupIntegration.selectDefineById(actId);
		if(!canContinue(defineRecord)) {
			defineRecord.setIsContinue(GroupIntegrationDefineEnums.IsContinue.FALSE.value());
			saas().getShopApp(getShopId()).groupIntegration.updateDefine(defineRecord);
		}


	}

	/**
	 * 拼团活动是否能继续开团
	 * @param defineRecord
	 * @return
	 */
	private boolean canContinue(GroupIntegrationDefineRecord defineRecord) {
		boolean canDayDivide = defineRecord.getIsDayDivide().equals(GroupIntegrationDefineEnums.IsDayDivide.YES.value());
		int remainInte = defineRecord.getInteRemain();
		int groupInte = defineRecord.getInteGroup();
		int totalInte = defineRecord.getInteTotal();
		if(canDayDivide&&remainInte == 0 && totalInte >0 ) {
			return false;
		}
		if(!canDayDivide && remainInte<groupInte && totalInte >0) {
			return false;
		}
		return true;
	}

	/**
	 *组团失败条件，积分不足时失败，
	 *用户开团24小时后,拼团未满员不可以瓜分积分，且用户数量不满足成团条件 返回失败
	 *失败 返回false
	 * @param defineRecord
	 * @param listParticipation
	 * @return
	 */
	private boolean isFail(GroupIntegrationDefineRecord defineRecord,
			List<GroupIntegrationListParticipationVo> listParticipation) {
//		剩余积分
		Integer canIntegration = listParticipation.get(0).getCanIntegration();
//		积分不足时失败
		if(canIntegration == 0) {
			return true;
		}
		int userNum = listParticipation.size();
//		用户开团24小时后,拼团未满员不可以瓜分积分，且用户数量不满足成团条件 返回失败
		if(GroupIntegrationDefineEnums.IsDayDivide.NO.value().equals(defineRecord.getIsDayDivide())&&defineRecord.getLimitAmount()>userNum) {
			return true;
		}
		return false;
	}

	/**
	 * @param select
	 * @param param
	 * @return
	 */
	private SelectConditionStep<?> buildOptionDetail(SelectOnConditionStep<?> select, GroupIntegrationListDetailParam param) {
		SelectConditionStep<?> step = select.where(GROUP_INTEGRATION_DEFINE.ID.eq(param.getActId()))
			.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(param.getActId()));
		if(!StringUtils.isBlank(param.getMobile())) {
			step.and(USER.MOBILE.like(this.likeValue(param.getMobile())));
		}
		if(!StringUtils.isBlank(param.getUsername())) {
			step.and(USER.USERNAME.like(this.likeValue(param.getUsername())));
		}
		if(param.getStartTime() != null) {
			step.and(GROUP_INTEGRATION_LIST.START_TIME.ge(param.getStartTime()));
		}
		if(param.getEndTime() != null) {
			step.and(GROUP_INTEGRATION_LIST.START_TIME.le(param.getEndTime()));
		}
		if(param.getIsGrouper() != null) {
			step.and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(param.getIsGrouper()));
		}
		if(param.getInviteNum() != null) {
			step.and(GROUP_INTEGRATION_LIST.INVITE_NUM.eq(param.getInviteNum()));
		}
		if(param.getMinIntegration() != null) {
			step.and(GROUP_INTEGRATION_LIST.INTEGRATION.ge(param.getMinIntegration()));
		}
		if(param.getMaxIntegration() != null) {
			step.and(GROUP_INTEGRATION_LIST.INTEGRATION.le(param.getMaxIntegration()));
		}
		if(param.getIsNew() != null) {
			step.and(GROUP_INTEGRATION_LIST.IS_NEW.eq(param.getIsNew()));
		}
		if(param.getGroupId() != null) {
			step.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(param.getGroupId()));
		}
		return step;

	}


	/**
	 * @param selectFrom
	 * @param param
	 * @return
	 */
	private SelectConditionStep<?> buildOptionSuccess(SelectJoinStep<?> selectFrom, GroupIntegrationSuccessParam param) {
		SelectConditionStep<?> select = selectFrom.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(param.getActId()));
		//select.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(param.getActId()));
		select.groupBy(GROUP_INTEGRATION_LIST.GROUP_ID);
		if(param.getGroupId() != null) {
			select.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(param.getGroupId()));
		}
		if(param.getStatus() != null) {
			select.and(GROUP_INTEGRATION_LIST.STATUS.eq(param.getStatus()));
		}
		if(param.getStartTime() != null) {
			select.and(GROUP_INTEGRATION_LIST.END_TIME.ge(param.getStartTime()));
		}
		if(param.getEndTime() != null) {
			select.and(GROUP_INTEGRATION_LIST.END_TIME.le(param.getEndTime()));
		}
		return select;
	}


	/**
	 * 获取参团团数
	 * @param pinInteId
	 * @param userId
	 * @return
	 */
	public int getJoinNum(Integer pinInteId,Integer userId) {
		Result<GroupIntegrationListRecord> fetch = db().selectFrom(GROUP_INTEGRATION_LIST).where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(pinInteId)
				.and(GROUP_INTEGRATION_LIST.USER_ID.eq(userId)).and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(ZERO))).fetch();
		return fetch.size();

	}

    /**
     * 正在拼团中的、是团长的groupId
     * @param userId
     * @param actId
     * @return
     */
	public int getExistGroup(int userId,int actId){
		 Integer groupId = db().select(GROUP_INTEGRATION_LIST.GROUP_ID)
				.from(GROUP_INTEGRATION_LIST.leftJoin(GROUP_INTEGRATION_DEFINE)
						.on(GROUP_INTEGRATION_DEFINE.ID.eq(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID)))
				.where(GROUP_INTEGRATION_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(GROUP_INTEGRATION_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
						.and(GROUP_INTEGRATION_LIST.STATUS.eq(Status.UNDERWAY.value()))
						.and(GROUP_INTEGRATION_LIST.IS_GROUPER.eq(IsGrouper.TRUE.value()))
						.and(GROUP_INTEGRATION_LIST.USER_ID.eq(userId))
						.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(actId)))
				.fetchOneInto(Integer.class);
		return groupId==null?0:groupId;
    }

	public List<GroupIntegrationMaVo> getPinIntegrationGroupDetail(Integer id, Integer groupId) {
		List<GroupIntegrationMaVo> fetchInto = db().select(GROUP_INTEGRATION_LIST.fields())
				.select(USER.USERNAME, USER.MOBILE, USER_DETAIL.USER_AVATAR, GROUP_INTEGRATION_DEFINE.DEL_FLAG,
						GROUP_INTEGRATION_DEFINE.LIMIT_AMOUNT, GROUP_INTEGRATION_DEFINE.END_TIME.as("groupEndTime"),
						GROUP_INTEGRATION_DEFINE.INTE_GROUP, GROUP_INTEGRATION_DEFINE.DIVIDE_TYPE)
				.from(GROUP_INTEGRATION_LIST).leftJoin(USER).on(GROUP_INTEGRATION_LIST.USER_ID.eq(USER.USER_ID))
				.leftJoin(USER_DETAIL).on(GROUP_INTEGRATION_LIST.USER_ID.eq(USER_DETAIL.USER_ID))
				.leftJoin(GROUP_INTEGRATION_DEFINE)
				.on(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(GROUP_INTEGRATION_DEFINE.ID))
				.where(GROUP_INTEGRATION_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(id))
						.and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId)))
				.orderBy(GROUP_INTEGRATION_LIST.INTEGRATION.desc()).fetchInto(GroupIntegrationMaVo.class);
		return fetchInto;
	}

	public UserRecord getinviteUser(Integer pinInteId, Integer userId) {
		return db().selectFrom(USER).where(USER.USER_ID.eq(userId).and(USER.INVITE_SOURCE.eq("pin_integration"))
				.and(USER.INVITE_ACT_ID.eq(pinInteId))).fetchAny();
	}


	/**
	 * 是否参加过组团瓜分积分活动  true：参加过 ；false：没参加
	 * @param userId
	 * @return
	 */
	public boolean haveJoinGroup(Integer userId) {
		GroupIntegrationListRecord fetchAny = db().selectFrom(GROUP_INTEGRATION_LIST).where(GROUP_INTEGRATION_LIST.USER_ID.eq(userId)).fetchAny();
		if(fetchAny!=null) {
			return true;
		}
		return false;
	}

	/**
	 * 新增拼团详情
	 * @param groupId
	 * @param userId
	 * @param pinInteId
	 * @param isGrouper
	 * @param isNew
	 * @param inviteUser
	 * @return
	 */
	public int addPinGroup(Integer groupId, Integer userId,Integer pinInteId,Byte isGrouper,Byte isNew,Integer inviteUser) {
		GroupIntegrationListRecord newRecord = db().newRecord(GROUP_INTEGRATION_LIST);
		newRecord.setInteActivityId(pinInteId);
		newRecord.setGroupId(groupId);
		newRecord.setUserId(userId);
		newRecord.setIsGrouper(isGrouper);
		newRecord.setStatus(ZERO);
		newRecord.setIntegration(0);
		newRecord.setStartTime(DateUtils.getLocalDateTime());
		newRecord.setIsNew(isNew);
		newRecord.setInviteUser(inviteUser == null ? 0 : inviteUser);
		int insert = newRecord.insert();
		logger().info("新增拼团详情{}",insert);
		return insert;
	}

	public GroupIntegrationListRecord getUserIntegrationInfo(Integer userId, Integer pinInteId, Integer groupId) {
		return db().selectFrom(GROUP_INTEGRATION_LIST).where(GROUP_INTEGRATION_LIST.USER_ID.eq(userId).and(
				GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(pinInteId).and(GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId))))
				.fetchAny();
	}


	public List<GroupIntegrationInfoPojo> getPinGroupByUser(Integer userId, Timestamp time) {
		return db().select(GROUP_INTEGRATION_LIST.fields())
				.select(GROUP_INTEGRATION_DEFINE.ID.as("pinInteId"), GROUP_INTEGRATION_DEFINE.NAME,
						GROUP_INTEGRATION_DEFINE.LIMIT_AMOUNT, GROUP_INTEGRATION_DEFINE.DIVIDE_TYPE)
				.from(GROUP_INTEGRATION_LIST, GROUP_INTEGRATION_DEFINE)
				.where(GROUP_INTEGRATION_LIST.USER_ID.eq(userId)
						.and(GROUP_INTEGRATION_DEFINE.ID.eq(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID))
						.and(DSL.timestampAdd(GROUP_INTEGRATION_LIST.START_TIME, 5, DatePart.DAY).ge(time)))
				.orderBy(GROUP_INTEGRATION_LIST.START_TIME.desc()).fetchInto(GroupIntegrationInfoPojo.class);
	}

	public List<GroupIntegrationListPojo> getGroupInfo(Integer pinInteId, Integer groupId) {
		return db().selectFrom(GROUP_INTEGRATION_LIST).where(
				GROUP_INTEGRATION_LIST.GROUP_ID.eq(groupId).and(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(pinInteId)))
				.fetchInto(GroupIntegrationListPojo.class);
	}

	/**
	 * 开团
	 * @param userId
	 * @param pinInteId
	 * @return
	 */
	public int startNewGroup(Integer userId, Integer pinInteId) {
		List<Integer> existGroups = db().select(GROUP_INTEGRATION_LIST.GROUP_ID).from(GROUP_INTEGRATION_LIST)
				.where(GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID.eq(pinInteId)).groupBy(GROUP_INTEGRATION_LIST.GROUP_ID)
				.fetchInto(Integer.class);
		int groupId = 0;
		if (existGroups.size() > 0) {
			groupId = existGroups.size()+1;
		} else {
			groupId = 1;
		}
		int addPinGroup = addPinGroup(groupId, userId, pinInteId, IsGrouper.TRUE.value(), IsNew.NO.value(), null);
		logger().info("groupId:{};开团结果：{}",groupId,addPinGroup);
		if(addPinGroup==0) {
			return 0;
		}
		return groupId;
	}

	/**
	 * 得已经结束的拼团
	 *
	 * @param dateTime
	 * @return
	 */
	public List<GroupInteGetEndVo> getAlreadyEndPinGroup(Timestamp dateTime) {

		GroupIntegrationDefine p = GROUP_INTEGRATION_DEFINE.as("p");
		GroupIntegrationList g = GROUP_INTEGRATION_LIST.as("g");
		List<GroupInteGetEndVo> fetchInto = db()
				.select(p.ID, p.NAME, p.LIMIT_AMOUNT, p.DIVIDE_TYPE, g.GROUP_ID, g.CAN_INTEGRATION, g.START_TIME)
				.from(g).leftJoin(p).on(p.ID.eq(g.INTE_ACTIVITY_ID))
				.where(g.STATUS.eq(GroupIntegrationListEnums.Status.UNDERWAY.value()).and(g.IS_GROUPER
						.eq(IsGrouper.TRUE.value())
						.and((DSL.timestampAdd(g.START_TIME, 1, DatePart.DAY).lt(dateTime))
								.or(p.END_TIME.lt(dateTime).or(p.IS_CONTINUE.eq(ZERO))))
						.and(p.STATUS.eq(GroupIntegrationListEnums.Status.SUCCESS.value()).and(p.DEL_FLAG.eq(ZERO)))))
				.fetchInto(GroupInteGetEndVo.class);
		return fetchInto;
	}

}

