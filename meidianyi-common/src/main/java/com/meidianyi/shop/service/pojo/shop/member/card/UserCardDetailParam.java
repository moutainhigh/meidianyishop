package com.meidianyi.shop.service.pojo.shop.member.card;


import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月19日
* @Description: 用户查询入参参数
*/
@Data
public class UserCardDetailParam {
	/** - 用户id */
	private Integer userId;
	/** -手机号 */
	private String mobile; 
	/** -昵称 */
	private String username;
	/** -领取时间： 开始 */
	private String createTimeFirst;
	/** -领取时间：结束 */
	private String createTimeSecond;
	/** - 会员卡类型 */
	private Byte cardType;
	/** - 会员卡id */
	private Integer cardId;
	/**
	 * 会员卡状态
	 */
	private Byte statusValue;
}
