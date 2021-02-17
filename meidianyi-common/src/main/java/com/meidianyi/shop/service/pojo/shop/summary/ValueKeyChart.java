package com.meidianyi.shop.service.pojo.shop.summary;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 图表数据出参（x轴为 value，y轴为 key）
 *
 * @author 郑保乐
 */
@Getter
@Setter
@NoArgsConstructor
public class ValueKeyChart extends AbstractChart {

    private List<Integer> xAxis;
    private List<String> yAxis;

    @Override
    public void setKeys(List<String> keys) {
        yAxis = keys;
    }

    @Override
    public void setValues(List<Integer> values) {
        super.setValues(values);
        xAxis = values;
    }
}
