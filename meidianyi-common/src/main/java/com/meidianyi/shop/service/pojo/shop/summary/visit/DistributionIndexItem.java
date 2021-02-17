package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

/**
 * 访问分布 json 中的 KV
 *
 * @author 郑保乐
 */
@Data
public class DistributionIndexItem {

    /**
     * 序号
     */
    private Integer key;

    /**
     * 统计数值
     */
    private Integer value;
}
