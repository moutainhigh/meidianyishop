package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Builder;
import lombok.Data;

/**
 * The type Line chart vo.
 *
 * @author liufei
 * @date 1 /19/20
 */
@Data
@Builder
public class LineChartVo {
    /**
     * The Ref date.
     */
    private String refDate;
    /**
     * The Open times.
     */
    private Integer openTimes;
}
