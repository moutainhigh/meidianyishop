package com.meidianyi.shop.service.pojo.shop.member.ucard;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.util.I18N;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardCustomActionVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄壮壮
 * 	激活会员卡返回数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateCardVo {
	private Map<String,Object> data;
	private List<String> fields;
	
	@I18N(propertiesFileName = "member")
	private List<String> education;
	
	@I18N(propertiesFileName = "member")
	@JsonProperty("industry_info")
	private List<String>industryInfo;
	
	/**
	 * 	 订阅消息模板id
	 */
	@JsonProperty("template_ids")
	private List<String> templateIds;
	
	/**
	 * 	自定义激活选项
	 */
	private List<CardCustomActionVo> customOptions;

    /**
     * message
     */
    private JsonResultCode msg;
}
