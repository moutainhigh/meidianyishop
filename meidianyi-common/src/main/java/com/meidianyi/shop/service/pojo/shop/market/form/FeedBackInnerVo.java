package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author liufei
 * @date 2019/8/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackInnerVo {

    private String moduleName;
    private String moduleType;
    private String moduleValue;
    private Integer votes;
    private BigDecimal percentage;

}
