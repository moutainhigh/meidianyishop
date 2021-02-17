package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author liangchen
 * @date 2019年7月12日
 */
@Data
public class GoodsDeliverTemplateAreaParam {
    /** 地区编号 */
    @JsonProperty(value = "area_list")
    private List<String> areaList;
    /** 地区名称 */
    @JsonProperty(value = "area_text")
    private List<String> areaText;
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
