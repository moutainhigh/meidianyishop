package com.meidianyi.shop.service.pojo.shop.overview.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/2
 * @description
 */
@Data
public class RevenueProfileVo {
    @JsonIgnore
    public Date refDate;
    /** 现金净收入 */
    private BigDecimal incomeRealMoney;
    /** 现金总收入 */
    private BigDecimal incomeTotalMoney;
    /** 现金总支出 */
    private BigDecimal outgoMoney;

    /** 较上一周期增长率 */
    private BigDecimal incomeRealMoneyPer;
    private BigDecimal incomeTotalMoneyPer;
    private BigDecimal outgoMoneyPer;

    /** 折线图数据 */
    private List<RevenueDate> revenueDates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endTime;
}
