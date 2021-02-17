package com.meidianyi.shop.service.pojo.shop.market.seckill.analysis;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-10-30 10:32
 **/
@Data
public class SeckillAnalysisParam {
    @NotNull
    private Integer skId;

    @NotNull
    private Timestamp startTime;

    @NotNull
    private Timestamp endTime;
}
