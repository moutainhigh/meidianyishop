package com.meidianyi.shop.service.pojo.shop.store.technician;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *售后人员信息 出参
 */
@Data
@NoArgsConstructor
public class ServiceTechnicianPojo {
	private Integer id;
	private Integer storeId;
	private String technicianName;
	private String technicianMobile;
	private String bgImgPath;
	private String technicianIntroduce;
	private ServiceTechnicianGroup seviceGroup;
	private Byte serviceType;
	private List<TechnicianService> serviceList;
	private String remarks;
	
	private Timestamp createTime;
	private Timestamp updateTime;
	
	/**
	 * @param id
	 * @param storeId
	 * @param technicianName
	 * @param technicianMobile
	 * @param bgImgPath
	 * @param technicianIntroduce
	 * @param seviceGroup
	 * @param serviceType
	 * @param serviceList
	 * @param remarks
	 * @param createTime
	 * @param updateTime
	 */
	public ServiceTechnicianPojo(Integer id, Integer storeId, String technicianName, String technicianMobile,
			String bgImgPath, String technicianIntroduce, ServiceTechnicianGroup seviceGroup, Byte serviceType,
			List<TechnicianService> serviceList, String remarks, Timestamp createTime, Timestamp updateTime) {
		super();
		this.id = id;
		this.storeId = storeId;
		this.technicianName = technicianName;
		this.technicianMobile = technicianMobile;
		this.bgImgPath = bgImgPath;
		this.technicianIntroduce = technicianIntroduce;
		this.seviceGroup = seviceGroup;
		this.serviceType = serviceType;
		this.serviceList = serviceList;
		this.remarks = remarks;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	
}
