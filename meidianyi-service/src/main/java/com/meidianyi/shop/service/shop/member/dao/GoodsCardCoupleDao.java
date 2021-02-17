package com.meidianyi.shop.service.shop.member.dao;

import static com.meidianyi.shop.db.shop.Tables.GOODS_CARD_COUPLE;

import java.util.List;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.shop.tables.GoodsCardCouple;
import com.meidianyi.shop.db.shop.tables.records.GoodsCardCoupleRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

/**
* @author 黄壮壮
* @Date: 2019年11月8日
* @Description: b2c_goods_card_couple操作
*/
@Service
public class GoodsCardCoupleDao extends ShopBaseService {
	public List<GoodsCardCoupleRecord> selectGoodsCardCouple(Integer cardId, Byte type) {
		return  db().selectFrom(GOODS_CARD_COUPLE)
					.where(GOODS_CARD_COUPLE.CARD_ID.eq(cardId))
					.and(GOODS_CARD_COUPLE.TYPE.eq(type))
					.fetchInto(GoodsCardCoupleRecord.class);
	}

}
