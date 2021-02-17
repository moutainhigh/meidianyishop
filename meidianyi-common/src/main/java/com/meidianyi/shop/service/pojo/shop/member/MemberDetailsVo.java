package com.meidianyi.shop.service.pojo.shop.member;

import lombok.Getter;
import lombok.Setter;
/**
* @author 黄壮壮
* @Date: 2019年8月14日
* @Description: 会员用户详情信息-出参
*/
@Getter
@Setter
public class MemberDetailsVo {
	MemberBasicInfoVo memberBasicInfo;
	MemberTransactionStatisticsVo transStatistic;
	
}
