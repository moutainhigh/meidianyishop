package com.meidianyi.shop.service.pojo.shop.member.card;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月19日
* @Description: 用户持有会员卡详情
*/
@Data
public class UserCardDetailVo {
	/** 会员id */
	private Integer userId;
	/** 卡id */
	private Integer cardId;
	/** - 领取时间 */
	private Timestamp createTime;
	/** - 过期时间 */
	private Timestamp expireTime;
	/** - 会员卡号 */
	private String cardNo;
	/** - 用户名 */
	private String username;
	/** - 会员卡类型 */
	private String cardType;
	/** - 会员卡名称 */
	private String cardName;
	/** -使用状态 */
	private String flag;
	/** - 余额 */
	private BigDecimal money;
	/** - 门店服务次数（次） */
	private Integer surplus;
	/** - 兑换商品次数（次） */
	@JsonProperty("exchang_surplus")
	private Integer exchangSurplus;
	/**
	 * 	包邮信息
	 */
	private CardFreeship freeShip;
	
}
