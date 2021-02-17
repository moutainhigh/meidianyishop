package com.meidianyi.shop.service.shop.member.dao;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.CardConsumerRecord;
import com.meidianyi.shop.db.shop.tables.records.CardUpgradeRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardVo;
import com.meidianyi.shop.service.pojo.shop.member.account.UserIdAndCardIdParam;
import com.meidianyi.shop.service.pojo.shop.member.account.WxAppUserCardVo;
import com.meidianyi.shop.service.pojo.shop.member.bo.UserCardGradePriceBo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBgBean;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.EffectTimeBean;
import com.meidianyi.shop.service.pojo.shop.member.card.EffectTimeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.SearchCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardConsumeBean;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.member.card.base.CardMarketActivity;
import com.meidianyi.shop.service.pojo.shop.member.card.base.UserCardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.ucard.DefaultCardParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import com.meidianyi.shop.service.shop.card.CardDetailService;
import com.meidianyi.shop.service.shop.card.CardFreeShipService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.member.card.GradeCardService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep3;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.CARD_CONSUMER;
import static com.meidianyi.shop.db.shop.Tables.CARD_EXAMINE;
import static com.meidianyi.shop.db.shop.Tables.CHARGE_MONEY;
import static com.meidianyi.shop.db.shop.Tables.GRADE_PRD;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.SHOP_CFG;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.AVAILABLE_IN_STORE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ACT_NO;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ACT_YES;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_DURING;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_FIX;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_FOREVER;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_TP_ALL;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_TP_GRADE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.UCARD_FG_STOP;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.UCARD_FG_USING;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
* @author 黄壮壮
* @Date: 2019年10月10日
* @Description:
*/

@Service
public class UserCardDaoService extends ShopBaseService{
	public final static Byte CARD_ONLINE = 0;
    public final static Byte CARD_OFFLINE = 1;
	@Autowired private  UserCardService userCardService;
	@Autowired private CardFreeShipService freeshipSvc;
    @Autowired private GradeCardService gradeCardService;
	@Autowired private CardDetailService cardDetailSvc;

    public UserCardRecord getCardRecordByNo(String cardNo) {
        return db().selectFrom(USER_CARD)
            .where(USER_CARD.CARD_NO.eq(cardNo))
            .fetchOne();
    }

    /**
	 * 获取用户持有的等级卡
	 */
	public MemberCardRecord getUserGradeCard(Integer userId) {
		return  db().select(MEMBER_CARD.asterisk())
				.from(USER_CARD.leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(USER_CARD.CARD_ID)))
				.where(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
				.and(USER_CARD.USER_ID.eq(userId))
				.and(USER_CARD.FLAG.eq(UCARD_FG_USING))
				.fetchAnyInto(MemberCardRecord.class);
	}

	/**
	 * 获取会员卡
	 * @param cardId 会员卡Id
	 */
	public MemberCardRecord getMemberCardById(Integer cardId) {
		  MemberCardRecord card  = db().selectFrom(MEMBER_CARD).where(MEMBER_CARD.ID.eq(cardId)).fetchAny();
		  if(card != null){
              if(!StringUtils.isBlank(card.getBgImg())) {
                  String imageUrl = saas.getShopApp(getShopId()).image.imageUrl(card.getBgImg());
                  card.setBgImg(imageUrl);
              }

              if(StringUtils.isBlank(card.getBgColor())) {
                  card.setBgColor(CardUtil.getDefaultBgColor());
              }
          }
		  return card;
	}

	/**
	 * 更新userCard
	 */
	public void updateUserCard(Integer userId,Integer cardId) {
		db().update(USER_CARD.leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(USER_CARD.CARD_ID)))
			.set(USER_CARD.CARD_ID,cardId).set(USER_CARD.FLAG,UCARD_FG_USING)
			.where(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
			.and(USER_CARD.USER_ID.eq(userId))
			.execute();
	}

	/**
	 * 插入会员持卡升级记录
	 */
	public void insertIntoCardUpGrade(CardUpgradeRecord r) {

		db().executeInsert(r);
	}

	/**
	 * 获取店铺的积分限制配置信息
	 */
	public String getScoreLimit() {
		String val = "score_limit";
		ShopCfgRecord res = db().selectFrom(SHOP_CFG).where(SHOP_CFG.K.eq(val)).fetchAny();
		if(res != null) {
			return res.getV();
		}else {
			return null;
		}

    }


	public void updateCardFlag(List<Integer> cardIdList, List<String> cardNoList) {
		db().update(USER_CARD)
			.set(USER_CARD.FLAG, UCARD_FG_STOP)
			.where(USER_CARD.CARD_ID.in(cardIdList))
			.and(USER_CARD.CARD_NO.notIn(cardNoList))
			.execute();
	}

