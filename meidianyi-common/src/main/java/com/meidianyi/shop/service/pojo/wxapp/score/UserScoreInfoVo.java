package com.meidianyi.shop.service.pojo.wxapp.score;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.member.score.CheckSignVo;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年10月16日 下午2:49:52
 */
@Data
public class UserScoreInfoVo {
	@JsonProperty(value = "score_num")
	public Integer scoreNum;
	@JsonProperty(value = "sign_score")
	public CheckSignVo signScore;
	@JsonProperty(value = "page_id")
	public String pageId;
	
}
