package com.meidianyi.shop.service.pojo.shop.summary;

import lombok.Data;

import java.util.List;

/**
 * 抽象图表
 *
 * @author 郑保乐
 */
@Data
public abstract class AbstractChart implements ChartData {

    protected Integer min;
    protected Integer max;

    @Override
    public void setValues(List<Integer> values) {
        min = values.stream().min(Integer::compareTo).orElse(0);
        max = values.stream().max(Integer::compareTo).orElse(0);
    }
}
