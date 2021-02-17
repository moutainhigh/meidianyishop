package com.meidianyi.shop.service.pojo.saas.shop.officeaccount;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年8月21日 上午10:41:28
 */

@Data
public class MpOaPayManageParam {
	
	
	private Integer sysId;
	private String appId;
	private String payMchId;
	private String payKey;
	private String payCertContent;
	private String payKeyContent;

}
