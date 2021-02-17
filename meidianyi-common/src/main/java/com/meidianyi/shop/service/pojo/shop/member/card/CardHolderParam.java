package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月24日
* @Description: 查询持卡会员信息
*/
@Data
public class CardHolderParam {
	/** -每页总数 */
	public Integer pageRows;
	/** -当前页 */
	public Integer currentPage;
	/** - 会员卡Id */
	private Integer cardId;
	/** - 会员ID */
	private Integer userId;
	/** - 昵称 */
	private String username;
	/** - 手机号 */
	private String mobile;
	/** - 会员卡号 */
	private String cardNo;
	/** - 0:正常，1:删除 2 已过期{@link com.meidianyi.shop.common.pojo.shop.member.card.CardConstant.CARD_USING } */
	private Byte flag;
	/** - 领卡时间 开始范围 */
	private Timestamp firstDateTime;
	/** - 领卡时间 结束范围 */
	private Timestamp secondDateTime;
	
	/**
	 * 	是否提交审核申请
	 */
	private Byte submitValue;
	
	/**
	 * 	卡审核状态
	 */
	private Byte examineStatusValue;
	
	/**
	 * 	有无消费记录
	 */
	private Byte consumeRecordValue;
	
	/**
	 * 	有无充值记录
	 */
	private Byte chargeRecordValue;
}
