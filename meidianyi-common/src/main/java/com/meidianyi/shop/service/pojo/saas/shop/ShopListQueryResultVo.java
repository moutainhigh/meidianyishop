package com.meidianyi.shop.service.pojo.saas.shop;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopListQueryResultVo {
	public Page page;
	public Integer sysId;
	public Integer shopId;
	public String shopName;
	public String shopType;
	/**
	 * 小程序昵称
	 */
	public String nickName;
	public String mobile;
	public Timestamp created;
	/**
	 * 店铺状态
	 */

	public Integer isUse;
	/**
	 * 续费到期时间
	 */
	public Timestamp expireTime;
	/**
	 * 是否禁止
	 */
	public Byte isEnabled;
	/**
	 * 小程序是否授权
	 */
	public Byte isAuthOk;
	/**
	 * 所属账号
	 */
	public String userName;
	/**
	 * 续费总金额
	 */
	public BigDecimal renewMoney;
	/**
	 * 店铺标识
	 */
	public Byte shopFlag;
	/**
	 * 底部导航
	 */
	public Byte hidBottom;

	private String receiveMobile;
	private String shopPhone;
	private String shopNotice;
	private String shopWx;
	private String shopEmail;
	private String shopQq;
	private String memberKey;
	private String tenancyName;
	private Timestamp endTime;
	private String currency;
	private String shopLanguage;
	private String shopExpireStatus;
	@JsonIgnore
	private String versionConfig;
	private List<String> specialInfo;
	private String principalName;
	
	private String appId;
}
