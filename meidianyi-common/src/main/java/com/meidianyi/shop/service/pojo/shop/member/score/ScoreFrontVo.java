package com.meidianyi.shop.service.pojo.shop.member.score;

import com.meidianyi.shop.service.foundation.util.I18N;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年10月22日
* @Description: 前端积分导出参数
*/
@Data
public class ScoreFrontVo {
	@I18N(propertiesFileName = "member")
	public String title;
	public String document;
}
