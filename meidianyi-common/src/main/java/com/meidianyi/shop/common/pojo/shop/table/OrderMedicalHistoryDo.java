package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/7/28 14:07
 */
@Data
public class OrderMedicalHistoryDo {
    private Integer   id;
    private Integer   orderId;
    private Integer   patientId;
    private String    patientName;
    private Byte      sex;
    private Integer   age;
    private String    identityCode;
    private Byte      identityType;
    private String    patientTreatmentCode;
    private String    patientComplain;
    private String    diseaseHistory;
    private String    allergyHistory;
    private String    familyDiseaseHistory;
    private String    describe;
    private Byte      gestationType;
    private Byte      kidneyFunctionOk;
    private Byte      liverFunctionOk;
    private String    imagesList;
    private Byte      isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;

}
