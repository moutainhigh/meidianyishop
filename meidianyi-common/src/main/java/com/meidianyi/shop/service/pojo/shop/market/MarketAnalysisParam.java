package com.meidianyi.shop.service.pojo.shop.market;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年8月2日
 */
@Data
public class MarketAnalysisParam {

    @NotNull
    private Integer actId;

    /**
     * user表的邀请来源标记
     */
    private String inviteSource;

    @NotNull
    private Timestamp startTime;

    @NotNull
    private Timestamp endTime;

}
