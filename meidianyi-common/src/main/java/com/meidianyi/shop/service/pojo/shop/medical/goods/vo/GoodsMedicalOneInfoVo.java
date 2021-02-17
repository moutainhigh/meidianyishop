package com.meidianyi.shop.service.pojo.shop.medical.goods.vo;

import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsMedicalInfoEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/7/24
 **/
@Data
public class GoodsMedicalOneInfoVo extends GoodsMedicalInfoEntity {
    private BigDecimal shopPrice;
    private String goodsImg;
    private Integer goodsNumber;
    private Integer prdId;
}
