package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit;

import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionSimpleVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 审核订单商品
 *
 * @author 孔德成
 * @date 2020/7/27 17:53
 */
@Data
public class AuditOrderGoodsVo {

    private List<OrderGoodsDo> goodsList;

    private PrescriptionSimpleVo prescription;

    private Timestamp time;
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单号
     */
    private String orderSn;
}
