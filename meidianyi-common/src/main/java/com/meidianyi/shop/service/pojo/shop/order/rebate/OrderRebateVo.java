package com.meidianyi.shop.service.pojo.shop.order.rebate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 订单返利
 * @author 王帅
 */
@Data
public class OrderRebateVo {
    @JsonIgnore
    private Integer rebateId;
    @JsonIgnore
    private String orderSn;
    @JsonIgnore
    private Integer goodsId;
    @JsonIgnore
    private Integer productId;
    @JsonIgnore
    private Integer recId;
    /**0-2三种返利关系*/
    private Byte rebateLevel;
    /**分销员用户id*/
    private Integer rebateUserId;
    /**返利比例*/
    private BigDecimal rebatePercent;
    /**单品返利金额*/
    private BigDecimal rebateMoney;
    /**成本价*/
    private BigDecimal costPrice;
    /**总返利金额*/
    @JsonIgnore
    private BigDecimal totalRebateMoney;
    /**单品可计算返利金额*/
    @JsonIgnore
    private BigDecimal canCalculateMoney;
    /**可返利商品总金额*/
    private BigDecimal canRebateTotalMoney;
    /**商品可返利金额*/
    private BigDecimal canRebateMoney;
    /**返利日期*/
    private Timestamp updateTime;
    /**实际返利金额*/
    private BigDecimal realRebateMoney;
    /**分销员微信昵称*/
    private String username;
    private String goodsName;
    @JsonIgnore
    private Integer goodsNumber;
    @JsonIgnore
    private Integer returnNumber;
    @JsonIgnore
    private String fanliStrategy;
    private String mobile;
    private String realName;
    /**商品金额*/
    private BigDecimal goodsPrice;
    /**返利商品总金额*/
    private BigDecimal rebateTotalMoney;
}
