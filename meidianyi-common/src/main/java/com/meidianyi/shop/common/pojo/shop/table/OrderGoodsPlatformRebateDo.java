package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/9/27
 **/
@Data
public class OrderGoodsPlatformRebateDo implements Serializable {

    private static final long serialVersionUID = -1543987899;

    private Integer    id;
    private Integer    shopId;
    private Integer    recId;
    private BigDecimal totalMoney;
    private BigDecimal canCalculateMoney;
    private BigDecimal goodsSharingProportion;
    private BigDecimal platformRebateMoney;
    private BigDecimal platformRealRebateMoney;
    private Byte       status;
    private Byte       isDelete;
    private Timestamp  createTime;
    private Timestamp updateTime;
}
