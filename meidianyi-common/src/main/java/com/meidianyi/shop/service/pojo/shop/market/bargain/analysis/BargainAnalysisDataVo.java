package com.meidianyi.shop.service.pojo.shop.market.bargain.analysis;

import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 砍价活动数据图表分析的格式化输出数据
 * @author: 王兵兵
 * @create: 2019-07-30 18:09
 **/
@Data
public class BargainAnalysisDataVo {
    private List<Integer> recordNumber = new ArrayList<>();
    private List<Integer> userNumber = new ArrayList<>();
    private List<Integer> orderNumber = new ArrayList<>();
    private List<Integer> sourceNumber = new ArrayList<>();
    private List<Date> dateList = new ArrayList<>();

    private BargainAnalysisTotalVo total;
}
