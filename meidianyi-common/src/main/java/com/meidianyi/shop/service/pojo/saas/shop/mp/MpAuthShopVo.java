package com.meidianyi.shop.service.pojo.saas.shop.mp;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class MpAuthShopVo {

	/**
	 * app_id
	 */
	private String appId;
	/**
	 * 店铺ID
	 */
	private Integer shopId;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 别名
	 */
	private String alias;
	/**
	 * 是否认证 -1 未 0 已
	 */
	private String verifyTypeInfo;
	/**
	 * 头像
	 */
	private String headImg;
	/**
	 * 授权权限
	 */
	private String funcInfo;
	/**
	 *  是否授权
	 */
	private Byte isAuthOk;
	/**
	 * 最后授权时间
	 */
	private Timestamp lastAuthTime;
	/**
	 *  最后取消授权时间
	 */
	private Timestamp lastCancelAuthTime;
	/**
	 * 记录时间
	 */
	private Timestamp createTime;
	/**
	 * 小程序二维码
	 */
	private String qrcodeUrl;
	/**
	 * 是否支持微信支付 1 是
	 */
	private Byte openPay;
	/**
	 * 是否支持卡券  1 是
	 */
	private Byte openCard;

	private String authorizerInfo;

	private String authorizationInfo;
	/**
	 * 商户号
	 */
	private String payMchId;
	/**
	 * 商户密钥
	 */
	private String payKey;
	/**
	 *  证书内容
	 */
	private String payCertContent;
	/**
	 * 私钥内容
	 */
	private String payKeyContent;
	/**
	 * 是否修改开发和业务域名，0未修改，1已修改
	 */
	private Byte isModifyDomain;
	/**
	 * 绑定代码模板ID
	 */
	private Integer bindTemplateId;
	/**
	 * 上传代码状态
	 */
	private Byte uploadState;
	/**
	 * 最后上传代码时间
	 */
	private Timestamp lastUploadTime;
	/**
	 * 提交审核ID
	 */
	private Long auditId;
	/**
	 * 提交审核状态
	 */
	private Byte auditState;
	/**
	 * 提交审核时间
	 */
	private Timestamp submitAuditTime;
	/**
	 *   审批成功时间
	 */
	private Timestamp auditOkTime;
	/**
	 * 审批失败原因
	 */
	private String auditFailReason;
	/**
	 * 发布代码状态
	 */
	private Byte publishState;
	/**
	 * 发布时间
	 */
	private Timestamp publishTime;
	/**
	 * 小程序体验者
	 */
	private String tester;
	/**
	 * 体验二维码
	 */
	private String testQrPath;
	/**
	 * 可选类目
	 */
	private String category;
	/**
	 * 页面配置
	 */
	private String pageCfg;
	/**
	 * 主体名称
	 */
	private String principalName;
	private String bindOpenAppId;
	private String linkOfficialAppId;
	/**
	 * 支付方式：
	 */
	private Byte isSubMerchant;
	/**
	 * 通联支付子商户APPID
	 */
	private String unionPayAppId;
	/**
	 * 通联支付子商户商户号
	 */
	private String unionPayCusId;
	/**
	 * 通联支付子商户密钥
	 */
	private String unionPayAppKey;
	/**
	 * MCC码
	 */
	private String merchantCategoryCode;
	/**
	 *  标价币种
	 */
	private String feeType;

}
