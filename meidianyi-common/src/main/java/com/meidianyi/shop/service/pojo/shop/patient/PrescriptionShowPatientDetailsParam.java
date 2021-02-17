package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-26 09:17
 **/
@Data
public class PrescriptionShowPatientDetailsParam {
    /**
     * 疾病史
     **/
    private List<PatientMoreInfoParam> diseaseHistoryList;
    /**
     * 家族病史
     **/
    private List<PatientMoreInfoParam> familyDiseaseHistoryList;
    /**
     * 年龄
     **/
    private Integer age;
    /**
     * 姓名
     **/
    private String name;
    /**
     * 性别
     **/
    private Byte sex;
    /**
     * 过敏史
     **/
    private String allergyHistory;
    /**
     * 妊娠状态
     **/
    private Byte gestationType;
    /**
     * 备注
     **/
    private String remarks;


}
