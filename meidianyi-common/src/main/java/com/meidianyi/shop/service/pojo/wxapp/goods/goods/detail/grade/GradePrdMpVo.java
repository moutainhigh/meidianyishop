package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员等价卡-规格价
 * @author 李晓冰
 * @date 2020年01月08日
 */
@Data
public class GradePrdMpVo {
    private Integer productId;
    private BigDecimal gradePrice;
    private BigDecimal prdPrice;
}
