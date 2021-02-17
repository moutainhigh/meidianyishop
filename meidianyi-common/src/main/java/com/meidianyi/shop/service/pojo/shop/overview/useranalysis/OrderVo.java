package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.List;

/**
 * 成交用户分析
 * @author liangchen
 * @date 2019年7月19日
 */
@Data
public class OrderVo {
    /** 老客户每日数据 */
    List<OrderDailyVo> dailyOldVo;
    /** 新客户每日数据 */
    List<OrderDailyVo> dailyNewVo;
    /** 累积数据 */
    OrderTotalVo dataVo;
    /** 累积数据变化率 */
    OrderChangeRateVo changeRateVo;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
}
