package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription;

import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockField;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionDrugVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单开方
 * @author yangpengcheng
 * @date 2020/7/24
 **/
@Data
public class PrescriptionMakeParam extends AbstractOrderOperateQueryParam {
    @RedisLockField
    private Integer orderId;
    private Integer patientId;
    private Integer doctorId;
    private String departmentCode;
    private String departmentName;
    private String diagnosisName;
    private String doctorAdvice;
    /**
     * 审核状态 2失败 1成功
     */
    private Byte auditStatus;
    /**
     * 驳回类型
     */
    private Byte reasonType;
    /**
     * 驳回原因
     */
    private String reasonDesc;
    private List<PrescriptionDrugVo> goodsList=new ArrayList<>();
}
