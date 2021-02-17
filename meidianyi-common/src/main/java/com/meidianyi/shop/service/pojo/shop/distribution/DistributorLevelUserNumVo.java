package com.meidianyi.shop.service.pojo.shop.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author changle
 */
@Data
public class DistributorLevelUserNumVo {
    @JsonProperty("distributor_level")
	private Integer distributorLevel;
	private Integer userNumber;
}
