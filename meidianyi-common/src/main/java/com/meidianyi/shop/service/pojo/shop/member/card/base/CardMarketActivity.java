package com.meidianyi.shop.service.pojo.shop.member.card.base;

/**
 * 会员卡营销活动
 * @author 黄壮壮
 *
 */
public enum CardMarketActivity {
	/**
	 * 优惠券 0
	 */
	COUPON,
	/**
	 * 会员价 1
	 */
	MEMBER_PRICE,
	
	/**
	 * 限时降价 2
	 */
	REDUCE_PRICE,
	
	/**
	 * 首单特惠 3
	 */
	FIRST_SPECIAL;
	
	public static void main(String ...args) {
		CardMarketActivity[] contain = CardMarketActivity.values();
		for(CardMarketActivity item: contain) {
			System.out.println(item);
		}
	}
}
