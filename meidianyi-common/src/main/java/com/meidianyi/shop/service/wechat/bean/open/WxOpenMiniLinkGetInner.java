package com.meidianyi.shop.service.wechat.bean.open;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年8月23日 上午10:45:44
 */
@Data
public class WxOpenMiniLinkGetInner implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7776362851183500998L;
	
	@SerializedName("items")
	private List<WxOpenMiniLinkGetItems> items;

}
