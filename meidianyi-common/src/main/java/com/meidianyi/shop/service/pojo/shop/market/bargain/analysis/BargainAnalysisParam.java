package com.meidianyi.shop.service.pojo.shop.market.bargain.analysis;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月30日
 */
@Data
public class BargainAnalysisParam {

    @NotNull
    private Integer bargainId;

    @NotNull
    private Timestamp startTime;

    @NotNull
    private Timestamp endTime;

}
