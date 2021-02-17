package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.pojo.shop.table.UserPatientCoupleDo;
import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年07月31日
 */
@Data
public class UserPatientDetailVo extends UserPatientCoupleDo {
    private List<PatientMoreInfoParam> diseaseHistoryList;
    private List<PatientMoreInfoParam> familyDiseaseHistoryList;
    private String diseaseHistoryStr;
    private String familyDiseaseHistoryStr;
    private Integer age;
    private String    name;
    private String    mobile;
    private String    identityCode;
}
