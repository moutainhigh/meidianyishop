package com.meidianyi.shop.service.pojo.shop.market.presale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 活动商品规格入参
 *
 * @author 郑保乐
 */
@Data
public class ProductParam {

    /** 预售id **/
    @JsonIgnore
    private Integer presaleId;

    /**  主键 */
    private Integer id;
    /** 商品id **/
    @NotNull
    private Integer goodsId;
    /** 产品id **/
    @NotNull
    private Integer productId;
    /** 活动价格 **/
    @NotNull
    private BigDecimal presalePrice;
    /** 活动库存 **/
    @NotNull
    private Integer presaleNumber;
    /** 定金 **/
    private BigDecimal presaleMoney;
    /** 1段定金可抵扣金额 **/
    private BigDecimal preDiscountMoney1;
    /** 2段定金可抵扣金额 **/
    private BigDecimal preDiscountMoney2;
}
