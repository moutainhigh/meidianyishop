package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 运费模板具体内容
 * @author liangchen
 * @date 2019.11.05
 */
@Data
public class GoodsDeliverTemplateContentParam {
    /** 除可配送区域外，其他是否配送 */
    GoodsDeliverTemplateLimitParam limitParam;
    /** 可配送区域信息 */
    ArrayList<GoodsDeliverTemplateAreaParam> areaParam;
    /** 指定条件包邮（可选） 0:关闭，1:开启 */
    @JsonProperty(value = "has_fee_0_condition")
    private Integer hasFee0Condition;
    /** 包邮可配送区域信息 */
    List<GoodsDeliverTemplateFeeConditionParam> feeConditionParam;
}
