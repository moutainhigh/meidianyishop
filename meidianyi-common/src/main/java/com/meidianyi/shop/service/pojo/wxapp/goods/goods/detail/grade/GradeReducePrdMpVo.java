package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 等级会员价和限时降价同时存在时使用的规格数据类
 * @author 李晓冰
 * @date 2020年01月08日
 */
@Data
public class GradeReducePrdMpVo {

    private Integer productId;
    /** true是会员价，false是限时降价*/
    private Boolean isGradePrice;
    /**会员或限时降价活动价*/
    private BigDecimal activityPrice;
    /**规格原价*/
    private BigDecimal prdPrice;
    /**限时降价价格*/
    private BigDecimal reducePrice;
}
