package com.meidianyi.shop.service.pojo.shop.store.technician;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月18日
 *
 */
@Data
@NoArgsConstructor
public class ServiceTechnicianGroupParam {
	/** 分组ID */
	Integer groupId;
	/** 分组名称*/
	@NotBlank
	String groupName;
	/** 门店ID */
	@NotNull
	Integer storeId;
}
