package com.meidianyi.shop.service.pojo.shop.market.live;

import com.meidianyi.shop.common.foundation.util.PageResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 直播列表校验的一些出参数
 * 
 * @author zhaojianqiang
 * @time 下午2:00:05
 */
@Data
@NoArgsConstructor
public class LiveCheckVo {
	private Boolean isAuthLive;
	private Byte auditState;
	private Boolean hasLiveFunc;
	private PageResult<LiveListVo> pageList;
	public LiveCheckVo(Boolean isAuthLive, Byte auditState, Boolean hasLiveFunc, PageResult<LiveListVo> pageList) {
		super();
		this.isAuthLive = isAuthLive;
		this.auditState = auditState;
		this.hasLiveFunc = hasLiveFunc;
		this.pageList = pageList;
	}
	public LiveCheckVo(Boolean isAuthLive, Byte auditState, Boolean hasLiveFunc) {
		super();
		this.isAuthLive = isAuthLive;
		this.auditState = auditState;
		this.hasLiveFunc = hasLiveFunc;
	}
	
	
}
