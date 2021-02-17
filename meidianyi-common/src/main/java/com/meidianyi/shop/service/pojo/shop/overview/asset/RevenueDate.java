package com.meidianyi.shop.service.pojo.shop.overview.asset;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author liufei
 * @date 2019/8/5
 * @description
 */
@Data
public class RevenueDate{
    public Date refDate;
    public BigDecimal incomeRealMoney;
    public BigDecimal incomeTotalMoney;
    public BigDecimal outgoMoney;
}
