package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liangchen
 * @date 2019年7月12日
 */
@Data
public class GoodsDeliverTemplateLimitParam {
    /** 限制开关 0:不限制-下面内容生效，1:限制-下面内容无效 */
	@JsonProperty(value = "limit_deliver_area")
	private Integer 	limitDeliverArea;
	/** 地区编号 固定值:0 */
	@JsonProperty(value = "area_list")
	private String 		areaList;
	/** 地区名称 固定值:全国(其他地区) */
	@JsonProperty(value = "area_text")
	private String 		areaText;
	/** N件/公斤内 */
	@JsonProperty(value = "first_num")
	private Integer 	firstNum;
	/** N元 */
	@JsonProperty(value = "first_fee")
	private BigDecimal 	firstFee;
	/** 每增加N件/公斤 */
	@JsonProperty(value = "continue_num")
	private Integer 	continueNum;
	/** 增加N元 */
	@JsonProperty(value = "continue_fee")
	private BigDecimal 	continueFee;
}
