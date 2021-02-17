package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-10 10:24
 **/
@Data
public class PatientPrescriptionVo {

    private Integer    id;
    private String     prescriptionCode;
    private String     doctorCode;
    private String     doctorName;
    private Byte       auditType;
    private String     departmentName;
    private BigDecimal totalPrice;
    private Timestamp createTime;
    private String orderSnByOrderInfo;
    private List<OrderGoodsDo>  goodsList;



}
