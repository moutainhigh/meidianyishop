package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-09 09:47
 **/

@Data
public class PatientMedicineParam {
    /**
     * 患者id
     */
    @NotNull(message = JsonResultMessage.PATIENT_IS_NOT_EXIST)
    private Integer patientId;
    /**
     * 药品名
     */
    private String goodsCommonName;
    /**
     * 药品批准文号
     */
    private String goodsApprovalNumber;
    /**
     * 生产厂家
     */
    private String goodsProductionEnterprise;
    /**
     * 购药时间上界
     */
    private Timestamp startTime;
    /**
     * 购药时间下界
     */
    private Timestamp endTime;
    /**
     * 分页查询参数
     */
    private Integer currentPage;
    private Integer pageRows;
}
