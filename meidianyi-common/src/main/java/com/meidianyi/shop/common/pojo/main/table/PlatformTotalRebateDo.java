package com.meidianyi.shop.common.pojo.main.table;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/9/27
 **/
@Data
public class PlatformTotalRebateDo implements Serializable {
    private static final long serialVersionUID = 914604917;

    private Integer    id;
    private Integer    shopId;
    private BigDecimal totalMoney;
    private BigDecimal finalMoney;
    private Byte       isDelete;
    private Timestamp  createTime;
    private Timestamp updateTime;
}
