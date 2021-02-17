package com.meidianyi.shop.service.pojo.shop.overview.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/1
 */
@Data
public class LabelAnalysisParam {
    /** 筛选时间 */
    private Byte screeningTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /** 排序字段 */
    private String orderByField;
    /** 排序方式 */
    private String orderByType;
    private Integer currentPage;
    private Integer pageRows;
}
