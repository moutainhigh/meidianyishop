package com.meidianyi.shop.service.pojo.shop.order.goods;

import com.meidianyi.shop.common.pojo.shop.table.OrderMedicalHistoryDo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsMedicalOneInfoVo;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/7/24
 **/
@Data
public class OrderGoodsMedicalVo {
    private Integer orderId;
    private String orderSn;
    private Timestamp createTime;
    private OrderMedicalHistoryDo medicalHistory;
    private List<GoodsMedicalOneInfoVo> goodsMedicalOneInfoVoList;
}
