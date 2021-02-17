package com.meidianyi.shop.service.pojo.shop.overview.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author:liufei
 * @Date:2019/7/29
 * @Description:
 */
@Data
public class LineChartVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    /** 付款金额 */
    private double totalPaidMoney;
    /** 访问付款转化率 */
    private double uv2Paid;
    /** 客单价 */
    private double pct;
    /** 付款订单数 */
    private int payOrderNum;
    /** 付款用户数 */
    private int payUserNum;
    /** 访客量 */
    private int uv;
    /** 浏览量 */
    private int pv;
}
