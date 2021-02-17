package com.meidianyi.shop.service.pojo.shop.prescription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 处方全字段
 * @author 孔德成
 * @date 2020/7/2 18:09
 */
@Data
public class PrescriptionVo extends PrescriptionDo {

    List<PrescriptionItemDo> list =new ArrayList<>();
    /**
     * 处方明细号
     */
    private String prescriptionDetailOldCode;
    /**
     * 仅用于下单匹配订单商品
     */
    @JsonIgnore
    private List<OrderBeforeParam.Goods>  orderGoodsList;
    @JsonIgnore
    private List<Integer> orderProductIdList;

    @Override
    public String toString() {
        return "PrescriptionVo{" +
                "prescriptionDetailOldCode='" + prescriptionDetailOldCode + '\'' +
                '}';
    }
}
