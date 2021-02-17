package com.meidianyi.shop.service.pojo.shop.patient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author yangpengcheng
 */
@Data
public class PatientExternalVo{
    private Integer   id;
    private String    name;
    @JsonProperty("phone")
    private String    mobile;
    @JsonProperty("code")
    private String    patientCode;
    @JsonProperty("identityNo")
    private String    identityCode;
    private Byte      identityType;
    @JsonProperty("visitNo")
    private String    treatmentCode;
    @JsonProperty("carteVitalNo")
    private String    insuranceCardCode;
    private Byte      sex;
    private Date      birthday;
    private String    remarks;
    @JsonProperty("state")
    @JsonDeserialize(using = StateDeSerialize.class)
    private Byte      isDelete;
    private Integer createTime;
    private Integer lastUpdateTime;
}
