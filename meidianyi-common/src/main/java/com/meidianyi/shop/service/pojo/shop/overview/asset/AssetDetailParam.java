package com.meidianyi.shop.service.pojo.shop.overview.asset;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/5
 * @description
 */
@Data
public class AssetDetailParam {
    /** 起始时间 */
    private Timestamp startTime;
    /** 结束时间 */
    private Timestamp endTime;
    /** 交易金额下限 */
    private BigDecimal lowerLimit;
    /** 交易金额上限 */
    private BigDecimal upperLimit;
    /** 交易单号 */
    private String tradeSn;
    /** 交易类型 */
    private Byte tradeType;
    /** 资金流向 0：收入，1：支出，2：待确认收入*/
    private Byte tradeFlow = -1;
    /** 交易内容：0：现金，1：积分 */
    @NotNull
    private Byte tradeContent;
    private Integer currentPage;
    private Integer pageRows;

    private String username;
    private String mobile;
    private String realName;

    private Integer exportRowStart;
    private Integer exportRowEnd;
}
