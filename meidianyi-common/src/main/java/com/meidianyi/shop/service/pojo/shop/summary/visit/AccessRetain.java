package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 留存统计
 *
 * @author 郑保乐
 */
@Data
public class AccessRetain {

    private String refDate;
    private Integer sum;
    private Map<Integer,Integer> data;
}
