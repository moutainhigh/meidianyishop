package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

/**
 * @author yangpengcheng
 */
@Data
public class PatientExternalRequestParam {
    @NotNull
    private String name;
    @NotNull
    private String mobile;
    private String identityCode;
}
