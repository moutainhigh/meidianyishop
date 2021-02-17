package com.meidianyi.shop.service.pojo.shop.overview.asset;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author liufei
 * @date 2019/8/6
 * @description
 */
@Data
public class RevenueScoreDate {
    public Date refDate;
    public BigDecimal incomeRealScore;
    public BigDecimal incomeTotalScore;
    public BigDecimal outgoScore;
}
