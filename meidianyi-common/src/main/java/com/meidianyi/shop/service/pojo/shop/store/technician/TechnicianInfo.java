package com.meidianyi.shop.service.pojo.shop.store.technician;

import lombok.Data;

/**
 * The type Technician info.
 *
 * @author liufei
 * @date 11 /5/19
 */
@Data
public class TechnicianInfo {
    private Integer id;
    private Integer storeId;
    private String technicianName;
    private String technicianMobile;
    private String bgImgPath;
    private String technicianIntroduce;
    private Long groupId;
    private Byte serviceType;
    private String serviceList;
    private String remarks;
    private String workDate;
    private Integer scheduleId;
    private String scheduleName;
    private String begcreateTime;
    private String endTime;
}
