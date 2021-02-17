package com.meidianyi.shop.service.pojo.shop.market.reduceprice;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-05-21 15:28
 **/
@Getter
@Setter
public class ReducePriceAnalysisParam {
    @NotNull
    private Integer id;

    @NotNull
    private Timestamp startTime;

    @NotNull
    private Timestamp endTime;
}
