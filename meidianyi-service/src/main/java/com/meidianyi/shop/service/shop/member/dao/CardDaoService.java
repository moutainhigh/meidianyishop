package com.meidianyi.shop.service.shop.member.dao;

import static com.meidianyi.shop.db.shop.Tables.CARD_BATCH;
import static com.meidianyi.shop.db.shop.Tables.CARD_CONSUMER;
import static com.meidianyi.shop.db.shop.Tables.CARD_EXAMINE;
import static com.meidianyi.shop.db.shop.Tables.CARD_RECEIVE_CODE;
import static com.meidianyi.shop.db.shop.Tables.CHARGE_MONEY;
import static com.meidianyi.shop.db.shop.Tables.GIVE_CARD_RECORD;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.ALL_BATCH;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.COUNT_TYPE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.EXCHANG_COUNT_TYPE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_DF_NO;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_DF_YES;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_FLAG_USING;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_TP_GRADE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.SHORT_ZERO;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.UCARD_FG_EXPIRED;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.UCARD_FG_STOP;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.UCARD_FG_USING;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.service.foundation.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.MemberCard;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.UserCard;
import com.meidianyi.shop.db.shop.tables.records.CardBatchRecord;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;
import com.meidianyi.shop.db.shop.tables.records.CardReceiveCodeRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBasicVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBatchDetailVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBatchParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumeVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardHolderExcelVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardHolderParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardHolderVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardNoImportTemplate;
import com.meidianyi.shop.service.pojo.shop.member.card.CardVerifyConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CodeReceiveParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CodeReceiveVo;
import com.meidianyi.shop.service.pojo.shop.member.card.SearchCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.dao.CardFullDetail;
import com.meidianyi.shop.service.pojo.shop.member.card.export.receive.CardReceiveDownVo;

/**
 * @author 黄壮壮
 * @Date: 2019年9月24日
 * @Description: 会员卡dao访问
 */
@Service
public class CardDaoService extends ShopBaseService {

