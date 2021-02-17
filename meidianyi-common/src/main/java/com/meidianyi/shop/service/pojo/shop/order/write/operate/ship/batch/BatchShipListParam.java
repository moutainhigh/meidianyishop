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

public class BatchShipListParam {
    private Integer batchId;
    public Timestamp start;
    public Timestamp end;
    public Integer currentPage;
    public Integer pageRows;
}
