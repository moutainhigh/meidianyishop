package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit;


import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 医生审核过的处方
 * @author 孔德成
 * @date 2020/8/5 16:27
 */
@Data
public class DoctorAuditedPrescriptionParam extends BasePageParam {

    /**
     * 0 全部 1审核 2开方 3会话
     */
    @NotNull
    private Byte type;
    @NotNull
    private Integer doctorId;

    private String doctorCode;
}
