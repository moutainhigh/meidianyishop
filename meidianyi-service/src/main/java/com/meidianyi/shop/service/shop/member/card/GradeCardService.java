package com.meidianyi.shop.service.shop.member.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.CardVerifyConstant;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBasicVo;
import com.meidianyi.shop.service.shop.member.dao.card.GradeCardDao;

import static com.meidianyi.shop.db.shop.Tables.CARD_EXAMINE;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.USER;


/**
 *
 * @author 黄壮壮
 * 等级会员卡服务
 */
@Service
public class GradeCardService extends ShopBaseService{
	@Autowired
	private GradeCardDao dao;
	/**
	 * 获得所有未被删除的卡，包括停用卡
	 */
	public Result<MemberCardRecord> getAllNoDeleteCard() {
		return dao.getAllNoDeleteCard();
	}
	/**
	 * 获得所有未被删除等级卡的等级
	 */
	public List<String> getAllNoDeleteCardGrade() {
		List<String> res = new ArrayList<>();
		Result<MemberCardRecord> cardList = getAllNoDeleteCard();
		for(MemberCardRecord re: cardList) {
			res.add(re.getGrade());
		}
		return res;
	}

    /**
     * 清除历史的等价卡信息
     * @param userId
     */
	public void clearUserAllGrade(Integer userId,Integer cardId,boolean isFromAdmin){
	    logger().info("清空所有之前的等级卡为废除状态");
	    this.transaction(()->{
	        //  user_card 废除所有用户持有的等级卡
            int execute = db().update(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
                .set(USER_CARD.FLAG, CardConstant.UCARD_FG_STOP)
                .where(USER_CARD.USER_ID.eq(userId))
                .and(USER_CARD.FLAG.eq(CardConstant.UCARD_FG_USING))
                .and(MEMBER_CARD.CARD_TYPE.eq(CardConstant.MCARD_TP_GRADE))
                .execute();
            logger().info("USER_CARD执行{}行",execute);

            String cardName = db().select(MEMBER_CARD.CARD_NAME).from(MEMBER_CARD).where(MEMBER_CARD.ID.eq(cardId)).fetchAnyInto(String.class);
            String userName = db().select(USER.USERNAME).from(USER).where(USER.USER_ID.eq(userId)).fetchAnyInto(String.class);
            List<Integer> cardIds = db().select(USER_CARD.CARD_ID)
                .from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
                .where(MEMBER_CARD.CARD_TYPE.eq(CardConstant.MCARD_TP_GRADE))
                .and(USER_CARD.USER_ID.eq(userId))
                .fetchInto(Integer.class);
            if(cardIds != null && cardIds.size()>0){
                //  激活审核设置为审核失败，失败原因为“更换等价卡“
                StringBuilder desc = new StringBuilder();
                if(isFromAdmin){
                    desc.append("该审核记录自动不通过: 管理员操作该卡已经替换为"+cardName);
                }else{
                    desc.append("该审核记录自动不通过: "+userName+"领取卡了新的等级卡"+cardName);
                }

                int execute1 = db().update(CARD_EXAMINE)
                    .set(CARD_EXAMINE.REFUSE_DESC, desc.toString())
                    .set(CARD_EXAMINE.REFUSE_TIME, DateUtils.getLocalDateTime())
                    .set(CARD_EXAMINE.STATUS, CardVerifyConstant.VSTAT_REFUSED)
                    .set(CARD_EXAMINE.DEL_FLAG,CardVerifyConstant.VDF_YES)
                    .where(CARD_EXAMINE.CARD_ID.in(cardIds).and(CARD_EXAMINE.USER_ID.eq(userId)))
                    .and(CARD_EXAMINE.DEL_FLAG.eq(CardVerifyConstant.VDF_NO))
                    .execute();

                logger().info("CARD_EXAMINE执行{}行",execute1);

            }
        });
    }

	/**
	 * 获取系统中所有可用的等级卡信息
	 * @return List<CardBasicVo> 等级卡基本信息列表
	 */
	public List<CardBasicVo> getAllAvailableGradeCards(){
		logger().info("获取可用等级卡会员卡的基本信息");
		return dao.getAllAvailableGradeCards();
	}

	/**
	 * 	查询有效等级卡，简单信息
	 * @return 等级卡List,该等价卡包括Id,name,grade信息
	 */
	public List<Map<String, Object>> getAllValidGradeCardList() {
		return dao.getAllValidGradeCardList();
	}
}
