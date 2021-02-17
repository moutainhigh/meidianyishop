package com.meidianyi.shop.service.pojo.shop.market.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhaojianqiang
 */
@Data
@ToString
public class BindRabbitParam {

	private String appId;
	private String language;
	private Integer sysId;
	private Integer taskJobId;
}
