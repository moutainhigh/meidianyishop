package com.meidianyi.shop.service.shop.member.dao;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.ActiveAuditParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardVerifyResultVo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectWhereStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.meidianyi.shop.db.shop.Tables.CARD_EXAMINE;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardVerifyConstant.*;

/**
* @author 黄壮壮
* @Date: 2019年10月30日
* @Description:
*/
@Service
public class CardVerifyDaoService extends ShopBaseService {

	/**
	 * 获取卡的审核结果
	 * @param cardNo
	 */
	public CardVerifyResultVo getCardVerifyResult(String cardNo){
		return db().select(CARD_EXAMINE.ID,CARD_EXAMINE.STATUS,CARD_EXAMINE.REFUSE_DESC,CARD_EXAMINE.REFUSE_TIME,CARD_EXAMINE.PASS_TIME)
					.from(CARD_EXAMINE)
					.where(CARD_EXAMINE.CARD_NO.eq(cardNo))
                    .and(CARD_EXAMINE.DEL_FLAG.eq(VDF_NO))
					.orderBy(CARD_EXAMINE.ID.desc())
					.fetchAnyInto(CardVerifyResultVo.class);
	}


	public PageResult<? extends Record> getVerifyPageList(ActiveAuditParam param) {
		Field<?>[] fields = CARD_EXAMINE.fields();
		List<Field<?>> f = new ArrayList<>(Arrays.asList(fields));
		f.add(USER.MOBILE);
		f.add(USER.USERNAME);
		Field<?>[] myFields = f.toArray(new Field<?>[0]);
		Record myRecord = db().newRecord(myFields);
		SelectJoinStep<?> select = db().select(myFields)
					.from(CARD_EXAMINE)
					.leftJoin(USER).on(CARD_EXAMINE.USER_ID.eq(USER.USER_ID));
		buildOptions(select,param);
		select.orderBy(CARD_EXAMINE.UPDATE_TIME.desc(),CARD_EXAMINE.CREATE_TIME.desc());

		if(param.getStartNum()!=null && param.getEndNum()!=null) {
			logger().info("查询区间");
			Integer startNum = param.getStartNum()-1;
			if(startNum<0) {
				startNum = 0;
			}
			Integer rowNum = param.getEndNum()-startNum;
			return getRangeResult(select,startNum,rowNum,myRecord.getClass());
		}else {
			return getPageResult(select, param.getCurrentPage(), param.getPageRows(), myRecord.getClass());
		}

	}
	/**
	 * 	查询区间
	 * @return
	 */
	public <T> PageResult<T> getRangeResult(SelectLimitStep<?> select, Integer startNum, Integer rowNum,
			Class<T> clazz) {
		PageResult<T> pageResult = new PageResult<>();
		Result<?> result = select.limit(startNum,rowNum).fetch();
		pageResult.dataList = result.into(clazz);
		return pageResult;
	}

	public CardExamineRecord getLastRecord(ActiveAuditParam param) {
		SelectWhereStep<CardExamineRecord> select = db().selectFrom(CARD_EXAMINE);
		buildOptions(select,param);
		return select.orderBy(CARD_EXAMINE.CREATE_TIME.asc()).fetchAny();
	}

	/**
	 * 查询审核多条件构建
	 */
	private void buildOptions(SelectWhereStep<?> select, ActiveAuditParam param) {

		// 会员卡id
		if(isNotNull(param.getCardId())) {
			select.where(CARD_EXAMINE.CARD_ID.eq(param.getCardId()));
		}

		// 订单id
		if(isNotNull(param.getIds())) {
			select.where(CARD_EXAMINE.ID.in(param.getIds()));
		}else if (isNotNull(param.getId())){
			select.where(CARD_EXAMINE.ID.eq(param.getId()));
		}

		// 审核状态
		if(isNotNull(param.getStatus())) {
			select.where(CARD_EXAMINE.STATUS.eq(param.getStatus()));
		}
		// 真实姓名
		if(isNotNull(param.getRealName())) {
			String likeValue = likeValue(param.getRealName());
			select.where(CARD_EXAMINE.REAL_NAME.like(likeValue));
		}
		// 手机号
		if(isNotNull(param.getMobile())) {
			select.where(USER.MOBILE.like(likeValue(param.getMobile())));
		}
		// 申请时间 - 开始
		if(isNotNull(param.getFirstTime())) {
			select.where(CARD_EXAMINE.CREATE_TIME.ge(param.getFirstTime()));
		}
		// 申请时间 - 结束
		if(isNotNull(param.getSecondTime())) {
			select.where(CARD_EXAMINE.CREATE_TIME.le(param.getSecondTime()));
		}

		// 审核超时
		if(isNotNull(param.getExamineOver())) {
			select.where(CARD_EXAMINE.CREATE_TIME.le(param.getExamineOver()))
				  .and(CARD_EXAMINE.DEL_FLAG.eq(VDF_NO))
				  .and(CARD_EXAMINE.STATUS.eq(VSTAT_CHECKING));

		}

	}

	private boolean isNotNull(Object obj) {
		if(obj instanceof String){
            return !StringUtils.isBlank((String)obj);
        }
        return obj != null;
	}


	public Integer getCountOverDueRecord(ActiveAuditParam param) {
		SelectJoinStep<?> select = db().selectCount().from(CARD_EXAMINE);
		buildOptions(select,param);
		return select.fetchAnyInto(Integer.class);
	}


	public Integer countUndealUser(Integer cardId) {
		return db().fetchCount(CARD_EXAMINE, CARD_EXAMINE.CARD_ID.eq(cardId).and(CARD_EXAMINE.STATUS.eq(VSTAT_CHECKING)));
	}

    public Set<Integer> countUndealUserSet(Integer cardId) {
        Condition condition = CARD_EXAMINE.CARD_ID.eq(cardId).and(CARD_EXAMINE.STATUS.eq(VSTAT_CHECKING));
        return db().select(CARD_EXAMINE.ID).from(CARD_EXAMINE).where(condition).fetchSet(CARD_EXAMINE.ID);
    }


	public List<CardExamineRecord> selectUndealVerifyRecord(Integer cardId) {
		return db().selectFrom(CARD_EXAMINE)
			.where(CARD_EXAMINE.CARD_ID.eq(cardId))
			.and(CARD_EXAMINE.STATUS.eq(VSTAT_CHECKING))
			.fetchInto(CardExamineRecord.class);
	}


	public void updateCardVerify(Integer id) {
		db().update(CARD_EXAMINE)
			.set(CARD_EXAMINE.STATUS,VSTAT_PASS)
			.set(CARD_EXAMINE.PASS_TIME, DateUtils.getLocalDateTime())
			.where(CARD_EXAMINE.ID.eq(id));
	}


	public CardExamineRecord selectRecordById(Integer id) {
		return db().selectFrom(CARD_EXAMINE).where(CARD_EXAMINE.ID.eq(id)).fetchAny();
	}


	public CardExamineRecord getStatusByNo(String cardNo) {
		return db().selectFrom(CARD_EXAMINE)
                   .where(CARD_EXAMINE.CARD_NO.eq(cardNo))
                   .and(CARD_EXAMINE.DEL_FLAG.eq(VDF_NO))
                   .orderBy(CARD_EXAMINE.ID.desc())
				.fetchAny();
	}

}
