package com.meidianyi.shop.service.pojo.shop.goods.es;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * es 聚合查询通用类(可扩展)
 * @author 卢光耀
 * @date 2019/10/28 4:12 下午
 *
*/
@Data
@Builder
public class Fact {

    /**
     * 聚合名称
     */
    private String name;
    /**
     * 聚合的字段
     */
    private String fieldName;

    /**
     * 聚合值限制(聚合条件)
     */
    private List<String> values;
}
