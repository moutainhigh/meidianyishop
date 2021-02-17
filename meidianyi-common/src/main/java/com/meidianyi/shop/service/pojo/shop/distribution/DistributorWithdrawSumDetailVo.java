package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.PageResult;

import lombok.Builder;
import lombok.Data;
/**
 * @author huangzhuangzhuang
 */
@Data
@Builder
public class DistributorWithdrawSumDetailVo {
	/**
	 * 当前用户提现列表
	 */
	private PageResult<DistributorWithdrawListVo> data;
	/**
	 * 已提现金额
	 */
	@JsonProperty("done")
	private BigDecimal withdrawCrash;
}
