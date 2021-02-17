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
@AllArgsConstructor
@NoArgsConstructor
public class LiveCheckTwoVo {
	private Boolean isAuthLive;
	private PageResult<LiveListVo> pageList;
}
