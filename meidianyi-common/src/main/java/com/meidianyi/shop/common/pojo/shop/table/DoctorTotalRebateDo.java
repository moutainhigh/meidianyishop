package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/8/25
 **/
@Data
public class DoctorTotalRebateDo {
    private Integer    id;
    private Integer    doctorId;
    private BigDecimal totalMoney;
    private BigDecimal blockedMoney;
    private BigDecimal finalMoney;
    private Byte       isDelete;
    private Timestamp createTime;
    private Timestamp  updateTime;
}