	/**
	 * 获取可用的用户卡
	 */
	public UserCardRecord getUsableUserCard(Integer userId, UserCardParam card) {
		SelectConditionStep<UserCardRecord> sql = db().selectFrom(USER_CARD)
			.where(USER_CARD.CARD_ID.eq(userId))
			.and(USER_CARD.CARD_ID.eq(card.getCardId()))
			.and(USER_CARD.FLAG.eq(UCARD_FG_USING));
		if(StringUtils.isBlank(card.getCardName())) {
			sql.and((USER_CARD.EXPIRE_TIME.isNull()).or(USER_CARD.EXPIRE_TIME.ge(DateUtils.getLocalDateTime())));
		}

		 return sql.fetchAny();
	}

	/**
	 * 	获取可用的用户卡(未过期，未废除)
	 */
	public UserCardRecord getUsableUserCard(Integer userId, Integer cardId) {
		return db().selectFrom(USER_CARD)
					.where(USER_CARD.CARD_ID.eq(userId))
					.and(USER_CARD.CARD_ID.eq(cardId))
					.and(USER_CARD.FLAG.eq(UCARD_FG_USING))
					.and((USER_CARD.EXPIRE_TIME.isNull()).or(USER_CARD.EXPIRE_TIME.ge(DateUtils.getLocalDateTime())))
					.fetchAny();
	}


	/**
	 * 获取有效用户会员卡列表
	 */
	public List<ValidUserCardBean> getValidCardList(Integer userId){
		return getValidCardList(userId,MCARD_TP_ALL,CARD_ONLINE);
	}

	public List<ValidUserCardBean> getValidCardList(Integer userId,Byte cardType,Byte type){
		if(MCARD_TP_ALL.equals(cardType)) {
			return getAllValidCardList(userId);
		}else {
			return getValidCardList(userId,new Byte[] {cardType},type);
		}
	}

    /**
     * Gets store valid card list.获取门店有效用户会员卡列表
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the store valid card list
     */
    public List<ValidUserCardBean> getStoreValidCardList(Integer userId, Integer storeId) {
        List<ValidUserCardBean> result = getValidCardList(userId, BYTE_ZERO, BYTE_ZERO)
            // 门店只支持普通会员卡,type=0
            .stream().filter((e) -> BYTE_ZERO.equals(e.getCardType()))
            // 首先支持门店使用
            .filter(e -> BYTE_ONE.equals(e.getStoreUseSwitch()))
            // 其次是否包含指定门店
            .filter((c) -> isStoreAvalid(c.getStoreList(), storeId))
            .collect(toList());
        // 设置会员卡有效时间
        result.forEach(this::setCardEffectTime);
        logger().debug("用户【{}】在门店【{}】的有效会员卡列表：{}", userId, storeId, result);
        return result;
    }

    private void setCardEffectTime(ValidUserCardBean bean) {
        // 设置会员卡有效时间
        EffectTimeParam etParam = new EffectTimeParam();
        etParam.setStartTime(bean.getStartTime());
        etParam.setEndTime(bean.getEndTime());
        etParam.setCreateTime(bean.getCreateTime());
        etParam.setExpireTime(bean.getExpireTime());
        etParam.setExpireType(bean.getExpireType());
        EffectTimeBean etBean = CardUtil.getUserCardEffectTime(etParam);
        bean.setStartTime(etBean.getStartTime());
        bean.setEndTime(etBean.getEndTime());
        bean.setStartDate(etBean.getStartDate());
        bean.setEndDate(etBean.getEndDate());
        bean.setExpireType(etBean.getExpireType());
    }

    private boolean isStoreAvalid(String s, Integer storeId) {
        List<Integer> list = Util.json2Object(s, new TypeReference<List<Integer>>() {
        }, false);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        if (list.size() == 1 && list.get(INTEGER_ZERO).equals(INTEGER_ZERO)) {
            // 说明为全部门店
            return true;
        }
        return list.contains(storeId);
    }

    /**
     * Check store valid card boolean.校验会员卡的有效性
     *
     * @param userId  the user id
     * @param storeId the store id
     * @param cardNo  the card no
     * @return the boolean
     */
    public boolean checkStoreValidCard(Integer userId, Integer storeId, String cardNo) {
        return getStoreValidCardList(userId, storeId).stream()
            .anyMatch((e) -> cardNo.equals(e.getCardNo()));
    }

	public List<ValidUserCardBean> getValidCardList(Integer userId,Byte[] cardType,Byte type) {
		logger().info("获取有效会员卡");
		assert cardType != null : "card type should not be null";
		if(cardType.length==1 && MCARD_TP_ALL.equals(cardType[0])) {
			// 所有可用卡
            return getAllValidCardList(userId);
		}
		if(CARD_OFFLINE.equals(type)) {
			// 线下处理
            return getOfflineValidCardList(userId, cardType);
		}
		// 线上
		return getOnlineValidCardList(userId, cardType);
	}

    private List<ValidUserCardBean> getOnlineValidCardList(Integer userId, Byte[] cardType){
		return selectValidCardCondition(userId, cardType)
				.orderBy(USER_CARD.IS_DEFAULT.desc(),MEMBER_CARD.GRADE.desc())
				.fetchInto(ValidUserCardBean.class);
	}

