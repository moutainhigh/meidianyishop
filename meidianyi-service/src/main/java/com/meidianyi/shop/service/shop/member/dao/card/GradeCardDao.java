package com.meidianyi.shop.service.shop.member.dao.card;

import org.jooq.Condition;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBasicVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
/**
 * 
 * @author 黄壮壮
 * 等级卡数据交互层
 */
@Service
public class GradeCardDao extends ShopBaseService {
	
	/**
	 * 获取所有没有被删除的等级卡
	 * @return 
	 */
	public Result<MemberCardRecord> getAllNoDeleteCard() {
		Condition condition = DSL.noCondition();
		condition = condition
					.and(MEMBER_CARD.CARD_TYPE.eq(CardConstant.MCARD_TP_GRADE))
					.and(MEMBER_CARD.DEL_FLAG.eq(CardConstant.MCARD_DF_NO));
		return getCardList(condition);
		
	}

	public Result<MemberCardRecord> getCardList(Condition condition) {
		return db().selectFrom(MEMBER_CARD).where(condition).orderBy(MEMBER_CARD.GRADE.asc()).fetchInto(MEMBER_CARD);
	}

	/**
	 * 获取可用的会员卡列表信息
	 * @return List<CardBasicVo> 会员卡基本信息
	 */
	public List<CardBasicVo> getAllAvailableGradeCards() {
		Condition condition = DSL.noCondition();
		condition = condition
				.and(MEMBER_CARD.CARD_TYPE.eq(CardConstant.MCARD_TP_GRADE))
				.and(MEMBER_CARD.DEL_FLAG.eq(CardConstant.MCARD_DF_NO))
				.and(MEMBER_CARD.FLAG.eq(CardConstant.MCARD_FLAG_USING));
		return db()
				.select(MEMBER_CARD.ID,MEMBER_CARD.CARD_NAME,MEMBER_CARD.GRADE)
				.from(MEMBER_CARD)
				.where(condition)
				.orderBy(MEMBER_CARD.GRADE.asc())
				.fetchInto(CardBasicVo.class);
	}
	
	
	/**
	 * 	查询有效等级卡，简单信息
	 * @return List<Map<String,Object>> 等级卡List,该等价卡包括Id,name,grade信息
	 */
	public List<Map<String,Object>> getAllValidGradeCardList() {
		 Result<Record3<Integer, String, String>> res = db().select(MEMBER_CARD.ID,MEMBER_CARD.CARD_NAME,MEMBER_CARD.GRADE)
		 	 .from(MEMBER_CARD)
			.where(MEMBER_CARD.CARD_TYPE.eq(CardConstant.MCARD_TP_GRADE))
			.and(MEMBER_CARD.FLAG.eq(CardConstant.MCARD_FLAG_USING))
			.fetch().into(MEMBER_CARD.ID,MEMBER_CARD.CARD_NAME,MEMBER_CARD.GRADE);
		 List<Map<String,Object>> list = new ArrayList<>();
		 for(int i=0;i<res.size();i++) {
			 Record3<Integer, String, String> record = res.get(i);
			 list.add(record.intoMap());
		 }
		return list;
	}
}
