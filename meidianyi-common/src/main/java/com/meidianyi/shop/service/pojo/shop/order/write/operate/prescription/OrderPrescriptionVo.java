package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription;

import lombok.Data;

import java.util.List;

/**
 * @author 孔德成
 * @date 2020/7/9 14:03
 */
@Data
public class OrderPrescriptionVo {
    /**
     * 处方号
     */
    private String prescriptionOldCode;
    /**
     * 订单id
     */
    private Integer orderId;

}
