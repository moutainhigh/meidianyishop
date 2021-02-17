package com.meidianyi.shop.service.pojo.shop.store.statistic;

import lombok.Data;
import org.jooq.types.UInteger;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author chenjie
 * @date 2020年08月27日
 */
@Data
public class StatisticParam {
    private Integer storeId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte type;
    private Date refDate;
}