	public PageResult<CardHolderVo> getAllCardHolder(CardHolderParam param) {

		User invitedUser = USER.as("a");
		User giveCardUser = USER.as("b");
		List<Field<?>> f = new ArrayList<>(Arrays.asList(USER_CARD.fields()));
		f.add(USER.USERNAME);
		f.add(USER.MOBILE);
		f.add(invitedUser.USERNAME.as("invitedName"));
		f.add(MEMBER_CARD.CARD_TYPE);
		f.add(CARD_EXAMINE.STATUS);
		f.add(GIVE_CARD_RECORD.GET_TIME);
		f.add(giveCardUser.USERNAME.as("giveName"));
		f.add(GIVE_CARD_RECORD.GET_USER_ID);
		Field<?>[] myFields = f.toArray(new Field<?>[0]);
		SelectJoinStep<?> select = db()
				.select(myFields)
				.from(USER_CARD.leftJoin(USER.leftJoin(invitedUser).on(USER.INVITE_ID.eq(invitedUser.USER_ID))
										).on(USER_CARD.USER_ID.eq(USER.USER_ID)))
				.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID))
				.leftJoin(GIVE_CARD_RECORD.leftJoin(giveCardUser).on(GIVE_CARD_RECORD.GET_USER_ID.eq(giveCardUser.USER_ID)))
					.on(USER_CARD.CARD_NO.eq(GIVE_CARD_RECORD.CARD_NO).and(USER_CARD.FLAG.eq(CardConstant.UCARD_FG_GIVED)))
				.leftJoin(CARD_EXAMINE).on(USER_CARD.CARD_NO.eq(CARD_EXAMINE.CARD_NO))
				.leftJoin(CARD_CONSUMER).on(USER_CARD.CARD_NO.eq(CARD_CONSUMER.CARD_NO))
				.leftJoin(CHARGE_MONEY).on(USER_CARD.CARD_NO.eq(CHARGE_MONEY.CARD_NO));


		buildOptions(param, select);
		select.where(USER_CARD.CARD_ID.eq(param.getCardId()))
			  .groupBy(myFields)
			  .orderBy(USER_CARD.CREATE_TIME.desc());
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(), CardHolderVo.class);
	}

	/**
	 * 构建查询参数
	 *
	 * @param param
	 * @param select
	 */
	private void buildOptions(CardHolderParam param, SelectJoinStep<?> select) {
        /* - 会员id */
		if (param.getUserId() != null) {
			select.where(USER_CARD.USER_ID.eq(param.getUserId()));
		}
        /* - 昵称 */
		if (!StringUtils.isBlank(param.getUsername())) {
			String likeValue = likeValue(param.getUsername().trim());
			select.where(USER.USERNAME.like(likeValue));
		}
		/** -手机号 */
		if (!StringUtils.isBlank(param.getMobile())) {
			String likeValue = likeValue(param.getMobile().trim());
			select.where(USER.MOBILE.like(likeValue));
		}
		/** - 会员卡号 */
		if (!StringUtils.isBlank(param.getCardNo())) {
			String likeValue = likeValue(param.getCardNo().trim());
			select.where(USER_CARD.CARD_NO.like(likeValue));
		}
		/** - 卡状态 */
        buildCardFlagOption(param, select);


        /** - 领卡时间 开始范围 */
		if (param.getFirstDateTime() != null) {
			select.where(USER_CARD.CREATE_TIME.ge(param.getFirstDateTime()));
		}

		/** - 领卡时间 结束范围 */
		if (param.getSecondDateTime() != null) {
			select.where(USER_CARD.CREATE_TIME.le(param.getSecondDateTime()));
		}

		/**	是否提交审核申请	*/
		if(param.getSubmitValue()!=null) {
			if(CardVerifyConstant.HAS_CONDITION.equals(param.getSubmitValue())) {
				select.where(CARD_EXAMINE.CARD_NO.isNotNull());
			}else if(CardVerifyConstant.NO_CONDITION.equals(param.getSubmitValue())) {
				select.where(CARD_EXAMINE.CARD_NO.isNull());
			}
		}

		/**	 卡审核状态	*/
		if(param.getExamineStatusValue()!=null) {
			if(CardVerifyConstant.VSTAT_CHECKING.equals(param.getExamineStatusValue())) {
				select.where(CARD_EXAMINE.STATUS.eq(CardVerifyConstant.VSTAT_CHECKING));
			}else if(CardVerifyConstant.VSTAT_PASS.equals(param.getExamineStatusValue())){
				select.where(CARD_EXAMINE.STATUS.eq(CardVerifyConstant.VSTAT_PASS));
			}else if(CardVerifyConstant.VSTAT_REFUSED.equals(param.getExamineStatusValue())) {
				select.where(CARD_EXAMINE.STATUS.eq(CardVerifyConstant.VSTAT_REFUSED));
			}
		}

		/**	 有无消费记录	*/
		if(param.getConsumeRecordValue()!=null) {
			if(CardVerifyConstant.HAS_CONDITION.equals(param.getConsumeRecordValue())) {
				select.where(CARD_CONSUMER.CARD_NO.isNotNull());
			}else if(CardVerifyConstant.NO_CONDITION.equals(param.getConsumeRecordValue())){
				select.where(CARD_CONSUMER.CARD_NO.isNull());
			}
		}

		/**	有无充值记录 */
		if(param.getChargeRecordValue()!=null) {
			if(CardVerifyConstant.HAS_CONDITION.equals(param.getChargeRecordValue())) {
				select.where(CHARGE_MONEY.CARD_NO.isNotNull());
			}else if(CardVerifyConstant.NO_CONDITION.equals(param.getChargeRecordValue())){
				select.where(CHARGE_MONEY.CARD_NO.isNull());
			}
		}

	}

    /**
     * 卡状态选项
     *
     * @param param
     * @param select
     */
    private void buildCardFlagOption(CardHolderParam param, SelectJoinStep<?> select) {
        if (param.getFlag() != null && !NumberUtils.BYTE_MINUS_ONE.equals(param.getFlag())) {
            /** - 状态为过期 */
            Condition condition = DSL.noCondition();
            if (param.getFlag().equals(UCARD_FG_EXPIRED)) {

                condition = condition.and(USER_CARD.EXPIRE_TIME.le(DateUtils.getLocalDateTime()));
                select.where(condition);
            } else if (param.getFlag().equals(UCARD_FG_USING)) {
                condition = condition.and(USER_CARD.EXPIRE_TIME.ge(DateUtils.getLocalDateTime()).or(USER_CARD.EXPIRE_TIME.isNull()))
                                    .and(USER_CARD.FLAG.eq(param.getFlag()));
                select.where(condition);
            }else if(param.getFlag().equals(UCARD_FG_STOP)) {
                condition = condition.and(USER_CARD.FLAG.eq(param.getFlag()));
                select.where(condition);
            }else {
                //	转赠中或已转赠
                select.where(USER_CARD.FLAG.eq(param.getFlag()));
            }
        }
    }

    /**
	 * 分页查询会员卡领取详情
	 */
	public PageResult<CodeReceiveVo> getReceiveListSql(CodeReceiveParam param) {
		// CARD_RECEIVE_CODE
		SelectConditionStep<?> select = buildSelect(param);
		return this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), CodeReceiveVo.class);

	}

	private SelectConditionStep<?> buildSelect(CodeReceiveParam param) {
		SelectConditionStep<?> select = db()
				.select(CARD_BATCH.NAME, CARD_RECEIVE_CODE.ID, USER.USER_ID, USER.USERNAME, USER.MOBILE,
						CARD_RECEIVE_CODE.RECEIVE_TIME, CARD_RECEIVE_CODE.CARD_NO, CARD_RECEIVE_CODE.CODE,
						CARD_RECEIVE_CODE.CARD_PWD, CARD_RECEIVE_CODE.DEL_FLAG)
				.from(CARD_RECEIVE_CODE.leftJoin(USER).on(CARD_RECEIVE_CODE.USER_ID.eq(USER.USER_ID))
						.leftJoin(CARD_BATCH).on(CARD_RECEIVE_CODE.BATCH_ID.eq(CARD_BATCH.ID)))
				.where(CARD_RECEIVE_CODE.CARD_ID.eq(param.getCardId()).and(CARD_RECEIVE_CODE.STATUS.eq(CardConstant.ONE)));

		buildOptionForReceiveCode(param, select);
		return select;
	}

	public List<CardReceiveDownVo> toMakeDownList(CodeReceiveParam param,String lang) {
		SelectConditionStep<?> select = buildSelect(param);
		List<CardReceiveDownVo> list = select.fetchInto(CardReceiveDownVo.class);
		/** 已领取*/
		String haveReceived = Util.translateMessage(lang, JsonResultCode.MSG_CARD_HAVE_RECEIVED.getMessage(), "excel",null);
		/** 未领取*/
		String noReceived = Util.translateMessage(lang, JsonResultCode.MSG_CARD_NO_RECEIVED.getMessage(), "excel",null);
		/** 正常*/
		String normal = Util.translateMessage(lang, JsonResultCode.MSG_CARD_NORMAL.getMessage(), "excel",null);
		/** 已废除*/
		String abolished = Util.translateMessage(lang, JsonResultCode.MSG_CARD_NO_ABOLISHED.getMessage(), "excel",null);

		for (CardReceiveDownVo vo : list) {
			String code = vo.getCode();
			if(!StringUtils.isBlank(code)) {
				vo.setCardMsg(code);
			}
			String cardPwd = vo.getCardPwd();
			if(!StringUtils.isBlank(cardPwd)) {
				if(StringUtils.isEmpty(vo.getCardMsg())) {
					vo.setCardMsg(cardPwd);
				}else {
					vo.setCardMsg(vo.getCardMsg().concat("\n").concat(cardPwd));
				}
			}
			vo.setSReveiveStatus(vo.getReceiveTime() != null ? haveReceived : noReceived);
			vo.setSDelStatus(vo.getDelFlag().equals(CardConstant.ZERO) ? normal : abolished);
		}
		return list;
	}
	/**
	 * 下载Excel
	 * @param param
	 * @param lang
	 * @return
	 */
	public Workbook getCardReceiveExcel(CodeReceiveParam param, String lang) {
		Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
		ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
		excelWriter.writeModelList(toMakeDownList(param,lang), CardReceiveDownVo.class);
		return workbook;

	}


	/**
	 * 会员卡领取详情构建多条件查询参数
	 *
	 * @param param
	 * @param select
	 */
	private void buildOptionForReceiveCode(CodeReceiveParam param, SelectConditionStep<?> select) {
		/** -手机号 */
		if (!StringUtils.isBlank(param.getMobile())) {
			select.and(USER.MOBILE.like(this.likeValue(param.getMobile())));
		}
		/** -用户名 */
		if (!StringUtils.isBlank(param.getUsername())) {
			String likeValue = this.likeValue(param.getUsername());
			select.and(USER.USERNAME.like(likeValue));
		}
		/** -批次号 */
		if (param.getBatchId() != null && !param.getBatchId().equals(ALL_BATCH)) {
			select.and(CARD_RECEIVE_CODE.BATCH_ID.eq(param.getBatchId()));
		}
		Byte reveiveStatus = param.getReveiveStatus();
		if(null!=reveiveStatus) {
			if(reveiveStatus.equals(CardConstant.ONE)) {
				select.and(CARD_RECEIVE_CODE.RECEIVE_TIME.cast(String.class).ne("0000-00-00 00:00:00"));
			}
			if(reveiveStatus.equals(CardConstant.TWO)) {
				select.and(CARD_RECEIVE_CODE.RECEIVE_TIME.cast(String.class).eq("0000-00-00 00:00:00"));
			}
		}
		Byte delStatus = param.getDelStatus();
		if (null != delStatus) {
			if (delStatus.equals(CardConstant.ONE)) {
				select.and(CARD_RECEIVE_CODE.DEL_FLAG.eq(CardConstant.ZERO));
			}
			if (delStatus.equals(CardConstant.TWO)) {
				select.and(CARD_RECEIVE_CODE.DEL_FLAG.eq(CardConstant.ONE));
			}
		}
		/**
		 * 领取码或卡号
		 */
		if(!StringUtils.isBlank(param.getSearch())) {
			select.and(
					CARD_RECEIVE_CODE.CODE.eq(param.getSearch())
					.or(CARD_RECEIVE_CODE.CARD_NO.eq(param.getSearch()))
					);
		}
	}

	/**
	 * 设置del_flag为删除状态
	 *
	 * @param id
	 */
	public Integer deleteCardBatchSql(Integer id) {
		return db().update(CARD_RECEIVE_CODE).set(CARD_RECEIVE_CODE.DEL_FLAG, MCARD_DF_YES)
				.where(CARD_RECEIVE_CODE.ID.eq(id)).execute();
	}

	/**
	 * 会员卡充值明细
	 *
	 * @param param
	 * @return
	 */
	public PageResult<ChargeVo> getChargeList(ChargeParam param,String language) {
		SelectJoinStep<?> select = db()
				.select(USER.USERNAME, USER.MOBILE, CHARGE_MONEY.CHARGE.as("money"), CHARGE_MONEY.REASON,CHARGE_MONEY.REASON_ID,
						CHARGE_MONEY.CHARGE,CHARGE_MONEY.TYPE,CHARGE_MONEY.COUNT,CHARGE_MONEY.EXCHANG_COUNT,
						CHARGE_MONEY.CREATE_TIME, CHARGE_MONEY.MESSAGE)
				.from(CHARGE_MONEY.leftJoin(USER).on(CHARGE_MONEY.USER_ID.eq(USER.USER_ID)));

		buildOptionsForCharge(param, select);
        select.orderBy(CHARGE_MONEY.CREATE_TIME.desc());
		PageResult<ChargeVo> result = getPageResult(select, param.getCurrentPage(), param.getPageRows(), ChargeVo.class);
		for(ChargeVo vo: result.dataList) {
			String reason = RemarkUtil.remarkI18N(language, vo.getReasonId(), vo.getReason());
			vo.setReason(reason);
		}
		return result;
	}

	/**
	 * 充值明细多条件构建
	 *
	 * @param param
	 * @param select
	 */
	private void buildOptionsForCharge(ChargeParam param, SelectJoinStep<?> select) {
		// 会员卡id
		if (param.getCardId() != null) {
			select.where(CHARGE_MONEY.CARD_ID.eq(param.getCardId()));
		}

		// 会员Id
		if(param.getUserId() != null) {
			select.where(CHARGE_MONEY.USER_ID.eq(param.getUserId()));
		}

		// 会员卡类型
		if (param.getCardType() != null) {
			select.where(CHARGE_MONEY.TYPE.eq(param.getCardType()));
		}
		// 用户名
		if (!StringUtils.isBlank(param.getUsername())) {
			select.where(USER.USERNAME.like(likeValue(param.getUsername())));
		}

		// 手机号
		if (!StringUtils.isBlank(param.getMobile())) {
			select.where(USER.MOBILE.eq(param.getMobile()));
		}

		// 余额变动时间 - 开始
		if (param.getStartTime() != null) {
			select.where(CHARGE_MONEY.CREATE_TIME.ge(param.getStartTime()));
		}
		// 余额变动时间 - 结束
		if (param.getEndTime() != null) {
			select.where(CHARGE_MONEY.CREATE_TIME.le(param.getEndTime()));
		}
		if(!StringUtils.isBlank(param.getCardNo())) {
			select.where(CHARGE_MONEY.CARD_NO.eq(param.getCardNo()));
		}
	}

	/**
	 * 	会员卡消费明细
	 *
	 * @param param
	 * @return
	 */
	public PageResult<ChargeVo> getConsumeList(ChargeParam param,String language) {
		SelectJoinStep<?> select = db()
				.select(USER.USERNAME, USER.MOBILE, CARD_CONSUMER.MONEY, CARD_CONSUMER.REASON,CARD_CONSUMER.REASON_ID,CARD_CONSUMER.TYPE,CARD_CONSUMER.EXCHANG_COUNT,CARD_CONSUMER.COUNT,
						CARD_CONSUMER.CREATE_TIME, CARD_CONSUMER.MESSAGE)
				.from(CARD_CONSUMER.leftJoin(USER).on(CARD_CONSUMER.USER_ID.eq(USER.USER_ID)));
		buildOptionsForConsume(param, select);
		select.orderBy(CARD_CONSUMER.CREATE_TIME.desc());
		PageResult<ChargeVo> result = getPageResult(select, param.getCurrentPage(), param.getPageRows(), ChargeVo.class);
		// 处理国际化
		for(ChargeVo vo: result.dataList) {
			String reason = RemarkUtil.remarkI18N(language, vo.getReasonId(), vo.getReason());
			vo.setReason(reason);
		}
		return result;
	}

	/**
	 * 消费明细多条件查询构建
	 *
	 * @param param
	 * @param select
	 */
	private void buildOptionsForConsume(ChargeParam param, SelectJoinStep<?> select) {

		// 会员卡id
		if (param.getCardId() != null) {
			select.where(CARD_CONSUMER.CARD_ID.eq(param.getCardId()));
		}
		// 会员卡类型
		if (param.getCardType() != null) {
			select.where(CARD_CONSUMER.TYPE.eq(param.getCardType()));
		}
		// 用户名
		if (!StringUtils.isBlank(param.getUsername())) {
			select.where(USER.USERNAME.like(likeValue(param.getUsername())));
		}

		// 手机号
		if (!StringUtils.isBlank(param.getMobile())) {
			select.where(USER.MOBILE.eq(param.getMobile()));
		}

		// 余额变动时间 - 开始
		if (param.getStartTime() != null) {
			select.where(CARD_CONSUMER.CREATE_TIME.ge(param.getStartTime()));
		}
		// 余额变动时间 - 结束
		if (param.getEndTime() != null) {
			select.where(CARD_CONSUMER.CREATE_TIME.le(param.getEndTime()));
		}
		if(!StringUtils.isBlank(param.getCardNo())) {
			select.where(CARD_CONSUMER.CARD_NO.eq(param.getCardNo()));
		}
	}

	/**
	 * 更新审核会员卡表
	 *
	 * @param record
	 */
	public int updateCardExamine(CardExamineRecord record) {
		return db().executeUpdate(record, CARD_EXAMINE.ID.eq(record.getId()));
	}

	public CardExamineRecord getCardExamineRecordById(Integer id) {
		return db().selectFrom(CARD_EXAMINE)
					.where(CARD_EXAMINE.ID.eq(id))
					.fetchAnyInto(CARD_EXAMINE);
	}
	/**
	 * 更新user_card 激活时间
	 *
	 * @param cardNo
	 * @param now
	 */
	public void updateUserCardByCardNo(String cardNo, Timestamp now) {
		db().update(USER_CARD).set(USER_CARD.ACTIVATION_TIME, now).where(USER_CARD.CARD_NO.eq(cardNo)).execute();
	}

	/**
	 * 获取会员卡订单信息
	 *
	 * @param param
	 * @return
	 */
	public PageResult<CardConsumeVo> getCardConsumeOrderList(CardConsumeParam param) {
		SelectConditionStep<?> select = db()
				.select(CARD_CONSUMER.ORDER_SN, CARD_CONSUMER.EXCHANG_COUNT, CARD_CONSUMER.COUNT,
						CARD_CONSUMER.CREATE_TIME, USER.USERNAME, USER.MOBILE)
				.from(CARD_CONSUMER.leftJoin(USER).on(CARD_CONSUMER.USER_ID.eq(USER.USER_ID)))
				.where(CARD_CONSUMER.CARD_ID.eq(param.getCardId())).and(CARD_CONSUMER.ORDER_SN.isNotNull());

		buildOptionsForCardConsumer(select, param);
		select.orderBy(CARD_CONSUMER.CREATE_TIME.desc());
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(), CardConsumeVo.class);
	}

	/**
	 * 会员卡查询获取参数s
	 *
	 * @param select
	 * @param param
	 */
	private void buildOptionsForCardConsumer(SelectConditionStep<?> select, CardConsumeParam param) {
		// 订单号
		if (!StringUtils.isBlank(param.getOrderSn())) {
			select.and(CARD_CONSUMER.ORDER_SN.eq(param.getOrderSn()));
		}
		// 会员昵称
		if (!StringUtils.isBlank(param.getUsername())) {
			String likeValue = this.likeValue(param.getUsername());
			select.and(USER.USERNAME.like(likeValue));
		}
		// 手机号
		if (!StringUtils.isBlank(param.getMobile())) {
			select.and(USER.MOBILE.eq(param.getMobile()));
		}
		// 次数使用类型
		if (param.getType() != null) {
			// 1 兑换商品
			if (EXCHANG_COUNT_TYPE.equals(param.getType())) {
				select.and(CARD_CONSUMER.EXCHANG_COUNT.notEqual(SHORT_ZERO));
			} else if (COUNT_TYPE.equals(param.getType())) {
				// 2 门店服务
				select.and(CARD_CONSUMER.COUNT.notEqual(SHORT_ZERO));
			}
		}

		// 次数变动时间
		if (param.getFirstTime() != null) {
			select.and(CARD_CONSUMER.CREATE_TIME.ge(param.getFirstTime()));
		}
		if (param.getSecondTime() != null) {
			select.and(CARD_CONSUMER.CREATE_TIME.le(param.getSecondTime()));
		}
	}

	/**
	 * 插入card_batch 批次记录
	 *
	 * @param param
	 * @return 批次id
	 */
	public Integer createCardBatch(CardBatchParam param) {
		Record1<Integer> batchId = db()
				.insertInto(CARD_BATCH, CARD_BATCH.ACTION, CARD_BATCH.CODE_PREFIX, CARD_BATCH.NAME,
						CARD_BATCH.CODE_SIZE,CARD_BATCH.CARD_PWD_SIZE, CARD_BATCH.NUMBER)
				.values(param.getAction(), param.getCodePrefix(), param.getBatchName(), param.getCodeSize(),param.getCardPwdSize(),
						param.getNumber())
				.returningResult(CARD_BATCH.ID).fetchOne();

		if (batchId != null) {
			logger().info("批次id为： " + batchId.get(CARD_BATCH.ID));
			return batchId.get(CARD_BATCH.ID);
		} else {
			return 0;
		}

	}

	/**
	 * 生成分组ID
	 *
	 * @param batchId
	 */
	public Integer generateGroupId(Integer batchId) {
		Record1<Integer> maxGroupId = db().select(DSL.max(CARD_RECEIVE_CODE.GROUP_ID)).from(CARD_RECEIVE_CODE)
				.where(CARD_RECEIVE_CODE.BATCH_ID.eq(batchId)).fetchOne();
		if (maxGroupId.value1() != null) {
			return maxGroupId.into(Integer.class) + 1;
		} else {
			return 1;
		}
	}

	/**
	 * code是否存在，是返回true，否返回false
	 *
	 * @param code
	 * @return
	 */
	public boolean isExistCode(String code) {

		CardReceiveCodeRecord r = db().selectFrom(CARD_RECEIVE_CODE).where(CARD_RECEIVE_CODE.CODE.eq(code))
				.and(CARD_RECEIVE_CODE.DEL_FLAG.eq(MCARD_DF_NO)).and(CARD_RECEIVE_CODE.ERROR_MSG.isNull()).fetchAny();

		return r != null;
	}

	/**
	 * 限制的batchId里code是否存在，是返回true，否返回false
	 *
	 * @param code
	 * @return
	 */
	public boolean isExistCodeInBatch(String code,Integer[] batchIds) {
		if(batchIds!=null&&batchIds.length>0) {
			CardReceiveCodeRecord r = db().selectFrom(CARD_RECEIVE_CODE).where(CARD_RECEIVE_CODE.CODE.eq(code))
					.and(CARD_RECEIVE_CODE.DEL_FLAG.eq(MCARD_DF_NO)).and(CARD_RECEIVE_CODE.ERROR_MSG.isNull().and(CARD_RECEIVE_CODE.BATCH_ID.in(batchIds))).fetchAny();
			return r != null;
		}
		return false;

	}
	/**
	 * 限制的batchId里code是否存在，是返回true，否返回false
	 *
	 * @param code
	 * @return
	 */
	public boolean isExistCardNoInBatch(String code,Integer[] batchIds) {
		if(batchIds!=null&&batchIds.length>0) {
			CardReceiveCodeRecord r = db().selectFrom(CARD_RECEIVE_CODE).where(CARD_RECEIVE_CODE.CARD_NO.eq(code))
					.and(CARD_RECEIVE_CODE.DEL_FLAG.eq(MCARD_DF_NO)).and(CARD_RECEIVE_CODE.ERROR_MSG.isNull().and(CARD_RECEIVE_CODE.BATCH_ID.in(batchIds))).fetchAny();
			return r != null;
		}
		return false;

	}
	/**
	 * 将领取批次的生成码存入数据库
	 *
	 * @param param
	 * @param codeList
	 */
	public void insertIntoCardReceiveCode(CardBatchParam param, List<String> codeList) {
		InsertValuesStep3<CardReceiveCodeRecord, Integer, Integer, String> insert = db().insertInto(CARD_RECEIVE_CODE)
				.columns(CARD_RECEIVE_CODE.BATCH_ID, CARD_RECEIVE_CODE.GROUP_ID, CARD_RECEIVE_CODE.CODE);

		Integer batchId = param.getBatchId();
		Integer groupId = param.getGroupId();
		for (String code : codeList) {
			insert.values(batchId, groupId, code);
		}
		int res = insert.execute();
		logger().info("成功生成领取码" + res + "条");

	}

	/**
	 * 导入文件的插入
	 * @param param
	 * @param codeList
	 * @return
	 */
	public int insertCardReceiveCodeByCheck(CardBatchParam param, List<String> codeList) {
		InsertValuesStep4<CardReceiveCodeRecord, Integer, Integer, String, String> insert = db().insertInto(CARD_RECEIVE_CODE)
				.columns(CARD_RECEIVE_CODE.BATCH_ID, CARD_RECEIVE_CODE.GROUP_ID, CARD_RECEIVE_CODE.CODE,CARD_RECEIVE_CODE.ERROR_MSG);
		Integer batchId = param.getBatchId();
		Integer groupId = param.getGroupId();
		String regex = "^[\\w\\d]*$";
		Integer[] batchIdStr = param.getBatchIdStr();
		logger().info("batchIds:"+batchIdStr);
		for (String code : codeList) {
			String msg=null;
			if(StringUtils.isEmpty(code)) {
				msg=CardNoImportTemplate.CARDNO_NULL.getCode();
			}else {
				if(!code.matches(regex)) {
					msg=CardNoImportTemplate.CARDNO_ERROR.getCode();
				}if(code.length()>15) {
					msg=CardNoImportTemplate.CARDNO_LIMIT.getCode();
					code=code.substring(0,15);
				}else if(true) {
					//batchIdStr!=null&&batchIdStr.length>0
					if(getReceiveCode(code, batchIdStr)) {
						msg=CardNoImportTemplate.CARDNO_EXIST.getCode();
					}
				}
			}
			logger().info("导入batchId："+batchId+" groupId："+groupId+" code："+code+" msg："+msg);
			insert.values(batchId, groupId, code,msg);
		}
		int res = insert.execute();
		logger().info("成功生成领取码" + res + "条");
		return res;
	}


	/**
	 * 卡号存在 是 true, 否 false
	 *
	 * @param cardNo
	 * @return
	 */
	public boolean isExistCardNo(String cardNo) {
		CardReceiveCodeRecord res = db().selectFrom(CARD_RECEIVE_CODE).where(CARD_RECEIVE_CODE.CARD_NO.eq(cardNo))
				.and(CARD_RECEIVE_CODE.ERROR_MSG.isNull()).fetchAny();
		return res != null;
	}

	/**
	 * 插入数据
	 *
	 * @param param
	 * @param cardNoList
	 * @param pwdList
	 */
	public void insertIntoCardReceiveCode(CardBatchParam param, List<String> cardNoList, List<String> pwdList) {
		InsertValuesStep4<CardReceiveCodeRecord, Integer, Integer, String, String> insert = db()
				.insertInto(CARD_RECEIVE_CODE).columns(CARD_RECEIVE_CODE.BATCH_ID, CARD_RECEIVE_CODE.GROUP_ID,
						CARD_RECEIVE_CODE.CARD_NO, CARD_RECEIVE_CODE.CARD_PWD);
		Integer batchId = param.getBatchId();
		Integer groupId = param.getGroupId();
		for (int i = 0; i < param.getNumber(); i++) {
			insert.values(batchId, groupId, cardNoList.get(i), pwdList.get(i));
		}
		int res = insert.execute();
		logger().info("成功执行" + res + "条");
	}

	/**
	 * 带校验的导入
	 * @param param
	 * @param cardNoList
	 * @param pwdList
	 * @return
	 */
	public int insertIntoCardReceiveCodeByCheck(CardBatchParam param, List<String> cardNoList, List<String> pwdList) {
		InsertValuesStep5<CardReceiveCodeRecord, Integer, Integer, String, String, String> insert = db()
				.insertInto(CARD_RECEIVE_CODE).columns(CARD_RECEIVE_CODE.BATCH_ID, CARD_RECEIVE_CODE.GROUP_ID,
						CARD_RECEIVE_CODE.CARD_NO, CARD_RECEIVE_CODE.CARD_PWD, CARD_RECEIVE_CODE.ERROR_MSG);
		Integer batchId = param.getBatchId();
		Integer groupId = param.getGroupId();
		int size = cardNoList.size();
		int size2 = pwdList.size();
		int length = size > size2 ? size : size2;
		String regex = "^[\\w\\d]*$";
		Integer[] batchIdStr = param.getBatchIdStr();
		logger().info("batchIds:" + batchIdStr);
		for (int i = 0; i < length; i++) {
			String msg = null;
			String code = cardNoList.get(i);
			String pwd = pwdList.get(i);
			if (StringUtils.isEmpty(code)) {
				msg = CardNoImportTemplate.CARDNO_NULL.getCode();
			} else {
				if (!code.matches(regex)) {
					msg = CardNoImportTemplate.CARDNO_ERROR.getCode();
				}
				if (code.length() > 15) {
					msg = CardNoImportTemplate.CARDNO_LIMIT.getCode();
					code = code.substring(0, 15);
				} else if (true) {
					//batchIdStr != null && batchIdStr.length > 0 如果只对当前卡的校验，把true替换，更改sql
					if (getReceiveCardNo(code, batchIdStr)) {
						msg = CardNoImportTemplate.CARDNO_EXIST.getCode();
					}
				}
			}
			if (StringUtils.isEmpty(pwd)) {
				msg = CardNoImportTemplate.CARDPWD_NULL.getCode();
			} else {
				if (!pwd.matches(regex)) {
					msg = CardNoImportTemplate.CARDPWD_ERROR.getCode();
				} else if (pwd.length() > 20) {
					msg = CardNoImportTemplate.CARDPWD_LIMIT.getCode();
					pwd = code.substring(0, 20);
				}
			}
			insert.values(batchId, groupId, code, pwd, msg);
		}
		int res = insert.execute();
		logger().info("成功执行" + res + "条");
		return res;
	}
	/**
	 * 根据会员卡类型查询会员卡
	 */
	public List<Integer> getCardIdByType(Byte type) {
		return db().select(MEMBER_CARD.ID).from(MEMBER_CARD).where(MEMBER_CARD.CARD_TYPE.eq(type))
				.fetchInto(Integer.class);
	}

	/**
	 * 获取会员卡信息
	 */
	public MemberCardRecord getCardById(Integer cardId) {
		MemberCardRecord mCard = getInfoByCardId(cardId);
		// 处理背景图片
		if(mCard != null) {
			if(!StringUtils.isBlank(mCard.getBgImg())) {
				String imageUrl = saas.getShopApp(getShopId()).image.imageUrl(mCard.getBgImg());
				mCard.setBgImg(imageUrl);
			}
		}
        if(StringUtils.isBlank(mCard.getBgColor())) {
            mCard.setBgColor(CardUtil.getDefaultBgColor());
        }

		return mCard;

	}

	public MemberCardRecord getInfoByCardId(Integer cardId) {
		MemberCardRecord mCard = db().selectFrom(MEMBER_CARD).where(MEMBER_CARD.ID.eq(cardId)).fetchAny();
		return mCard;
	}

	/**
	 * 获取所有正在使用的等级会员卡
	 */
	public List<MemberCardRecord> getAllUsingGradeCard() {
		return db().selectFrom(MEMBER_CARD).where(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
				.and(MEMBER_CARD.FLAG.eq(MCARD_FLAG_USING)).orderBy(MEMBER_CARD.GRADE.asc())
				.fetchInto(MemberCardRecord.class);
	}


	/**
	 * 将数据放入到数据库
	 */
	public int addMemberCardAndGetCardId(MemberCardRecord cardRecord) {
		MemberCardRecord c = db().insertInto(MEMBER_CARD).set(cardRecord).returning(MEMBER_CARD.ID).fetchOne();
		logger().info("新成功新的会员卡，id: "+c.getId());
		return c.getId();
	}

	/**
	 * 会员卡数据更新
	 */
	public void updateMemberCardById(MemberCardRecord cardRecord, Integer id) {
		int num = db().executeUpdate(cardRecord, MEMBER_CARD.ID.eq(id));
		logger().info("成功更新： " + num + " 行数据");
	}

	public List<CardBatchDetailVo> selectBatchCfgById(Integer batchId) {
		 return db()
				 .selectFrom(CARD_BATCH)
				 .where(CARD_BATCH.ID.eq(batchId))
				 .fetchInto(CardBatchDetailVo.class);
	}

	public List<CardHolderExcelVo> getAllCardHolderAll(CardHolderParam param) {
		User invitedUser = USER.as("a");
        Field<Timestamp> cardExamineCreateTime = CARD_EXAMINE.CREATE_TIME.as("card_examine_create_time");
        List<Field<?>> f = new ArrayList<>(Arrays.asList(USER_CARD.fields()));
		f.add(USER.USERNAME);
		f.add(invitedUser.USERNAME.as("invitedName"));
		f.add(USER.MOBILE);
		f.add(CARD_EXAMINE.STATUS);
		f.add(cardExamineCreateTime);
		SelectJoinStep<?> select = db()
				.select(f.toArray(new Field<?>[0])).select(DSL.count(CHARGE_MONEY.CARD_NO).as("chargeTimes"),DSL.count(CARD_CONSUMER.CARD_NO).as("consumeTimes"))
				.from(USER_CARD.leftJoin(USER.leftJoin(invitedUser).on(USER.INVITE_ID.eq(invitedUser.USER_ID))

				).on(USER_CARD.USER_ID.eq(USER.USER_ID)))
				.leftJoin(CARD_EXAMINE).on(USER_CARD.CARD_NO.eq(CARD_EXAMINE.CARD_NO))
				.leftJoin(CHARGE_MONEY).on(USER_CARD.CARD_NO.eq(CHARGE_MONEY.CARD_NO))
				.leftJoin(CARD_CONSUMER).on(USER_CARD.CARD_NO.eq(CARD_CONSUMER.CARD_NO));

		buildOptions(param, select);
		f.add(CHARGE_MONEY.CARD_NO);
		f.add(CARD_CONSUMER.CARD_NO);
		select.where(USER_CARD.CARD_ID.eq(param.getCardId()))
			.groupBy(f.toArray(new Field<?>[0]))
			.orderBy(cardExamineCreateTime.desc(),USER_CARD.USER_ID.desc());
		List<CardHolderExcelVo> list = new ArrayList<CardHolderExcelVo>();
		Result<?> fetch = select.fetch();
		if(fetch!=null) {
			list=fetch.into(CardHolderExcelVo.class);
		}
		return list;
	}

	public List<CodeReceiveVo> getBatchGroupList(Integer batchId) {
		Result<CardReceiveCodeRecord> fetch = db().selectFrom(CARD_RECEIVE_CODE).where(CARD_RECEIVE_CODE.BATCH_ID.eq(batchId).and(CARD_RECEIVE_CODE.DEL_FLAG.eq(MCARD_DF_NO))).fetch();
		List<CodeReceiveVo> list = new ArrayList<CodeReceiveVo>();
		if (fetch != null) {
			list = fetch.into(CodeReceiveVo.class);
		}
		return list;
	}

	public Result<CardReceiveCodeRecord> getBatchGroupListByMsg(Integer batchId,Boolean success) {
		SelectConditionStep<CardReceiveCodeRecord> where = db().selectFrom(CARD_RECEIVE_CODE).where(CARD_RECEIVE_CODE.BATCH_ID.eq(batchId).and(CARD_RECEIVE_CODE.DEL_FLAG.eq(MCARD_DF_NO)));
		if(success) {
			where.and(CARD_RECEIVE_CODE.ERROR_MSG.isNull());
		}else {
			where.and(CARD_RECEIVE_CODE.ERROR_MSG.isNotNull());
		}
		Result<CardReceiveCodeRecord> fetch = where.fetch();
		return fetch;
	}

	public CardBatchRecord  getBatch(Integer batchId) {
		return db().selectFrom(CARD_BATCH).where(CARD_BATCH.ID.eq(batchId)).fetchAny();
	}

	/**
	 * code是否已经被定义，重复的返回true
	 * @param code
	 * @param batchIds
	 * @return
	 */
	public boolean getReceiveCode(String code, Integer[] batchIds) {
		CardReceiveCodeRecord fetch = db().selectFrom(CARD_RECEIVE_CODE)
				.where(CARD_RECEIVE_CODE.CODE.eq(code)
								.and(CARD_RECEIVE_CODE.ERROR_MSG.isNull()))
				.fetchAny();
		if (fetch != null) {
			return true;
		}
		return false;
	}

	/**
	 * code是否已经被定义，重复的返回true
	 * @param code
	 * @param batchIds
	 * @return
	 */
	public boolean getReceiveCardNo(String code, Integer[] batchIds) {
		 CardReceiveCodeRecord fetch = db().selectFrom(CARD_RECEIVE_CODE)
				.where(CARD_RECEIVE_CODE.CARD_NO.eq(code)
								.and(CARD_RECEIVE_CODE.ERROR_MSG.isNull()))
				.fetchAny();
		if (fetch != null) {
			return true;
		}
		return false;
	}


	/**
	 * 	根据会员卡ID获取会员卡的名称
	 * @return cardId和cardName的对象List
	 */
	public List<CardBasicVo> getCardBasicInfoById(Integer ...ids){

		return db().select(MEMBER_CARD.ID,MEMBER_CARD.CARD_NAME)
			.from(MEMBER_CARD)
			.where(MEMBER_CARD.ID.in(ids))
			.fetchInto(CardBasicVo.class);
	}
	/**
	 * 	获取系统中未被删除的卡
	 */
	public PageResult<MemberCardRecord> selectCardList(SearchCardParam param) {

		/**
		 * 	滤掉停用会员卡
		 */
		Condition condition = DSL.noCondition();
		if(param.getFilterStop()!=null && Boolean.TRUE.equals(param.getFilterStop())) {
			condition = condition.and(MEMBER_CARD.FLAG.eq(CardConstant.MCARD_FLAG_USING));
		}
		SelectSeekStep2<MemberCardRecord, String, Integer> select = db().selectFrom(MEMBER_CARD)
				.where(MEMBER_CARD.CARD_TYPE.equal(param.getCardType()))
				.and(MEMBER_CARD.DEL_FLAG.equal(MCARD_DF_NO))
				.and(condition)
				.orderBy(MEMBER_CARD.GRADE.desc(),MEMBER_CARD.ID.desc());
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MemberCardRecord.class);
	}


	/**
	 * 获取卡号-会员卡详情
	 * @param nos
	 * @return
	 */
	public Map<String, MemberCardRecord> getCardByNo(String ...nos) {
		 return db().select(USER_CARD.CARD_NO).select(MEMBER_CARD.fields())
			.from(USER_CARD)
			.innerJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID))
			.where(USER_CARD.CARD_NO.in(nos))
			.fetchMap(USER_CARD.CARD_NO, MemberCardRecord.class);

	}

	public CardFullDetail getCardDetailByNo(String cardNo) {
		logger().info("根据卡号获取卡的详细信息");
		MemberCard memberCard = MEMBER_CARD.as("memberCardd");
		UserCard userCard = USER_CARD.as("userCard");
		CardFullDetail res = db().select(memberCard.fields()).select(userCard.fields())
			.from(userCard)
			.innerJoin(memberCard).on(userCard.CARD_ID.eq(memberCard.ID))
			.where(userCard.CARD_NO.eq(cardNo))
			.fetchSingle(recordMapToCardFullDetail(userCard,memberCard));
		return res;
	}


	private RecordMapper<? super Record, CardFullDetail> recordMapToCardFullDetail(UserCard uCard,MemberCard mCard) {
		return record->{
			Table<MemberCardRecord> mCardTable = MEMBER_CARD.as(mCard.getName());
			MemberCardRecord memberCard = mCardTable.from(record);
			Table<UserCardRecord> uCardTable = USER_CARD.as(uCard.getName());
			UserCardRecord userCard = uCardTable.from(record);
			return CardFullDetail.builder()
						.userId(userCard.getUserId())
						.cardNo(userCard.getCardNo())
						.userCard(userCard)
						.memberCard(memberCard)
						.build();
		};
	}

    public String getCardNameByNo(String cardNo) {
        MemberCard memberCard = MEMBER_CARD.as("memberCardd");
        UserCard userCard = USER_CARD.as("userCard");
        return db().select(memberCard.CARD_NAME)
            .from(userCard)
            .innerJoin(memberCard).on(userCard.CARD_ID.eq(memberCard.ID))
            .where(userCard.CARD_NO.eq(cardNo))
            .fetchAnyInto(String.class);
    }
}
