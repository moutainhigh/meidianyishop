package com.meidianyi.shop.service.pojo.shop.medicalhistory;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-09 18:49
 */
@Data
public class MedicalHistoryPageInfoVo {

    /**
     * 病历ID、诊断、就诊科室、诊疗处理意见、医师、就诊时间
     */

    private Integer id;
    private String diagnosisContent;
    private String departmentName;
    private String diagnosisSuggestion;
    private String doctorName;
    private Timestamp visitTime;

}
