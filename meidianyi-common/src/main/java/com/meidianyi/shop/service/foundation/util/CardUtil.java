package com.meidianyi.shop.service.foundation.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.EffectTimeBean;
import com.meidianyi.shop.service.pojo.shop.member.card.EffectTimeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.base.UserCardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardGive;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardGive.CardGiveSwitch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ISE_ALL;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ISE_PART;
/**
* @author 黄壮壮
* @Date: 2019年11月28日
* @Description: 抽离出的关于会员卡的工具类
*/
public class CardUtil {

	/**
	 * 	会员卡是颜色背景吗
	 *	@return true: 是，false: 不是
	 */
	public static boolean isBgColorType(Byte type) {
		return CardConstant.MCARD_BGT_COLOR.equals(type);
	}

	/**
	 * 	会员卡是图片背景吗
	 *	@return true: 是，false: 不是
	 */
	public static boolean isBgImgType(Byte type) {
		if(type==null) {
			return false;
		}
		return CardConstant.MCARD_BGT_IMG.equals(type);
	}

	/**
	 * 是否等级会员卡
	 */
	public static boolean isGradeCard(Byte cardType) {
		return CardConstant.MCARD_TP_GRADE.equals(cardType);
	}
	/**
	 * 	是否为限次卡
	 */
	public static boolean isLimitCard(Byte cardType) {
		return CardConstant.MCARD_TP_LIMIT.equals(cardType);
	}
	/**
	 * 是否为普通卡
	 */
	public static boolean isNormalCard(Byte cardType) {
		return  CardConstant.MCARD_TP_NORMAL.equals(cardType);
	}

	/**
	 * 	是否为等级卡或普通卡
	 */
	public static boolean isNormalOrGradeCard(Byte cardType) {
		return isNormalCard(cardType) || isGradeCard(cardType);
	}

	/**
	 * 	卡是否需要购买
	 * @return true 需要，false不需要
	 */
	public static boolean isNeedToBuy(Byte isPay) {
		return CardConstant.MCARD_ISP_BUY.equals(isPay);
	}

	/**
	 * 卡领取是否需要码
	 * @return true: 需要；false: 不需要
	 */
	public static boolean isNeedReceiveCode(Byte isPay) {
		return CardConstant.MCARD_ISP_CODE.equals(isPay);
	}
	/**
	 * 卡领取需要领取码
	 * @return true: 需要 ; false: 不需要
	 */
	public static boolean isReceiveByCode(Byte type) {
		return CardConstant.MCARD_REA_CODE.equals(type);
	}

	/**
	 * 卡领取需要密码
	 */
	public static boolean isReceiveByPwd(Byte type) {
		return CardConstant.MCARD_REA_PWD.equals(type);
	}

	/**
	 * 	卡是否永久有效
	 * @return true: 是，false: 不是
	 */
	public static boolean isCardTimeForever(Byte expireType) {
		return CardConstant.MCARD_ET_FOREVER.equals(expireType);
	}

	/**
	 * 卡是否为自领取之日起
	 * @param expireType
	 * @return true: 是，false: 不是
	 */
	public static boolean isCardTimeStartFrom(Byte expireType) {
		return CardConstant.MCARD_ET_DURING.equals(expireType);
	}


	/**
	 * 	卡固定日期有效
	 * @return true: 是，false: 不是
	 */
	public static boolean isCardFixTime(Byte expireType) {
		return CardConstant.MCARD_ET_FIX.equals(expireType);
	}

	/**
	 * 卡是否过期
	 * @return true: 已经过期，false: 未过期
	 */
	public static boolean isCardExpired(Timestamp endTime) {
		if(endTime==null) {
			return false;
		}
		return DateUtils.getLocalDateTime().after(endTime);
	}

	/**
	 * 是否可以兑换商品
	 * @return true: 是，false: 否
	 */
	public static boolean canExchangGoods(Byte isExchang) {
		return !CardConstant.MCARD_ISE_NON.equals(isExchang);
	}

	/**
	 * 是否可以兑换全部商品
	 * @return true 是，false 否
	 */
	public static boolean isExchangAllGoods(Byte isExchange) {
		return MCARD_ISE_ALL.equals(isExchange);
	}

	/**
	 * 是否可以兑换部分商品
	 * @return true 是，false 否
	 */
	public static boolean isExchangPartGoods(Byte isExchange) {
		return MCARD_ISE_PART.equals(isExchange);
	}

