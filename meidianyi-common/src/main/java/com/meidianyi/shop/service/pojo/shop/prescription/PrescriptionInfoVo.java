package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/7/7 18:27
 */
@Data
public class PrescriptionInfoVo {

    /**
     * 处方号
     */
    private String    prescriptionCode;
    /**
     * 患者就诊号
     */
    private String    patientTreatmentCode;
    /**
     * 患者名称
     */
    private String    patientName;
    /**
     * 患者年龄
     */
    private Integer   patientAge;
    /**
     * 性别
     */
    private Byte      patientSex;
    /**
     * 科室名称
     */
    private String    departmentName;
    /**
     * 诊断名称
     */
    private String    diagnosisName;
    /**
     * 医师名称
     */
    private String    doctorName;
    /**
     * 开方时间
     */
    private Timestamp prescriptionCreateTime;
    /**
     * 是否用过
     */
    private Byte isUsed;
    /**
     * 是否有效
     */
    private Byte isValid;
    /**
     * 过期类型 0过期 1永久 2时间段有效
     */
    private Byte expireType;
    /**
     * 处方过期时间
     */
    private Timestamp prescriptionExpireTime;

    /**
     * 处方明细
     */
    private List<PrescriptionItemInfoVo> itemList;
    /**
     * 医师签名
     */
    private String doctorSignature;
    /**
     * 发药药师签名
     */
    private String pharmacistSignature;
    /**
     * 医院公章
     */
    private String cachet;
    /**
     * 医嘱
     */
    private String doctorAdvice;
    /**
     * 处方顶部显示医院名称
     */
    private String hospitalName;

}
