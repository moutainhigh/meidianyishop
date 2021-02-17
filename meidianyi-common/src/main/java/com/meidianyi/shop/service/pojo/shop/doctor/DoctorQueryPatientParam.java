package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description 医师查询关联患者列表入参
 * @create 2020-09-21 15:31
 **/

@Data
public class DoctorQueryPatientParam {

    @NotNull(message = "医师id不能为空")
    private Integer doctorId;
    @NotNull(message = "医师编码不能为空")
    private String doctorCode;

    private String patientName;
    /**
     * 时间上界
     */
    private Timestamp startTime;
    /**
     * 时间下界
     */
    private Timestamp endTime;

    /**
     * 分页查询参数
     */
    private Integer currentPage = 1;
    private Integer pageRows = 5;

}
