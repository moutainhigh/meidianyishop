package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

import java.util.List;

/**
 * 访问分布查询参数
 *
 * @author 郑保乐
 */
@Data
public class VisitDistributionParam {
    /** 日期类型 7:最近7天 30:最近30天 0:自定义 */
    public Integer type = 7;
    public String startDate;
    public String endDate;

    /**
     * 忽略哪些访问来源
     */
    public List<Integer> cancelBtn;
    public Integer sourceId = 1;
}
