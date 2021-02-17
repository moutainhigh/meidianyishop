package com.meidianyi.shop.service.shop.member;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.*;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorSpendVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.member.account.*;
import com.meidianyi.shop.service.pojo.shop.member.bo.UserCardGradePriceBo;
import com.meidianyi.shop.service.pojo.shop.member.builder.ChargeMoneyRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.builder.MemberCardRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.builder.UserCardParamBuilder;
import com.meidianyi.shop.service.pojo.shop.member.builder.UserCardRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.member.buy.*;
import com.meidianyi.shop.service.pojo.shop.member.card.*;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomRights;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.exception.*;
import com.meidianyi.shop.service.pojo.shop.member.order.UserOrderBean;
import com.meidianyi.shop.service.pojo.shop.member.ucard.DefaultCardParam;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.virtual.AnalysisParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.AnalysisVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import com.meidianyi.shop.service.pojo.wxapp.card.CardUpgradeVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.member.card.GeneralUserCardVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.process.DefaultMarketingProcess;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.shop.card.CardFreeShipService;
import com.meidianyi.shop.service.shop.card.msg.CardMsgNoticeService;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardDetailService;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardGiveAwaySerivce;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.distribution.DistributorLevelService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.card.BaseCardUserOpt;
import com.meidianyi.shop.service.shop.member.card.GradeBaseCardOpt;
import com.meidianyi.shop.service.shop.member.card.LimitBaseCardOpt;
import com.meidianyi.shop.service.shop.member.card.NormalBaseCardOpt;
import com.meidianyi.shop.service.shop.member.dao.CardDaoService;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.order.action.base.Calculate;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.trade.TradesRecordService;
import com.meidianyi.shop.service.shop.order.virtual.MemberCardOrderService;
import com.meidianyi.shop.service.shop.order.virtual.VirtualOrderService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import jodd.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.MemberCard.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserCard.USER_CARD;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.*;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.*;
import static com.meidianyi.shop.service.pojo.shop.payment.PayCode.PAY_CODE_BALANCE_PAY;
import static com.meidianyi.shop.service.shop.order.virtual.MemberCardOrderService.MEMBER_CARD_ORDER_SN_PREFIX;
import static com.meidianyi.shop.service.shop.order.virtual.VirtualOrderService.*;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * @author 黄壮壮
 * @Date: 2019年10月10日
 * @Description:
 */
@Service
public class UserCardService extends ShopBaseService {
    /** 升级 */
    final static Byte UPGRADE = 1;
    /** 检测有卡领取 */
    final static Byte TP_CHECK = 0;
    /** 领取 */
    final static Byte TP_RECEIVE_ONE = 1;
    final static Byte TP_RECEIVE_TWO = 2;
    @Autowired
    public UserCardDaoService userCardDao;
    @Autowired
    public CardDaoService cardDao;
    @Autowired
    public ScoreService scoreService;
    @Autowired
    public MemberCardService memberCardService;
    @Autowired
    public MemberService memberService;
    @Autowired
    public DistributorLevelService distributorLevelService;
    @Autowired
    public TradesRecordService tradesRecord;
    @Autowired
    public StoreService storeService;
    @Autowired
    public GoodsService goodsService;
    @Autowired
    public CardVerifyService cardVerifyService;
    @Autowired
    public GoodsCardCoupleService goodsCardCoupleService;
    @Autowired
    private CardUpgradeService cardUpgradeService;
    @Autowired
    private Calculate calculate;
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;

    @Autowired
    private VirtualOrderService virtualOrderService;
    @Autowired
    private LimitBaseCardOpt limitCardOpt;
    @Autowired
    private NormalBaseCardOpt normalCardOpt;
    @Autowired
    private GradeBaseCardOpt gradeCardOpt;
    @Autowired
    private BaseCardUserOpt cardUserOpt;
    @Autowired
    private CardFreeShipService cardFreeShipSvc;
    @Autowired
    private UserService userService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private MpPaymentService mpPaymentService;
    @Autowired
    private MemberCardOrderService cardOrderService;

    @Autowired
    private AccountService accountService;
    @Autowired
    private WxCardDetailService wxCardDetailSvc;
    @Autowired
    private CardMsgNoticeService cardMsgSvc;
    @Autowired
    private WxCardGiveAwaySerivce wxCardGiveSvc;
    public static final String DESC = "score_open_card";

    /**
     * 返回会员等级-按照持有会员等级卡划分，若无持有等级会员卡，则返回默认最低等级
     */
    public String getUserGrade(Integer userId) {
        String userGrade = userCardDao.calcUserGrade(userId);
        return StringUtils.isBlank(userGrade) ? LOWEST_GRADE : userGrade;
    }