    private List<ValidUserCardBean> getOfflineValidCardList(Integer userId, Byte[] cardType) {
		return selectValidCardCondition(userId, cardType)
							.and(MEMBER_CARD.STORE_USE_SWITCH.eq(AVAILABLE_IN_STORE))
							.orderBy(USER_CARD.IS_DEFAULT.desc(),MEMBER_CARD.GRADE.desc())
							.fetchInto(ValidUserCardBean.class);
	}

	private SelectConditionStep<Record> selectValidCardCondition(Integer userId, Byte[] cardType) {
		return selectValidCardSql().where(USER_CARD.USER_ID.eq(userId))
							.and(USER_CARD.FLAG.eq(UCARD_FG_USING))
							.and(MEMBER_CARD.CARD_TYPE.in(cardType))
							.and(
									(USER_CARD.EXPIRE_TIME.greaterThan(DateUtils.getLocalDateTime()).or(USER_CARD.EXPIRE_TIME.isNull()))
									.or(MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FOREVER))
								)
							.and(
									(MEMBER_CARD.USE_TIME.in(userCardService.useInDate()))
									.or(MEMBER_CARD.USE_TIME.isNull())
								)
							.and(
									((MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FIX)).and(MEMBER_CARD.START_TIME.le(DateUtils.getLocalDateTime())))
									.or(MEMBER_CARD.EXPIRE_TYPE.in(MCARD_ET_DURING,MCARD_ET_FOREVER))
								)
							.and(
									(MEMBER_CARD.ACTIVATION.eq(MCARD_ACT_YES).and(USER_CARD.ACTIVATION_TIME.isNotNull()))
									.or(MEMBER_CARD.ACTIVATION.eq(MCARD_ACT_NO))
									.or(MEMBER_CARD.ACTIVATION_CFG.isNull())
								);
	}

	/**
	 * 获取用户所有的可用卡列表
	 */
	private List<ValidUserCardBean> getAllValidCardList(Integer userId) {

        return selectValidCardSql()
			.where(USER_CARD.USER_ID.eq(userId))
			.and(USER_CARD.FLAG.in(CardConstant.UCARD_FG_USING,CardConstant.UCARD_FG_GIVING))
			.and(
					(USER_CARD.EXPIRE_TIME.isNull())
					.or(USER_CARD.EXPIRE_TIME.greaterThan(DateUtils.getLocalDateTime()))
				)
            .and(
					((MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FIX)).and(MEMBER_CARD.START_TIME.le(DateUtils.getLocalDateTime())))
					.or(MEMBER_CARD.EXPIRE_TYPE.in(MCARD_ET_DURING,MCARD_ET_FOREVER))
				)
			.orderBy(USER_CARD.IS_DEFAULT.desc(),MEMBER_CARD.GRADE.desc())
			.fetchInto(ValidUserCardBean.class);
	}

    /**
	 * 查询用户有效卡的信息
	 */
	private SelectJoinStep<Record> selectValidCardSql() {
		 return db().select(USER_CARD.fields()).select(MEMBER_CARD.CARD_NAME,MEMBER_CARD.CARD_TYPE,MEMBER_CARD.DISCOUNT,MEMBER_CARD.BG_TYPE,MEMBER_CARD.BG_COLOR,
				MEMBER_CARD.BG_IMG,MEMBER_CARD.BUY_SCORE,MEMBER_CARD.EXPIRE_TYPE,MEMBER_CARD.START_TIME,MEMBER_CARD.END_TIME,MEMBER_CARD.RECEIVE_DAY,
				MEMBER_CARD.DATE_TYPE,MEMBER_CARD.STORE_USE_SWITCH,MEMBER_CARD.STORE_LIST,MEMBER_CARD.ACTIVATION,MEMBER_CARD.GRADE)
			.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)));
	}

    /**
	 * 获取用户持有会员卡的等级
	 */
	public String getUserCardGrade(Integer userId) {
		return db().select(MEMBER_CARD.GRADE).from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
			.where(USER_CARD.USER_ID.eq(userId))
			.and(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
			.and(USER_CARD.FLAG.eq(UCARD_FG_USING))
            // 判断是否已经激活
            .and(MEMBER_CARD.FLAG.eq(CardConstant.MCARD_FLAG_USING))
            .and(MEMBER_CARD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(MEMBER_CARD.ACTIVATION.eq(CardConstant.MCARD_ACT_NO).or(MEMBER_CARD.ACTIVATION.eq(CardConstant.MCARD_ACT_YES).and(USER_CARD.ACTIVATION_TIME.isNotNull())))
            .fetchAnyInto(String.class);
	}

	/**
	 *	 获取用户等级
	 *	@return 等级 || null
	 */
    public String calcUserGrade(Integer userId) {
		return db().select(MEMBER_CARD.GRADE)
				.from(USER_CARD.leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(USER_CARD.CARD_ID)))
				.where(USER_CARD.FLAG.eq(UCARD_FG_USING))
				.and(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
				.and(USER_CARD.USER_ID.eq(userId))
                // 判断是否已经激活
                .and(MEMBER_CARD.FLAG.eq(CardConstant.MCARD_FLAG_USING))
                .and(MEMBER_CARD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
                .and(MEMBER_CARD.ACTIVATION.eq(CardConstant.MCARD_ACT_NO).or(MEMBER_CARD.ACTIVATION.eq(CardConstant.MCARD_ACT_YES).and(USER_CARD.ACTIVATION_TIME.isNotNull())))
				.fetchAnyInto(String.class);
    }

	/**
	 * 根据id获取用户名
	 */
	public String getUserName(Integer id) {
		return db().select(USER.USERNAME).from(USER).where(USER.USER_ID.eq(id)).fetchAnyInto(String.class);
	}

    /**
	 * 更新会员卡详情
	 */
	public int updateUserCardByNo(String cardNo,UserCardRecord record) {
		return  db().update(USER_CARD).set(record).where(USER_CARD.CARD_NO.eq(cardNo)).execute();
	}


    /**
	 * 获取会员卡详情
	 */
	public UserCardParam getUserCardInfo(String cardNo) {
		Record extracted = getUserCardInfoBycardNo(cardNo);
		UserCardParam param=null;
		if(extracted!=null) {
			param=extracted.into(WxAppUserCardVo.class);
		}
		return param;
	}

	public Record  getUserCardInfoBycardNo(String cardNo) {
		return wxUserCardSelectSql()
				.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
				.where(USER_CARD.CARD_NO.eq(cardNo)).fetchOne();
	}

	/**
	 * 获取会员卡详情
	 */
	public UserCardParam getUserCardInfo(Integer userId,Integer cardId) {
		return wxUserCardSelectSql()
				.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
				.where(USER_CARD.USER_ID.eq(userId))
				.and(USER_CARD.CARD_ID.eq(cardId))
				.fetchAnyInto(WxAppUserCardVo.class);
	}

    public int getHasSend(Integer cardId){
		return db().selectCount().from(USER_CARD).where(USER_CARD.CARD_ID.eq(cardId)).execute();
	}

    /**
	 * 升级卡
	 */
	public void updateUserRankCard(MemberCardRecord cardInfo,Integer userId) {
		db().update(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
			.set(USER_CARD.CARD_ID,cardInfo.getId())
			.set(USER_CARD.UPDATE_TIME, DateUtils.getLocalDateTime())
			.set(USER_CARD.FLAG,UCARD_FG_USING)
			.where(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
			.and(USER_CARD.USER_ID.eq(userId))
			.and(USER_CARD.FLAG.eq(UCARD_FG_USING))
			.execute();
	}


    /**
	 * 更新卡余额
	 */
	public int updateUserCardMoney(UserCardConsumeBean data, UserCardParam userInfo) {
		return db().update(USER_CARD)
			.set(USER_CARD.MONEY,userInfo.getMoney().add(data.getMoney()))
			.where(USER_CARD.CARD_NO.eq(data.getCardNo()))
			.execute();
	}
	/**
	 * 更新卡剩余次数
	 */
	public int updateUserCardSurplus(UserCardConsumeBean data, UserCardParam userInfo) {
		return db().update(USER_CARD)
			.set(USER_CARD.SURPLUS,userInfo.getSurplus()+data.getCount())
			.where(USER_CARD.CARD_NO.eq(data.getCardNo()))
			.execute();
	}

    /**
	 * 更新卡剩余兑换次数
	 */
	public int updateUserCardExchangePlus(UserCardConsumeBean data, UserCardParam userInfo) {
		return db().update(USER_CARD)
			.set(USER_CARD.EXCHANG_SURPLUS,userInfo.getExchangSurplus()+data.getExchangCount())
			.where(USER_CARD.CARD_NO.eq(data.getCardNo()))
			.execute();
	}

    /**
     * 	充值记录
     * @param userInfo
	 */
	public void insertIntoCharge(UserCardConsumeBean data) {
        db().newRecord(CHARGE_MONEY, data).insert();
	}
	/**
	 * 消费记录
	 */
	public void insertConsume(UserCardConsumeBean data) {
		CardConsumerRecord cardConsumer = db().newRecord(CARD_CONSUMER,data);
		if(data.getMoney() != null) {
			cardConsumer.setMoney(data.getMoney().abs());
		}
		cardConsumer.insert();
	}

    /**
	 * 获取用户会员卡列表
	 */
	public PageResult<WxAppUserCardVo> getCardList(SearchCardParam param) {

        SelectSeekStep3<Record, String, Byte, Timestamp> select = wxUserCardSelectSql()
				.from(USER_CARD)
				.leftJoin(MEMBER_CARD)
				.on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID))
				.where(USER_CARD.USER_ID.eq(param.getUserId()))
				.and(USER_CARD.FLAG.notEqual(CardConstant.UCARD_FG_STOP))
				.orderBy(MEMBER_CARD.GRADE.desc(),USER_CARD.IS_DEFAULT.desc(),USER_CARD.CREATE_TIME.desc());
        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), WxAppUserCardVo.class);
    }

	private SelectSelectStep<Record> wxUserCardSelectSql(){
		return db().select(
			 	USER_CARD.USER_ID,USER_CARD.CARD_ID,USER_CARD.FLAG.as("userCardFlag"),USER_CARD.CARD_NO,USER_CARD.EXPIRE_TIME,
			 	USER_CARD.IS_DEFAULT,USER_CARD.MONEY,USER_CARD.SURPLUS,USER_CARD.ACTIVATION_TIME,USER_CARD.EXCHANG_SURPLUS,
			 	USER_CARD.CREATE_TIME.as("userCardCreateTime"),USER_CARD.UPDATE_TIME.as("userCardUpdateTime"),USER_CARD.FREE_LIMIT,
			 	USER_CARD.FREE_NUM,
			 MEMBER_CARD.asterisk());
	}

    /**
	 * get card type
	 * @param cardNo
	 */
	public Byte getCardType(String cardNo) {
		return db().select(MEMBER_CARD.CARD_TYPE)
			.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
			.where(USER_CARD.CARD_NO.eq(cardNo))
			.fetchAnyInto(Byte.class);
	}

    public int calcNumCardById(Integer cardId) {
		return db().fetchCount(USER_CARD, USER_CARD.CARD_ID.eq(cardId));
	}

	public int updateUserGradeCardId(Integer userId,Integer cardId,Boolean isActivate) {
        String cardNo = userCardService.getCurrentAvalidGradeCardNo(userId);
        //  兼容： 先废除所有的之前的等价卡和待激活的等价卡
        gradeCardService.clearUserAllGrade(userId,cardId,isActivate);
		return  db().update(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
			.set(USER_CARD.CARD_ID,cardId)
            .set(USER_CARD.CREATE_TIME, DateUtils.getLocalDateTime())
			.set(USER_CARD.UPDATE_TIME, DateUtils.getLocalDateTime())
			.set(USER_CARD.FLAG,UCARD_FG_USING)
		    .set(USER_CARD.ACTIVATION_TIME,isActivate? DateUtils.getLocalDateTime():null)
			.where(USER_CARD.CARD_NO.eq(cardNo))
            .and(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
			.and(USER_CARD.USER_ID.eq(userId))
			.execute();
	}

	public int getNumHasSendUser(Integer userId, Integer cardId) {
		return db().fetchCount(USER_CARD, USER_CARD.USER_ID.eq(userId)
				   .and(USER_CARD.CARD_ID.eq(cardId))
				   .and(USER_CARD.FLAG.eq(DelFlag.NORMAL_VALUE))
				   .and(USER_CARD.EXPIRE_TIME.isNull().or(USER_CARD.EXPIRE_TIME.ge(DateUtils.getLocalDateTime())))
				   );
	}

	public List<UserCardGradePriceBo> getUserCartGradePrice(Integer userId, List<Integer> prdIdList) {
		return db().select(MEMBER_CARD.CARD_NAME, MEMBER_CARD.GRADE, GRADE_PRD.GOODS_ID, GRADE_PRD.PRD_ID,
						GRADE_PRD.GRADE_PRICE)
				.from(USER_CARD).leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)).leftJoin(GRADE_PRD)
				.on(GRADE_PRD.GRADE.eq(MEMBER_CARD.GRADE)).where(USER_CARD.FLAG.eq(CardConstant.UCARD_FG_USING))
				.and(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE)).and(USER_CARD.USER_ID.eq(userId))
				.and(GRADE_PRD.PRD_ID.in(prdIdList)).fetchInto(UserCardGradePriceBo.class);

	}

    public List<UserCardGradePriceBo> getUserCartGradePrice(String grade, List<Integer> prdIdList) {
        return db().select( GRADE_PRD.GOODS_ID, GRADE_PRD.PRD_ID,
            GRADE_PRD.GRADE_PRICE)
            .from(GRADE_PRD)
            .where(GRADE_PRD.PRD_ID.in(prdIdList).and(GRADE_PRD.GRADE.eq(grade)).and(GRADE_PRD.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(UserCardGradePriceBo.class);

    }

    /**
     * 王帅
     * 获取有效会员卡
     * @param cardNo
     * @return
     */
	public OrderMemberVo getValidByCardNo(String cardNo){
        ValidUserCardBean card = selectValidCardSql().where(USER_CARD.CARD_NO.eq(cardNo))
            .and(USER_CARD.FLAG.eq(UCARD_FG_USING))
            .and(
                (USER_CARD.EXPIRE_TIME.greaterThan(DateUtils.getLocalDateTime()))
                    .or(MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FOREVER))
            )
            .and(
                (MEMBER_CARD.USE_TIME.in(userCardService.useInDate()))
                    .or(MEMBER_CARD.USE_TIME.isNull())
            )
            .and(
                ((MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FIX)).and(MEMBER_CARD.START_TIME.le(DateUtils.getLocalDateTime())))
                    .or(MEMBER_CARD.EXPIRE_TYPE.in(MCARD_ET_DURING, MCARD_ET_FOREVER))
            )
            .and(
                (MEMBER_CARD.ACTIVATION.eq(MCARD_ACT_YES).and(USER_CARD.ACTIVATION_TIME.isNotNull()))
                    .or(MEMBER_CARD.ACTIVATION.eq(MCARD_ACT_NO))
                    .or(MEMBER_CARD.ACTIVATION_CFG.isNull())
            ).fetchAnyInto(ValidUserCardBean.class);

        if(card != null) {
        	// 处理卡的有效时间
            dealWithValidUserCardEffectimeAndBgImg(card);
            // 处理包邮信息
            dealWithValidUserCardFreeship(card);
            //	处理折扣不可与优惠券公用信息
            dealWithValidUserCardMarketActivity(card);
            return new OrderMemberVo().init(card);
        }
        return null;
    }

    private void dealWithValidUserCardMarketActivity(ValidUserCardBean card) {
		logger().info("处理有效卡的营销活动");
		MemberCardRecord param  = new MemberCardRecord();
		param.setDiscount(card.getDiscount());
		param.setCannotUseAction(card.getCannotUseAction());
		param.setCannotUseCoupon(card.getCannotUseCoupon());
		List<CardMarketActivity> marketActivities = cardDetailSvc.getMarketActivities(param);
		card.setMarketActivities(marketActivities);
		List<Integer> ids = new ArrayList<>();
		if(marketActivities!=null && marketActivities.size()>0) {
			for(CardMarketActivity act: marketActivities) {
				ids.add(act.ordinal());
			}
		}
		card.setMarketIdActivities(ids);
	}

	private void dealWithValidUserCardFreeship(ValidUserCardBean card) {
		logger().info("处理有效卡的包邮信息");
		// TODO 小程序端语言国际化处理
		String lang = null;
		UserCardParam param = new UserCardParam();
		BeanUtils.copyProperties(card,param);
		CardFreeship freeshipData = freeshipSvc.getFreeshipData(param, lang);
		card.setCardFreeShip(freeshipData);
	}

	private void dealWithValidUserCardEffectimeAndBgImg(ValidUserCardBean card) {
		logger().info("计算卡的有效时间和背景");
		card.setAvatar(userCardService.getCardAvatar());
        // 快照时间
        EffectTimeParam etParam = new EffectTimeParam();
        BeanUtils.copyProperties(card, etParam);
        EffectTimeBean etBean = CardUtil.getUserCardEffectTime(etParam);
        BeanUtils.copyProperties(etBean, card);
        // 背景处理
        CardBgBean bg = saas.getShopApp(getShopId()).member.card.getBackground(card.getBgType(), card.getBgColor(), card.getBgImg());
        BeanUtils.copyProperties(bg, card);
	}

	/**
     * 王帅
     * 获取等级卡
     * @param userId id
     * @return result
     */
    public OrderMemberVo getOrderGradeCard(Integer userId){
        List<ValidUserCardBean> validCardList = getValidCardList(userId,
            new Byte[] {CardConstant.MCARD_TP_GRADE },
            OrderConstant.MEMBER_CARD_ONLINE);
        if(CollectionUtils.isEmpty(validCardList)) {
            return null;
        }else {
            //等级卡只有一个
            ValidUserCardBean card = validCardList.get(0);
            //处理卡的有效时间
            dealWithValidUserCardEffectimeAndBgImg(card);
            // 处理卡的包邮信息
            dealWithValidUserCardFreeship(card);
            //	处理折扣不可与优惠券公用信息
            dealWithValidUserCardMarketActivity(card);
            return new OrderMemberVo().init(card);
        }
    }

    /**
     * 获取营销会员卡（转化）
     * @param userId 用户id
     * @param cardType 卡类型
     * @param type 0线上 1线下
     * @return result
     */
    public List<OrderMemberVo> getOrderMembers(Integer userId,Byte[] cardType,Byte type) {
        List<ValidUserCardBean> validCardList = getValidCardList(userId, cardType, type);
        // 会员卡头像处理

        for(ValidUserCardBean card: validCardList) {
        	// 处理卡的有效时间
        	dealWithValidUserCardEffectimeAndBgImg(card);
        	// 处理卡的包邮信息
            dealWithValidUserCardFreeship(card);
            //  处理折扣不可与优惠券公用信息
            dealWithValidUserCardMarketActivity(card);
        }
        if(CollectionUtils.isEmpty(validCardList)){
            return Lists.newArrayList();
        }
        List<OrderMemberVo> result = new ArrayList<>(validCardList.size());
        for (ValidUserCardBean card : validCardList) {
            result.add(new OrderMemberVo().init(card));
        }
        return result;
    }

	/**
	 * 获取持有会员卡的用户id
	 */
	public List<Integer> getUserIdThatHasValidCard() {
		Timestamp localDateTime = DateUtils.getLocalDateTime();
		return db().select(USER_CARD.USER_ID)
				.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
				.where(USER_CARD.FLAG.eq(UCARD_FG_USING))
				.and(USER_CARD.EXPIRE_TIME.greaterThan(localDateTime).or(MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FOREVER)))
				.and((MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FIX).and(MEMBER_CARD.START_TIME.le(localDateTime)))
						.or(MEMBER_CARD.EXPIRE_TYPE.in(MCARD_ET_DURING, MCARD_ET_FOREVER)))
				.fetch().getValues(USER_CARD.USER_ID);
	}

	/**
	 * 废除会员卡
	 */
	public void repealCardByCardNo(String cardNo) {
		int res = db().update(USER_CARD).set(USER_CARD.FLAG,UCARD_FG_STOP)
						.set(USER_CARD.UPDATE_TIME, DateUtils.getLocalDateTime())
						.where(USER_CARD.CARD_NO.eq(cardNo))
						.execute();
		logger().info("废除会员卡: "+cardNo+" "+res+"张");
	}

    /**
     * Gets card bal and dis.获取会员卡余额和折扣
     *
     * @param cardNo the card no
     * @return the card bal and dis
     */
    public Record2<BigDecimal, BigDecimal> getCardBalAndDis(String cardNo) {
        return db().select(USER_CARD.MONEY, MEMBER_CARD.DISCOUNT).from(USER_CARD).leftJoin(MEMBER_CARD)
            .on(Tables.USER_CARD.CARD_ID.eq(Tables.MEMBER_CARD.ID)).where(USER_CARD.CARD_NO.eq(cardNo)).fetchAny();
    }

    public UserCardVo getUserCardJudge(UserIdAndCardIdParam param) {
    	logger().info("查询用户"+param.getUserId()+"的会员卡"+param.getCardId());
    	Condition condition = DSL.noCondition();
    	if(param.getUserId()!=null) {
    		condition = condition.and(USER_CARD.USER_ID.eq(param.getUserId()));
    	}
    	if(param.getCardId() != null) {
    		condition = condition.and(USER_CARD.CARD_ID.eq(param.getCardId()));
    	}

    	return getUserCard(condition);
    }

    public UserCardVo getUserCardByCardNo(String cardNo) {
    	Condition condition = DSL.noCondition();
    	if(!StringUtils.isBlank(cardNo)) {
    		condition = condition.and(USER_CARD.CARD_NO.eq(cardNo));
    	}
    	return getUserCard(condition);
    }

    public UserCardRecord getCardByUserCardId(Integer userId,Integer cardId) {
    	UserCardRecord res = db().select(USER_CARD.CARD_ID,USER_CARD.CARD_NO).from(USER_CARD)
    		.where(USER_CARD.USER_ID.eq(userId))
    		.and(USER_CARD.CARD_ID.eq(cardId))
    		.and(USER_CARD.FLAG.eq(CardConstant.UCARD_FG_USING))
            .fetchAnyInto(USER_CARD);
    	return res;
    }


    public UserCardVo getUserCard(Condition condition) {

		return db().select(USER_CARD.USER_ID,USER_CARD.CARD_ID,USER_CARD.FLAG.as("uFlag"),USER_CARD.CARD_NO,USER_CARD.EXPIRE_TIME,USER_CARD.IS_DEFAULT,
				USER_CARD.MONEY,USER_CARD.SURPLUS,USER_CARD.EXCHANG_SURPLUS,USER_CARD.ACTIVATION_TIME,USER_CARD.CREATE_TIME.as("uCreateTime"),
				USER_CARD.FREE_LIMIT,USER_CARD.FREE_NUM,MEMBER_CARD.asterisk())
			.from(USER_CARD.leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID)))
			.where(condition)
			.and(USER_CARD.FLAG.eq(CardConstant.UCARD_FG_USING))
			.fetchAnyInto(UserCardVo.class);
	}

	public int getHasSendUser(UserIdAndCardIdParam param) {
		int res = db().fetchCount(USER_CARD, USER_CARD.CARD_ID.eq(param.getCardId()).and(USER_CARD.USER_ID.eq(param.getUserId())));
		logger().info("获取这张卡(id="+param.getCardId()+")已经发送给用户("+param.getUserId()+")"+res+"张");
		return res;
	}

	public void updateActivationTime(String cardNo, Timestamp time) {
		db().update(USER_CARD).set(USER_CARD.ACTIVATION_TIME, time).where(USER_CARD.CARD_NO.eq(cardNo)).execute();
	}

	public void updateIsDefault(Condition condition, Byte defaultCardSignal) {
		db().update(USER_CARD).set(USER_CARD.IS_DEFAULT, defaultCardSignal).where(condition).execute();
	}
	/**
	 * 获取查询user card有效的卡条件
	 * @return
	 */
	public Condition getUserCardValidCondition() {
		Condition condition = DSL.noCondition();

		// 卡使用中，未被废除
		condition = condition.and(USER_CARD.FLAG.eq(UCARD_FG_USING));

		// 卡未过期，或卡本身是永久有效
		condition = condition.and(
				(USER_CARD.EXPIRE_TIME.greaterThan(DateUtils.getLocalDateTime()))
                .or(MEMBER_CARD.EXPIRE_TYPE.eq(MCARD_ET_FOREVER)));

		// 卡的使用时间，针对限次卡
		condition = condition.and(
				(MEMBER_CARD.USE_TIME.in(userCardService.useInDate()))
                .or(MEMBER_CARD.USE_TIME.isNull())
				);
		return condition;
	}

	/**
	 * 	更新用户卡为停止使用
	 */
	public int updateUserCardFlag(DefaultCardParam param) {
		logger().info("更新用户卡为停止使用");
		return db().update(USER_CARD)
			.set(USER_CARD.FLAG,CardConstant.UCARD_FG_STOP)
			.where(USER_CARD.USER_ID.eq(param.getUserId()))
			.and(USER_CARD.CARD_NO.eq(param.getCardNo()))
			.execute();
	}
	/**
	 * 	获取已经领取该卡的用户数量
	 * @param cardId 卡ID
	 */
	public int getCardNum(Integer cardId) {

		logger().info("查询领取该卡的用户数量");
		return getSelectRecieveCardSql(cardId,true)
					.orderBy(DSL.max(USER_CARD.CREATE_TIME).desc())
					.fetchOne(0, int.class);
	}

	/**
	 *	 查询该卡被领取的数量
	 */
	public int getCardUserList(Integer cardId) {
		logger().info("查询该卡被领取的数量");
		return getSelectRecieveCardSql(cardId).fetchOne(0, int.class);
	}

	/**
	 * 	查询可以该卡可以正常使用的数量
	 */
	public int getCanUseCardNum(Integer cardId,boolean isNeedActive) {
		logger().info("查询可以该卡可以正常使用的数量");
		Condition condition = DSL.noCondition().and(USER_CARD.FLAG.eq(CardConstant.UCARD_FG_USING));
		if(isNeedActive) {
			// 卡只有激活后才能正常使用
			condition = condition.and(USER_CARD.ACTIVATION_TIME.isNotNull());
		}
		return getSelectRecieveCardSql(cardId)
					.and(condition)
					.fetchOne(0, int.class);
	}

	/**
	 * 	查询已经发送出去的卡Sql
	 */
	private SelectConditionStep<Record2<Integer, Timestamp>> getSelectRecieveCardSql(Integer cardId){
		return getSelectRecieveCardSql(cardId,false);
	}
	private SelectConditionStep<Record2<Integer, Timestamp>> getSelectRecieveCardSql(Integer cardId,boolean isDistinctUserId) {
		Select<?> subQuery = db().select(DSL.max(CARD_EXAMINE.ID).as(CARD_EXAMINE.ID),CARD_EXAMINE.CARD_NO).from(CARD_EXAMINE).groupBy(CARD_EXAMINE.CARD_NO);

		AggregateFunction<Integer> count = DSL.count();
		if(isDistinctUserId) {
			count = DSL.countDistinct(USER_CARD.USER_ID);
		}
		SelectConditionStep<Record2<Integer, Timestamp>> query = db().select(count,DSL.max(USER_CARD.CREATE_TIME)).from(USER_CARD)
			.leftJoin(subQuery.asTable("a")).on(DSL.field("a.card_no").eq(USER_CARD.CARD_NO))
			.leftJoin(CARD_EXAMINE.asTable("b")).on(DSL.field("b.id").eq(DSL.field("a.id")))
			.leftJoin(USER).on(USER.USER_ID.eq(USER_CARD.USER_ID))
			.where(USER_CARD.CARD_ID.eq(cardId));
		return query;
	}


	/**
	 * 	设置所有用户的等级卡为废除状态
	 */
	public int setAllUserGradeCardDelete(Integer cardId) {
		logger().info("设置所有用户的等级卡为废除状态");
		int num = db().update(USER_CARD)
			.set(USER_CARD.FLAG,UserCardConstant.FLAG_DEL)
			.where(USER_CARD.CARD_ID.eq(cardId))
			.and(USER_CARD.FLAG.notEqual(UserCardConstant.FLAG_DEL))
			.execute();
		return num;
	}

	/**
	 * 	获取正在使用该卡的用户Id列表
	 * @return List<Integer> 用户ID列表
	 */
	public List<Integer> getUserIdsUsingCard(Integer cardId){
		return  db().select(USER_CARD.USER_ID)
					.from(USER_CARD)
					.where(USER_CARD.CARD_ID.eq(cardId))
					.and(USER_CARD.FLAG.notEqual(UserCardConstant.FLAG_DEL))
					.fetchInto(Integer.class);
	}
}
