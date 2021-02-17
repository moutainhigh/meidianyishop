/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;
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
public class ExpressPackageInfoList {

	@JsonProperty(value = "express_company_id")
    private int expressCompanyId;
	
	@JsonProperty(value = "express_company_name")
    private String expressCompanyName;
	
	@JsonProperty(value = "express_code")
    private String expressCode;
	
	@JsonProperty(value = "ship_time")
    private long shipTime;
	
	@JsonProperty(value = "express_page")
    private ExpressPage expressPage;
	
	@JsonProperty(value = "express_goods_info_list")
    private List<ExpressGoodsInfoList> expressGoodsInfoList;

}