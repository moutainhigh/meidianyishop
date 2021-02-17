package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月29日
* @Description: 会员卡订单查询-入参
*/
@Data
public class CardConsumeParam {
	/** -每页总数 */
	public Integer pageRows;
	/** -当前页 */
	public Integer currentPage;
	/**   会员卡id */
	private Integer cardId;
	/**   订单号 */
	private String orderSn;
	/**   会员昵称 */
	private String username;
	/**   手机号 */
	private String mobile;
	/**   次数使用类型 */
	private Byte type;
	
	/**   次数变动时间 - 开始 */
	private Timestamp firstTime;
	/**   次数变动时间 - 结束 */
	private Timestamp secondTime;
	
}
