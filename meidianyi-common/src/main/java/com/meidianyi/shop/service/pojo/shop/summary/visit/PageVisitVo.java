package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

import java.util.List;

/**
 * 访问页面统计出参单项
 *
 * @author 郑保乐
 */
@Data
public class PageVisitVo {
    private String startDate;
    private String endDate;
    private List<PageVisitVoItem> list;
    private String sum;

    public void setList(List<PageVisitVoItem> list) {
        this.list = list;
        double sum = list.stream().mapToDouble(r -> Double.parseDouble(r.getPageVisitPv())).sum();
        setSum(Double.toString(sum));
    }
}
