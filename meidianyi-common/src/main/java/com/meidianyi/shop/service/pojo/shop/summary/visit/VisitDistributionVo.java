package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.pojo.shop.summary.KeyValueChart;
import com.meidianyi.shop.service.pojo.shop.summary.ValueKeyChart;
import lombok.Data;

import java.util.List;

/**
 * 访问分布出参
 *
 * @author 郑保乐
 */
@Data
public class VisitDistributionVo {
    private String startDate;
    private String endDate;
    /**
     * 平均访问深度
     */
    private List<VisitInfoItem> accessDepthInfo;
    /**
     * 访问来源
     */
    private List<VisitInfoItem> accessSourceSessionCnt;
    /**
     * 停留时长
     */
    private List<VisitInfoItem> accessStayTimeInfo;
    /**
     * 平均访问深度
     */
    private ValueKeyChart visitDepth;
    /**
     * 访问来源
     */
    private KeyValueChart visitSource;
    /**
     * 停留时长
     */
    private ValueKeyChart visitStayTime;
}
