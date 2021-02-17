package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 赵晓东
 * @description 带患者信息，用于患者拉取个人信息
 * @create 2020-08-18 11:23
 **/
@Data
public class PatientSmsCheckNumParam extends PatientSmsCheckParam{
    /**
     * 患者身份证
     */
    @NotNull
    private String identityCode;
}
