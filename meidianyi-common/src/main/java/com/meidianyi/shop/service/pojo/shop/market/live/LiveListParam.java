package com.meidianyi.shop.service.pojo.shop.market.live;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 直播列表的入参
 * 
 * @author zhaojianqiang
 * @time 下午2:00:05
 */
@Data
public class LiveListParam {
	private String name;
	private Timestamp beginStartTime;
	private Timestamp beginEndTime;
	private Short liveStatus = -1;
	private String anchorName;
	private Timestamp finishStartTime;
	private Timestamp finishEndTime;
	private Integer currentPage;
	private Integer pageRows;
}
