package com.meidianyi.shop.service.wechat.bean.open;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年8月23日 上午10:45:44
 */
@Data
public class WxOpenMiniLinkGetFuncInfos  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8677614338911618426L;

	private Integer status;
	private Integer id;
	private String name;
}
