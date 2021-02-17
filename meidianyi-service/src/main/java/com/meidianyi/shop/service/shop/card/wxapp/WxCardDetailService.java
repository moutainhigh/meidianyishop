package com.meidianyi.shop.service.shop.card.wxapp;

import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_DF_NO;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_FLAG_USING;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;
import com.meidianyi.shop.db.shop.tables.records.GiveCardRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.market.couponpack.CouponPackUpdateVo;
import com.meidianyi.shop.service.pojo.shop.member.account.NextGradeCardVo;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardCoupon;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardCouponPack;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardVo;
import com.meidianyi.shop.service.pojo.shop.member.account.WxAppCardExamineVo;
import com.meidianyi.shop.service.pojo.shop.member.account.WxAppUserCardVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBgBean;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.EffectTimeBean;
import com.meidianyi.shop.service.pojo.shop.member.card.EffectTimeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.RankCardToVo;
import com.meidianyi.shop.service.pojo.shop.member.card.base.UserCardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomAction;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomRights;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.card.dao.CardFullDetail;
import com.meidianyi.shop.service.pojo.shop.member.exception.UserCardNullException;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardExchangTipVo;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardGiveVo;
import com.meidianyi.shop.service.shop.card.CardDetailService;
import com.meidianyi.shop.service.shop.card.CardExchangService;
import com.meidianyi.shop.service.shop.card.CardFreeShipService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.market.couponpack.CouponPackService;
import com.meidianyi.shop.service.shop.member.CardVerifyService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.member.card.LimitBaseCardOpt;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import com.meidianyi.shop.service.shop.user.user.UserService;

/**
 * 	微信小程序会员卡详情
 * 	@author 黄壮壮
 *
 */
@Service
public class WxCardDetailService extends ShopBaseService{
	@Autowired private CardDetailService cardDetailSvc;
	@Autowired private UserCardDaoService userCardDao;
	@Autowired private MemberCardService memberCardSvc;
	@Autowired private CardFreeShipService cardFreeShipSvc;
	@Autowired private OrderInfoService orderInfoService;
	@Autowired private ScoreService scoreService;
	@Autowired private CardVerifyService cardVerifyService;
	@Autowired private QrCodeService qrCodeService;
	@Autowired private StoreService storeService;
	@Autowired private GoodsService goodsService;
	@Autowired private WxCardGiveAwaySerivce wxCardGiveAwaySvc;
	@Autowired private UserService userSvc;
	@Autowired private CouponService couponService;
	@Autowired private CouponPackService couponPackService;
	@Autowired private CardExchangService cardExchangSvc;
    @Autowired private WxCardExchangeService wxCardExchangSvc;
	@Autowired private LimitBaseCardOpt limitCardOpt;
	
	/**
	 * 	获取自定义的激活项
	 */
	public List<CardCustomAction> getNeedActivationCustomOptions(MemberCardRecord card) {
		List<CardCustomAction> customOptions = cardDetailSvc.getCustomAction(card);
		customOptions.removeIf(action-> action.getChecked().equals(NumberUtils.BYTE_ZERO));
		return customOptions;
	}
	
	
	
