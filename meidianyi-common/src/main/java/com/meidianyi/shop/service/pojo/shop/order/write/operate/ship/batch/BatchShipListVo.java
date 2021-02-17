package com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * 批量发货查询
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class BatchShipListVo {
    private Integer   id;
    private Integer   sysId;
    private Integer   accountId;
    private Integer   totalNum;
    private Integer   successNum;
    private Timestamp createTime;
    private String userName;
    private String mobile;
    private String childUserName;
    private String childMobile;
}
