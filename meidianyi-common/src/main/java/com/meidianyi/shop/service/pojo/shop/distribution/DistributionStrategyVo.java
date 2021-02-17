package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class DistributionStrategyVo {
    private Integer   id;
    private String    strategyName;
    private Byte      strategyLevel;
    private Timestamp startTime;
    private Timestamp endTime;
    private Double    fanliRatio;
    private Byte      status;
    private Timestamp createTime;

    /**
     * 当前状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;
}
