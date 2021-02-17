/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-12 10:52:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class ExpressInfo {

    private String name;
    private String phone;
    private String address;
    private BigDecimal price;
    @JsonProperty(value = "national_code")
    private String nationalCode;
    private String country;
    private String province;
    private String city;
    private String district;
    @JsonProperty(value = "express_package_info_list")
    private List<ExpressPackageInfoList> expressPackageInfoList;

}