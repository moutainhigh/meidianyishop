package com.meidianyi.shop.service.pojo.shop.prescription;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/7/6 9:36
 */
@Data
public class PrescriptionPatientListParam  extends BasePageParam {

    /**
     * 患者id
     */
    @NotNull
    private Integer patientId;
    /**
     * 医师名称
     */
    private String doctorName;
    /**
     * 医师id
     */
    private String doctorCode;
    /**
     * 患者id
     */
    private String patientName;

    /**
     * 审核类型 0不审核,1审核,2开方,3根据处方下单
     */
    private Byte auditType;

    /**
     * 就诊时间-开始
     */
    private Timestamp diagnoseStartTime;
    /**
     * 就诊时间-结束
     */
    private Timestamp diagnoseEndTime;


    private Integer userId;

    private UserPatientParam userPatientParam;

    private List<String> prescriptionNos;
}
