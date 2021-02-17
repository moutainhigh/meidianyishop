package com.meidianyi.shop.service.pojo.saas.shop.mp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class MpDeployQueryParam {
	/**
	 * 设置服务器域名
	 */
	public static final String ACT_MODIFY_DOMAIN = "modify-domain";

	/**
	 * 上传代码
	 */
	public static final String ACT_UPLOAD_CODE = "upload-code";

	/**
	 * 添加体验者
	 */
	public static final String ACT_ADD_TESTER = "add-tester";

	/**
	 * 删除体验者
	 */
	public static final String ACT_DEL_TESTER = "remove-tester";

	/**
	 * 获取体验者二维码
	 */
	public static final String ACT_GET_TESTER_QR = "get-test-qr";

	/**
	 * 获取可选类目
	 */
	public static final String ACT_GET_CATEGORY = "get-category";

	/**
	 * 获取页面配置
	 */
	public static final String ACT_GET_PAGE_CFG = "get-page";

	/**
	 * 提交审核
	 */
	public static final String ACT_SUBMIT_AUDIT = "submit-audit";

	/**
	 * 发布代码
	 */
	public static final String ACT_PUBLISH_CODE = "publish-code";

	/**
	 * 上传代码并提交审核
	 */
	public static final String ACT_UPLOAD_AUDIT = "upload-audit";

	/**
	 * 更新小程序信息
	 */
	public static final String ACT_UPDATE_MP = "update-mp";

	/**
	 * 刷新审核状态
	 */
	public static final String ACT_REFRESH_AUDIT_STATE = "refresh-audit-state";
	
	/**
	 * 设置支付方式
	 */
	public static final String SETTING_SUB_MERCHANT="setting-sub-merchant";

	/**
	 * 请求动作，以上ACT_下划线开头的常量值
	 */
	String act;

	/**
	 * 小程序appId
	 */
	String appId;

	/**
	 * 绑定或解除的体验者微信Id，act 为add-tester remove-tester有效
	 */
	String wechatId;

	/**
	 * 模板Id，act 为upload-code upload-audit有效
	 */
	Integer templateId;
	
	/**
	 * 支付方式设置
	 * 
	 * "0"：微信直连支付
	   "1"：微铺宝子商户支付
	   "2"：通联子商户支付
	   "3"：微信国际融合钱包支付
	 * 
	 */
	Integer isSubMerchant;
	/**  商户APPID */
    @JsonProperty("union_pay_app_id")
	String unionPayAppId;
	/**  商户商户号 */
    @JsonProperty("union_pay_cus_id")
	String unionPayCusId;
	/**  商户密钥 */
    @JsonProperty("union_pay_app_key")
	String unionPayAppKey;
	/**  MCC码 */
    @JsonProperty("merchant_category_code")
	String merchantCategoryCode;
	/**  标价币种 */
    @JsonProperty("fee_type")
	String feeType;
	
}
