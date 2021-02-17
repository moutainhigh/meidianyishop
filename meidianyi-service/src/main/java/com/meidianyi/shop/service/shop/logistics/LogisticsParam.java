package com.meidianyi.shop.service.shop.logistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author liufei
 * @date 12/2/19
 */
@Data
public class LogisticsParam {
    @JsonProperty(value = "delivery_id")
    private String deliveryId;
    @JsonProperty(value = "delivery_name")
    private String deliverName;
    @JsonProperty(value = "can_use_cash")
    private byte canUseCash;
    @JsonProperty(value = "can_get_quota")
    private byte canGetQuota;
    @JsonProperty(value = "service_type")
    private List<Map<String, ?>> serviceType;
    @JsonProperty(value = "cash_biz_id")
    private String cashBizId;
}
