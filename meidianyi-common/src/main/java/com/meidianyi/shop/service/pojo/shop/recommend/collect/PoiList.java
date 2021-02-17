/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.collect;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auto-generated: 2019-11-13 16:15:35
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoiList {

    private double longitude;
    private double latitude;
    private int radius;
    @JsonProperty(value = "business_name")
    private String businessName;
    
    @JsonProperty(value = "branch_name")
    private String branchName;

    private String address;

}