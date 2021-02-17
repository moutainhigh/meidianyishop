package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-21 17:39
 **/
@Data
public class DoctorQueryInquiryParam {

    private String patientName;
    @NotNull(message = "医师编码不能为空")
    private Integer doctorId;
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
    private Integer currentPage;
    private Integer pageRows = 5;

}
