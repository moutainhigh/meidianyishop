package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-10 10:20
 **/
@Data
public class PatientPrescriptionParam {

    @NotNull(message = JsonResultMessage.PATIENT_IS_NOT_EXIST)
    private Integer patientId;

    private String doctorName;

    private String departmentName;

    private Byte prescriptionType;
    /**
     * 时间上界
     */
    private Timestamp startTime;
    /**
     * 时间下届
     */
    private Timestamp endTime;
    /**
     * 分页查询参数
     */
    private Integer currentPage;
    private Integer pageRows;

}
