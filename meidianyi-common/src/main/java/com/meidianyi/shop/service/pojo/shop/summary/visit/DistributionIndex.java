package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 访问分布索引项
 *
 * @author 郑保乐
 */
@Data
public class DistributionIndex {
    /**
     * 访问人数来源
     */
    public static final String ACCESS_SOURCE_UV = "access_source_visit_uv";
    /**
     * 访问次数来源
     */
    public static final String ACCESS_SOURCE_PV = "access_source_session_cnt";

    /**
     * 停留时间
     */
    public static final String VISIT_DURATION = "access_staytime_info";

    /**
     * 平均访问深度
     */
    public static final String VISIT_DEPTH = "access_depth_info";

    /**
     * 统计数据名称
     */
    private String index;

    /**
     * 统计数据项
     * 数据库字段存储的是下划线格式，如果用驼峰会导致反序列化失败
     */
    @JsonProperty("item_list")
    private List<DistributionIndexItem> indexItems;
}
