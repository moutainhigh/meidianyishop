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
public class WxOpenMiniLinkGetItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2010572800865421333L;
	private int status;
	private String username;
	private String source;
	private String nickname;
	private int selected;
	
	@SerializedName("nearby_display_status")
	private int nearbydisplayStatus;
	
	private int released;
	@SerializedName("headimg_url")
	private String headimgUrl;
	
	@SerializedName("func_infos")
	private List<WxOpenMiniLinkGetFuncInfos> funcInfos;
	
	@SerializedName("copy_verify_status")
	private int copyVerifyStatus;
	
	private String email;

}
