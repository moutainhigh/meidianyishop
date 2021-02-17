package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄壮壮
 * 	用户返利
 */
@Data
@AllArgsConstructor
public class AccountWithdrawVo {
	/**
	 * 可提现金额
	 */
	private BigDecimal withdraw;
	/**
	 * 	余额
	 */
	private BigDecimal account;
	
	/**
	 * 提现开关
	 */
	@JsonProperty(value = "withdraw_status")
	public Byte withdrawStatus;
	/**
	 * 是否强制用户绑定手机号
	 */
	@JsonProperty(value="is_bind_mobile")
	private Byte isBindMobile;
	
	/**
	 * 返利方式
	 */
	@JsonProperty(value = "withdraw_source")
	private String withdrawSource;
	
	/**
	 * 是否订阅该公众号 0取关，1 关注
	 */
	@JsonProperty(value="is_public_user")
	private Byte isPublicUser;
	
	/**
	 * 小程序昵称 没有值则为null
	 */
	@JsonProperty(value="nick_name")
	private String nickName;
	
	public AccountWithdrawVo() {
		// set for default value
		this.isPublicUser = (byte)0;
		this.isBindMobile = (byte)0;
	}
}
