package com.meidianyi.shop.service.pojo.shop.overview.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * @author liufei
 * @date 2019/7/31
 * @description
 */
@Data
public class GeographicalParam {
    /** 筛选时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private Date screeningTime;
    /** 排序字段 */
    private String orderByField;
    /** 排序方式 */
    private String orderByType;
    /** 页面限制数量 */
    private Integer limitNum;
}
