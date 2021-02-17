package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/7/24 10:19
 */
@Data
public class PatientSmsCheckParam {

    /**
     * 电话
     */
    @NotNull
    private String mobile;


    /**
     * 用户id
     */
    private Integer userId;
}
