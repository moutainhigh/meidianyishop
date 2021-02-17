package com.meidianyi.shop.service.pojo.shop.member.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  用户等级会员卡商品价格
 * @author 孔德成
 * @date 2019/10/29 14:09
 */
@Data
public class UserCardGradePriceBo {

    /**
     * 会员卡名称
     */
    private String cardName;
    /**
     * 会员卡等级
     */
    private String grade;
    /**
     *  商品id
     */
    private Integer goodsId;
    /**
     * 规格ID
     */
    private Integer prdId;
    /**
     * 会员价格
     */
    private BigDecimal gradePrice;
}