	/**
	 * 	是否可以再门店使用
	 * @return true: 可以，false: 不可以
	 */
	public static boolean canUseInStore(Byte type) {
		return !CardConstant.MCARD_STP_BAN.equals(type);
	}

	/**
	 * 获取卡适用的门店类型
	 */
	public static Byte getUseStoreType(Byte storeUseSwitch,String storeList) {
		Byte useStoreType = null;
		if(CardConstant.AVAILABLE_IN_STORE.equals(storeUseSwitch)) {
			List<Integer> ids = parseStoreList(storeList);
			if(ids.size()>0 && ids.get(0) != 0) {
				// 在部分门店使用
				useStoreType = CardConstant.MCARD_STP_PART;
			}else {
				// 全部门店使用
				useStoreType = CardConstant.MCARD_STP_ALL;
			}
		}else {
			// 不可在门店使用
			useStoreType = CardConstant.MCARD_STP_BAN;
		}
		return useStoreType;
	}



	/**
	 * 是否开卡送券
	 * @return true: 开卡送优惠券；false: 开卡不送优惠券
	 */
	public static boolean isOpenCardSendCoupon(Byte type) {
		return CardConstant.MCARD_SEND_COUPON_ON.equals(type);
	}

	/**
	 * 送优惠券
	 */
	public static boolean isSendCoupon(Byte type) {
		return CardConstant.MCARD_COUPON_TYPE.equals(type);
	}
	/**
	 * 送优惠卷礼包
	 */
	public static boolean isSendCouponPack(Byte type) {
		return CardConstant.MCARD_COUPON_PACK_TYPE.equals(type);
	}

	/**
	 * 解析卡的使用门店
	 */
	public static List<Integer> parseStoreList(String storeList){
		if(StringUtils.isBlank(storeList)) {
			return Collections.emptyList();
		}
		return Util.json2Object(storeList, new TypeReference<List<Integer>>() {
        }, false);
	}

	/**
	 * 解析优惠券id
	 */
	public static List<Integer> parseCouponList(String couponList){
		if(StringUtils.isBlank(couponList)) {
			return new ArrayList<Integer>();
		}
		return Util.splitValueToList(couponList);
	}

	/**
	 * 	解析激活配置信息
	 */
	public static List<String> parseActivationCfg(String activationCfg){
		if(StringUtils.isBlank(activationCfg)) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(Arrays.<String>asList(activationCfg.split(",")));
	}

	/**
	 * 卡是否可用
	 * @reture true: 可用；false: 不可用
	 */
	public static boolean isCardAvailable(Byte flag) {
		return CardConstant.MCARD_FLAG_USING.equals(flag);
	}

	/**
	 * 卡是否已经删除
	 * @return true: 已删除；false: 未删除
	 */
	public static boolean isCardDeleted(Byte type) {
		return CardConstant.MCARD_DF_YES.equals(type);
	}

	/**
	 * 卡是否有效
	 * @return true: 有效；false: 无效
	 */
	public static boolean isValidCard(MemberCardRecord card) {
		if(card==null) {
			return false;
		}
		if(isCardFixTime(card.getExpireType()) && isCardExpired(card.getEndTime())) {
			return false;
		}
		if(!isCardAvailable(card.getFlag())) {
			return false;
		}
		return true;
	}
	/**
	 * 	卡是否需要审核
	 * 	@return true: 需要审核；false: 不需要审核
	 */
	public static boolean isCardExamine(Byte type) {
		return CardConstant.MCARD_EXAMINE_ON.equals(type);
	}

	/**
	 * 获取会员卡默认背景色
	 * @return
	 */
	public static String getDefaultBgColor() {
		return "#e6cb96";
	}


	/**
	 * 获取卡的是否有效
	 * @param expireType 时间类型  0:固定日期 1：自领取之日起 2:不过期
	 * @param endTime 终止时间
	 * @return Integer 卡状态  -1 无效，1 有效
	 */
	public static Integer getStatus(Byte expireType,Timestamp endTime) {
		Integer status = 1;
		if(!isCardTimeForever(expireType) && isCardExpired(endTime)) {
			status = -1;
		}
		return status;
	}




