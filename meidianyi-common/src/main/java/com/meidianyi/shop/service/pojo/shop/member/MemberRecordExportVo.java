package com.meidianyi.shop.service.pojo.shop.member;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年10月11日
* @Description:
*/
@ExcelSheet
@Data
public class MemberRecordExportVo {
	/** 会员ID */
	private String userId;
	
	/** 昵称 */
	private String username;
	
	/** 手机号 */
	private String mobile;
	
	/** OpenID */
	private String wxOpenid;
	
	/** 余额 */
	private BigDecimal account;
	
	/** 积分 */
	private Integer score;
	
	/** 来源  => 转为名称*/
	private String source;
	
	/** 注册时间 */
	private Timestamp createTime;
	
	/** 会员卡 */
	private String cardName;
	
	/** 会员标签 */
	private String tagName;
	
	/** 地址 */
	private String userAddress;
	
	/** 累计下单金额 */
	private BigDecimal orderAmount;
	
	/** 累计消费单数 */
	private Integer order;
	
	/** 累计退款金额 */
	private BigDecimal returnOrderMoney;
	
	/** 累计订单数 */
	private Integer returnOrder;

	
	/** 邀请人 */
	private String inviteUserName;
	
	/** 邀请人手机号 */
	private String inviteMobile;
	
	/** 获返利订单数量 */
	private Integer rebateOrderNum;
	
	/**获返利订单佣金总额(元) */
	private BigDecimal rebateMoney;
	
	/** 已提现佣金总额（元） */
	private BigDecimal withdrawMoney;
	
	/** 下级用户数 */
	private Integer sublayerNumber;
	
	/** 分销员等级 */
	private String levelName;
	/** 分销员分组 */
	private String groupName;
	
	
	
}
