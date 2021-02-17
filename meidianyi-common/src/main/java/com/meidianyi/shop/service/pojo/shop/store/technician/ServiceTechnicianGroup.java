package com.meidianyi.shop.service.pojo.shop.store.technician;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *
 */
@Data
@NoArgsConstructor
public class ServiceTechnicianGroup {
	Integer groupId;
	String groupName;
	Integer storeId;
	Timestamp createTime;
}
