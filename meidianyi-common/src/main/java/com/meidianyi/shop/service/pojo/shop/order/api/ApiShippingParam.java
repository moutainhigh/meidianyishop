package com.meidianyi.shop.service.pojo.shop.order.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.exception.MpException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 发货
 *
 * @author 王帅
 * @date 2020年04月29日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiShippingParam {
    @JsonProperty("order_sn")
    private String orderSn;
    @JsonProperty("logistics_code")
    private String logisticsCode;
    @JsonProperty("logistics_no")
    private String logisticsNo;
    @JsonProperty("order_goods_sn")
    private String orderGoodsSn;

    /**
     * 获取当前发货的商品
     * @return this will never be null
     */
    public List<Integer> getRecIds() throws MpException {
        if(StringUtils.isBlank(orderGoodsSn)) {
            return null;
        }
        ArrayList<Integer> result = Lists.newArrayList();
        List<String> initParameters = Util.stringToStringList(orderGoodsSn);
        for (String next : initParameters) {
            if (next.length() < orderSn.length()) {
                throw new MpException(null, "参数order_goods_sn规则错误");
            }
            result.add(Integer.parseInt(next.substring(orderSn.length())) - 10000);
        }
            return result;
    }
}
