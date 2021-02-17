package com.meidianyi.shop.service.wechat.bean.open;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 获取直播房间列表 返回值
 * 
 * @author zhaojianqiang 2020年4月2日下午4:47:55
 */
@Getter
@Setter
@ToString
@Slf4j
public class WxMaLiveInfoResult extends WxOpenResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4400409481898888644L;

	@SerializedName("room_info")
	private List<WxMaLiveRoomInfo> roomInfo;
	
	private Integer total;

	public static WxMaLiveInfoResult fromJson(String json) {
		log.info(json);
		return WxGsonBuilder.create().fromJson(json, WxMaLiveInfoResult.class);
	}
}
