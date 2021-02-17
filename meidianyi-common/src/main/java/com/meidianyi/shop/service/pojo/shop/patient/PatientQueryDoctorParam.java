package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-09 11:28
 **/
@Data
public class PatientQueryDoctorParam {

    @NotNull(message = JsonResultMessage.PATIENT_IS_NOT_EXIST)
    private Integer patientId;

    private String doctorName;

    private String departmentName;

    /**
     * 分页查询参数
     */
    private Integer currentPage;
    private Integer pageRows;

}
