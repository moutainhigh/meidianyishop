package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author liufei
 * @date 1/13/20
 */
@Data
public class ShopAddress {
    @JsonProperty(value = "province_code")
    public String provinceCode;
    @JsonProperty(value = "city_code")
    public String cityCode;
    @JsonProperty(value = "district_code")
    public String districtCode;
    @JsonProperty(value = "address")
    public String address;
}
