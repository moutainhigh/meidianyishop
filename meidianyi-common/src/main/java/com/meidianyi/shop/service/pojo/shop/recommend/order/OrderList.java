/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月12日 上午10:55:19
 */
@Data
@ToString
public class OrderList {

	@JsonProperty(value = "order_id")
    private String orderId;
	
	@JsonProperty(value = "create_time")
    private long createTime;
	
	@JsonProperty(value = "pay_finish_time")
    private long payFinishTime;
	
    private String desc;
    
    private BigDecimal fee;
    
    @JsonProperty(value = "trans_id")
    private String transId;
    
    private int status;
    
    @JsonProperty(value = "ext_info")
    private ExtInfo extInfo;

}