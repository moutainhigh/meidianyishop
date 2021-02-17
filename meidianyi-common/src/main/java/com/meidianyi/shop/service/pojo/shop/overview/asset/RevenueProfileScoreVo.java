package com.meidianyi.shop.service.pojo.shop.overview.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/6
 * @description
 */
@Data
public class RevenueProfileScoreVo {
    @JsonIgnore
    public Date refDate;
    /** 积分净收入 */
    private BigDecimal incomeRealScore;
    /** 积分总收入 */
    private BigDecimal incomeTotalScore;
    /** 积分总支出 */
    private BigDecimal outgoScore;

    /** 较上一周期增长率 */
    private BigDecimal incomeRealScorePer;
    private BigDecimal incomeTotalScorePer;
    private BigDecimal outgoScorePer;

    /** 折线图数据 */
    private List<RevenueScoreDate> revenueDates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endTime;
}
