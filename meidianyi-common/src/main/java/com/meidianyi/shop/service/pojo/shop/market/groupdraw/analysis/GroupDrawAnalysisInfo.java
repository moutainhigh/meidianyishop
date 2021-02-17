package com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 拼团抽奖数据效果
 * @author liangchen
 * @date 2020.01.06
 */
@Data
public class GroupDrawAnalysisInfo {
    /** 用户id */
    private Integer userId;
    /** 创建时间 */
    private Timestamp createTime;

    private String startDate;
}
