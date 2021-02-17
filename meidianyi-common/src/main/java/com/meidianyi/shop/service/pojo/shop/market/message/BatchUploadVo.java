package com.meidianyi.shop.service.pojo.shop.market.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年9月9日 下午1:45:55
 */
@Data
@ToString
public class BatchUploadVo {

	private Integer success;
	private Integer fail;
	private Integer totalNum;

	@JsonIgnore
	private Integer code = 1;
	@JsonIgnore
	private String message;
}
