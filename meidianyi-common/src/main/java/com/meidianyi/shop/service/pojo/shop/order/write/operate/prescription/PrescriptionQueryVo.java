package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription;

import lombok.Data;

import java.util.List;

/**
 * 订单审核查询返回参数
 * @author 孔德成
 * @date 2020/7/9 14:00
 */
@Data
public class PrescriptionQueryVo {

    /**
     * 处方列表
     */
    List<PrescriptionOrderGoodsVo> auditedList;
    List<PrescriptionOrderGoodsVo> unauditedList;
}
