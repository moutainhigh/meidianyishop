package com.meidianyi.shop.service.pojo.shop.store.service;

import com.meidianyi.shop.service.pojo.shop.config.pledge.group.DeleteGroup;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年7月15日
 */
@Data
public class StoreServiceParam {
    @PositiveOrZero(groups = {UpdateGroup.class, DeleteGroup.class})
    private Integer    id;
    private Integer    storeId;
    private String     serviceName;
    private String     serviceSn;
    private Integer    saleNum;
    private BigDecimal servicePrice;
    private BigDecimal serviceSubsist;
    private Integer    catId;
    private Byte       serviceShelf;
    private String     serviceImg;
    private Date       startDate;
    private Date       endDate;
    private String     startPeriod;
    private String     endPeriod;
    @Positive
    private Integer    serviceDuration;
    private Byte       serviceType;
    private Integer    servicesNumber;
    private Integer    techServicesNumber;
    private String     content;
    private Timestamp  createTime;
    private Timestamp  updateTime;
    private String     chargeResolve;
    private Byte       delFlag;
}
