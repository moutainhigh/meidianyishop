package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 助力活动发起起止时间
 * @author liangchen
 * @date 2020.02.27
 */
@Data
public class Duration {
    /** 开始时间 */
    private Timestamp startTime;
    /** 结束时间 */
    private Timestamp endTime;
}