	public WxAppUserCardVo getUserCardDetail(UserCardParam param,String lang) throws UserCardNullException {
		logger().info("获取卡的详细信息");
		WxAppUserCardVo card = null;
		if(param.getCardId() != null) {
			card = (WxAppUserCardVo) userCardDao.getUserCardInfo(param.getUserId(),param.getCardId());
		}else {
			card = (WxAppUserCardVo) userCardDao.getUserCardInfo(param.getCardNo());
		}
		if (card == null) {
			throw new UserCardNullException();
		}
		
		CardFullDetail cardFullDetail = memberCardSvc.getCardDetailByNo(card.getCardNo());
		MemberCardRecord memberCardRecord = cardFullDetail.getMemberCard();
		UserCardRecord userCardRecord = cardFullDetail.getUserCard();

		dealWithUserCardDetailInfo(card);
		
		card.setCumulativeConsumptionAmounts(orderInfoService.getAllConsumpAmount(param.getUserId()));
		card.setCumulativeScore(scoreService.getAccumulationScore(param.getUserId()));
		logger().info("卡的校验状态");
		CardExamineRecord  cardExamine = cardVerifyService.getStatusByNo(param.getCardNo());
		if(cardExamine != null) {
			card.setCardVerifyStatus(cardVerifyService.getCardVerifyStatus(param.getCardNo()));
			WxAppCardExamineVo cardExamineVo = new WxAppCardExamineVo();
			cardExamineVo.setPassTime(cardExamine.getPassTime());
			cardExamineVo.setRefuseTime(cardExamine.getRefuseTime());
			cardExamineVo.setRefuseDesc(cardExamine.getRefuseDesc());
			cardExamineVo.setStatus(cardExamine.getStatus());
			card.setIsExamine(cardExamineVo);
		}
		setQrCode(card);

		if (CardUtil.isGradeCard(card.getCardType())) {

			NextGradeCardVo nextGradeCard = getNextGradeCard(card.getGrade());
			card.setNextGradeCard(nextGradeCard);
		}
		//	包邮信息
		dealWithFreeShipInfo(card,lang);
		//	自定义权益
		CardCustomRights customRights = memberCardSvc.cardDetailSvc.getCustomRights(memberCardRecord);
		card.setCardCustomRights(customRights);
		if(CardUtil.isLimitCard(card.getCardType())) {
			card.setCardGive(getCardGiveVo(memberCardRecord, userCardRecord));
			//	处理转赠信息
			if(!param.getUserId().equals(card.getUserId())) {
				card.setUserId(param.getUserId());
				card.setCardNo(null);
			}
		}
		//	优惠券
		UserCardVo voTmp = new UserCardVo();
		voTmp.setSendCouponType(memberCardRecord.getSendCouponType());
		voTmp.setSendCouponIds(memberCardRecord.getSendCouponIds());
		this.dealSendCouponInfo(voTmp, lang);
		card.setCouponPack(voTmp.getCouponPack());
		card.setCoupons(voTmp.getCoupons());
		
		if(CardUtil.isLimitCard(memberCardRecord.getCardType())) {
			CardExchangTipVo cardExchangTip = wxCardExchangSvc.getCardExchangTip(userCardRecord.getCardNo(), memberCardRecord);
			card.setCardExchangTip(cardExchangTip);
			card.setGoodsList(getExchangGoodsDetail(memberCardRecord));
		}
		
		return card;
	}

	/**
	 * 	获取用户卡的转赠数据
	 */
	private CardGiveVo getCardGiveVo(MemberCardRecord memberCardRecord, UserCardRecord userCardRecord) {
			logger().info("获取卡的转赠数据");
			//	转赠信息
			if(limitCardOpt.canGiveAway(userCardRecord,memberCardRecord,true)) {
				
				Byte giveWayStatus = userCardRecord.getGiveAwayStatus();
				if(UserCardConstant.GIVE_AWAY_ING.equals(giveWayStatus)) {
					//	转赠中
					GiveCardRecordRecord giveCardRecord = wxCardGiveAwaySvc.getNormalGiveCardRecordByCardNo(userCardRecord.getCardNo());
					if(giveCardRecord!=null) {
						return CardGiveVo.builder()
								.canGiveWay(NumberUtils.BYTE_ONE)
								.createTime(giveCardRecord.getCreateTime())
								.deadline(giveCardRecord.getDeadline())
								.build();
					}
				}else if(UserCardConstant.GIVE_WAY_SUCCESS.equals(giveWayStatus)) {
					//	转赠成功
					GiveCardRecordRecord giveCardRecord = wxCardGiveAwaySvc.getNormalGiveCardRecordByCardNo(userCardRecord.getCardNo());
					if(giveCardRecord !=null) {
						UserInfo user = userSvc.getUserInfo(giveCardRecord.getGetUserId());
						return CardGiveVo.builder()
									.canGiveWay(NumberUtils.BYTE_ONE)
									.createTime(giveCardRecord.getCreateTime())
									.deadline(giveCardRecord.getDeadline())
									.giveUsername(user != null?user.getUsername():null)
									.giveAwayTime(giveCardRecord.getGetTime())
									.build();
					}
				}else {
					//	卡可以转赠
					if(limitCardOpt.canGiveAway(userCardRecord,memberCardRecord,false)) {
						logger().info("卡设置是允许转赠");
						OrderInfoRecord orderRecord = orderInfoService.getNotFinishedOrderOfCard(userCardRecord.getCardNo());
						if(orderRecord != null) {
							//	还有未完成的订单
							return CardGiveVo.builder()
										.canGiveWay(NumberUtils.BYTE_ZERO)
										.cardOrderSn(orderRecord.getOrderSn())
										.build();
						}else {
							// 	转赠分享码
							CardGiveVo cardGiveVo = wxCardGiveAwaySvc.getCardGiveImg(memberCardRecord);
							cardGiveVo.setCanGiveWay(NumberUtils.BYTE_ONE);
							//	该用户卡可以转赠
							return cardGiveVo;
						}
					
					}
				}
			}
			
			return CardGiveVo.builder().canGiveWay(NumberUtils.BYTE_ZERO).build();

	}
	
