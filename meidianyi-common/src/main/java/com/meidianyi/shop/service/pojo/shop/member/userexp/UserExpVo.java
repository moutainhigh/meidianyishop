package com.meidianyi.shop.service.pojo.shop.member.userexp;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 * 	会员导出参数选择
 */
@Data
public class UserExpVo {
	/**
	 * 	用户id,必须导出的数据
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_USER_ID) 
	@JsonProperty(value=UserExpCont.EXP_USER_ID,index=0,required=true)
	private Integer userId;
	
	/**
	 * 	昵称
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_USERNAME)
	@JsonProperty(value=UserExpCont.EXP_USERNAME,index=1)
	private String username;
	
	/**
	 * 	手机号
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_MOBILE)
	@JsonProperty(value=UserExpCont.EXP_MOBILE,index=2)
	private String mobile;
	
	/**
	 * OpenID
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_WX_OPENID) 
	@JsonProperty(value=UserExpCont.EXP_WX_OPENID,index=3)
	private String wxOpenid;
	
	/**
	 *	 余额
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_ACCOUNT) 
	@JsonProperty(value=UserExpCont.EXP_ACCOUNT,index=4)
	private BigDecimal account;
	
	/**
	 * 	积分
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_SCORE)
	@JsonProperty(value=UserExpCont.EXP_SCORE,index=5)
	private Integer score;
	
	/**
	 * 用户来源
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_USER_SOURCE) 
	@JsonProperty(value=UserExpCont.EXP_USER_SOURCE,index=6)
	private String userSource;
	
	/**
	 * 用户来源Id
	 */
	private Integer source;
	
	
	/**
	 * 	注册时间
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_CREATE_TIME)
	@JsonProperty(value=UserExpCont.EXP_CREATE_TIME,index=7)
	private String createTime;
	
	/**
	 * 	会员卡
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_USER_CARD) 
	@JsonProperty(value=UserExpCont.EXP_USER_CARD,index=8)
	private String userCard;
	
	/**
	 * 	地址
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_USER_ADDRESS)
	@JsonProperty(value=UserExpCont.EXP_USER_ADDRESS,index=9)
	private String userAddress;
	
	/**
	 * 	累计消费金额
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_ORDER_AMOUNT)
	@JsonProperty(value=UserExpCont.EXP_ORDER_AMOUNT,index=10)
	private BigDecimal orderAmount;
	
	/**
	 * 	累计消费单数
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_ORDER)
	@JsonProperty(value=UserExpCont.EXP_ORDER,index=11)
	private Integer order;
	
	/**
	 *	 累计退款金额
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_RETURN_ORDER_MONEY)
	@JsonProperty(value=UserExpCont.EXP_RETURN_ORDER_MONEY,index=12)
	private BigDecimal returnOrderMoney;
	
	/**
	 * 	累计退款订单数
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_RETURN_ORDER)
	@JsonProperty(value=UserExpCont.EXP_RETURN_ORDER,index=13)
	private Integer returnOrder;
	
	/**
	 * 	备注
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_REMARK)
	@JsonProperty(value=UserExpCont.EXP_REMARK,index=14)
	private String remark;
	
	/**
	 *	 邀请人
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_INVITE_USER_NAME)
	@JsonProperty(value=UserExpCont.EXP_INVITE_USER_NAME,index=15)
	private String inviteUserName;
	
	/**
	 * 	邀请人手机号
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_INVITE_MOBILE)
	@JsonProperty(value=UserExpCont.EXP_INVITE_MOBILE,index=16)
	private String inviteMobile;
	
	/**
	 *	 邀请人分销员分组
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_INVITE_GROUP_NAME)
	@JsonProperty(value=UserExpCont.EXP_INVITE_GROUP_NAME,index=17)
	private String inviteGroupName;
	
	/**
	 * 	获返利订单数量
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_REBATE_ORDER_NUM)
	@JsonProperty(value=UserExpCont.EXP_REBATE_ORDER_NUM,index=18)
	private String rebateOrderNum;
	
	/**
	 * 	返利商品总金额
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_CALCULATE_MONEY)
	@JsonProperty(value=UserExpCont.EXP_CALCULATE_MONEY,index=19)
	private BigDecimal calculateMoney;
	
	/**
	 * 	获返利订单佣金总额
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_REBATE_MONEY)
	@JsonProperty(value=UserExpCont.EXP_REBATE_MONEY,index=20)
	private BigDecimal rebateMoney;
	
	/**
	 * 	已提现佣金总额
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_SUBLAYER_NUMBER) 
	@JsonProperty(value=UserExpCont.EXP_WITHDRAW_MONEY,index=21)
	private BigDecimal withdrawMoney;
	
	/**
	 * 	下级用户数
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_WITHDRAW_MONEY) 
	@JsonProperty(value=UserExpCont.EXP_SUBLAYER_NUMBER,index=22)
	private Integer sublayerNumber;
	
	/**
	 * 	分销员等级
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_LEVEL_NAME) 
	@JsonProperty(value=UserExpCont.EXP_LEVEL_NAME,index=23)
	private String levelName;
	
	/**
	 * 	分销员分组
	 */
	@ExcelColumn(columnName=JsonResultMessage.UEXP_GROUP_NAM)
	@JsonProperty(value=UserExpCont.EXP_GROUP_NAME,index=24)
	private String groupName;
	
	


}
