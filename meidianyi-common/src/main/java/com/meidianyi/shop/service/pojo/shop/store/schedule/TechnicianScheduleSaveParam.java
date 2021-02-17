package com.meidianyi.shop.service.pojo.shop.store.schedule;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月16日
 *
 */
@Data
@NoArgsConstructor
public class TechnicianScheduleSaveParam {
	private Integer storeId;
	private Integer technicianId;
	private Map<String,Byte> scheduleMap;
}
