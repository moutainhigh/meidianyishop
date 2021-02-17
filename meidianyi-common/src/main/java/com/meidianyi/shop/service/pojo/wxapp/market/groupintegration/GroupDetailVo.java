package com.meidianyi.shop.service.pojo.wxapp.market.groupintegration;

import java.util.List;

import com.meidianyi.shop.service.pojo.shop.market.integration.CanApplyPinInteVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListPojo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationMaVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationPojo;

import lombok.Data;

/**
 * 小程序组团瓜分积分详情出参
 * 
 * @author zhaojianqiang
 * @time 下午4:21:49
 */
@Data
public class GroupDetailVo {
	private List<GroupIntegrationMaVo> groupInfo;
	private Long remainTime;
	private GroupIntegrationPojo pinInteInfo;
	private Integer userNum;
	private Integer inviteUser;
	private GroupIntegrationListPojo pinInteUser;
	private Integer score;
	private CanApplyPinInteVo canPin;
}
