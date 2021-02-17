package com.meidianyi.shop.service.pojo.saas.shop;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 店铺发布列表返回值
 * @author zhaojianqiang
 *
 * 2019年11月22日 上午11:30:59
 */
@Data
public class ShopMpListVo {
	private String appId;
	private Integer shopId;
	private Timestamp createTime;
	private String nickName;
	private Timestamp publishTime;
	private Timestamp lastUploadTime;
	private Integer bindTemplateId;
	private Byte openPay;
	private String principalName;
	private String shopType;
	private Byte isEnabled;
	private BigDecimal renewMoney;
	private Timestamp expireTime;
	private Integer templateId;
	private String userVersion;
	private String bindUserVersion;
	private String shopExpireStatus;
	private Timestamp startTime;
}
