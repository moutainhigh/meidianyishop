package com.meidianyi.shop.service.pojo.shop.medicalhistory;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-09 09:28
 */
@Data
public class MedicalHistoryListVo{

    private Integer   id;
    private String    posCode;
    private Integer   patientId;
    private String    patientName;
    private Byte      sex;
    private Integer   age;
    private Integer   departmentId;
    private String    departmentName;
    private String    outPatientCode;
    private String    allergyHistory;
    private String    patientComplain;
    private String    diseaseHistory;
    private String    physicalExamination;
    private String    auxiliaryPhysicalExamination;
    private String    diagnosisContent;
    private String    diagnosisSuggestion;
    private String    doctorCode;
    private String    doctorName;
    private Timestamp visitTime;
    private Byte      isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;
}
