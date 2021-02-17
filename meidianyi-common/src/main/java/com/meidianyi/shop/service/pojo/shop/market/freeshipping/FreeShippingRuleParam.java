package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2019/7/29 18:41
 */
@Data
public class FreeShippingRuleParam {

    private Integer id;
    private Integer shippingId;
    /**
     * 包邮条件 0满金额 1满件数 2满金额或满件数
     */
    @NotNull
    private Integer conType;
    @DecimalMin("0")
    private BigDecimal money;
    @Min(0)
    private Integer num;
    @NotBlank
    private String  area;

}