	/**
	 * 	用户目前的未被废除的等级卡
	 * @Return  true拥有 ，false 未拥有
	 */
	public boolean isHasAvailableGradeCard(Integer userId) {
        String grade = getCurrentAvalidGradeCard(userId);
		return !StringUtils.isBlank(grade);
	}
    /**
     * 用户目前未被废除的等级卡等级
     */
    public String getCurrentAvalidGradeCard(Integer userId){
        String grade = db().select(MEMBER_CARD.GRADE)
            .from(USER_CARD.leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(USER_CARD.CARD_ID)))
            .where(USER_CARD.USER_ID.eq(userId))
            .and(USER_CARD.FLAG.eq(UCARD_FG_USING))
            .and(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
            .fetchAnyInto(String.class);
        return grade;
    }
    /**
     *  获取用户当前可用的等级会员卡号
     * @param userId
     * @return
     */
	public String getCurrentAvalidGradeCardNo(Integer userId){
        return  db().select(USER_CARD.CARD_NO)
                    .from(USER_CARD.leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(USER_CARD.CARD_ID)))
                    .where(USER_CARD.USER_ID.eq(userId))
                    .and(USER_CARD.FLAG.eq(UCARD_FG_USING))
                    .and(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_GRADE))
                    .fetchAnyInto(String.class);
    }

	/**
	 * 	获取用户持有的等级卡
	 */
	public MemberCardRecord getUserGradeCard(Integer userId) {
		return userCardDao.getUserGradeCard(userId);
	}

	/**
	 * 	用户卡等级变动
	 */
	public void changeUserGradeCard(Integer userId, MemberCardRecord oldCard, MemberCardRecord newCard,
			String option,Boolean isActivate) {
		logger().info("用户会员卡升级");

		//	更新卡
		userCardDao.updateUserGradeCardId(userId, newCard.getId(),isActivate);
		//	保存用户卡等级变动信息
		cardUpgradeService.recordCardUpdateGrade(userId, oldCard, newCard, option);
		//	用户卡升级订阅消息通知
        cardMsgSvc.cardGradeChangeMsg(userId, oldCard, newCard, option);
		if (newCard.getSorce() != null && newCard.getSorce() > 0) {
			addUserCardScore(userId, newCard);
		}
	}

    /**
     * 会员卡升降级记录
     */
    public PageResult<CardUpgradeVo> getGradeList(SearchCardParam param) {
        logger().info("获取会员的升降级记录");
        PageResult<CardUpgradeVo> upgradeList = cardUpgradeService.getGradeList(param);
        List<CardUpgradeVo> upgradeListNew = new ArrayList<>();
        if(upgradeList.dataList !=null && upgradeList.dataList.size()>0) {
            UserCardRecord info = userCardDao.getCardByUserCardId(param.getUserId(), upgradeList.dataList.get(0).getNewCardId());
            if(info!=null) {
                Integer oldCardId = info.getCardId();
                for(CardUpgradeVo vo: upgradeList.dataList) {
                    if(oldCardId.equals(vo.getNewCardId())) {
                        upgradeListNew.add(vo);
                        oldCardId = vo.getOldCardId();
                    }else {
                        break;
                    }
                }
            }
        }
        upgradeList.setDataList(upgradeListNew);
        return upgradeList;
    }


    /**
     *  开卡赠送积分
     */
    public void addUserCardScore(Integer userId, MemberCardRecord card) {
        if (card.getSorce() == null || card.getSorce() <= 0) {
            return;
        }
        ScoreParam scoreParam = new ScoreParam();
        scoreParam.setUserId(userId);
        scoreParam.setScore(card.getSorce());
        scoreParam.setDesc("score_open_card");
        if(CardUtil.isGradeCard(card.getCardType())) {
            scoreParam.setRemarkCode(RemarkTemplate.CARD_UPGRADE.code);
        }else {
            scoreParam.setRemarkCode(RemarkTemplate.OPEN_CARD_SEND.code);
        }
        scoreParam.setExpiredTime(scoreService.getScoreExpireTime());

        try {
            scoreService.updateMemberScore(scoreParam, Integer.valueOf(DEFAULT_ADMIN.val()), TYPE_SCORE_CREATE_CARD.val(), TRADE_FLOW_IN.val());
        } catch (MpException e) {
            logger().info("卡赠送积分失败");
        }
    }

    /**
     * 	会员卡升级检测并升级
     *
     * @param type 是否领取 1领取 0只是检测
     * @return cardId（当type为0时为检测可升级的卡id,type为1时为领取后的卡id),0为没有可升级的卡
     */
    public Integer updateGrade(Integer userId, Integer cardId, Byte type) throws MpException {
        Assert.isTrue(userId != null, "userId required");

        if (cardId != null) {
            // 直接升级
            updateUserGradeCard(userId, cardId);
            return 0;
        } else {
            if (TP_RECEIVE_ONE.equals(type) || TP_RECEIVE_TWO.equals(type)) {
                // 检测并升级
                cardId = checkAndUpgradeUserCard(userId);
            } else if (TP_CHECK.equals(type)) {
                // 检测可升级到的卡
                cardId = checkCardCanUpgrade(userId);
            }
            if (cardId == null) {
                logger().info("没有可升级的等级会员卡");
                return 0;
            } else if (type == 0) {
                logger().info(String.format("检测到可领取等级卡 %d", cardId));
                // 仅仅检测是否可领取等级卡
                return cardId;
            }else {
                return cardId;
            }
        }
    }

	private Integer checkAndUpgradeUserCard(Integer userId) throws MpException {
		logger().info("检测并升级卡");
		Integer cardId = null;
		// 获取用户累积获得积分和累积消费总额
		Integer userTotalScore = scoreService.getAccumulationScore(userId);
		BigDecimal amount = getUserTotalSpendAmount(userId);
		// 获取等级卡列表等级升序
		List<MemberCardRecord> gCardList = getAvailGradeCard();
		String uGrade = getCurrentAvalidGradeCard(userId);
		// 判断用户是否拥有等级卡
		if (StringUtils.isBlank(uGrade)) {
			// 用户第一次领取会员卡，给用户分配一级会员卡
			MemberCardRecord gCard = gCardList.get(0);
			logger().info("给用户分配等级卡: " + gCard.getCardName() + "等级: " + gCard.getGrade());
			// 升级条件
			GradeConditionJson gradeCondition = getGradeCondition(userTotalScore, amount, gCard);
			if (isSatisfyUpgradeCondition(userTotalScore, amount, gradeCondition)) {
				addUserCard(userId, gCard.getId());
			}
			uGrade = getCurrentAvalidGradeCard(userId);
		}
		logger().info("此时的会员卡等级："+uGrade);
		MemberCardRecord userGradeCard = userCardDao.getUserGradeCard(userId);
		cardId = userGradeCard.getId();

		boolean flag = false;
		MemberCardRecord oldGradeCard = null,newGradeCard = null;
		for (MemberCardRecord gCard : gCardList) {
			if (!StringUtils.isBlank(gCard.getGradeCondition())) {
				// 升级条件
				GradeConditionJson gradeCondition = getGradeCondition(userTotalScore, amount, gCard);
				// 等级卡的等级高于用户卡等级或者用户目前等级为空
				if (isCardGradeGtUserGrade(uGrade, gCard)) {
					if (isSatisfyUpgradeCondition(userTotalScore, amount, gradeCondition)) {
						cardId = gCard.getId();
						oldGradeCard = getUserGradeCard(userId);
						newGradeCard = gCard;
						flag = true;
					} else {
						break;
					}
				}
			}
		}
		//	升级
		if(flag) {
			String operation = "领取等级卡";
			changeUserGradeCard(userId, oldGradeCard, newGradeCard, operation,true);
		}
		return cardId;
	}

	/**
	 * 	检测可升级到的等级卡
	 */
	private Integer checkCardCanUpgrade(Integer userId) throws MemberCardNullException {
		String userCardGrade = getCurrentAvalidGradeCard(userId);
		final String uGrade;
		if(StringUtils.isBlank(userCardGrade)) {
			uGrade = CardConstant.LOWEST_GRADE;
		}else {
			uGrade = userCardGrade;
		}

		Integer cardId = null;
		if (!StringUtils.isBlank(uGrade)) {
			List<MemberCardRecord> gradeCard = getAvailGradeCard();
			gradeCard.removeIf(item->item.getGrade().compareTo(uGrade)<1);
			Integer userTotalScore = scoreService.getAccumulationScore(userId);
			BigDecimal amount = distributorLevelService.getTotalSpend(userId).getTotal();

            for (MemberCardRecord gCard : gradeCard) {
                GradeConditionJson gradeCondition = getGradeCondition(userTotalScore, amount, gCard);

                if (isSatisfyUpgradeCondition(userTotalScore, amount, gradeCondition)) {
                    cardId = gCard.getId();
                } else {
                    break;
                }
            }
        }
        logger().info("检测可升级到的等级卡"+cardId);
        return cardId;
    }

    private List<MemberCardRecord> getAvailGradeCard() throws MemberCardNullException {
        List<MemberCardRecord> gradeCard = cardDao.getAllUsingGradeCard();

        if (gradeCard.size() == 0) {
            logger().info("系统中没有等级卡");
            throw new MemberCardNullException(JsonResultCode.CODE_CARD_GRADE_NONE);
        }
        return gradeCard;
    }

    public GradeConditionJson getGradeCondition(Integer userTotalScore, BigDecimal amount, MemberCardRecord gCard) {

        GradeConditionJson gradeCondition = Util.parseJson(gCard.getGradeCondition(), GradeConditionJson.class);

		if (BigDecimalUtil.compareTo(gradeCondition.getGradeScore(), BigDecimal.ZERO) < 0) {
			//gradeCondition.setGradeScore(new BigDecimal(userTotalScore + 1000));
		}

		if (BigDecimalUtil.compareTo(gradeCondition.getGradeMoney(), BigDecimal.ZERO) < 0) {
			//gradeCondition.setGradeMoney(amount.add(new BigDecimal(1000)));
		}
		return gradeCondition;
	}

	/**
	 * 是否满足升级条件
	 */
	public boolean isSatisfyUpgradeCondition(Integer userTotalScore, BigDecimal amount,
			GradeConditionJson gradeCondition) {
	    boolean scoreCheck = false,moneyCheck = false;
	    if(gradeCondition.getGradeScore() != null){
	        scoreCheck = gradeCondition.getGradeScore().intValue() <= userTotalScore;
        }

	    if(gradeCondition.getGradeMoney()!=null){
	        moneyCheck = BigDecimalUtil.compareTo(gradeCondition.getGradeMoney(), amount) <= 0;
        }
		return scoreCheck || moneyCheck;
	}

    private boolean isCardGradeGtUserGrade(String uGrade, MemberCardRecord gCard) {
        return !StringUtils.isBlank(uGrade) && !StringUtils.isBlank(gCard.getGrade())
            && gCard.getGrade().compareTo(uGrade) > 0;
    }

	/**
	 * 用户卡升级
	 */
	private void updateUserGradeCard(Integer userId, Integer cardId) throws MemberCardNullException {
		// 等级卡升级
		if (isHasAvailableGradeCard(userId)) {
			MemberCardRecord oldGradeCard = getUserGradeCard(userId);
			MemberCardRecord newGradeCard = memberCardService.getCardById(cardId);
			String option = "Admin operation";
			changeUserGradeCard(userId, oldGradeCard, newGradeCard, option,false);
		} else {
			// 发放等级卡
			sendCard(userId, cardId);
		}
	}

    public List<String> addUserCard(Integer userId, Integer... cardId) throws MpException {
        List<UserCardParam> cardList = new ArrayList<>();
        for (Integer id : cardId) {
            cardList.add(UserCardParamBuilder.create().cardId(id).build());
        }
        return addUserCard(userId, cardList, UCARD_ACT_NO);
    }

    /**
     * 	添加会员卡
     *
     * @return
     */
    public List<String> addUserCard(Integer userId, List<UserCardParam> cardList, boolean isActivate)
        throws MpException {

        stopUserLimitCard(cardList);
        List<String> cardNoList = new ArrayList<>();
        for (UserCardParam card : cardList) {
            MemberCardRecord mCard = cardDao.getCardById(card.getCardId());
            if (CardUtil.isLimitCard(mCard.getCardType())) {
                if (canSendLimitCard(userId, mCard)) {
                    String cardNo = sendCard(userId, mCard, isActivate);
                    cardNoList.add(cardNo);
                } else {
                    logger().info("该限次卡领取次数用完");
                    throw new LimitCardAvailSendNoneException();
                }
            } else if (StringUtils.isBlank(card.getCardNo())) {
                String cardNo = sendCard(userId, mCard, isActivate);
                cardNoList.add(cardNo);
            } else {
                logger().info("一张卡不能重复领取，除非此卡过期或删除");
                throw new CardSendRepeatException();
            }
        }
        return cardNoList;
    }

    /**
     * 检测限次卡是否能发放
     *
     * @return true: 能,false：不可以
     */
    private boolean canSendLimitCard(Integer userId, MemberCardRecord mCard) {
        int numSendToUser = userCardDao.getNumHasSendUser(userId, mCard.getId());
        int totalSend = userCardDao.getHasSend(mCard.getId());
        // 检测每人领取次数,和允许发送的总量
        return !((mCard.getLimit() > 0 && numSendToUser >= mCard.getLimit())
            || (mCard.getStock() > 0 && totalSend >= mCard.getStock()));
    }

    private void stopUserLimitCard(List<UserCardParam> cardList) {
        logger().info("处理user_card中限次卡逻辑");
        List<String> cardNos = cardList.stream().map(UserCardParam::getCardNo).collect(Collectors.toList());
        List<Integer> timeCardsId = memberCardService.getCardIdByType(MCARD_TP_LIMIT);
        userCardDao.updateCardFlag(timeCardsId, cardNos);
    }

    private void sendCard(Integer userId, Integer cardId) throws MemberCardNullException {
        MemberCardRecord card = MemberCardRecordBuilder.create().id(cardId).build();
        sendCard(userId, card, UCARD_ACT_NO);
    }

    /**
     * 发卡
     */
    public String sendCard(Integer userId, MemberCardRecord card, boolean isActivate) throws MemberCardNullException {
        logger().info("给用户发送会员卡");
        if (CardUtil.isCardDeleted(card.getDelFlag())) {
            throw new MemberCardNullException();
        }
        UserCardRecord newCard = createNewUserCard(userId, card, isActivate);

        addChargeMoney(card, newCard);
        addUserCardScore(userId, card);
        sendScoreTemplateMsg(card);
        return newCard.getCardNo();
    }

    /**
     * 	发送模板消息
     */
    public void sendScoreTemplateMsg(MemberCardRecord card) {
        StringBuilder content = new StringBuilder();
        if (CardUtil.isLimitCard(card.getCardType())) {
            // TODO 国际化
            if (card.getIsExchang() != null && card.getIsExchang() > 0) {
                content.append(String.format("可兑换商品数量%d件,", card.getExchangCount()));
            } else if (card.getStoreUseSwitch() != null && card.getCount() > 0) {
                content.append(String.format("可使用门店服务%d次,", card.getCount()));
            }
            content.setLength(content.length() - 1);
        } else if (card.getDiscount() != null && card.getDiscount().intValue() > 0) {
            content.append(String.format("打%f折", card.getDiscount().floatValue()));
        }
        StringBuilder expireTimeMessage = new StringBuilder();
        if (calcCardExpireTime(card) == null) {
            expireTimeMessage.append("card.effective.forever");
        } else {
            expireTimeMessage.append("card.effective.expired");
        }
        // TODO 消息队列
    }

    /**
     * 	会员卡余额，门店，商品兑换次数记录
     * @param card
     * @param userCard
     */
    public void addChargeMoney(MemberCardRecord card, UserCardRecord userCard) {
        logger().info("生成会员卡余额，门店，商品兑换次数记录");
        if (CardUtil.isNormalCard(card.getCardType()) && card.getSendMoney() != null) {
        	//  发卡余额变动
            ChargeMoneyRecordBuilder builder = getPreparedChargeMoneyBuilder(card, userCard);
            builder.afterChargeMoney(userCard.getMoney()).changeType(CardConstant.CHARGE_SEND_CARD);
            builder.charge(new BigDecimal(card.getSendMoney())).reasonId(String.valueOf(RemarkTemplate.ADMIN_SEND_CARD.code)).build().insert();

        }
        if (CardUtil.isLimitCard(card.getCardType())) {
            if(CardUtil.canUseInStore(card.getStoreUseSwitch())) {
                // 发卡 - 门店服务次数
                ChargeMoneyRecordBuilder builder = getPreparedChargeMoneyBuilder(card, userCard);
                builder.count(card.getCount().shortValue()).reasonId(String.valueOf(RemarkTemplate.SEND_CARD_REASON.code));
                builder.build().insert();
            }

            if (CardUtil.canExchangGoods(card.getIsExchang())) {
                // 发卡 - 兑换商品数量
                ChargeMoneyRecordBuilder builder = getPreparedChargeMoneyBuilder(card, userCard);
                builder.count((short) 0).exchangCount(card.getExchangCount().shortValue()).reasonId(String.valueOf(RemarkTemplate.ADMIN_EXCHANGE_GOODS.code));
                builder.build().insert();
            }

        }
    }

    private ChargeMoneyRecordBuilder getPreparedChargeMoneyBuilder(MemberCardRecord card, UserCardRecord userCard) {
        ChargeMoneyRecordBuilder builder = ChargeMoneyRecordBuilder.create(db().newRecord(CHARGE_MONEY))
            .userId(userCard.getUserId()).cardId(userCard.getCardId()).type(card.getCardType())
            .cardNo(userCard.getCardNo()).payment("store.payment").createTime(DateUtils.getLocalDateTime());
        return builder;
    }

    private UserCardRecord createNewUserCard(Integer userId, MemberCardRecord card, boolean isActivate) {
        UserCardRecordBuilder cardBuilder = UserCardRecordBuilder.create(db().newRecord(USER_CARD)).userId(userId)
            .cardId(card.getId()).cardNo(getRandomCardNo(card.getId())).createTime(DateUtils.getLocalDateTime())
            .expireTime(calcCardExpireTime(card));

        if (CardUtil.isLimitCard(card.getCardType())) {
            cardBuilder.surplus(card.getCount());
            if (card.getIsExchang() != null) {
                cardBuilder.exchangSurplus(card.getExchangCount());
            }
        } else if (CardUtil.isNormalCard(card.getCardType())) {
            if (card.getSendMoney() != null) {
                cardBuilder.money(new BigDecimal(card.getSendMoney()));
            }
        } else if (CardUtil.isGradeCard(card.getCardType()) &&
            isHasAvailableGradeCard(userId)) {
            logger().info("即将更改用户的等级");
            MemberCardRecord oldGradeCard = getUserGradeCard(userId);
            MemberCardRecord newGradeCard = memberCardService.getCardById(card.getId());
            String option = "Admin operation";
            changeUserGradeCard(userId, oldGradeCard, newGradeCard, option,false);
        }

        if (isActivate || isActivateNow(card)) {
            cardBuilder.activationTime(DateUtils.getLocalDateTime());
        }

        int result = 0;
        if (CardUtil.isGradeCard(card.getCardType()) && !isHasAvailableGradeCard(userId)) {
            logger().info("用户目前没有等级卡，设置一个等级卡: " + card.getGrade());
            result = cardBuilder.build().insert();
        } else {
            logger().info("发送普通或者限次卡：" + card.getCardName());
            result = cardBuilder.build().insert();
        }
        logger().info(String.format("成功向ID为%d的用户，发送了%d张会员卡", userId, result));
        return cardBuilder.build();
    }


    public boolean isActivateNow(MemberCardRecord card) {
        return MCARD_ACT_NO.equals(card.getActivation());
    }

    public Timestamp calcCardExpireTime(MemberCardRecord card) {
        Assert.isTrue(card != null,"card should not be null");
        LocalDateTime expireTime = null;
        LocalDateTime now = LocalDateTime.now();
        if (isFixDate(card)) {
            return card.getEndTime();
        } else if (isDuringDay(card)) {
            expireTime = now.plusDays(card.getReceiveDay());
        } else if (isDuringWeek(card)) {
            expireTime = now.plusWeeks(card.getReceiveDay());
        } else if (isDuringMonth(card)) {
            expireTime = now.plusMonths(card.getReceiveDay());
        }
        return expireTime != null ? Timestamp.valueOf(expireTime) : null;
    }

    private boolean isDuring(MemberCardRecord card) {
        return MCARD_ET_DURING.equals(card.getExpireType());
    }

    private boolean isDuringDay(MemberCardRecord card) {
        return isDuring(card) && MCARD_DT_DAY.equals(card.getDateType());
    }

    private boolean isDuringWeek(MemberCardRecord card) {
        return isDuring(card) && MCARD_DT_WEEK.equals(card.getDateType());
    }

    private boolean isDuringMonth(MemberCardRecord card) {
        return isDuring(card) && MCARD_DT_MONTH.equals(card.getDateType());
    }

    private boolean isFixDate(MemberCardRecord card) {
        return MCARD_ET_FIX.equals(card.getExpireType());
    }

    /**
     * 计算用户卡使用的时间段
     */
    public List<Integer> useInDate() {
        LocalDate now = DateUtils.getLocalDate();

        DayOfWeek dayOfWeek = now.getDayOfWeek();
        List<Integer> inData = new ArrayList<>(Arrays.asList(new Integer[] { 0, 1 }));

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            inData.clear();
            inData.addAll(Arrays.asList(new Integer[] { 0, 2 }));
        }

        return inData;
    }

    /**
     * 更新会员卡详情
     */
    public int updateUserCardByNo(String cardNo, UserCardRecord record) {
        return userCardDao.updateUserCardByNo(cardNo, record);
    }

    /**
     * Get Random card_no
     */
    public String getRandomCardNo(int cardId) {
        return memberCardService.generateCardNo(cardId);
    }

    public void cardConsumer(UserCardConsumeBean data) {
        cardConsumer(data, 0, TYPE_DEFAULT.val(), TRADE_FLOW_IN.val(), (byte) 0, true);
    }

    /**
     * 增加会员卡消费记录
     *
     * @param tradeType  {@link com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum}
     * @param isContinue 卡余额时（次数或余额）在休息时间内（23:00-8:00）是否继续发送消息：true继续，false停止
     * @param type       修改类型 0: 卡余额；1：兑换商品次数，2： 兑换门店次数
     */
    public int cardConsumer(UserCardConsumeBean data, Integer adminUser, Byte tradeType, Byte tradeFlow, Byte type,
                            Boolean isContinue) {
        // 生成新的充值记录
        // 验证现有积分跟提交的积分是否一致
        UserCardParam userInfo = userCardDao.getUserCardInfo(data.getCardNo());
        if (CardUtil.isLimitCard(data.getType())) {
            // 限次卡
            if (NumberUtils.BYTE_ONE.equals(type)) {
                if (adminUser != null && (userInfo.getExchangSurplus() - data.getCountDis()) != 0) {
                    return -1;
                }
            } else {
                if (adminUser != null && (userInfo.getSurplus() - data.getCountDis()) != 0) {
                    return -1;
                }
            }
        } else {
            // 普通卡
            if (adminUser != null
                && BigDecimalUtil.subtrac(userInfo.getMoney(), userInfo.getMoney()).floatValue() != 0.00) {
                return -1;
            }
        }

        // serviceOrder

        data.setCreateTime(DateUtils.getLocalDateTime());
        if (StringUtils.isBlank(data.getReason())) {
            data.setReasonId(String.valueOf(RemarkTemplate.ADMIN_OPERATION.code));
        }
        if (CardUtil.isLimitCard(data.getType())) {
            if (NumberUtils.BYTE_ONE.equals(type)) {
                // 兑换商品次数
                data.setReasonId(String.valueOf(RemarkTemplate.ADMIN_EXCHANGE_GOODS.code));
            } else {
                // 兑换门店次数
                data.setReasonId(String.valueOf(RemarkTemplate.ADMIN_STORE_SERIVICE.code));
            }
        } else {
            // 卡余额变动
            data.setReasonId(String.valueOf(RemarkTemplate.ADMIN_CARD_ACCOUNT.code));
        }

        if (CardUtil.isLimitCard(data.getType())) {
            if (NumberUtils.BYTE_ONE.equals(type)) {
                if (data.getExchangCount() < 0) {
                    // 消费记录
                    userCardDao.insertConsume(data);
                } else {
                    // 充值记录
                    userCardDao.insertIntoCharge(data);
                }
            } else {
                if (data.getCount() < 0) {
                    // 消费记录
                    userCardDao.insertConsume(data);
                } else {
                    // 充值记录
                    userCardDao.insertIntoCharge(data);
                }
            }
        } else {
            if (data.getMoney().intValue() < 0) {
                userCardDao.insertConsume(data);
            } else {
                data.setCharge(data.getMoney());
                data.setAfterChargeMoney(userInfo.getMoney().add(data.getCharge()));
                userCardDao.insertIntoCharge(data);
            }
        }

        // 更新用户卡余额
        // type=1 次卡 0普通卡
        int ret = 0;
        if (data.getType() == (byte) 1) {
            if (type == (byte) 1) {
                ret = userCardDao.updateUserCardExchangePlus(data, userInfo);
                // TODO 模板消息
            } else {
                ret = userCardDao.updateUserCardSurplus(data, userInfo);
                // TODO 模板消息
            }
        } else {
            ret = userCardDao.updateUserCardMoney(data, userInfo);
            if (tradeType > TYPE_DEFAULT.val()) {
                // 插入交易记录
                TradeOptParam param = TradeOptParam.builder().build();
                // TODO
                // tradesRecord.insertTradesRecord(data, tradeType, tradeFlow);
            }
            // TODO 模板消息
        }
        return ret;
    }

    /**
     * 获取用户所有的会员卡列表
     *
     */
    public PageResult<WxAppUserCardVo> getAllCardsOfUser(SearchCardParam param,String lang) {
        logger().info("获取用户所有的会员卡列表");
        PageResult<WxAppUserCardVo> cardList = userCardDao.getCardList(param);
        String avatar = getCardAvatar();
        for (WxAppUserCardVo card : cardList.dataList) {
            dealWithWxUserCard(card, avatar);
            //过期检查
            setWxUserCardVoExpire(card);
            // 包邮信息
            dealWithFreeShipInfo(card,lang);
            if(CardConstant.UCARD_FG_GIVING.equals(card.getUserCardFlag())) {
            	//	处理转赠中的会员卡
            	wxCardGiveSvc.dealWithGivingCard(card.getCardNo());
            }
        }
        return cardList;
    }

    /**
     * 	处理卡的包邮信息
     */
    private void dealWithFreeShipInfo(WxAppUserCardVo card,String lang) {
        CardFreeship freeShip = cardFreeShipSvc.getFreeshipData(card, lang);
        card.setFreeshipDesc(freeShip.getDesc());
    }

    /**
     * 处理返回给微信端的用户卡数据
     *
     * @param card
     */
    private void dealWithWxUserCard(WxAppUserCardVo card, String avatar) {
        card.calcCardIsExpired();
        card.calcRenewal();
        card.setAvatar(avatar);
        card.calcCash();

        // 背景
        CardBgBean bg = memberCardService.getBackground(card.getBgType(), card.getBgColor(), card.getBgImg());
        BeanUtils.copyProperties(bg, card);

        // 用户卡的有效时间
        setWxUserCardVoExpire(card);

        // 设置卡是否过期状态
        card.setStatus(CardUtil.getStatus(card.getExpireType(), card.getEndTime()));
    }

    /**
     * 	设置WxUserCardVo的有效时间
     */
    private void setWxUserCardVoExpire(WxAppUserCardVo card) {
        EffectTimeParam etParam = new EffectTimeParam();
        BeanUtils.copyProperties(card, etParam);
        etParam.setCreateTime(card.getUserCardCreateTime());
        EffectTimeBean etBean = CardUtil.getUserCardEffectTime(etParam);
        BeanUtils.copyProperties(etBean, card);
    }

    /**
     * 获取用户卡的头像
     *
     * @return
     */
    public String getCardAvatar() {
        String relativePath = saas().shop.getShopAvatarById(this.getShopId());
        if(StringUtils.isBlank(relativePath)) {
            return null;
        }else {
            return saas().getShopApp(getShopId()).image.imageUrl(relativePath);
        }
    }

    public WxAppUserCardVo getUserCardDetail(UserCardParam param,String lang) throws UserCardNullException {
        return wxCardDetailSvc.getUserCardDetail(param, lang);
    }

    private NextGradeCardVo getNextGradeCard(String currentGrade) {
        // 升级进度
        logger().info("当前会员卡的等级：" + currentGrade);

        Integer gVal = Integer.valueOf(currentGrade.substring(1));
        gVal = gVal + 1;
        while (gVal < 10) {
            logger().info("设置会员升级的下一张等级卡");
            String newGrade = "v" + gVal;
            MemberCardRecord gradeCard = getGradeCardByGrade(newGrade);
            if(gradeCard==null) {
                gVal = gVal+1;
                continue;
            }
            RankCardToVo resCard = memberCardService.cardDetailSvc.changeToGradeCardDetail(gradeCard);
            NextGradeCardVo vo = new NextGradeCardVo();
            vo.setCardName(resCard.getCardName());
            vo.setPowerCount(resCard.getPowerCount());
            vo.setDiscount(resCard.getDiscount());
            vo.setSorce(resCard.getSorce());
            vo.setScoreJson(resCard.getScoreJson());
            vo.setGrade(resCard.getGrade());
            vo.setPowerScore(resCard.getPowerScore());
            vo.setGradeConditionJson(resCard.getGradeConditionJson());
            return vo;
        }
        return null;
    }

    public MemberCardRecord getGradeCardByGrade(String grade) {
        return db().selectFrom(MEMBER_CARD)
            .where(MEMBER_CARD.GRADE.eq(grade))
            .and(MEMBER_CARD.DEL_FLAG.eq(MCARD_DF_NO))
            .and(MEMBER_CARD.FLAG.equal(MCARD_FLAG_USING))
            .fetchAny();
    }

    private void setQrCode(WxAppUserCardVo card) {
        MemberCardRecord mCard = memberCardService.getCardById(card.getCardId());
        String qrCode = qrCodeService.getUserCardQrCode(card.getCardNo(), mCard);
        card.setQrCode(qrCode);
    }

    public void dealWithUserCardDetailInfo(WxAppUserCardVo card) {
        logger().info("处理wx 用户会员卡数据详情");
        dealWithUserCardBasicInfo(card);
        dealWithUserCardAvailableStore(card);
        card.setGoodsList(getExchangGoodsDetail(card));
    }

    public void dealWithUserCardAvailableStore(WxAppUserCardVo card) {
        logger().info("正在处理会员卡门店列表信息");
        card.setStoreInfoList(Collections.emptyList());
        card.setStoreIdList(Collections.emptyList());


        if (card.isStoreAvailable()) {
            List<Integer> storeIdList = card.retrieveStoreList();
            if(storeIdList != null && storeIdList.size()>0 && storeIdList.get(0) != 0) {
                // 部分门店
                card.setStoreIdList(card.retrieveStoreList());
                List<StoreBasicVo> storeBasicVo = storeService.getStoreListByStoreIds(card.retrieveStoreList());
                card.setStoreInfoList(storeBasicVo);
                card.setStoreUseSwitch(CardConstant.MCARD_STP_PART);
            }else {
                // 全部门店
                card.setStoreUseSwitch(CardConstant.MCARD_STP_ALL);
            }
        }else {
            // 不可在门店使用
            card.setStoreUseSwitch(CardConstant.MCARD_STP_BAN);
        }
    }

    public void dealWithUserCardBasicInfo(WxAppUserCardVo card) {
        String avatar = getCardAvatar();
        dealWithWxUserCard(card, avatar);
    }

    /**
     * 处理可兑换的商品
     */
    public List<GoodsSmallVo> getExchangGoodsDetail(WxAppUserCardVo userCard) {
        List<GoodsSmallVo> res = Collections.<GoodsSmallVo>emptyList();
        if(CardUtil.isLimitCard(userCard.getCardType()) && CardUtil.canExchangGoods(userCard.getIsExchang())) {
            logger().info("处理限次卡兑换的商品");
            boolean partGoodsFlag = CardConstant.MCARD_ISE_PART.equals(userCard.getIsExchang());
            if(partGoodsFlag) {
                // 部分商品
                if(!StringUtils.isBlank(userCard.getExchangGoods())) {
                    List<Integer> goodsIdList = Util.splitValueToList(userCard.getExchangGoods());
                    res = goodsService.getGoodsList(goodsIdList, false);
                }
            }else {
                // 全部商品，只取两个进行展示
                GoodsPageListParam goodsParam = new GoodsPageListParam();
                goodsParam.setPageRows(2);
                goodsParam.setCurrentPage(1);
                PageResult<GoodsPageListVo> goodsPageList = goodsService.getPageList(goodsParam);
                List<Integer> goodsIdList = new ArrayList<>();
                for(GoodsPageListVo goodsVo: goodsPageList.dataList) {
                    goodsIdList.add(goodsVo.getGoodsId());
                }
                res = goodsService.getGoodsList(goodsIdList, false);
            }

            if(res.size()>0) {
                logger().info("价格处理为两位小数");
                for(GoodsSmallVo goodsVo: res) {
                    BigDecimal shopPrice = goodsVo.getShopPrice();
                    goodsVo.setShopPrice(shopPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                }
            }
        }

        return res;
    }


    /**
     * 王帅 get card type
     */
    public Byte getCardType(String cardNo) {
        if (StringUtil.isBlank(cardNo)) {
            return null;
        }
        return userCardDao.getCardType(cardNo);
    }

    /**
     * 王帅 get card type
     */
    public Byte getCardByCardNo(String cardNo) {
        if (StringUtil.isBlank(cardNo)) {
            return null;
        }
        return userCardDao.getCardType(cardNo);
    }

    /**
     * 获取商品的等级会员价
     *
     * @param userId    用户
     * @param prdIdList 规格ids
     */
    public List<UserCardGradePriceBo> getUserCartGradePrice(Integer userId, List<Integer> prdIdList) {
        return userCardDao.getUserCartGradePrice(userId, prdIdList);
    }

    /**
     * 获取商品的等级会员价
     *
     * @param grade     等级
     * @param prdIdList 规格ids
     */
    public List<UserCardGradePriceBo> getUserCartGradePrice(String grade, List<Integer> prdIdList) {
        return userCardDao.getUserCartGradePrice(grade, prdIdList);
    }

    /**
     * 筛选会员专享商品
     * @param userId        用户id
     * @param cartGoodsList 需要筛选的商品
     * @return 反回会员卡绑定商品
     */
    public Set<Integer> getUserCardExclusiveGoodsIds(Integer userId, List<WxAppCartGoods> cartGoodsList) {
        cartGoodsList = cartGoodsList.stream().distinct().collect(Collectors.toList());
        Set<Integer> resGoodsIds = new HashSet<>();
        // 获取关联商品
        Map<Byte, List<Integer>> goodsCardCouple = goodsCardCoupleService.getGoodsCardCouple(userId);
        cartGoodsList.forEach(cartGoods -> {
            goodsCardCouple.forEach((k, v) -> {
                if (v != null) {
                    if (CardConstant.COUPLE_TP_GOODS.equals(k)) {
                        resGoodsIds.add(cartGoods.getGoodsId());
                    }
                    if (CardConstant.COUPLE_TP_STORE.equals(k)) {
                        resGoodsIds.add(cartGoods.getGoodsRecord().getSortId());
                    }
                    if (CardConstant.COUPLE_TP_PLAT.equals(k)) {
                        resGoodsIds.add(cartGoods.getCartId());
                    }
                    if (CardConstant.COUPLE_TP_BRAND.equals(k)) {
                        resGoodsIds.add(cartGoods.getGoodsRecord().getBrandId());
                    }
                }
            });
        });
        return resGoodsIds;

    }

    /**
     * 根据id获得具有卡的数量
     */
    public int getNumCardsWithSameId(Integer cardId) {
        return userCardDao.calcNumCardById(cardId);
    }

    /**
     * 王帅 得到订单下的用户可用会员卡
     * @param userId
     * @param bos
     * @param storeId
     * @param defaultCards
     * @return cards
     */
    public List<OrderMemberVo> getValidCardList(Integer userId, List<OrderGoodsBo> bos, Integer storeId,
                                                List<OrderMemberVo> defaultCards) {
        List<OrderMemberVo> cards = new ArrayList<OrderMemberVo>();
        if (CollectionUtils.isEmpty(defaultCards)) {
            // 初始化
            defaultCards = userCardDao.getOrderMembers(userId,
                new Byte[] { CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE },
                OrderConstant.MEMBER_CARD_ONLINE);
        }else if(!CardConstant.MCARD_TP_LIMIT.equals(defaultCards.get(0).getInfo().getCardType())) {
            List<OrderMemberVo> temp = userCardDao.getOrderMembers(userId,
                new Byte[]{CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE},
                OrderConstant.MEMBER_CARD_ONLINE);
            for (OrderMemberVo orderMemberVo : temp) {
                if(!defaultCards.get(0).getInfo().getCardId().equals(orderMemberVo.getInfo().getCardId())) {
                    defaultCards.add(orderMemberVo);
                }
            }
        }

        if (CollectionUtils.isEmpty(defaultCards)) {
            // 校验
            return Lists.newArrayList();
        }
        //会员卡权益：打折，包邮，余额
        for (Iterator<OrderMemberVo> iterator = defaultCards.iterator(); iterator.hasNext();) {
            OrderMemberVo card = iterator.next();
            // 当前会员卡适用商品
            BigDecimal[] tolalNumberAndPrice = calculate.getTolalNumberAndPriceByType(bos,
                OrderConstant.D_T_MEMBER_CARD,
                DefaultMarketingProcess.builder().card(card).type(OrderConstant.D_T_MEMBER_CARD).build());
            // 折扣金额
            BigDecimal discountAmount = null;
            // 判断门店（无门店||全部门店||部分门店）
            boolean canDiscount = storeId == null || MCARD_STP_ALL.equals(Byte.valueOf(card.getInfo().getStoreList()))
                || (MCARD_SUSE_OK.equals(card.getInfo().getStoreUseSwitch())
                && Arrays.asList(card.getInfo().getStoreList().split(",")).contains(storeId.toString()));
            if (canDiscount) {
                // 折扣金额
                discountAmount = getDiscountAmount(card, tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
            }
            boolean noDiscount = BigDecimalUtil.compareTo(discountAmount, null) < 1 &&
                CollectionUtils.isEmpty(card.getBos()) &&
                BigDecimalUtil.compareTo(card.getInfo().getMoney(), BigDecimalUtil.BIGDECIMAL_ZERO) < 1 &&
                (card.getInfo().getCardFreeShip() == null || card.getInfo().getCardFreeShip().getType() == CardFreeship.shipType.SHIP_NOT_AVAIL.getType() || card.getInfo().getCardFreeShip().getType() > CardFreeship.shipType.SHIP_IN_EFFECTTIME.getType());
            if(noDiscount) {
                //折扣金额为0，无折扣商品，无卡余额，无包邮权益->不展示
                iterator.remove();
                continue;
            }
            card.setTotalPrice(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
            card.setTotalGoodsNumber(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_NUMBER]);
            card.setTotalDiscount(discountAmount);
            card.setIdentity(card.getInfo().getCardNo());
            card.initRatio();
            cards.add(card);
        }
        return cards;
    }

    /**
     * 王帅 校验该商品是否可以打折
     * @param cardId 卡
     * @param bo     商品
     * @return boolean
     */
    public boolean isContainsProduct(Integer cardId, OrderGoodsBo bo) {
        MemberCardRecord card = userCardDao.getMemberCardById(cardId);
        if (card == null) {
            return false;
        }
        if (CardConstant.MCARD_DIS_ALL.equals(card.getDiscountIsAll())) {
            return true;
        }
        if (StringUtil.isNotBlank(card.getDiscountGoodsId()) && Arrays.asList(card.getDiscountGoodsId().split(",")).contains(bo.getGoodsId().toString())) {
            // 商品id
            return true;
        }
        if (StringUtil.isNotBlank(card.getDiscountCatId()) && Arrays.asList(card.getDiscountCatId().split(",")).contains(bo.getCatId().toString())) {
            // 平台分类id
            return true;
        }
        if (StringUtil.isNotBlank(card.getDiscountSortId()) && Arrays.asList(card.getDiscountSortId().split(",")).contains(bo.getSortId().toString())) {
            // 商家分类id
            return true;
        }
        if (StringUtil.isNotBlank(card.getDiscountBrandId()) && Arrays.asList(card.getDiscountBrandId().split(",")).contains(bo.getBrandId().toString())) {
            // 商品品牌id
            return true;
        }
		return false;
	}

	/**
	 * 王帅 获取该卡打折金额
	 * @param card       会员卡
	 * @param totalPrice 折前总价
	 * @return 折后总价
	 */
	public BigDecimal getDiscountAmount(OrderMemberVo card, BigDecimal totalPrice) {
		if (CardConstant.MCARD_TP_LIMIT.equals(card.getInfo().getCardType())) {
			// 限次卡
			card.setTotalDiscount(totalPrice);
		} else if (BigDecimalUtil.compareTo(card.getInfo().getDiscount(), BigDecimal.ZERO) < 1) {
			// 如果没有打折权益则为十折
			card.setTotalDiscount(BigDecimal.ZERO);
		} else {
			// 正常打折(价格 * （10 - 折扣（eg:6.66） / 10）)
			card.setTotalDiscount(
			    BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.DOWN,
					BigDecimalUtil.BigDecimalPlus.create(totalPrice, BigDecimalUtil.Operator.multiply),
					BigDecimalUtil.BigDecimalPlus.create(
							BigDecimalUtil.addOrSubtrac(
									BigDecimalUtil.BigDecimalPlus.create(BigDecimal.TEN, BigDecimalUtil.Operator.subtrac),
									BigDecimalUtil.BigDecimalPlus.create(card.getInfo().getDiscount(), null)),
							BigDecimalUtil.Operator.divide),
					BigDecimalUtil.BigDecimalPlus.create(BigDecimal.TEN, null))
            );
        }
        return card.getTotalDiscount();
    }

    /**
     * 获取用户累积消费总额
     *
     * @return 消费总额,默认为0
     */
    public BigDecimal getUserTotalSpendAmount(Integer userId) {
        DistributorSpendVo distributorSpendVo = distributorLevelService.getTotalSpend(userId);
        return distributorSpendVo.getTotal() != null ? distributorSpendVo.getTotal() : BigDecimal.ZERO;
    }

    /**
     * 王帅 get card
     */
    public UserCardParam getCard(String cardNo) {
        if (StringUtil.isBlank(cardNo)) {
            return null;
        }
        return userCardDao.getUserCardInfo(cardNo);
    }

    /**
     * 获取持有会员卡的用户id
     */
    public List<Integer> getUserIdThatHasValidCard() {
        return userCardDao.getUserIdThatHasValidCard();
    }

    /**
     * 废除会员卡
     */
    public void repealCardByCardNo(String cardNo) {
        userCardDao.repealCardByCardNo(cardNo);
    }

    /**
     * Gets single field.
     *
     * @param <T>       the type parameter
     * @param field     the field
     * @param condition the condition
     * @return the single field
     */
    public <T> T getSingleField(Field<T> field, Condition condition) {
        return db().select(field).from(USER_CARD).where(condition).fetchOne(field);
    }

    public UserCardVo getUserCardJudge(UserIdAndCardIdParam param) {
        UserCardVo userCard = userCardDao.getUserCardJudge(param);
        // 处理背景图片
        if(userCard != null) {
            if(!StringUtils.isBlank(userCard.getBgImg())) {
                String imageUrl = saas.getShopApp(getShopId()).image.imageUrl(userCard.getBgImg());
                userCard.setBgImg(imageUrl);
            }

            if(StringUtils.isBlank(userCard.getBgColor())) {
                userCard.setBgColor(CardUtil.getDefaultBgColor());
            }
        }
        return userCard;
    }

    public UserCardJudgeVo userCardJudgement(UserIdAndCardIdParam param,String lang) {
        logger().info("用户卡判断");
        UserCardVo userCard = getUserCardJudge(param);
        MemberCardRecord mCard = cardDao.getCardById(param.getCardId());
        if(mCard != null && StringUtils.isBlank(mCard.getBgColor())) {
			mCard.setBgColor(CardUtil.getDefaultBgColor());
		}
        boolean isGet = false;
        if (userCard != null) {
            logger().info("用户有此卡");
            if(StringUtils.isBlank(userCard.getBgColor())) {
				userCard.setBgColor(CardUtil.getDefaultBgColor());
			}
            isGet = true;
        }else {
            logger().info("用户没有有此卡");
            isGet = false;
            userCard = mCard.into(UserCardVo.class);
        }
        userCard.setIsGet(isGet);
        // 限次卡
        if (CardUtil.isLimitCard(userCard.getCardType())) {
            if (!canSendLimitCard(param.getUserId(), mCard)) {
                logger().info("限次卡领取次数用完");
                isGet = true;
            }else{
                // 能继续领取限次卡
                userCard = mCard.into(UserCardVo.class);
                isGet = false;
            }
        }

        if (!isGet) {
            return judgeByNoCard(param, lang, userCard, mCard);
        }else{

            return judgeByGotCard(param, lang, userCard, mCard);
        }
    }

    private UserCardJudgeVo judgeByNoCard(UserIdAndCardIdParam param, String lang, UserCardVo userCard, MemberCardRecord mCard) {
        // 返回新卡也就是memberCard的配置详情信息
        logger().info("用户有此限次卡但是还可以继续领取，或者没有此卡");
        if (!CardUtil.isNeedToBuy(mCard.getIsPay())) {
            userCard.setPayFee(null);
        }
        if (!CardUtil.isCardTimeForever(userCard.getExpireType())) {
            if (CardUtil.isCardFixTime(userCard.getExpireType()) && CardUtil.isCardExpired(userCard.getEndTime())) {
                logger().info("卡过期");
                userCard.setStatus(-1);
            } else {
                userCard.setStatus(1);
            }

            if (CardUtil.isCardFixTime(userCard.getExpireType())) {
                userCard.setStartDate(userCard.getStartTime().toLocalDateTime().toLocalDate());
                userCard.setEndDate(userCard.getEndTime().toLocalDateTime().toLocalDate());
            }
        } else {
            userCard.setStatus(1);
        }

        userCard.setShopAvatar(getCardAvatar());
        userCard.setScoreAmount(scoreService.getAccumulationScore(param.getUserId()));
        userCard.setPaidAmount(orderInfoService.getAllConsumpAmount(param.getUserId()));
        userCard.setBindMobile(shopCommonConfigService.getBindMobile());

        //兑换商品
        WxAppUserCardVo vo = new WxAppUserCardVo();
        vo.setCardType(userCard.getCardType());
        vo.setIsExchang(userCard.getIsExchang());
        vo.setExchangGoods(userCard.getExchangGoods());
        userCard.setGoodsList(getExchangGoodsDetail(vo));
        // 处理限次兑换次数
        String cardNo = userCard.getCardNo();
        boolean toGetCard = StringUtils.isBlank(cardNo);
        // 没有领取卡取卡的配置，已经领卡取用户卡的快照信息
        if(CardUtil.isLimitCard(userCard.getCardType()) && toGetCard ) {
            // 门店兑换次数
            if(CardUtil.canUseInStore(userCard.getStoreUseSwitch())) {
                if(userCard.getSurplus()==null) {
                    userCard.setSurplus(userCard.getCount());
                }
            }
            // 商品兑换次数
            if(CardUtil.canExchangGoods(userCard.getIsExchang())) {
                userCard.setExchangSurplus(userCard.getExchangCount());
            }

        }

        if(!StringUtil.isBlank(userCard.getStoreList()) && CardUtil.canUseInStore(userCard.getStoreUseSwitch())) {
            dealWithCardStore(userCard);
        }

        // 包邮信息
        dealWithJudgeFreeship(lang, userCard);
        // 自定义权益信息
        CardCustomRights customRights = memberCardService.cardDetailSvc.getCustomRights(mCard);
        userCard.setCardCustomRights(customRights);


        logger().info("开卡送券");
        wxCardDetailSvc.dealSendCouponInfo(userCard,lang);
        UserCardJudgeVo userCardJudgeVo = new UserCardJudgeVo();
        userCardJudgeVo.setStatus(1);

        // 卡的显示金额
        if(StringUtils.isBlank(userCard.getCardNo())) {
            if(userCard.getSendMoney() != null) {
                userCard.setMoney(BigDecimal.valueOf(userCard.getSendMoney()));
            }
        }

        // 有效时间
        setEffectTimeForJudgeCard(userCard);
        userCard.setUserId(param.getUserId());
        userCard.setCardId(param.getCardId());
        userCardJudgeVo.setCardInfo(userCard);
        return userCardJudgeVo;
    }

    private UserCardJudgeVo judgeByGotCard(UserIdAndCardIdParam param, String lang, UserCardVo userCard, MemberCardRecord mCard) {
        UserCardVo uCard = getUserCardByCardNo(userCard.getCardNo());
        uCard.setIsGet(true);

        // 计算用户卡的有效时间
        EffectTimeParam tp = new EffectTimeParam();
        tp.setStartTime(uCard.getStartTime());
        tp.setEndTime(uCard.getEndTime());
        tp.setExpireTime(uCard.getExpireTime());
        tp.setExpireType(uCard.getExpireType());
        tp.setCreateTime(uCard.getUCreateTime());

        EffectTimeBean tB = CardUtil.getUserCardEffectTime(tp);
        if(CardUtil.isCardExpired(tB.getEndTime())) {
            logger().info("卡过期");
            uCard.setStatus(-1);
        }else {
            uCard.setStatus(1);
        }
        uCard.setStartDate(tB.getStartDate());
        uCard.setEndDate(tB.getEndDate());
        uCard.setExpireType(tB.getExpireType());
        uCard.setScoreAmount(scoreService.getAccumulationScore(param.getUserId()));
        uCard.setPaidAmount(orderInfoService.getAllConsumpAmount(param.getUserId()));
        if(CardUtil.isGradeCard(uCard.getCardType())) {
            // 升级进度条内容
            NextGradeCardVo nextGradeCard = getNextGradeCard(uCard.getGrade());
            uCard.setNext(nextGradeCard);
        }
        if(!CardUtil.isGradeCard(uCard.getCardType()) && !StringUtil.isBlank(uCard.getStoreList()) && CardUtil.canUseInStore(uCard.getStoreUseSwitch())) {
            dealWithCardStore(uCard);
        }
        // 会员卡头像
        uCard.setShopAvatar(getCardAvatar());
        // 背景图片
        logger().info("虚拟卡订单下单时间");
        VirtualOrderRecord order = virtualOrderService.getInfoByNo(uCard.getCardNo());
        if(order != null) {
            Timestamp buyTime = order.getCreateTime();
            uCard.setBuyTime(buyTime);
        }

        logger().info("卡的校验状态");
        CardExamineRecord cardExamine = cardVerifyService.getStatusByNo(uCard.getCardNo());
        if(cardExamine != null) {
            uCard.setCardVerifyStatus(cardVerifyService.getCardVerifyStatus(uCard.getCardNo()));
            WxAppCardExamineVo cardExamineVo = new WxAppCardExamineVo();
            cardExamineVo.setPassTime(cardExamine.getPassTime());
            cardExamineVo.setRefuseTime(cardExamine.getRefuseTime());
            cardExamineVo.setRefuseDesc(cardExamine.getRefuseDesc());
            cardExamineVo.setStatus(cardExamine.getStatus());
            uCard.setIsExamine(cardExamineVo);
        }
        if(CardUtil.isLimitCard(uCard.getCardType()) && CardUtil.canExchangGoods(uCard.getIsExchang())) {
            logger().info("处理限次卡兑换的商品");
            if(!StringUtils.isBlank(uCard.getExchangGoods())) {
                List<Integer> goodsIdList = Util.splitValueToList(uCard.getExchangGoods());
                List<GoodsSmallVo> goodsList = goodsService.getGoodsList(goodsIdList, false);
                uCard.setGoodsList(goodsList);
            }
            if(userCard.getGoodsList()!=null) {
                logger().info("价格处理为两位小数");
                for(GoodsSmallVo goodsVo: userCard.getGoodsList()) {
                    BigDecimal shopPrice = goodsVo.getShopPrice();
                    goodsVo.setShopPrice(shopPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                }
            }
        }
        // 包邮信息
        dealWithJudgeFreeship(lang, uCard);
        // 自定义权益信息
        CardCustomRights customRights = memberCardService.cardDetailSvc.getCustomRights(mCard);
        uCard.setCardCustomRights(customRights);

        wxCardDetailSvc.dealSendCouponInfo(uCard,lang);
        UserCardJudgeVo userCardJudgeVo = new UserCardJudgeVo();
        userCardJudgeVo.setStatus(1);
        setEffectTimeForJudgeCard(uCard);
        uCard.setUserId(param.getUserId());
        uCard.setCardId(param.getCardId());
        uCard.setStoreUseSwitch(CardUtil.getUseStoreType(uCard.getStoreUseSwitch(),uCard.getStoreList()));
        userCardJudgeVo.setCardInfo(uCard);
        return userCardJudgeVo;
    }


    private void dealWithJudgeFreeship(String lang, UserCardVo userCard) {
        logger().info("处理判断卡的包邮信息");
        if(StringUtils.isBlank(userCard.getCardNo())) {
            logger().info("取会员卡的包邮信息");
            MemberCardRecord mCardRec = new MemberCardRecord();
            mCardRec.setFreeshipLimit(userCard.getFreeshipLimit());
            mCardRec.setFreeshipNum(userCard.getFreeshipNum());
            CardFreeship freeshipData = cardFreeShipSvc.getFreeshipData(mCardRec, lang);
            userCard.setFreeshipDesc(freeshipData.getDesc());
        }else {
            logger().info("取用户卡的包邮信息");
            UserCardParam cardParam = new UserCardParam();
            cardParam.setUserId(userCard.getUserId());
            cardParam.setCardId(userCard.getCardId());
            cardParam.setFreeLimit(userCard.getFreeLimit());
            cardParam.setFreeNum(userCard.getFreeNum());
            CardFreeship freeshipData = cardFreeShipSvc.getFreeshipData(cardParam,lang);
            userCard.setFreeshipDesc(freeshipData.getDesc());
        }
    }

    private void dealWithCardStore(UserCardVo userCard) {
        logger().info("获取门店信息");
        // 门店使用类型
        Byte useStoreType = CardUtil.getUseStoreType(userCard.getStoreUseSwitch(), userCard.getStoreList());
        userCard.setStoreUseSwitch(useStoreType);
        // 门店Id
        List<Integer> storeIdList = CardUtil.parseStoreList(userCard.getStoreList());
        userCard.setStoreIdList(storeIdList);
        // 门店具体信息
        List<StoreBasicVo> storeList = storeService.getStoreListByStoreIds(storeIdList);
        userCard.setStoreInfoList(storeList);

    }

    /**
     * 	设置有效时间
     * @param userCard
     */
    private void setEffectTimeForJudgeCard(UserCardVo userCard) {

        if(StringUtils.isBlank(userCard.getCardNo())) {
            logger().info("直接设置会员卡的配置有效期");
            userCard.setStartDate(CardUtil.timeToLocalDate(userCard.getStartTime()));
            userCard.setEndDate(CardUtil.timeToLocalDate(userCard.getEndTime()));
            // 兼容
            Byte expireType = userCard.getExpireType();
            if(CardUtil.isCardFixTime(expireType)) {
                userCard.setExpireType(NumberUtils.BYTE_ONE);
            }else if(CardUtil.isCardTimeStartFrom(expireType)) {
                userCard.setExpireType(NumberUtils.BYTE_ZERO);
            }
        }else {
            logger().info("处理用户卡的有效快照时间");
            EffectTimeParam etParam = new EffectTimeParam();
            etParam.setStartTime(userCard.getStartTime());
            etParam.setEndTime(userCard.getEndTime());
            etParam.setCreateTime(userCard.getUCreateTime());
            etParam.setExpireTime(userCard.getExpireTime());
            etParam.setExpireType(userCard.getExpireType());
            EffectTimeBean etBean = CardUtil.getUserCardEffectTime(etParam);
            userCard.setStartDate(etBean.getStartDate());
            userCard.setStartTime(etBean.getStartTime());
            userCard.setEndDate(etBean.getEndDate());
            userCard.setEndTime(etBean.getEndTime());
            userCard.setExpireType(etBean.getExpireType());
        }

    }


    /**
     * 	领取会员卡
     * @param param
     * @return 带有卡号的卡信息
     * @throws MpException
     */
    public CardReceiveVo getCard(UserIdAndCardIdParam param) throws MpException {
        logger().info("领取会员卡");
        CardReceiveVo vo = new CardReceiveVo();
        //	第一次领取
        if (param.getCardId() != null) {
            MemberCardRecord mCard = memberCardService.getCardById(param.getCardId());
            if(mCard == null) {
                return null;
            }
            if(CardUtil.isLimitCard(mCard.getCardType())) {
                logger().info("领取限次会员卡");
                cardUserOpt.setDecorate(limitCardOpt);
                String cardNo = cardUserOpt.handleSendCard(param.getUserId(), param.getCardId(), false);

                if(StringUtils.isBlank(cardNo)) {
                    logger().info("领取失败");
                    throw new CardReceiveFailException();
                }else {
                    if (!limitCardOpt.canSendCard(param.getUserId(), param.getCardId())) {
                        vo.setIsContinue("not_continue");
                    }
                    vo.setCardNo(cardNo);
                    return vo;
                }
            }else if(CardUtil.isNormalCard(mCard.getCardType())) {
                logger().info("领取普通会员卡");
                cardUserOpt.setDecorate(normalCardOpt);
                String cardNo = cardUserOpt.handleSendCard(param.getUserId(), param.getCardId(), false);
                //	如果已经领取了该普通卡，不再次领取，返回拥有的会员卡号
                if(StringUtil.isNotBlank(cardNo)) {
                    vo.setCardNo(cardNo);
                }else {
                    cardNo = getCardNoByUserAndCardId(param.getUserId(), param.getCardId());
                    if(StringUtil.isBlank(cardNo)) {
                        logger().info("领取失败");
                        throw new CardReceiveFailException();
                    }
                    vo.setCardNo(cardNo);
                    return vo;
                }
            }else if(CardUtil.isGradeCard(mCard.getCardType())) {
                logger().info("领取等级会员卡");
                cardUserOpt.setDecorate(gradeCardOpt);
                String cardNo = cardUserOpt.handleSendCard(param.getUserId(), param.getCardId(), false);
                if(StringUtil.isBlank(cardNo)) {
                    logger().info("领取失败");
                    throw new CardReceiveFailException();
                }
                vo.setCardNo(cardNo);
            }

        } else {
            logger().info("首次领取等级卡");
            Integer cardId = 0;
            try {
                cardId = updateGrade(param.getUserId(), param.getCardId(), (byte) 2);
                vo.setIsMostGrade(false);
            } catch (MemberCardNullException e) {
                vo.setIsMostGrade(true);
            }
            String cardNo = getCardNoByUserAndCardId(param.getUserId(), cardId);
            logger().info("领取的会员卡卡号为： " + cardNo);
            if (StringUtils.isBlank(cardNo)) {
                logger().info("没有查询到卡号");
                throw new CardReceiveFailException();
            }

            MemberCardRecord newCard = memberCardService.getCardById(cardId);

            GradeCardData data = new GradeCardData();
            GradeConditionJson gradeCondition = Util.parseJson(newCard.getGradeCondition(), GradeConditionJson.class);
            BigDecimal gradeMoney = gradeCondition.getGradeMoney();
            BigDecimal gradeScore = gradeCondition.getGradeScore();
            data.setAmount(gradeMoney);
            data.setScore(gradeScore);
            vo.setCardNo(cardNo);
            vo.setGradeCard(data);
            return vo;
        }
        return vo;
    }

    /**
     * 获取用户的等级卡
     * @param param
     * @return
     */
    public MemberCardRecord getGradeCard(UserIdAndCardIdParam param) {
        MemberCardRecord card = cardDao.getCardById(param.getCardId());
        if (card == null || !CardUtil.isGradeCard(card.getCardType())) {
            return null;
        }
        Integer accumulationScore = scoreService.getAccumulationScore(param.getUserId());
        BigDecimal consumpAmount = orderInfoService.getAllConsumpAmount(param.getUserId());
        MemberCardRecord userGradeCard = getUserGradeCard(param.getUserId());
        List<MemberCardRecord> gradeCardList = null;
        if (userGradeCard != null) {
            gradeCardList = memberCardService.getGradeCardList(userGradeCard.getGrade());
        } else {
            gradeCardList = memberCardService.getGradeCardList();
        }
        Integer cardId = 0;
        if (gradeCardList == null) {
            logger().info("已有最高等级等级卡");
            return null;
        } else {
            for (MemberCardRecord gCard : gradeCardList) {
                GradeConditionJson gradeCondition = getGradeCondition(accumulationScore, consumpAmount, gCard);
                if (isSatisfyUpgradeCondition(accumulationScore, consumpAmount, gradeCondition)) {
                    cardId = gCard.getId();
                }
            }
        }

        if (cardId.equals(userGradeCard.getId())) {
            return null;
        }

        MemberCardRecord mCard = memberCardService.getCardById(param.getCardId());
        if (!CardUtil.isGradeCard(mCard.getCardType())) {
            return null;
        }

		GradeConditionJson gradeCondition = getGradeCondition(accumulationScore, consumpAmount, mCard);
		if (isSatisfyUpgradeCondition(accumulationScore, consumpAmount, gradeCondition)) {
			MemberCardRecord oldCard = getUserGradeCard(param.getUserId());
			if (!StringUtils.isBlank(oldCard.getGrade())) {
				logger().info("升级记录");
				String operation = "首页领取";
				changeUserGradeCard(param.getUserId(), oldCard, mCard, operation,false);
			} else {
				createNewUserCard(param.getUserId(), mCard, NumberUtils.BYTE_ZERO.equals(mCard.getActivation()));
			}
			return mCard;
		}
		return null;
	}

	public String getCardNoByUserAndCardId(Integer userId, Integer cardId) {
		UserCardRecord rec = db().selectFrom(USER_CARD)
				.where(USER_CARD.USER_ID.eq(userId).and(USER_CARD.CARD_ID.eq(cardId)))
                .and(USER_CARD.FLAG.eq(UCARD_FG_USING))
                .fetchAny();
		return rec != null ? rec.getCardNo() : null;
	}
	public UserCardVo getUserCardByCardNo(String cardNo){
		UserCardVo userCard = userCardDao.getUserCardByCardNo(cardNo);
		if(userCard != null && CardUtil.isBgImgType(userCard.getBgType())) {
			if(!StringUtils.isBlank(userCard.getBgImg())) {
				String imageUrl = saas.getShopApp(getShopId()).image.imageUrl(userCard.getBgImg());
				userCard.setBgImg(imageUrl);
			}
		}
		if(userCard != null && StringUtils.isBlank(userCard.getBgColor())){
			userCard.setBgColor(CardUtil.getDefaultBgColor());
		}
		return userCard;
	}

    public void updateActivationTime(String cardNo,Timestamp time) {
        if(time==null) {
            time = DateUtils.getLocalDateTime();
        }
        userCardDao.updateActivationTime(cardNo,time);
    }

    /**
     *	 设置默认会员卡
     */
    public void setDefault(DefaultCardParam param) {
        logger().info("设置默认会员卡");
        transaction(()->{
            Condition condition = DSL.noCondition();
            // set user card all 0
            condition = condition.and(USER_CARD.USER_ID.eq(param.getUserId()));
            userCardDao.updateIsDefault(condition,NumberUtils.BYTE_ZERO);
            // only set card 1 by cardNo
            condition = condition.and(USER_CARD.CARD_NO.eq(param.getCardNo()));
            userCardDao.updateIsDefault(condition,NumberUtils.BYTE_ONE);
        });
    }

    /**
     * 检查用户等级升级
     * @param userId
     * @param grade
     * @return
     */
    public boolean checkUserGradeCard(Integer userId,String grade) {
        String userGrade = getUserGrade(userId);
        if(Objects.equals(userGrade, grade)) {
            return true;
        }
        MemberCardRecord cardByGrade = getGradeCardByGrade(grade);
        if(cardByGrade==null) {
            return false;
        }
        try {
            updateGrade(userId, cardByGrade.getId(), TP_CHECK);
        } catch (MpException e) {
            logger().info("升级错误");
            logger().info(e.getMessage(),e);
            return false;
        }
        return true;
    }


	public Integer insertRow(UserCardRecord record) {
		return db().executeInsert(record);
	}

    /**
     * 获得可用的、余额大于0的普通会员卡
     * @param userId
     * @return
     */
	public List<GeneralUserCardVo> getCanUseGeneralCardList(Integer userId){
        List<GeneralUserCardVo> list = db().select(USER_CARD.CARD_NO,USER_CARD.EXPIRE_TIME,USER_CARD.MONEY,MEMBER_CARD.ID,MEMBER_CARD.BG_COLOR,MEMBER_CARD.CARD_NAME,MEMBER_CARD.DISCOUNT,MEMBER_CARD.BG_TYPE,MEMBER_CARD.BG_IMG,MEMBER_CARD.EXPIRE_TYPE,MEMBER_CARD.CARD_TYPE,MEMBER_CARD.START_TIME,MEMBER_CARD.END_TIME).
            from(USER_CARD).leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(USER_CARD.CARD_ID)).
            where(USER_CARD.USER_ID.eq(userId)).
            and(MEMBER_CARD.CARD_TYPE.eq(CardConstant.MCARD_TP_NORMAL)).
            and(USER_CARD.FLAG.eq(DelFlag.NORMAL_VALUE)).
            and(USER_CARD.EXPIRE_TIME.isNull().or(USER_CARD.EXPIRE_TIME.gt(DateUtils.getLocalDateTime()))).
            and(MEMBER_CARD.ACTIVATION.eq(CardConstant.MCARD_ACT_NO).or(USER_CARD.ACTIVATION_TIME.isNotNull().and(MEMBER_CARD.ACTIVATION.eq(CardConstant.MCARD_ACT_YES)))).
            and(USER_CARD.MONEY.gt(BigDecimal.ZERO)).
            fetchInto(GeneralUserCardVo.class);
        list.forEach(c->{
            if(StringUtil.isNotBlank(c.getBgImg())){
                c.setBgImg(domainConfig.imageUrl(c.getBgImg()));
            }
            if(c.getStartTime() != null && c.getEndTime() != null){
                c.setStartDate(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,c.getStartTime()));
                c.setEndDate(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,c.getEndTime()));
            }
        });
        return list;
    }

    /**
     * 获取用户的会员卡续费订单统计
     */
    public UserOrderBean getRenewOrderStatistics(Integer userId) {
        return db().select(DSL.count(CARD_RENEW.RENEW_ORDER_SN).as("orderNum"),
            DSL.sum(CARD_RENEW.MONEY_PAID.add(CARD_RENEW.USE_ACCOUNT).add(CARD_RENEW.MEMBER_CARD_REDUNCE)).as("totalMoneyPaid"))
            .from(CARD_RENEW)
            .where(CARD_RENEW.ORDER_STATUS.eq(CardConstant.CARD_RENEW_ORDER_STATUS_OK))
            .and(CARD_RENEW.USER_ID.eq(userId))
            .fetchAnyInto(UserOrderBean.class);
    }

    /**
     * 获取用户的会员卡充值订单统计
     */
    public UserOrderBean getChargeOrderStatistics(Integer userId) {
        return db().select(DSL.count(CHARGE_MONEY.ORDER_SN).as("orderNum"),
            DSL.sum(CHARGE_MONEY.MONEY_PAID).as("totalMoneyPaid"))
            .from(CHARGE_MONEY)
            .where(CHARGE_MONEY.ORDER_STATUS.eq(CardConstant.TWO))
            .and(CHARGE_MONEY.CHARGE.gt(BigDecimal.ZERO))
            .and(CHARGE_MONEY.CHANGE_TYPE.eq(CardConstant.TWO))
            .and(CHARGE_MONEY.USER_ID.eq(userId))
            .fetchAnyInto(UserOrderBean.class);
    }

    /**
     * 删除用户卡
     */
    public int delUserCard(DefaultCardParam param) {
        return userCardDao.updateUserCardFlag(param);
    }

    /**
     * 	获取已经领取该卡的用户数量
     * @param cardId
     */
    public int getCardUserNum(Integer cardId) {
        logger().info("获取已经领取该卡的用户数量");
        int num = userCardDao.getCardNum(cardId);
        return num;
    }

    /**
     * 	获取该卡被领取的数量
     */
    public int getReceiveCardNum(Integer cardId) {
        logger().info("获取该卡被领取的数量");
        int num = userCardDao.getCardUserList(cardId);
        return num;
    }


    /**
     * 	获取可该卡可正常使用的数量
     */
    public int getCanUseCardNum(MemberCardRecord card) {
        logger().info("获取可该卡可正常使用的数量");
        int num =userCardDao.getCanUseCardNum(card.getId(),CardUtil.isNeedActive(card.getActivation()));
        return num;
    }

    /**
     * 会员卡续费接口
     * @param param 用户id 会员卡编号
     */
    public UserCardParam cardRenew(CardRenewParam param){
        //得到用户持有会员卡的详细信息
        CardRenewInfoVo ret = new CardRenewInfoVo();
        Record extracted = userCardDao.getUserCardInfoBycardNo(param.getCardNo());
        if(extracted!=null) {
            ret = extracted.into(CardRenewInfoVo.class);
        }
        if (ret!=null){
            //should_renew_money
            //should_renew_date
            if (ret.getRenewMemberCard()==(byte)1){
                //设置支付时应当花费的钱或积分
                if (ret.getRenewType()==(byte)0){
                    ret.setShouldRenewMoney("￥"+ret.getRenewNum());
                }else {
                    ret.setShouldRenewMoney(ret.getRenewNum()+"积分");
                }
                //设置续费日期
                if (ret.getRenewDateType()==(byte)0){
                    ret.setShouldRenewDate(ret.getRenewTime()+"日");
                }else if (ret.getRenewDateType()==(byte)1){
                    ret.setShouldRenewDate(ret.getRenewTime()+"周");
                }else {
                    ret.setShouldRenewDate(ret.getRenewTime()+"月");
                }
            }
            if (ret.getExpireTime()!=null){
                ret.setStartTime(ret.getCreateTime());
                ret.setEndTime(ret.getExpireTime());
                //0:固定日期
                ret.setExpireType((byte)0);
            }else {
                //2:不过期
                ret.setExpireType((byte)2);
            }
            if (ret.getExpireType()!=(byte)2){
                if (ret.getExpireTime()!=null&&ret.getExpireTime().before(DateUtils.getLocalDateTime())){
                    ret.setStatus((byte)-1);
                }else {
                    ret.setStatus((byte)1);
                }
            }else {
                ret.setStatus((byte)-1);
            }
            //门店信息
            if (ret.getStoreList()!=null&&ret.getStoreUseSwitch()==(byte)0){
                List<Integer> storeList =
                    Arrays.stream(ret.getStoreList().substring(1,ret.getStoreList().length()-1).split(",")).map(Integer::parseInt)
                    .collect(Collectors.toList());
                List<StoreBasicVo> storeInfoList = getStoreList(storeList);
                if (storeInfoList!=null){
                    ret.setStoreInfoList(storeInfoList);
                }
            }
            //用户积分和余额
            UserRecord userRecord = userService.getUserByUserId(param.getUserId());
            ret.setScore(userRecord.getScore());
            ret.setAccount(userRecord.getAccount());

            List<RenewValidCardList> cardList = getRenewValidCardList(param.getUserId());
            Map<String,RenewValidCardList> memberCardList = new HashMap<>();
            String memberCardNo;
            if (cardList!=null&&cardList.size()>0){
                cardList.forEach(c-> memberCardList.put(c.getCardNo(),c));
                memberCardNo = cardList.get(0).getCardNo();
            }else {
                memberCardNo = "1";
            }

            ret.setMemberCardList(memberCardList);
            ret.setMemberCardNo(memberCardNo);
            ret.setCardFirst(tradeService.getCardFirst());
            ret.setBalanceFirst(tradeService.getBalanceFirst());
        }
        return ret;
    }

    /**
     * 获取指定门店信息
     * @param storeList 门店id集合
     * @return 门店id&门店name
     */
    private List<StoreBasicVo> getStoreList(List<Integer> storeList){
        List<StoreBasicVo> storeInfoList = db().select(STORE.STORE_ID,STORE.STORE_NAME)
            .from(STORE)
            .where(STORE.STORE_ID.in(storeList))
            .and(STORE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchInto(StoreBasicVo.class);
        return storeInfoList;
    }

    /**
     * 获取会员卡续费时有效用户会员卡列表
     * @param userId 用户id
     * @return
     */
    private List<RenewValidCardList> getRenewValidCardList(Integer userId){
        List<RenewValidCardList> cardList = db().select()
            .from(USER_CARD)
            .leftJoin(MEMBER_CARD).on(USER_CARD.CARD_ID.eq(MEMBER_CARD.ID))
            .where(USER_CARD.USER_ID.eq(userId))
            .and(USER_CARD.FLAG.eq((byte)0))
            .and(USER_CARD.EXPIRE_TIME.isNull().or(USER_CARD.EXPIRE_TIME.greaterThan(DateUtils.getSqlTimestamp())))
            .and(USER_CARD.MONEY.greaterThan(BigDecimal.ZERO))
            .orderBy(USER_CARD.IS_DEFAULT.desc(),USER_CARD.MONEY.desc())
            .fetchInto(RenewValidCardList.class);
        cardList.forEach(c->{
            if(StringUtils.isBlank(c.getBgColor())) {
                // 默认背景色
                c.setBgColor(CardUtil.getDefaultBgColor());
            }
            if (c.getExpireTime()!=null&&c.getExpireTime().before(DateUtils.getLocalDateTime())){
                c.setExpire(NumberUtils.BYTE_ONE);
            }else {
                c.setExpire(NumberUtils.BYTE_ZERO);
            }
            c.setStartDate(c.getCreateTime());
            if (c.getExpireTime()!=null){
                c.setEndDate(c.getExpireTime());
            }
        });
        return cardList;
    }

    /**
     * 会员卡续费支付完成接口
     * @param param 会员卡续费金额及时间详情
     * @return 过期时间&金额
     * @throws MpException
     */
    public CardRenewCheckoutVo renewCardCheckout(CardRenewCheckoutParam  param) throws MpException {
        Integer userId = param.getUserId();
        String cardNo = param.getCardNo();
        CardRenewCheckoutVo vo = new CardRenewCheckoutVo();
        UserCardParam memberCard = userCardDao.getUserCardInfo(cardNo);
        UserRecord userInfo = userService.getUserByUserId(userId);
        CardRenewRecord order = createRenewMemberOrder(userInfo,param,memberCard,vo);
        if(order==null){
            return null;
        }

        Timestamp expireTime = null;
        BigDecimal money = BigDecimal.ZERO;
        WebPayVo webPayVo = new WebPayVo();
        //现金
        if (order.getRenewType()==(byte)0){
            webPayVo = processCashPay(param, userId, userInfo, order, webPayVo);
            //修改会员卡过期时间
            expireTime = updateExpireTime(memberCard,order.getId());
            memberCard = userCardDao.getUserCardInfo(cardNo);
            money = memberCard.getMoney();
        }
        //积分
        else {
            if (order.getUseScore().compareTo(BigDecimal.ZERO)>0){
                logger().info("开始扣减积分");
                ScoreParam scoreParam = new ScoreParam();
                scoreParam.setScoreDis(userInfo.getScore());
                scoreParam.setUserId(userId);
                scoreParam.setScore(-order.getUseScore().intValue());
                scoreParam.setRemarkCode(RemarkTemplate.CARD_RENEW.code);
                scoreParam.setRemarkData("会员卡续费"+order.getRenewOrderSn());
                scoreParam.setChangeWay(61);
                scoreService.updateMemberScore(scoreParam,0,TYPE_SCORE_PAY.val(),TRADE_FLOW_IN.val());
            }
            //更新订单信息
            updateOrderInfo(order.getRenewOrderSn());
            //修改会员卡过期时间
            expireTime = updateExpireTime(memberCard,order.getId());
            memberCard = userCardDao.getUserCardInfo(cardNo);
            money = memberCard.getMoney();
        }
        vo.setExpireTime(expireTime);
        vo.setMoney(money);
        vo.setWebPayVo(webPayVo);
        return vo;
    }

    private WebPayVo processCashPay(CardRenewCheckoutParam param, Integer userId, UserRecord userInfo, CardRenewRecord order, WebPayVo webPayVo) throws MpException {
        //使用账户余额数量大于0
        if (order.getUseAccount().compareTo(BigDecimal.ZERO)>0){
            logger().info("开始扣减余额");
            AccountParam accountParam = new AccountParam();
            accountParam.setUserId(userId);
            accountParam.setAccount(userInfo.getAccount());
            accountParam.setOrderSn(order.getRenewOrderSn());
            accountParam.setAmount(new BigDecimal("-"+order.getUseAccount().toString()));
            accountParam.setPayment("balance");
            accountParam.setIsPaid((byte)1);
            accountParam.setRemarkId(RemarkTemplate.CARD_RENEW.code);
            accountParam.setRemarkData("会员卡续费"+order.getRenewOrderSn());
            //扣减余额
            accountService.updateUserAccount(accountParam,
                TradeOptParam.builder().tradeType(TYPE_CRASH_ACCOUNT_PAY.val()).tradeFlow(TRADE_FLOW_IN.val()).build());
        }
        if (order.getMemberCardRedunce().compareTo(BigDecimal.ZERO)>0){
            logger().info("开始增加会员卡消费记录");
            //增加会员卡消费记录
            UserCardParam cardInfo = userCardDao.getUserCardInfo(order.getMemberCardNo());
            UserCardConsumeBean userCardConsume = UserCardConsumeBean.builder()
                .userId(userId)
                .moneyDis(cardInfo.getMoney())
                .money(new BigDecimal("-"+order.getMemberCardRedunce().toString()))
                .cardNo(param.getMemberCardNo())
                .cardId(cardInfo.getCardId())
                .reasonId("3008")
                .reason(order.getRenewOrderSn())
                .type(NumberUtils.BYTE_ZERO)
                .payment("")
                .build();
            cardConsumer(userCardConsume);
        }
        //支付
        if (order.getMoneyPaid().compareTo(BigDecimal.ZERO)>0){
            //微信支付接口
            try {
                logger().info("会员卡续费微信支付-开始");
                UserRecord user = memberService.getUserRecordById(param.getUserId());
                MemberCardRecord cardInfo = userCardDao.getMemberCardById(param.getCardId());
                webPayVo = mpPaymentService.wxUnitOrder(param.getClientIp(), cardInfo.getCardName(), order.getRenewOrderSn(), param.getMoneyPaid(), user.getWxOpenid());
            } catch (WxPayException e) {
                logger().error("微信预支付调用接口失败WxPayException，订单号：{},异常：{}", order.getRenewOrderSn(), e);
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }catch (Exception e) {
                logger().error("微信预支付调用接口失败Exception，订单号：{},异常：{}", order.getRenewOrderSn(), e.getMessage());
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }
            logger().debug("会员卡续费-微信支付接口调用结果：{}", webPayVo);
            // 更新记录微信预支付id：prepayid
            cardOrderService.updatePrepayId(order.getRenewOrderSn(),webPayVo.getResult().getPrepayId());
        }
        //更新订单信息
        updateOrderInfo(order.getRenewOrderSn());
        return webPayVo;
    }

    /**
     * 创建会员卡续费支付订单
     * @param user
     * @param orderInfo
     * @param memberCard
     * @return
     */
    public CardRenewRecord createRenewMemberOrder(UserRecord user,CardRenewCheckoutParam orderInfo,UserCardParam memberCard,CardRenewCheckoutVo vo){
        //判断当前卡是否删除，是否可续费
        if (orderInfo.getCardNo()!=null){
            if (memberCard.getUserCardFlag()==(byte)1){
                vo.setFailMsg("该会员卡已删除");
            }
            if (memberCard.getRenewMemberCard()==(byte)0){
                vo.setFailMsg("该会员卡不可续费");
            }
        }
        //当前卡号为空-续费失败
        if(orderInfo.getCardId()==null){
            vo.setFailMsg("续费失败");
        }
        //账户余额
        BigDecimal accountNum = orderInfo.getUseAccount()!=null?orderInfo.getUseAccount():new BigDecimal(0);
        //会员卡余额
        BigDecimal memberCardReduce = orderInfo.getMemberCardBalance()!=null?orderInfo.getMemberCardBalance():new BigDecimal(0);
        //应付金额
        BigDecimal moneyPaid = orderInfo.getRenewNum().subtract(accountNum).subtract(memberCardReduce).setScale(2,BigDecimal.ROUND_HALF_UP);
        //积分值
        Integer scoreNum = orderInfo.getScoreNum()!=null?orderInfo.getScoreNum():0;
        //积分续费
        if (memberCard.getRenewType()==(byte)1){
            //校验用户积分
            if (scoreNum>user.getScore()){
                vo.setFailMsg("积分不足，无法下单");
            }
            //校验积分数量
            if (!orderInfo.getRenewNum().equals(memberCard.getRenewNum())){
                vo.setFailMsg("积分数量不对，无法下单");
            }
        }
        //现金支付 可用会员卡余额，余额，微信
        else {
            //校验余额数量
            if (!orderInfo.getRenewNum().equals(memberCard.getRenewNum())){
                vo.setFailMsg("金额数量不对，无法下单");
            }

            if (accountNum.compareTo(user.getAccount()) > 0){
                vo.setFailMsg("余额不足，无法下单");
            }
            if (memberCardReduce.compareTo(BigDecimal.ZERO)>0){
                BigDecimal memberCardMoney = userCardDao.getUserCardInfo(orderInfo.getMemberCardNo()).getMoney();
                if (memberCardReduce.compareTo(memberCardMoney)>0){
                    vo.setFailMsg("会员卡余额不足，无法下单");
                }
            }
            if (orderInfo.getMoneyPaid().compareTo(BigDecimal.ZERO)<0|| !orderInfo.getMoneyPaid().equals(moneyPaid)){
                vo.setFailMsg("应付金额计算错误");
            }
        }//else结束

        String orderSn = generateOrderSn();
        String payCode;
        //如果是积分支付
        if (memberCard.getRenewType()==(byte)1){
            payCode = "score";
            moneyPaid = BigDecimal.ZERO;
            memberCardReduce = BigDecimal.ZERO;
            accountNum = BigDecimal.ZERO;
        }
        //现金支付
        else{
            payCode = moneyPaid.compareTo(BigDecimal.ZERO) > 0?"wxpay":(accountNum.compareTo(BigDecimal.ZERO)>0?"balance":"member_card");
        }
        CardRenewRecord record = new CardRenewRecord();
        record.setCardId(orderInfo.getCardId());
        record.setCardNo(orderInfo.getCardNo());
        record.setRenewOrderSn(orderSn);
        record.setUserId(user.getUserId());
        record.setOrderStatus((byte)0);
        record.setPayment("");
        record.setPayCode(payCode);
        record.setMoneyPaid(moneyPaid);
        record.setMemberCardNo(orderInfo.getMemberCardNo());
        record.setMemberCardRedunce(memberCardReduce);
        record.setUseScore(new BigDecimal(scoreNum));
        record.setUseAccount(accountNum);
        record.setRenewMoney(orderInfo.getRenewNum()!=null?orderInfo.getRenewNum():BigDecimal.ZERO);
        record.setRenewTime(memberCard.getRenewTime());
        record.setRenewDateType(memberCard.getRenewDateType());
        record.setRenewType(memberCard.getRenewType());
        record.setAddTime(DateUtils.getSqlTimestamp());
        db().executeInsert(record);
        Integer id =  db().lastID().intValue();
        CardRenewRecord cardRenewRecord = db().select()
            .from(CARD_RENEW)
            .where(CARD_RENEW.ID.eq(id))
            .limit(1)
            .fetchOneInto(CardRenewRecord.class);
        return cardRenewRecord;
    }

    /**
     * 生成订单号
     * @return 订单号
     */
    public String generateOrderSn(){
        String orderSn = null;
        CardRenewRecord record = new CardRenewRecord();
        while (record!=null){
            Double randomNum = (Math.random()*(9999-1000+1)+1000);
            orderSn = "xf"+ DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE, DateUtils.getLocalDateTime())+randomNum.intValue();
            record = db().select().from(CARD_RENEW)
                .where(CARD_RENEW.RENEW_ORDER_SN.eq(orderSn))
                .limit(1)
                .fetchOneInto(CardRenewRecord.class);
        }
        return orderSn;
    }

    /**
     * 更新会员卡续费订单信息
     * @param orderSn
     */
    private void updateOrderInfo(String orderSn){
        db().update(CARD_RENEW)
            .set(CARD_RENEW.ORDER_STATUS,(byte)1)
            .set(CARD_RENEW.PAY_TIME, DateUtils.getSqlTimestamp())
            .where(CARD_RENEW.RENEW_ORDER_SN.eq(orderSn))
            .execute();
    }

    /**
     * 获取会员卡续费信息
     * @param orderSn 订单号
     * @return 会员卡续费信息
     */
    public CardRenewRecord get(String orderSn){
        return db().selectFrom(CARD_RENEW).where(CARD_RENEW.RENEW_ORDER_SN.eq(orderSn)).fetchAny();
    }

    /**
     * 更新用户会员卡过期时间
     * @param memberCard 会员卡信息
     * @return 新的过期时间
     */
    private Timestamp updateExpireTime(UserCardParam memberCard,Integer id){
        Timestamp expireTime = DateUtils.getLocalDateTime();
        //已过期
        if (memberCard.getExpireTime()==null||memberCard.getExpireTime().before(DateUtils.getLocalDateTime())){
            //'0:固定日期 1：自领取之日起N单位内有效'
            //date_type 0:日，1:周 2: 月
            if (memberCard.getRenewDateType()==(byte)0){
                expireTime = DateUtils.getTimeStampPlus(DateUtils.getLocalDateTime(),memberCard.getRenewTime(), ChronoUnit.DAYS);
            }else if (memberCard.getRenewDateType()==(byte)1){
                expireTime = DateUtils.getTimeStampPlus(DateUtils.getLocalDateTime(),memberCard.getRenewTime(), ChronoUnit.WEEKS);
            }else if (memberCard.getRenewDateType()==(byte)2){
                expireTime = DateUtils.getTimeStampPlus(DateUtils.getLocalDateTime(),memberCard.getRenewTime(), ChronoUnit.MONTHS);
            }
        }
        //未过期
        else {
            if (memberCard.getRenewDateType()==(byte)0){
                expireTime = DateUtils.getTimeStampPlus(memberCard.getExpireTime(),memberCard.getRenewTime(), ChronoUnit.DAYS);
            }else if (memberCard.getRenewDateType()==(byte)1){
                expireTime = DateUtils.getTimeStampPlus(memberCard.getExpireTime(),memberCard.getRenewTime(), ChronoUnit.WEEKS);
            }else if (memberCard.getRenewDateType()==(byte)2){
                expireTime = DateUtils.getTimeStampPlus(memberCard.getExpireTime(),memberCard.getRenewTime(), ChronoUnit.MONTHS);
            }
        }
        logger().info("开始更新用户会员卡过期时间");
        //更新用户-会员卡表
        db().update(USER_CARD)
            .set(USER_CARD.EXPIRE_TIME,expireTime)
            .where(USER_CARD.CARD_NO.eq(memberCard.getCardNo()))
            .execute();
        //更新会员卡续费表
        db().update(CARD_RENEW)
            .set(CARD_RENEW.RENEW_EXPIRE_TIME,expireTime)
            .where(CARD_RENEW.CARD_NO.eq(memberCard.getCardNo()))
            .and(CARD_RENEW.ID.eq(id))
            .execute();
        return expireTime;
    }

    /**
     * 购买结算
     * @param param
     */
    public CardBuyClearingVo toBuyCardClearing(CardBuyClearingParam param) {
        logger().info("会员卡-购买结算-开始");
        CardBuyClearingVo cardBuyVo =new CardBuyClearingVo();

        logger().info("会员卡-结算-店铺配置");
        ShopRecord shop = saas.shop.getShopById(this.getShopId());
        if(StringUtil.isNotEmpty(shop.getLogo())){
            cardBuyVo.setShopLogo(domainConfig.imageUrl(shop.getLogo()));
        }
        if(StringUtil.isNotEmpty(shop.getShopAvatar())){
            cardBuyVo.setShopAvatar(domainConfig.imageUrl(shop.getShopAvatar()));
        }
        cardBuyVo.setInvoiceSwitch(shopCommonConfigService.getInvoice());
        cardBuyVo.setScoreProportion(memberService.score.scoreCfgService.getScoreProportion());
        cardBuyVo.setIsShowServiceTerms(shopCommonConfigService.getServiceTerms());
        if(cardBuyVo.getIsShowServiceTerms() == 1){
            cardBuyVo.setServiceChoose(tradeService.getServiceChoose());
            cardBuyVo.setServiceName(tradeService.getServiceName());
            cardBuyVo.setServiceDocument(tradeService.getServiceDocument());
        }
        logger().info("会员卡=结算-用户余额");
        UserRecord user = memberService.getUserRecordById(param.getUserId());
        cardBuyVo.setAccount(user.getAccount());
        cardBuyVo.setScore(user.getScore());
        logger().info("会员卡-结算-会员卡配置");
        MemberCardRecord cardInfo = userCardDao.getMemberCardById(param.getCardId());
        CardBuyClearingVo.CardInfo into = cardInfo.into(CardBuyClearingVo.CardInfo.class);
        cardBuyVo.setCardInfo(into);
        if (MCARD_ET_FIX.equals(into.getExpireType())){
            into.setStartTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,cardInfo.getStartTime()));
            into.setEndTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,cardInfo.getEndTime()));
        }
        if(StringUtil.isNotBlank(into.getBgImg())){
            into.setBgImg(domainConfig.imageUrl(into.getBgImg()));
        }
        if (BUY_BY_SCORE.equals(cardInfo.getPayType())){
            cardBuyVo.setOrderAmount(BigDecimal.ZERO);
            cardBuyVo.setMoneyPaid(BigDecimal.ZERO);
            cardBuyVo.setOrderPayScore(cardInfo.getPayFee().intValue());
        }else {
            cardBuyVo.setOrderAmount(cardInfo.getPayFee());
            cardBuyVo.setMoneyPaid(cardBuyVo.getOrderAmount());
        }
        logger().info("会员卡-购买结算-结束");
        return cardBuyVo;
    }

    /**
     * 支付微信接口
     * @param param
     * @return
     */
    public WebPayVo buyCardCreateOrder(CardToPayParam param) throws MpException {
        logger().info("会员卡创建订单-开始");
        UserRecord user = memberService.getUserRecordById(param.getUser().getUserId());
        MemberCardRecord cardInfo = userCardDao.getMemberCardById(param.getCardId());
        //校验
        checkIsCanOrder(param, user, cardInfo);
        //支付类型
        String payCode = param.getMoneyPaid().compareTo(BigDecimal.ZERO) > 0 ? OrderConstant.PAY_CODE_WX_PAY : (param.getScoreDiscount() > 0 ? OrderConstant.PAY_CODE_SCORE_PAY : OrderConstant.PAY_CODE_BALANCE_PAY);
        logger().info("会员卡创建订单-支付类型payCode:{}",payCode);
        VirtualOrderRecord insertVirtualOrderRecord = db().newRecord(VIRTUAL_ORDER);
        //保存订单
        String orderSn = saveOrderRecord(param, cardInfo, payCode, insertVirtualOrderRecord);
        WebPayVo vo = new WebPayVo();
        if(param.getMoneyPaid().compareTo(BigDecimal.ZERO) <= 0){
            logger().info("订单已支付");
            String s = this.finishPayCallback(insertVirtualOrderRecord, null);
            vo.setCardSn(s);
        }else {
            //微信支付接口
            try {
                vo = mpPaymentService.wxUnitOrder(param.getClientIp(), cardInfo.getCardName(), orderSn, param.getMoneyPaid(), user.getWxOpenid());
            } catch (WxPayException e) {
                logger().error("微信预支付调用接口失败WxPayException，订单号：{},异常：{}", orderSn, e);
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }catch (Exception e) {
                logger().error("微信预支付调用接口失败Exception，订单号：{},异常：{}", orderSn, e.getMessage());
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }
            logger().debug("优惠券礼包-微信支付接口调用结果：{}", vo);
            // 更新记录微信预支付id：prepayid
            cardOrderService.updatePrepayId(orderSn,vo.getResult().getPrepayId());
        }
        logger().info("会员卡创建订单-结束");
        vo.setOrderSn(orderSn);
        return vo;
    }

    /**
     * 保存
     * @param param
     * @param cardInfo
     * @param payCode
     * @param insertVirtualOrderRecord
     * @return
     */
    private String saveOrderRecord(CardToPayParam param, MemberCardRecord cardInfo, String payCode, VirtualOrderRecord insertVirtualOrderRecord) {
        String orderSn = IncrSequenceUtil.generateOrderSn(MEMBER_CARD_ORDER_SN_PREFIX);
        insertVirtualOrderRecord.setOrderSn(orderSn);
        insertVirtualOrderRecord.setUserId(param.getUser().getUserId());
        insertVirtualOrderRecord.setVirtualGoodsId(param.getCardId());
        insertVirtualOrderRecord.setOrderStatus(ORDER_STATUS_WAIT_PAY);
        insertVirtualOrderRecord.setInvoiceId(param.getInvoiceId());
        insertVirtualOrderRecord.setInvoiceDetail(param.getInvoiceDetail());
        insertVirtualOrderRecord.setPayCode(payCode);
        insertVirtualOrderRecord.setMoneyPaid(param.getMoneyPaid());
        insertVirtualOrderRecord.setUseAccount(param.getAccountDiscount());
        insertVirtualOrderRecord.setUseScore(param.getScoreDiscount());
        insertVirtualOrderRecord.setMemberCardBalance(param.getMemberCardBalance());
        insertVirtualOrderRecord.setCardNo(param.getCardNo());
        insertVirtualOrderRecord.setOrderAmount(param.getOrderAmount() == null ? BigDecimal.ZERO : param.getOrderAmount());
        insertVirtualOrderRecord.setGoodsType(GOODS_TYPE_MEMBER_CARD);
        insertVirtualOrderRecord.setAccessMode(cardInfo.getIsPay());
        insertVirtualOrderRecord.setCurrency(saas().shop.getCurrency(getShopId()));
        insertVirtualOrderRecord.setSendCardNo("");
        insertVirtualOrderRecord.insert();
        return orderSn;
    }

    /**
     * 检查是否能下单
     * @param param
     * @param user 用户信息
     * @param cardInfo 会员卡信息
     */
    private void checkIsCanOrder(CardToPayParam param, UserRecord user, MemberCardRecord cardInfo) {
        if(param.getAccountDiscount() != null && param.getAccountDiscount().compareTo(user.getAccount()) > 0){
            throw new BusinessException(JsonResultCode.CODE_BALANCE_INSUFFICIENT);
        }
        if(param.getScoreDiscount() != null && param.getScoreDiscount() > 0 && param.getScoreDiscount() > user.getScore()){
            throw new BusinessException(JsonResultCode.CODE_SCORE_INSUFFICIENT);
        }
        if (cardInfo.getDelFlag().equals(DelFlag.DISABLE_VALUE)){
            throw new BusinessException(JsonResultCode.CODE_USER_CARD_NONE);
        }
        BigDecimal scoreAccount = BigDecimal.valueOf(param.getScoreDiscount());
        if (BUY_BY_SCORE.equals(cardInfo.getPayType())){
            logger().info("会员卡创建订单-积分支付");
            if (scoreAccount.compareTo(cardInfo.getPayFee())!=0){
                throw new BusinessException(JsonResultCode.CODE_SCORE_INSUFFICIENT);
            }
        }
        //真实支付金额= 订单金额-积分-余额-会员卡
        BigDecimal actualPayment = cardInfo.getPayFee().subtract(scoreAccount).subtract(param.getAccountDiscount());
        if (actualPayment.compareTo(param.getMoneyPaid())!=0){
            logger().error("会员卡创建订单-真实支付金额计算错误");
            throw new BusinessException(JsonResultCode.CODE_AMOUNT_PAYABLE_CALCULATION_FAILED);
        }

    }

    /**
     * 支付回调
     */
    public String finishPayCallback(VirtualOrderRecord orderRecord, PaymentRecordRecord paymentRecord) throws MpException {
        logger().info("会员卡订单-支付完成(回调)-开始");
        if(orderRecord.getOrderStatus().equals(ORDER_STATUS_FINISHED)){
            return null;
        }
        orderRecord.setOrderStatus(ORDER_STATUS_FINISHED);
        orderRecord.setPaySn(paymentRecord==null?"":paymentRecord.getPaySn());
        orderRecord.setPayTime(DateUtils.getLocalDateTime());
        orderRecord.update();
        if(orderRecord.getUseScore() != null && orderRecord.getUseScore() > 0){
            ScoreParam scoreParam = new ScoreParam();
            scoreParam.setScore(- orderRecord.getUseScore());
            scoreParam.setUserId(orderRecord.getUserId());
            scoreParam.setOrderSn(orderRecord.getOrderSn());
            scoreParam.setRemarkCode(RemarkTemplate.ORDER_MAKE.code);
            scoreParam.setRemarkData(orderRecord.getOrderSn());
            try {
                memberService.score.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_PAY.val(), TRADE_FLOW_OUT.val());
            } catch (MpException e) {
                e.printStackTrace();
            }
        }
        if(BigDecimalUtil.greaterThanZero(orderRecord.getUseAccount())){
            AccountParam accountParam = new AccountParam() {{
                setUserId(orderRecord.getUserId());
                setAmount(orderRecord.getUseAccount().negate());
                setOrderSn(orderRecord.getOrderSn());
                setPayment(PAY_CODE_BALANCE_PAY);
                setIsPaid(UACCOUNT_CONSUMPTION.val());
                setRemarkId(RemarkTemplate.ORDER_MAKE.code);
                setRemarkData(orderRecord.getOrderSn());
            }};
            TradeOptParam tradeOptParam = TradeOptParam.builder()
                .tradeType(TYPE_CRASH_ACCOUNT_PAY.val())
                .tradeFlow(TRADE_FLOW_OUT.val())
                .build();
            try {
                memberService.account.updateUserAccount(accountParam,tradeOptParam);
            } catch (MpException e) {
                e.printStackTrace();
            }
        }
        List<String> strings = addUserCard(orderRecord.getUserId(), orderRecord.getVirtualGoodsId());
        List<VirtualOrderRecord> list = new ArrayList<>();
        list.add(orderRecord);
        saas.getShopApp(getShopId()).couponPack.sendCouponPack(list);
        orderRecord.setSendCardNo(strings.get(0));
        orderRecord.update();
        logger().info("会员卡订单-支付完成(回调)-结束");
        return strings.get(0);
    }

    /**
     *
     * @param param
     * @return
     */
    public CardOrdeerSnVo getCardNoByOrderSn(CardOrdeerSnParam param) {
        Record1<String> record = db().select(VIRTUAL_ORDER.SEND_CARD_NO).from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.ORDER_SN.eq(param.getOrderSn()))
            .and(VIRTUAL_ORDER.USER_ID.eq(param.getUserId()))
            .fetchOne();
        CardOrdeerSnVo vo =new  CardOrdeerSnVo();
        if (record!=null&&!Strings.isNullOrEmpty(record.component1())){
            vo.setCardNo(record.component1());
            return vo;
        }
        return vo ;
    }
    /**
     * 	删除所有的用户领取的该等级卡
     */
    public void deleteAllUserGradeCard(Integer cardId) {
        int num = userCardDao.setAllUserGradeCardDelete(cardId);
        logger().info(num+"个用户的等级卡被废除");
    }

    /**
     * 会员卡续费回调完成
     * @param order
     * @param paymentRecord
     * @throws MpException
     */
    public void cardRenewFinish(CardRenewRecord order,PaymentRecordRecord paymentRecord) throws MpException {
        UserRecord userInfo = db().selectFrom(USER).where(USER.WX_OPENID.eq(paymentRecord.getBuyerId())).limit(1).fetchAny();
        if (order.getUseAccount().compareTo(BigDecimal.ZERO)>0){
            logger().info("开始扣减余额");
            AccountParam accountParam = new AccountParam();
            accountParam.setUserId(userInfo.getUserId());
            accountParam.setAccount(userInfo.getAccount());
            accountParam.setOrderSn(order.getRenewOrderSn());
            accountParam.setAmount(new BigDecimal("-"+order.getUseAccount().toString()));
            accountParam.setPayment("balance");
            accountParam.setIsPaid((byte)1);
            accountParam.setRemarkId(RemarkTemplate.CARD_RENEW.code);
            accountParam.setRemarkData("会员卡续费"+order.getRenewOrderSn());
            //扣减余额
            accountService.updateUserAccount(accountParam,
                TradeOptParam.builder().tradeType(TYPE_CRASH_ACCOUNT_PAY.val()).tradeFlow(TRADE_FLOW_IN.val()).build());
        }
        if (order.getMemberCardRedunce().compareTo(BigDecimal.ZERO)>0){
            logger().info("开始增加会员卡消费记录");
            //增加会员卡消费记录
            UserCardParam cardInfo = userCardDao.getUserCardInfo(order.getMemberCardNo());
            UserCardConsumeBean userCardConsume = UserCardConsumeBean.builder()
                .userId(order.getUserId())
                .moneyDis(cardInfo.getMoney())
                .money(new BigDecimal("-"+order.getMemberCardRedunce().toString()))
                .cardNo(order.getMemberCardNo())
                .cardId(cardInfo.getCardId())
                .reasonId("3008")
                .reason(order.getRenewOrderSn())
                .type(NumberUtils.BYTE_ZERO)
                .build();
            cardConsumer(userCardConsume);
        }
        //更新订单信息
        updateOrderInfo(order.getRenewOrderSn());
        //修改会员卡过期时间
        UserCardParam memberCard = userCardDao.getUserCardInfo(order.getRenewOrderSn());
        updateExpireTime(memberCard,order.getId());
        TradesRecordRecord tradesRecord = db().newRecord(TRADES_RECORD);
        tradesRecord.setTradeNum(paymentRecord.getTotalFee());
        tradesRecord.setTradeSn(paymentRecord.getOrderSn());
        tradesRecord.setUserId(userInfo.getUserId());
        tradesRecord.setTradeContent((byte)0);
        tradesRecord.setTradeType((byte)1);
        tradesRecord.setTradeFlow((byte)0);
        tradesRecord.setTradeStatus((byte)0);
        tradesRecord.setTradeTime(DateUtils.getSqlTimestamp());
        db().executeInsert(tradesRecord);
        logger().info("会员卡续费-支付完成(回调)-结束");
    }

    /**
     * 获取会员卡权益
     * 会员折扣(折扣)
     * 会员权益(积分)
     * 会员专享商品
     * 包邮
     * 自定义权益
     * @param param
     */
    public RankCardToVo getInterestsByGrade(CardInterestsParam param) {
        MemberCardRecord gradeCard = wxCardDetailSvc.getGradeCardByGrade(param.getGrade());
        RankCardToVo resCard = memberCardService.cardDetailSvc.changeToGradeCardDetail(gradeCard);
        if (BaseConstant.YES.equals(resCard.getPayOwnGood())) {
            List<Integer> ownGoodsId = memberCardService.cardDetailSvc.getOwnGoodsId(resCard.getId());
            resCard.setPayOwnGoodNum(ownGoodsId.size());
        }
        return resCard;
    }

    /**
     * 会员卡续费记录列表
     *
     * @param param
     * @return
     */
    public PageResult<UserCardRenewListVo> getCardRenewList(UserCardRenewListParam param) {
        SelectConditionStep<? extends Record> select = db().select(CARD_RENEW.RENEW_ORDER_SN, CARD_RENEW.RENEW_TYPE, CARD_RENEW.USER_ID, MEMBER_CARD.CARD_NAME, USER_CARD.CARD_ID, USER.USERNAME, USER.MOBILE, CARD_RENEW.ADD_TIME, CARD_RENEW.RENEW_MONEY, CARD_RENEW.RENEW_TIME, CARD_RENEW.RENEW_DATE_TYPE, CARD_RENEW.RENEW_EXPIRE_TIME)
            .from(CARD_RENEW.leftJoin(USER_CARD).on(CARD_RENEW.CARD_NO.eq(USER_CARD.CARD_NO)))
            .leftJoin(USER).on(CARD_RENEW.USER_ID.eq(USER.USER_ID))
            .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(CARD_RENEW.CARD_ID))
            .where(CARD_RENEW.ORDER_STATUS.eq(CardConstant.CARD_RENEW_ORDER_STATUS_OK));
        select = renewBuildOptions(select, param);
        select = (SelectConditionStep<? extends Record>) select.orderBy(CARD_RENEW.ADD_TIME.desc());
        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), UserCardRenewListVo.class);
    }

    /**
     * @param select
     * @param param
     * @return
     */
    private SelectConditionStep<? extends Record> renewBuildOptions(SelectConditionStep<? extends Record> select, UserCardRenewListParam param) {
        if (param.getUserId() != null && param.getUserId() > 0) {
            select = select.and(CARD_RENEW.USER_ID.eq(param.getUserId()));
        }
        if (StringUtil.isNotBlank(param.getRenewOrderSn())) {
            select = select.and(CARD_RENEW.RENEW_ORDER_SN.contains(param.getRenewOrderSn()));
        }
        if (StringUtil.isNotBlank(param.getCardName())) {
            select = select.and(MEMBER_CARD.CARD_NAME.contains(param.getCardName()));
        }
        if (param.getCardId() != null) {
            select = select.and(CARD_RENEW.CARD_ID.eq(param.getCardId()));
        }
        if (StringUtil.isNotBlank(param.getUserInfo())) {
            select = select.and(USER.USERNAME.contains(param.getUserInfo()).or(USER.MOBILE.contains(param.getUserInfo())));
        }
        if (param.getStartTime() != null) {
            select = select.and(CARD_RENEW.ADD_TIME.ge(param.getStartTime()));
        }
        if (param.getEndTime() != null) {
            select = select.and(CARD_RENEW.ADD_TIME.le(param.getEndTime()));
        }
        if (param.getRenewMoneyMin() != null) {
            select = select.and(CARD_RENEW.RENEW_MONEY.ge(param.getRenewMoneyMin()));
        }
        if (param.getRenewMoneyMax() != null) {
            select = select.and(CARD_RENEW.RENEW_MONEY.le(param.getRenewMoneyMax()));
        }
        if (param.getRenewType() != null) {
            select = select.and(CARD_RENEW.RENEW_TYPE.eq(param.getRenewType()));
        }
        if (param.getRenewTimeMin() != null) {
            select = select.and(CARD_RENEW.RENEW_EXPIRE_TIME.ge(param.getRenewTimeMin()));
        }
        if (param.getRenewTimeMax() != null) {
            select = select.and(CARD_RENEW.RENEW_EXPIRE_TIME.le(param.getRenewTimeMax()));
        }
        return select;
    }

    /**
     * 会员卡续费记录
     * 图表分析的数据
     *
     * @param param
     * @return
     */
    public AnalysisVo cardRenewAnalysis(AnalysisParam param) {
        AnalysisVo analysisVo = new AnalysisVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_BEGIN, DateUtils.getLocalDateTime()));
            param.setStartTime(startDate);
            endDate = DateUtils.getLocalDateTime();
            param.setEndTime(endDate);
        }
        Map<java.sql.Date, List<CardRenewAnalysisBo>> orderGoodsMap = getRenewAnalysisOrderMap(param);

        Set<Integer> allUserIds = new HashSet<>();
        //填充
        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            java.sql.Date k = new Date(startDate.getTime());
            List<CardRenewAnalysisBo> v = orderGoodsMap.get(k);
            Set<Integer> userIds = new HashSet<>();

            if (v != null) {
                /**支付金额 */
                BigDecimal paymentAmount = BigDecimal.ZERO;
                for (CardRenewAnalysisBo o : v) {
                    userIds.add(o.getUserId());
                    paymentAmount = BigDecimalUtil.addOrSubtrac(
                        BigDecimalUtil.BigDecimalPlus.create(paymentAmount, BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getMoneyPaid(), BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getUseAccount(), BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getMemberCardRedunce(), BigDecimalUtil.Operator.add));
                }

                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaidOrderNumber().add(v.size());
                analysisVo.getPaidUserNumber().add(userIds.size());
                analysisVo.getPaymentAmount().add(paymentAmount);
                analysisVo.getReturnAmount().add(BigDecimal.ZERO);
            } else {
                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaidOrderNumber().add(0);
                analysisVo.getPaidUserNumber().add(0);
                analysisVo.getPaymentAmount().add(BigDecimal.ZERO);
                analysisVo.getReturnAmount().add(BigDecimal.ZERO);
            }
            allUserIds.addAll(userIds);
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }

        AnalysisVo.AnalysisTotalVo totalVo = new AnalysisVo.AnalysisTotalVo();
        totalVo.setTotalPaidOrderNumber(analysisVo.getPaidOrderNumber().stream().mapToInt(Integer::intValue).sum());
        totalVo.setTotalPaidUserNumber(allUserIds.size());
        totalVo.setTotalPaymentAmount(analysisVo.getPaymentAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        totalVo.setTotalReturnAmount(BigDecimal.ZERO);
        analysisVo.setTotal(totalVo);
        return analysisVo;
    }

    /**
     * 导出续费记录
     *
     * @param param
     * @param lang
     * @return
     */
    public Workbook exportRenewList(UserCardRenewListParam param, String lang) {
        SelectConditionStep<? extends Record> select = db().select(CARD_RENEW.RENEW_ORDER_SN, MEMBER_CARD.CARD_NAME, USER_CARD.CARD_ID, USER.USERNAME, USER.MOBILE, CARD_RENEW.ADD_TIME, CARD_RENEW.RENEW_MONEY, CARD_RENEW.RENEW_TYPE, CARD_RENEW.RENEW_TIME, CARD_RENEW.RENEW_DATE_TYPE, CARD_RENEW.RENEW_EXPIRE_TIME)
            .from(CARD_RENEW.leftJoin(USER_CARD).on(CARD_RENEW.CARD_NO.eq(USER_CARD.CARD_NO)))
            .leftJoin(USER).on(CARD_RENEW.USER_ID.eq(USER.USER_ID))
            .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(CARD_RENEW.CARD_ID))
            .where(CARD_RENEW.ORDER_STATUS.eq(CardConstant.CARD_RENEW_ORDER_STATUS_OK));
        select = renewBuildOptions(select, param);
        select = (SelectConditionStep<? extends Record>) select.orderBy(CARD_RENEW.ADD_TIME.desc());
        List<UserCardRenewExportVo> list = select.fetchInto(UserCardRenewExportVo.class);

        list.forEach(o -> {
            if (CardConstant.USER_CARD_RENEW_TYPE_CASH.equals(o.getRenewType())) {
                o.setRenewMoneyString(o.getRenewMoney().toString() + Util.translateMessage(lang, JsonResultMessage.USER_CARD_RENEW_CASH, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else if (CardConstant.USER_CARD_RENEW_TYPE_INTEGRAL.equals(o.getRenewType())) {
                o.setRenewMoneyString(o.getRenewMoney().toString() + Util.translateMessage(lang, JsonResultMessage.USER_CARD_RENEW_INTEGRAL, OrderConstant.LANGUAGE_TYPE_EXCEL));
            }

            if (CardConstant.USER_CARD_RENEW_DATE_TYPE_DAY.equals(o.getRenewDateType())) {
                o.setRenewTimeString(o.getRenewTime().toString() + Util.translateMessage(lang, JsonResultMessage.USER_CARD_RENEW_DAY, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else if (CardConstant.USER_CARD_RENEW_DATE_TYPE_WEEK.equals(o.getRenewDateType())) {
                o.setRenewTimeString(o.getRenewTime().toString() + Util.translateMessage(lang, JsonResultMessage.USER_CARD_RENEW_WEEK, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else if (CardConstant.USER_CARD_RENEW_DATE_TYPE_MONTH.equals(o.getRenewDateType())) {
                o.setRenewTimeString(o.getRenewTime().toString() + Util.translateMessage(lang, JsonResultMessage.USER_CARD_RENEW_MONTH, OrderConstant.LANGUAGE_TYPE_EXCEL));
            }
        });

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(list, UserCardRenewExportVo.class);
        return workbook;
    }

    /**
     * 会员卡续费订单数据
     *
     * @param param
     * @return
     */
    private Map<java.sql.Date, List<CardRenewAnalysisBo>> getRenewAnalysisOrderMap(AnalysisParam param) {
        List<CardRenewAnalysisBo> list = db().select(DSL.date(CARD_RENEW.ADD_TIME).as("createTime"), CARD_RENEW.RENEW_ORDER_SN, CARD_RENEW.USER_ID, CARD_RENEW.MONEY_PAID, CARD_RENEW.USE_ACCOUNT, CARD_RENEW.MEMBER_CARD_REDUNCE)
            .from(CARD_RENEW)
            .where(CARD_RENEW.ADD_TIME.between(param.getStartTime(), param.getEndTime()))
            .and(CARD_RENEW.ORDER_STATUS.eq(CardConstant.CARD_RENEW_ORDER_STATUS_OK))
            .fetchInto(CardRenewAnalysisBo.class);
        return list.stream().collect(Collectors.groupingBy(CardRenewAnalysisBo::getCreateTime));
    }

    /**
     * 会员卡充值订单数据
     *
     * @param param
     * @return
     */
    private Map<java.sql.Date, List<CardChargeAnalysisBo>> getChargeAnalysisOrderMap(AnalysisParam param) {
        List<CardChargeAnalysisBo> list = db().select(DSL.date(CHARGE_MONEY.CREATE_TIME).as("createTime"), CHARGE_MONEY.ORDER_SN, CHARGE_MONEY.USER_ID, CHARGE_MONEY.CHARGE, CHARGE_MONEY.RETURN_MONEY)
            .from(CHARGE_MONEY)
            .where(CHARGE_MONEY.CREATE_TIME.between(param.getStartTime(), param.getEndTime()))
            .and(CHARGE_MONEY.CHARGE.ge(BigDecimal.ZERO))
            .and(CHARGE_MONEY.TYPE.eq(ZERO))
            .and(CHARGE_MONEY.CHANGE_TYPE.ge(CardConstant.CHARGE_SEND_CARD))
            .and(CHARGE_MONEY.CHANGE_TYPE.le(CardConstant.CHARGE_ADMIN_OPT))
            .fetchInto(CardChargeAnalysisBo.class);
        return list.stream().collect(Collectors.groupingBy(CardChargeAnalysisBo::getCreateTime));
    }

    /**
     * 会员卡充值记录列表
     *
     * @param param
     * @return
     */
    public PageResult<UserCardChargeListVo> getCardChargeList(UserCardChargeListParam param, String language) {
        SelectConditionStep<? extends Record> select = db().select(CHARGE_MONEY.ORDER_SN, CHARGE_MONEY.USER_ID, MEMBER_CARD.CARD_NAME, USER_CARD.CARD_ID, USER.USERNAME, USER.MOBILE, CHARGE_MONEY.CHARGE, CHARGE_MONEY.CREATE_TIME, CHARGE_MONEY.RETURN_MONEY, CHARGE_MONEY.AFTER_CHARGE_MONEY, CHARGE_MONEY.REASON_ID, CHARGE_MONEY.CHANGE_TYPE)
            .from(CHARGE_MONEY.leftJoin(USER_CARD).on(CHARGE_MONEY.CARD_NO.eq(USER_CARD.CARD_NO)))
            .leftJoin(USER).on(CHARGE_MONEY.USER_ID.eq(USER.USER_ID))
            .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(CHARGE_MONEY.CARD_ID))
            //已完成的
            .where(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_NORMAL)
                .and(CHARGE_MONEY.CHARGE.gt(BigDecimal.ZERO)))
                .and(CHARGE_MONEY.CHANGE_TYPE.eq(CardConstant.CHARGE_USER_POWER));
        select = chargeBuildOptions(select, param);
        select = (SelectConditionStep<? extends Record>) select.orderBy(CHARGE_MONEY.CREATE_TIME.desc());
        PageResult<UserCardChargeListVo> result = getPageResult(select, param.getCurrentPage(), param.getPageRows(), UserCardChargeListVo.class);
        for (UserCardChargeListVo vo : result.dataList) {
            String reason = RemarkUtil.remarkI18N(language, vo.getReasonId(), vo.getReason());
            vo.setReason(reason);
        }
        return result;
    }

    /**
     * @param select
     * @param param
     * @return
     */
    private SelectConditionStep<? extends Record> chargeBuildOptions(SelectConditionStep<? extends Record> select, UserCardChargeListParam param) {
        if (param.getUserId() != null && param.getUserId() > 0) {
            select = select.and(CHARGE_MONEY.USER_ID.eq(param.getUserId()));
        }
        if (StringUtil.isNotBlank(param.getOrderSn())) {
            select = select.and(CHARGE_MONEY.ORDER_SN.contains(param.getOrderSn()));
        }
        if (StringUtil.isNotBlank(param.getCardName())) {
            select = select.and(MEMBER_CARD.CARD_NAME.contains(param.getCardName()));
        }
        if (param.getCardId() != null) {
            select = select.and(CHARGE_MONEY.CARD_ID.eq(param.getCardId()));
        }
        if (param.getChangeType() != null && param.getChangeType() > 0) {
            select = select.and(CHARGE_MONEY.CHANGE_TYPE.eq(param.getChangeType()));
        }
        if (StringUtil.isNotBlank(param.getUserInfo())) {
            select = select.and(USER.USERNAME.contains(param.getUserInfo()).or(USER.MOBILE.contains(param.getUserInfo())));
        }
        if (param.getCreateTimeMin() != null) {
            select = select.and(CHARGE_MONEY.CREATE_TIME.ge(param.getCreateTimeMin()));
        }
        if (param.getCreateTimeMax() != null) {
            select = select.and(CHARGE_MONEY.CREATE_TIME.le(param.getCreateTimeMax()));
        }
        if (param.getChargeMin() != null) {
            select = select.and(CHARGE_MONEY.CHARGE.ge(param.getChargeMin()));
        }
        if (param.getChargeMax() != null) {
            select = select.and(CHARGE_MONEY.CHARGE.le(param.getChargeMax()));
        }
        if (param.getAfterChargeMoneyMin() != null) {
            select = select.and(CHARGE_MONEY.AFTER_CHARGE_MONEY.ge(param.getAfterChargeMoneyMin()));
        }
        if (param.getAfterChargeMoneyMax() != null) {
            select = select.and(CHARGE_MONEY.AFTER_CHARGE_MONEY.le(param.getAfterChargeMoneyMax()));
        }
        return select;
    }

    /**
     * 会员卡续费记录
     * 图表分析的数据
     *
     * @param param
     * @return
     */
    public AnalysisVo cardChargeAnalysis(AnalysisParam param) {
        AnalysisVo analysisVo = new AnalysisVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_BEGIN, DateUtils.getLocalDateTime()));
            param.setStartTime(startDate);
            endDate = DateUtils.getLocalDateTime();
            param.setEndTime(endDate);
        }
        Map<java.sql.Date, List<CardChargeAnalysisBo>> orderGoodsMap = getChargeAnalysisOrderMap(param);

        Set<Integer> allUserIds = new HashSet<>();
        //填充
        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            java.sql.Date k = new Date(startDate.getTime());
            List<CardChargeAnalysisBo> v = orderGoodsMap.get(k);
            Set<Integer> userIds = new HashSet<>();

            if (v != null) {
                /**支付金额 */
                BigDecimal paymentAmount = BigDecimal.ZERO;

                BigDecimal returnAmount = BigDecimal.ZERO;
                for (CardChargeAnalysisBo o : v) {
                    userIds.add(o.getUserId());
                    paymentAmount = BigDecimalUtil.addOrSubtrac(
                        BigDecimalUtil.BigDecimalPlus.create(paymentAmount, BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getCharge(), BigDecimalUtil.Operator.add));
                    returnAmount = BigDecimalUtil.addOrSubtrac(
                        BigDecimalUtil.BigDecimalPlus.create(returnAmount, BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getReturnMoney(), BigDecimalUtil.Operator.add));
                }

                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaidOrderNumber().add(v.size());
                analysisVo.getPaidUserNumber().add(userIds.size());
                analysisVo.getPaymentAmount().add(paymentAmount);
                analysisVo.getReturnAmount().add(returnAmount);
            } else {
                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaidOrderNumber().add(0);
                analysisVo.getPaidUserNumber().add(0);
                analysisVo.getPaymentAmount().add(BigDecimal.ZERO);
                analysisVo.getReturnAmount().add(BigDecimal.ZERO);
            }
            allUserIds.addAll(userIds);
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }

        AnalysisVo.AnalysisTotalVo totalVo = new AnalysisVo.AnalysisTotalVo();
        totalVo.setTotalPaidOrderNumber(analysisVo.getPaidOrderNumber().stream().mapToInt(Integer::intValue).sum());
        totalVo.setTotalPaidUserNumber(allUserIds.size());
        totalVo.setTotalPaymentAmount(analysisVo.getPaymentAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        totalVo.setTotalReturnAmount(BigDecimal.ZERO);
        analysisVo.setTotal(totalVo);
        return analysisVo;
    }

    /**
     * 导出充值记录
     *
     * @param param
     * @param lang
     * @return
     */
    public Workbook exportChargeList(UserCardChargeListParam param, String lang) {
        SelectConditionStep<? extends Record> select = db().select(CHARGE_MONEY.ORDER_SN, CHARGE_MONEY.USER_ID, MEMBER_CARD.CARD_NAME, USER_CARD.CARD_ID, USER.USERNAME, USER.MOBILE, CHARGE_MONEY.CHARGE, CHARGE_MONEY.CREATE_TIME, CHARGE_MONEY.RETURN_MONEY, CHARGE_MONEY.AFTER_CHARGE_MONEY, CHARGE_MONEY.REASON_ID, CHARGE_MONEY.CHANGE_TYPE)
            .from(CHARGE_MONEY.leftJoin(USER_CARD).on(CHARGE_MONEY.CARD_NO.eq(USER_CARD.CARD_NO)))
            .leftJoin(USER).on(CHARGE_MONEY.USER_ID.eq(USER.USER_ID))
            .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(CHARGE_MONEY.CARD_ID))
            //已完成的
            .where(MEMBER_CARD.CARD_TYPE.eq(MCARD_TP_NORMAL).and(CHARGE_MONEY.CHARGE.gt(BigDecimal.ZERO)));
        select = chargeBuildOptions(select, param);
        select = (SelectConditionStep<? extends Record>) select.orderBy(CHARGE_MONEY.CREATE_TIME.desc());
        List<UserCardChargeExportVo> list = select.fetchInto(UserCardChargeExportVo.class);

        list.forEach(o -> {
            String reason = RemarkUtil.remarkI18N(lang, o.getReasonId(), o.getReason());
            o.setReason(reason);
        });

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(list, UserCardChargeExportVo.class);
        return workbook;
    }
}

