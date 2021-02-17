package com.meidianyi.shop.service.pojo.shop.store.schedule;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月16日
 *
 */
@Data
@NoArgsConstructor
public class TechnicianScheduleParam {
	@NotNull
	private Integer storeId;
	@NotNull
	private Integer technicianId;
	@NotBlank
	private String beginTime;
	@NotBlank
	private String endTime;
}
