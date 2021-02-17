package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-05-11 17:08
 **/
@Setter
@Getter
public class MrkingStrategyAnalysisParam {
    @NotNull
    private Integer id;

    @NotNull
    private Timestamp startTime;

    @NotNull
    private Timestamp endTime;
}
