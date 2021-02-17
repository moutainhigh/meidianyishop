package com.meidianyi.shop.service.pojo.shop.store.service;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * The type Store service list query vo.
 *
 * @author 王兵兵  2019年7月15日
 */
@Data
public class StoreServiceListQueryVo {
	private Integer id;
	private String serviceName;
	private String serviceImg;
	private BigDecimal servicePrice;
	private String catName;
	private Integer saleNum;
	private Timestamp createTime;
	private Byte serviceType;
	private String technicianTitle;
	private Byte serviceShelf;

    /**
     * The Service subsist.预约订金
     */
    public Double serviceSubsist;
    /**
     * The Start date.可服务日期开始时间
     */
    public Date startDate;
    /**
     * The End date.可服务日期结束时间
     */
    public Date endDate;
    /**
     * The Start period.开始服务时段
     */
    public String startPeriod;
    /**
     * The End period.结束服务时段
     */
    public String endPeriod;

}
