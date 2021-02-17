package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月29日
* @Description: 会员卡订单出参
*/
@Data
public class CardConsumeVo {
	/**   订单 */
	private String orderSn;
	/**   商品兑换次数 */
	private Short exchangCount;
	/**   门店兑换次数 */
	private Short count;
	/**   次数变动时间 */
	private Timestamp createTime;
	/**   用户名 */
	private String username;
	/**   手机号 */
	private String mobile;
	/**   商品名称 */
	private String goodsName;
	/**   商品图片 */
	private String goodsImg;
}
