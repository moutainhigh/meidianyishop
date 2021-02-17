package com.meidianyi.shop.service.shop.card;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.foundation.util.DateUtils.IntervalType;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardExchangGoods;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardExchangGoods.GoodsCfg;
/**
 * 	会员卡兑换商品服务
 * @author 黄壮壮
 *
 */
@Service
public class CardExchangService extends ShopBaseService {
	/**
	 * 	兑换商品数量无限制
	 */
	public final static Integer NUM_INFINITE = NumberUtils.INTEGER_ZERO;
	/**
	 * 	获取会员卡兑换商品的配置信息
	 * @return CardExchangGoods 兑换商品配置数据信息
	 */
	public CardExchangGoods getCardExchangGoodsService(MemberCardRecord card){
		logger().info("获取会员卡的兑换商品配置信息");
		CardExchangGoods cardExGoods = new CardExchangGoods();
		//	是否可兑换商品
		Byte isExchang = card.getIsExchang();
		cardExGoods.setIsExchange(isExchang);

		//	商品兑换总次数
		cardExGoods.setExchangCount(card.getExchangCount());
		//	兑换时间类型
		cardExGoods.setExchangTimeType(card.getPeriodLimit());
		//	兑换商品周期内次数
		cardExGoods.setExchangTimeNum(card.getPeriodNum());
		//	运费策略
		cardExGoods.setExchangFreight(card.getExchangFreight());

		String exchangGoods = card.getExchangGoods();
		if(CardUtil.canExchangGoods(isExchang) && !StringUtils.isBlank(exchangGoods)) {
			if(CardUtil.isExchangPartGoods(isExchang)) {
				//	兑换部分商品
				List<GoodsCfg> goodsCfgList = parseExchangGoods(exchangGoods);
				cardExGoods.setExchangGoods(goodsCfgList);
			}else if(CardUtil.isExchangAllGoods(isExchang)) {
				//	兑换全部商品
				cardExGoods.setEveryGoodsMaxNum(Integer.parseInt(exchangGoods));
			}
		}
		return cardExGoods;
	}

	/**
	 * 	解析兑换商品数据
	 * @param exchangGoods 兑换商品存储的json数据
	 * @return GoodsCfg商品的配置信息列表
	 */
	private List<GoodsCfg> parseExchangGoods(String exchangGoods) {
		List<GoodsCfg> goodsCfgList = new ArrayList<GoodsCfg>();
        String leftBracket = "{";
        if(exchangGoods.startsWith(leftBracket)) {
			//	目前存储的数据格式为map
			Map<String,Integer> map = Util.json2Object(exchangGoods, new TypeReference<Map<String,Integer>>() {}, false);
			for(Map.Entry<String, Integer> entry: map.entrySet()) {
				GoodsCfg goodsCfg = new GoodsCfg();
				String key = entry.getKey();
				List<Integer> goodsId = Util.stringToList(key);
				goodsCfg.setGoodsIds(goodsId);
				goodsCfg.setMaxNum(entry.getValue());

				goodsCfgList.add(goodsCfg);
			}
		}else {
			//	逗号分隔的数据，直接解析
			GoodsCfg goodsCfg = new GoodsCfg();
			goodsCfg.setGoodsIds(Util.stringToList(exchangGoods));
			goodsCfg.setMaxNum(NUM_INFINITE);

			goodsCfgList.add(goodsCfg);
		}
		return goodsCfgList;
	}

	/**
	 * 	获取可兑换部分商品的所有Ids
	 * @param exchangGoods 兑换商品存储的json数据
	 * @return 兑换部分商品的所有Id
	 */
	public List<Integer> getExchangPartGoodsAllIds(String exchangGoods){
		List<Integer> goodsIds = new ArrayList<>();
		List<GoodsCfg> goodsCfgList = parseExchangGoods(exchangGoods);
		if(CollectionUtils.isNotEmpty(goodsCfgList)) {
			for(GoodsCfg item: goodsCfgList) {
				goodsIds.addAll(item.getGoodsIds());
			}
		}
		return goodsIds;
	}

	/**
	 * 获取兑换卡可兑换的商品Id
	 * @param card
	 * @return null 表示全部商品 | List<Integer> 可兑换商品的Id
	 */
	public List<Integer> getExchangGoodsAllIds(MemberCardRecord card){
		if(CardUtil.isExchangPartGoods(card.getIsExchang())) {
			return getExchangPartGoodsAllIds(card.getExchangGoods());
		}else {
			return null;
		}
	}

	/**
	 * 兑换商品时间限制范围
	 * @param periodLimit
	 * @return Timestamp[2] 包含开始时间，截止时间 | null 表示没有时间限制范围
	 */
	public Timestamp[] getIntervalTime(Byte periodLimit) {
		if(CardExchangGoods.TimeType.NO_LIMIT.val.equals(periodLimit)) {
			//	不限制
			return null;
		}
		IntervalType[] values = DateUtils.IntervalType.values();
		return DateUtils.getInterval(values[periodLimit-1]);
	}


	/**
	 * 获取兑换商品期间信息
	 */
	public static String getPeriodLimitDesc(Byte periodLimit,String lang) {
		List<String> descArr = CardFreeship.getFreeShipDesc(lang);
		Collections.reverse(descArr);
		ArrayList<String> tmpList = new ArrayList<>(descArr.subList(0, CardExchangGoods.TimeType.values().length));
		return tmpList.get(periodLimit-1);
	}
}
