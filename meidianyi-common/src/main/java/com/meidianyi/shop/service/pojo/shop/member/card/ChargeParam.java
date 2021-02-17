package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月26日
* @Description: 充值明细-入参数
*/
@Data
public class ChargeParam {
	/** -每页总数 */
	public Integer pageRows;
	/** -当前页 */
	public Integer currentPage;
	private Integer userId;
	/**   会员卡id */
	private Integer cardId;
	/**   会员卡类型 */
	private Byte cardType;
	/**   会员昵称 */
	private String username;
	/**   手机号 */
	private String mobile;
	/**   余额变动时间 */
	private Timestamp startTime;
	private Timestamp endTime;

	private String cardNo;
}
