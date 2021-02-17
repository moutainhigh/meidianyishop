package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2020/7/29 13:56
 */
@Data
public class OrderGoodsSimpleAuditVo {
    /**
     * 订单商品id
     */
    private Integer recId;
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 商品规格id
     */
    private Integer productId;
    /**
     * 商品数量
     */
    private Integer goodsNumber;
    /**
     * 关联处方号
     */
    private String prescriptionOldCode;
    /**
     * 处方号
     */
    private String prescriptionCode;
    /**
     * 处方审核类型
     */
    private Byte medicalAuditType;
    /**
     * 处方审核状态
     */
    private Byte medicalAuditStatus;
    /**
     * 商品图片
     */
    private String goodsImg;
    /**
     * 商品价格
     */
    private BigDecimal shopPrice;
}
