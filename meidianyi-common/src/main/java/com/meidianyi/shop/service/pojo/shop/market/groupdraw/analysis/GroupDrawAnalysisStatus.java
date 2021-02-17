package com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 拼团抽奖数据效果
 * @author liangchen
 * @date 2020.01.07
 */
@Data
public class GroupDrawAnalysisStatus {
    /** 状态 */
    private Byte status;
    /** 开团时间 */
    private Timestamp openTime;
    
    private String startDate;
}
