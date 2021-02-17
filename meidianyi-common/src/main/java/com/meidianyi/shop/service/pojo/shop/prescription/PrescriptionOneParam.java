package com.meidianyi.shop.service.pojo.shop.prescription;

import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockField;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangpengcheng
 */
@Data
public class PrescriptionOneParam {
    private Integer orderId;
    private String orderSn;
    private Integer patientId;
    @RedisLockField
    private Integer doctorId;
    private Integer userId;
    private String departmentCode;
    private String departmentName;
    private String diagnosisName;
    private Timestamp diagnoseTime;
    private String doctorAdvice;
    private Byte isUsed;
    private Byte auditType;
    private List<PrescriptionDrugVo> goodsList=new ArrayList<>();
}