	public void dealWithUserCardDetailInfo(WxAppUserCardVo card) {
		logger().info("处理wx 用户会员卡数据详情");
		dealWithUserCardBasicInfo(card);
		dealWithUserCardAvailableStore(card);
		
	}
	
	
	public void dealWithUserCardBasicInfo(WxAppUserCardVo card) {
		String avatar = getCardAvatar();
		dealWithWxUserCard(card, avatar);
	}
	

	/**
	 * 	获取用户卡的头像
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
	
	/**
	 * 	处理返回给微信端的用户卡数据
	 *
	 * @param card
	 */
	private void dealWithWxUserCard(WxAppUserCardVo card, String avatar) {
		card.calcCardIsExpired();
		card.calcRenewal();
		card.setAvatar(avatar);
		card.calcCash();

		// 背景
		CardBgBean bg = memberCardSvc.getBackground(card.getBgType(), card.getBgColor(), card.getBgImg());
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
	 * 	处理卡的包邮信息
	 */
	private void dealWithFreeShipInfo(WxAppUserCardVo card,String lang) {
		CardFreeship freeShip = cardFreeShipSvc.getFreeshipData(card, lang);
		card.setFreeshipDesc(freeShip.getDesc());
	}
	
	private void setQrCode(WxAppUserCardVo card) {
		MemberCardRecord mCard = memberCardSvc.getCardById(card.getCardId());
		String qrCode = qrCodeService.getUserCardQrCode(card.getCardNo(), mCard);
		card.setQrCode(qrCode);
	}
	
	private NextGradeCardVo getNextGradeCard(String currentGrade) {
		// 升级进度
		logger().info("当前会员卡的等级：" + currentGrade);

		Integer gVal = Integer.valueOf(currentGrade.substring(1));
		gVal = gVal + 1;
		int max = 10;
		while (gVal < max) {
			logger().info("设置会员升级的下一张等级卡");
			String newGrade = "v" + gVal;
			MemberCardRecord gradeCard = getGradeCardByGrade(newGrade);
			if(gradeCard==null) {
				gVal = gVal+1;
				continue;
			}
			RankCardToVo resCard = memberCardSvc.cardDetailSvc.changeToGradeCardDetail(gradeCard);
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
	
	/**
	 * 	处理可兑换的商品
	 */
	public List<GoodsSmallVo> getExchangGoodsDetail(MemberCardRecord card) {
		List<GoodsSmallVo> res = Collections.<GoodsSmallVo>emptyList();
		if(CardUtil.isLimitCard(card.getCardType()) && CardUtil.canExchangGoods(card.getIsExchang())) {
			logger().info("处理限次卡兑换的商品");
			if(CardUtil.isExchangPartGoods(card.getIsExchang())) {
				//	部分商品
				if(!StringUtils.isBlank(card.getExchangGoods())) {
					List<Integer> goodsIdList = cardExchangSvc.getExchangPartGoodsAllIds(card.getExchangGoods());
					res = goodsService.getGoodsList(goodsIdList, false);
				}
			}else {
				//	全部商品，只取两个进行展示
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
					goodsVo.setLimitExhangNum(wxCardExchangSvc.getCardAllowExchangGoodsTimes(goodsVo.getGoodsId(),card));
				}
			}
		}

		return res;
	}
	
	
	/**
	 * 	处理会员卡相应的优惠券信息
	 * @param userCard 需要有优惠券类型，优惠券Id
	 * @param lang
	 */
	public void dealSendCouponInfo(UserCardVo userCard,String lang) {
		logger().info("处理会员卡相应的优惠券信息");
		if(userCard == null) {
			return;
		}
		if (CardUtil.isSendCoupon(userCard.getSendCouponType())) {
			logger().info("正在生成优惠券信息");
			List<Integer> couponIds = CardUtil.parseCouponList(userCard.getSendCouponIds());
			if(couponIds == null || couponIds.size()==0) {
				return;
			}
			// 优惠券信息列表
			List<CouponView> couponList = couponService.getCouponViewByIds(couponIds);
			userCard.setCoupons(cardCouponI18N(couponList,lang));
		} else if (CardUtil.isSendCouponPack(userCard.getSendCouponType())) {
			logger().info("处理优惠券礼包");
			List<UserCardCouponPack> packList = new ArrayList<>();
			if (!StringUtils.isBlank(userCard.getSendCouponIds())) {
				int id = Integer.parseInt(userCard.getSendCouponIds());
				CouponPackUpdateVo couponPack = couponPackService.getCouponPackById(id);
				UserCardCouponPack pack = new UserCardCouponPack();
				if(couponPack != null) {
					pack = UserCardCouponPack.builder()
							.id(couponPack.getId())
							.actName(couponPack.getActName())
							.packName(couponPack.getPackName())
							.build();
					packList.add(pack);
				}
				userCard.setCouponPack(packList);
			}
		}
	}
	
	

	/**
	 * 	处理会员卡优惠券显示信息
	 */
	private List<UserCardCoupon> cardCouponI18N(List<CouponView> couponList,String lang) {
		logger().info("处理会员卡优惠券信息");
		String i18nfile = "member";
		List<UserCardCoupon> res = new ArrayList<>();
		if(couponList == null || couponList.size()==0) {
			return res;
		}
		for (CouponView coupon : couponList) {
			// 1-优惠券使用范围
			String couponCon = null;
			Byte suiteGoodsType;
			if (NumberUtils.BYTE_ZERO.equals(coupon.getSuitGoods())) {
				// 全部商品可用
				couponCon = JsonResultMessage.CARD_COUPON_CON_ALL;
				suiteGoodsType = NumberUtils.BYTE_ZERO;
			} else {
				// 部分商品可用
				couponCon = JsonResultMessage.CARD_COUPON_CON_PART;
				suiteGoodsType = NumberUtils.BYTE_ONE;
			}
			couponCon = Util.translateMessage(lang, couponCon, i18nfile);

			// 2-优惠券过期时间
			Integer day = coupon.getValidity();
			Integer hour = coupon.getValidityHour();
			Integer minute = coupon.getValidityMinute();
			String couponExpireTimeDesc = null;
			String desc="";
			Byte timeType;
			if (day > 0 || hour > 0 || minute > 0) {
				timeType = NumberUtils.BYTE_ZERO;
				StringBuilder con = new StringBuilder();

				// 领券日起
				String receiveInfo = Util.translateMessage(lang, JsonResultMessage.CARD_COUPON_RECEIVE_DAY_START, i18nfile);
				con.append(receiveInfo);

                StringBuilder timedhm = buildTimeDhm(lang, i18nfile, day, hour, minute, con);

                desc = con.toString();
				couponExpireTimeDesc = timedhm.toString();
			} else {
				timeType = NumberUtils.BYTE_ONE;
				String startTime = coupon.getStartTime().toLocalDateTime().toLocalDate().toString();
				String endTime = coupon.getEndTime().toLocalDateTime().toLocalDate().toString();
				couponExpireTimeDesc = startTime + "--" + endTime;
				desc = startTime + "--" + endTime;
			}

			// 3-优惠券使用条件限制
			String restrict = null;
			Byte restrictType;
			BigDecimal leastConsume=null;
			if (NumberUtils.BYTE_ZERO.equals(coupon.getUseConsumeRestrict())) {
				//	无门槛
				restrict = Util.translateMessage(lang,JsonResultMessage.CARD_COUPON_NOLIMIT, i18nfile);
				restrictType = NumberUtils.BYTE_ZERO;
			} else {
				//	满多少使用
				restrict = Util.translateMessage(lang, JsonResultMessage.CARD_COUPON_SATISFY, i18nfile, coupon.getLeastConsume());
				restrictType = NumberUtils.BYTE_ONE;
				leastConsume = coupon.getLeastConsume();
			}
			res.add(
					UserCardCoupon.builder()
						.id(coupon.getId())
						.name(coupon.getActName())
						.actCode(coupon.getActCode())
						.denomination(coupon.getDenomination())
						.suiteGoodsType(suiteGoodsType)
						.couponCondition(couponCon)
						.timeType(timeType)
						.desc(desc)
						.expireTime(couponExpireTimeDesc)
						.useConsumeRestrictDesc(restrict)
						.consumeRestrictType(restrictType)
						.leastConsume(leastConsume)
						.build()
					);
		}
		return res;
	}

    private StringBuilder buildTimeDhm(String lang, String i18nfile, Integer day, Integer hour, Integer minute, StringBuilder con) {
        StringBuilder timedhm = new StringBuilder();
        timedhm.append("{");
        int comma = 0;
        if (day> 0) {
            con.append(Util.translateMessage(lang, JsonResultMessage.CARD_COUPON_DAY, i18nfile, day));
            timedhm.append("\"day\": "+day);
            comma++;
        }
        if (hour > 0) {
            con.append(Util.translateMessage(lang, JsonResultMessage.CARD_COUPON_HOUR, i18nfile,hour));
            if(comma>0) {
                timedhm.append(",");
            }
            timedhm.append("\"hour\": "+hour);
            comma++;
        }
        if (minute > 0) {
            con.append(Util.translateMessage(lang, JsonResultMessage.CARD_COUPON_MINUTE, i18nfile,minute));
            if(comma>0) {
                timedhm.append(",");
            }
            timedhm.append("\"minute\": "+minute);
        }
        timedhm.append("}");
        return timedhm;
    }

}
