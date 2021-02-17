package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.integral;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 积分兑换商品规格信息
 * @author 李晓冰
 * @date 2020年03月03日
 */
@Data
public class IntegralMallPrdMpVo {
    private Integer productId;
    private Integer score;
    private Integer stock;
    private BigDecimal money;
}
