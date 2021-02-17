package com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 拼团抽奖数据效果
 * @author liangchen
 * @date 2020.01.06
 */
@Data
public class GroupDrawAnalysisParam {
    /** 拼团抽奖id */
    private Integer groupDrawId;
    /** 开始时间 */
    private Timestamp startTime;
    /** 结束时间 */
    private Timestamp endTime;
}
