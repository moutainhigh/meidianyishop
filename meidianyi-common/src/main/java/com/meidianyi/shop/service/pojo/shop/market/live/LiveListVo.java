package com.meidianyi.shop.service.pojo.shop.market.live;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 * @time 下午4:04:17
 */
@Data
public class LiveListVo {
	private Integer id;
	private Integer roomId;
	private String name;
	private Short liveStatus;
	private Timestamp startTime;
	private Timestamp endTime;
	private String anchorName;
	private String coverImg;
	private String anchorImg;
	private Timestamp addTime;
	private Timestamp updateTime;
	private Byte delFlag;
	private Timestamp delTime;

	private Integer goodsListNum;
	private Integer addCartNum;
	private Integer orderNum;
}
