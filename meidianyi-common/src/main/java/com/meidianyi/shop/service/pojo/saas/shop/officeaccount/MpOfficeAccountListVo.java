package com.meidianyi.shop.service.pojo.saas.shop.officeaccount;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月14日 上午10:29:49
 */
@Data
public class MpOfficeAccountListVo {
	private String appId;
	private String nickName;
	private String userName;
	private String alias;
	private String verifyTypeInfo;
	private String headImg;
	private String funcInfo;
	private Integer isAuthOk;
	private Timestamp lastAuthTime;
	private Timestamp lastCancelAuthTime;
	private Timestamp createTime;
	private Integer openPay;
	private Integer openCard;
	private String authorizerInfo;
	private String authorizationInfo;
	private String payMchId;
	private String payKey;
	private String payCertContent;
	private String payKeyContent;
	private String principalName;
	private Integer accountType;
	private String bindOpenAppId;
	private Integer sysId;
	private String qrcodeUrl;
	/**
	 * 小程序昵称
	 */
	private List<String> mpNickName;

}
