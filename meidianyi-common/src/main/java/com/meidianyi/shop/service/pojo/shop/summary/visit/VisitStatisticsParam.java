package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

/**
 * 访问统计
 *
 * @author 郑保乐
 */
@Data
public class VisitStatisticsParam {

    /**
     * 打开次数
     */
    public static final int SESSION_COUNT = 1;
    /**
     * 访问次数
     */
    public static final int PV = 2;
    /**
     * 访问人数
     */
    public static final int UV = 3;
    /**
     * 新用户数
     */
    public static final int UV_NEW = 4;
    /**
     * 人均停留时间
     */
    public static final int STAY_TIME_UV = 5;
    /**
     * 次均停留时长
     */
    public static final int STAY_TIME_SESSION = 6;
    /**
     * 平均访问深度
     */
    public static final int VISIT_DEPTH = 7;
    /** 日期类型 7:最近7天 30:最近30天 0:自定义 */
    private Integer type = 7;
    private String startDate;
    private String endDate;
    private Integer action = SESSION_COUNT;
    private Integer grading = 1;
}
