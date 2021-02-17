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
public class SchedulePojo {
	private Byte scheduleId;
	private Integer storeId;
	private String scheduleName;
	private String begcreateTime;
	private String endTime;
    /**
     * The Business type.门店营业时间1：每天，0：工作日
     */
    public Byte businessType;
}
