package com.meidianyi.shop.service.pojo.shop.store.technician;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *
 */
@Data
@NoArgsConstructor
public class TechnicianService{
	private Integer id;
	private Integer storeId;
	private String serviceName;
}
