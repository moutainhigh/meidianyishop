package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-05-15 16:40
 * 图表数据分析的入参
 **/
@Getter
@Setter
public class AnalysisParam {

    @NotNull
    private Timestamp startTime;

    @NotNull
    private Timestamp endTime;
}