	/**
	 * 	用户卡的有效时间
	 * @param card 卡信息
	 * @return 卡的有效期对象
	 */
	public static EffectTimeBean getUserCardEffectTime(EffectTimeParam card) {
		EffectTimeBean bean = new EffectTimeBean();
		//	按照领卡的时间快照将进行设置
		if(isCardFixTime(card.getExpireType()) &&
				card.getExpireTime() != null) {
			//	固定时间 取会员卡的设置的起始时间，以及领卡时设置的过期时间
			if(card.getStartTime() != null) {
				bean.setStartDate(card.getStartTime().toLocalDateTime().toLocalDate());
				bean.setStartTime(card.getStartTime());
			}
			if(card.getExpireTime() != null) {
				bean.setEndDate(card.getExpireTime().toLocalDateTime().toLocalDate());
				bean.setEndTime(card.getExpireTime());
			}
		}else {
            boolean isValid = isCardTimeStartFrom(card.getExpireType()) ||
                (isCardTimeForever(card.getExpireType()) &&
                    card.getExpireTime() != null);
            if(isValid) {
                //	自领取之日起 取用户领卡的时间  或者 永久有效，但是之前设置了有效期也取快照
                if(card.getCreateTime() != null) {
                    bean.setStartDate(card.getCreateTime().toLocalDateTime().toLocalDate());
                    bean.setStartTime(card.getCreateTime());
                }

                if(card.getExpireTime() != null) {
                    bean.setEndDate(card.getExpireTime().toLocalDateTime().toLocalDate());
                    bean.setEndTime(card.getExpireTime());
                }
            }
        }

		if(card.getExpireTime() != null) {
			//	取快照 有效期
			bean.setExpireType(NumberUtils.BYTE_ONE);
		}else {
			//	永久有效
			bean.setExpireType((byte)2);
		}

		return bean;
	}

	/**
	 * 	是否需要激活
	 * @return true 需要 false 不需要
	 */
	public static boolean isNeedActive(Byte activte) {
		return CardConstant.MCARD_ACT_YES.equals(activte);
	}

	/**
	 * 	会员卡是否停用
	 * @return true 是  false 否
	 */
	public static boolean isStopUsing(Byte flag) {
		return CardConstant.MCARD_FLAG_STOP.equals(flag);
	}

	/**
	 * 将time转化为localdate
	 */
	public static LocalDate timeToLocalDate(Timestamp time) {
		if(time==null) {
			return null;
		}else {
			return time.toLocalDateTime().toLocalDate();
		}
	}
	/**
	 * 	会员卡是否开启转赠模式
	 * @param cardGiveWay
	 * @return true开启 | false 禁止转赠
	 */
	public static boolean isCardGiveWway(Byte cardGiveWay) {
		CardGiveSwitch val = CardGive.CardGiveSwitch.values()[cardGiveWay];
		return CardGive.CardGiveSwitch.on.equals(val);
	}

	/**
	 * 	卡是否允许继续转赠
	 * 	@return true 可以继续转赠 |  false 不允许继续转赠
	 */
	public static boolean isCardGiveContinue(Byte cardGiveContinue) {
		CardGiveSwitch val = CardGive.CardGiveSwitch.values()[cardGiveContinue];
		return CardGive.CardGiveSwitch.on.equals(val);
	}

	/**
	 * 	卡来源是否正常
	 * @return true 正常来源  |  false 非正常来源
	 */
	public static boolean isCardSourceNormal(Byte cardSource) {
		return UserCardConstant.SOURCE_NORMAL.equals(cardSource);
	}

	/**
	 * 	卡来源与转赠
	 *	@return true 转赠 | false 非转赠
	 */
	public static boolean isCardSourceGiveWay(Byte cardSource) {
		return UserCardConstant.SOURCE_GIVE_WAY.equals(cardSource);
	}

    /**
     * 限次卡是否免运费
     * @param exchangFreight 运费策略 0免运费 1使用商品运费策略
     * @return true 免 | false 使用商品运费策略
     */
    public static boolean isFreeShipping(Byte exchangFreight) {
        return CardConstant.FREE_SHIPPING.equals(exchangFreight);
    }


	/**
	 * 	是否为无限制转赠
	 * @return true 无次数限制 || false: 有次数限制
	 */
	public static boolean isCardGiveAwayForeverTimes(Integer mostGiveAway) {
		return NumberUtils.INTEGER_ZERO.equals(mostGiveAway);
	}

}

