package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.pojo.shop.summary.RefDateRecord;
import lombok.Data;

/**
 * 访问统计分组
 *
 * @author 郑保乐
 */
@Data
public class VisitStatisticsUnit implements RefDateRecord<Double> {

    private String refDate;
    private Double value;
}
