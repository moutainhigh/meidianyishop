package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-21 18:03
 **/
@Data
public class DoctorQueryPrescriptionParam {
    /**
     * 医院拉取处方
     */
    public static final Byte PRESCRIPTION_TYPE_IS_HIS_FETCH = 4;

    public static final Byte PRESCRIPTION_SOURCE_IS_HIS = 1;

    @NotNull(message = "医师编码不能为空")
    private String doctorCode;

    private String patientName;

    private String departmentName;
    /**
     * 就诊类型 0、不审核；1、审核；2、开方；3、根据处方下单；4、his拉取
     */
    private Byte auditType;
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
    private Integer pageRows;
}
