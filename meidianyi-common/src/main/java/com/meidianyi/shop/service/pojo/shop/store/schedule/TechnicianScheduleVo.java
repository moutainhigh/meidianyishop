package com.meidianyi.shop.service.pojo.shop.store.schedule;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月16日
 *
 */
@Data
@NoArgsConstructor
public class TechnicianScheduleVo {
	private Integer storeId;
	private Integer technicianId;
	private String technicianName;
	private String workDate;
	
	private Byte scheduleId;
	private String scheduleName;
	private String begcreateTime;
	private String endTime;
}
