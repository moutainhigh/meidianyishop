package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.meidianyi.shop.common.foundation.validator.ListValid;

/**
 * 包邮规则 信息 (国际化)
 * @author 孔德成
 * @date 2020/2/20
 */
@Getter
@Setter
public class FreeShippingRuleInfoVo {

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动有效期
     */
    private String expire;
    /**
     * 商品范围
     */
    private String goodsAreaInfo;
    /**
     * 规则条件
     */
    private String ruleCondition;
    /**
     * 包邮地区
     */
    private List<String> areaText;
    /**
     * 活动规则说明
     */
    private String ruleText;

}
