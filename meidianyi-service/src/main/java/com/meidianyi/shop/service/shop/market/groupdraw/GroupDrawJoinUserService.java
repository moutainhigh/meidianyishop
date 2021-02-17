package com.meidianyi.shop.service.shop.market.groupdraw;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.invite.InvitedUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.invite.InvitedUserListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.join.JoinUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.join.JoinUserListVo;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.GroupDrawInvite.GROUP_DRAW_INVITE;
import static com.meidianyi.shop.db.shop.tables.JoinDrawList.JOIN_DRAW_LIST;
import static com.meidianyi.shop.db.shop.tables.JoinGroupList.JOIN_GROUP_LIST;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 拼团抽奖 - 参与用户、新用户
 *
 * @author 郑保乐
 */
@Service
public class GroupDrawJoinUserService extends ShopBaseService {
	@Autowired
	private GroupDrawService groupDrawService;

	/** 查询别名 **/
	private static final byte ZERO = 0;
	private static final byte ONE = 1;
	private static final byte TWO = 2;
	User a = USER.as("a");
	User b = USER.as("b");

	/**
	 * 新用户列表
	 */
	public PageResult<InvitedUserListVo> getInvitedUserList(InvitedUserListParam param) {
		SelectOnConditionStep<Record6<Integer, String, String, Timestamp, Integer, String>> select = db()
				.select(a.USER_ID, a.USERNAME.as("userName"), a.MOBILE, a.CREATE_TIME, a.INVITE_ID, b.USERNAME.as("inviteUserName"))
				.from(a).leftJoin(b).on(a.INVITE_ID.eq(b.USER_ID));
		select.where(a.INVITE_SOURCE.eq("group_draw").and(a.INVITE_ACT_ID.eq(param.getGroupDrawId())));
		inviteOption(select, param);
		select.orderBy(a.CREATE_TIME.desc());
		return getPageResult(select, param, InvitedUserListVo.class);
	}

	public void inviteOption(SelectOnConditionStep<Record6<Integer, String, String, Timestamp, Integer, String>> select,InvitedUserListParam param) {
		if(StringUtils.isNotBlank(param.getMobile())) {
			select.and(a.MOBILE.like(likeValue(param.getMobile())));
		}
		if(StringUtils.isNotBlank(param.getNickName())) {
			select.and(a.USERNAME.like(likeValue(param.getNickName())));
		}
		if(StringUtils.isNotBlank(param.getInviteUserNickname())) {
			select.and(b.USERNAME.like(likeValue(param.getInviteUserNickname())));
		}
	}


	/**
	 * 参与用户列表
	 */
	public PageResult<JoinUserListVo> getJoinUserList(JoinUserListParam param) {

		SelectConditionStep<Record> select = db().select(JOIN_GROUP_LIST.fields())
				.select(USER.USERNAME.as("userName"), USER.MOBILE).from(JOIN_GROUP_LIST).leftJoin(USER)
				.on(JOIN_GROUP_LIST.USER_ID.eq(USER.USER_ID))
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(param.getGroupDrawId()));
		select = buildOptions(select, param);
		select.orderBy(JOIN_GROUP_LIST.OPEN_TIME.desc());
		PageResult<JoinUserListVo> pageResult = getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				JoinUserListVo.class);
		List<JoinUserListVo> dataList = pageResult.getDataList();
		for (JoinUserListVo vo : dataList) {
			vo.setDrawNum(groupDrawService.getDrawNum(vo.getGroupDrawId(), vo.getUserId(), vo.getGoodsId()));
			vo.setInviteNum(groupDrawService.getInviteNum(vo.getGroupDrawId(), vo.getUserId(), vo.getGoodsId()));
		}
		return pageResult;
	}

	private SelectConditionStep<Record> buildOptions(SelectConditionStep<Record> select, JoinUserListParam param) {
		if (!StringUtils.isEmpty(param.getNickName())) {
			select.and(USER.USERNAME.like(likeValue(param.getNickName())));
		}
		if (null != param.getStartTime()) {
			select.and(JOIN_GROUP_LIST.OPEN_TIME.ge(param.getStartTime()));
		}
		if (null != param.getEndTime()) {
			select.and(JOIN_GROUP_LIST.OPEN_TIME.le(param.getEndTime()));
		}
		if (!StringUtils.isEmpty(param.getMobile())) {
			select.and(USER.MOBILE.like(likeValue(param.getMobile())));
		}
		if (null != param.getGrouped()) {
			if (param.getGrouped()) {
				select.and(JOIN_GROUP_LIST.STATUS.eq(ONE));
			} else {
				select.and(JOIN_GROUP_LIST.STATUS.eq(TWO)).or(JOIN_GROUP_LIST.STATUS.eq(ZERO));
			}
		}
		if (null != param.getGroupId()) {
			select.and(JOIN_GROUP_LIST.GROUP_ID.eq(param.getGroupId()));
		}
		if (null != param.getMinInviteUserCount()) {
			select.and(JOIN_GROUP_LIST.INVITE_USER_NUM.ge(param.getMinInviteUserCount()));
		}
		if (null != param.getMaxInviteUserCount()) {
			select.and(JOIN_GROUP_LIST.INVITE_USER_NUM.le(param.getMaxInviteUserCount()));
		}
		if (null != param.getIsGrouper()) {
			select.and(JOIN_GROUP_LIST.IS_GROUPER.eq(param.getIsGrouper() ? ONE : ZERO));
		}
		return select;
	}

	/**
	 * 获得该活动的参团人数
	 * 
	 * @param actId
	 * @return
	 */
	public int getJoinGroupNumByGroupDraw(int actId) {
		return db().selectCount().from(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(actId))
				.and(JOIN_GROUP_LIST.STATUS.ge((byte) 0)).fetchOptionalInto(Integer.class).orElse(0);
	}
}
