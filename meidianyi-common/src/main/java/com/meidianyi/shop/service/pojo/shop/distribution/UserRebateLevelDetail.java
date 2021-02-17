package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 *分销员分销信息
 * @author 常乐
 * 2019年8月1日
 */
@Data
public class UserRebateLevelDetail {
	private Integer userId;
	private Integer sublayerNumber;
	private Byte distributorLevel;
	private String levelName;
}
