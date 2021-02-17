package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年10月22日
* @Description: 积分前端展示入参信息
*/
@Data
public class ScoreFrontParam {
	private String document;
	private Timestamp updateTime;
}
